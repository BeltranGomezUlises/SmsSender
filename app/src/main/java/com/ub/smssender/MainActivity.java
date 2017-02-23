package com.ub.smssender;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.smssender.models.ModelMensajes;
import static com.ub.smssender.services.WSUtils.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends Activity {

    private static final int PERMISSION_READ_PHONE_STATE = 1;
    private static final int PERMISSION_SEND_SMS = 2;
    
    private TextView txtData;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtData = (TextView) findViewById(R.id.txtImei);
        btnSend = (Button) findViewById(R.id.btnSend); btnSend.setEnabled(false);

        this.permisos(); //SOLICITAR PERMISOS

    }

    private void init(){
        //si TODOS los permisos esta garantizados
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            btnSend.setEnabled(true);
            btnSend.setOnClickListener((view) ->{
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("6671995737", null, "hola dani", null,null);
            });
            txtData.setText(this.getDevideImei());
        }else{
            System.out.println("ambos permisos no garantizados");
        }

        //this.obtenerMensajes();

        //iniciar servicio de mensajes

    }

    private void permisos() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //solicitar el permiso
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_READ_PHONE_STATE
            );
        } else {
            Log.i("permiso", "read phone state ya concedido");
            init();
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            //solicitar el permiso
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS
            );
        } else {
            //permisos ya concedidos
            Log.i("permiso", "Send sms ya concedido");
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case PERMISSION_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.w("permision", "PERMISO ACCEDIDO code: " + PERMISSION_READ_PHONE_STATE);
                    Toast.makeText(MainActivity.this, "Servicio listo para operar", Toast.LENGTH_LONG).show();
                    init();
                } else {
                    //mostrar mensaje de necesida de permiso para funcionar
                    Toast.makeText(MainActivity.this, "Son necesarios permisos para operar", Toast.LENGTH_LONG).show();;
                    Log.w("permision", "PERMISO DENEGADO code: " + PERMISSION_READ_PHONE_STATE);
                }
                break;
            case PERMISSION_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.w("permision", "PERMISO ACCEDIDO code: " + PERMISSION_SEND_SMS);
                    Toast.makeText(MainActivity.this, "Servicio listo para operar", Toast.LENGTH_LONG).show();;
                    init();
                } else {
                    //mostrar mensaje de necesida de permiso para funcionar
                    Toast.makeText(MainActivity.this, "Son necesarios permisos para operar", Toast.LENGTH_LONG).show();;
                    Log.w("permision", "PERMISO DENEGADO code: " + PERMISSION_SEND_SMS);
                }
                break;
            default: Log.i("Atention", "No requestCode"); break;
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
