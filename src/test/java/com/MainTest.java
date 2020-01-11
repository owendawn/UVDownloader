package com;

import javax.swing.filechooser.FileSystemView;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 2018/3/5 16:55
 *
 * @author owen pan
 */
public class MainTest {


    public static void main(String[] args) throws Exception {
//        System.out.println(FileSystemView.getFileSystemView() .getHomeDirectory().getAbsolutePath());
//        try {
//            System.out.println(new File("").getCanonicalPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        File file=new File("E:/workstation/intellij/UVDownloader/ffmpeg.exe");
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuffer msg = new StringBuffer();
//        String command=file.getAbsolutePath()+" -v";
        String command="E:/workstation/intellij/UVDownloader/ffmpeg.exe -i \"concat:C:/Users/owen-p/Desktop/index/tmp/finish_5fe97a3f46b000.ts|C:/Users/owen-p/Desktop/index/tmp/finish_5fe97a3f46b001.ts\" -c copy -bsf:a aac_adtstoasc -movflags +faststart C:/Users/owen-p/Desktop/index/transfer/1578756930726.ts";
        ProcessBuilder builder = new ProcessBuilder(command.replaceAll("\\s+"," ").split(" "));
//        builder.directory(file.getParentFile());
//        builder.command(command);
        System.out.println(builder.directory());
        builder.redirectErrorStream(true);
        Process process = builder.start();

        isr = new InputStreamReader(process.getInputStream());
        br = new BufferedReader(isr);
        String szline;
        while ((szline = br.readLine()) != null) {
            System.out.println(szline);
            msg.append(szline);
        }
        int result = process.waitFor();
        System.out.println(result);
        process.destroy();

    }
}
