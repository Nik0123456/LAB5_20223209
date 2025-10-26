package com.example.l5_20223209.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.l5_20223209.models.Course;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {
    private static final String PREF_NAME = "StudyManagerPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_MOTIVATIONAL_MESSAGE = "motivational_message";
    private static final String KEY_MESSAGE_FREQUENCY = "message_frequency";
    private static final String KEY_COURSES = "courses";
    private static final String KEY_HAS_PROFILE_IMAGE = "has_profile_image";
    
    private final SharedPreferences preferences;
    private final Gson gson;

    public PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Gestión del nombre del usuario
    public void saveUserName(String name) {
        preferences.edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "Usuario");
    }

    // Gestión del mensaje motivacional
    public void saveMotivationalMessage(String message) {
        preferences.edit().putString(KEY_MOTIVATIONAL_MESSAGE, message).apply();
    }

    public String getMotivationalMessage() {
        return preferences.getString(KEY_MOTIVATIONAL_MESSAGE, 
                "Hoy es un gran día para aprender");
    }

    // Gestión de la frecuencia de mensajes (en horas)
    public void saveMessageFrequency(int hours) {
        preferences.edit().putInt(KEY_MESSAGE_FREQUENCY, hours).apply();
    }

    public int getMessageFrequency() {
        return preferences.getInt(KEY_MESSAGE_FREQUENCY, 24); // Default: cada 24 horas
    }

    // Gestión de cursos
    public void saveCourses(List<Course> courses) {
        String coursesJson = gson.toJson(courses);
        preferences.edit().putString(KEY_COURSES, coursesJson).apply();
    }

    public List<Course> getCourses() {
        String coursesJson = preferences.getString(KEY_COURSES, null);
        if (coursesJson == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Course>>(){}.getType();
        return gson.fromJson(coursesJson, type);
    }

    public void addCourse(Course course) {
        List<Course> courses = getCourses();
        courses.add(course);
        saveCourses(courses);
    }

    public void updateCourse(Course updatedCourse) {
        List<Course> courses = getCourses();
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId().equals(updatedCourse.getId())) {
                courses.set(i, updatedCourse);
                break;
            }
        }
        saveCourses(courses);
    }

    public void deleteCourse(String courseId) {
        List<Course> courses = getCourses();
        courses.removeIf(course -> course.getId().equals(courseId));
        saveCourses(courses);
    }

    // Gestión de imagen de perfil
    public void setHasProfileImage(boolean hasImage) {
        preferences.edit().putBoolean(KEY_HAS_PROFILE_IMAGE, hasImage).apply();
    }

    public boolean hasProfileImage() {
        return preferences.getBoolean(KEY_HAS_PROFILE_IMAGE, false);
    }

    // Limpiar todas las preferencias
    public void clearAll() {
        preferences.edit().clear().apply();
    }
}
