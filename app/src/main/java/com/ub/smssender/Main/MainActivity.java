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
import android.widget.EditText;
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

    private Button btnSim1;
    private Button btnSim2;

    private EditText edtCell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtImei = (TextView) findViewById(R.id.txtImei);
        txtModel = (TextView) findViewById(R.id.txtModel);
        btnSim1 = (Button) findViewById(R.id.btnSim1);
        btnSim2 = (Button) findViewById(R.id.btnSim2);
        edtCell = (EditText) findViewById(R.id.edtCell);


        this.permisos(PERMISOS); //SOLICITAR PERMISOS

    }

    private void init(){
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        List<String> imeiList = telephonyInfo.getImeiList();
        for (String s : imeiList) {
            txtImei.setText(txtImei.getText() + "\n" + s);
        }

        btnSim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cell = edtCell.getText().toString();
                System.out.println(cell);

                if (cell.length() != 10){
                    Toast.makeText(MainActivity.this, "El número no es valido", Toast.LENGTH_LONG).show();
                }else{
                    SmsManager.getDefault().sendTextMessage(cell, null, "Mensaje de prueba sim 1 - Ulises Beltrán", null, null);
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
                            SmsManager.getSmsManagerForSubscriptionId(2).sendTextMessage(cell, null, "Mensaje de prueba sim 2 - Ulises Beltrán", null, null);
                            Toast.makeText(MainActivity.this, "Enviando mensaje...", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Version de android incompatible", Toast.LENGTH_LONG).show();
                        }

                    }

                }
            });
        }



        txtModel.setText(txtModel.getText() + "\n" + Build.MANUFACTURER + " " + Build.MODEL);

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
}
