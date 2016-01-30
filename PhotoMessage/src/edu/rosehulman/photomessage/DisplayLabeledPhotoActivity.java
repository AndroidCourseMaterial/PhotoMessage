package edu.rosehulman.photomessage;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayLabeledPhotoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_labeled_photo);

        PhotoMessage photoMessage = getIntent().getParcelableExtra(MainActivity.KEY_PHOTO_MESSAGE);
        Log.d(MainActivity.LOG, "Photo message received: " + photoMessage);
        
        ImageView imageView = (ImageView) findViewById(R.id.labeled_image_view);

		Bitmap bitmap = null; 
		try {
	        Uri uri = Uri.parse(photoMessage.getUri());
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			Log.e(MainActivity.LOG, "Error: " + e);
		}
        // Bitmap bitmap = BitmapFactory.decodeFile(photoMessage.getUri());
        imageView.setImageBitmap(bitmap);

        TextView messageTextView = (TextView) findViewById(R.id.labeled_text_view);
        messageTextView.setText(photoMessage.getMessage());
        messageTextView.setTextSize(32);
        messageTextView.setX(photoMessage.getLeft());
        messageTextView.setY(photoMessage.getTop());
        
    }
}
