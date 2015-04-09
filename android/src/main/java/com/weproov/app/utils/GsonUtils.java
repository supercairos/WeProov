package com.weproov.app.utils;


import android.net.Uri;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.DateFormat;

public final class GsonUtils {

	public static final Gson GSON = new GsonBuilder()
			// .serializeNulls() // FIXME : not sure if this is usefull
			.excludeFieldsWithoutExposeAnnotation()
			.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
			.registerTypeAdapter(Uri.class, new UriAdapter())
			.setDateFormat(DateFormat.LONG)
			.setVersion(1.0)
			.create();

	private static class UriAdapter extends TypeAdapter<Uri> {
		public Uri read(JsonReader reader) throws IOException {
			if (reader.peek() == JsonToken.NULL) {
				reader.nextNull();
				return null;
			}

			return Uri.parse(reader.nextString());
		}

		public void write(JsonWriter writer, Uri value) throws IOException {
			if (value == null) {
				writer.nullValue();
				return;
			}

			writer.value(value.toString());
		}
	}
}
