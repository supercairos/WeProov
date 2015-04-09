package com.weproov.app.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "cars", id = BaseColumns._ID)
public class CarInfo extends BaseModel implements Parcelable {

	@Expose
	@Column(name = "plate")
	public String plate;

	@Column(name = "brand")
	public String brand;

	@Column(name = "model")
	public String model;

	@Column(name = "car_type")
	public String car_type;

	@Column(name = "millage")
	public float millage;

	@Column(name = "millage_type")
	public String millage_type;

	@Column(name = "color")
	public String color;

	@Column(name = "gas_level")
	public int gas_level;

	@Column(name = "vehicle_documentation")
	public Uri vehicle_documentation;


	public CarInfo() {
		super();
	}

	protected CarInfo(Parcel in) {
		super();
		plate = in.readString();
		brand = in.readString();
		model = in.readString();
		car_type = in.readString();
		millage = in.readFloat();
		millage_type = in.readString();
		color = in.readString();
		gas_level = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(plate);
		dest.writeString(brand);
		dest.writeString(model);
		dest.writeString(car_type);
		dest.writeFloat(millage);
		dest.writeString(millage_type);
		dest.writeString(color);
		dest.writeInt(gas_level);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<CarInfo> CREATOR = new Parcelable.Creator<CarInfo>() {
		@Override
		public CarInfo createFromParcel(Parcel in) {
			return new CarInfo(in);
		}

		@Override
		public CarInfo[] newArray(int size) {
			return new CarInfo[size];
		}
	};

	@Override
	public String toString() {
		return "CarInfo{" +
				"plate='" + plate + '\'' +
				", brand='" + brand + '\'' +
				", model='" + model + '\'' +
				", car_type='" + car_type + '\'' +
				", millage=" + millage +
				", millage_type='" + millage_type + '\'' +
				", color='" + color + '\'' +
				", gas_level=" + gas_level +
				'}';
	}
}
