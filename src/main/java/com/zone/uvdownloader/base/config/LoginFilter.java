package com.zone.uvdownloader.base.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Owen Pan on 2017-07-07.
 */
@Component
@WebFilter(urlPatterns = "*", filterName = "loginFilter")
public class LoginFilter implements Filter {
    Log log = LogFactory.getLog(LoginFilter.class);

    @Autowired
    private ProjectProperty projectProperty;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("init LoginFilter");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest= (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse= (HttpServletResponse) servletResponse;
        String uri=httpServletRequest.getRequestURI();
        String queryString=httpServletRequest.getQueryString();
        String host=httpServletRequest.getServerName();
        if("/".equals(uri)){
             httpServletResponse.sendRedirect(projectProperty.getIndexURI());
             return;
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
