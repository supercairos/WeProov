package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weproov.app.utils.GsonUtils;

public class ParseProovCodeQuery {

    @Expose
    @SerializedName("weProovID")
    public String query;

    public ParseProovCodeQuery(String identifier) {
        query = identifier;
    }

    @Override
    public String toString() {
        return GsonUtils.GSON.toJson(this);
    }
}
