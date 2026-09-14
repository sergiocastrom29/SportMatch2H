package com.example.sportmatch2h;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.email_registro);
        passwordEditText = findViewById(R.id.password_registro);
        progressBar = findViewById(R.id.progress_bar_registro);

        findViewById(R.id.btn_registrar_usuario).setOnClickListener(v -> registrarUsuario());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void registrarUsuario() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty()) {
            emailEditText.setError("El correo es obligatorio");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("La contraseña es obligatoria");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("La contraseña debe tener al menos 6 caracteres");
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

                        Map<String, Object> usuario = new HashMap<>();
                        usuario.put("email", email);
                        usuario.put("nombre", "");
                        usuario.put("telefono", "");
                        usuario.put("fecha_registro", com.google.firebase.firestore.FieldValue.serverTimestamp());

                        db.collection("usuarios").document(uid)
                                .set(usuario)
                                .addOnFailureListener(e -> android.util.Log.e("RegistroActivity", "Error guardando usuario", e));

                        final String emailRegistro = email;
                        final String passwordRegistro = password;
                        EmailSender.sendWelcomeEmail(emailRegistro, emailRegistro, passwordRegistro, (success, message) -> {
                            runOnUiThread(() -> {
                                if (!success) {
                                    Toast.makeText(RegistroActivity.this, "No se pudo enviar el correo de bienvenida", Toast.LENGTH_LONG).show();
                                }
                            });
                        });

                        Intent intent = new Intent(RegistroActivity.this, MenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage;
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            errorMessage = "La contraseña es muy débil";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            errorMessage = "El formato del correo es inválido";
                        } catch (FirebaseAuthUserCollisionException e) {
                            errorMessage = "Este correo ya está registrado";
                        } catch (Exception e) {
                            errorMessage = "Error: " + e.getMessage();
                        }
                        Toast.makeText(RegistroActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}