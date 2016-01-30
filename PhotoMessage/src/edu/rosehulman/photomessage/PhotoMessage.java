package edu.rosehulman.photomessage;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public class PhotoMessage implements Parcelable {

	private String mMessage;
	private String mPath;
	private float mLeft;
	private float mTop;

	@Override
	public int describeContents() {
		return 0;
	}

	public PhotoMessage() {
		// empty
	}

	public PhotoMessage(Parcel src) {

		mMessage = src.readString();
		mPath = src.readString();
		mLeft = src.readFloat();
		mTop = src.readFloat();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mMessage);
		dest.writeString(mPath);
		dest.writeFloat(mLeft);
		dest.writeFloat(mTop);
	}

	public static final Parcelable.Creator<PhotoMessage> CREATOR = new Parcelable.Creator<PhotoMessage>() {
		public PhotoMessage createFromParcel(Parcel in) {
			return new PhotoMessage(in);
		}

		public PhotoMessage[] newArray(int size) {
			return new PhotoMessage[size];
		}
	};

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String message) {
		this.mMessage = message;
	}

	public String getUri() {
		return mPath;
	}

	public void setPath(String uri) {
		this.mPath = uri;
	}

	public float getTop() {
		return mTop;
	}

	public void setTop(float top) {
		this.mTop = top;
	}

	public float getLeft() {
		return mLeft;
	}

	public void setLeft(float left) {
		this.mLeft = left;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		return String.format(
				"Photomessage: photo=[%s], message=[%s], location=(%.1f,%.1f)",
				mPath, mMessage, mLeft, mTop);
	}
}
