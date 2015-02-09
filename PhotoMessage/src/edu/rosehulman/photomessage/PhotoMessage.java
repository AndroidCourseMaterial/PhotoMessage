package edu.rosehulman.photomessage;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public class PhotoMessage implements Parcelable {

	private String mMessage;
	private String mPhotoPath;
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
		mPhotoPath = src.readString();
		mLeft = src.readFloat();
		mTop = src.readFloat();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mMessage);
		dest.writeString(mPhotoPath);
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

	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}

	public String getPhotoPath() {
		return mPhotoPath;
	}

	public void setPhotoPath(String mPhotoPath) {
		this.mPhotoPath = mPhotoPath;
	}

	public float getTop() {
		return mTop;
	}

	public void setTop(float mTop) {
		this.mTop = mTop;
	}

	public float getLeft() {
		return mLeft;
	}

	public void setLeft(float mLeft) {
		this.mLeft = mLeft;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		return String.format(
				"Photomessage: message=[%s], photo=[%s], location=(%.1f,%.1f)",
				mMessage, mPhotoPath, mLeft, mTop);
	}
}
