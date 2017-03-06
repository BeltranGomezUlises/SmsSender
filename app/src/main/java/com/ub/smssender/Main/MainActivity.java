package com.ub.smssender.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.smssender.R;
import com.ub.smssender.utils.TelephonyInfo;
import com.ub.smssender.utils.UtilPreferences;

import java.util.List;

import static android.Manifest.permission;


public class MainActivity extends Activity {

    private static final String[] PERMISOS =  {permission.READ_PHONE_STATE, permission.SEND_SMS};


    private TextView txtImei;
    private TextView txtModel;
    private Button btnSalir;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtImei = (TextView) findViewById(R.id.txtImei);
        txtModel = (TextView) findViewById(R.id.txtModel);
        btnSalir = (Button) findViewById(R.id.btnSalir);

        this.permisos(PERMISOS); //SOLICITAR PERMISOS

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void suscriptionInfo(){
        SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            Log.d("info","subscriptionId:"+subscriptionId +" name: " + subscriptionInfo.getDisplayName());

            /*TO SEND A MESSAGE FRON A PARTICULAR SUSCRIPTION*/
            //SmsManager.getSmsManagerForSubscriptionId(subscriptionId).sendTextMessage("6672118438", null, "un text", null, null);
        }
    }

    private void init(){
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        List<String> imeiList = telephonyInfo.getImeiList();
        for (String s : imeiList) {
            txtImei.setText(txtImei.getText() + "\n" + s);
        }

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                salirYDetener();
            }
        });
        //this.temporalListener(imeiList);

        txtModel.setText(txtModel.getText() + "\n" + Build.MANUFACTURER + " " + Build.MODEL);

        serviceIntent = new Intent(MainActivity.this, SMSService.class);
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

    private void obtenerMensajes(String imei){

    }

    private void salirYDetener(){
        UtilPreferences.LogOutPreferences(MainActivity.this);
        MainActivity.this.finishAffinity();
    }

    /*
    private void temporalListener(List<String> imeiList){
        btnSim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cell = edtCell.getText().toString();
                System.out.println(cell);

                if (cell.length() != 10){
                    Toast.makeText(MainActivity.this, "El número no es valido", Toast.LENGTH_LONG).show();
                }else{
                    SmsManager.getDefault().sendTextMessage(cell, null, "ModelMensaje de prueba sim 1 - Ulises Beltrán", null, null);
                    Toast.makeText(MainActivity.this, "Enviando mensaje...", Toast.LENGTH_LONG).show();
                }

            }
        });

        if (imeiList.size() == 1){
            btnSim2.setVisibility(View.INVISIBLE);
        }else{
            btnSim2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cell = edtCell.getText().toString();
                    System.out.println(cell);

                    if (cell.length() != 10){
                        Toast.makeText(MainActivity.this, "El número no es valido", Toast.LENGTH_LONG).show();
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                            SmsManager.getSmsManagerForSubscriptionId(2).sendTextMessage(cell, null, "ModelMensaje de prueba sim 2 - Ulises Beltrán", null, null);
                            Toast.makeText(MainActivity.this, "Enviando mensaje...", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Version de android incompatible", Toast.LENGTH_LONG).show();
                        }

                    }

                }
            });
        }
    }
    */
}
