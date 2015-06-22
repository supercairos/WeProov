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
import com.weproov.app.models.wrappers.parse.ParseFile;
import com.weproov.app.models.wrappers.parse.ParseFileResponse;
import com.weproov.app.models.wrappers.parse.ParseObjectResponse;
import com.weproov.app.models.wrappers.parse.ParsePointer;
import com.weproov.app.utils.connections.ParseConnection;
import com.weproov.app.utils.connections.TypedUri;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

@Table(name = "pictures", id = BaseColumns._ID)
public class PictureItem extends BaseModel implements Parcelable {

	// api endpoint
	private static final String MODULE = "/pictures";

	// endpoints
	private static final String POST_FILE = "/files/{filename}";
	private static final String POST_PICTURE = "/classes/photo";

	private static final IPictureService SERVICE = ParseConnection.ADAPTER.create(IPictureService.class);

	@Column(name = "parent", onDelete = Column.ForeignKeyAction.CASCADE)
	public WeProov parent;

	@Expose
	@SerializedName("weProovID")
	public ParsePointer parsePointer;

	@Column(name = "path")
	public Uri path;

	@Expose
	@SerializedName("file")
	public ParseFile parsePictureFile;

	@Expose
	@SerializedName("commentaire")
	@Column(name = "comment")
	public String comment;

	@Expose
	@SerializedName("type_photo")
	@Column(name = "type")
	public String type = "<TODO>";

	@Expose
	@SerializedName("nom_photo")
	@Column(name = "name")
	public String name = "<TODO>";

	@Expose
	@SerializedName("description_photo")
	@Column(name = "description")
	public String description = "<TODO>";

    @Expose
    @SerializedName("numero_photo")
    public int number = -1;

	@SuppressWarnings("unused")
	public PictureItem() {
		super();
	}

	public static IPictureService getService() {
		return SERVICE;
	}

	public void prepare(ParseFile file) {
        parsePictureFile = file;
		parsePointer = new ParsePointer(parent.proovCode, "weproov");
	}

	@Override
	public String toString() {
		return "PictureItem{" +
				"parent=" + parent +
				", path=" + path +
				", comment='" + comment + '\'' +
				", type='" + type + '\'' +
				'}';
	}



	public interface IPictureService {

		@POST(POST_FILE)
		// User register(@Body User user) throws LoginException;
		ParseFileResponse upload(@Path("filename") String filename, @Body TypedUri uri) throws NetworkException;

		@POST(POST_PICTURE)
		ParseObjectResponse post(@Body PictureItem item) throws NetworkException;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeParcelable(this.parent, 0);
		dest.writeParcelable(this.path, 0);
		dest.writeString(this.comment);
		dest.writeString(this.type);
		dest.writeString(this.name);
	}

	protected PictureItem(Parcel in) {
		super(in);
		this.parent = in.readParcelable(WeProov.class.getClassLoader());
		this.path = in.readParcelable(Uri.class.getClassLoader());
		this.comment = in.readString();
		this.type = in.readString();
		this.name = in.readString();
	}

	public static final Creator<PictureItem> CREATOR = new Creator<PictureItem>() {
		public PictureItem createFromParcel(Parcel source) {
			return new PictureItem(source);
		}

		public PictureItem[] newArray(int size) {
			return new PictureItem[size];
		}
	};
}
