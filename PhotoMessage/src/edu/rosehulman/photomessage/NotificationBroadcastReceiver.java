package edu.rosehulman.photomessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = intent
				.getParcelableExtra(MainActivity.KEY_SOON_NOTIFICATION);
		int id = intent
				.getParcelableExtra(MainActivity.KEY_SOON_NOTIFICATION_ID);
		manager.notify(id, notification);
	}
}


