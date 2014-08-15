package com.yoavst.quickapps.launcher;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Yoav.
 */
public class LauncherDeserializer implements JsonDeserializer<LauncherActivity.ListItem> {
	@Override
	public LauncherActivity.ListItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		String name = object.get("name").getAsString();
		int drawable = object.get("drawable").getAsInt();
		int id = object.get("id").getAsInt();
		boolean enabled = !object.has("enabled") || object.get("enabled").getAsBoolean();
		return new LauncherActivity.ListItem(name,drawable,id,enabled);
	}
}
