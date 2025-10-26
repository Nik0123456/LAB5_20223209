package com.example.l5_20223209.workers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.l5_20223209.models.Course;
import com.example.l5_20223209.utils.PreferencesManager;

public class NotificationScheduler {

    /**
     * Programa una notificación para un curso específico
     */
    public static void scheduleCourseNotification(Context context, Course course) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Intent intent = new Intent(context, CourseNotificationReceiver.class);
        intent.putExtra("course_id", course.getId());
        intent.putExtra("course_name", course.getName());
        intent.putExtra("suggested_action", course.getSuggestedAction());
        intent.putExtra("notification_channel", course.getNotificationChannel());
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                course.getId().hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Programar la alarma exacta
        if (alarmManager != null) {
            try {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        course.getNextSessionDate(),
                        pendingIntent
                );
            } catch (SecurityException e) {
                // Fallback si no tiene permiso para alarmas exactas
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        course.getNextSessionDate(),
                        pendingIntent
                );
            }
        }
    }

    /**
     * Cancela la notificación de un curso
     */
    public static void cancelCourseNotification(Context context, String courseId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Intent intent = new Intent(context, CourseNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                courseId.hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE
        );

        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    /**
     * Programa las notificaciones motivacionales recurrentes
     */
    public static void scheduleMotivationalNotifications(Context context) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        int frequencyHours = preferencesManager.getMessageFrequency();
        
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Intent intent = new Intent(context, MotivationalNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                999999, // ID único para notificaciones motivacionales
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Calcular tiempo hasta la próxima notificación
        long intervalMillis = frequencyHours * 60 * 60 * 1000L; // Convertir horas a milisegundos
        long triggerTime = System.currentTimeMillis() + intervalMillis;

        // Programar alarma repetitiva
        if (alarmManager != null) {
            try {
                alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        intervalMillis,
                        pendingIntent
                );
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cancela las notificaciones motivacionales
     */
    public static void cancelMotivationalNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Intent intent = new Intent(context, MotivationalNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                999999,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE
        );

        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    /**
     * Reprograma todas las notificaciones de cursos
     */
    public static void rescheduleAllCourseNotifications(Context context) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        for (Course course : preferencesManager.getCourses()) {
            scheduleCourseNotification(context, course);
        }
    }
}
