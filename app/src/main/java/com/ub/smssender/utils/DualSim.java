package com.ub.smssender.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kirianov.multisim.MultiSimTelephonyManager;

/**
 * Created by ulises on 25/02/17.
 */

public class DualSim {

    private static MultiSimTelephonyManager multiSimTelephonyManager;

    public static void printInfo(Context context) {
        multiSimTelephonyManager = new MultiSimTelephonyManager(context);

        // get number of slots:
        System.out.println("size slots: " + multiSimTelephonyManager.sizeSlots());

        // get info from each slot:
        for(int i = 0; i < multiSimTelephonyManager.sizeSlots(); i++) {
            multiSimTelephonyManager.getSlot(i).getImei();
            multiSimTelephonyManager.getSlot(i).getImsi();
            multiSimTelephonyManager.getSlot(i).getSimSerialNumber();
            multiSimTelephonyManager.getSlot(i).getSimState();
            multiSimTelephonyManager.getSlot(i).getSimOperator();
            multiSimTelephonyManager.getSlot(i).getSimOperatorName();
            multiSimTelephonyManager.getSlot(i).getSimCountryIso();
            multiSimTelephonyManager.getSlot(i).getNetworkOperator();
            multiSimTelephonyManager.getSlot(i).getNetworkOperatorName();
            multiSimTelephonyManager.getSlot(i).getNetworkCountryIso();
            multiSimTelephonyManager.getSlot(i).getNetworkType();
            multiSimTelephonyManager.getSlot(i).isNetworkRoaming();
        }

    }

}
