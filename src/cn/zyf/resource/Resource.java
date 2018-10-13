package cn.zyf.resource;

import cn.zyf.util.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class Resource {

    private Resource() {}

    public static final Map<Object, Object> global = new HashMap<Object, Object>();

    public static void setProperty(Object key, Object value) {
        global.put(key, value);
    }

    public static Object getProperty(Object key) {
        return global.get(key);
    }

    public static Object getProperty(Object key, Object defaultValue) { return global.getOrDefault(key, defaultValue); }

    static {
        ResourceBundle resource = ResourceBundle.getBundle("kancolle");
        Resource.setProperty("server_ip", resource.getString("server_ip"));
        Resource.setProperty("kancolle_version", resource.getString("kancolle_version"));
        Resource.setProperty("api_member_id", Integer.parseInt(resource.getString("api_member_id")));
        Resource.setProperty("api_token", resource.getString("api_token"));
        Resource.setProperty("deck2_mission", Integer.parseInt(resource.getString("deck2_mission")));
        Resource.setProperty("deck3_mission", Integer.parseInt(resource.getString("deck3_mission")));
        Resource.setProperty("deck4_mission", Integer.parseInt(resource.getString("deck4_mission")));
    }

}
