package com.weproov.app.utils.serializers;

import android.net.Uri;
import com.activeandroid.serializer.TypeSerializer;

public class UriSerializer extends TypeSerializer {

	@Override
	public Class<?> getDeserializedType() {
		return Uri.class;
	}

	@Override
	public Class<?> getSerializedType() {
		return Long.class;
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
		if (data == null) {
			return null;
		}

		return Uri.parse((String) data);
	}
}