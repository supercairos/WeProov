package com.weproov.app.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class PictureItem extends BaseModel implements Parcelable {

	public Uri path;
	public String comment;

	public PictureItem() {
		super();
	}

	protected PictureItem(Parcel in) {
		super();
		path = in.readParcelable(Uri.class.getClassLoader());
		comment = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(path, flags);
		dest.writeString(comment);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<PictureItem> CREATOR = new Parcelable.Creator<PictureItem>() {
		@Override
		public PictureItem createFromParcel(Parcel in) {
			return new PictureItem(in);
		}

		@Override
		public PictureItem[] newArray(int size) {
			return new PictureItem[size];
		}
	};

	@Override
	public String toString() {
		return "PictureItem{" +
				"path=" + path +
				", comment='" + comment + '\'' +
				'}';
	}
}
