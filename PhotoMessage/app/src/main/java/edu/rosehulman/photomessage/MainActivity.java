package edu.rosehulman.photomessage;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    static final String KEY_PHOTO_MESSAGE = "KEY_PHOTO_MESSAGE";
    static final String KEY_SOON_NOTIFICATION_ID = "KEY_SOON_NOTIFICATION_ID";
    static final String KEY_NOTIFICATION = "KEY_NOTIFICATION";
    private static final int RC_PHOTO_ACTIVITY = 1;
    private static final int RC_PICK_FROM_GALLERY = 2;
    private static final int THUMBNAIL_SIZE = 96;
    private static final int NOTIFICATION_ID = 1;
    private static final int SOON_NOTIFICATION_ID = 2;
    private static final int SECONDS_UNTIL_ALARM = 15;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addFabListeners();

        // Set initial image
        mImageView = (ImageView) findViewById(R.id.image_view);
        mBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        // For debugging
        mBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/PhotoMessage/IMG_20150810_132053.jpg");
        mBitmap = Bitmap.createScaledBitmap(mBitmap, 512, 512, true);
        mImageView.setImageBitmap(mBitmap);
        mCanSavePhoto = true;


        mPhotoMessage = new PhotoMessage();
        
        mGestureDetector = new GestureDetector(this,
                new MessageGestureListener());
        Log.d(Constants.TAG, "onCreate() completed");

    }

    private void addFabListeners() {
        FloatingActionButton fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromGallery();
            }
        });

        FloatingActionButton fabText = (FloatingActionButton) findViewById(R.id.fab_text);
        fabText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMessage();
            }
        });

        FloatingActionButton fabNotifyNow = (FloatingActionButton) findViewById(R.id.fab_notify_now);
        fabNotifyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyNow();
            }
        });

        FloatingActionButton fabNotifyLater = (FloatingActionButton) findViewById(R.id.fab_notify_later);
        fabNotifyLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyLater();
            }
        });

        FloatingActionButton fabSavePhoto = (FloatingActionButton) findViewById(R.id.fab_save_photo);
        fabSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePhoto();
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void takePhoto() {
        Log.d(Constants.TAG, "takePhoto() started");
        // DONE: Launch an activity using the camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = PhotoUtils.getOutputMediaUri(getString(R.string.app_name));
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        Log.d(Constants.TAG, "Path " + uri.getPath());
        startActivityForResult(cameraIntent, RC_PHOTO_ACTIVITY);
        mPhotoMessage.setPath(uri.getPath());
    }

    private void loadFromGallery() {
        Log.d(Constants.TAG, "loadFromGallery() started");
        // TODO: Launch the gallery to pick a photo from it.
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RC_PICK_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == RC_PHOTO_ACTIVITY) {
            Log.d(Constants.TAG, "back from taking a photo");
            Log.d(Constants.TAG, "Path " + mPhotoMessage.getPath());
            // TODO: Get and show the bitmap
            mBitmap = BitmapFactory.decodeFile(mPhotoMessage.getPath());
            //Use the next 2 lines if your camera res is so high, it crashes your app
            int width = 512, height = 512;
            mBitmap = Bitmap.createScaledBitmap(mBitmap, width, height, true);
            mImageView.setImageBitmap(mBitmap);
            mCanSavePhoto = true;
        }

        if (requestCode == MainActivity.RC_PICK_FROM_GALLERY) {
            Log.d(Constants.TAG, "Back from the gallery");
            // TODO: Get and show the bitmap
            Uri uri = data.getData();
            String realPath = getRealPathFromUri(uri);
            mBitmap = BitmapFactory.decodeFile(realPath);
            int width = 512, height = 512;
            mBitmap = Bitmap.createScaledBitmap(mBitmap, width, height, true);
            mImageView.setImageBitmap(mBitmap);
            mPhotoMessage.setPath(realPath);
            mCanSavePhoto = false;
        }
    }

    private void addMessage() {
        Log.d(Constants.TAG, "addMessage() started");
        DialogFragment df = new AddMessageDialogFragment();
        df.show(getSupportFragmentManager(), "add message");
    }

    public void setMessage(String message, boolean selected) {
        Log.d(Constants.TAG, "Got message " + message);
        mPhotoMessage.setMessage(message);
        mPhotoMessage.setIsWhite(selected);

        if (mMessageTextView == null) {
            mMessageTextView = new TextView(this);
            mMessageTextView.setTextSize(32);
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main_relative_layout);
            layout.addView(mMessageTextView);
        }
        mMessageTextView.setText(message);
        mMessageTextView.setTextColor(selected ? Color.WHITE : Color.BLACK);
        Log.d(Constants.TAG, "" + mMessageTextView.getCurrentTextColor());
    }

    private void notifyNow() {
        Log.d(Constants.TAG, "notifyNow() started");
        if (mPhotoMessage != null && mPhotoMessage.getPath() != null) {
            Intent displayIntent = new Intent(this,
                    DisplayLabeledPhotoActivity.class);
            displayIntent.putExtra(KEY_PHOTO_MESSAGE, mPhotoMessage);
            Log.d(Constants.TAG, "setMessage message to send: " + mPhotoMessage);

            // TODO: Replace this with a notification.
            // startActivity(displayIntent);
            Notification notification = getNotification(displayIntent);
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, notification);
        }
    }

    private Notification getNotification(final Intent intent) {
        int unusedRequestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, unusedRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new Notification.Builder(this)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(mPhotoMessage.getMessage())
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setLargeIcon(Bitmap.createScaledBitmap(mBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, true))
                .setContentIntent(pendingIntent)
                .build();
    }

    private void notifyLater() {
        Log.d(Constants.TAG, "showLater() started");
        DialogFragment df = new SetAlarmDialogFragment();
        df.show(getSupportFragmentManager(), "set alarm");
    }

    public void setSoonAlarm() {
        Intent displayIntent = new Intent(this,
                DisplayLabeledPhotoActivity.class);
        displayIntent.putExtra(KEY_PHOTO_MESSAGE, mPhotoMessage);
        Log.d(Constants.TAG, "setMessage message to send: " + mPhotoMessage);

        // TODO: Replace this with a notification that launches via a timer.
        // startActivity(displayIntent);
        Notification notification = getNotification(displayIntent);
        Intent notifictionIntent = new Intent(this, NotificationBroadcastReceiver.class);
        notifictionIntent.putExtra(KEY_NOTIFICATION, notification);
        notifictionIntent.putExtra(KEY_SOON_NOTIFICATION_ID, SOON_NOTIFICATION_ID);
        int unusedRequestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, unusedRequestCode, notifictionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + SECONDS_UNTIL_ALARM * 1000;
        AlarmManager alarmManger = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManger.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
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
            Log.d(Constants.TAG, "Can't save this photo now.");
        }
    }

    // From
    // http://android-er.blogspot.com/2013/08/convert-between-uri-and-file-path-and.html
    private String getRealPathFromUri(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, contentUri,
                projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        cursor.moveToFirst();
        int columnIndex = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(columnIndex);
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


}
