package com.github.chenjianjx.srb4jfullsample.impl.support.beanvalidate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.LinkedHashMap;
import java.util.Map;

public class ValidationError {

    /**
     * not related for any field
     */
    private String nonFieldError;

    /**
     * related to fields.  key = the field name (the property path of a bean, e.g.), value = the error message
     */
    private Map<String, String> fieldErrors = new LinkedHashMap<>();

    public boolean hasErrors() {
        return nonFieldError != null || (fieldErrors != null && fieldErrors.size() > 0);
    }

    public String getNonFieldError() {
        return nonFieldError;
    }

    public void setNonFieldError(String nonFieldError) {
        this.nonFieldError = nonFieldError;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void addFieldError(String field, String error) {
        this.fieldErrors.put(field, error);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
