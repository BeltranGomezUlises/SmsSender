package com.ub.smssender.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ub.smssender.entities.ImeiRealm;
import com.ub.smssender.entities.MensajeRealm;
import com.ub.smssender.models.ModelBodyResponse;
import com.ub.smssender.models.ModelEnviado;
import com.ub.smssender.utils.Constantes;
import com.ub.smssender.utils.TelephonyInfo;
import com.ub.smssender.utils.UtilPreferences;
import com.ub.smssender.utils.UtilsDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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

    public static String SENT = "SMS_SENT";
    public static String DELIVERED = "SMS_RECEIVED";

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

        //arancar timer
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Realm.init(getApplicationContext());
                getMensajes();
            }
        }, 0, UtilPreferences.loadIntervalTime(SMSService.this) * 1000);
    }

    private void getMensajes() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<MensajeRealm> mensajes = realm.where(MensajeRealm.class).findAll(); //mensajes por enviar
        if (mensajes.size() == 0) {
            solicitarPaqueteMensajes();
        } else {
            enviarMensaje();
        }
        realm.close();
    }

    private void solicitarPaqueteMensajes() {
        System.out.println("Solicitando mensajes...");
        Realm realm = Realm.getDefaultInstance();
        for (int j = 0; j < imeiList.size(); j++) {
            ImeiRealm imeiRealm = realm.where(ImeiRealm.class).equalTo("imei", imeiList.get(j)).findFirst();
            System.out.println(imeiRealm);
            if (imeiRealm.isActivo()) {
                System.out.println("solicitando paquete de: " + imeiRealm.getImei());
                final Call<ModelBodyResponse> request = webServices().mensajes(
                        UtilPreferences.loadToken(this),
                        UtilPreferences.loadLogedUserId(SMSService.this),
                        imeiList.get(j),
                        UtilPreferences.loadNumMensajes(SMSService.this)
                );
                 //impresion de parametros de la peticion
//               System.out.println("token: " + UtilPreferences.loadToken(this));
//               System.out.println("userID: " + UtilPreferences.loadLogedUserId(SMSService.this));
//               System.out.println("imei: " + imeiList.get(j));
//               System.out.println("numMensajes: " + UtilPreferences.loadNumMensajes(SMSService.this));

                try {
                    Response<ModelBodyResponse> response = request.execute();
                    if (response.body().isExito()) {
                        //System.out.println(response.body().toString());
                        try {
                            List<MensajeRealm> mensajeRealms = WSUtils.readValue(response.body().getDatos(), new TypeReference<List<MensajeRealm>>() {
                            });
                            if (mensajeRealms.size() == 0) {
                                //System.out.println("No hay ningun mensaje para mi, usuario: " + UtilPreferences.loadLogedUserId(SMSService.this) +  " imei: " + imei);
                                System.out.println("No hay ningun mensaje para mi, imei: " + imeiList.get(j));
                                continue;
                            } else {
                                System.out.println("Hay mensajes para mi, imei: " +  imeiList.get(j));
                                realm.beginTransaction();
                                for (MensajeRealm m : mensajeRealms) {
                                    if (realm.where(MensajeRealm.class).equalTo("_id", m.get_id()).findFirst() == null) { //si no existe de forma local
                                        realm.copyToRealm(m);
                                        System.out.println(m);
                                    }
                                }
                                realm.commitTransaction();
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
        }
        realm.close();
        //System.out.println("mensajes solicitados y guardados");
    }

    private void enviarMensaje() {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<MensajeRealm> mensajes = realm.where(MensajeRealm.class).findAll(); //mensajes por enviar (estado = 0)

        for (MensajeRealm mensajeRealm : mensajes) {
            //System.out.println("enviando mensaje: " + mensajeRealm.get_id() + " para: " + mensajeRealm.getDestino());

            Intent intentSend = new Intent("services.SMS_SENT");
            intentSend.putExtra("smsId", mensajeRealm.get_id());
            intentSend.putExtra("imei", mensajeRealm.getEnvia());
            sendBroadcast(intentSend);

            Intent intendDelivered = new Intent("services.SMS_RECEIVED");
            intendDelivered.putExtra("smsId", mensajeRealm.get_id());
            intendDelivered.putExtra("imei", mensajeRealm.getEnvia());
            sendBroadcast(intendDelivered);

            //cuando un mensaje de la lista tiene mas de 3 intentos de
            // envio capturar como imposible de enviar

            System.out.println("mensaje a enviar: " + mensajeRealm);
            if (mensajeRealm.getEstado() >= 3) {
                //capturar mensaje como imposible de enviar despues de 3 intentos
                try {
                    ModelEnviado enviado = new ModelEnviado(mensajeRealm.get_id(), 2, "imposible enviar despues de 3 intentos");

                    final Call<ModelBodyResponse> call = webServices().enviado(enviado); //-> estado _= 2 para mandar como erroneo
                    Response<ModelBodyResponse> response = call.execute();
                    System.out.println(response.body());

                    if (response.body().isExito()) {
                        System.out.println("imposible enviar: " + mensajeRealm.get_id() + " Desde el imei: " + mensajeRealm.getEnvia());
                        realm.beginTransaction();
                        MensajeRealm mensaje = realm.where(MensajeRealm.class).equalTo("_id", mensajeRealm.get_id()).findFirst();
                        mensaje.deleteFromRealm();
                        realm.commitTransaction();
                    } else {
                        if (response.body().getMensaje().equals(Constantes.NO_EXISTE_EN_DB)) {
                            System.out.println("imposible enviar: " + mensajeRealm.get_id() + " Desde el imei: " + mensajeRealm.getEnvia());
                            realm.beginTransaction();
                            MensajeRealm mensaje = realm.where(MensajeRealm.class).equalTo("_id", mensajeRealm.get_id()).findFirst();
                            mensaje.deleteFromRealm();
                            realm.commitTransaction();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //solo si ya pasó 1 minuto, desde el ultimo intento de envio, intentar enviar de nuevo
                if (mensajeRealm.getFechaUltimoIntentoEnvio() != null){
                    if (UtilsDate.diferenciaEnMinutos(new Date(), mensajeRealm.getFechaUltimoIntentoEnvio()) >= 1 ){
                        this.procesoDeEnvio(realm, mensajeRealm);
                    }
                }else{
                    this.procesoDeEnvio(realm, mensajeRealm);
                }

            }
        }
        realm.close();
    }

    private void procesoDeEnvio(Realm realm, MensajeRealm mensajeRealm){
        //actualizar el estado del mensaje
        realm.beginTransaction();
        mensajeRealm.setEstado(mensajeRealm.getEstado() + 1);
        mensajeRealm.setFechaUltimoIntentoEnvio(new Date());
        realm.copyToRealmOrUpdate(mensajeRealm);
        realm.commitTransaction();

        ArrayList<String> messageList = SmsManager.getDefault().divideMessage(mensajeRealm.getMensaje());
        if (messageList.size() > 1) {
            ArrayList<PendingIntent> SendPendingIntents = new ArrayList<>();
            ArrayList<PendingIntent> DeliveredPendingIntents = new ArrayList<>();

            for (int i = 0; i < messageList.size(); i++) {
                SendPendingIntents.add(PendingIntent.getBroadcast(SMSService.this, 0, new Intent(SENT), PendingIntent.FLAG_ONE_SHOT));
                DeliveredPendingIntents.add(PendingIntent.getBroadcast(SMSService.this, 0, new Intent(DELIVERED), PendingIntent.FLAG_ONE_SHOT));
            }
            this.sendSMS(imeiList.indexOf(mensajeRealm.getEnvia()) + 1, mensajeRealm, messageList, SendPendingIntents, DeliveredPendingIntents);
        } else {
            PendingIntent sentPI = PendingIntent.getBroadcast(SMSService.this, 0, new Intent(SENT), PendingIntent.FLAG_ONE_SHOT);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(SMSService.this, 0, new Intent(DELIVERED), PendingIntent.FLAG_ONE_SHOT);

            this.sendSMS(imeiList.indexOf(mensajeRealm.getEnvia()) + 1, mensajeRealm, sentPI, deliveredPI);
        }
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendSMS(int subscriptionIndex, MensajeRealm mensajeRealm, ArrayList<String> messageList, ArrayList<PendingIntent> SendPendingIntent, ArrayList<PendingIntent> DeliveredPendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            getSmsManagerForSubscriptionId(subscriptionIndex).sendMultipartTextMessage(mensajeRealm.getDestino(), null, messageList, SendPendingIntent, DeliveredPendingIntent);
        } else {
            SmsManager.getDefault().sendMultipartTextMessage(mensajeRealm.getDestino(), null, messageList, SendPendingIntent, DeliveredPendingIntent);
        }
    }

    private void sendSMS(int subscriptionIndex, MensajeRealm mensajeRealm, PendingIntent sentPI, PendingIntent deliveredPI) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            getSmsManagerForSubscriptionId(subscriptionIndex).sendTextMessage(mensajeRealm.getDestino(), null, mensajeRealm.getMensaje(), sentPI, deliveredPI);
        } else {
            SmsManager.getDefault().sendTextMessage(mensajeRealm.getDestino(), null, mensajeRealm.getMensaje(), sentPI, deliveredPI);
        }
    }


}
