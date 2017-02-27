package com.ub.smssender.Main;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ub.smssender.R;
import com.ub.smssender.models.ModelMensajes;
import com.ub.smssender.utils.DualSim;
import com.ub.smssender.utils.TelephonyInfo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.*;
import static com.ub.smssender.services.WSUtils.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends Activity {

    private static final String[] PERMISOS =  {permission.READ_PHONE_STATE, permission.SEND_SMS};


    private TextView txtImei;
    private TextView txtModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtImei = (TextView) findViewById(R.id.txtImei);
        txtModel = (TextView) findViewById(R.id.txtModel);

        this.permisos(PERMISOS); //SOLICITAR PERMISOS

    }

    private void init(){
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        for (String s : telephonyInfo.getImeiList()) {
            txtImei.setText(txtImei.getText() + "\n" + s);
        }

        txtModel.setText(txtModel.getText() + "\n" + Build.MANUFACTURER + " " + Build.MODEL);

        SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            Log.d("info","subscriptionId:"+subscriptionId +" name: " + subscriptionInfo.getDisplayName());
            //SmsManager.getSmsManagerForSubscriptionId(subscriptionId).sendTextMessage("6672118438", null, "un text", null, null);
        }

        Intent serviceIntent = new Intent(MainActivity.this, SMSService.class);
        MainActivity.this.startService(serviceIntent);
    }

    private void permisos(String[] listaPermisos) {
        boolean validos = true;
        for (String permiso : PERMISOS) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permiso) != PackageManager.PERMISSION_GRANTED){
                validos = false;
                break;
            }
        }
        if (!validos) { //pedir permisos
            ActivityCompat.requestPermissions(MainActivity.this, listaPermisos, 1);
        }else{ //permisos garantizados
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        boolean todosLosPermisos = true;
        for (int grantResult : grantResults) {
            if (grantResult != 0){
                todosLosPermisos = false;
            }
        }
        if (todosLosPermisos){
            init();
        }else{
            Toast.makeText(MainActivity.this, "Se necesitan todos los permisos para operar", Toast.LENGTH_LONG).show();
        }
    }

    private void obtenerMensajes(String imei){
        final Call<ModelMensajes> request = webServices().mensajes("58a0e7c955ceb035c6449a82", imei);
        request.enqueue(new Callback<ModelMensajes>() {
            @Override
            public void onResponse(Call<ModelMensajes> call, Response<ModelMensajes> response) {
                if (response.isSuccessful()){
                    Log.i("success", response.body().toString());
                    Toast.makeText(MainActivity.this, "mensajes obtenidos", Toast.LENGTH_LONG).show();;
                }else{
                    Log.w("Warning", "no suscces");
                    Toast.makeText(MainActivity.this, "Atencion respuesta no exitosa", Toast.LENGTH_LONG).show();;
                }
            }

            @Override
            public void onFailure(Call<ModelMensajes> call, Throwable t) {
                Toast.makeText(MainActivity.this, "peticion al servidor fallida", Toast.LENGTH_LONG).show();;
                Log.e("Service: ", t.getMessage());

            }
        });
    }

}
