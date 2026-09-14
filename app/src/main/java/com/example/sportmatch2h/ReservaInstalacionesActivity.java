package com.example.sportmatch2h;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class ReservaInstalacionesActivity extends AppCompatActivity {

    private String tipoValoracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_instalaciones);
        BottomNavHelper.setup(this, findViewById(android.R.id.content));

        tipoValoracion = getIntent().getStringExtra(ValoracionesActivity.TIPO_VALORACION);
        boolean esValoracion = tipoValoracion != null;

        TextView tvTitulo = findViewById(R.id.tv_titulo_pantalla);
        if (esValoracion) {
            tvTitulo.setText("VALORAR INSTALACIÓN");
        }

        // Textos de las tarjetas
        TextView tvReservarPadel = findViewById(R.id.tv_reservar_padel);
        TextView tvReservarTenis = findViewById(R.id.tv_reservar_tenis);
        TextView tvReservarFutbol = findViewById(R.id.tv_reservar_futbol);
        TextView tvReservarFutbolSala = findViewById(R.id.tv_reservar_futbol_sala);

        if (esValoracion) {
            tvReservarPadel.setText("VALORAR");
            tvReservarTenis.setText("VALORAR");
            tvReservarFutbol.setText("VALORAR");
            tvReservarFutbolSala.setText("VALORAR");
        }

        // Enlazamos las tarjetas del XML
        MaterialCardView cardPadel = findViewById(R.id.card_padel);
        MaterialCardView cardTenis = findViewById(R.id.card_tenis);
        MaterialCardView cardFutbol = findViewById(R.id.card_futbol);
        MaterialCardView cardFutbolSala = findViewById(R.id.card_futbol_sala);

        // Creamos un escuchador de clics genérico
        View.OnClickListener listener = v -> {
            Intent intent = new Intent(ReservaInstalacionesActivity.this, ComplejosDeportivosActivity.class);

            if (esValoracion) {
                intent.putExtra(ValoracionesActivity.TIPO_VALORACION, tipoValoracion);
            }

            // "putExtra" envía un dato a la siguiente pantalla
            int id = v.getId();
            if (id == R.id.card_padel) {
                intent.putExtra("DEPORTE_SELECCIONADO", "Pádel");
            } else if (id == R.id.card_tenis) {
                intent.putExtra("DEPORTE_SELECCIONADO", "Tenis");
            } else if (id == R.id.card_futbol) {
                intent.putExtra("DEPORTE_SELECCIONADO", "Fútbol");
            } else if (id == R.id.card_futbol_sala) {
                intent.putExtra("DEPORTE_SELECCIONADO", "Fútbol Sala");
            }

            startActivity(intent);
        };

        // Asignamos el escuchador a todas las tarjetas
        cardPadel.setOnClickListener(listener);
        cardTenis.setOnClickListener(listener);
        cardFutbol.setOnClickListener(listener);
        cardFutbolSala.setOnClickListener(listener);
    }
}