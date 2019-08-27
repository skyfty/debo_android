package com.qcwl.debo.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 工具类
 *
 * @author Administrator
 */
public class JsonUtils {

	private static Gson gson = new Gson();
	/**
	 * 获取json字符串
	 *
	 * 微信
	 * @param list
	 * @return
	 */
	public static String getStringFromList(List<Map<String, Object>> list) {
		return gson.toJson(list);
	}

	public static String getStringFromObject(Object object) {
		return gson.toJson(object);
	}

	public static String getStringFromMap(Map<String, Object> hashMap) {
		return gson.toJson(hashMap);
	}

	public static List<String> getListFromString(String json) {
		return gson.fromJson(json, new TypeToken<List<String>>() {
		}.getType());
	}

	/**
	 * 返回List
	 *
	 * @param jsonStr
	 * @return
	 */
	public static List<Map<String, Object>> getListMFromString(String jsonStr) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Gson gson = new Gson();
			list = gson.fromJson(jsonStr,
					new TypeToken<List<Map<String, Object>>>() {
					}.getType());
		} catch (Exception e) {
		}
		return list;
	}

	/**
	 * 返回Map
	 *
	 * @param jsonStr
	 * @return
	 */
	public static Map<String, Object> getMapFromString(String jsonStr) {
		return gson.fromJson(jsonStr, new TypeToken<Map<String, Object>>() {
		}.getType());
	}


	public static <T> T getTFromString(String json,Class<T> cls){
		return gson.fromJson(json, cls);
	}

	public static <T> List<T>  getListTFromStr(String json,Class<T> clazz){
		List<T> lst = new ArrayList<T>();
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		for(final JsonElement elem : array){
			lst.add(gson.fromJson(elem, clazz));
		}
		return lst;
	}
}
