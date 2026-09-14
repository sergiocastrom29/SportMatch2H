package com.example.sportmatch2h;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.example.sportmatch2h.BottomNavHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HorariosActivity extends AppCompatActivity {

    private static final int COLOR_SELECCIONADO = 0xFF1E8449;
    private static final int COLOR_NORMAL = 0xFFE2E8F0;
    private static final int COLOR_OCUPADO = 0xFFE74C3C;

    private MaterialCardView cardSeleccionado = null;
    private String nombreDeporte;
    private String nombreComplejo;
    private String nombrePista;
    private String tipoPista;

    private String precioCalculado = "";

    private Date fechaSeleccionada;
    private String fechaString;
    private SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat sdfFirestore = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private List<MaterialCardView> todosLosCards = new ArrayList<>();
    private List<String> todasLasHoras = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);
        BottomNavHelper.setup(this, findViewById(android.R.id.content));

        nombreDeporte  = getIntent().getStringExtra("DEPORTE_SELECCIONADO");
        nombreComplejo = getIntent().getStringExtra("COMPLEJO_SELECCIONADO");
        nombrePista    = getIntent().getStringExtra("PISTA_SELECCIONADA");
        tipoPista      = getIntent().getStringExtra("TIPO_PISTA");
        if (tipoPista == null) tipoPista = "exterior";

        TextView tvTitulo = findViewById(R.id.tv_titulo_horario);
        tvTitulo.setText(nombreDeporte + " - " + nombrePista);

        LinearLayout gridManana = findViewById(R.id.grid_manana);
        LinearLayout gridTarde  = findViewById(R.id.grid_tarde);

        boolean esHoraYMedia = "Pádel".equals(nombreDeporte) || "Tenis".equals(nombreDeporte);
        List<String[]> tramosManana = generarTramos(8, 0, 14, 0, esHoraYMedia);
        List<String[]> tramosTarde  = generarTramos(17, 0, 23, 0, esHoraYMedia);

        poblarGrid(gridManana, tramosManana);
        poblarGrid(gridTarde, tramosTarde);

        Button btnConfirmar = findViewById(R.id.btn_confirmar_reserva);
        btnConfirmar.setOnClickListener(v -> {
            if (fechaSeleccionada == null) {
                Toast.makeText(this, "Selecciona una fecha primero", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cardSeleccionado == null) {
                Toast.makeText(this, "Selecciona una franja horaria primero", Toast.LENGTH_SHORT).show();
                return;
            }
            String etiquetaHorario = obtenerEtiquetaHorario(cardSeleccionado);
            mostrarDialogResumen(etiquetaHorario);
        });

        com.google.android.material.textfield.TextInputEditText etFecha = findViewById(R.id.et_fecha_seleccion);
        etFecha.setOnClickListener(v -> mostrarDatePicker());
    }

    private void mostrarDatePicker() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        long minDate = now.getTimeInMillis();

        Calendar calMax = (Calendar) now.clone();
        calMax.add(Calendar.DAY_OF_MONTH, 7);
        long maxDate = calMax.getTimeInMillis();

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setStart(minDate)
                .setEnd(maxDate)
                .setValidator(new DateRangeValidator(minDate, maxDate))
                .build();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona una fecha")
                .setSelection(minDate)
                .setCalendarConstraints(constraints)
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            fechaSeleccionada = new Date(selection);
            fechaString = sdfDisplay.format(fechaSeleccionada);

            com.google.android.material.textfield.TextInputEditText etFecha = findViewById(R.id.et_fecha_seleccion);
            etFecha.setText(fechaString);

            if (cardSeleccionado != null) {
                desmarcarCard(cardSeleccionado);
                cardSeleccionado = null;
                precioCalculado = "";
            }

            consultarDisponibilidad();
        });

        picker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void consultarDisponibilidad() {
        if (fechaSeleccionada == null) return;

        String fechaQuery = sdfFirestore.format(fechaSeleccionada);
        String idUnico = nombreComplejo + "|" + nombrePista;

        Query query = db.collection("reservas")
                .whereEqualTo("id_unico_instalacion", idUnico)
                .whereEqualTo("fecha_reserva", fechaQuery);

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> horasOcupadas = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String hora = doc.getString("hora_reserva");
                        if (hora != null) {
                            horasOcupadas.add(hora);
                        }
                    }
                    actualizarEstadoBotones(horasOcupadas);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al consultar disponibilidad: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void actualizarEstadoBotones(List<String> horasOcupadas) {
        for (int i = 0; i < todasLasHoras.size(); i++) {
            String hora = todasLasHoras.get(i);
            MaterialCardView card = todosLosCards.get(i);

            if (horasOcupadas.contains(hora)) {
                card.setEnabled(false);
                card.setStrokeColor(COLOR_OCUPADO);
                card.setStrokeWidth(dpToPx(2));
                card.setCardBackgroundColor(0xFFFADBD8);

                if (card == cardSeleccionado) {
                    desmarcarCard(card);
                    cardSeleccionado = null;
                    precioCalculado = "";
                }
            } else {
                card.setEnabled(true);
                if (card == cardSeleccionado) {
                    marcarCard(card);
                } else {
                    desmarcarCard(card);
                }
            }
        }
    }

    private String calcularPrecio(String horaInicioStr) {
        boolean esInterior = "interior".equals(tipoPista);
        String[] partes = horaInicioStr.split(":");
        int hora = Integer.parseInt(partes[0]);
        boolean conFocos = !esInterior && hora >= 20;

        switch (nombreDeporte) {
            case "Pádel":
                if (esInterior) return "9,00 €";
                return conFocos ? "8,50 €" : "6,50 €";

            case "Tenis":
                return conFocos ? "4,50 €" : "2,50 €";

            case "Fútbol":
                return conFocos ? "16,50 €" : "10,00 €";

            case "Fútbol Sala":
                if (esInterior) return "10,00 €";
                return conFocos ? "6,00 €" : "4,00 €";

            default:
                return "—";
        }
    }

    private void mostrarDialogResumen(String horario) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_resumen_reserva);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.92),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        ((TextView) dialog.findViewById(R.id.tv_resumen_deporte)).setText(nombreDeporte);
        ((TextView) dialog.findViewById(R.id.tv_resumen_complejo)).setText(nombreComplejo);
        ((TextView) dialog.findViewById(R.id.tv_resumen_pista)).setText(nombrePista);
        ((TextView) dialog.findViewById(R.id.tv_resumen_horario)).setText(horario);
        ((TextView) dialog.findViewById(R.id.tv_resumen_precio)).setText(precioCalculado);

        dialog.findViewById(R.id.btn_ir_pago).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(HorariosActivity.this, PasarelaActivity.class);
            intent.putExtra("DEPORTE_SELECCIONADO", nombreDeporte);
            intent.putExtra("COMPLEJO_SELECCIONADO", nombreComplejo);
            intent.putExtra("PISTA_SELECCIONADA", nombrePista);
            intent.putExtra("HORARIO_SELECCIONADO", horario);
            intent.putExtra("PRECIO_PISTA", precioCalculado);
            intent.putExtra("FECHA_SELECCIONADA", fechaString);
            startActivity(intent);
        });

        dialog.findViewById(R.id.btn_cancelar_dialog).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private List<String[]> generarTramos(int hInicio, int mInicio, int hFin, int mFin, boolean horaYMedia) {
        List<String[]> tramos = new ArrayList<>();
        int totalMinInicio = hInicio * 60 + mInicio;
        int totalMinFin    = hFin * 60 + mFin;
        int paso = horaYMedia ? 90 : 60;

        for (int ini = totalMinInicio; ini + paso <= totalMinFin; ini += paso) {
            int fin = ini + paso;
            tramos.add(new String[] { formatearHora(ini), formatearHora(fin) });
        }
        return tramos;
    }

    private String formatearHora(int totalMinutos) {
        int h = totalMinutos / 60;
        int m = totalMinutos % 60;
        return String.format("%02d:%02d", h, m);
    }

    private void poblarGrid(LinearLayout grid, List<String[]> tramos) {
        for (String[] item : tramos) {
            MaterialCardView card = crearCardTramo(item[0], item[1]);
            grid.addView(card);
            String etiqueta = item[0] + " - " + item[1];
            todasLasHoras.add(etiqueta);
            todosLosCards.add(card);
        }
    }

    private MaterialCardView crearCardTramo(String inicio, String fin) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, dpToPx(12));
        card.setLayoutParams(lp);
        card.setRadius(dpToPx(14));
        card.setCardElevation(dpToPx(4));
        card.setStrokeWidth(dpToPx(1.5f));
        card.setStrokeColor(COLOR_NORMAL);
        card.setCardBackgroundColor(0xFFFFFFFF);
        card.setClickable(true);
        card.setFocusable(true);

        String etiqueta = inicio + " - " + fin;
        card.setTag(inicio);

        LinearLayout interior = new LinearLayout(this);
        interior.setOrientation(LinearLayout.HORIZONTAL);
        interior.setGravity(android.view.Gravity.CENTER_VERTICAL);
        interior.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        View acento = new View(this);
        LinearLayout.LayoutParams acentoLp = new LinearLayout.LayoutParams(dpToPx(4), dpToPx(36));
        acentoLp.setMargins(0, 0, dpToPx(14), 0);
        acento.setLayoutParams(acentoLp);
        acento.setBackgroundColor(COLOR_SELECCIONADO);
        interior.addView(acento);

        LinearLayout colIzq = new LinearLayout(this);
        colIzq.setOrientation(LinearLayout.VERTICAL);
        colIzq.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView tvHora = new TextView(this);
        tvHora.setText(etiqueta);
        tvHora.setTextColor(0xFF1A2340);
        tvHora.setTextSize(16);
        tvHora.setTypeface(null, android.graphics.Typeface.BOLD);
        colIzq.addView(tvHora);

        int horaInt = Integer.parseInt(inicio.split(":")[0]);
        if ("exterior".equals(tipoPista) && horaInt >= 20) {
            TextView tvFocos = new TextView(this);
            tvFocos.setText("Con focos incluidos");
            tvFocos.setTextColor(0xFF4A5568);
            tvFocos.setTextSize(11);
            colIzq.addView(tvFocos);
        }

        interior.addView(colIzq);

        TextView tvPrecio = new TextView(this);
        tvPrecio.setText(calcularPrecio(inicio));
        tvPrecio.setTextColor(COLOR_SELECCIONADO);
        tvPrecio.setTextSize(14);
        tvPrecio.setTypeface(null, android.graphics.Typeface.BOLD);
        tvPrecio.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        interior.addView(tvPrecio);

        card.addView(interior);

        card.setOnClickListener(v -> seleccionarCard(card, inicio));

        return card;
    }

    private void seleccionarCard(MaterialCardView nuevo, String horaInicio) {
        if (cardSeleccionado != null && cardSeleccionado != nuevo) {
            desmarcarCard(cardSeleccionado);
        }

        if (nuevo == cardSeleccionado) {
            desmarcarCard(cardSeleccionado);
            cardSeleccionado = null;
            precioCalculado = "";
        } else {
            marcarCard(nuevo);
            cardSeleccionado = nuevo;
            precioCalculado = calcularPrecio(horaInicio);
        }
    }

    private void marcarCard(MaterialCardView card) {
        card.setStrokeColor(COLOR_SELECCIONADO);
        card.setStrokeWidth(dpToPx(3));
        card.setCardBackgroundColor(0xFFE8F5E9);
    }

    private void desmarcarCard(MaterialCardView card) {
        card.setStrokeColor(COLOR_NORMAL);
        card.setStrokeWidth(dpToPx(1.5f));
        card.setCardBackgroundColor(0xFFFFFFFF);
    }

    private String obtenerEtiquetaHorario(MaterialCardView card) {
        try {
            LinearLayout interior = (LinearLayout) card.getChildAt(0);
            LinearLayout colIzq = (LinearLayout) interior.getChildAt(1);
            return ((TextView) colIzq.getChildAt(0)).getText().toString();
        } catch (Exception e) {
            return (String) card.getTag();
        }
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}