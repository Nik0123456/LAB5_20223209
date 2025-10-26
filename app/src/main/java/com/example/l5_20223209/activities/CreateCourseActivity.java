package com.example.l5_20223209.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.l5_20223209.R;
import com.example.l5_20223209.databinding.ActivityCreateCourseBinding;
import com.example.l5_20223209.models.Course;
import com.example.l5_20223209.utils.PreferencesManager;
import com.example.l5_20223209.workers.NotificationScheduler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class CreateCourseActivity extends AppCompatActivity {

    private ActivityCreateCourseBinding binding;
    private PreferencesManager preferencesManager;
    
    private Calendar selectedDateTime;
    private String selectedCategory = "";
    private String selectedFrequencyUnit = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar
        preferencesManager = new PreferencesManager(this);
        selectedDateTime = Calendar.getInstance();

        // Configurar dropdowns
        setupCategoryDropdown();
        setupFrequencyUnitDropdown();

        // Configurar listeners
        setupListeners();

        // Manejar botón de retroceso del toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupCategoryDropdown() {
        String[] categories = {"Teóricos", "Laboratorios", "Electivos", "Otros"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, 
                android.R.layout.simple_dropdown_item_1line, 
                categories
        );
        binding.actvCategory.setAdapter(adapter);
        binding.actvCategory.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = categories[position];
        });
    }

    private void setupFrequencyUnitDropdown() {
        String[] units = {"horas", "días"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, 
                android.R.layout.simple_dropdown_item_1line, 
                units
        );
        binding.actvFrequencyUnit.setAdapter(adapter);
        binding.actvFrequencyUnit.setOnItemClickListener((parent, view, position, id) -> {
            selectedFrequencyUnit = units[position];
        });
    }

    private void setupListeners() {
        // Click en campo de fecha
        binding.etDate.setOnClickListener(v -> showDatePicker());

        // Click en campo de hora
        binding.etTime.setOnClickListener(v -> showTimePicker());

        // Botón guardar
        binding.btnSaveCourse.setOnClickListener(v -> validateAndSaveCourse());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateField();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // No permitir fechas pasadas
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    selectedDateTime.set(Calendar.SECOND, 0);
                    updateTimeField();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // Formato 12 horas
        );
        
        timePickerDialog.show();
    }

    private void updateDateField() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateString = dateFormat.format(selectedDateTime.getTime());
        binding.etDate.setText(dateString);
    }

    private void updateTimeField() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String timeString = timeFormat.format(selectedDateTime.getTime());
        binding.etTime.setText(timeString);
    }

    private void validateAndSaveCourse() {
        // Validar nombre
        String courseName = binding.etCourseName.getText().toString().trim();
        if (courseName.isEmpty()) {
            binding.tilCourseName.setError("Este campo no puede estar vacío");
            return;
        } else {
            binding.tilCourseName.setError(null);
        }

        // Validar categoría
        if (selectedCategory.isEmpty()) {
            binding.tilCategory.setError("Selecciona una categoría");
            return;
        } else {
            binding.tilCategory.setError(null);
        }

        // Validar frecuencia
        String frequencyValueStr = binding.etFrequencyValue.getText().toString().trim();
        if (frequencyValueStr.isEmpty()) {
            binding.tilFrequencyValue.setError("Ingresa un valor");
            return;
        } else {
            binding.tilFrequencyValue.setError(null);
        }

        int frequencyValue;
        try {
            frequencyValue = Integer.parseInt(frequencyValueStr);
            if (frequencyValue <= 0) {
                binding.tilFrequencyValue.setError("Debe ser mayor que 0");
                return;
            }
        } catch (NumberFormatException e) {
            binding.tilFrequencyValue.setError("Ingresa un número válido");
            return;
        }

        // Validar unidad de frecuencia
        if (selectedFrequencyUnit.isEmpty()) {
            binding.tilFrequencyUnit.setError("Selecciona una unidad");
            return;
        } else {
            binding.tilFrequencyUnit.setError(null);
        }

        // Validar fecha
        if (binding.etDate.getText().toString().isEmpty()) {
            binding.tilDate.setError("Selecciona una fecha");
            return;
        } else {
            binding.tilDate.setError(null);
        }

        // Validar hora
        if (binding.etTime.getText().toString().isEmpty()) {
            binding.tilTime.setError("Selecciona una hora");
            return;
        } else {
            binding.tilTime.setError(null);
        }

        // Crear el curso
        String courseId = UUID.randomUUID().toString();
        Course newCourse = new Course(
                courseId,
                courseName,
                selectedCategory,
                frequencyValue,
                selectedFrequencyUnit,
                selectedDateTime.getTimeInMillis()
        );

        // Guardar el curso
        preferencesManager.addCourse(newCourse);

        // Programar notificación
        NotificationScheduler.scheduleCourseNotification(this, newCourse);

        // Mostrar mensaje
        Toast.makeText(this, "Curso guardado correctamente", Toast.LENGTH_SHORT).show();

        // Cerrar actividad
        finish();
    }
}
