package com.weproov.app.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.utils.connections.ParseConnection;
import retrofit.client.Response;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

@Table(name = "pictures", id = BaseColumns._ID)
public class PictureItem extends BaseModel implements Parcelable {

	// api endpoint
	private static final String MODULE = "/pictures";

	// endpoints
	private static final String POST_PICTURE = "/";
	private static final IPictureService SERVICE = ParseConnection.ADAPTER.create(IPictureService.class);

	@Column(name = "parent", onDelete = Column.ForeignKeyAction.CASCADE)
	public WeProov parent;

	@Column(name = "path")
	public Uri path;

	@Expose
	@Column(name = "comment")
	public String comment;

	@Expose
	@Column(name = "type")
	public String type = "<TODO>";

	@Column(name = "uploaded")
	public boolean uploaded = false;

	@SuppressWarnings("unused")
	public PictureItem() {
		super();
	}

	protected PictureItem(Parcel in) {
		super();
		this.path = in.readParcelable(Uri.class.getClassLoader());
		this.comment = in.readString();
		this.type = in.readString();
		this.uploaded = in.readByte() == 1;
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
		dest.writeString(type);
		dest.writeByte((byte) (uploaded ? 1 : 0));
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
				"parent=" + parent +
				", path=" + path +
				", comment='" + comment + '\'' +
				", type='" + type + '\'' +
				", uploaded=" + uploaded +
				'}';
	}

	public interface IPictureService {

		@Multipart
		@POST(MODULE + POST_PICTURE)
		Response upload(@Part("picture") PictureItem item, @Part("file") TypedFile file) throws NetworkException;
	}
}
