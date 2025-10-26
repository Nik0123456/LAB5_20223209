package com.example.l5_20223209;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l5_20223209.activities.CoursesListActivity;
import com.example.l5_20223209.activities.SettingsActivity;
import com.example.l5_20223209.databinding.ActivityMainBinding;
import com.example.l5_20223209.models.Course;
import com.example.l5_20223209.utils.NotificationHelper;
import com.example.l5_20223209.utils.PreferencesManager;
import com.example.l5_20223209.utils.StorageHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferencesManager preferencesManager;
    private StorageHelper storageHelper;
    private NotificationHelper notificationHelper;

    // Launchers para permisos y selección de imagen
    private ActivityResultLauncher<String> requestNotificationPermission;
    private ActivityResultLauncher<String> requestImagePermission;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // Inicializar ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar utilidades
        preferencesManager = new PreferencesManager(this);
        storageHelper = new StorageHelper(this);
        notificationHelper = new NotificationHelper(this);

        // Configurar launchers
        setupLaunchers();

        // Solicitar permisos si es necesario
        requestPermissionsIfNeeded();

        // Configurar UI
        setupUI();
        loadUserData();
        loadStatistics();
        
        // Configurar listeners
        setupListeners();
    }

    private void setupLaunchers() {
        // Launcher para permiso de notificaciones (Android 13+)
        requestNotificationPermission = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(this, "Permiso de notificaciones denegado", 
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Launcher para permiso de lectura de imágenes
        requestImagePermission = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(this, "Permiso de galería denegado", 
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Launcher para seleccionar imagen
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            saveProfileImage(imageUri);
                        }
                    }
                }
        );
    }

    private void requestPermissionsIfNeeded() {
        // Solicitar permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void setupUI() {
        // Configurar imagen de perfil
        loadProfileImage();
    }

    private void loadUserData() {
        // Cargar nombre del usuario
        String userName = preferencesManager.getUserName();
        String greeting = "¡Hola, " + userName + "!";
        binding.tvGreeting.setText(greeting);

        // Cargar mensaje motivacional
        String motivationalMessage = preferencesManager.getMotivationalMessage();
        binding.tvMotivationalMessage.setText(motivationalMessage);
    }

    private void loadStatistics() {
        // Cargar estadísticas de cursos
        List<Course> courses = preferencesManager.getCourses();
        binding.tvTotalCourses.setText(String.valueOf(courses.size()));

        // Encontrar próxima sesión
        if (!courses.isEmpty()) {
            Course nextCourse = getNextSessionCourse(courses);
            if (nextCourse != null) {
                String timeUntilNext = getTimeUntilSession(nextCourse.getNextSessionDate());
                binding.tvNextSession.setText(timeUntilNext);
            } else {
                binding.tvNextSession.setText("--");
            }
        } else {
            binding.tvNextSession.setText("--");
        }
    }

    private Course getNextSessionCourse(List<Course> courses) {
        Course nextCourse = null;
        long currentTime = System.currentTimeMillis();
        long minTimeDiff = Long.MAX_VALUE;

        for (Course course : courses) {
            long timeDiff = course.getNextSessionDate() - currentTime;
            if (timeDiff > 0 && timeDiff < minTimeDiff) {
                minTimeDiff = timeDiff;
                nextCourse = course;
            }
        }
        return nextCourse;
    }

    private String getTimeUntilSession(long sessionTime) {
        long currentTime = System.currentTimeMillis();
        long diff = sessionTime - currentTime;

        if (diff < 0) {
            return "Pasada";
        }

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff) % 24;

        if (days > 0) {
            return days + "d " + hours + "h";
        } else if (hours > 0) {
            return hours + "h";
        } else {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            return minutes + "min";
        }
    }

    private void setupListeners() {
        // Click en imagen de perfil para cambiarla
        binding.ivProfileImage.setOnClickListener(v -> requestImagePermissionAndPick());

        // Botón Ver mis cursos
        binding.btnViewCourses.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CoursesListActivity.class);
            startActivity(intent);
        });

        // Botón Configuraciones
        binding.btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void requestImagePermissionAndPick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestImagePermission.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            // Android 12 y anteriores
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestImagePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void saveProfileImage(Uri imageUri) {
        boolean saved = storageHelper.saveProfileImage(imageUri);
        if (saved) {
            loadProfileImage();
            preferencesManager.setHasProfileImage(true);
            Toast.makeText(this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileImage() {
        Bitmap profileImage = storageHelper.getProfileImage();
        if (profileImage != null) {
            binding.ivProfileImage.setImageBitmap(profileImage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos cuando se vuelve a la actividad
        loadUserData();
        loadStatistics();
    }
}
