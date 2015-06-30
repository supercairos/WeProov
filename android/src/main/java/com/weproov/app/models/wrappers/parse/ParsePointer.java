package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;
import com.weproov.app.utils.GsonUtils;

public class ParsePointer {

    @Expose
    public String className;

    @Expose
    public String objectId;

    @Expose
    public String __type = "Pointer";

    public ParsePointer(String objectId, String className) {
        this.objectId = objectId;
        this.className = className;
    }

    @Override
    public String toString() {
        return GsonUtils.GSON.toJson(this);
    }
}
