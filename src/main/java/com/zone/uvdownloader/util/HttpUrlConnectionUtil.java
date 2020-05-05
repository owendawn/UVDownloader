package com.zone.uvdownloader.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 2018/11/26 13:54
 * httpUrlConnection 工具类
 *
 * HttpUrlConnectionUtil.HttpComunication httpComunication = HttpUrlConnectionUtil.sendHttpByGet("http://172.23.31.21/op/jmgl/jmglnew!getJieMianList.action"));
 * System.out.println(httpComunication);
 * httpComunication = HttpUrlConnectionUtil.sendHttpByPost("http://172.23.31.21/op/jmgl/jmglnew!getJieMianList.action");
 * System.out.println(httpComunication);
 *
 * @author owen pan
 */
public class HttpUrlConnectionUtil {
    private static Logger log = LoggerFactory.getLogger(HttpUrlConnectionUtil.class);
    private static ObjectMapper objectMapper=new ObjectMapper();

    private static SSLSocketFactory getSSLSocketFactory(){
        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager[] tm = {new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        } };
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static HttpComunication sendHttpByGet(String url) {
        return sendHttpByGet(url, new HashMap<>(0));
    }

    public static HttpComunication sendHttpByGet(String url, Map<String, String> params) {
        HttpsURLConnection conn = null;
        HttpComunication httpComunication = new HttpComunication();
        httpComunication.setType(HttpComunication.HttpType.GET);
        try {
            url = url + "?" + map2FormData(params).replace("\\?+", "?");
            conn= (HttpsURLConnection) new URL(null, url, new sun.net.www.protocol.https.Handler()).openConnection();
            if(url.startsWith("https:")){
                conn.setSSLSocketFactory(getSSLSocketFactory());
            }
            //HttpURLConnection默认就是用GET发送请求，所以下面的setRequestMethod可以省略
            conn.setRequestMethod("GET");
            //HttpURLConnection默认也支持从服务端读取结果流，所以下面的setDoInput也可以省略
            conn.setDoInput(true);
            // Post请求必须设置允许输出 默认false
            conn.setDoOutput(false);
            //禁用网络缓存
            conn.setUseCaches(false);
            // 设置连接主机超时时间
            conn.setConnectTimeout(15 * 1000);
            //设置从主机读取数据超时
            conn.setReadTimeout(25 * 1000);
            //设置请求中的媒体类型信息。
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            //设置客户端与服务连接类型
            conn.addRequestProperty("Connection", "Keep-Alive");
            //获取请求头
            String requestHeader = getReqeustHeader(conn);
            httpComunication.setRequestHeader(requestHeader);
            //在对各种参数配置完成后，通过调用connect方法建立TCP连接，但是并未真正获取数据
            //conn.connect()方法不必显式调用，当调用conn.getInputStream()方法时内部也会自动调用connect方法
            conn.connect();

            handleResponse(conn,httpComunication);
            return httpComunication;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
    public static HttpComunication sendHttpByJsonPost(String url) {
        return sendHttpByJsonPost(url,new HashMap<>(0));
    }
    public static HttpComunication sendHttpByJsonPost(String url, Map<String, String> params) {
        HttpsURLConnection conn = null;
        HttpComunication httpComunication = new HttpComunication();
        httpComunication.setType(HttpComunication.HttpType.POST);

        try {
            conn = (HttpsURLConnection) new URL(null, url, new sun.net.www.protocol.https.Handler()).openConnection();
            if(url.startsWith("https:")){
                conn.setSSLSocketFactory(getSSLSocketFactory());
            }
            //HttpURLConnection默认就是用GET发送请求，所以下面的setRequestMethod可以省略
            conn.setRequestMethod("POST");
            //HttpURLConnection默认也支持从服务端读取结果流，所以下面的setDoInput也可以省略
            conn.setDoInput(true);
            // Post请求必须设置允许输出 默认false
            conn.setDoOutput(true);
            //禁用网络缓存
            conn.setUseCaches(false);
            // 设置连接主机超时时间
            conn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            conn.setReadTimeout(25 * 1000);
            //设置请求中的媒体类型信息。
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            //获取请求头
            httpComunication.setRequestHeader(getReqeustHeader(conn));
            //在对各种参数配置完成后，通过调用connect方法建立TCP连接，但是并未真正获取数据
            //获取conn的输出流
            OutputStream os = conn.getOutputStream();
            //将请求体写入到conn的输出流中
            String json = objectMapper.writeValueAsString(params);
            os.write(json.getBytes());
            //记得调用输出流的flush方法
            os.flush();
            //关闭输出流
            os.close();

            handleResponse(conn,httpComunication);

            return httpComunication;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public static HttpComunication sendHttpByFormPost(String url) {
        return sendHttpByFormPost(url,new HashMap<>(0));
    }

    public static HttpComunication sendHttpByFormPost(String url, Map<String, String> params) {
        HttpsURLConnection conn = null;
        HttpComunication httpComunication = new HttpComunication();
        httpComunication.setType(HttpComunication.HttpType.POST);

        try {
            // 请求的参数转换为byte数组
            byte[] postData = map2FormData(params).getBytes();

            conn = (HttpsURLConnection) new URL(null, url, new sun.net.www.protocol.https.Handler()).openConnection();
            if(url.startsWith("https:")){
                conn.setSSLSocketFactory(getSSLSocketFactory());
            }
            //HttpURLConnection默认就是用GET发送请求，所以下面的setRequestMethod可以省略
            conn.setRequestMethod("POST");
            //HttpURLConnection默认也支持从服务端读取结果流，所以下面的setDoInput也可以省略
            conn.setDoInput(true);
            // Post请求必须设置允许输出 默认false
            conn.setDoOutput(true);
            //禁用网络缓存
            conn.setUseCaches(false);
            // 设置连接主机超时时间
            conn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            conn.setReadTimeout(25 * 1000);
            //设置请求中的媒体类型信息。
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            //获取请求头
            String requestHeader = getReqeustHeader(conn);
            httpComunication.setRequestHeader(requestHeader);
            //在对各种参数配置完成后，通过调用connect方法建立TCP连接，但是并未真正获取数据
            //获取conn的输出流
            OutputStream os = conn.getOutputStream();
            //将请求体写入到conn的输出流中
            os.write(postData);
            //记得调用输出流的flush方法
            os.flush();
            //关闭输出流
            os.close();

            handleResponse(conn,httpComunication);

            return httpComunication;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    //map to form data
    private static String map2FormData(Map<String,String> params) throws UnsupportedEncodingException {
        StringBuilder tempParams = new StringBuilder();
        int pos = 0;
        for (String key : params.keySet()) {
            if (pos > 0) {
                tempParams.append("&");
            }
            tempParams.append(String.format("%s=%s", key, URLEncoder.encode(params.get(key), "utf-8")));
            pos++;
        }
        return tempParams.toString();
    }

    //处理响应体
    private static void handleResponse( HttpsURLConnection conn, HttpComunication httpComunication) throws IOException {
        //调用getInputStream方法后，服务端才会收到请求，并阻塞式地接收服务端返回的数据
        InputStream is = conn.getInputStream();
        //将InputStream转换成byte数组,getBytesByInputStream会关闭输入流
        byte[] responseBody = getBytesByInputStream(is);
        httpComunication.setResponseBody(responseBody);
        //获取响应头
        String responseHeader = getResponseHeader(conn);
        httpComunication.setResponseHeader(responseHeader);
        // 判断请求是否成功
        if (conn.getResponseCode() == 200) {
            String str = httpComunication.getResponseBodyStr();
            if (httpComunication.getResponseBodyStr().length() > 100) {
                str = str.substring(0, 100) + "...";
            }
            log.debug("请求成功,response:" + str);
        } else {
            log.warn("请求失败:" + conn.getResponseCode());
        }
    }

    //读取请求头
    private static String getReqeustHeader(HttpsURLConnection conn) {
        Map<String, List<String>> requestHeaderMap = conn.getRequestProperties();
        Iterator<String> requestHeaderIterator = requestHeaderMap.keySet().iterator();
        StringBuilder sbRequestHeader = new StringBuilder();
        while (requestHeaderIterator.hasNext()) {
            String requestHeaderKey = requestHeaderIterator.next();
            String requestHeaderValue = conn.getRequestProperty(requestHeaderKey);
            sbRequestHeader.append(requestHeaderKey);
            sbRequestHeader.append(":");
            sbRequestHeader.append(requestHeaderValue);
            sbRequestHeader.append("\n");
        }
        return sbRequestHeader.toString();
    }

    //读取响应头
    private static String getResponseHeader(HttpsURLConnection conn) {
        Map<String, List<String>> responseHeaderMap = conn.getHeaderFields();
        int size = responseHeaderMap.size();
        StringBuilder sbResponseHeader = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String responseHeaderKey = conn.getHeaderFieldKey(i);
            String responseHeaderValue = conn.getHeaderField(i);
            sbResponseHeader.append(responseHeaderKey);
            sbResponseHeader.append(":");
            sbResponseHeader.append(responseHeaderValue);
            sbResponseHeader.append("\n");
        }
        return sbResponseHeader.toString();
    }

    /**
     * 从InputStream中读取数据，转换成byte数组，最后关闭InputStream
     * @param is 输入流
     * @return byte[]
     */
    private static byte[] getBytesByInputStream(InputStream is) {
        byte[] bytes = null;
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        byte[] buffer = new byte[1024 * 8];
        int length = 0;
        try {
            while ((length = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    public static class HttpComunication {
        enum HttpType {GET, POST}
        //请求头
        private String requestHeader;
        //请求体
        private byte[] requestBody;
        //响应头
        private String responseHeader;
        //响应体
        private byte[] responseBody;
        private HttpType type;

        public HttpComunication() {
        }

        public String getRequestHeader() {
            return requestHeader;
        }

        public void setRequestHeader(String requestHeader) {
            this.requestHeader = requestHeader;
        }

        public byte[] getRequestBody() {
            return requestBody;
        }

        public void setRequestBody(byte[] requestBody) {
            this.requestBody = requestBody;
        }

        public String getResponseHeader() {
            return responseHeader;
        }

        public void setResponseHeader(String responseHeader) {
            this.responseHeader = responseHeader;
        }

        public byte[] getResponseBody() {
            return responseBody;
        }

        public String getResponseBodyStr() {
            try {
                return new String(responseBody,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        public <T> T getResponseEntity(Class<T> cls) {
            try {
                return objectMapper.readValue(getResponseBodyStr(), cls);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void setResponseBody(byte[] responseBody) {
            this.responseBody = responseBody;
        }

        public HttpType getType() {
            return type;
        }

        public void setType(HttpType type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "HttpComunication{" +
                    "requestHeader='" + requestHeader.replaceAll("\n", "") + '\'' +
                    ", type=" + type +
                    ", requestBody=" + (requestBody == null || "".equals(requestBody) ? "null" : "not null") +
//                    ", requestBodyStr=" + (requestBody == null ? "null" : new String(requestBody)) +
                    ", responseHeader='" + responseHeader.replaceAll("\n", "") + '\'' +
                    ", responseBody=" + (responseBody == null ? "null" : "not null") +
//                    ", responseBodyStr=" + (responseBody == null ? "" : new String(responseBody)) +
                    '}';
        }
    }
}
