package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ParseQueryWrapper<T> {

    @Expose
    public List<T> results;
}
