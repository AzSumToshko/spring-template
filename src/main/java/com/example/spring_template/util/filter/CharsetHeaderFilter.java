package com.example.spring_template.util.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CharsetHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (response instanceof HttpServletResponse httpResp) {
            httpResp.setCharacterEncoding("UTF-8");
            httpResp.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpResp.setHeader("Accept-Charset", "UTF-8");
        }

        chain.doFilter(request, response);
    }
}