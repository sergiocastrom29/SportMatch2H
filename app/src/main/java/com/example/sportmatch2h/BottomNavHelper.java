package com.example.sportmatch2h;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BottomNavHelper {

    public static final int SECTION_HOME = 1;
    public static final int SECTION_RESERVAS = 2;
    public static final int SECTION_PERFIL = 3;

    private static final int COLOR_ACTIVE = 0xFF1E8449;
    private static final int COLOR_INACTIVE = 0xFF718096;

    public static void setup(Activity activity, View rootView) {
        setup(activity, rootView, getSectionFromActivity(activity));
    }

    private static int getSectionFromActivity(Activity activity) {
        String className = activity.getClass().getSimpleName();
        if ("MenuActivity".equals(className)) return SECTION_HOME;
        if ("MisReservasActivity".equals(className)) return SECTION_RESERVAS;
        if ("PerfilActivity".equals(className)) return SECTION_PERFIL;
        return SECTION_HOME;
    }

    public static void setup(Activity activity, View rootView, int activeSection) {
        View btnHome = rootView.findViewById(R.id.btn_home);
        View btnReservas = rootView.findViewById(R.id.btn_reservas);
        View btnPerfil = rootView.findViewById(R.id.btn_perfil);

        ImageView iconHome = rootView.findViewById(R.id.icon_home);
        ImageView iconReservas = rootView.findViewById(R.id.icon_reservas);
        ImageView iconPerfil = rootView.findViewById(R.id.icon_perfil);

        TextView textHome = (TextView) btnHome.findViewById(android.R.id.text1);
        TextView textReservas = (TextView) btnReservas.findViewById(android.R.id.text1);
        TextView textPerfil = (TextView) btnPerfil.findViewById(android.R.id.text1);

        if (textHome == null) {
            textHome = (TextView) ((android.view.ViewGroup) btnHome).getChildAt(1);
            textReservas = (TextView) ((android.view.ViewGroup) btnReservas).getChildAt(1);
            textPerfil = (TextView) ((android.view.ViewGroup) btnPerfil).getChildAt(1);
        }

        updateItemColor(btnHome, iconHome, textHome, activeSection == SECTION_HOME);
        updateItemColor(btnReservas, iconReservas, textReservas, activeSection == SECTION_RESERVAS);
        updateItemColor(btnPerfil, iconPerfil, textPerfil, activeSection == SECTION_PERFIL);

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                if (activeSection != SECTION_HOME) {
                    Intent intent = new Intent(activity, MenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                }
            });
        }

        if (btnReservas != null) {
            btnReservas.setOnClickListener(v -> {
                if (activeSection != SECTION_RESERVAS) {
                    Intent intent = new Intent(activity, MisReservasActivity.class);
                    activity.startActivity(intent);
                }
            });
        }

        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                if (activeSection != SECTION_PERFIL) {
                    Intent intent = new Intent(activity, PerfilActivity.class);
                    activity.startActivity(intent);
                }
            });
        }
    }

    private static void updateItemColor(View container, ImageView icon, TextView text, boolean isActive) {
        int color = isActive ? COLOR_ACTIVE : COLOR_INACTIVE;
        if (icon != null) {
            icon.setColorFilter(color);
        }
        if (text != null) {
            text.setTextColor(color);
        }
    }
}