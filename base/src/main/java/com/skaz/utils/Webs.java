package com.skaz.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jungle
 */
public class Webs {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String ACCEPT = "accept";
    private static final String X_REQUESTED_WITH = "X-Requested-With";
    private static final String XML_HTTP_REQUEST = "XMLHttpRequest";


    public static boolean isAjaxRequest(HttpServletRequest request) {
        if ((request.getHeader(ACCEPT) != null && request.getHeader(ACCEPT).indexOf(CONTENT_TYPE_JSON) > -1)) {
            return true;
        }
        if ((request.getHeader(X_REQUESTED_WITH) != null && request.getHeader(X_REQUESTED_WITH).indexOf(XML_HTTP_REQUEST) > -1)) {
            return true;
        }
        return false;
    }
}
