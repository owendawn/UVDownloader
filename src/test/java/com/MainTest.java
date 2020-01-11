package com;

import javax.swing.filechooser.FileSystemView;

/**
 * 2018/3/5 16:55
 *
 * @author owen pan
 */
public class MainTest {


    public static void main(String[] args) {
        System.out.println(FileSystemView.getFileSystemView() .getHomeDirectory().getAbsolutePath());
    }
}
