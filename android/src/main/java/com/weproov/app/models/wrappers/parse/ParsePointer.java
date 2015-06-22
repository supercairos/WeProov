package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;

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
}
