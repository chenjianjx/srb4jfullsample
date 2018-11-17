package com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support;


import javax.servlet.http.HttpSession;

public class BoSessionHelper {

    private static final String SESSION_KEY_STAFF_USER = "staffUser";

    /**
     * will make sure only a valid object is returned
     *
     * @param session
     * @return
     */
    public static BoSessionStaffUser getStaffUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        BoSessionStaffUser staffUser = (BoSessionStaffUser) session.getAttribute(SESSION_KEY_STAFF_USER);
        if (staffUser == null) {
            return null;
        }

        if (!isStaffUserValid(staffUser)) { //invalid
            synchronized (session) {
                session.removeAttribute(SESSION_KEY_STAFF_USER);
            }
            return null;
        }

        return staffUser;
    }

    public static void setStaffUser(HttpSession session, BoSessionStaffUser staffUser) {
        if (session == null) {
            throw new IllegalArgumentException("session is null");
        }

        if (staffUser == null) {
            throw new IllegalArgumentException("staffUser is null");
        }

        if (!isStaffUserValid(staffUser)) {
            throw new IllegalArgumentException("The staffUser you passed in is not valid. It is " + staffUser.toString());
        }

        session.setAttribute(SESSION_KEY_STAFF_USER, staffUser);
    }

    /**
     * will throw an exception if there is no staff user in the session
     * @param session
     * @return
     */
    public static long getSessionStaffUserId(HttpSession session) {
        BoSessionStaffUser staffUser = getStaffUser(session);
        if(staffUser == null){
            throw new IllegalStateException("No staffUser in session");
        }
        return staffUser.getUserId();
    }


    private static boolean isStaffUserValid(BoSessionStaffUser staffUser) {
        return staffUser.getUsername() != null && staffUser.getUserId() > 0;
    }
}
