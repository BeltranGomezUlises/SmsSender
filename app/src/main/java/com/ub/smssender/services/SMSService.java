package com.ub.smssender.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ub.smssender.models.ModelBodyResponse;
import com.ub.smssender.models.ModelMensaje;
import com.ub.smssender.utils.TelephonyInfo;
import com.ub.smssender.utils.UtilPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

import static android.telephony.SmsManager.getSmsManagerForSubscriptionId;
import static com.ub.smssender.services.WSUtils.webServices;

/**
 * Created by ulises on 26/02/17.
 */

public class SMSService extends IntentService {

    private static Timer timer;
    private String SENT = "SMS_SENT";
    private String DELIVERED = "SMS_RECEIVED";

    private List<String> imeiList;

    public SMSService() {
        super("SMS Service");
    }

    public static void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        imeiList = telephonyInfo.getImeiList();

        Realm.init(getApplicationContext());

        //registrar receivers
        registerReceiver(new SmsSentReceiver(), new IntentFilter(SENT));
        registerReceiver(new SmsDeliveredReceiver(), new IntentFilter(DELIVERED));


        //arancar timer
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getMensajes();
            }
        }, 0, UtilPreferences.loadIntervalTime(SMSService.this) * 1000);
    }

    private void getMensajes() {
        Realm realm = Realm.getDefaultInstance();


        RealmResults<ModelMensaje> todosMensajes = realm.where(ModelMensaje.class).findAll();
        System.out.println("todos los mensajes guardados");
        for (ModelMensaje mensaje : todosMensajes) {
            System.out.println(mensaje);
        }

        RealmResults<ModelMensaje> mensajes = realm.where(ModelMensaje.class).equalTo("estado", 0).findAll(); //mensajes por enviar (estado = 0)
        if (mensajes.size() == 0) {
            solicitarPaqueteMensajes();
        } else {
           // System.out.println("aun tengo mensajes por enviar: " + mensajes);
            enviarMensaje(mensajes);
        }
        realm.close();
    }

    private void solicitarPaqueteMensajes() {
        //System.out.println("Solicitando mensajes...");
        for (int j = 0; j < imeiList.size(); j++) {
            //System.out.println("...");
            final Call<ModelBodyResponse> request = webServices().mensajes(
                    UtilPreferences.loadToken(this),
                    UtilPreferences.loadLogedUserId(SMSService.this),
                    imeiList.get(j),
                    UtilPreferences.loadNumMensajes(SMSService.this)
            );
            //impresion de parametros de la peticion
            //            System.out.println("token: " + UtilPreferences.loadToken(this));
//            System.out.println("userID: " + UtilPreferences.loadLogedUserId(SMSService.this));
//            System.out.println("imei: " + imeiList.get(j));
//            System.out.println("numMensajes: " + UtilPreferences.loadNumMensajes(SMSService.this));

            try {
                Response<ModelBodyResponse> response = request.execute();
                if (response.body().isExito()) {
                    //System.out.println(response.body().toString());
                    try {
                        List<ModelMensaje> modelMensajes = WSUtils.readValue(response.body().getDatos(), new TypeReference<List<ModelMensaje>>() {
                        });
                        if (modelMensajes.size() == 0) {
                            //System.out.println("No hay ningun mensaje para mi, usuario: " + UtilPreferences.loadLogedUserId(SMSService.this) +  " imei: " + imei);
                            //System.out.println("No hay ningun mensaje para mi, imei: " + imeiList.get(j));
                            continue;
                        } else {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            for (ModelMensaje m : modelMensajes) {
                                if (realm.where(ModelMensaje.class).equalTo("_id", m.get_id()).findFirst() == null) { //si no existe de forma local
                                    realm.copyToRealm(m);
                                }
                            }
                            realm.commitTransaction();
                            realm.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //pedir nuevo token
                    Log.w("Service: ", response.body().getMensaje());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //System.out.println("mensajes solicitados y guardados");
    }


    private void enviarMensaje(RealmResults<ModelMensaje> mensajes) {

        Realm realm = Realm.getDefaultInstance();


        for (ModelMensaje modelMensaje : mensajes) {
            //System.out.println("enviando mensaje: " + modelMensaje.get_id() + " para: " + modelMensaje.getDestino());

            Intent intentSend = new Intent("services.SMS_SENT");
            intentSend.putExtra("smsId", modelMensaje.get_id());
            intentSend.putExtra("imei", modelMensaje.getEnvia());
            sendBroadcast(intentSend);

            Intent intendDelivered = new Intent("services.SMS_RECEIVED");
            intendDelivered.putExtra("smsId", modelMensaje.get_id());
            intendDelivered.putExtra("imei", modelMensaje.getEnvia());
            sendBroadcast(intendDelivered);

            realm.beginTransaction();
            //actualizar el estado del
            modelMensaje.setEstado(1);
            realm.copyToRealmOrUpdate(modelMensaje);

            realm.commitTransaction();
            if (modelMensaje.getMensaje().length() > 160) {
                ArrayList<String> messageList = SmsManager.getDefault().divideMessage(modelMensaje.getMensaje());
                ArrayList<PendingIntent> SendPendingIntents = new ArrayList<>();
                ArrayList<PendingIntent> DeliveredPendingIntents = new ArrayList<>();

                for (int i = 0; i < messageList.size(); i++) {
                    SendPendingIntents.add(PendingIntent.getBroadcast(SMSService.this, 0, new Intent(SENT), PendingIntent.FLAG_ONE_SHOT));
                    DeliveredPendingIntents.add(PendingIntent.getBroadcast(SMSService.this, 0, new Intent(DELIVERED), PendingIntent.FLAG_ONE_SHOT));
                }
                this.sendSMS(imeiList.indexOf(modelMensaje.getEnvia()) + 1, modelMensaje, messageList, SendPendingIntents,DeliveredPendingIntents);
            } else {
                PendingIntent sentPI = PendingIntent.getBroadcast(SMSService.this, 0, new Intent(SENT), PendingIntent.FLAG_ONE_SHOT);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(SMSService.this, 0, new Intent(DELIVERED), PendingIntent.FLAG_ONE_SHOT);

                this.sendSMS(imeiList.indexOf(modelMensaje.getEnvia()) + 1, modelMensaje, sentPI, deliveredPI);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        realm.close();
    }

    private void sendSMS(int subscriptionIndex, ModelMensaje modelMensaje, ArrayList<String> messageList, ArrayList<PendingIntent> SendPendingIntent, ArrayList<PendingIntent> DeliveredPendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            getSmsManagerForSubscriptionId(subscriptionIndex).sendMultipartTextMessage(modelMensaje.getDestino(), null, messageList, SendPendingIntent, DeliveredPendingIntent);
        } else {
            SmsManager.getDefault().sendMultipartTextMessage(modelMensaje.getDestino(), null, messageList, SendPendingIntent, DeliveredPendingIntent);
        }
    }

    private void sendSMS(int subscriptionIndex, ModelMensaje modelMensaje, PendingIntent sentPI, PendingIntent deliveredPI) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            getSmsManagerForSubscriptionId(subscriptionIndex).sendTextMessage(modelMensaje.getDestino(), null, modelMensaje.getMensaje(), sentPI, deliveredPI);
        } else {
            SmsManager.getDefault().sendTextMessage(modelMensaje.getDestino(), null, modelMensaje.getMensaje(), sentPI, deliveredPI);
        }
    }


}
