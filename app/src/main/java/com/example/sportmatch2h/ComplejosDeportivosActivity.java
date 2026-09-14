package com.example.sportmatch2h;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class ComplejosDeportivosActivity extends AppCompatActivity {

    private String tipoValoracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complejos_deportivos);
        BottomNavHelper.setup(this, findViewById(android.R.id.content));

        // Recogemos el deporte de la pantalla anterior (ej: "Pádel")
        String deporteSeleccionado = getIntent().getStringExtra("DEPORTE_SELECCIONADO");
        tipoValoracion = getIntent().getStringExtra(ValoracionesActivity.TIPO_VALORACION);
        boolean esValoracion = tipoValoracion != null;

        TextView tvTitulo = findViewById(R.id.tv_titulo_pantalla);
        if (esValoracion) {
            tvTitulo.setText("VALORAR INSTALACIÓN");
        }

        MaterialCardView cardPalacio = findViewById(R.id.card_palacio);
        MaterialCardView cardRamonCajal = findViewById(R.id.card_ramon_cajal);
        MaterialCardView cardMontequinto = findViewById(R.id.card_montequinto);

        View.OnClickListener listener = v -> {
            Intent intent = new Intent(ComplejosDeportivosActivity.this, PistasActivity.class);

            // Pasamos el deporte a la siguiente pantalla
            intent.putExtra("DEPORTE_SELECCIONADO", deporteSeleccionado);

            if (esValoracion) {
                intent.putExtra(ValoracionesActivity.TIPO_VALORACION, tipoValoracion);
            }

            int id = v.getId();
            if (id == R.id.card_palacio) {
                intent.putExtra("COMPLEJO_SELECCIONADO", "Palacio de los Deportes");
            } else if (id == R.id.card_ramon_cajal) {
                intent.putExtra("COMPLEJO_SELECCIONADO", "Polideportivo Ramón y Cajal");
            } else if (id == R.id.card_montequinto) {
                intent.putExtra("COMPLEJO_SELECCIONADO", "Zona Montequinto");
            }

            startActivity(intent);
        };

        cardPalacio.setOnClickListener(listener);
        cardRamonCajal.setOnClickListener(listener);
        cardMontequinto.setOnClickListener(listener);
    }
}