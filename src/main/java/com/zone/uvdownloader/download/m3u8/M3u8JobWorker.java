package com.zone.uvdownloader.download.m3u8;

import com.zone.uvdownloader.controller.M3U8Controller;
import com.zone.uvdownloader.download.BaseWorker;
import com.zone.uvdownloader.entity.M3u8Item;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 2020/1/11 16:45
 *
 * @author owen pan
 */
public class M3u8JobWorker implements BaseWorker {
    private M3u8Job m3u8Job;
    private List<M3u8Item> jobList = new ArrayList<>();
    private ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public M3u8JobWorker(M3u8Job m3u8Job) {
        this.m3u8Job = m3u8Job;
        m3u8Job.getActive().set(pool.getActiveCount());
        jobList.addAll(m3u8Job.getItems());
    }

    @Override
    public void run() {
        pool.submit(new Runnable() {
            @Override
            public void run() {
                m3u8Job.setLast(new AtomicLong(System.currentTimeMillis()));
                long complete = m3u8Job.getComplete().longValue();
                System.out.println((m3u8Job.getTotal().longValue() > m3u8Job.getCount().longValue()) + "|" + m3u8Job.getTotal().longValue() + "|" + m3u8Job.getCount().longValue());
                while (m3u8Job.getTotal().longValue() > m3u8Job.getCount().longValue()) {
                    try {
                        long during = (System.currentTimeMillis() - m3u8Job.getLast().longValue());
                        if (during > 0) {
                            m3u8Job.getSpeed().set(
                                    Math.round(1D * (m3u8Job.getComplete().longValue() - complete) / during * 1000)
                            );
                        }
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                m3u8Job.getSpeed().set(0);
            }
        });
        System.out.println("开始下载");

        for (int i = 0; i < jobList.size(); i++) {
            M3u8Item m3u8Item = jobList.get(i);
            if (pool.getActiveCount() < M3U8Controller.CONNECT_SIZE.get()) {
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        m3u8Job.getActive().incrementAndGet();
                        long length = 0;
                        HttpURLConnection conn = null;
                        RandomAccessFile file = null;
                        BufferedInputStream bis = null;
                        File finishtTarget=new File(m3u8Item.getFinishTarget());
                        File tmpTarget=new File(m3u8Item.getTmpTarget());
                        try {
                            if (finishtTarget.exists() &&finishtTarget.isFile() && finishtTarget.length() > 0) {
                                length = finishtTarget.length();
//                                System.out.print("F");
                                m3u8Job.getCount().incrementAndGet();
                                m3u8Job.getLength().addAndGet(length);
                                m3u8Item.setLength(length);
                                m3u8Item.getComplete().set(length);
                                m3u8Job.getComplete().addAndGet(length);
                                m3u8Job.getDuringAlready().addAndGet(m3u8Item.getDuring());
                                log();
                                m3u8Item.setState(2);
                                return;
                            } else if (finishtTarget.length() <= 0) {
                                finishtTarget.delete();
                            }

                            if (tmpTarget.exists() && tmpTarget.isFile()) {
                                length = tmpTarget.length();
//                                System.out.print("A");
                            } else {
                                tmpTarget.createNewFile();
                            }
                            m3u8Item.setState(1);
                            file = new RandomAccessFile(tmpTarget.getAbsoluteFile(), "rw");

                            if (m3u8Item.getUrl().startsWith("https:")) {
                                conn = (HttpsURLConnection) new URL(null, m3u8Item.getUrl(), new sun.net.www.protocol.https.Handler()).openConnection();
                                ((HttpsURLConnection) conn).setSSLSocketFactory(getSSLSocketFactory());
                            }else{
                                conn = (HttpURLConnection) new URL(null, m3u8Item.getUrl(), new sun.net.www.protocol.http.Handler()).openConnection();
                            }
                            conn.setConnectTimeout(30 * 1000);
                            long reLen = 0;
                            try {
                                reLen = Long.parseLong(conn.getHeaderField("Content-Length"));
                            } catch (Exception e) {
                                this.run();
                                return;
//                                e.printStackTrace();
                            }
                            conn.disconnect();

                            m3u8Job.getCount().incrementAndGet();
                            if (length >= reLen) {
                                m3u8Item.setLength(reLen);
                                m3u8Job.getLength().addAndGet(reLen);
                                m3u8Item.getComplete().set(reLen);
                                m3u8Job.getComplete().addAndGet(reLen);
                                m3u8Job.getDuringAlready().addAndGet(m3u8Item.getDuring());
                                IOUtils.closeQuietly(file);
                                file = null;
                                tmpTarget.renameTo(finishtTarget);
                                log();
                            } else {
                                file.seek(length);
                                if (m3u8Item.getUrl().startsWith("https:")) {
                                    conn = (HttpsURLConnection) new URL(null, m3u8Item.getUrl(), new sun.net.www.protocol.https.Handler()).openConnection();
                                    ((HttpsURLConnection) conn).setSSLSocketFactory(getSSLSocketFactory());
                                }else{
                                    conn = (HttpURLConnection) new URL(null, m3u8Item.getUrl(), new sun.net.www.protocol.http.Handler()).openConnection();
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
                                conn.setConnectTimeout(30 * 1000);
                                //在对各种参数配置完成后，通过调用connect方法建立TCP连接，但是并未真正获取数据
                                //conn.connect()方法不必显式调用，当调用conn.getInputStream()方法时内部也会自动调用connect方法
                                conn.addRequestProperty("Range", "bytes=" + length + "-" + reLen);
                                conn.connect();
                                m3u8Item.setLength(Long.parseLong(conn.getHeaderField("Content-Length")));
                                m3u8Job.getLength().addAndGet(m3u8Item.getLength());
                                bis = new BufferedInputStream(conn.getInputStream());
                                int len = 0;
                                byte[] buff = new byte[1024];
                                while ((len = bis.read(buff)) > 0) {
                                    file.write(buff, 0, len);
                                    m3u8Item.getComplete().addAndGet(len);
                                    m3u8Job.getComplete().addAndGet(len);
                                }
                                m3u8Job.getDuringAlready().addAndGet(m3u8Item.getDuring());
                                IOUtils.closeQuietly(file);
                                file = null;
                                tmpTarget.renameTo(finishtTarget);
                                log();
                                m3u8Item.setState(2);
                            }
                        } catch (SocketTimeoutException | ConnectException e) {
                            System.out.println("异常重置：(" + m3u8Item.getUrl()+"):"+e.getMessage());
                            if(tmpTarget.exists()) {
                                tmpTarget.delete();
                            }
                            m3u8Item.setState(3);
                        } catch (Exception e) {
                            System.out.println("异常：" + m3u8Item.getUrl());
                            e.printStackTrace();
                            if(tmpTarget.exists()) {
                                tmpTarget.delete();
                            }
                            m3u8Item.setState(3);
                        } finally {
                            if (conn != null) {
                                conn.disconnect();
                            }
                            IOUtils.closeQuietly(bis);
                            IOUtils.closeQuietly(file);
                            file = null;
                            if(m3u8Item.getState()!=2){
                                if(tmpTarget.exists()) {
                                    tmpTarget.delete();
                                }
                                if(finishtTarget.exists()) {
                                    finishtTarget.delete();
                                }
                            }
                            m3u8Job.getActive().decrementAndGet();
                        }
                    }
                });
            } else {
                log();
                i--;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        log();
    }

    private void log() {
        int percent = (int) (Math.floor(m3u8Job.getCount().get() * 1.0 / m3u8Job.getTotal() * 100));
        String str = "";
        String ss = "" + percent;
        for (int i = 0; i < 100; i++) {
            if (i < percent) {
                str += "-";
            } else {
                str += " ";
            }
        }
        while (ss.length() < 3) {
            ss = " " + ss;
        }
        String content = ss + "% >" + str + "<";
        String prefix = "";
        for (int i = 0; i < /*content.length()*/500; i++) {
            prefix += "\b";
        }
        System.out.print(prefix + content);
    }

    public static SSLSocketFactory getSSLSocketFactory() {
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
        }};
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
