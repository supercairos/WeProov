package com.weproov.app.models.wrappers.slack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weproov.app.models.exceptions.WeProovException;

public class SlackNetworkError extends WeProovException {

	@Expose
	@SerializedName("ok")
	public boolean status;

	@Expose
	@SerializedName("error")
	public String error;

}
