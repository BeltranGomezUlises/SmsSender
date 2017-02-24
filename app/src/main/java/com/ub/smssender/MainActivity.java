package com.ub.smssender;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.smssender.models.ModelMensajes;

import java.util.Arrays;

import static android.Manifest.*;
import static com.ub.smssender.services.WSUtils.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends Activity {

    private static final int PERMISSION_READ_PHONE_STATE = 1;
    private static final int PERMISSION_SEND_SMS = 2;
    
    private TextView txtData;
    private Button btnSend;

    private String[] permisos =  {permission.READ_PHONE_STATE, permission.SEND_SMS};

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtData = (TextView) findViewById(R.id.txtImei);
        btnSend = (Button) findViewById(R.id.btnSend); btnSend.setEnabled(false);


        this.permisos(permisos); //SOLICITAR PERMISOS

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void init(){

        btnSend.setEnabled(true);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("6672118438", null, "hola probando un mensaje", null,null);
            }
        });
        txtData.setText(this.getDevideImei());

        //this.obtenerMensajes();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void permisos(String[] listaPermisos) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //pedir permisos
            ActivityCompat.requestPermissions(MainActivity.this, listaPermisos, 1);
        }else{
            //permisos garantizados
            init();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        Log.w("permisions", Arrays.toString(permissions));
        Log.w("grantResults", Arrays.toString(grantResults));
        Log.w("code", String.valueOf(requestCode));

        boolean todosLosPermisos = true;
        for (int grantResult : grantResults) {
            if (grantResult != 0){
                todosLosPermisos = false;
            }
        }

        if (todosLosPermisos){
            init();
            //obtenerMensajes();
        }else{
            Toast.makeText(MainActivity.this, "Se necesitan todos los permisos para operar", Toast.LENGTH_LONG).show();
        }
    }

    private String getDevideImei(){
        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }

    private void obtenerMensajes(){
        final Call<ModelMensajes> request = webServices().mensajes("58a0e7c955ceb035c6449a82", getDevideImei());
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
