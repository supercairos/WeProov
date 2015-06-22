package com.weproov.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.ParseObjectResponse;
import com.weproov.app.models.wrappers.parse.ParseProovCodeQuery;
import com.weproov.app.models.wrappers.parse.ParseQueryWrapper;
import com.weproov.app.utils.GsonUtils;
import com.weproov.app.utils.connections.ParseConnection;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public class ProovCode implements Parcelable {

    private static final String GET_PROOV_CODE = "/classes/weproov";
    private static final String QUERY_STRING = "?where={\"weProovID\":\"MaSocietePro_061815_223114\"}";

    private static final IProovCodeService SERVICE = ParseConnection.ADAPTER.create(IProovCodeService.class);

    @Expose
    @SerializedName("weProovID")
    public String name;

    @Expose
    @SerializedName("objectId")
    public String id;

    @Expose
    @SerializedName("type_process")
    public int type;

    public ProovCode() {
    }

    public ProovCode(String identifier) {
        this.name = identifier;
    }

    public static IProovCodeService getService() {
        return SERVICE;
    }

    public interface IProovCodeService {

        @GET(GET_PROOV_CODE)
        ParseQueryWrapper<ProovCode> get(@Query("where") ParseProovCodeQuery code) throws NetworkException;
    }

    @Override
    public String toString() {
        return GsonUtils.GSON.toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeInt(this.type);
    }

    protected ProovCode(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<ProovCode> CREATOR = new Parcelable.Creator<ProovCode>() {
        public ProovCode createFromParcel(Parcel source) {
            return new ProovCode(source);
        }

        public ProovCode[] newArray(int size) {
            return new ProovCode[size];
        }
    };
}
