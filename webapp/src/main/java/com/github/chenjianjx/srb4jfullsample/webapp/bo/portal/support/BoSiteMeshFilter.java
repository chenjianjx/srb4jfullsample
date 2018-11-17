package com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

/**
 * Created by chenjianjx@gmail.com on 17/11/18.
 */
public class BoSiteMeshFilter extends ConfigurableSiteMeshFilter {

    @Override
    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        builder.addDecoratorPath("/*", BoPortalApplication.JSP_DIR + "/decorator.jsp");
    }
}
