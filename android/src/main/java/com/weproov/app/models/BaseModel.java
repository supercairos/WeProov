package com.weproov.app.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.content.ContentProvider;
import com.weproov.app.MyApplication;

public abstract class BaseModel extends Model implements Parcelable {

    @Column(name = "server_id")
    private String mServerId;

    public String getServerId() {
        return mServerId;
    }

    public void setServerId(String serverId) {
        this.mServerId = serverId;
    }

    public BaseModel() {
        super();
    }

    public static void deleteAll(Class<? extends Model> clazz) {
        MyApplication.getAppContext().getContentResolver().delete(ContentProvider.createUri(clazz, null), null, null);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mServerId);
    }

    protected BaseModel(Parcel in) {
        this.mServerId = in.readString();
    }
}
