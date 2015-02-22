package com.weproov.app.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

public final class GsonUtils {

    public static final Gson GSON = new GsonBuilder()
            .serializeNulls() // FIXME : not sure if this is usefull
            .excludeFieldsWithoutExposeAnnotation()
            .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
            .create();
}
