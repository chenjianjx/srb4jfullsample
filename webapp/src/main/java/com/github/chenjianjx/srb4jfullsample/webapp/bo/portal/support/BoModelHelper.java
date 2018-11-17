package com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support;


import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.ErrorResult;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoPortalConstants.MVC_KEY_ERR;

public class BoModelHelper {

    public static Map<String, Object> initErrorModel(ErrorResult err) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put(MVC_KEY_ERR, err);
        return model;
    }

    public static Map<String, Object> addError(Map<String, Object> model, ErrorResult err) {
        model.put(MVC_KEY_ERR, err);
        return model;
    }
}
