package com.zone.test.download.m3u8;

import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.zone.test.controller.M3U8Controller;
import com.zone.test.download.BaseWorker;
import com.zone.test.entity.M3u8Item;
import com.zone.test.util.HttpUrlConnectionUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
        System.out.println("开始下载");
        File root = new File(m3u8Job.getDir() + "/" + m3u8Job.getFile() + "/tmp");
        if (!root.exists()) {
            root.mkdirs();
        }
        System.out.println("存放路径：" + root.getParent());
        String tmpPath = root.getAbsolutePath();
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
                        File target = new File(tmpPath + "/" + m3u8Item.getFileName());
                        File finishtTarget = new File(tmpPath + "/finish_" + m3u8Item.getFileName());
                        try {
                            if(finishtTarget.exists()&&finishtTarget.isFile()){
                                length=finishtTarget.length();
                                System.out.println("完整文件已存在:" + length);
                                m3u8Item.setTarget(finishtTarget.getAbsolutePath().replaceAll("\\\\", "/"));
                                m3u8Job.getLength().addAndGet(length);
                                m3u8Item.setLength(length);
                                m3u8Job.getCount().incrementAndGet();
                                m3u8Item.getComplete().set(length);
                                m3u8Job.getComplete().addAndGet(length);
                                m3u8Job.getDuringAlready().addAndGet(m3u8Item.getDuring());
                                return;
                            }
                            if (target.exists() && target.isFile()) {
                                length = target.length();
                                System.out.println("文件已存在:" + length);
                            } else {
                                target.createNewFile();
                            }
                            m3u8Item.setTarget(target.getAbsolutePath().replaceAll("\\\\", "/"));
                            file = new RandomAccessFile(target.getAbsoluteFile(), "rw");


                            conn = (HttpURLConnection) new URL(m3u8Item.getUrl()).openConnection();
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
                            m3u8Job.getLength().addAndGet(reLen);
                            m3u8Item.setLength(reLen);
                            if (length >= reLen) {
                                System.out.println("无需下载：" + m3u8Item.getUrl());
                                m3u8Job.getCount().incrementAndGet();
                                m3u8Item.getComplete().set(reLen);
                                m3u8Job.getComplete().addAndGet(reLen);
                                m3u8Job.getDuringAlready().addAndGet(m3u8Item.getDuring());
                                IOUtils.closeQuietly(file);
                                file = null;
                                target.renameTo(finishtTarget);
                                m3u8Item.setTarget(finishtTarget.getAbsolutePath().replaceAll("\\\\", "/"));
                            } else {
                                file.seek(length);
                                conn = (HttpURLConnection) new URL(m3u8Item.getUrl()).openConnection();
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
                                m3u8Job.getCount().incrementAndGet();
                                m3u8Job.getDuringAlready().addAndGet(m3u8Item.getDuring());
                                IOUtils.closeQuietly(file);
                                file = null;
                                target.renameTo(finishtTarget);
                                m3u8Item.setTarget(finishtTarget.getAbsolutePath().replaceAll("\\\\", "/"));
                            }
                        } catch (SocketTimeoutException | ConnectException e) {
                            this.run();
                        } catch (Exception e) {
                            System.out.println("异常：" + m3u8Item.getUrl());
                            e.printStackTrace();
                            IOUtils.closeQuietly(file);
                            file = null;
                            target.delete();
                        } finally {
                            if (conn != null) {
                                conn.disconnect();
                            }
                            IOUtils.closeQuietly(bis);
                            IOUtils.closeQuietly(file);
                            m3u8Job.getActive().decrementAndGet();
                        }
                    }
                });
            } else {
                i--;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
