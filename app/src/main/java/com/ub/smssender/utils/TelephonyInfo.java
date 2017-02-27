package com.ub.smssender.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ulises on 25/02/17.
 */

public final class TelephonyInfo {

    private static TelephonyInfo telephonyInfo;
    private static final String[] METHODS_NAMES = {"getDeviceIdGemini", "getDeviceIdDs", "getSimSerialNumberGemini", "getImei", "getDevideId"};

    private ArrayList<String> imeiList = new ArrayList<>();

    public ArrayList<String> getImeiList() {
        return imeiList;
    }

    private TelephonyInfo() {
    }

    public void printInfo(){
        for (int i = 0; i < imeiList.size(); i++) {
            System.out.println("IMEI " + (i + 1) +": " + imeiList.get(i));
        }
    }

    public static TelephonyInfo getInstance(Context context){
        if(telephonyInfo == null) {
            telephonyInfo = new TelephonyInfo();
            try {
                for (int i = 0; i < 4; i++) {
                    String simx = getDeviceIdBySlot(context, i) ;
                    if (simx != null){
                        telephonyInfo.imeiList.add(simx);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return telephonyInfo;
    }

    private static String getDeviceIdBySlot(Context context, int slotID){
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = getDeviceIdBySlot(telephony, 0, slotID);
        return  imsi;
    }

    private static String getDeviceIdBySlot(TelephonyManager telephony, int index, int slotID){
        //System.out.println("buscando en: " + METHODS_NAMES[index] + " para slot: " + slotID);
        try{
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;

            Method getSimID = telephonyClass.getMethod(METHODS_NAMES[index], parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;

            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if(ob_phone != null){
                return ob_phone.toString();
            }else{
                return null;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            if (slotID < METHODS_NAMES.length){
                return getDeviceIdBySlot(telephony, index + 1, slotID );
            }else{
                return null;
            }
        }
    }

    //CON ESTE METODO PODEMOS BUSCAR EL  NOMBRE EL METODO DE TELEPHONY MANAGER QUE CADA FABRICANTE TENGA PARA SU DISPOSITIVO PARA BUSCAR EL IMEI DE LOS SIM
    public static void printTelephonyManagerMethodNamesForThisDevice(Context context) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            Method[] methods = telephonyClass.getMethods();
            for (int idx = 0; idx < methods.length; idx++) {
                //System.out.println("\n" + methods[idx] + " declared by " + methods[idx].getDeclaringClass());
                System.out.println(methods[idx]);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
