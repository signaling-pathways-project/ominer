package edu.bcm.dldcc.big.nursa.util.filter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
 
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
 
/**
 * CMW 
 * 
 * smarter caching based on resource type
 * 
 * got this from http://www.digitalsanctuary.com/tech-blog/java/jboss/setting-cache-headers-from-jboss.html
 * 
 * 
 * The Class CacheHeaderFilter.
 * 
 * @author Devon Hillard
 */
public class CacheFilter implements Filter {
 
    /**
     * The Constant MILLISECONDS_IN_SECOND.
     */
    private static final int MILLISECONDS_IN_SECOND = 1000;
 
    /** The Constant POST_CHECK_VALUE. */
    private static final String POST_CHECK_VALUE = "post-check=";
 
    /** The Constant PRE_CHECK_VALUE. */
    private static final String PRE_CHECK_VALUE = "pre-check=";
 
    /** The Constant MAX_AGE_VALUE. */
    private static final String MAX_AGE_VALUE = "max-age=";
 
    /** The Constant ZERO_STRING_VALUE. */
    private static final String ZERO_STRING_VALUE = "0";
 
    /** The Constant NO_STORE_VALUE. */
    private static final String NO_STORE_VALUE = "no-store";
 
    /** The Constant NO_CACHE_VALUE. */
    private static final String NO_CACHE_VALUE = "no-cache";
 
    /** The Constant PRAGMA_HEADER. */
    private static final String PRAGMA_HEADER = "Pragma";
 
    /** The Constant CACHE_CONTROL_HEADER. */
    private static final String CACHE_CONTROL_HEADER = "Cache-Control";
 
    /** The Constant EXPIRES_HEADER. */
    private static final String EXPIRES_HEADER = "Expires";
 
    /** The Constant LAST_MODIFIED_HEADER. */
    private static final String LAST_MODIFIED_HEADER = "Last-Modified";
 
    /** The Constant CACHE_TIME_PARAM_NAME. */
    private static final String CACHE_TIME_PARAM_NAME = "CacheTime";
 
    /** The Static HTTP_DATE_FORMAT object. */
    private static final DateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
 
static {
HTTP_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
}
 
    /** The reply headers. */
    private String[][] mReplyHeaders = { {} };
 
    /** The cache time in seconds. */
    private Long mCacheTime = 0L;
 
    /**
     * Initializes the Servlet filter with the cache time and sets up the unchanging headers.
     * 
     * @param pConfig the config
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(final FilterConfig pConfig) {
        final ArrayList<String[]> newReplyHeaders = new ArrayList<String[]>();
        this.mCacheTime = Long.parseLong(pConfig.getInitParameter(CACHE_TIME_PARAM_NAME));
        if (this.mCacheTime > 0L) {
            newReplyHeaders.add(new String[] { CACHE_CONTROL_HEADER, MAX_AGE_VALUE + this.mCacheTime.longValue() });
            newReplyHeaders.add(new String[] { CACHE_CONTROL_HEADER, PRE_CHECK_VALUE + this.mCacheTime.longValue() });
            newReplyHeaders.add(new String[] { CACHE_CONTROL_HEADER, POST_CHECK_VALUE + this.mCacheTime.longValue() });
        } else {
            newReplyHeaders.add(new String[] { PRAGMA_HEADER, NO_CACHE_VALUE });
            newReplyHeaders.add(new String[] { EXPIRES_HEADER, ZERO_STRING_VALUE });
            newReplyHeaders.add(new String[] { CACHE_CONTROL_HEADER, NO_CACHE_VALUE });
            newReplyHeaders.add(new String[] { CACHE_CONTROL_HEADER, NO_STORE_VALUE });
        }
        this.mReplyHeaders = new String[newReplyHeaders.size()][2];
        newReplyHeaders.toArray(this.mReplyHeaders);
    }
 
    /**
     * Do filter.
     * 
     * @param pRequest the request
     * @param pResponse the response
     * @param pChain the chain
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ServletException the servlet exception
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
     *      javax.servlet.FilterChain)
     */
    public void doFilter(final ServletRequest pRequest, final ServletResponse pResponse, final FilterChain pChain)
            throws IOException, ServletException {
        // Apply the headers
        final HttpServletResponse httpResponse = (HttpServletResponse) pResponse;
        for (final String[] replyHeader : this.mReplyHeaders) {
            final String name = replyHeader[0];
            final String value = replyHeader[1];
            httpResponse.addHeader(name, value);
        }
        if (this.mCacheTime > 0L) {
            final long now = System.currentTimeMillis();
             final DateFormat httpDateFormat = (DateFormat) HTTP_DATE_FORMAT.clone();
            httpResponse.addHeader(LAST_MODIFIED_HEADER, httpDateFormat.format(new Date(now)));
            httpResponse.addHeader(EXPIRES_HEADER, httpDateFormat.format(new Date(now
                    + (this.mCacheTime.longValue() * MILLISECONDS_IN_SECOND))));
        }
        pChain.doFilter(pRequest, pResponse);
    }
 
    /**
     * Destroy all humans!
     * 
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
    }
 
}
