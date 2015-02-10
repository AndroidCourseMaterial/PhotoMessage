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
		// TODO: Launch an activity using the camera intent

	}

	private void loadFromGallery() {
		Log.d(LOG, "loadFromGallery() started");
		// TODO: Launch the gallery to pick a photo from it.

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		if (requestCode == TAKE_PHOTO_ACTIVITY_REQUEST) {
			Log.d(LOG, "back from taking a photo");
			// TODO: Get and show the bitmap

		}

		if (requestCode == MainActivity.PICK_FROM_GALLERY_REQUEST) {
			Log.d(LOG, "Back from the gallery");
			// TODO: Get and show the bitmap

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
		startActivity(displayIntent);
	}

	private void notifyLater() {
		Log.d(LOG, "showLater() started");
		DialogFragment df = new SetAlarmDialogFragment();
		df.show(getFragmentManager(), "set alarm");
	}

	public void setSoonAlarm() {
		Intent displayIntent = new Intent(this,
				DisplayLabeledPhotoActivity.class);
		displayIntent.putExtra(KEY_PHOTO_MESSAGE, mPhotoMessage);
		Log.d(MainActivity.LOG, "Photo message to send: " + mPhotoMessage);

		// TODO: Replace this with a notification that launches via a timer.
		startActivity(displayIntent);
	}

	public void setFixedAlarm(int hour, int minute) {
		// Pleaceholder if you wanted to try this out (totally optional)
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
