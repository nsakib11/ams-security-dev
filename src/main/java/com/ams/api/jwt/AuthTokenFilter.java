package com.ams.api.jwt;

import com.ams.api.service.impl.CustomUserDetailsService;
import com.ams.api.util.CommonUtils;
import com.ams.api.util.GlobalProperties;
import com.ams.api.util.SessionMap;
import com.google.common.base.Strings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

import static com.ams.api.util.GlobalConstant.*;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private GlobalProperties properties;

    /*@Autowired
    private NgiAuditLogRepository manifestAuditLogRepository;*/

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Timestamp createdTime = new Timestamp(System.currentTimeMillis());
        Timestamp responseTime = null;
        HttpServletRequest requestToUse = request;
        HttpServletResponse responseToUse = response;
        try {
            SessionMap.setContext(KEY_MAP_TRACE_ID, CommonUtils.getReferenceNo());
            MDC.put(KEY_MAP_TRACE_ID, String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID)));
            MDC.put(KEY_MAP_SPAN_ID, request.getRemoteAddr());
            SessionMap.setContext(KEY_MAP_API_VERSION, "v1");
            String jwt = parseJwt(request);
            boolean isFirstRequest = !isAsyncDispatch(request);
            if (isFirstRequest)
                requestToUse = wrapRequest(request);
            responseToUse = wrapResponse(response);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
//                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                String email = jwtUtils.getEmailFromJwtToken(jwt);
                SessionMap.setContext(KEY_MAP_USER_ID, email);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(requestToUse, responseToUse);
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        } finally {
            responseTime = new Timestamp(System.currentTimeMillis());
            if (properties != null && properties.isGlobalAuditLog() && requestToUse != null && responseToUse != null
                    && !isAsyncStarted(requestToUse)) {
                logRequestResponse(requestToUse, responseToUse, createdTime, responseTime);
            }
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

    private void logRequestResponse(HttpServletRequest req, HttpServletResponse resp, Timestamp createdTime,
                                    Timestamp responseTime) {

        String requestJson = null;
        String responseJson = null;
//        NgiAuditLog auditlog = null;

        try {

            String urlRequest = getURL(req);
            String requestStr = getRequest(req);
            String responseStr = getResponse(resp);
            String responseStatus = getStatus(resp);
            String httpMethod = getMethod(req);
            String client = getClient(req);
            String httpHeader = getHeader(req);
            String podName = getPodName();
            String queryString = getQueryString(req);

            if (CommonUtils.isBypassedEndpoint(urlRequest)) {
                return;
            }

            requestJson = requestStr;
            responseJson = responseStr;

            logger.error("Saved Audit Log Request : " + requestJson);
            logger.info("Saved Audit Log : " + responseJson);

            String comments = String.valueOf(SessionMap.getValue(KEY_MAP_COMMENTS));
            String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));
            String user = String.valueOf(SessionMap.getValue(KEY_MAP_USER_ID));

//            auditlog = new NgiAuditLog();
//            auditlog.setContainerId(podName);
//            auditlog.setHttpMethod(httpMethod);
//            auditlog.setClient(client);
//            auditlog.setHttpHeader(httpHeader);
//            auditlog.setUrl(urlRequest);
//            auditlog.setComments(comments);
//            auditlog.setQueryString(queryString);
//            auditlog.setRequest(requestJson);
//            auditlog.setResponse(responseJson);
//            auditlog.setResponseStatus(responseStatus);
//            auditlog.setResponseTime(responseTime.getTime() - createdTime.getTime());
//            auditlog.setRequestTime(createdTime);
//            auditlog.setProcessingStartTime(createdTime);
//            auditlog.setProcessingEndTime(responseTime);
//            auditlog.setTraceId(traceId);
//            Timestamp endTime = new Timestamp(System.currentTimeMillis());
//            auditlog.setInsertTime(endTime);
//            auditlog.setInsertBy(user == null || user.equals("null") || user.isEmpty() ? client : user);
//            logger.info("Saved Audit Log");
//            manifestAuditLogRepository.save(auditlog);
            SessionMap.remove();
        } catch (Exception e) {
            logger.error("Error in Audit :{}", e);
        } finally {
            requestJson = null;
            responseJson = null;
//            auditlog = null;
        }
    }

    // Get Resposestaus
    private String getStatus(HttpServletResponse response) {
        return Integer.valueOf(response.getStatus()).toString();
    }

    // Get URL
    private String getURL(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // Get Request
    private String getRequest(HttpServletRequest request) {

        StringBuilder msg = new StringBuilder();
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                // int length = Math.min(buf.length, getMaxPayloadLength());
                int length = buf.length;
                String payload;
                try {
                    payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    payload = "[unknown]";
                }
                msg.append(payload);
            }
        }
        return msg.toString();
    }

    // Get Response
    private String getResponse(HttpServletResponse resp) {

        StringBuilder msg = new StringBuilder();

        ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(resp,
                ContentCachingResponseWrapper.class);
        if (responseWrapper != null) {
            byte[] buf = responseWrapper.getContentAsByteArray();
            try {
                responseWrapper.copyBodyToResponse();
            } catch (IOException e) {
                logger.error("Fail to write response body back", e);
            }
            if (buf.length > 0) {
                String payload;
                try {
                    payload = new String(buf, 0, buf.length, responseWrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    payload = "[unknown]";
                }
                msg.append(payload);
            }
        }
        return msg.toString();

    }

    private ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

    // Get Method
    private String getMethod(HttpServletRequest request) {
        return request.getMethod();
    }

    // Get Client
    private String getClient(HttpServletRequest request) {
        String client = request.getRemoteAddr();
        if (!StringUtils.hasLength(client)) {
            client = "";
        }
        return client;
    }

    // Get Header
    private String getHeader(HttpServletRequest request) {
        String httpHeader = String.valueOf(new ServletServerHttpRequest(request).getHeaders());
        if (!StringUtils.hasLength(httpHeader)) {
            httpHeader = "";
        }
        return httpHeader;
    }

    // Get Pod
    private String getPodName() {
        String podName = System.getenv("HOSTNAME");
        if (podName == null) {
            podName = "";
        }
        return podName;
    }

    // Get QueryString
    private String getQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null) {
            queryString = "";
        }
        return queryString;
    }
}
