package me.aifaq.commons.lang;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Wang Wei [5waynewang@gmail.com]
 * @since 20:20 2017/5/18
 */
public class RequestUtil {
    public static final String UNKNOWN = "unknown";

    public static final String HTTP_SCHEME = "http";
    public static final int HTTP_DEFAULT_PORT = 80;

    public static final String HTTPS_SCHEME = "https";
    public static final int HTTPS_DEFAULT_PORT = 443;

    /**
     * 获取请求主机的IP地址
     *
     * @param request
     * @return
     */
    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getRemoteAddr();
        }

        // 如果通过了多级反向代理，取X-Forwarded-For中第一个非unknown的有效IP字符串
        final String[] ips = ip.split(",");
        for (int i = ips.length - 1; i >= 0; i--) {
            if (!StringUtils.equalsIgnoreCase(UNKNOWN, ips[i])) {
                ip = ips[i];
                break;
            }
        }

        return StringUtils.trimToEmpty(ip);
    }

    /**
     * 获取请求域名 host:port
     *
     * @param request
     * @return
     */
    public static String getRequestDomain(HttpServletRequest request) {
        return String.format("%s:%s", request.getServerName(), request.getServerPort());
    }

    public static String getContextPath(HttpServletRequest request) {
        final String contextPath = request.getContextPath();
        if ("/".equals(contextPath)) {
            // Invalid case, but happens for includes on Jetty: silently adapt it.
            return "";
        }
        return contextPath;
    }

    /**
     * 获取请求前缀 scheme://host:port/{contextPath}
     * <p>
     * <pre>
     *     eg:
     *     http://github.aifaq.me
     *     https://github.aifaq.me/example
     * </pre>
     *
     * @param request
     * @return
     */
    public static String getRequestPrefix(HttpServletRequest request) {
        return String.format("%s://%s%s", request.getScheme(), getRequestDomain(request), getContextPath(request));
    }
}
