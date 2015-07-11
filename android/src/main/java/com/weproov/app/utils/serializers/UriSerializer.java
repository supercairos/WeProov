package com.weproov.app.utils.serializers;

import android.net.Uri;
import com.activeandroid.serializer.TypeSerializer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class UriSerializer extends TypeSerializer {

	@Override
	public Class<?> getDeserializedType() {
		return Uri.class;
	}

	@Override
	public Class<?> getSerializedType() {
		return String.class;
	}

	@Override
	public String serialize(Object data) {
		if (data == null) {
			return null;
		}

		return data.toString();
	}

	@Override
	public Uri deserialize(Object data) {
		if (data == null || !(data instanceof String)) {
			return null;
		}

		try {
			String string = URLDecoder.decode((String) data, "UTF-8");
			return Uri.parse(string);
		} catch (UnsupportedEncodingException e) {
			return Uri.EMPTY;
		}

	}
}
