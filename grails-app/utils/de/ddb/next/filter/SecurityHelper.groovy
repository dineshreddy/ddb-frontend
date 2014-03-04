/*
 * Copyright (C) 2014 FIZ Karlsruhe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.ddb.next.filter

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

import org.apache.commons.logging.LogFactory
import org.ccil.cowan.tagsoup.Parser

import de.ddb.next.exception.InvalidUrlException


/**
 * Helper class to sanitize all incoming request data (parameters, cookies, etc) from malicious code.
 * Mainly script tags used for XSS attacks.
 * 
 * @author hla
 */
class SecurityHelper {

    private def log = LogFactory.getLog(this.class)

    /**
     * Cleans a ServletRequest from malicious code. The request object must be of type DdbRequestWrapper, because
     * the original HttpServletRequest does not allow manipulation of the request parameters.
     * 
     * @param request The wrapped request object
     */
    void sanitizeRequest(ServletRequestWrapper request, HttpServletResponse response){
        Parser tagsoupParser = new Parser()
        XmlSlurper slurper = new XmlSlurper(tagsoupParser)

        sanitizeRequestUrl(request, response)
        sanitizeRequestParameters(request, slurper)
        sanitizeRequestCookies(request, slurper)
        sanitizeRequestHeaders(request)
        addReponseSecurityHeaders(request, response)
    }

    private void sanitizeRequestUrl(ServletRequestWrapper request, HttpServletResponse response) {
        String requestUri = request.getRequestURI()

        if(requestUri.contains("<") ||
        requestUri.contains(">") ||
        requestUri.contains("%3E") ||
        requestUri.contains("%3C")) {
            log.warn "sanitizeRequestUrl(): possible xss attempt over url: '"+requestUri+"', redirecting to '"+request.getContextPath()+"'"
            throw new InvalidUrlException()
        }
    }

    /**
     * Cleans the "Host" header of a servlet request from characters that are definetly not allowed there
     * (e.g. <, >, -, ...) thus preventing code-injection over this field
     * 
     * @param request The wrapped request object
     */
    private void sanitizeRequestHeaders(ServletRequestWrapper request) {
        request.getHeaderNames().each {
            if(it.toLowerCase() == "host" || it.toLowerCase() == "referer"){
                String header = request.getHeader(it)
                if(header.contains(">") || header.contains("<") || header.contains("%3C") || header.contains("%3E") ){
                    log.warn "sanitizeRequestHeaders(): possible xss attempt over '" + it + "' header: '"+header+"'"
                }
                header = header.replace(">", "")
                header = header.replace("<", "")
                header = header.replace("%3C", "") // <
                header = header.replace("%3E", "") // >
                header = header.replace("\"", "")
                header = header.replace("'", "")
                request.setHeader(it, header)
            }
        }
    }

    /**
     * Cleans the request parameters from malicious code
     * 
     * @param parameterMap The parameter map
     * @param slurper The XmlSlurper instance
     */
    private void sanitizeRequestParameters(ServletRequestWrapper request, XmlSlurper slurper){
        Map<String,String[]> parameterMap = request.getParameterMap()

        String[] keys = parameterMap.keySet().toArray()
        for (int i=0; i<keys.length; i++) {

            String key = keys[i]
            String[] valueArray = parameterMap.get(key)
            for (int j=0; j<valueArray.length; j++) {
                println key
                String parsedText = slurper.parseText(valueArray[j]).text()
                if(valueArray[j] != parsedText){
                    log.warn "sanitizeRequestParameters(): possible xss attempt over request parameters: '"+valueArray[j]+"' -> '"+parsedText+"'"
                }
                valueArray[j] = parsedText
            }

            parameterMap.put(key, valueArray)
        }
    }

    /**
     * Cleans the request cookies from malicious code
     * 
     * @param cookies
     * @param slurper The cookie array
     */
    private void sanitizeRequestCookies(ServletRequestWrapper request, XmlSlurper slurper){
        Cookie[] cookies = request.getCookies()

        if(cookies){
            for (int i=0; i<cookies.length; i++){
                Cookie cookie = cookies[i]
                String decodedCookieValue = URLDecoder.decode(cookie.value, "UTF-8")
                String parsedCookieValue = slurper.parseText(decodedCookieValue).text()
                if(decodedCookieValue != parsedCookieValue){
                    log.warn "sanitizeRequestCookies(): possible xss attempt over cookie: '"+decodedCookieValue+"' -> '"+parsedCookieValue+"'"
                }
                cookie.setValue(parsedCookieValue)
            }
        }
    }

    private void addReponseSecurityHeaders(ServletRequestWrapper request, HttpServletResponse response) {
        response.addHeader("X-Frame-Options", "SAMEORIGIN")
        response.addHeader("X-Content-Type-Options", "nosniff")
    }
}
