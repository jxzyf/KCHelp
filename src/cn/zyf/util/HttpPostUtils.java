package cn.zyf.util;

import cn.zyf.resource.Resource;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpPostUtils implements HttpUtils {

    private static final Logger logger = LogManager.getLogger(HttpPostUtils.class);

    private String url;
    private HttpPost post;

    public HttpPostUtils(String url) {
        this.url = url;
        post = new HttpPost(url);
        setDefaultHeader();
    }

    public void printHeader() {
        for (Header header : post.getAllHeaders()) {
            System.out.println(header.getName() + " : " + header.getValue());
        }
    }

    @Override
    public void setDefaultHeader() {
        // 构造消息头
        // cookie和referer需要手动指定
        /*UM_distinctid=16637fa36f943c-0c90039504ab82-7b113d-144000-16637fa36fa1cc; CNZZDATA5787084=cnzz_eid%3D1323979881-1538533183-null%26ntime%3D1538673590
        post.setHeader("Cookie", "UM_distinctid=16637fa36f943c-0c90039504ab82-7b113d-144000-16637fa36fa1cc; CNZZDATA5787084=cnzz_eid%3D1323979881-1538533183-null%26ntime%3D1538614188");
        post.setHeader("Referer", "http://203.104.209.39/kcs2/index.php?api_root=/kcsapi&voice_root=/kcs/sound&osapi_root=osapi.dmm.com&version=4.1.1.6&api_token=e34e0e0b50d6b7eeb7c555df439e10aabc64a72f&api_starttime=1538617885344");
         */
        post.setHeader("Accept", "application/json, text/plain, */*");
        post.setHeader("Accept-Encoding", "gzip, deflate");
        post.setHeader("Accept-Language", "en-US,en;q=0.9");
        post.setHeader("Content-type", "application/x-www-form-urlencoded");
        post.setHeader("Host", "203.104.209.39");
        post.setHeader("Origin", "http://203.104.209.39");
        post.setHeader("Proxy-Connection", "keep-alive");
        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");

        String cookie_ntime = (String) Resource.getProperty("cookie_ntime");
        if (null == cookie_ntime) {
            cookie_ntime = String.valueOf(System.currentTimeMillis() / 1000);
            Resource.setProperty("cookie_ntime", cookie_ntime);
        }
        post.setHeader("Cookie", cookiePrefix + cookie_ntime);

        String api_token = (String) Resource.getProperty("api_token");
        String api_starttime = (String) Resource.getProperty("api_starttime");
        if (null == api_starttime) {
            api_starttime = String.valueOf(System.currentTimeMillis());
            Resource.setProperty("api_starttime", api_starttime);
        }
        post.setHeader("Referer", refererPrefix + String.format("&api_token=%s&api_starttime=%s", api_token, api_starttime));
    }

    @Override
    public void addHeader(Map<String, String> map) {
        for (String key : map.keySet()) {
            post.setHeader(key, map.get(key));
        }
    }

    @Override
    public void addPostParameter(Map<String, String> map) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (String key : map.keySet()) {
            list.add(new BasicNameValuePair(key, map.get(key)));
        }
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            post.setEntity(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized HttpResponse execute() {
        logger.info(Thread.currentThread().getName() + ": " + this.url);
        int retry = 5;
        while (retry >= 0) {
            try {
                synchronized (client) {
                    HttpResponse response = client.execute(post);
                    logger.info("end: " + Thread.currentThread().getName() + ": " + this.url);
                    return response;
                }
            } catch (IOException e) {
                logger.error("send post request error, retry: " + retry);
                retry -= 1;
                e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (Exception e1) {}
            }
        }
        throw new RuntimeException();
    }
}
