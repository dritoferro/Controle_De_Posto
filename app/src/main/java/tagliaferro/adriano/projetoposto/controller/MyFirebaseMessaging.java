package tagliaferro.adriano.projetoposto.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tagliaferro.adriano.projetoposto.R;

/**
 * Created by Adriano2 on 27/12/2017.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        buildNotification(notification);

    }

    public void buildNotification(RemoteMessage.Notification notification) {

        String title = notification.getTitle();
        String body = notification.getBody();

        String[] splitMsg = body.split(":");

        String[] splitted2 = splitMsg[0].split(",");
        String[] splitted3 = splitted2[1].split("\t");
        String[] splitted4 = splitted3[5].split("Endereço");
        String text = "";
        try {
            text = splitted2[0].concat(" ").concat(splitted4[0]).concat(" Endereço: ").concat(splitMsg[1]);
        }catch (Exception e){
            String error = e.getMessage();
        }
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(splitMsg[1]));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mapIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        Notification notificacao = builder
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.gas_station)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificacao);

    }
}
