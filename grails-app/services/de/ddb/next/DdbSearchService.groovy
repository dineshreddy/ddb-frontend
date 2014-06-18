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
package de.ddb.next


import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper

import de.ddb.common.constants.SearchParamEnum

class DdbSearchService {

    //Autowire the grails application bean
    def grailsApplication

    def configurationService
    def searchService

    //CharacterEncoding of query-String
    private static final String CHARACTER_ENCODING = "UTF-8"

    //Name of search-cookie
    private static final String SEARCH_COOKIE_NAME = "searchParameters"

    /**
     * Create Cookie with search-parameters for use on other pages
     * convert HashMap containing parameters to JSON
     *
     * @param requestObject request object
     * @param reqParameters request-parameters
     * @param additionalParams additional params
     * @param searchType item, entity or institution
     *
     * @return Cookie with search-parameters
     */
    def createSearchCookie(HttpServletRequest requestObject, Map reqParameters, Map additionalParams, Map oldCookieValues=[:], SearchTypeEnum searchType) {
        def jSonObject = new JSONObject()

        //Create Cookie with search-parameters for use on other pages
        //convert HashMap containing parameters to JSON
        if (additionalParams) {
            for (entry in additionalParams) {
                reqParameters[entry.key] = entry.value
            }
        }

        //restore oldCookie values, omit the facetValues of the current searchType
        def searchParams
        for (cookie in requestObject.cookies) {
            if (cookie.name == SEARCH_COOKIE_NAME + requestObject.contextPath) {
                searchParams = cookie.value
            }
        }
        if (searchParams) {
            def jSonSlurper = new JsonSlurper()
            try{
                jSonObject = jSonSlurper.parseText(searchParams)
            }catch(Exception e){
                log.error "getSearchCookieAsMap(): Could not parse search params: "+searchParams, e
            }
        }
        jSonObject.remove(searchType.getName() + "_" + SearchParamEnum.FACETVALUES.getName())


        //set actual request params in the cookie
        Map paramMap = searchService.getSearchCookieParameters(reqParameters)
        for (entry in paramMap) {
            def key = entry.key

            //special handling for the facetValues[] parameter. Add the searchType as a prefix
            if (key.contains(SearchParamEnum.FACETVALUES.getName())) {
                key = searchType.getName() + "_" + key
            }

            if (entry.value instanceof String[]) {
                //First reset than accumulate!
                jSonObject.remove(key)

                for (entry1 in entry.value) {
                    jSonObject.accumulate(key, URLEncoder.encode(entry1, CHARACTER_ENCODING))
                }
            }
            else if (entry.value instanceof String){
                jSonObject.put(key, URLEncoder.encode(entry.value, CHARACTER_ENCODING))
            }
            else {
                jSonObject.put(key, entry.value)
            }
        }

        def cookie = new Cookie(SEARCH_COOKIE_NAME + requestObject.contextPath, jSonObject.toString())
        //Set the cookie path to "/", so all search pages (items, entities, institutions) are using the same cookie!
        cookie.path = "/"
        cookie.maxAge = -1
        return cookie
    }

    /**
     * Reads the cookie containing the search-Parameters and fills the values in Map.
     *
     * @param request
     * @return Map with key-values from cookie
     */
    def getSearchCookieAsMap(HttpServletRequest requestObject, Cookie[] cookies) {
        def searchParams
        def searchParamsMap = [:]
        for (cookie in cookies) {
            if (cookie.name == SEARCH_COOKIE_NAME + requestObject.contextPath) {
                searchParams = cookie.value
            }
        }
        if (searchParams) {
            def jSonSlurper = new JsonSlurper()
            try{
                searchParamsMap = jSonSlurper.parseText(searchParams)
            }catch(Exception e){
                log.error "getSearchCookieAsMap(): Could not parse search params: "+searchParams, e
            }
            for (entry in searchParamsMap) {
                if (entry.value instanceof String) {
                    entry.value = URLDecoder.decode(entry.value, CHARACTER_ENCODING)
                }
                else if (entry.value instanceof List) {
                    String[] arr = new String[entry.value.size()]
                    def i = 0
                    for (entry1 in entry.value) {
                        if (entry1 instanceof String) {
                            entry1 = URLDecoder.decode(entry1, CHARACTER_ENCODING)
                        }
                        arr[i] = entry1
                        i++
                    }
                    entry.value = arr
                }
            }
        }
        return searchParamsMap
    }

    /**
     * Check if its not an ajax request and searchCookie contains keepFilters=true.
     * If yes, expand requestParameters with facets and return true.
     * Otherwise return false
     *
     * @param cookieMap the searchParameters cookie map
     * @param requestParameters the request parameters
     * @param additionalParams additional request parameters
     * @param searchType the search type (item, entity, institution)
     *
     * @return boolean <code>true</code>
     */
    def checkPersistentFacets(Map cookieMap, Map requestParameters, Map additionalParams, SearchTypeEnum searchType) {
        def retVal = false

        //Check persistent facets neither for ajax requests or for requests containing clearFilter params
        if(! (requestParameters["reqType"] == "ajax") && !(requestParameters["clearFilter"] == "true")){

            //Check if the keepfilter flag is set in the cookie
            if (cookieMap[SearchParamEnum.KEEPFILTERS.getName()] && cookieMap[SearchParamEnum.KEEPFILTERS.getName()] == "true") {
                additionalParams[SearchParamEnum.KEEPFILTERS.getName()] = "true"

                //The cookie key for the facetValue is searchType dependent!
                def facetValueCookieKey = searchType.getName() + "_" +SearchParamEnum.FACETVALUES.getName()
                if (!requestParameters[SearchParamEnum.FACETVALUES.getName()] && cookieMap[facetValueCookieKey]) {
                    requestParameters[SearchParamEnum.FACETVALUES.getName()] = cookieMap[facetValueCookieKey]
                    retVal = true
                }
            }
        }

        return retVal
    }
}