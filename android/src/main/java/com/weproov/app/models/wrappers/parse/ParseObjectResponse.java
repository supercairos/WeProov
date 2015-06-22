package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParseObjectResponse {

    @Expose
    @SerializedName("createdAt")
    public String date;

    @Expose
    @SerializedName("objectId")
    public String id;
}
