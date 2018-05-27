package com.zone.test.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class a {
    public  void ReadFileFromSftp() {

        String path="uploads//";
        String fileName="20161012buy.txt";
        String result="";
        FTPClient ftp = new FTPClient();

        try {
            int reply;
            //1.连接服务器
            ftp.connect("23.234.51.145",7213);
            //2.登录服务器 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login("ygdy8", "ygdy8");
            //3.判断登陆是否成功
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
            }
            //4.指定要下载的目录
            ftp.changeWorkingDirectory(path);// 转移到FTP服务器目录
            //5.遍历下载的目录
            ftp.enterLocalPassiveMode();
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                //解决中文乱码问题，两次解码
                byte[] bytes=ff.getName().getBytes("iso-8859-1");
                String fn=new String(bytes,"utf8");
                if (fn.equals(fileName)) {
                    InputStream inputStream=ftp.retrieveFileStream(ff.getName());
//                    result = IOUtils.toString(inputStream);
                }

            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }

        System.out.println("result"+result);

    }

    public static void main(String[] args) {
        new a().ReadFileFromSftp();
    }

}
