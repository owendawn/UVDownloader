package com.zone.test.base.config;
 
import org.springframework.core.annotation.Order;
 
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
 
@javax.servlet.annotation.WebFilter(filterName = "sessionFilter",urlPatterns = "/*")
@Order(1)
public class WebFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req= (HttpServletRequest) request;
        req.getSession().setAttribute("ip",req.getRemoteHost());
        chain.doFilter(request,response);
    }
}