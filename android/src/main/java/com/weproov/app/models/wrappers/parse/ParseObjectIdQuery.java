package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weproov.app.utils.GsonUtils;

public class ParseObjectIdQuery {

	@Expose
	@SerializedName("objectId")
	String objectId;

	public ParseObjectIdQuery(String id) {
		this.objectId = id;
	}

	@Override
	public String toString() {
		return GsonUtils.GSON.toJson(this);
	}
}
