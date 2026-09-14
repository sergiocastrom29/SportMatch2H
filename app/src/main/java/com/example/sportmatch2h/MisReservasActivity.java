package com.example.sportmatch2h;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MisReservasActivity extends AppCompatActivity {

    private LinearLayout containerReservas;
    private View emptyState;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);
        BottomNavHelper.setup(this, findViewById(android.R.id.content));

        containerReservas = findViewById(R.id.container_reservas);
        emptyState = findViewById(R.id.empty_state);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarReservas();
    }

    private void cargarReservas() {
        containerReservas.removeAllViews();

        db.collection("reservas")
                .whereEqualTo("id_usuario", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        mostrarEmptyState(true);
                    } else {
                        mostrarEmptyState(false);
                        
                        java.util.List<com.google.firebase.firestore.DocumentSnapshot> docs = new java.util.ArrayList<>();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            docs.add(doc);
                        }
                        
                        docs.sort((d1, d2) -> {
                            String f1 = d1.getString("fecha_reserva");
                            String f2 = d2.getString("fecha_reserva");
                            if (f1 == null) f1 = "";
                            if (f2 == null) f2 = "";
                            return f1.compareTo(f2);
                        });
                        
                        for (com.google.firebase.firestore.DocumentSnapshot doc : docs) {
                            String Complejo = doc.getString("nombre_complejo");
                            String deporte = doc.getString("deporte");
                            String pista = doc.getString("id_instalacion");
                            String fecha = doc.getString("fecha_reserva");
                            String hora = doc.getString("hora_reserva");
                            Double precio = doc.getDouble("total_pagado");
                            Boolean confirmado = doc.getBoolean("confirmado");

                            agregarReservaCard(deporte, pista, fecha, hora, precio, confirmado);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar reservas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    mostrarEmptyState(true);
                });
    }

    private void agregarReservaCard(String deporte, String pista, String fecha, String hora, Double precio, Boolean confirmado) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, (int) (12 * getResources().getDisplayMetrics().density));
        card.setLayoutParams(lp);
        card.setCardBackgroundColor(0xFFFFFFFF);
        card.setCardElevation(4 * getResources().getDisplayMetrics().density);
        card.setRadius(16 * getResources().getDisplayMetrics().density);
        card.setStrokeWidth(0);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(
                (int) (16 * getResources().getDisplayMetrics().density),
                (int) (16 * getResources().getDisplayMetrics().density),
                (int) (16 * getResources().getDisplayMetrics().density),
                (int) (16 * getResources().getDisplayMetrics().density)
        );

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tvDeporte = new TextView(this);
        tvDeporte.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        tvDeporte.setText(deporte != null ? deporte : "Reserva");
        tvDeporte.setTextColor(0xFF1A2340);
        tvDeporte.setTextSize(16);
        tvDeporte.setTypeface(null, android.graphics.Typeface.BOLD);
        header.addView(tvDeporte);

        TextView tvEstado = new TextView(this);
        tvEstado.setText(confirmado != null && confirmado ? "Confirmada" : "Completada");
        tvEstado.setTextSize(10);
        tvEstado.setTextColor(0xFFFFFFFF);
        tvEstado.setPadding(
                (int) (10 * getResources().getDisplayMetrics().density),
                (int) (4 * getResources().getDisplayMetrics().density),
                (int) (10 * getResources().getDisplayMetrics().density),
                (int) (4 * getResources().getDisplayMetrics().density)
        );
        tvEstado.setBackgroundColor(0xFF1E8449);
        header.addView(tvEstado);

        layout.addView(header);

        TextView tvPista = new TextView(this);
        tvPista.setText(pista != null ? pista : "");
        tvPista.setTextColor(0xFF4A5568);
        tvPista.setTextSize(13);
        tvPista.setPadding(0, (int) (8 * getResources().getDisplayMetrics().density), 0, 0);
        layout.addView(tvPista);

        LinearLayout rowFechaHora = new LinearLayout(this);
        rowFechaHora.setOrientation(LinearLayout.HORIZONTAL);
        rowFechaHora.setPadding(0, (int) (8 * getResources().getDisplayMetrics().density), 0, 0);

        TextView tvFecha = new TextView(this);
        tvFecha.setText(fecha != null ? fecha : "");
        tvFecha.setTextColor(0xFF4A5568);
        tvFecha.setTextSize(12);
        rowFechaHora.addView(tvFecha);

        if (hora != null) {
            TextView tvHora = new TextView(this);
            tvHora.setText("  " + hora);
            tvHora.setTextColor(0xFF4A5568);
            tvHora.setTextSize(12);
            rowFechaHora.addView(tvHora);
        }

        layout.addView(rowFechaHora);

        String precioStr = precio != null ? String.format("%.2f €", precio) : "";
        TextView tvPrecio = new TextView(this);
        tvPrecio.setText(precioStr);
        tvPrecio.setTextColor(0xFF1E8449);
        tvPrecio.setTextSize(15);
        tvPrecio.setTypeface(null, android.graphics.Typeface.BOLD);
        tvPrecio.setPadding(0, (int) (10 * getResources().getDisplayMetrics().density), 0, 0);
        layout.addView(tvPrecio);

        card.addView(layout);
        containerReservas.addView(card);
    }

    private void mostrarEmptyState(boolean show) {
        emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        containerReservas.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}