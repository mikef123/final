package com.valquiria.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViewById(R.id.ruta).setOnClickListener(this);
        findViewById(R.id.recorridos).setOnClickListener(this);
    }
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ruta:
                Intent intent = new Intent(Menu.this, Principal.class);
                startActivity(intent);
                break;
            case R.id.recorridos:
                Intent intent1 = new Intent(Menu.this, Recorrido.class);
                startActivity(intent1);
                break;

        }
    }
}
