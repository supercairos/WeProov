package com.weproov.app.models;


import com.activeandroid.Model;
import com.activeandroid.content.ContentProvider;
import com.weproov.app.MyApplication;

public abstract class BaseModel extends Model {

    public BaseModel() {
        super();
    }

    public static void deleteAll(Class<? extends Model> clazz) {
        MyApplication.getAppContext().getContentResolver().delete(ContentProvider.createUri(clazz, null), null, null);
    }
}
