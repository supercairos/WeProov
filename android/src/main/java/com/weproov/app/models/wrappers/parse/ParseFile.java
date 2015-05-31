package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;

public class ParseFile {

	@Expose
	public String name;

	@Expose
	public String url;

	@Expose
	public String __type = "File";

	public ParseFile(String name, String url) {
		this.name = name;
		this.url = url;
	}
}
