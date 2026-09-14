package com.example.sportmatch2h;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class PistasActivity extends AppCompatActivity {

    private static final int REQUEST_VALORAR = 1001;
    private String tipoValoracion;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private java.util.HashMap<String, Double> mediasValoraciones = new java.util.HashMap<>();
    private java.util.HashMap<String, String> tiposInstalaciones = new java.util.HashMap<>();
    private boolean esModoValoracion = false;

    private TextView tvNombreInt1, tvNombreInt2, tvNombreInt3, tvNombreExt1, tvNombreExt2;
    private LinearLayout seccionInteriores, seccionExteriores;
    private TextView tvTituloInteriores, tvTituloExteriores, tvSinInstalaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pistas);
        BottomNavHelper.setup(this, findViewById(android.R.id.content));

        String nombreComplejo = getIntent().getStringExtra("COMPLEJO_SELECCIONADO");
        String nombreDeporte = getIntent().getStringExtra("DEPORTE_SELECCIONADO");
        tipoValoracion = getIntent().getStringExtra(ValoracionesActivity.TIPO_VALORACION);
        esModoValoracion = tipoValoracion != null;

        TextView tvTitulo = findViewById(R.id.tv_titulo_pistas);
        if (nombreComplejo != null && nombreDeporte != null) {
            if (esModoValoracion) {
                tvTitulo.setText("VALORAR " + nombreDeporte.toUpperCase() + " EN\n" + nombreComplejo.toUpperCase());
            } else {
                tvTitulo.setText("RESERVA DE " + nombreDeporte.toUpperCase() + " EN\n" + nombreComplejo.toUpperCase());
            }
        }

        seccionInteriores = findViewById(R.id.seccion_interiores);
        seccionExteriores = findViewById(R.id.seccion_exteriores);
        tvSinInstalaciones = findViewById(R.id.tv_sin_instalaciones);
        tvTituloInteriores = findViewById(R.id.tv_titulo_interiores);
        tvTituloExteriores = findViewById(R.id.tv_titulo_exteriores);

        tvNombreInt1 = findViewById(R.id.tv_nombre_int_1);
        tvNombreInt2 = findViewById(R.id.tv_nombre_int_2);
        tvNombreInt3 = findViewById(R.id.tv_nombre_int_3);
        tvNombreExt1 = findViewById(R.id.tv_nombre_ext_1);
        tvNombreExt2 = findViewById(R.id.tv_nombre_ext_2);

        MaterialCardView cardInt1 = findViewById(R.id.card_int_1);
        MaterialCardView cardInt2 = findViewById(R.id.card_int_2);
        MaterialCardView cardInt3 = findViewById(R.id.card_int_3);
        MaterialCardView cardExt1 = findViewById(R.id.card_ext_1);
        MaterialCardView cardExt2 = findViewById(R.id.card_ext_2);
        MaterialCardView cardExt3 = findViewById(R.id.card_ext_3);
        MaterialCardView cardExt4 = findViewById(R.id.card_ext_4);
        MaterialCardView cardExt5 = findViewById(R.id.card_ext_5);
        MaterialCardView cardExt6 = findViewById(R.id.card_ext_6);

        ImageView imgInt1 = findViewById(R.id.img_int_1);
        ImageView imgInt2 = findViewById(R.id.img_int_2);
        ImageView imgInt3 = findViewById(R.id.img_int_3);
        ImageView imgExt1 = findViewById(R.id.img_ext_1);
        ImageView imgExt2 = findViewById(R.id.img_ext_2);
        ImageView imgExt3 = findViewById(R.id.img_ext_3);
        ImageView imgExt4 = findViewById(R.id.img_ext_4);
        ImageView imgExt5 = findViewById(R.id.img_ext_5);
        ImageView imgExt6 = findViewById(R.id.img_ext_6);

        int drawableIdInt1 = R.drawable.pistapadelinterior;
        int drawableIdInt2 = R.drawable.pistapadelinterior;
        int drawableIdInt3 = R.drawable.pistapadelinterior;
        int drawableIdExt1 = R.drawable.pistapadelexterior;
        int drawableIdExt2 = R.drawable.pistapadelexterior;
        int drawableIdExt3 = R.drawable.pistapadelexterior;
        int drawableIdExt4 = R.drawable.pistapadelexterior;
        int drawableIdExt5 = R.drawable.pistapadelexterior;
        int drawableIdExt6 = R.drawable.pistapadelexterior;

        if ("Pádel".equals(nombreDeporte)) {
            if ("Palacio de los Deportes".equals(nombreComplejo)) {
                drawableIdInt1 = drawableIdInt2 = drawableIdInt3 = R.drawable.pistapadelinterior;
                drawableIdExt1 = drawableIdExt2 = drawableIdExt3 = drawableIdExt4 = drawableIdExt5 = drawableIdExt6 = R.drawable.pistapadelexterior;
            } else if ("Polideportivo Ramón y Cajal".equals(nombreComplejo)) {
                drawableIdExt1 = drawableIdExt2 = R.drawable.pistapadelexterior;
            } else if ("Zona Montequinto".equals(nombreComplejo)) {
                drawableIdExt1 = drawableIdExt2 = drawableIdExt3 = drawableIdExt4 = R.drawable.pistapadelexterior;
            }
        } else if ("Tenis".equals(nombreDeporte)) {
            drawableIdExt1 = drawableIdExt2 = drawableIdExt3 = drawableIdExt4 = drawableIdExt5 = drawableIdExt6 = R.drawable.pistatenis_ramonycajal_exterior;
        } else if ("Fútbol".equals(nombreDeporte)) {
            if ("Palacio de los Deportes".equals(nombreComplejo)) {
                drawableIdExt1 = R.drawable.futbol_palaciodeportes;
            } else if ("Zona Montequinto".equals(nombreComplejo)) {
                drawableIdExt1 = R.drawable.futbol_zonamontequinto;
            } else {
                drawableIdExt1 = drawableIdExt2 = drawableIdExt3 = drawableIdExt4 = drawableIdExt5 = drawableIdExt6 = R.drawable.futbol;
            }
        } else if ("Fútbol Sala".equals(nombreDeporte)) {
            if ("Palacio de los Deportes".equals(nombreComplejo)) {
                drawableIdInt1 = drawableIdInt2 = R.drawable.futbolsala_ramonycajal_interior;
                drawableIdExt1 = drawableIdExt2 = R.drawable.futbolsala_ramonycajal_exterior;
            } else if ("Polideportivo Ramón y Cajal".equals(nombreComplejo)) {
                drawableIdInt1 = drawableIdInt2 = R.drawable.futbolsala_ramonycajal_interior;
                drawableIdExt1 = drawableIdExt2 = R.drawable.futbolsala_ramonycajal_exterior;
            } else if ("Zona Montequinto".equals(nombreComplejo)) {
                drawableIdInt1 = R.drawable.futbolsala_ramonycajal_interior;
                drawableIdInt2 = R.drawable.futbolsala_ramonycajal_interior;
            }
        }

        if (imgInt1 != null) imgInt1.setImageResource(drawableIdInt1);
        if (imgInt2 != null) imgInt2.setImageResource(drawableIdInt2);
        if (imgInt3 != null) imgInt3.setImageResource(drawableIdInt3);
        if (imgExt1 != null) imgExt1.setImageResource(drawableIdExt1);
        if (imgExt2 != null) imgExt2.setImageResource(drawableIdExt2);
        if (imgExt3 != null) imgExt3.setImageResource(drawableIdExt3);
        if (imgExt4 != null) imgExt4.setImageResource(drawableIdExt4);
        if (imgExt5 != null) imgExt5.setImageResource(drawableIdExt5);
        if (imgExt6 != null) imgExt6.setImageResource(drawableIdExt6);

        TextView tvPrecioInt1 = findViewById(R.id.tv_precio_int_1);
        TextView tvPrecioInt2 = findViewById(R.id.tv_precio_int_2);
        TextView tvPrecioInt3 = findViewById(R.id.tv_precio_int_3);
        TextView tvPrecioExt1 = findViewById(R.id.tv_precio_ext_1);
        TextView tvPrecioExt2 = findViewById(R.id.tv_precio_ext_2);
        TextView tvPrecioExt3 = findViewById(R.id.tv_precio_ext_3);
        TextView tvPrecioExt4 = findViewById(R.id.tv_precio_ext_4);
        TextView tvPrecioExt5 = findViewById(R.id.tv_precio_ext_5);
        TextView tvPrecioExt6 = findViewById(R.id.tv_precio_ext_6);

        if (tvPrecioInt1 != null && "Pádel".equals(nombreDeporte)) {
            if (tvPrecioInt1 != null) tvPrecioInt1.setText("9,00 €");
            if (tvPrecioInt2 != null) tvPrecioInt2.setText("9,00 €");
            if (tvPrecioExt1 != null) tvPrecioExt1.setText("Desde 6,50 €");
            if (tvPrecioExt2 != null) tvPrecioExt2.setText("Desde 6,50 €");
            if (tvPrecioExt3 != null) tvPrecioExt3.setText("Desde 6,50 €");
            if (tvPrecioExt4 != null) tvPrecioExt4.setText("Desde 6,50 €");
            if (tvPrecioExt5 != null) tvPrecioExt5.setText("Desde 6,50 €");
            if (tvPrecioExt6 != null) tvPrecioExt6.setText("Desde 6,50 €");

            if ("Palacio de los Deportes".equals(nombreComplejo)) {
            } else if ("Polideportivo Ramón y Cajal".equals(nombreComplejo)) {
                seccionInteriores.setVisibility(View.GONE);
                if (cardExt2 != null) cardExt2.setVisibility(View.GONE);
                if (cardExt3 != null) cardExt3.setVisibility(View.GONE);
                if (cardExt4 != null) cardExt4.setVisibility(View.GONE);
                if (cardExt5 != null) cardExt5.setVisibility(View.GONE);
                if (cardExt6 != null) cardExt6.setVisibility(View.GONE);
            } else if ("Zona Montequinto".equals(nombreComplejo)) {
                seccionInteriores.setVisibility(View.GONE);
                if (cardExt5 != null) cardExt5.setVisibility(View.GONE);
                if (cardExt6 != null) cardExt6.setVisibility(View.GONE);
            }
        } else if ("Tenis".equals(nombreDeporte)) {
            seccionInteriores.setVisibility(View.GONE);
            tvTituloExteriores.setText("PISTAS DE TENIS");
            if (tvPrecioExt1 != null) tvPrecioExt1.setText("Desde 2,50 €");
            if (tvPrecioExt2 != null) tvPrecioExt2.setText("Desde 2,50 €");
            if (tvPrecioExt3 != null) tvPrecioExt3.setText("Desde 2,50 €");
            if (tvPrecioExt4 != null) tvPrecioExt4.setText("Desde 2,50 €");
            if (tvPrecioExt5 != null) tvPrecioExt5.setText("Desde 2,50 €");
            if (tvPrecioExt6 != null) tvPrecioExt6.setText("Desde 2,50 €");
            if (cardExt3 != null) cardExt3.setVisibility(View.GONE);
            if (cardExt4 != null) cardExt4.setVisibility(View.GONE);
            if (cardExt5 != null) cardExt5.setVisibility(View.GONE);
            if (cardExt6 != null) cardExt6.setVisibility(View.GONE);
        } else if ("Fútbol".equals(nombreDeporte)) {
            seccionInteriores.setVisibility(View.GONE);
            tvTituloExteriores.setText("INSTALACIONES DE FÚTBOL");
            if (tvPrecioExt1 != null) tvPrecioExt1.setText("Desde 10,00 €");
            if (tvPrecioExt2 != null) tvPrecioExt2.setText("Desde 10,00 €");
            if (tvPrecioExt3 != null) tvPrecioExt3.setText("Desde 10,00 €");
            if (tvPrecioExt4 != null) tvPrecioExt4.setText("Desde 10,00 €");
            if (tvPrecioExt5 != null) tvPrecioExt5.setText("Desde 10,00 €");
            if (tvPrecioExt6 != null) tvPrecioExt6.setText("Desde 10,00 €");

            if ("Palacio de los Deportes".equals(nombreComplejo)) {
                if (tvNombreExt1 != null) tvNombreExt1.setText("Estadio Manuel Utrilla");
                View tvSubtipoExt1 = findViewById(R.id.tv_subtipo_ext_1);
                if (tvSubtipoExt1 != null) tvSubtipoExt1.setVisibility(View.GONE);
                if (cardExt2 != null) cardExt2.setVisibility(View.GONE);
                if (cardExt3 != null) cardExt3.setVisibility(View.GONE);
                if (cardExt4 != null) cardExt4.setVisibility(View.GONE);
                if (cardExt5 != null) cardExt5.setVisibility(View.GONE);
                if (cardExt6 != null) cardExt6.setVisibility(View.GONE);
            } else if ("Polideportivo Ramón y Cajal".equals(nombreComplejo)) {
                seccionExteriores.setVisibility(View.GONE);
                if (tvSinInstalaciones != null) tvSinInstalaciones.setVisibility(View.VISIBLE);
            } else if ("Zona Montequinto".equals(nombreComplejo)) {
                if (tvNombreExt1 != null) tvNombreExt1.setText("Campo Fútbol 7 Pabellón");
                View tvSubtipoExt1 = findViewById(R.id.tv_subtipo_ext_1);
                if (tvSubtipoExt1 != null) tvSubtipoExt1.setVisibility(View.GONE);
                if (cardExt2 != null) cardExt2.setVisibility(View.GONE);
                if (cardExt3 != null) cardExt3.setVisibility(View.GONE);
                if (cardExt4 != null) cardExt4.setVisibility(View.GONE);
                if (cardExt5 != null) cardExt5.setVisibility(View.GONE);
                if (cardExt6 != null) cardExt6.setVisibility(View.GONE);
            }
        } else if ("Fútbol Sala".equals(nombreDeporte)) {
            if (tvTituloInteriores != null) tvTituloInteriores.setText("PISTAS INTERIORES");
            if (tvTituloExteriores != null) tvTituloExteriores.setText("PISTAS EXTERIORES");
            if (tvPrecioInt1 != null) tvPrecioInt1.setText("10,00 €");
            if (tvPrecioInt2 != null) tvPrecioInt2.setText("10,00 €");
            if (tvPrecioInt3 != null) tvPrecioInt3.setText("10,00 €");
            if (tvPrecioExt1 != null) tvPrecioExt1.setText("Desde 4,00 €");
            if (tvPrecioExt2 != null) tvPrecioExt2.setText("Desde 4,00 €");
            if (tvPrecioExt3 != null) tvPrecioExt3.setText("Desde 4,00 €");
            if (tvPrecioExt4 != null) tvPrecioExt4.setText("Desde 4,00 €");
            if (tvPrecioExt5 != null) tvPrecioExt5.setText("Desde 4,00 €");
            if (tvPrecioExt6 != null) tvPrecioExt6.setText("Desde 4,00 €");

            if ("Palacio de los Deportes".equals(nombreComplejo)) {
                if (cardExt3 != null) cardExt3.setVisibility(View.GONE);
                if (cardExt4 != null) cardExt4.setVisibility(View.GONE);
                if (cardExt5 != null) cardExt5.setVisibility(View.GONE);
                if (cardExt6 != null) cardExt6.setVisibility(View.GONE);
            } else if ("Polideportivo Ramón y Cajal".equals(nombreComplejo)) {
                if (cardExt3 != null) cardExt3.setVisibility(View.GONE);
                if (cardExt4 != null) cardExt4.setVisibility(View.GONE);
                if (cardExt5 != null) cardExt5.setVisibility(View.GONE);
                if (cardExt6 != null) cardExt6.setVisibility(View.GONE);
            } else if ("Zona Montequinto".equals(nombreComplejo)) {
                if (tvNombreInt1 != null) tvNombreInt1.setText("Pabellón Montequinto");
                if (tvNombreInt2 != null) tvNombreInt2.setText("Pabellón Entretorres");
                seccionExteriores.setVisibility(View.GONE);
            }
        }

        cargarValoraciones(nombreComplejo, nombreDeporte);

        new Handler(Looper.getMainLooper()).postDelayed(this::actualizarValoracionesEnUI, 1500);

        final String complejoFinal = nombreComplejo;
        final String deporteFinal = nombreDeporte;

        View.OnClickListener listener = v -> {
            int id = v.getId();
            String nombrePista = "";
            String tipoPista = "exterior";

            if (id == R.id.card_int_1) {
                nombrePista = tvNombreInt1.getText().toString();
                tipoPista = "interior";
            } else if (id == R.id.card_int_2) {
                nombrePista = tvNombreInt2.getText().toString();
                tipoPista = "interior";
            } else if (id == R.id.card_int_3) {
                nombrePista = tvNombreInt3.getText().toString();
                tipoPista = "interior";
            } else if (id == R.id.card_ext_1) {
                nombrePista = tvNombreExt1.getText().toString();
            } else if (id == R.id.card_ext_2) {
                nombrePista = tvNombreExt2.getText().toString();
            }

            Intent intent;
            if (esModoValoracion) {
                intent = new Intent(PistasActivity.this, ValorarInstalacionActivity.class);
                intent.putExtra(ValoracionesActivity.TIPO_VALORACION, tipoValoracion);
                intent.putExtra("COMPLEJO_SELECCIONADO", complejoFinal);
                intent.putExtra("DEPORTE_SELECCIONADO", deporteFinal);
                intent.putExtra("PISTA_SELECCIONADA", nombrePista);
                intent.putExtra("TIPO_PISTA", tipoPista);
                startActivityForResult(intent, REQUEST_VALORAR);
            } else {
                intent = new Intent(PistasActivity.this, HorariosActivity.class);
                intent.putExtra("TIPO_PISTA", tipoPista);
                intent.putExtra("DEPORTE_SELECCIONADO", deporteFinal);
                intent.putExtra("COMPLEJO_SELECCIONADO", complejoFinal);
                intent.putExtra("PISTA_SELECCIONADA", nombrePista);
                startActivity(intent);
            }
        };

        cardInt1.setOnClickListener(listener);
        cardInt2.setOnClickListener(listener);
        cardInt3.setOnClickListener(listener);
        cardExt1.setOnClickListener(listener);
        cardExt2.setOnClickListener(listener);
        cardExt3.setOnClickListener(listener);
        cardExt4.setOnClickListener(listener);
        cardExt5.setOnClickListener(listener);
        cardExt6.setOnClickListener(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VALORAR && resultCode == RESULT_OK) {
            String nombreComplejo = getIntent().getStringExtra("COMPLEJO_SELECCIONADO");
            String nombreDeporte = getIntent().getStringExtra("DEPORTE_SELECCIONADO");
            mediasValoraciones.clear();
            tiposInstalaciones.clear();
            cargarValoraciones(nombreComplejo, nombreDeporte);
            new Handler(Looper.getMainLooper()).postDelayed(this::actualizarValoracionesEnUI, 800);
        }
    }

    private void cargarValoraciones(String complejo, String deporte) {
        if (complejo == null || deporte == null) return;

        db.collection("valoraciones_instalaciones")
            .whereEqualTo("complejo", complejo)
            .whereEqualTo("deporte", deporte)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.isEmpty()) return;

                java.util.HashMap<String, Integer> contadorPorInstalacion = new java.util.HashMap<>();
                java.util.HashMap<String, Integer> sumaPorInstalacion = new java.util.HashMap<>();
                java.util.HashMap<String, String> tiposInstalacion = new java.util.HashMap<>();

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String instalacion = doc.getString("instalacion");
                    String tipo = doc.getString("tipo");
                    Long puntuacion = doc.getLong("puntuacion");
                    Log.d("PistasActivity", "Valoracion cargada - instalacion: " + instalacion + ", tipo: " + tipo + ", puntuacion: " + puntuacion);

                    if (instalacion != null && puntuacion != null) {
                        int pts = puntuacion.intValue();
                        sumaPorInstalacion.put(instalacion, sumaPorInstalacion.getOrDefault(instalacion, 0) + pts);
                        contadorPorInstalacion.put(instalacion, contadorPorInstalacion.getOrDefault(instalacion, 0) + 1);
                        if (tipo != null && !tipo.isEmpty()) {
                            tiposInstalacion.put(instalacion, tipo);
                        }
                    }
                }

                Log.d("PistasActivity", "Valoraciones cargadas: " + sumaPorInstalacion.toString() + ", tipos: " + tiposInstalacion.toString());

                for (String inst : sumaPorInstalacion.keySet()) {
                    int s = sumaPorInstalacion.get(inst);
                    int c = contadorPorInstalacion.get(inst);
                    double media = Math.round((double) s / c * 10) / 10.0;
                    mediasValoraciones.put(inst, media);
                    String tipo = tiposInstalacion.get(inst);
                    if (tipo != null && !tipo.isEmpty()) {
                        tiposInstalaciones.put(inst, tipo);
                    }
                }
            })
            .addOnFailureListener(e -> { });
    }

    private void actualizarValoracionesEnUI() {
        MaterialCardView cardInt1 = findViewById(R.id.card_int_1);
        MaterialCardView cardInt2 = findViewById(R.id.card_int_2);
        MaterialCardView cardInt3 = findViewById(R.id.card_int_3);
        MaterialCardView cardExt1 = findViewById(R.id.card_ext_1);
        MaterialCardView cardExt2 = findViewById(R.id.card_ext_2);
        MaterialCardView cardExt3 = findViewById(R.id.card_ext_3);
        MaterialCardView cardExt4 = findViewById(R.id.card_ext_4);
        MaterialCardView cardExt5 = findViewById(R.id.card_ext_5);
        MaterialCardView cardExt6 = findViewById(R.id.card_ext_6);

        String nombrePista1 = tvNombreInt1 != null ? tvNombreInt1.getText().toString() : "";
        String nombrePista2 = tvNombreInt2 != null ? tvNombreInt2.getText().toString() : "";
        Log.d("PistasActivity", "Buscando valoracion para: " + nombrePista1 + " y " + nombrePista2);
        Log.d("PistasActivity", "Medias disponibles: " + mediasValoraciones.toString());

        if (cardInt1 != null && cardInt1.getVisibility() == View.VISIBLE && tvNombreInt1 != null)
            actualizarUnaValoracion(R.id.tv_valoracion_int_1, tvNombreInt1.getText().toString(), "interior");
        if (cardInt2 != null && cardInt2.getVisibility() == View.VISIBLE && tvNombreInt2 != null)
            actualizarUnaValoracion(R.id.tv_valoracion_int_2, tvNombreInt2.getText().toString(), "interior");
        if (cardInt3 != null && cardInt3.getVisibility() == View.VISIBLE && tvNombreInt3 != null)
            actualizarUnaValoracion(R.id.tv_valoracion_int_3, tvNombreInt3.getText().toString(), "interior");
        if (cardExt1 != null && cardExt1.getVisibility() == View.VISIBLE && tvNombreExt1 != null)
            actualizarUnaValoracion(R.id.tv_valoracion_ext_1, tvNombreExt1.getText().toString(), "exterior");
        if (cardExt2 != null && cardExt2.getVisibility() == View.VISIBLE && tvNombreExt2 != null)
            actualizarUnaValoracion(R.id.tv_valoracion_ext_2, tvNombreExt2.getText().toString(), "exterior");

        TextView tvNombreExt3 = findViewById(R.id.tv_nombre_ext_3);
        TextView tvNombreExt4 = findViewById(R.id.tv_nombre_ext_4);
        TextView tvNombreExt5 = findViewById(R.id.tv_nombre_ext_5);
        TextView tvNombreExt6 = findViewById(R.id.tv_nombre_ext_6);

        if (cardExt3 != null && cardExt3.getVisibility() == View.VISIBLE && tvNombreExt3 != null)
            actualizarUnaValoracion(R.id.tv_valoracion_ext_3, tvNombreExt3.getText().toString(), "exterior");
        if (cardExt4 != null && cardExt4.getVisibility() == View.VISIBLE && tvNombreExt4 != null)
            actualizarUnaValoracion(R.id.tv_valoracion_ext_4, tvNombreExt4.getText().toString(), "exterior");
        if (cardExt5 != null && cardExt5.getVisibility() == View.VISIBLE && tvNombreExt5 != null)
            actualizarUnaValoracion(R.id.tv_valoracion_ext_5, tvNombreExt5.getText().toString(), "exterior");
        if (cardExt6 != null && cardExt6.getVisibility() == View.VISIBLE && tvNombreExt6 != null)
            actualizarUnaValoracion(R.id.tv_valoracion_ext_6, tvNombreExt6.getText().toString(), "exterior");
    }

    private void actualizarUnaValoracion(int id, String nombre, String tipoPista) {
        TextView tvValoracion = findViewById(id);
        if (tvValoracion == null) return;

        if (nombre == null || nombre.isEmpty()) {
            tvValoracion.setText("★ 0.0");
            return;
        }

        Double media = buscarMediaPorNombre(nombre, tipoPista);
        if (media != null) {
            tvValoracion.setText("★ " + media);
        } else {
            tvValoracion.setText("★ 0.0");
        }
    }

    private Double buscarMediaPorNombre(String nombre, String tipoPista) {
        if (nombre == null || nombre.isEmpty()) return null;

        String nombreLimpio = nombre.toLowerCase().trim();
        String numeroPista = nombreLimpio.replaceAll("[^0-9]", "");
        String tipoBusqueda = tipoPista != null ? tipoPista.toLowerCase() : "";

        Log.d("PistasActivity", "buscarMediaPorNombre - nombre: " + nombreLimpio + ", tipoPista: " + tipoBusqueda + ", tiposInstalaciones: " + tiposInstalaciones);

        for (String key : mediasValoraciones.keySet()) {
            if (key == null) continue;

            String keyLimpio = key.toLowerCase().trim();
            String keyTipoFirebase = tiposInstalaciones.get(key);

            String keyTipo;
            if (keyTipoFirebase != null && !keyTipoFirebase.isEmpty()) {
                keyTipo = keyTipoFirebase.toLowerCase();
            } else {
                keyTipo = keyLimpio.contains("interior") ? "interior" : (keyLimpio.contains("exterior") ? "exterior" : "");
            }

            Log.d("PistasActivity", "Comparando con key: " + keyLimpio + ", keyTipo: " + keyTipo);

            if (nombreLimpio.contains(keyLimpio) || keyLimpio.contains(nombreLimpio)) {
                if (tipoBusqueda.isEmpty() || tipoBusqueda.equals(keyTipo) || keyTipo.isEmpty()) {
                    Log.d("PistasActivity", "COINCIDENCIA directa con: " + key);
                    return mediasValoraciones.get(key);
                }
            }

            String keyNumero = keyLimpio.replaceAll("[^0-9]", "");
            if (!numeroPista.isEmpty() && !keyNumero.isEmpty() && numeroPista.equals(keyNumero)) {
                if (tipoBusqueda.isEmpty() || keyTipo.isEmpty() || tipoBusqueda.equals(keyTipo)) {
                    Log.d("PistasActivity", "COINCIDENCIA por numero con tipo compatible: " + key);
                    return mediasValoraciones.get(key);
                }
            }
        }
        Log.d("PistasActivity", "SIN COINCIDENCIA para: " + nombre);
        return null;
    }
}
