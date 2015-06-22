package com.weproov.app.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.ParseFileResponse;
import com.weproov.app.models.wrappers.parse.ParseObjectResponse;
import com.weproov.app.models.wrappers.parse.ParsePointer;
import com.weproov.app.utils.connections.ParseConnection;
import com.weproov.app.utils.connections.TypedUri;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

@Table(name = "cars", id = BaseColumns._ID)
public class CarInfo extends BaseModel implements Parcelable {

	private static final String POST_CAR = "/classes/car";

	private static final ICarService SERVICE = ParseConnection.ADAPTER.create(ICarService.class);

	@Expose
	@Column(name = "plate")
	@SerializedName("registration_id")
	public String plate;

	@Expose
	@Column(name = "brand")
	@SerializedName("brand")
	public String brand;

	@Expose
	@Column(name = "model")
	@SerializedName("model")
	public String model;

	@Expose
	@Column(name = "car_type")
	@SerializedName("gas_type")
	public String car_type;

	@Expose
	@Column(name = "millage")
	@SerializedName("kilometers")
	public float millage;

	@Expose
	@Column(name = "millage_type")
	public String millage_type;

	@Expose
	@Column(name = "color")
	public String color;

	@Expose
	@Column(name = "gas_level")
	public int gas_level;

	@Column(name = "vehicle_documentation")
	public Uri vehicle_documentation;

	public static ICarService getService() {
		return SERVICE;
	}

	public CarInfo() {
		super();
	}

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
				", vehicle_documentation=" + vehicle_documentation +
				'}';
	}

	public interface ICarService {

		@POST(POST_CAR)
		ParseObjectResponse upload(@Body CarInfo uri) throws NetworkException;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(this.plate);
		dest.writeString(this.brand);
		dest.writeString(this.model);
		dest.writeString(this.car_type);
		dest.writeFloat(this.millage);
		dest.writeString(this.millage_type);
		dest.writeString(this.color);
		dest.writeInt(this.gas_level);
		dest.writeParcelable(this.vehicle_documentation, 0);
	}

	protected CarInfo(Parcel in) {
		super(in);
		this.plate = in.readString();
		this.brand = in.readString();
		this.model = in.readString();
		this.car_type = in.readString();
		this.millage = in.readFloat();
		this.millage_type = in.readString();
		this.color = in.readString();
		this.gas_level = in.readInt();
		this.vehicle_documentation = in.readParcelable(Uri.class.getClassLoader());
	}

	public static final Creator<CarInfo> CREATOR = new Creator<CarInfo>() {
		public CarInfo createFromParcel(Parcel source) {
			return new CarInfo(source);
		}

		public CarInfo[] newArray(int size) {
			return new CarInfo[size];
		}
	};
}
