package com.example.sportmatch2h;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "UploadInstalaciones";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        db = FirebaseFirestore.getInstance();
        BottomNavHelper.setup(this, findViewById(android.R.id.content));

        MaterialCardView cardReservar = findViewById(R.id.card_reservar);
        MaterialCardView cardClases = findViewById(R.id.card_clases);
        MaterialCardView cardHorarios = findViewById(R.id.card_horarios);
        MaterialCardView cardValoraciones = findViewById(R.id.card_valoraciones);

        View.OnClickListener listener = v -> {
            int id = v.getId();
            if (id == R.id.card_reservar) {
                Intent intent = new Intent(MenuActivity.this, ReservaInstalacionesActivity.class);
                startActivity(intent);
            } else if (id == R.id.card_clases) {
                Intent intent = new Intent(MenuActivity.this, ClasesTalleresActivity.class);
                startActivity(intent);
            } else if (id == R.id.card_horarios) {
                Intent intent = new Intent(MenuActivity.this, HorariosComplejosActivity.class);
                startActivity(intent);
            } else if (id == R.id.card_valoraciones) {
                Intent intent = new Intent(MenuActivity.this, ValoracionesActivity.class);
                startActivity(intent);
            }
        };

        cardReservar.setOnClickListener(listener);
        cardClases.setOnClickListener(listener);
        cardHorarios.setOnClickListener(listener);
        cardValoraciones.setOnClickListener(listener);
    }

}