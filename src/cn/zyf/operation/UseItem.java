package cn.zyf.operation;

import cn.zyf.resource.Resource;
import cn.zyf.util.GsonUtils;
import cn.zyf.util.HttpPostUtils;
import cn.zyf.util.HttpUtils;
import cn.zyf.util.ResponseDecoder;
import org.apache.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class UseItem {

    private static final String URL = HttpUtils.server + "/kcsapi/api_get_member/useitem";

    public static void useitem() {
        HttpUtils http = new HttpPostUtils(URL);
        http.addPostParameter(generateBody());
        HttpResponse response = http.execute();

        if (200 != response.getStatusLine().getStatusCode()) {
            System.err.println("[UseItem]unexpected http code: " + response.getStatusLine().getStatusCode());
            throw new RuntimeException();
        }

        Map<String, Object> result = ResponseDecoder.getHeaderAndBody(response);
        String responseBody = String.valueOf(result.get("body"));
        Map svdata = GsonUtils.json2map(responseBody);

        int api_result = ((Double) svdata.get("api_result")).intValue();
        String api_result_msg = (String) svdata.get("api_result_msg");
        if (1 != api_result && (!("成功".equals(api_result_msg) || "".equals(api_result_msg)))) {
            System.err.println(String.format("[UseItem]unexpected response body, api_result: %s, api_result_msg: %s", api_result, api_result_msg));
            throw new RuntimeException();
        }

        saveResource(svdata);
    }

    private static void saveResource(Map svdata) {
    }

    private static Map<String, String> generateBody() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("api_token", String.valueOf(Resource.getProperty("api_token")));
        map.put("api_verno", "1");
        return map;
    }
}
