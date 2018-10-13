package cn.zyf.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class ResponseDecoder {

    public static Map<String, Object> getHeaderAndBody(HttpResponse response) {
        Header header[] = response.getAllHeaders();
        HttpEntity entity = response.getEntity();
        String body = "";
        try {
            body = gunzipContent(entity.getContent());
        } catch (IOException e) {
            try {
                body = EntityUtils.toString(response.getEntity(), "utf-8");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            body = unicode2cn(body);
        } catch (Exception e) {}

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("header", header);
        map.put("body", body.substring(body.indexOf('{')));
        return map;
    }

    public static String gunzipContent(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPInputStream gunzip = new GZIPInputStream(is);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        gunzip.close();
        return new String(out.toByteArray());
    }

    public static String unicode2cn(final String dataStr) {
        String tempStr = "s" + dataStr;
        StringBuffer sb = new StringBuffer();
        int start = 0;
        int cur = start;
        try {
            while (true) {
                start = tempStr.indexOf("\\u", start + 1);
                if (-1 == start) {
                    sb.append(tempStr.substring(cur));
                    break;
                } else {
                    sb.append(tempStr.substring(cur, start));
                    char letter = (char) Integer.parseInt(tempStr.substring(start + 2, start + 6), 16); // 16进制parse整形字符串。
                    sb.append(new Character(letter).toString());
                    cur = start + 6;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return dataStr;
        }
        sb.deleteCharAt(0);
        return new String(sb);
    }

}
