package com.zone.test.util;

import jdk.nashorn.internal.runtime.regexp.RegExp;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FTPDownloader {
    private FTPClient ftpClient = new FTPClient();

    private String url;
    private String fileName;
    private String targetPath;
    private String hostname;
    private int port;
    private String username;
    private String password;
    private String path;

    public FTPDownloader(String url,String targetPath) {
        Pattern pattern= Pattern.compile("^ftp://([^/]+):([^/]+)@([^/:]+):?(\\d+)?/(.*)");
        Matcher matcher=pattern.matcher(url);

        if (matcher.find( )) {
            url= matcher.group(0);
            username= matcher.group(1) ;
            password=matcher.group(2) ;
            hostname=matcher.group(3) ;
            port=matcher.group(4) ==null?80:Integer.parseInt(matcher.group(4));
            path="/"+matcher.group(5) ;
            if (path.contains("?")) {
                String onlypath = path.substring(0, path.indexOf("?"));
                this.fileName = onlypath.substring(onlypath.lastIndexOf("/") + 1);
            } else {
                this.fileName = path.substring(path.lastIndexOf("/") + 1);
            }
            this.targetPath =targetPath + "/" + fileName;
        } else {
            System.err.println("NO MATCH");
        }
    }

    public void start(){
        try {
            ftpClient.connect(hostname, port);
            ftpClient.setConnectTimeout(60*1000);
            ftpClient.setControlEncoding("GBK");
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                if (ftpClient.login(username, password)) {
                    System.out.println("登陆成功");
                    download(path,targetPath);
                    return;
                }
            }
            ftpClient.logout();
           if(ftpClient.isConnected()){
                ftpClient.disconnect();
           }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download(String remote, String local)
            throws IOException {
        // 设置被动模式
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("GBK"), "iso-8859-1"));
        if (files.length != 1) {
            System.err.println("远程文件不存在");
            return;
        }

        long remoteSize = files[0].getSize();
        File f = new File(local);
        // 本地存在文件，进行断点下载
        if (f.exists()) {
            long localSize = f.length();
            // 判断本地文件大小是否大于远程文件大小
            if (localSize >= remoteSize) {
                System.out.println("本地文件大于远程文件，下载中止");
                return;
            }

            // 进行断点续传，并记录状态
            FileOutputStream out = new FileOutputStream(f, true);
            ftpClient.setRestartOffset(localSize);
        }
        InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"), "iso-8859-1"));
        byte[] bytes = new byte[1024];
        int c;
        OutputStream out = new FileOutputStream(f);
        while ((c = in.read(bytes)) != -1) {
            out.write(bytes, 0, c);
//            localSize += c;

        }
        in.close();
        out.close();
        boolean isDo = ftpClient.completePendingCommand();
    }


    public static void main(String[] args) {
        String url="";
        url="ftp://ygdy8:ygdy8@yg45.dydytt.net:7213/阳光电影www.ygdy8.com.芳华.BD.720p.国语中字.mkv";
        url="ftp://ygdy8:ygdy8@yg45.dydytt.net:7213/阳光电影www.ygdy8.com.芳华.BD.720p.国语中字.mkv";
        FTPDownloader ftpDownloader=new FTPDownloader(url,"d://");
        ftpDownloader.start();
    }
}
