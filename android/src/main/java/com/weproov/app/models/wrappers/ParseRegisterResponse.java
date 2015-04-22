package com.weproov.app.models.wrappers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParseRegisterResponse {

	@Expose
	@SerializedName("sessionToken")
	public String token;
}
