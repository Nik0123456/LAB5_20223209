package com.example.l5_20223209.workers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.l5_20223209.models.Course;
import com.example.l5_20223209.utils.NotificationHelper;
import com.example.l5_20223209.utils.PreferencesManager;

import java.util.Calendar;

public class CourseNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        // Obtener datos del curso
        String courseId = intent.getStringExtra("course_id");
        String courseName = intent.getStringExtra("course_name");
        String suggestedAction = intent.getStringExtra("suggested_action");
        String notificationChannel = intent.getStringExtra("notification_channel");

        // Mostrar notificación
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.showCourseNotification(
                courseId,
                courseName,
                suggestedAction,
                notificationChannel
        );

        // Actualizar la próxima sesión del curso
        updateNextSession(context, courseId);

        // Reprogramar la próxima notificación
        PreferencesManager preferencesManager = new PreferencesManager(context);
        Course course = getCourseById(preferencesManager, courseId);
        if (course != null) {
            NotificationScheduler.scheduleCourseNotification(context, course);
        }
    }

    private void updateNextSession(Context context, String courseId) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        Course course = getCourseById(preferencesManager, courseId);

        if (course != null) {
            // Calcular la próxima sesión basada en la frecuencia
            long currentSessionTime = course.getNextSessionDate();
            long frequencyMillis;

            if ("horas".equals(course.getFrequencyUnit())) {
                frequencyMillis = course.getFrequency() * 60L * 60L * 1000L;
            } else { // días
                frequencyMillis = course.getFrequency() * 24L * 60L * 60L * 1000L;
            }

            long nextSessionTime = currentSessionTime + frequencyMillis;
            course.setNextSessionDate(nextSessionTime);

            // Actualizar en SharedPreferences
            preferencesManager.updateCourse(course);
        }
    }

    private Course getCourseById(PreferencesManager preferencesManager, String courseId) {
        for (Course course : preferencesManager.getCourses()) {
            if (course.getId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }
}
