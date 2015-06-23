package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParseRegisterResponse {

	@Expose
	@SerializedName("sessionToken")
	public String token;

	@Expose
	@SerializedName("objectId")
	public String id;
}
