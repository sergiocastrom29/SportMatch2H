package com.example.sportmatch2h;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class ValoracionesActivity extends AppCompatActivity {

    public static final String TIPO_VALORACION = "TIPO_VALORACION";
    public static final String TIPO_INSTALACION = "instalacion";
    public static final String TIPO_MONITOR = "monitor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoraciones);
        BottomNavHelper.setup(this, findViewById(android.R.id.content));

        MaterialCardView cardValorarInstalacion = findViewById(R.id.card_valorar_instalacion);
        MaterialCardView cardValorarMonitor = findViewById(R.id.card_valorar_monitor);

        cardValorarInstalacion.setOnClickListener(v -> {
            Intent intent = new Intent(ValoracionesActivity.this, ReservaInstalacionesActivity.class);
            intent.putExtra(TIPO_VALORACION, TIPO_INSTALACION);
            startActivity(intent);
        });

        cardValorarMonitor.setOnClickListener(v -> {
            Intent intent = new Intent(ValoracionesActivity.this, ReservaInstalacionesActivity.class);
            intent.putExtra(TIPO_VALORACION, TIPO_MONITOR);
            startActivity(intent);
        });
    }
}