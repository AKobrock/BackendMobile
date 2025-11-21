package com.usuarios.Demo.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //Correo de bienvenida
    public void enviarCorreoBienvenida(String destinatario, String nombreUsuario) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Bienvenido a Aquí Papá!");
        mensaje.setText(
            "Hola " + nombreUsuario + ",\n\n" +
            "Tu cuenta ha sido creada exitosamente.\n" +
            "Gracias por unirte a Aquí Papá, el mejor lugar para encontrar al papá perfecto para arrendar.\n\n" +
            "¡Nos alegra tenerte con nosotros!\n\n" +
            "Atentamente,\nEl equipo de Aquí Papá."
        );
        mailSender.send(mensaje);
    }

    //Aviso de desactivación
    public void enviarAvisoDesactivacion(String destinatario, String nombreUsuario) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Tu cuenta ha sido desactivada por inactividad");
        mensaje.setText(
            "Hola " + nombreUsuario + ",\n\n" +
            "Tu cuenta ha sido desactivada automáticamente por inactividad durante más de un año.\n" +
            "Para reactivarla, envíanos un mail pidiendo la reactivación de tu cuenta. \n Esto tomará solo unas 24 horas.\n\n" +
            "Atentamente,\nEl equipo de Aquí Papá."
        );
        mailSender.send(mensaje);
    }

    //Confirmación de reserva
    public void enviarConfirmacionReserva(String destinatario, String nombreUsuario, String nombrePapa, String fecha) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("✅ Reserva confirmada: " + nombrePapa);
        mensaje.setText(
            "Hola " + nombreUsuario + ",\n\n" +
            "Tu reserva del papá " + nombrePapa + " ha sido confirmada exitosamente.\n" +
            "Fecha de visita: " + fecha + "\n\n" +
            "Prepárate para una experiencia inolvidable.\n\n" +
            "Atentamente,\nEl equipo de Aquí Papá."
        );
        mailSender.send(mensaje);
    }

    //Recordatorio de visita
    public void enviarRecordatorioVisita(String destinatario, String nombreUsuario, String nombrePapa, String fecha) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Recordatorio: Papá" + nombrePapa + " llega mañana");
        mensaje.setText(
            "Hola " + nombreUsuario + ",\n\n" +
            "Te recordamos que mañana (" + fecha + ") llegará papá " + nombrePapa + " a visitarte.\n" +
            "Ten todo listo para recibirlo.\n\n" +
            "Atentamente,\nEl equipo de Aquí Papá."
        );
        mailSender.send(mensaje);
    }
}

