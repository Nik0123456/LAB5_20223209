package com.example.l5_20223209.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l5_20223209.databinding.ItemCourseBinding;
import com.example.l5_20223209.models.Course;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private final List<Course> courses;
    private final OnCourseClickListener onCourseClickListener;
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Course course, int position);
    }

    public CourseAdapter(List<Course> courses, 
                        OnCourseClickListener onCourseClickListener,
                        OnDeleteClickListener onDeleteClickListener) {
        this.courses = courses;
        this.onCourseClickListener = onCourseClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCourseBinding binding = ItemCourseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course, position);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private final ItemCourseBinding binding;

        public CourseViewHolder(ItemCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Course course, int position) {
            // Nombre del curso
            binding.tvCourseName.setText(course.getName());

            // Categoría
            binding.chipCategory.setText(course.getCategory());

            // Icono según categoría
            String icon = getCategoryIcon(course.getCategory());
            binding.tvCategoryIcon.setText(icon);

            // Frecuencia
            String frequency = "Cada " + course.getFrequency() + " " + course.getFrequencyUnit();
            binding.tvFrequency.setText(frequency);

            // Próxima sesión
            String nextSessionText = formatNextSession(course.getNextSessionDate());
            binding.tvNextSession.setText(nextSessionText);

            // Click en el card
            binding.cardCourse.setOnClickListener(v -> {
                if (onCourseClickListener != null) {
                    onCourseClickListener.onCourseClick(course);
                }
            });

            // Click en botón eliminar
            binding.btnDeleteCourse.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(course, position);
                }
            });
        }

        private String getCategoryIcon(String category) {
            switch (category) {
                case "Teóricos":
                    return "📖";
                case "Laboratorios":
                    return "🔬";
                case "Electivos":
                    return "⭐";
                case "Otros":
                    return "📚";
                default:
                    return "📝";
            }
        }

        private String formatNextSession(long sessionTime) {
            Date sessionDate = new Date(sessionTime);
            Calendar sessionCalendar = Calendar.getInstance();
            sessionCalendar.setTime(sessionDate);

            Calendar today = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_YEAR, 1);

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String time = timeFormat.format(sessionDate);

            // Verificar si es hoy
            if (isSameDay(sessionCalendar, today)) {
                return "Hoy a las " + time;
            }
            // Verificar si es mañana
            else if (isSameDay(sessionCalendar, tomorrow)) {
                return "Mañana a las " + time;
            }
            // Otros días
            else {
                long diff = sessionTime - System.currentTimeMillis();
                long days = TimeUnit.MILLISECONDS.toDays(diff);
                
                if (days > 0 && days <= 7) {
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
                    String dayName = dayFormat.format(sessionDate);
                    return dayName + " a las " + time;
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String date = dateFormat.format(sessionDate);
                    return date + " a las " + time;
                }
            }
        }

        private boolean isSameDay(Calendar cal1, Calendar cal2) {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                   cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        }
    }
}
