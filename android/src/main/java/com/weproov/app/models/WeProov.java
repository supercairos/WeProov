package com.weproov.app.models;

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

	@Column(name = "renter")
	public RenterInfo renter;

	@Column(name = "car")
	public CarInfo car;

	private List<PictureItem> pictures = new ArrayList<>();

	public void addPicture(PictureItem pictureItem) {
		pictures.add(pictureItem);
	}

	public List<PictureItem> getPictures() {
		return getMany(PictureItem.class, "parent");
	}

	public WeProov() {
		super();
	}

	protected WeProov(Parcel in) {
		super();
		renter = in.readParcelable(RenterInfo.class.getClassLoader());
		car = in.readParcelable(CarInfo.class.getClassLoader());
		in.readList(pictures, List.class.getClassLoader());
		serverId = in.readString();
	}

	public void doSave() {
		ActiveAndroid.beginTransaction();
		try {
			renter.save();
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
		dest.writeParcelable(renter, flags);
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
