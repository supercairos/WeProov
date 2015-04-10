package com.weproov.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.List;

@Table(name = "weproov", id = BaseColumns._ID)
public class WeProov extends BaseModel implements Parcelable {

	@Column(name = "renter")
	public RenterInfo renter;

	@Column(name = "car")
	public CarInfo car;

	@Column(name = "uploaded")
	public boolean uploaded = false;

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
		in.readList(pictures, PictureItem.class.getClassLoader());
		uploaded = in.readByte() != 0;
	}

	public void savePictures() {
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
		dest.writeByte((byte) (uploaded ? 1 : 0));
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

	@Override
	public String toString() {
		return "WeProov{" +
				"renter=" + renter +
				", car=" + car +
				", uploaded=" + uploaded +
				", pictures=" + pictures +
				'}';
	}
}
