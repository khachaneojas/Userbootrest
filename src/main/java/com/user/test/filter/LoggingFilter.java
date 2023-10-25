package com.user.test.filter;

import com.user.test.exception.InvalidDataException;
import com.user.test.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;
    Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper contentCachingRequestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);

        Long startTime = System.currentTimeMillis();

        filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
        Long timeTaken = System.currentTimeMillis() - startTime;

        String requestBody = getStringValue
                (contentCachingRequestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
        String responseBody = getStringValue(
                contentCachingResponseWrapper.getContentAsByteArray(), response.getCharacterEncoding());

        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(null == requestAttributes)
            throw new InvalidDataException("No request in the context");

        HttpServletRequest requestAttributesRequest = requestAttributes.getRequest();
        String authorizationHeaderString = requestAttributesRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if(null == authorizationHeaderString)
            throw new InvalidDataException("Authorization Header is required");

        Integer userID = jwtUtil.getUserIdfromJwt(authorizationHeaderString);

        if(response.getStatus() >= 400){
            logger.error("filter Logs : METHOD = {}; REQUESTURI = {}; REQUEST BODY = {}; RESPONSE CODE = {}; USERID = {}; TIME TAKEN = {}",
                    request.getMethod(), request.getRequestURI(), requestBody, response.getStatus(), userID, timeTaken);
        }else {
            logger.info
                    ("filter Logs : METHOD = {}; REQUESTURI = {}; REQUEST BODY = {}; RESPONSE CODE = {}; USERID = {}; TIME TAKEN = {}; IP = {}; HOST = {}",
                            request.getMethod(), request.getRequestURI(), requestBody, response.getStatus(), userID, timeTaken, request.getRemoteAddr(), request.getRemoteHost());
            contentCachingResponseWrapper.copyBodyToResponse();
        }
    }

    private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {

        try
        {
            return new String(contentAsByteArray, 0, contentAsByteArray.length, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


}
