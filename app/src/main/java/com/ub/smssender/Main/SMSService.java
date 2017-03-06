package com.ub.smssender.Main;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ub.smssender.models.ModelMensaje;
import com.ub.smssender.services.BodyResponse;
import com.ub.smssender.services.SmsDeliverReceiver;
import com.ub.smssender.services.SmsSentReceiver;
import com.ub.smssender.services.WSUtils;
import com.ub.smssender.utils.TelephonyInfo;
import com.ub.smssender.utils.UtilPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ub.smssender.services.WSUtils.webServices;

/**
 * Created by ulises on 26/02/17.
 */

public class SMSService extends IntentService {

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    Timer timer;

    public SMSService() {
        super("SMS Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        registerReceiver(new SmsSentReceiver(), new IntentFilter(SENT));
        registerReceiver(new SmsDeliverReceiver(), new IntentFilter(DELIVERED));
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getMensajes();
            }
        }, 0, 10000);
    }


    private void getMensajes() {
        System.out.println("volví por mas mensajes");
        //esperar un tiempo
        if (WSUtils.mensajes == null){
            solicitarPaqueteMensajes();
        }else{
            if(WSUtils.mensajes.size() > 0){
                System.out.println("aun quedan " + WSUtils.mensajes.size() + " mensajes");
            }else{
                solicitarPaqueteMensajes();
            }
        }
    }

    private void solicitarPaqueteMensajes(){
        //obtener los mensajes a enviar
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        List<String> imeiList = telephonyInfo.getImeiList();
        for (String imei : imeiList) {
            final Call<BodyResponse> request = webServices().mensajes(UtilPreferences.getToken(this), "58a0e7c955ceb035c6449a82", imei);
            request.enqueue(new Callback<BodyResponse>() {
                @Override
                public void onResponse(Call<BodyResponse> call, Response<BodyResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body().isExito()){
                            try {
                                List<ModelMensaje> modelMensajes = WSUtils.readValue(response.body().getDatos(), new TypeReference<List<ModelMensaje>>(){});
                                if (modelMensajes.size() == 0){
                                    System.out.println("pero no hay ningun pinche mensaje para mi");
                                }
                                WSUtils.mensajes = new ArrayList<>(modelMensajes);

                                for (final ModelMensaje modelMensaje : modelMensajes) {
                                    System.out.println("enviando mensaje: " + modelMensaje.get_id() + " para: " + modelMensaje.getDestino());

                                    Intent in = new Intent("services.SMS_SENT");
                                    in.putExtra("smsId", modelMensaje.get_id());
                                    sendBroadcast(in);

                                    PendingIntent sentPI = PendingIntent.getBroadcast(SMSService.this, 0, new Intent(SENT), 0);
                                    PendingIntent deliveredPI = PendingIntent.getBroadcast(SMSService.this, 0, new Intent(DELIVERED), 0);

                                    SmsManager.getDefault().sendTextMessage(modelMensaje.getDestino(), null, modelMensaje.getMensaje(), sentPI, deliveredPI);
                                    try {
                                        Thread.sleep(1500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            //pedir nuevo token
                            System.out.println("pedir el nuevo token");
                        }
                    }else{
                        Log.w("Warning", "Sin exito para obtener mensajes");
                    }
                }
                @Override
                public void onFailure(Call<BodyResponse> call, Throwable t) {
                    Log.e("Service: ", t.getMessage());
                }
            });
        }
    }

}
