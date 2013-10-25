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

import groovy.json.JsonSlurper

import java.util.regex.Pattern

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Set of services used in the SearchController for views/search
 * 
 * @author ema
 *
 */

class SearchService {

    //Autowire the grails application bean
    def grailsApplication

    def configurationService

    //CharacterEncoding of query-String
    private characterEncoding = "UTF-8"

    //Name of search-cookie
    private searchCookieName = "searchParameters"

    private static facetsList = [
        "time_fct",
        "place_fct",
        "affiliate_fct",
        "keywords_fct",
        "language_fct",
        "type_fct",
        "sector_fct",
        "provider_fct"
    ]

    def transactional=false

    def getFacets(Map reqParameters, Map urlQuery, String key, int currentDepth){
        List facetValues = []
        def facets = urlQuery
        facets["facet"] = []
        if(reqParameters.get("facetValues[]").getClass().isArray()){
            reqParameters.get("facetValues[]").each{ facetValues.add(it) }
        }else{
            facetValues.add(reqParameters.get("facetValues[]").toString())
        }
        facetValues.each {
            def tmpVal = java.net.URLDecoder.decode(it.toString(), "UTF-8")
            List tmpSubVal = tmpVal.split("=")
            if(!facets["facet"].contains(tmpSubVal[0]))
                facets["facet"].add(tmpSubVal[0].toString())
            if(!facets[tmpSubVal[0]]){
                facets[tmpSubVal[0]]=[tmpSubVal[1]]
            }else
                facets[tmpSubVal[0]].add(tmpSubVal[1])
        }
        return facets
    }

    def facetValuesToString(facetValues){
        def res = ""
        def newFacetValues = []
        if(facetValues != null){
            if(facetValues.getClass().isArray()){
                newFacetValues = facetValues
            }else{
                newFacetValues.add(facetValues)
            }
            newFacetValues.each{ res += "&facetValues%5B%5D="+it }
        }

        return res
    }

    def buildMainFacetsUrl(GrailsParameterMap reqParameters, LinkedHashMap urlQuery, HttpServletRequest requestObject){
        def mainFacetsUrls = [:]
        facetsList.each {
            def searchQuery = (urlQuery["query"]) ? urlQuery["query"] : ""
            if(urlQuery["facet"] && urlQuery["facet"].contains(it)){
                mainFacetsUrls.put(it,requestObject.forwardURI+'?query='+searchQuery+"&offset=0&rows="+urlQuery["rows"]+facetValuesToString(reqParameters.get("facetValues[]")))
            }
            else{
                mainFacetsUrls.put(it,requestObject.forwardURI+'?query='+searchQuery+"&offset=0&rows="+urlQuery["rows"]+"&facets%5B%5D="+it+facetValuesToString(reqParameters.get("facetValues[]")))
            }
        }

        return mainFacetsUrls
    }

    def buildSubFacetsUrl(List facets, LinkedHashMap mainFacetsUrl, LinkedHashMap urlQuery){
        def res = [:]
        urlQuery["facet"].each{
            if(it!="grid_preview"){
                facets.each { x->
                    if(x.field == it && x.numberOfFacets>0){
                        res[x.field] = []
                        x.facetValues.each{ y->
                            //only proceed if the facetValue is of type main facet. Role facets will be ignored
                            if (mainFacetsUrl[x.field] != null) {
                                def tmpFacetValuesMap = ["fctValue": y.value,"url":"",cnt: y["count"],selected:""]
                                def tmpUrl = mainFacetsUrl[x.field]

                                //remove the facetvalue from the URL (the facet is selected)
                                if(mainFacetsUrl[x.field].contains(y["value"])){
                                    tmpUrl = tmpUrl.replaceAll("&facetValues%5B%5D="+x.field+"="+y["value"],"")
                                    tmpFacetValuesMap["url"] = tmpUrl
                                    tmpFacetValuesMap["selected"] = "selected"
                                }
                                //add the value from the link (the facet is deselected)
                                else{
                                    tmpUrl += "&facetValues%5B%5D="+x.field+"%3D"+y["value"]
                                    tmpFacetValuesMap["url"] = tmpUrl
                                }

                                res[x.field].add(tmpFacetValuesMap)
                            }
                        }
                    }
                }
            }
        }
        return res
    }


