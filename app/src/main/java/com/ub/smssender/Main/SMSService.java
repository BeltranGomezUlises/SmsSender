package com.ub.smssender.Main;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ub.smssender.models.ModelMensaje;
import com.ub.smssender.services.BodyResponse;
import com.ub.smssender.services.SmsSentReceiver;
import com.ub.smssender.services.WSUtils;
import com.ub.smssender.utils.TelephonyInfo;
import com.ub.smssender.utils.UtilPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.telephony.SmsManager.getSmsManagerForSubscriptionId;
import static com.ub.smssender.services.WSUtils.webServices;

/**
 * Created by ulises on 26/02/17.
 */

public class SMSService extends IntentService {

    String SENT = "SMS_SENT";

    private static Timer timer;

    private boolean enviandoSMS = false;

    public SMSService() {
        super("SMS Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        registerReceiver(new SmsSentReceiver(), new IntentFilter(SENT));
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getMensajes();
            }
        }, 0, 20000);
    }

    public static void stopTimer(){
        if (timer != null){
            timer.cancel();
        }
    }

    private void getMensajes() {
        //esperar un tiempo
        if (!enviandoSMS){
            solicitarPaqueteMensajes2();
        }else{
            System.out.println("entoy trabajando aun");
        }
    }

    private void solicitarPaqueteMensajes2(){
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        List<String> imeiList = telephonyInfo.getImeiList();
        //for (final String imei : imeiList) {
        for(int j = 0;j < imeiList.size();j++){
            //final Call<BodyResponse> request = webServices().mensajes(UtilPreferences.loadToken(this), UtilPreferences.loadLogedUserId(SMSService.this), imei);
            final Call<BodyResponse> request = webServices().mensajes(UtilPreferences.loadToken(this), UtilPreferences.loadLogedUserId(SMSService.this), imeiList.get(j));
            try {
                Response<BodyResponse> response = request.execute();
                if (response.body().isExito()){
                    System.out.println(response.body().toString());
                    try {
                        List<ModelMensaje> modelMensajes = WSUtils.readValue(response.body().getDatos(), new TypeReference<List<ModelMensaje>>(){});
                        if (modelMensajes.size() == 0){
                            //System.out.println("No hay ningun mensaje para mi, usuario: " + UtilPreferences.loadLogedUserId(SMSService.this) +  " imei: " + imei);
                            System.out.println("No hay ningun mensaje para mi, usuario: " + UtilPreferences.loadLogedUserId(SMSService.this) +  " imei: " + imeiList.get(j));
                        }
                        for (final ModelMensaje modelMensaje : modelMensajes) {
                            System.out.println("enviando mensaje: " + modelMensaje.get_id() + " para: " + modelMensaje.getDestino());

                            Intent in = new Intent("services.SMS_SENT");
                            in.putExtra("smsId", modelMensaje.get_id());
                            //in.putExtra("IMEIoutput",imei);
                            in.putExtra("IMEIoutput",imeiList.get(j));
                            sendBroadcast(in);

                            if (modelMensaje.getMensaje().length() > 160){
                                ArrayList<String> messageList = SmsManager.getDefault().divideMessage(modelMensaje.getMensaje());
                                ArrayList<PendingIntent> pendingIntents = new ArrayList<>();

                                for (int i = 0; i < messageList.size(); i++) {
                                    pendingIntents.add(PendingIntent.getBroadcast(SMSService.this, 0, new Intent(SENT),PendingIntent.FLAG_ONE_SHOT));
                                }

                                //SmsManager.getDefault().sendMultipartTextMessage(modelMensaje.getDestino(), null, messageList, pendingIntents, null);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                    getSmsManagerForSubscriptionId(j+1).sendMultipartTextMessage(modelMensaje.getDestino(), null, messageList, pendingIntents, null);
                                }else{
                                    SmsManager.getDefault().sendMultipartTextMessage(modelMensaje.getDestino(), null, messageList, pendingIntents, null);
                                }
                            }else {
                                PendingIntent sentPI = PendingIntent.getBroadcast(SMSService.this, 0, new Intent(SENT), PendingIntent.FLAG_ONE_SHOT);
                                SmsManager.getDefault().sendTextMessage(modelMensaje.getDestino(), null, modelMensaje.getMensaje(), sentPI, null);
                            }
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    //pedir nuevo token
                    Log.w("Service: ", response.body().getMensaje());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
