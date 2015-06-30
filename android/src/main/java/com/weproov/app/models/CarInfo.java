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
import com.weproov.app.models.wrappers.parse.ParseObjectIdQuery;
import com.weproov.app.models.wrappers.parse.ParseObjectResponse;
import com.weproov.app.models.wrappers.parse.ParseQueryWrapper;
import com.weproov.app.utils.connections.ParseConnection;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

@Table(name = "cars", id = BaseColumns._ID)
public class CarInfo extends BaseModel implements Parcelable {

	private static final String CAR = "/classes/car";

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
	public String carType;

	@Column(name = "millage")
	public float millage;

	@Expose
	@SerializedName("kilometers")
	public String parseMillage;

	@Expose
	@Column(name = "millage_type")
	public String millageType;

	@Expose
	@Column(name = "color")
	public String color;

	@Column(name = "gas_level")
	public int gasLevel;

	@Expose
	@SerializedName("gas_level")
	// FIXME Remove this hack when the database is better!
	public String parseGasLevel;

	@Column(name = "vehicle_documentation")
	public Uri vehicleDocumentation;

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
				", car_type='" + carType + '\'' +
				", millage=" + millage +
				", millage_type='" + millageType + '\'' +
				", color='" + color + '\'' +
				", gasLevel=" + gasLevel +
				", vehicle_documentation=" + vehicleDocumentation +
				'}';
	}

	public interface ICarService {

		@POST(CAR)
		ParseObjectResponse upload(@Body CarInfo uri) throws NetworkException;

		@GET(CAR)
		ParseQueryWrapper<CarInfo> download(@Query("where") ParseObjectIdQuery idQuery);
	}

	public void prepare() {
		parseGasLevel = String.valueOf(gasLevel);
		parseMillage = String.valueOf(millage);
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
		dest.writeString(this.carType);
		dest.writeFloat(this.millage);
		dest.writeString(this.millageType);
		dest.writeString(this.color);
		dest.writeInt(this.gasLevel);
		dest.writeParcelable(this.vehicleDocumentation, 0);
	}

	protected CarInfo(Parcel in) {
		super(in);
		this.plate = in.readString();
		this.brand = in.readString();
		this.model = in.readString();
		this.carType = in.readString();
		this.millage = in.readFloat();
		this.millageType = in.readString();
		this.color = in.readString();
		this.gasLevel = in.readInt();
		this.vehicleDocumentation = in.readParcelable(Uri.class.getClassLoader());
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
