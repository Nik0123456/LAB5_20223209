package com.example.l5_20223209.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StorageHelper {
    private static final String PROFILE_IMAGE_NAME = "profile_image.jpg";
    
    private final Context context;

    public StorageHelper(Context context) {
        this.context = context;
    }

    /**
     * Guarda una imagen de perfil desde una URI (galería) al internal storage
     */
    public boolean saveProfileImage(Uri imageUri) {
        try {
            // Abrir el stream de la imagen seleccionada
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                return false;
            }

            // Decodificar la imagen
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            // Redimensionar la imagen para ahorrar espacio
            bitmap = resizeBitmap(bitmap, 800, 800);

            // Guardar en internal storage
            FileOutputStream fileOutputStream = context.openFileOutput(PROFILE_IMAGE_NAME, 
                    Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
            bitmap.recycle();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lee la imagen de perfil desde el internal storage
     */
    public Bitmap getProfileImage() {
        try {
            File file = new File(context.getFilesDir(), PROFILE_IMAGE_NAME);
            if (!file.exists()) {
                return null;
            }

            FileInputStream fileInputStream = context.openFileInput(PROFILE_IMAGE_NAME);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica si existe una imagen de perfil guardada
     */
    public boolean hasProfileImage() {
        File file = new File(context.getFilesDir(), PROFILE_IMAGE_NAME);
        return file.exists();
    }

    /**
     * Elimina la imagen de perfil guardada
     */
    public boolean deleteProfileImage() {
        File file = new File(context.getFilesDir(), PROFILE_IMAGE_NAME);
        return file.delete();
    }

    /**
     * Redimensiona un bitmap manteniendo el aspect ratio
     */
    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap;
        }

        float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
