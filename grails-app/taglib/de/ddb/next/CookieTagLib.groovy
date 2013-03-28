/*
 * Copyright (C) 2013 FIZ Karlsruhe
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
package de.ddb.next

import javax.servlet.http.Cookie

class CookieTagLib {
    def searchService
    def AMP = "&amp;"
    def LT = "&lt;"
    def GT = "&gt;"
    def QUOTE = "&quot;"

    def getCookieFieldValue = {
        attrs, body ->
        def map = [:]
        if (params && params.controller == "search" && params.action == "results") {
            def cookies = new Cookie[1]
            cookies[0] = searchService.createSearchCookie(params, null)
            map = searchService.getSearchCookieAsMap(cookies)
        }
        else {
            map = searchService.getSearchCookieAsMap(request.cookies)
        }
        out << escapeEntities(map[attrs.fieldname])
    }
    
    /** Return the given string with all HTML entities escaped into their 
     * HTML equivalent. 
     * @param text String containing unsafe characters. 
     * @return <var>text</var> with characters turned into HTML entities. 
     */ 
    public String escapeEntities(String text) { 
        if (text == null) {
            text = ""
        } 
        String trim = text.trim() 
        char[] c = trim.toCharArray()
        
        StringBuffer buffer = new StringBuffer() 
        def i = -1 
        while (++i < c.length) { 
            if (c[i]=='&') {
                buffer.append(AMP)
            }
            else if (c[i]=='<') {
                buffer.append(LT)
            }
            else if(c[i]=='>') {
                buffer.append(GT)
            }
            else if(c[i]=='"') {
                buffer.append(QUOTE)
            }
            else {
                buffer.append(c[i]) 
            }
        } 
        return buffer.toString() }
}
