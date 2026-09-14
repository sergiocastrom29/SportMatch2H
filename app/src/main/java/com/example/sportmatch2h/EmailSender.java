package com.example.sportmatch2h;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private static final String FROM_EMAIL = "scastro@itpfp.com";
    private static final String FROM_PASSWORD = "kotq dfwd fpke srdw";
    private static final String SUBJECT = "SportMatch2H";

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public interface EmailCallback {
        void onResult(boolean success, String message);
    }

    public static void sendWelcomeEmail(String toEmail, String username, String password, EmailCallback callback) {
        EXECUTOR.execute(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "true");
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);
                props.put("mail.smtp.connectiontimeout", "10000");
                props.put("mail.smtp.timeout", "10000");
                props.put("mail.smtp.writetimeout", "10000");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
                    }
                });

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(FROM_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(SUBJECT);

                String body = "Bienvenido a SportMatch2H\n\n"
                        + "Gracias por registrarte en nuestra aplicacion.\n\n"
                        + "Tus credenciales de acceso son:\n"
                        + "Usuario: " + username + "\n"
                        + "Contrasena: " + password + "\n\n"
                        + "Ya puedes iniciar sesion y disfrutar de todas las funcionalidades.\n\n"
                        + "El equipo de SportMatch2H";
                message.setText(body, "UTF-8");

                Transport.send(message);

                if (callback != null) callback.onResult(true, "Correo enviado");
            } catch (MessagingException e) {
                if (callback != null) callback.onResult(false, e.getMessage());
            } catch (Exception e) {
                if (callback != null) callback.onResult(false, e.getMessage());
            }
        });
    }
}
