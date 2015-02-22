package com.weproov.app.models;


import com.activeandroid.Model;

public abstract class BaseModel extends Model {

    public static void deleteAll(Class<? extends Model> clazz) {
        // MyApplication.getAppContext().getContentResolver().delete(OllieProvider.createUri(clazz), null, null);
    }
}
