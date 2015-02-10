package edu.rosehulman.photomessage;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private static final int TAKE_PHOTO_ACTIVITY_REQUEST = 1;
	private static final int PICK_FROM_GALLERY_REQUEST = 2;
	static final String LOG = "LOG";
	static final String KEY_MESSAGE = "KEY_MESSAGE";
	static final String KEY_IMAGE_FILENAME = "KEY_IMAGE_FILENAME";
	static final String KEY_PHOTO_MESSAGE = "KEY_PHOTO_MESSAGE";
	static final String KEY_SOON_NOTIFICATION_ID = "KEY_SOON_NOTIFICATION_ID";
	static final String KEY_NOTIFICATION = "KEY_NOTIFICATION";
	static final String KEY_SOON_NOTIFICATION = "KEY_SOON_NOTIFICATION";
	private static final int NOTIFICATION_ID = 17;
	private static final int SOON_NOTIFICATION_ID = 22;
	private final int SECONDS_UNTIL_ALARM = 15;

	private static final int THUMBNAIL_SIZE = 96;

	private static PhotoMessage mPhotoMessage = null;
	private boolean mCanSavePhoto = false;
	private Bitmap mBitmap;
	private GestureDetector mGestureDetector;
	private TextView mMessageTextView = null;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.photo_button).setOnClickListener(this);
		findViewById(R.id.gallery_button).setOnClickListener(this);
		mImageView = (ImageView) findViewById(R.id.image_view);

		// Set the initial image to be the launcher icon (feel free to add your
		// own drawable)
		mBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		mImageView.setImageBitmap(mBitmap);
		mCanSavePhoto = true;

		mPhotoMessage = new PhotoMessage();
		mGestureDetector = new GestureDetector(this,
				new MessageGestureListener());
		Log.d(LOG, "onCreate() completed");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	class MessageGestureListener extends
			GestureDetector.SimpleOnGestureListener {

		private boolean moveMessage = false;

		@Override
		public boolean onDown(MotionEvent e) {
			moveMessage = inMessageBounds(e);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (moveMessage) {
				float x = e2.getX();
				float y = e2.getY();
				mPhotoMessage.setLeft(x);
				mPhotoMessage.setTop(y);
				mMessageTextView.setX(x);
				mMessageTextView.setY(y);
			}
			return true;
		}

		private boolean inMessageBounds(MotionEvent e) {
			return true;
			// CONSIDER: Determine if I'm actually in the bounds of the message.
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photo_button:
			takePhoto();
			return;
		case R.id.gallery_button:
			loadFromGallery();
			return;
		}
	}

	private void takePhoto() {
		Log.d(LOG, "takePhoto() started");
		// DONE: Launch an activity using the camera intent

		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri uri = PhotoUtils.getOutputMediaUri(getString(R.string.app_name));
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(cameraIntent, TAKE_PHOTO_ACTIVITY_REQUEST);
		mPhotoMessage.setPhotoPath(uri.getPath());
	}

	private void loadFromGallery() {
		Log.d(LOG, "loadFromGallery() started");
		// DONE: Launch the gallery to pick a photo from it.
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(galleryIntent, PICK_FROM_GALLERY_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		if (requestCode == TAKE_PHOTO_ACTIVITY_REQUEST) {
			Log.d(LOG, "back from taking a photo");
			// DONE: Get and show the bitmap
			mBitmap = BitmapFactory.decodeFile(mPhotoMessage.getPhotoPath());
			mImageView.setImageBitmap(mBitmap);
			mCanSavePhoto = true;
		}

		if (requestCode == MainActivity.PICK_FROM_GALLERY_REQUEST) {
			Log.d(LOG, "Back from the gallery");
			// DONE: Get and show the bitmap
			Uri uri = data.getData();
			Log.d(LOG, "URI from gallery:" + uri);
			String realPath = getRealPathFromUri(uri);
			Log.d(LOG, "Real URI on device:" + realPath);
			mBitmap = BitmapFactory.decodeFile(realPath);
			mImageView.setImageBitmap(mBitmap);
			mPhotoMessage.setPhotoPath(realPath);
			mCanSavePhoto = false;
		}

	}

	// From
	// http://android-er.blogspot.com/2013/08/convert-between-uri-and-file-path-and.html
	private String getRealPathFromUri(Uri contentUri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		CursorLoader cursorLoader = new CursorLoader(this, contentUri,
				projection, null, null, null);
		Cursor cursor = cursorLoader.loadInBackground();
		cursor.moveToFirst();
		int columnIndex = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		return cursor.getString(columnIndex);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_add_message:
			addMessage();
			return true;
		case R.id.action_notify_now:
			notifyNow();
			return true;
		case R.id.action_show_later:
			notifyLater();
			return true;
		case R.id.action_save_photo:
			savePhoto();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addMessage() {
		Log.d(LOG, "addMessage() started");
		DialogFragment df = new AddMessageDialogFragment();
		df.show(getFragmentManager(), "add message");
	}

	public void setMessage(String message) {
		Log.d(LOG, "Got message " + message);
		mPhotoMessage.setMessage(message);
		if (mMessageTextView == null) {
			mMessageTextView = new TextView(this);
			mMessageTextView.setTextSize(32);
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main_relative_layout);
			layout.addView(mMessageTextView);
		}
		mMessageTextView.setText(message);
	}

	private void notifyNow() {
		Log.d(LOG, "notifyNow() started");
		Intent displayIntent = new Intent(this,
				DisplayLabeledPhotoActivity.class);
		displayIntent.putExtra(KEY_PHOTO_MESSAGE, mPhotoMessage);
		Log.d(MainActivity.LOG, "Photo message to send: " + mPhotoMessage);

		// TODO: Replace this with a notification.
		// startActivity(displayIntent);
		Notification notification = getNotification(displayIntent);
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(NOTIFICATION_ID, notification);
	}

	private Notification getNotification(Intent intent) {
		Notification.Builder builder = new Notification.Builder(this);
		builder.setContentTitle(getString(R.string.notification_title));
		builder.setContentText(mPhotoMessage.getMessage());
		builder.setSmallIcon(android.R.drawable.ic_menu_camera);
		Bitmap thumbnail = Bitmap.createScaledBitmap(mBitmap, THUMBNAIL_SIZE,
				THUMBNAIL_SIZE, true);
		builder.setLargeIcon(thumbnail);
		// The last flag is useful if you want to re-use a non-dismissed
		// notification. Setting that flag and issuing a new notification with
		// the same ID as a previous one just replaces its contents.
		int unusedRequestCode = 0; // arbitrary
		PendingIntent pendingIntent = PendingIntent.getActivity(this,
				unusedRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pendingIntent);
		return builder.build();
	}

	private void notifyLater() {
		Log.d(LOG, "notifyLater() started");
		DialogFragment df = new SetAlarmDialogFragment();
		df.show(getFragmentManager(), "set alarm");
	}

	public void setSoonAlarm() {
		// Make an intent and notification from it.
		Intent displayIntent = new Intent(this,
				DisplayLabeledPhotoActivity.class);
		displayIntent.putExtra(KEY_PHOTO_MESSAGE, mPhotoMessage);
		Log.d(MainActivity.LOG, "Photo message to send: " + mPhotoMessage);
		Notification notification = getNotification(displayIntent);

		// Create an intent from this to send to the alarm manager. Add a
		// notification ID for the manager to use.
		Intent notificationIntent = new Intent(this,
				NotificationBroadcastReceiver.class);
		notificationIntent.putExtra(KEY_SOON_NOTIFICATION, notificationIntent);
		notificationIntent.putExtra(KEY_SOON_NOTIFICATION_ID,
				SOON_NOTIFICATION_ID);
		int unusedRequestCode = 0;
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
				unusedRequestCode, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		long futureInMillis = SystemClock.elapsedRealtime()
				+ SECONDS_UNTIL_ALARM * 1000;
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis,
				pendingIntent);
	}

	public void setFixedAlarm(int hour, int minute) {
		// TODO: Implement this (if we have time in class)

	}

	private void savePhoto() {
		if (mCanSavePhoto) {
			SavePhotoTask task = new SavePhotoTask(this);
			task.execute(mBitmap);
			mCanSavePhoto = false;
		} else {
			Log.d(LOG, "Can't save this photo now.");
		}
	}

}
