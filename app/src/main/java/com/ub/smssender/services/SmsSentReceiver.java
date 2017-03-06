package com.ub.smssender.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.ub.smssender.models.ModelEnviado;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 5/03/17.
 */

public class SmsSentReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String smsId = "";
        if(intent.getAction().equals("services.SMS_SENT")) {
            smsId = intent.getExtras().getString("smsId");
        }

        if (!smsId.isEmpty()){
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    this.capturarEnviado(smsId, 1, "RESULT_OK");
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    this.capturarEnviado(smsId, 2, "RESULT_ERROR_GENERIC_FAILURE");
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    this.capturarEnviado(smsId, 2, "RESULT_ERROR_NO_SERVICE");
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    this.capturarEnviado(smsId, 2, "RESULT_ERROR_NULL_PDU");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    this.capturarEnviado(smsId, 2, "RESULT_ERROR_RADIO_OFF");
                    break;
            }
            //se envió este mensaje, continuar con los demas
        }
    }

    private void capturarEnviado(final String smsId, int estado, String error){
         final Call<BodyResponse> call = WSUtils.webServices().enviado(new ModelEnviado(smsId, estado, error));
            call.enqueue(new Callback<BodyResponse>() {
                @Override
                public void onResponse(Call<BodyResponse> call, Response<BodyResponse> response) {
                    if (response.isSuccessful()){
                        //TODO aqui se podria hacer algo  despues de capturar como enviado
                        System.out.println("enviado: " + smsId);
                        WSUtils.removeMensaje(smsId);
                    }
                }

                @Override
                public void onFailure(Call<BodyResponse> call, Throwable t) {
                    System.out.println("no se logro capturar como enviado...");
                    t.printStackTrace();
                }
            });
    }

}
