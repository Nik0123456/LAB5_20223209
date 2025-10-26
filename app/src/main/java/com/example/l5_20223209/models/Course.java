package com.example.l5_20223209.models;

import java.io.Serializable;

public class Course implements Serializable {
    private String id;  // Identificador único
    private String name;
    private String category;  // Teóricos, Laboratorios, Electivos, Otros
    private int frequency;  // Frecuencia en horas
    private String frequencyUnit;  // "horas" o "días"
    private long nextSessionDate;  // Timestamp de la próxima sesión
    
    public Course() {
        // Constructor vacío para Gson
    }

    public Course(String id, String name, String category, int frequency, 
                  String frequencyUnit, long nextSessionDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.frequency = frequency;
        this.frequencyUnit = frequencyUnit;
        this.nextSessionDate = nextSessionDate;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getFrequencyUnit() {
        return frequencyUnit;
    }

    public void setFrequencyUnit(String frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }

    public long getNextSessionDate() {
        return nextSessionDate;
    }

    public void setNextSessionDate(long nextSessionDate) {
        this.nextSessionDate = nextSessionDate;
    }

    // Método helper para obtener el canal de notificación según la categoría
    public String getNotificationChannel() {
        switch (category) {
            case "Teóricos":
                return "CHANNEL_TEORICOS";
            case "Laboratorios":
                return "CHANNEL_LABORATORIOS";
            case "Electivos":
                return "CHANNEL_ELECTIVOS";
            default:
                return "CHANNEL_OTROS";
        }
    }
    
    // Método para obtener la acción sugerida
    public String getSuggestedAction() {
        if (category.equals("Laboratorios")) {
            return "Completar práctica de laboratorio de " + name;
        } else {
            return "Revisar apuntes de " + name;
        }
    }
}
