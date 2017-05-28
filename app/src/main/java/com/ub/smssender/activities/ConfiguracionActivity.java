package com.ub.smssender.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ub.smssender.R;
import com.ub.smssender.entities.ImeiRealm;
import com.ub.smssender.services.SMSService;
import com.ub.smssender.utils.UtilPreferences;

import java.util.List;

import io.realm.Realm;

public class ConfiguracionActivity extends AppCompatActivity {

    private EditText edtIntervalo;
    private EditText edtNumMensajes;

    private Button btnCancelar;
    private Button btnGuardar;
    private Button btnLimpiarContadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        edtIntervalo = (EditText) findViewById(R.id.edt_intervalo);
        edtNumMensajes = (EditText) findViewById(R.id.edt_numMensajes);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnLimpiarContadores = (Button) findViewById(R.id.btnLimpiarContadores);

        edtNumMensajes.setText(String.valueOf(UtilPreferences.loadNumMensajes(ConfiguracionActivity.this)));
        edtIntervalo.setText(String.valueOf(UtilPreferences.loadIntervalTime(ConfiguracionActivity.this)));

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfiguracionActivity.this.onBackPressed();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilPreferences.saveIntervalTime(ConfiguracionActivity.this, Integer.parseInt(edtIntervalo.getText().toString()));
                UtilPreferences.saveNumMensajes(ConfiguracionActivity.this, Integer.parseInt(edtNumMensajes.getText().toString()));

                ConfiguracionActivity.this.onBackPressed();

            }
        });

        btnLimpiarContadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            new AlertDialog.Builder(ConfiguracionActivity.this)
                .setMessage("¿Seguro de borrar los contadores?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Realm.init(ConfiguracionActivity.this);
                        Realm r = Realm.getDefaultInstance();

                        List<ImeiRealm> imeiRealmList = r.where(ImeiRealm.class).findAll();
                        r.beginTransaction();

                        for (ImeiRealm imeiRealm : imeiRealmList) {
                            imeiRealm.setCounter(0);
                            r.copyToRealmOrUpdate(imeiRealm);
                        }
                        r.commitTransaction();
                        r.close();
                        MainActivity.borrarContadores();

                        Toast.makeText(ConfiguracionActivity.this, "Contadores en 0 de nuevo", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("No", null)
                .show();
            }
        });

    }

}
