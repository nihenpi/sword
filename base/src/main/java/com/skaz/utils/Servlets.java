package com.skaz.utils;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author jungle
 */
public class Servlets {


    public static void sendFailure(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        Integer status = Exceptions.determinResponseStatus(e);
        String message = Exceptions.determinResponseMessage(e);
        sendFailure(request, response, status, message);

    }

    private static void sendFailure(HttpServletRequest request, HttpServletResponse response, Integer status, String message) throws IOException {
        if (Webs.isAjaxRequest(request)) {
            sendJsonFailureResult(response, status, message);
        } else {
            response.sendError(status, message);
        }
    }

    private static void sendJsonFailureResult(HttpServletResponse response, Integer status, String message) throws IOException {
        sendJsonFailureResult(response, null, status, message);
    }

    private static void sendJsonFailureResult(HttpServletResponse response, Map<String,ObjectMappers> content, Integer status, String message) throws IOException {
        sendJson(response, Results.failureWithData(status, content, message), status);
    }

    private static void sendJson(HttpServletResponse response, Object value, Integer status) throws IOException {
        PrintWriter writer = null;
        try {
            String jsonString = "";
            if (value != null) {
                jsonString = Jsons.toJSONString(value);
            }
            setContentTypeJson(response);
            setNoCacheHeader(response);
            setStatus(response, status);
            writer = response.getWriter();
            writer.write(jsonString);
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private static void setStatus(HttpServletResponse response, Integer status) {
        response.setStatus(status);

    }

    private static void setNoCacheHeader(HttpServletResponse response) {

    }

    private static void setContentTypeJson(HttpServletResponse response) {
        setContentType(response, MediaTypes.APPLICATION_JSON_UTF_8);
    }

    private static void setContentType(HttpServletResponse response, String contentType) {
        response.setHeader("Content-Type", contentType);
    }

    public static String getRequestURL(HttpServletRequest request){
        return request.getRequestURL().toString();
    }
}
