package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weproov.app.utils.GsonUtils;

public class ParseWeProovIdPointerQuery {

	@Expose
	@SerializedName("weProovID")
	ParsePointer weproov;

	public ParseWeProovIdPointerQuery(ParsePointer createdBy) {
		this.weproov = createdBy;
	}

	@Override
	public String toString() {
		return GsonUtils.GSON.toJson(this);
	}
}
