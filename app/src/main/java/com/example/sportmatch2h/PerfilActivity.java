package com.example.sportmatch2h;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvIniciales;
    private TextView tvNombreUsuario;
    private TextView tvEmailUsuario;
    private TextView tvNombreCompleto;
    private TextView tvEmail;
    private TextView tvTelefono;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        BottomNavHelper.setup(this, findViewById(android.R.id.content));

        tvIniciales = findViewById(R.id.tv_iniciales);
        tvNombreUsuario = findViewById(R.id.tv_nombre_usuario);
        tvEmailUsuario = findViewById(R.id.tv_email_usuario);
        tvNombreCompleto = findViewById(R.id.tv_nombre_completo);
        tvEmail = findViewById(R.id.tv_email);
        tvTelefono = findViewById(R.id.tv_telefono);

        cargarDatosUsuario();

        MaterialCardView cardMisReservas = findViewById(R.id.card_mis_reservas);
        cardMisReservas.setOnClickListener(v -> {
            startActivity(new Intent(PerfilActivity.this, MisReservasActivity.class));
        });

        MaterialCardView cardCerrarSesion = findViewById(R.id.card_cerrar_sesion);
        cardCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PerfilActivity.this, MainActivity.class));
            finish();
        });
    }

    private void cargarDatosUsuario() {
        var user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No has iniciado sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String email = user.getEmail();
        if (email != null) {
            tvEmail.setText(email);
            tvEmailUsuario.setText(email);
            tvIniciales.setText(obtenerIniciales(email));
        }

        String displayName = user.getDisplayName();
        if (displayName != null && !displayName.isEmpty()) {
            tvNombreUsuario.setText(displayName);
            tvNombreCompleto.setText(displayName);
        }

        db.collection("usuarios").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        if (nombre != null && !nombre.isEmpty()) {
                            tvNombreUsuario.setText(nombre);
                            tvNombreCompleto.setText(nombre);
                            tvIniciales.setText(obtenerIniciales(nombre));
                        }
                        String telefono = documentSnapshot.getString("telefono");
                        if (telefono != null) {
                            tvTelefono.setText(telefono);
                        }
                    }
                });
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isEmpty()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length >= 2) {
            return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
        } else if (partes[0].length() >= 2) {
            return partes[0].substring(0, 2).toUpperCase();
        }
        return partes[0].substring(0, 1).toUpperCase();
    }
}