package com.valquiria.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PrincipalEmpresa extends AppCompatActivity {

    Button indicador;
    Button recorrido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_empresa);

        indicador = (Button) findViewById(R.id.indicador);
        recorrido = (Button) findViewById(R.id.recorrido);

        indicador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrincipalEmpresa.this, AgregarIndicador.class);
                startActivity(intent);
            }
        });

        recorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrincipalEmpresa.this, AgregarRutaEmpresa.class);
                startActivity(intent);
            }
        });
    }
}
