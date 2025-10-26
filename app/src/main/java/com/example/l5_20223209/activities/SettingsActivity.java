package com.example.l5_20223209.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.l5_20223209.databinding.ActivitySettingsBinding;
import com.example.l5_20223209.utils.PreferencesManager;
import com.example.l5_20223209.workers.NotificationScheduler;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar
        preferencesManager = new PreferencesManager(this);

        // Cargar datos guardados
        loadSavedData();

        // Configurar listeners
        setupListeners();

        // Manejar botón de retroceso del toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadSavedData() {
        // Cargar nombre del usuario
        String userName = preferencesManager.getUserName();
        binding.etUserName.setText(userName);

        // Cargar mensaje motivacional
        String motivationalMessage = preferencesManager.getMotivationalMessage();
        binding.etMotivationalMessage.setText(motivationalMessage);

        // Cargar frecuencia de mensajes
        int messageFrequency = preferencesManager.getMessageFrequency();
        binding.etMessageFrequency.setText(String.valueOf(messageFrequency));
    }

    private void setupListeners() {
        // Botón guardar configuración
        binding.btnSaveSettings.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        // Validar nombre
        String userName = binding.etUserName.getText().toString().trim();
        if (userName.isEmpty()) {
            binding.tilUserName.setError("Este campo no puede estar vacío");
            return;
        } else {
            binding.tilUserName.setError(null);
        }

        // Validar mensaje motivacional
        String motivationalMessage = binding.etMotivationalMessage.getText().toString().trim();
        if (motivationalMessage.isEmpty()) {
            binding.tilMotivationalMessage.setError("Este campo no puede estar vacío");
            return;
        } else {
            binding.tilMotivationalMessage.setError(null);
        }

        // Validar frecuencia de mensajes
        String frequencyStr = binding.etMessageFrequency.getText().toString().trim();
        if (frequencyStr.isEmpty()) {
            binding.tilMessageFrequency.setError("Este campo no puede estar vacío");
            return;
        }

        int frequency;
        try {
            frequency = Integer.parseInt(frequencyStr);
            if (frequency <= 0 || frequency > 168) { // Max 1 semana (168 horas)
                binding.tilMessageFrequency.setError("Ingresa un valor entre 1 y 168 horas");
                return;
            }
            binding.tilMessageFrequency.setError(null);
        } catch (NumberFormatException e) {
            binding.tilMessageFrequency.setError("Ingresa un número válido");
            return;
        }

        // Guardar datos
        preferencesManager.saveUserName(userName);
        preferencesManager.saveMotivationalMessage(motivationalMessage);
        preferencesManager.saveMessageFrequency(frequency);

        // Reprogramar notificaciones motivacionales
        NotificationScheduler.scheduleMotivationalNotifications(this);

        // Mostrar mensaje de éxito
        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();

        // Cerrar actividad
        finish();
    }
}
