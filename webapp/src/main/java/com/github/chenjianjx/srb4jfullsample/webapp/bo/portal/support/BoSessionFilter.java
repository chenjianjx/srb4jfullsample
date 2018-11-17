package com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoSessionFilter implements Filter {

    private List<String> exempUrls = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        exempUrls.add("/login");
        exempUrls.add("/logout");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;


        String url = request.getRequestURI().substring(BoConstants.BO_PORTAL_URL_BASE.length());
        if (!url.startsWith("/")) {
            url = "/" + url;
        }

        if (exempUrls.contains(url)) {
            chain.doFilter(req, resp);
            return;
        }

        HttpSession session = request.getSession(true);
        String username = (String) session
                .getAttribute(BoConstants.SESSION_KEY_BO_USERNAME);

        if (username == null) {
            response.sendRedirect(BoUrlHelper.path2Url(BoResourcePaths.LOGIN));
            return;
        } else {
            chain.doFilter(req, resp);
        }

    }

    @Override
    public void destroy() {
    }
}
