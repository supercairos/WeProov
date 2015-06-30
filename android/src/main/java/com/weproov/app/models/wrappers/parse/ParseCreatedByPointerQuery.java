package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weproov.app.utils.GsonUtils;

public class ParseCreatedByPointerQuery {

	@Expose
	@SerializedName("CreatedBy")
	ParsePointer createdBy;

	public ParseCreatedByPointerQuery(ParsePointer createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public String toString() {
		return GsonUtils.GSON.toJson(this);
	}
}
