package com.ub.smssender.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 16/05/17.
 */

public class SmsDeliveredReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String smsId = "";
        String imeiOutput = "";

        //System.out.println("accion de SmsSendDelivered: " + intent.getAction());
        if (intent.getAction().equals("services.SMS_RECEIVED")) {
            smsId = intent.getExtras().getString("smsId");
            imeiOutput = intent.getExtras().getString("imei");
        }

        if (!smsId.isEmpty()) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    System.out.println("SMS_RECEIVED: RESULT_OK");
                    break;
                case Activity.RESULT_CANCELED:
                    System.out.println("SMS_RECEIVED: RESULT_CANCELED");
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    System.out.println("SMS_RECEIVED: RESULT_ERROR_GENERIC_FAILURE");
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    System.out.println("SMS_RECEIVED: RESULT_ERROR_NO_SERVICE");
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    System.out.println("SMS_RECEIVED: RESULT_ERROR_NULL_PDU");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    System.out.println("SMS_RECEIVED: RESULT_ERROR_RADIO_OFF");
                    break;
            }
        }
    }

}
