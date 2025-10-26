package com.example.l5_20223209.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.l5_20223209.adapters.CourseAdapter;
import com.example.l5_20223209.databinding.ActivityCoursesListBinding;
import com.example.l5_20223209.models.Course;
import com.example.l5_20223209.utils.PreferencesManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CoursesListActivity extends AppCompatActivity {

    private ActivityCoursesListBinding binding;
    private PreferencesManager preferencesManager;
    private CourseAdapter courseAdapter;
    private List<Course> coursesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoursesListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar
        preferencesManager = new PreferencesManager(this);

        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar cursos
        loadCourses();

        // Configurar FAB
        binding.fabAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(CoursesListActivity.this, CreateCourseActivity.class);
            startActivity(intent);
        });

        // Manejar botón de retroceso del toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCourses.setHasFixedSize(true);
    }

    private void loadCourses() {
        coursesList = preferencesManager.getCourses();

        if (coursesList.isEmpty()) {
            // Mostrar estado vacío
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.recyclerViewCourses.setVisibility(View.GONE);
        } else {
            // Mostrar lista
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.recyclerViewCourses.setVisibility(View.VISIBLE);

            // Configurar adapter
            courseAdapter = new CourseAdapter(coursesList, this::onCourseClick, this::onDeleteClick);
            binding.recyclerViewCourses.setAdapter(courseAdapter);
        }
    }

    private void onCourseClick(Course course) {
        // Aquí podrías abrir una actividad para editar el curso
        // Por ahora solo mostramos un mensaje
        Snackbar.make(binding.getRoot(), "Curso: " + course.getName(), Snackbar.LENGTH_SHORT).show();
    }

    private void onDeleteClick(Course course, int position) {
        // Mostrar diálogo de confirmación
        new AlertDialog.Builder(this)
                .setTitle("¿Eliminar curso?")
                .setMessage("¿Estás seguro de que deseas eliminar este curso? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Eliminar curso
                    preferencesManager.deleteCourse(course.getId());
                    coursesList.remove(position);
                    
                    if (courseAdapter != null) {
                        courseAdapter.notifyItemRemoved(position);
                        courseAdapter.notifyItemRangeChanged(position, coursesList.size());
                    }

                    // Mostrar mensaje
                    Snackbar.make(binding.getRoot(), "Curso eliminado", Snackbar.LENGTH_SHORT).show();

                    // Si no hay más cursos, mostrar estado vacío
                    if (coursesList.isEmpty()) {
                        binding.layoutEmptyState.setVisibility(View.VISIBLE);
                        binding.recyclerViewCourses.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar cursos cuando volvemos a esta actividad
        loadCourses();
    }
}