    def buildRoleFacetsUrl(List rolefacets, LinkedHashMap mainFacetsUrl, LinkedHashMap subFacetsUrl, LinkedHashMap urlQuery){
        def res = []
        def allBackendRolefacets = getRoleFacets()

        rolefacets.each { rf->
            if(rf.numberOfFacets>0){
                rf.facetValues.each{ fv->

                    def roleFacetDefinition = allBackendRolefacets.find {
                        it.name = rf.field
                    }

                    def tmpFacetValuesMap = ["parent": roleFacetDefinition.parent, "field":rf.field, "fctValue": fv.value,"url":"",cnt: fv["count"],selected:""]
                    def mainUrl = mainFacetsUrl.find{
                        rf.field.contains(it.key)
                    }

                    def tmpUrl = mainUrl.value

                    //remove the facetvalue from the URL (the role facet is selected)
                    if(tmpUrl.contains(rf.field+"="+fv["value"])){
                        tmpUrl = tmpUrl.replaceAll("&facetValues%5B%5D="+rf.field+"="+fv["value"],"")
                        tmpFacetValuesMap["url"] = tmpUrl
                        tmpFacetValuesMap["selected"] = "selected"

                        //remove also the role facets from the corresponding subFacetUrl

                        subFacetsUrl.each {  key, value  ->
                            if (rf.field.contains(key)) {
                                value.each { subUrl ->
                                    if (subUrl.fctValue.equals(fv['value'])) {
                                        def query = "&facetValues%5B%5D="+rf.field+"="+fv["value"]

                                        //replace the url in the subUrl Map
                                        def cleanedSubUrl = subUrl.url.replaceAll(query,"")
                                        subUrl.url = cleanedSubUrl
                                    }
                                }
                            }
                        }
                    }
                    //add the value to the link (the role facet is deselected)
                    else{
                        tmpUrl += "&facetValues%5B%5D="+rf.field+"%3D"+fv["value"]
                        tmpFacetValuesMap["url"] = tmpUrl
                    }

                    res.add(tmpFacetValuesMap)
                }
            }
        }

        return res
    }


