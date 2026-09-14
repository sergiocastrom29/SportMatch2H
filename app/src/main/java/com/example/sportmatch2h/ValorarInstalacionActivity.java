package com.example.sportmatch2h;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ValorarInstalacionActivity extends AppCompatActivity {

    private int puntuacion = 0;
    private ImageView[] estrellas;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String complejo, deporte, instalacion, tipoPista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valorar_instalacion);
        BottomNavHelper.setup(this, findViewById(android.R.id.content));

        complejo = getIntent().getStringExtra("COMPLEJO_SELECCIONADO");
        deporte = getIntent().getStringExtra("DEPORTE_SELECCIONADO");
        instalacion = getIntent().getStringExtra("PISTA_SELECCIONADA");
        tipoPista = getIntent().getStringExtra("TIPO_PISTA");

        TextView tvComplejo = findViewById(R.id.tv_complejo);
        TextView tvDeporte = findViewById(R.id.tv_deporte);
        TextView tvInstalacion = findViewById(R.id.tv_instalacion);
        TextView tvPuntuacion = findViewById(R.id.tv_puntuacion_seleccionada);

        tvComplejo.setText(complejo != null ? complejo : "-");
        tvDeporte.setText(deporte != null ? deporte : "-");
        tvInstalacion.setText(instalacion != null ? instalacion : "-");

        estrellas = new ImageView[]{
            findViewById(R.id.estrella_1),
            findViewById(R.id.estrella_2),
            findViewById(R.id.estrella_3),
            findViewById(R.id.estrella_4),
            findViewById(R.id.estrella_5)
        };

        for (int i = 0; i < estrellas.length; i++) {
            final int puntuacionEstrella = i + 1;
            estrellas[i].setOnClickListener(v -> {
                puntuacion = puntuacionEstrella;
                actualizarEstrellas();
                tvPuntuacion.setText(puntuacionEstrella + " estrella" + (puntuacionEstrella > 1 ? "s" : ""));
            });
        }

        MaterialButton btnEnviar = findViewById(R.id.btn_enviar);
        btnEnviar.setOnClickListener(v -> validarYEnviar());
    }

    private void actualizarEstrellas() {
        for (int i = 0; i < estrellas.length; i++) {
            if (i < puntuacion) {
                estrellas[i].setImageResource(R.drawable.ic_estrella_llena);
            } else {
                estrellas[i].setImageResource(R.drawable.ic_estrella_vacia);
            }
        }
    }

    private void validarYEnviar() {
        if (puntuacion == 0) {
            Toast.makeText(this, "Selecciona una puntuación", Toast.LENGTH_SHORT).show();
            return;
        }

        TextInputEditText etComentario = findViewById(R.id.et_comentario);
        String comentario = etComentario.getText() != null ? etComentario.getText().toString().trim() : "";

        if (comentario.length() < 10) {
            Toast.makeText(this, "El comentario debe tener al menos 10 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        String instalacionCompleta = instalacion;
        if (tipoPista != null && !tipoPista.isEmpty()) {
            String tipoCapitalizado = tipoPista.substring(0, 1).toUpperCase() + tipoPista.substring(1).toLowerCase();
            instalacionCompleta = instalacion + " " + tipoCapitalizado;
        }

        Map<String, Object> valoracion = new HashMap<>();
        valoracion.put("complejo", complejo);
        valoracion.put("instalacion", instalacionCompleta);
        valoracion.put("instalacion_original", instalacion);
        valoracion.put("tipo", tipoPista != null ? tipoPista : "");
        valoracion.put("deporte", deporte);
        valoracion.put("puntuacion", puntuacion);
        valoracion.put("comentario", comentario);
        valoracion.put("fecha", com.google.firebase.Timestamp.now());

        db.collection("valoraciones_instalaciones")
            .add(valoracion)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(this, "Valoración enviada correctamente", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            })
            .addOnFailureListener(e -> {
                Log.e("ValorarInstalacion", "Error al enviar valoración", e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }
}