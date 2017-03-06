package com.ub.smssender.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 * On 5/03/17.
 */

public class SmsDeliverReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode())
        {
            case Activity.RESULT_OK:
                System.out.println("Delivered RESULT_OK");
                break;
            case Activity.RESULT_CANCELED:
                System.out.println("Delivered RESULT_CANCELED");
                break;
        }
    }
}
