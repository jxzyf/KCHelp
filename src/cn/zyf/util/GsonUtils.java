package cn.zyf.util;

import com.google.gson.Gson;

import java.util.Map;

public class GsonUtils {

    private GsonUtils() {}

    private static final Gson gson = new Gson();

    public static Map json2map(String json) {
        return gson.fromJson(json, Map.class);
    }

    public static String obj2json(Object o) {
        return gson.toJson(o);
    }

}
