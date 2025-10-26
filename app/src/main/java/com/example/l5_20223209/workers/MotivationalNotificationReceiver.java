package com.example.l5_20223209.workers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.l5_20223209.utils.NotificationHelper;
import com.example.l5_20223209.utils.PreferencesManager;

public class MotivationalNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Obtener el mensaje motivacional configurado
        PreferencesManager preferencesManager = new PreferencesManager(context);
        String motivationalMessage = preferencesManager.getMotivationalMessage();

        // Mostrar notificación motivacional
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.showMotivationalNotification(motivationalMessage);
    }
}
