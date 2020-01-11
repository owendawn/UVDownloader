package com.zone.test.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.swing.filechooser.FileSystemView;

/**
 * Created by Owen Pan on 2016/10/8.
 */
@Controller
@RequestMapping("/view")
public class ViewController {
    @Value("${project.name}")
    private String projectName;
    private static final String desktopPath = FileSystemView
            .getFileSystemView()
            .getHomeDirectory()
            .getAbsolutePath()
            .replaceAll("\\\\","/");

    /**
     * 2019/12/7 14:28
     *
     * @param request
     * @param modelMap
     * @return {@code java.lang.String}
     * @author owen pan
     */
    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public String path(HttpServletRequest request, ModelMap modelMap) {
        modelMap.put("projectName", projectName);
        modelMap.put("desktopPath", desktopPath);
        return request.getRequestURI().replace(request.getContextPath() + "/view/", "/");
    }
}
