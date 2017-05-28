package com.ub.smssender.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.smssender.R;
import com.ub.smssender.entities.ImeiRealm;
import com.ub.smssender.services.SMSService;
import com.ub.smssender.services.SmsDeliveredReceiver;
import com.ub.smssender.services.SmsSentReceiver;
import com.ub.smssender.utils.TelephonyInfo;
import com.ub.smssender.utils.UtilPreferences;
import com.ub.smssender.views.adapters.ImeiListAdapter;
import com.ub.smssender.views.models.ImeiViewModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static android.Manifest.permission;


public class MainActivity extends AppCompatActivity {

    private static final String[] PERMISOS = {permission.READ_PHONE_STATE, permission.SEND_SMS};
    private static RecyclerView.Adapter mAdapter;
    private static List<ImeiViewModel> imeiModels;
    private Intent serviceIntent;

    private TextView txtModel;
    private Button btnSalir;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    public static void incrementarEnviados(String imei) {
        for (ImeiViewModel imeiModel : imeiModels) {
            if (imeiModel.getImei().equals(imei)) {
                imeiModel.incrementCounter();
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public static void borrarContadores() {
        for (ImeiViewModel imeiModel : imeiModels) {
            imeiModel.borrarContador();
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtModel = (TextView) findViewById(R.id.tvModel);
        btnSalir = (Button) findViewById(R.id.btnSalir);

        this.permisos(PERMISOS); //SOLICITAR PERMISOS

        //limpiar db
        Realm.init(getApplicationContext());
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        realm.delete(MensajeRealm.class);
//        realm.commitTransaction();
//        realm.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_configuracion:

                Intent intent = new Intent(MainActivity.this, ConfiguracionActivity.class);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean todosLosPermisos = true;
        for (int grantResult : grantResults) {
            if (grantResult != 0) {
                todosLosPermisos = false;
            }
        }
        if (todosLosPermisos) {
            init();
        } else {
            Toast.makeText(MainActivity.this, "Se necesitan todos los permisos para operar", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void suscriptionInfo() {
        SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            Log.d("info", "subscriptionId:" + subscriptionId + " name: " + subscriptionInfo.getDisplayName());
        }
    }

    private void init() {
        System.out.println("ejecucion de init");
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        List<String> imeiList = telephonyInfo.getImeiList();

        Realm.init(MainActivity.this);
        Realm r = Realm.getDefaultInstance();

        imeiModels = new ArrayList<>();
        for (String s : imeiList) {

            ImeiRealm imeiRealm = r.where(ImeiRealm.class).equalTo("imei", s).findFirst();
            ImeiViewModel model;

            if (imeiRealm != null) {
                model = new ImeiViewModel(s, imeiRealm.getCounter(), imeiRealm.isActivo());
            } else {
                //si es nulo inicilizar ImeiRealm por primera vez
                r.beginTransaction();
                imeiRealm = new ImeiRealm(s, 0, true);
                r.copyToRealm(imeiRealm);
                r.commitTransaction();

                model = new ImeiViewModel(s, 0, true);
            }

            imeiModels.add(model);
        }
        r.close();

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salirYDetener();
            }
        });
        txtModel.setText(txtModel.getText() + "\n" + Build.MANUFACTURER + " " + Build.MODEL);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ImeiListAdapter(imeiModels, this);
        mRecyclerView.setAdapter(mAdapter);

        //registrar receivers
        registerReceiver(new SmsSentReceiver(), new IntentFilter(SMSService.SENT));
        registerReceiver(new SmsDeliveredReceiver(), new IntentFilter(SMSService.DELIVERED));

        serviceIntent = new Intent(MainActivity.this, SMSService.class);
        this.startService(serviceIntent);
    }

    private void permisos(String[] listaPermisos) {
        boolean validos = true;
        for (String permiso : PERMISOS) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permiso) != PackageManager.PERMISSION_GRANTED) {
                validos = false;
                break;
            }
        }
        if (!validos) { //pedir permisos
            ActivityCompat.requestPermissions(MainActivity.this, listaPermisos, 1);
        } else { //permisos garantizados
            init();
        }
    }

    private void salirYDetener() {
        //probar actualizar a vista de los cards
        UtilPreferences.LogOutPreferences(MainActivity.this);
        SMSService.stopTimer();
        MainActivity.this.finishAffinity();
    }

    public void restartService(){
        if (serviceIntent != null) {
            this.stopService(serviceIntent);
            SMSService.stopTimer();
            this.startService(serviceIntent);
        }else{
            this.startService(serviceIntent);
        }
    }


}

