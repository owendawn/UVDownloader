package com.zone.uvdownloader.util;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HttpDownLoader {
    private int threadCount = 3;
    private String path;
    private String targetPath;
    private String fileName;

    private int reCode = -1;
    public int fileLength;
    public final AtomicInteger live = new AtomicInteger(0);
    public final AtomicLong loadLength = new AtomicLong(0);

    public String getPercent() {
        return Math.round(loadLength.get() * 10000.0 / fileLength) / 100.0 + "%";
    }

    public HttpDownLoader(String path, String targetPath, int threadCount) {
        this.path = path;
        this.threadCount = threadCount;
        if (path.contains("?")) {
            String onlypath = path.substring(0, path.indexOf("?"));
            this.fileName = onlypath.substring(onlypath.lastIndexOf("/") + 1);
        } else {
            this.fileName = path.substring(path.lastIndexOf("/") + 1);
        }
        this.targetPath =targetPath + "/" + fileName;
//        this.targetPath = PathCheckUtil.getPathAvailable(targetPath + "/" + fileName);
    }

    public void start() {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10 * 1000);
            reCode = connection.getResponseCode();
            if (reCode == 200) {
                fileLength = connection.getContentLength();
//                RandomAccessFile randomAccessFile = new RandomAccessFile(new File(targetPath), "rw");
//                randomAccessFile.setLength(fileLength);
                int blockSize = fileLength / threadCount;
                for (int i = 0; i < threadCount; i++) {
                    int start = i * blockSize;
                    int end = (i + 1) * blockSize - 1;
                    if (i == threadCount - 1) {
                        end = fileLength - 1;
                    }
                    new DownloadThread(fileName.substring(0,fileName.lastIndexOf(".")), i, start, end).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public class DownloadThread extends Thread {
        private String taskFlag;
        private int threadId;
        private int end;
        private int start;
        private int reCode;
        private long total = 0;
        private long load = 0;

        public DownloadThread(String taskFlag, int threadId, int start, int end) {
            this.taskFlag = taskFlag;
            this.threadId = threadId;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "DownloadThread{" +
                    "taskFlag='" + taskFlag + '\'' +
                    ", threadId=" + threadId +
                    ", end=" + end +
                    ", start=" + start +
                    ", reCode=" + reCode +
                    ", total=" + total +
                    '}';
        }

        public void run() {
            live.incrementAndGet();
            File targetFile = new File(targetPath);
            try {
                File file = new File("tmp/"+taskFlag + "_" + threadId + ".uvtmp");
                RandomAccessFile tmpRandomAccessFile = new RandomAccessFile(file, "rwd");
                RandomAccessFile downloadAccessFile = new RandomAccessFile(targetFile, "rw");
                if (file.exists()) {    //是否断点
                    String lastPostion_str = tmpRandomAccessFile.readLine();
                    if (lastPostion_str != null && !"".equals(lastPostion_str)) {
                        int newstart = Integer.parseInt(lastPostion_str) - 1;//设置下载起点
                        total = newstart-start;
                        loadLength.addAndGet(total);
                        start=newstart;
                    }
                }
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Range", "bytes=" + start + "-" + end);
                connection.setAllowUserInteraction(true);
                // 设置连接超时时间为10000ms
                connection.setConnectTimeout(10000);
                // 设置读取数据超时时间为10000ms
                connection.setReadTimeout(10000);
//                connection.setInstanceFollowRedirects(false);
                connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");

                System.err.println("实际线程:" + taskFlag + "_" + threadId + ",开始位置:" + start + ",结束位置:" + end);
                reCode = connection.getResponseCode();
                if (reCode != HttpURLConnection.HTTP_OK && reCode == HttpURLConnection.HTTP_PARTIAL) {    //200:请求全部资源成功  206:代表部分资源请求成功
                    InputStream inputStream = connection.getInputStream();
                    downloadAccessFile.seek(start);
                    byte[] buffer = new byte[1024 * 10];
                    int length = -1;
                    while ((length = inputStream.read(buffer)) != -1) {
                        downloadAccessFile.write(buffer, 0, length);
                        total += length;
                        load += length;
                        loadLength.getAndAdd(length);

                        //将当前现在到的位置保存到文件
                        tmpRandomAccessFile.seek(0);
                        tmpRandomAccessFile.write((load + start + "").getBytes("UTF-8"));
                    }
                    tmpRandomAccessFile.close();
                    downloadAccessFile.close();
                    inputStream.close();
                    file.deleteOnExit();
                } else if(reCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("响应码是" + connection.getResponseCode() + ". 服务器不支持断点重连下载");
                    InputStream is = connection.getInputStream();
                    downloadAccessFile.seek(start);//跳到某一位置开始

                    byte[] buf = new byte[1024];
                    int length = 0;
                    while((length = is.read(buf))!=-1){
                        downloadAccessFile.write(buf, 0, length);
                        total += length;
                        load += length;
                        loadLength.getAndAdd(length);
                    }
                    downloadAccessFile.close();
                    is.close();
                }
                System.err.println("线程:" + taskFlag + "_" + threadId + "下载完毕," + this.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                live.decrementAndGet();
            }
        }
    }


    public static void main(String[] args) {
        //断点多线程下载
        String url="";
         url="http://hd.yinyuetai.com/uploads/videos/common/90B8015D26C51713A86A1B985458D61E.mp4?sc\\u003d40ce1e07ffd3885d\\u0026br\\u003d1094\\u0026vid\\u003d2906675\\u0026aid\\u003d34557\\u0026area\\u003dHT\\u0026vst\\u003d0";
//         url="http://4g.55015.com/photoshop_5452.apk";
//         url="http://down10.zol.com.cn/tuxcl/Adobe_Photoshop_CC_16.0.0.88.exe";
        HttpDownLoader httpDownLoader = new HttpDownLoader(url, "d:/", 10);

        httpDownLoader.start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (httpDownLoader.live.get() > 0) {
                    System.out.println(httpDownLoader.loadLength + "/" + httpDownLoader.fileLength + "----" + httpDownLoader.getPercent());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(httpDownLoader.loadLength + "/" + httpDownLoader.fileLength + "----" +httpDownLoader.getPercent());
            }
        }).start();
    }
}