    /**
     * 
     * Build the list of facets to be rendered in the non javascript version of search results
     * 
     * @param urlQuery
     * @return list of all facets filtered
     */
    def buildSubFacets(LinkedHashMap urlQuery){
        def emptyFacets = this.facetsList.clone()
        def res = []
        //We want only the first 10 facets
        urlQuery["facet.limit"] = 10

        urlQuery["facet"].each{
            if(it != "grid_preview"){
                emptyFacets.remove(it)
                def tmpUrlQuery = urlQuery.clone()
                tmpUrlQuery["rows"]=1
                tmpUrlQuery["offset"]=0
                tmpUrlQuery.remove(it)
                def apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, tmpUrlQuery)
                if(!apiResponse.isOk()){
                    log.error "Json: Json file was not found"
                    apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                }
                def jsonResp = apiResponse.getResponse()
                jsonResp.facets.each{ facet->
                    if(facet.field==it){
                        res.add(facet)
                    }
                }
            }
        }
        //fill the remaining empty facets
        emptyFacets.each{
            res.add([field: it, numberOfFacets: 0, facetValues: []])
        }
        return res
    }

    /**
     * Build the list of role facets to be rendered in the non javascript version of search results
     *
     * @param urlQuery
     * @return list of all facets filtered
     */
    def buildRoleFacets(LinkedHashMap urlQuery){
        def res = []
        def roleFacets = getRoleFacets()

        roleFacets.each { roleFacet ->
            if (urlQuery[roleFacet.parent] != null) {

                urlQuery[roleFacet.parent].each { facetValue ->
                    def searchUrl = '/search/facets/' + roleFacet.name

                    def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl() ,searchUrl , false, [query:facetValue])
                    if(!apiResponse.isOk()){
                        log.error "Json: Json file was not found"
                        apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                    }
                    def jsonResp = apiResponse.getResponse()

                    if (jsonResp.numberOfFacets > 0) {
                        res.add(jsonResp)
                    }
                }
            }
        }

        return res
    }

    def buildPagination(int resultsNumber, LinkedHashMap queryParameters, String getQuery){
        def res = [firstPg:null,lastPg:null,prevPg:null,nextPg:null]
        //if resultsNumber greater rows number no buttons else we can start to create the pagination
        def currentRows = queryParameters.rows.toInteger()
        def currentOffset = queryParameters.offset.toInteger()
        if(!getQuery.contains("rows"))
            getQuery += "&rows=20"
        if(resultsNumber>currentRows){
            //We are not at the first page
            if(currentOffset>0){
                def prevUrl
                def firstUrl
                def offsetPrev = currentOffset - currentRows
                def offsetFirst = 0
                if(getQuery.contains("offset")){
                    prevUrl = getQuery.replaceAll('offset='+currentOffset, 'offset='+offsetPrev)
                    firstUrl = getQuery.replaceAll('offset='+currentOffset, 'offset='+offsetFirst)
                }else{
                    prevUrl = getQuery+'&offset='+offsetPrev
                    firstUrl = getQuery+'&offset='+offsetFirst
                }
                res["firstPg"]= firstUrl
                res["prevPg"]= prevUrl
            }
            //We are not at the last page
            if(currentOffset+currentRows<resultsNumber){
                def offsetNext = currentOffset + currentRows
                def offsetLast = ((Math.ceil(resultsNumber/currentRows)*currentRows)-currentRows).toInteger()
                def nextUrl
                def lastUrl
                if(getQuery.contains("offset")){
                    nextUrl = getQuery.replaceAll('offset='+currentOffset, 'offset='+offsetNext)
                    lastUrl = getQuery.replaceAll('offset='+currentOffset, 'offset='+offsetLast)
                }else{
                    nextUrl = getQuery+'&offset='+offsetNext
                    lastUrl = getQuery+'&offset='+offsetLast
                }
                res["lastPg"]= lastUrl
                res["nextPg"]= nextUrl
            }
        }
        return res
    }

    def buildPaginatorOptions(LinkedHashMap queryMap){
        def pageFilter = [10, 20, 40, 60, 100]
        if(!pageFilter.contains(queryMap["rows"].toInteger()))
            pageFilter.add(queryMap["rows"].toInteger())
        return [pageFilter: pageFilter.sort(), pageFilterSelected: queryMap["rows"].toInteger(), sortResultsSwitch: queryMap["sort"]]
    }

    def buildClearFilter(LinkedHashMap urlQuery, String baseURI){
        def res = baseURI+'?'
        urlQuery.each{ key, value ->
            if(!key.toString().contains("facet") && !key.toString().contains("facetValues[]") && !key.toString().contains("fct")){
                res+='&'+key+'='+value
            }
        }
        return res
    }

    /**
     * 
     * Gives you back the HTML title with "strong" attributes trimmed to desired length
     * 
     * @param title
     * @param length
     * @return String title
     */
    def trimTitle(String title, int length){
        def matchesMatch = title =~ /(?m)<match>(.*?)<\/match>/
        def cleanTitle = title.replaceAll("<match>", "").replaceAll("</match>", "")
        def index = cleanTitle.length() > length ? cleanTitle.substring(0,length).lastIndexOf(" ") : -1
        def tmpTitle = index >= 0 ? cleanTitle.substring(0,index) + "..." : cleanTitle
        def replacedMatches = []
        StringBuilder replacementsRegex = new StringBuilder("(")
        if(matchesMatch.size()>0){
            matchesMatch.each{
                if (replacementsRegex.size() > 1) {
                    replacementsRegex.append("|")
                }
                replacementsRegex.append(Pattern.quote(it[1]))
            }
            replacementsRegex.append(")")
            tmpTitle = tmpTitle.replaceAll(replacementsRegex.toString(), '<strong>$1</strong>')
        }
        return tmpTitle
    }

    /**
     *
     * Gives you back the string trimmed to desired length
     *
     * @param text
     * @param length
     * @return String text trimmed
     */
    def trimString(String text, int length){
        if(text.length()>length)
            return text.substring(0, text.substring(0,length).lastIndexOf(" "))+"..."
        return text
    }

    /**
     * Generate Map that can be used to call Search on Search-Server
     * 
     * @param reqParameters
     * @return Map with keys used for Search on Search-Server
     */
    def convertQueryParametersToSearchParameters(Map reqParameters) {
        def urlQuery = [:]
        def numbersRangeRegex = /^[0-9]+$/

        if (reqParameters["query"]!=null && reqParameters["query"].length()>0){
            urlQuery["query"] = getMapElementOfUnsureType(reqParameters, "query", "*")
        }else{
            urlQuery["query"] = "*"
        }

        if (reqParameters.rows == null || !(reqParameters.rows=~ numbersRangeRegex)) {
            urlQuery["rows"] = 20.toInteger()
        } else {
            urlQuery["rows"] = getMapElementOfUnsureType(reqParameters, "rows", "20").toInteger()
        }
        reqParameters.rows = urlQuery["rows"]

        if (reqParameters.offset == null || !(reqParameters.offset=~ numbersRangeRegex)) {
            urlQuery["offset"] = 0.toInteger()
        } else {
            urlQuery["offset"] = getMapElementOfUnsureType(reqParameters, "offset", "0").toInteger()
        }
        reqParameters.offset = urlQuery["offset"]

        //<--input query=rom&offset=0&rows=20&facetValues%5B%5D=time_fct%3Dtime_61800&facetValues%5B%5D=time_fct%3Dtime_60100&facetValues%5B%5D=place_fct%3DItalien
        //-->output query=rom&offset=0&rows=20&facet=time_fct&time_fct=time_61800&facet=time_fct&time_fct=time_60100&facet=place_fct&place_fct=Italien
        if(reqParameters["facetValues[]"]){
            urlQuery = this.getFacets(reqParameters, urlQuery,"facet", 0)
        }

        if(reqParameters.get("facets[]")){
            urlQuery["facet"] = (!urlQuery["facet"])?[]:urlQuery["facet"]
            if(!urlQuery["facet"].contains(reqParameters.get("facets[]")))
                urlQuery["facet"].add(reqParameters.get("facets[]"))
        }

        if(reqParameters.minDocs) {
            urlQuery["minDocs"] = getMapElementOfUnsureType(reqParameters, "minDocs", "")
        }

        if(reqParameters["sort"] != null && ((reqParameters["sort"]=~ /^random_[0-9]+$/) || reqParameters["sort"]=='ALPHA_ASC' || reqParameters["sort"]=='ALPHA_DESC')){
            urlQuery["sort"] = getMapElementOfUnsureType(reqParameters, "sort", "")
        }else{
            if(urlQuery["query"]!="*"){
                urlQuery["sort"] = "RELEVANCE"
            }
        }

        if(reqParameters.viewType == null || (!(reqParameters.viewType=~ /^list$/) && !(reqParameters.viewType=~ /^grid$/))) {
            urlQuery["viewType"] = "list"
            reqParameters.viewType = "list"
        } else {
            urlQuery["viewType"] = getMapElementOfUnsureType(reqParameters, "viewType", "")
        }

        if(reqParameters.isThumbnailFiltered){
            urlQuery["facet"] = (!urlQuery["facet"])?[]:urlQuery["facet"]
            if(!urlQuery["facet"].contains("grid_preview") && reqParameters.isThumbnailFiltered == "true"){
                urlQuery["facet"].add("grid_preview")
                urlQuery["grid_preview"] = "true"
            }
        }

        if(!urlQuery["facet"]){
            urlQuery["facet"] = []
        }
        urlQuery["facet"].add("affiliate_fct_involved_normdata")
        urlQuery["facet"].add("affiliate_fct_subject")

        return urlQuery
    }

    /**
     * Generate Map that can be used to call Autocomplete and Search Facets on Search-Server
     *
     * @param reqParameters
     * @return Map with keys used for Search on Search-Server
     */
    def convertQueryParametersToSearchFacetsParameters(Map reqParameters) {
        def urlQuery = [:]
        def numbersRangeRegex = /^[0-9]+$/

        if (reqParameters["searchQuery"]!=null && reqParameters["searchQuery"].length()>0){
            urlQuery["searchQuery"] = getMapElementOfUnsureType(reqParameters, "searchQuery", "*")
        }else{
            urlQuery["searchQuery"] = "*"
        }

        if (reqParameters["query"]!=null && reqParameters["query"].length()>0){
            urlQuery["query"] = getMapElementOfUnsureType(reqParameters, "query", "*")
        }else{
            urlQuery["query"] = "*"
        }

        //<--input query=rom&offset=0&rows=20&facetValues%5B%5D=time_fct%3Dtime_61800&facetValues%5B%5D=time_fct%3Dtime_60100&facetValues%5B%5D=place_fct%3DItalien
        //-->output query=rom&offset=0&rows=20&facet=time_fct&time_fct=time_61800&facet=time_fct&time_fct=time_60100&facet=place_fct&place_fct=Italien
        if(reqParameters["facetValues[]"]){
            urlQuery = this.getFacets(reqParameters, urlQuery,"facet", 0)
        }

        if(reqParameters.get("facets[]")){
            urlQuery["facet"] = (!urlQuery["facet"])?[]:urlQuery["facet"]
            if(!urlQuery["facet"].contains(reqParameters.get("facets[]")))
                urlQuery["facet"].add(reqParameters.get("facets[]"))
        }

        if(reqParameters["sortDesc"] != null && ((reqParameters["sortDesc"]== "true") || (reqParameters["sortDesc"]== "false"))){
            urlQuery["sortDesc"] = getMapElementOfUnsureType(reqParameters, "sortDesc", "")
        }

        if(reqParameters.isThumbnailFiltered){
            urlQuery["facet"] = (!urlQuery["facet"])?[]:urlQuery["facet"]
            if(!urlQuery["facet"].contains("grid_preview") && reqParameters.isThumbnailFiltered == "true"){
                urlQuery["facet"].add("grid_preview")
                urlQuery["grid_preview"] = "true"
            }
        }

        //We ask for a maximum of 301 facets
        urlQuery["facet.limit"] = 301

        return urlQuery
    }

    /**
     * Utility-method to fix a groovy-inconvenience. Parameter map values can either be a single String or
     * an Array of Strings (e.g. if the parameter was defined twice in the URL). To handle this, get the 
     * parameters over this method.
     * @param map The parameter map
     * @param elementName The map key
     * @param defaultValue The default value if no value was found for the key
     * @return The value or the defaultValue if no value was found
     */
    private String getMapElementOfUnsureType(map, elementName, defaultValue){
        if (map[elementName]?.class.isArray()){
            if(map[elementName].size() > 0){
                return map[elementName][0]
            } else {
                return defaultValue
            }
        }else{
            if(map[elementName]){
                return map[elementName]
            } else {
                return defaultValue
            }
        }

    }

    /**
     * Generate Map that contains GET-parameters used for search-request by ddb-next.
     * 
     * @param reqParameters
     * @return Map with keys used for search-request by ddb-next.
     */
    def getSearchGetParameters(Map reqParameters) {
        def searchParams = [:]
        def requiredParams = [
            "query",
            "offset",
            "rows",
            "sort",
            "viewType",
            "clustered",
            "isThumbnailFiltered",
            "facetValues[]",
            "facets[]"
        ]
        for (entry in reqParameters) {
            if (requiredParams.contains(entry.key)) {
                searchParams[entry.key] = entry.value
            }
        }
        return searchParams
    }

    /**
     * Generate Map that contains GET-parameters used for item-detail-request by ddb-next.
     * 
     * @param reqParameters
     * @return Map with keys used for item-detail-request by ddb-next.
     */
    def getSearchCookieParameters(Map reqParameters) {
        def searchCookieParameters = [:]
        def requiredParams = [
            "query",
            "offset",
            "rows",
            "sort",
            "viewType",
            "clustered",
            "isThumbnailFiltered",
            "facetValues[]",
            "facets[]",
            "firstHit",
            "lastHit",
            "keepFilters"
        ]
        for (entry in reqParameters) {
            if (requiredParams.contains(entry.key)) {
                searchCookieParameters[entry.key] = entry.value
            }
        }
        return searchCookieParameters
    }

    /**
     * 
     * Used in FacetsController gives you back an array containing the following Map: {facet value, localized facet value, count results} 
     * 
     * @param facets list of facets fetched from the backend
     * @param fctName name of the facet field required
     * @param numberOfElements number of elements to return
     * @return List of Map
     */
    def getSelectedFacetValues(net.sf.json.JSONObject facets, String fctName, int numberOfElements, String matcher, Locale locale){
        def res = [type: fctName, values: []]
        def allFacetFilters = configurationService.getFacetsFilter()

        int max = (numberOfElements != -1 && facets.numberOfFacets>numberOfElements)?numberOfElements:facets.numberOfFacets
        for(int i=0;i<max;i++){

            //Check if facet value has to be filtered
            boolean filterFacet = false
            for(int k=0; k<allFacetFilters.size(); k++){
                if(fctName == allFacetFilters[k].facetName && facets.facetValues[i].value.toString() == allFacetFilters[k].filter){
                    filterFacet = true
                    break
                }
            }

            if(!filterFacet){
                res.values.add([value: facets.facetValues[i].value, localizedValue: this.getI18nFacetValue(fctName, facets.facetValues[i].value.toString()), count: String.format(locale, "%,d", facets.facetValues[i].count.toInteger())])
            }
        }
        return res
    }

    /**
     *
     * Used in FacetsController gives you back an array containing the following Map: {facet value, localized facet value, count results}
     *
     * @param facets list of facets fetched from the backend
     * @param fctName name of the facet field required
     * @param numberOfElements number of elements to return
     * @return List of Map
     */
    def getSelectedFacetValuesFromOldApi(List facets, String fctName, int numberOfElements, String matcher, Locale locale){
        def res = [type: fctName, values: []]
        def allFacetFilters = grailsApplication.config.ddb.backend.facets.filter

        facets.each{
            if(it.field==fctName){
                int max = (numberOfElements != -1 && it.facetValues.size()>numberOfElements)?numberOfElements:it.facetValues.size()
                for(int i=0;i<max;i++){
                    //Check if facet value has to be filtered
                    boolean filterFacet = false
                    for(int k=0; k<allFacetFilters.size(); k++){
                        if(fctName == allFacetFilters[k].facetName && it.facetValues[i].value.toString() == allFacetFilters[k].filter){
                            filterFacet = true
                            break
                        }
                    }

                    if(!filterFacet){
                        if(matcher && this.getI18nFacetValue(fctName, it.facetValues[i].value.toString()).toLowerCase().contains(matcher.toLowerCase())){
                            def localizedValue = this.getI18nFacetValue(fctName, it.facetValues[i].value.toString())
                            def firstIndexMatcher = localizedValue.toLowerCase().indexOf(matcher.toLowerCase())
                            localizedValue = localizedValue.substring(0, firstIndexMatcher)+"<strong>"+localizedValue.substring(firstIndexMatcher,firstIndexMatcher+matcher.size())+"</strong>"+localizedValue.substring(firstIndexMatcher+matcher.size(),localizedValue.size())
                            res.values.add([value: it.facetValues[i].value, localizedValue: localizedValue, count: String.format(locale, "%,d", it.facetValues[i].count.toInteger())])
                        }else if(!matcher)
                            res.values.add([value: it.facetValues[i].value, localizedValue: this.getI18nFacetValue(fctName, it.facetValues[i].value.toString()), count: String.format(locale, "%,d", it.facetValues[i].count.toInteger())])
                    }
                }
            }
        }
        return res
    }

    /**
     * 
     * Gives you back the passed facet value internationalized
     * 
     * @param facetName
     * @param facetValue
     * @return String i18n facet value
     */
    def getI18nFacetValue(facetName, facetValue){

        def appCtx = grailsApplication.getMainContext()

        def res = ""

        if(facetName == 'affiliate_fct' || facetName == 'keywords_fct' || facetName == 'place_fct' || facetName == 'provider_fct'){
            res = facetValue
        }
        else if(facetName == 'type_fct'){
            res = appCtx.getMessage('ddbnext.type_fct_'+facetValue, null, LocaleContextHolder.getLocale() )
        }
        else if(facetName == 'time_fct'){
            res = appCtx.getMessage('ddbnext.time_fct_'+facetValue, null, LocaleContextHolder.getLocale())
        }
        else if(facetName == 'language_fct'){
            res = appCtx.getMessage('ddbnext.language_fct_'+facetValue, null, LocaleContextHolder.getLocale())
        }
        else if(facetName == 'sector_fct'){
            res = appCtx.getMessage('ddbnext.sector_fct_'+facetValue, null, LocaleContextHolder.getLocale())
        }
        return res
    }

    /**
     * Create Cookie with search-parameters for use on other pages
     * convert HashMap containing parameters to JSON
     * 
     * @param reqParameters request-parameters
     * @return Cookie with search-parameters
     */
    def createSearchCookie( HttpServletRequest requestObject, Map reqParameters, Map additionalParams) {
        //Create Cookie with search-parameters for use on other pages
        //convert HashMap containing parameters to JSON
        if (additionalParams) {
            for (entry in additionalParams) {
                reqParameters[entry.key] = entry.value
            }
        }
        Map paramMap = getSearchCookieParameters(reqParameters)
        def jSonObject = new JSONObject()
        for (entry in paramMap) {
            if (entry.value instanceof String[]) {
                for (entry1 in entry.value) {
                    jSonObject.accumulate(entry.key, URLEncoder.encode(entry1, characterEncoding))
                }
            }
            else if (entry.value instanceof String){
                jSonObject.put(entry.key, URLEncoder.encode(entry.value, characterEncoding))
            }
            else {
                jSonObject.put(entry.key, entry.value)
            }
        }
        def cookie = new Cookie(searchCookieName + requestObject.contextPath, jSonObject.toString())
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
            if (cookie.name == searchCookieName + requestObject.contextPath) {
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
                    entry.value = URLDecoder.decode(entry.value, characterEncoding)
                }
                else if (entry.value instanceof List) {
                    String[] arr = new String[entry.value.size()]
                    def i = 0
                    for (entry1 in entry.value) {
                        if (entry1 instanceof String) {
                            entry1 = URLDecoder.decode(entry1, characterEncoding)
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
     * Converts the params list received from the frontend during a request to get all the facets to be displayed in the flyout widget.
     * 
     * @param reqParameters the params variable containing all the req parameters
     * @return a map containing all the converted request parameters ready to be submitted to the related service to fetch the right facets values
     */
    def convertFacetQueryParametersToFacetSearchParameters(Map reqParameters) {
        def urlQuery = [:]

        if (reqParameters.searchQuery == null)
            urlQuery["query"] = '*'
        else urlQuery["query"] = reqParameters.searchQuery

        if (reqParameters.rows == null || reqParameters.rows == -1)
            urlQuery["rows"] = 1
        else urlQuery["rows"] = reqParameters.rows

        if (reqParameters.offset == null)
            urlQuery["offset"] = 0
        else urlQuery["offset"] = reqParameters.offset

        //<--input query=rom&offset=0&rows=20&facetValues%5B%5D=time_fct%3Dtime_61800&facetValues%5B%5D=time_fct%3Dtime_60100&facetValues%5B%5D=place_fct%3DItalien
        //-->output query=rom&offset=0&rows=20&facet=time_fct&time_fct=time_61800&facet=time_fct&time_fct=time_60100&facet=place_fct&place_fct=Italien
        if(reqParameters["facetValues[]"]){
            urlQuery = getFacets(reqParameters, urlQuery,"facet", 0)
        }

        if(reqParameters.get("name")){
            urlQuery["facet"] = (!urlQuery["facet"])?[]:urlQuery["facet"]
            if(!urlQuery["facet"].contains(reqParameters.get("name")))
                urlQuery["facet"].add(reqParameters.get("name"))
        }


        if(reqParameters.isThumbnailFiltered){
            urlQuery["facet"] = (!urlQuery["facet"])?[]:urlQuery["facet"]
            if(!urlQuery["facet"].contains("grid_preview") && reqParameters.isThumbnailFiltered == "true"){
                urlQuery["facet"].add("grid_preview")
                urlQuery["grid_preview"] = "true"
            }
        }

        //We ask for a maximum of 1000 facets
        urlQuery["facet.limit"] = 1000

        return urlQuery
    }

    /**
     * Check if searchCookie contains keepFilters=true.
     * If yes, expand requestParameters with facets and return true.
     * Otherwise return false
     * 
     * @param cookieMap
     * @param requestParameters
     * @return boolean
     */
    def checkPersistentFacets(Map cookieMap, Map requestParameters, Map additionalParams) {
        if (cookieMap["keepFilters"] && cookieMap["keepFilters"] == "true") {
            additionalParams["keepFilters"] = "true"
            if (!requestParameters["facetValues[]"] && cookieMap["facetValues[]"]) {
                requestParameters["facetValues[]"] = cookieMap["facetValues[]"]
                return true
            }
            else {
                return false
            }
        }
        else {
            return false
        }
    }

    def checkAndReplaceMediaTypeImages(def searchResult){
        searchResult.results.docs.each {
            def preview = it.preview
            if(preview.thumbnail == null ||
            preview.thumbnail instanceof net.sf.json.JSONNull ||
            preview.thumbnail.toString().trim().isEmpty() ||
            (preview.thumbnail.toString().startsWith("http://content") &&
            preview.thumbnail.toString().contains("/placeholder/search_result_"))
            ){
                def mediaTypes = []
                if(preview.media instanceof String){
                    mediaTypes.add(preview.media)
                }else{
                    mediaTypes.addAll(preview.media)
                }
                def mediaType = mediaTypes[0]
                if(mediaType != null){
                    mediaType = mediaType.toString().toLowerCase()
                }
                if(mediaType != "audio" &&
                mediaType != "image" &&
                mediaType != "institution" &&
                mediaType != "sonstiges" &&
                mediaType != "text" &&
                mediaType != "unknown" &&
                mediaType != "video"){
                    mediaType = "unknown"
                }
                def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
                preview.thumbnail = g.resource("dir": "images", "file": "/placeholder/search_result_media_"+mediaType+".png").toString()
            }
        }
        return searchResult
    }

    /**
     * Returns all role facets from the backend.
     * The method requests all available facets and then filter for the role attribute.
     * 
     * A role facet looks like this:
     * [name:affiliate_fct_involved, parent:affiliate_fct, paths:[], role:involved, searchType:TEXT, sortType:null, displayType:TECHNICAL, position:-1]
     * 
     * @return a list of all role facets in the json format
     */
    def getRoleFacets() {
        def res = []

        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(),'/search/facets/')
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(request)
        }

        def resultsItems = apiResponse.getResponse()
        resultsItems.each {
            if (it.role != 'null') {
                //FIXME set sortType '' to avoid "net.sf.json.JSONException: Object is null" exception
                it.sortType = ''
                res.add(it)
            }
        }
        return res
    }

}
