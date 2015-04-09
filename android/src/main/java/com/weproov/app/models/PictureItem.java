package com.weproov.app.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.weproov.app.models.exceptions.LoginException;
import com.weproov.app.utils.connections.Connection;
import retrofit.client.Response;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class PictureItem extends BaseModel implements Parcelable {

	// api endpoint
	private static final String MODULE = "/pictures";

	// endpoints
	private static final String POST_PICTURE = "/";

	private static final IPictureService SERVICE = Connection.ADAPTER.create(IPictureService.class);

	public Uri path;
	public String comment;

	@SuppressWarnings("unused")
	public PictureItem() {
		super();
	}

	protected PictureItem(Parcel in) {
		super();
		path = in.readParcelable(Uri.class.getClassLoader());
		comment = in.readString();
	}

	public static IPictureService getService() {
		return SERVICE;
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

	public interface IPictureService {

		@Multipart
		@POST(MODULE + POST_PICTURE)
		Response upload(@Part("type") TypedString type, @Part("comment") TypedString comment, @Part("file") TypedFile file) throws LoginException;
	}
}
