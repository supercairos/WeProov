package com.weproov.app.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.ParseObjectResponse;
import com.weproov.app.models.wrappers.parse.ParsePointer;
import com.weproov.app.utils.connections.ParseConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.POST;

@Table(name = "weproov", id = BaseColumns._ID)
public class WeProov extends BaseModel implements Parcelable {

    private static final String POST_WEPROOV = "/classes/b2c";
    private static final IWeProovService SERVICE = ParseConnection.ADAPTER.create(IWeProovService.class);

    @Column(name = "date", index = true)
    private long date = System.currentTimeMillis();

    @Expose
    @SerializedName("person_2")
    @Column(name = "client", onDelete = Column.ForeignKeyAction.CASCADE)
    public ClientInfo client;

    @Expose
    @SerializedName("email_person_2")
    public String client_email;

    @Column(name = "car", onDelete = Column.ForeignKeyAction.CASCADE)
    public CarInfo car;

    @Expose
    @SerializedName("carID")
    public ParsePointer parseCarPointer;

    @Column(name = "proov_code")
    public String proovCode;

    @Expose
    @SerializedName("weProovID")
    public ParsePointer parseProovCodePointer;

    private List<PictureItem> pictures;

    public Uri renterSignature;
    public Uri clientSignature;

    @Column(name = "checkout", index = true)
    public boolean isCheckout = false;

    public static IWeProovService getService() {
        return SERVICE;
    }

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

    public long getDate() {
        return date;
    }

    public WeProov() {
        super();
    }

    protected WeProov(Parcel in) {
        super();
        client = in.readParcelable(ClientInfo.class.getClassLoader());
        car = in.readParcelable(CarInfo.class.getClassLoader());
        in.readList(pictures, PictureItem.class.getClassLoader());
    }

    public void prepare() {
        if(client != null) {
            client_email = client.email;
        }

        parseCarPointer = new ParsePointer(car.getServerId(), "car");
        parseProovCodePointer = new ParsePointer(proovCode, "weproov");
    }

    public void doSave() {
        ActiveAndroid.beginTransaction();
        try {
            date = System.currentTimeMillis();
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

    public void setProovCodeId(String id) {
        this.proovCode = id;
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
                " client=" + client +
                ", car=" + car +
                ", pictures=" + pictures +
                ", renterSignature=" + renterSignature +
                ", clientSignature=" + clientSignature +
                '}';
    }

    public interface IWeProovService {

        @POST(POST_WEPROOV)
        ParseObjectResponse upload(@Body WeProov uri) throws NetworkException;
    }
}
