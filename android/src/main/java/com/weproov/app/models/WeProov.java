package com.weproov.app.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.List;

@Table(name = "weproov", id = BaseColumns._ID)
public class WeProov extends BaseModel implements Parcelable {

	@Column(name = "serverId", index = true)
	private String serverId = null;

	@Column(name = "client", onDelete = Column.ForeignKeyAction.CASCADE)
	public ClientInfo client;

	@Column(name = "car", onDelete = Column.ForeignKeyAction.CASCADE)
	public CarInfo car;

	private List<PictureItem> pictures;

	public Uri renterSignature;
	public Uri clientSignature;

	public void addPicture(PictureItem pictureItem) {
		if (pictures == null) {
			pictures = new ArrayList<>();
		}

		pictures.add(pictureItem);
	}

	public List<PictureItem> getPictures() {
		if (pictures == null && getId() > 0) {
			pictures = getMany(PictureItem.class, "parent");
		}

		return pictures;
	}

	public WeProov() {
		super();
	}

	protected WeProov(Parcel in) {
		super();
		client = in.readParcelable(ClientInfo.class.getClassLoader());
		car = in.readParcelable(CarInfo.class.getClassLoader());
		in.readList(pictures, PictureItem.class.getClassLoader());
		serverId = in.readString();
	}

	public void doSave() {
		ActiveAndroid.beginTransaction();
		try {
			client.save();
			car.save();
			save();
			savePictures();
			ActiveAndroid.setTransactionSuccessful();
		} finally {
			ActiveAndroid.endTransaction();
		}
	}

	private void savePictures() {
		for (PictureItem item : pictures) {
			item.parent = this;
			item.save();
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(client, flags);
		dest.writeParcelable(car, flags);
		dest.writeList(pictures);
		dest.writeString(serverId);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<WeProov> CREATOR = new Parcelable.Creator<WeProov>() {
		@Override
		public WeProov createFromParcel(Parcel in) {
			return new WeProov(in);
		}

		@Override
		public WeProov[] newArray(int size) {
			return new WeProov[size];
		}
	};


}
