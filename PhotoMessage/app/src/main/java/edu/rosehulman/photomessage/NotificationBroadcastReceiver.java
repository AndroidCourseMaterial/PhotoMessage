package edu.rosehulman.photomessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Matt Boutell on 2/1/2016.
 * Rose-Hulman Institute of Technology.
 * Covered by MIT license.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(MainActivity.KEY_NOTIFICATION);
        int id = intent.getIntExtra(MainActivity.KEY_SOON_NOTIFICATION_ID, 0);
        manager.notify(id, notification);
    }
}
