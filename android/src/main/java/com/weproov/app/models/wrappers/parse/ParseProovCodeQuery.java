package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weproov.app.utils.GsonUtils;

public class ParseProovCodeQuery<T> {

    @Expose
    @SerializedName("weProovID")
    public T query;

    public ParseProovCodeQuery(T identifier) {
        query = identifier;
    }

    @Override
    public String toString() {
        return GsonUtils.GSON.toJson(this);
    }
}
