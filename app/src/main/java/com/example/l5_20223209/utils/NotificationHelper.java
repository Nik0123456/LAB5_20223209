package com.example.l5_20223209.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.l5_20223209.MainActivity;
import com.example.l5_20223209.R;

public class NotificationHelper {
    
    // IDs de los canales de notificación
    public static final String CHANNEL_TEORICOS = "CHANNEL_TEORICOS";
    public static final String CHANNEL_LABORATORIOS = "CHANNEL_LABORATORIOS";
    public static final String CHANNEL_ELECTIVOS = "CHANNEL_ELECTIVOS";
    public static final String CHANNEL_OTROS = "CHANNEL_OTROS";
    public static final String CHANNEL_MOTIVACIONAL = "CHANNEL_MOTIVACIONAL";
    
    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) 
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannels();
    }

    /**
     * Crea todos los canales de notificación necesarios
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Configuración de audio para los canales
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // Canal para cursos teóricos
            NotificationChannel channelTeoricos = new NotificationChannel(
                    CHANNEL_TEORICOS,
                    "Cursos Teóricos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelTeoricos.setDescription("Notificaciones para cursos teóricos");
            channelTeoricos.enableVibration(true);
            channelTeoricos.setVibrationPattern(new long[]{0, 500, 200, 500});
            channelTeoricos.setSound(soundUri, audioAttributes);
            channelTeoricos.enableLights(true);

            // Canal para laboratorios
            NotificationChannel channelLaboratorios = new NotificationChannel(
                    CHANNEL_LABORATORIOS,
                    "Laboratorios",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelLaboratorios.setDescription("Notificaciones para prácticas de laboratorio");
            channelLaboratorios.enableVibration(true);
            channelLaboratorios.setVibrationPattern(new long[]{0, 300, 100, 300, 100, 300});
            channelLaboratorios.setSound(soundUri, audioAttributes);
            channelLaboratorios.enableLights(true);

            // Canal para electivos
            NotificationChannel channelElectivos = new NotificationChannel(
                    CHANNEL_ELECTIVOS,
                    "Cursos Electivos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelElectivos.setDescription("Notificaciones para cursos electivos");
            channelElectivos.enableVibration(true);
            channelElectivos.setVibrationPattern(new long[]{0, 400, 200, 400});
            channelElectivos.setSound(soundUri, audioAttributes);
            channelElectivos.enableLights(true);

            // Canal para otros cursos
            NotificationChannel channelOtros = new NotificationChannel(
                    CHANNEL_OTROS,
                    "Otros Cursos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelOtros.setDescription("Notificaciones para otros tipos de cursos");
            channelOtros.enableVibration(true);
            channelOtros.setVibrationPattern(new long[]{0, 500});
            channelOtros.setSound(soundUri, audioAttributes);
            channelOtros.enableLights(true);

            // Canal para mensajes motivacionales
            NotificationChannel channelMotivacional = new NotificationChannel(
                    CHANNEL_MOTIVACIONAL,
                    "Mensajes Motivacionales",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channelMotivacional.setDescription("Mensajes motivacionales personalizados");
            channelMotivacional.enableVibration(false);
            channelMotivacional.setSound(soundUri, audioAttributes);

            // Registrar todos los canales
            notificationManager.createNotificationChannel(channelTeoricos);
            notificationManager.createNotificationChannel(channelLaboratorios);
            notificationManager.createNotificationChannel(channelElectivos);
            notificationManager.createNotificationChannel(channelOtros);
            notificationManager.createNotificationChannel(channelMotivacional);
        }
    }

    /**
     * Muestra una notificación para un curso
     */
    public void showCourseNotification(String courseId, String courseName, 
                                      String suggestedAction, String channelId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                courseId.hashCode(), 
                intent, 
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification_course)
                .setContentTitle("📚 " + courseName)
                .setContentText(suggestedAction)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(suggestedAction))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER);

        notificationManager.notify(courseId.hashCode(), builder.build());
    }

    /**
     * Muestra una notificación motivacional
     */
    public void showMotivationalNotification(String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_MOTIVACIONAL)
                .setSmallIcon(R.drawable.ic_notification_motivation)
                .setContentTitle("💪 ¡Mensaje Motivacional!")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_STATUS);

        notificationManager.notify(999999, builder.build());
    }

    /**
     * Cancela una notificación específica
     */
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    /**
     * Cancela todas las notificaciones
     */
    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }
}
