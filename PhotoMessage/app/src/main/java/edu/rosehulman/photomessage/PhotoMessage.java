package edu.rosehulman.photomessage;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public class PhotoMessage implements Parcelable {

	private String message;
	private String path;
	private float left;
	private float top;

	protected PhotoMessage(Parcel in) {
		message = in.readString();
		path = in.readString();
		left = in.readFloat();
		top = in.readFloat();
	}

	public static final Creator<PhotoMessage> CREATOR = new Creator<PhotoMessage>() {
		@Override
		public PhotoMessage createFromParcel(Parcel in) {
			return new PhotoMessage(in);
		}

		@Override
		public PhotoMessage[] newArray(int size) {
			return new PhotoMessage[size];
		}
	};

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public float getLeft() {
		return left;
	}

	public void setLeft(float left) {
		this.left = left;
	}

	public float getTop() {
		return top;
	}

	public void setTop(float top) {
		this.top = top;
	}

	public PhotoMessage() {
		// empty
	}


	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		return String.format(
				"Photomessage: message=[%s], photo=[%s], location=(%.1f,%.1f)",
				message, path, left, top);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(message);
		dest.writeString(path);
		dest.writeFloat(left);
		dest.writeFloat(top);
	}
}
