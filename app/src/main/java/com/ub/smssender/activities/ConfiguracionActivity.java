package com.ub.smssender.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ub.smssender.R;
import com.ub.smssender.utils.UtilPreferences;

public class ConfiguracionActivity extends AppCompatActivity {

    private EditText edtIntervalo;
    private EditText edtNumMensajes;

    private Button cancelar;
    private Button guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        edtIntervalo = (EditText) findViewById(R.id.edt_intervalo);
        edtNumMensajes = (EditText) findViewById(R.id.edt_numMensajes);
        cancelar = (Button) findViewById(R.id.btnCancelar);
        guardar = (Button) findViewById(R.id.btnGuardar);

        edtNumMensajes.setText(String.valueOf(UtilPreferences.loadNumMensajes(ConfiguracionActivity.this)));
        edtIntervalo.setText(String.valueOf(UtilPreferences.loadIntervalTime(ConfiguracionActivity.this)));

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfiguracionActivity.this.onBackPressed();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilPreferences.saveIntervalTime(ConfiguracionActivity.this, Integer.parseInt(edtIntervalo.getText().toString()));
                UtilPreferences.saveNumMensajes(ConfiguracionActivity.this, Integer.parseInt(edtNumMensajes.getText().toString()));

                ConfiguracionActivity.this.onBackPressed();

            }
        });


    }

}
