package com.github.chenjianjx.srb4jfullsample.intf.fo.user;


import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoEntityBase;

public class FoUser extends FoEntityBase {


    private String principal;

    private String source;

    private String email;

    private boolean emailVerified;

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
