package com.zone.test.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zone.test.base.common.JsonResult;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 2019/7/31 9:15
 *
 * @author owen pan
 */
@Component
public class GlobalExceptionInterceptor implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        e.printStackTrace();
        ModelAndView mv=new ModelAndView();
        try {
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Content-Type", "text/html; charset=UTF-8");
            httpServletResponse.getWriter().write(
                    new ObjectMapper().writeValueAsString(
                            new JsonResult.Builder<>()
                                    .msg(e.getClass().getName()+"("+e.getMessage()+")")
                                    .code(500)
                                    .build()
                    )
            );
            httpServletResponse.getWriter().close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return mv;
    }
}
