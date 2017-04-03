package com.ub.smssender.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ub.smssender.R;
import com.ub.smssender.models.JWTBody;
import com.ub.smssender.models.ModelUsuario;
import com.ub.smssender.utils.JWTDecoder;
import com.ub.smssender.utils.TelephonyInfo;
import com.ub.smssender.utils.UtilPreferences;
import com.ub.smssender.views.adapters.ImeiListAdapter;
import com.ub.smssender.views.models.ImeiViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission;


public class MainActivity extends Activity {

    private static final String[] PERMISOS =  {permission.READ_PHONE_STATE, permission.SEND_SMS};
    private TextView txtModel;
    private Button btnSalir;
    private Intent serviceIntent;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<ImeiViewModel> imeiModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtModel = (TextView) findViewById(R.id.tvModel);
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
            Log.d("info","subscriptionId:"+ subscriptionId +" name: " + subscriptionInfo.getDisplayName());
        }
    }

    private void init(){
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        List<String> imeiList = telephonyInfo.getImeiList();

        for (String s : imeiList) {
            ImeiViewModel model = new ImeiViewModel(s, 0);
            imeiModels.add(model);
        }

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                salirYDetener();
            }
        });
        txtModel.setText(txtModel.getText() + "\n" + Build.MANUFACTURER + " " + Build.MODEL);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ImeiListAdapter(imeiModels);
        mRecyclerView.setAdapter(mAdapter);

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

    private void salirYDetener(){
        UtilPreferences.LogOutPreferences(MainActivity.this);
        SMSService.stopTimer();
        MainActivity.this.finishAffinity();
    }

    public void countSmsSended(String imei){

    }



}

