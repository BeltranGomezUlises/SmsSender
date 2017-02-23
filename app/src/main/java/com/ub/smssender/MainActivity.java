package com.ub.smssender;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.smssender.models.ModelMensajes;
import static com.ub.smssender.services.WSUtils.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private TextView txtData;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtData = (TextView) findViewById(R.id.txtImei);
        btnSend = (Button) findViewById(R.id.btnSend); btnSend.setEnabled(false);

        this.permisos();

    }

    private void init(){
        btnSend.setEnabled(true);
        btnSend.setOnClickListener((view) ->{ System.out.println("algo"); });
        txtData.setText(this.getDevideImei());
        this.obtenerMensajes();

        //iniciar servicio de mensajes

    }

    private void permisos() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //solicitar el permiso
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE
            );
        } else {
            //permisos ya concedidos
            this.init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.w("permision", "PERMISO ACCEDIDO");
                    Toast.makeText(MainActivity.this, "Servicio listo para operar", Toast.LENGTH_LONG).show();;
                    this.init();
                } else {
                    //mostrar mensaje de necesida de permiso para funcionar
                    Toast.makeText(MainActivity.this, "Son necesarios permisos para operar", Toast.LENGTH_LONG).show();;
                    Log.w("permision", "PERMISO DENEGADO");
                }
                return;
            }
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
