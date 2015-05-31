package com.weproov.app.models.wrappers.slack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SlackMessage {


	public SlackMessage(String username, String icon, String title, String message) {
		this.username = username;
		this.icon = icon;
		attachments.add(new SlackAttachment(title, message));
	}

	@Expose
	public String username;

	@Expose
	@SerializedName("icon_url")
	public String icon;

	@Expose
	public List<SlackAttachment> attachments = new ArrayList<>();

	private static class SlackAttachment {

		public SlackAttachment(String title, String message) {
			this.fallback = title + ": " + message;
			fields.add(new SlackField(title, message));
		}

		@Expose
		public String fallback;

		@Expose
		private String pretext = "Reported something new :";

		@Expose
		public String color = "good";

		@Expose
		public List<SlackField> fields = new ArrayList<>();
	}

	private static class SlackField {

		@Expose
		public String title;

		@Expose
		public String value;

		@Expose
		@SerializedName("short")
		public boolean isShort = false;

		public SlackField(String title, String message) {
			this.title = title;
			this.value = message;
		}
	}

}
