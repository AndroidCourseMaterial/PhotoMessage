package edu.rosehulman.photomessage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class PhotoUtils {
	static Uri getOutputMediaUri(String folder) {
		File storageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				folder);

		Log.d(MainActivity.LOG, "Media to be stored at " + storageDir.getPath());

		// Create the storage directory if it does not exist
		if (!storageDir.exists()) {
			if (!storageDir.mkdirs()) {
				Log.d(MainActivity.LOG, "Failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		File mediaFile = new File(storageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");
		// Return the URI of the file.
		Log.d(MainActivity.LOG, "The path is " + Uri.fromFile(mediaFile).getPath());		
		
		return Uri.fromFile(mediaFile);
	}

}
