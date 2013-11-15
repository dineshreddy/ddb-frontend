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

import groovy.json.*
import groovyx.net.http.HTTPBuilder

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.next.exception.BadRequestException

class SearchController {

    static defaultAction = "results"

    def searchService
    def configurationService
    def cultureGraphService

    def results() {
        try {
            def searchParametersMap = searchService.getSearchCookieAsMap(request, request.cookies)
            def additionalParams = [:]
            if (searchService.checkPersistentFacets(searchParametersMap, params, additionalParams)) {
                redirect(controller: "search", action: "results", params: params)
            }
            def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
            def firstLastQuery = searchService.convertQueryParametersToSearchParameters(params)
            def mainFacetsUrl = searchService.buildMainFacetsUrl(params, urlQuery, request)

            def apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, urlQuery)
            if(!apiResponse.isOk()){
                log.error "Json: Json file was not found"
                apiResponse.throwException(request)
            }
            def resultsItems = apiResponse.getResponse()

            if(resultsItems["randomSeed"]){
                urlQuery["randomSeed"] = resultsItems["randomSeed"]
                firstLastQuery["sort"] = resultsItems["randomSeed"]
                if (!params.sort) {
                    params.sort = urlQuery["randomSeed"]
                }
            }

            if (resultsItems != null && resultsItems["numberOfResults"] != null && (Integer)resultsItems["numberOfResults"] > 0) {
                //check for lastHit and firstHit
                //firstHit
                firstLastQuery["rows"] = 1
                firstLastQuery["offset"] = 0
                apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, firstLastQuery)
                if(!apiResponse.isOk()){
                    log.error "Json: Json file was not found"
                    apiResponse.throwException(request)
                }
                def firstHit = apiResponse.getResponse()
                if (firstHit != null && firstHit["numberOfResults"] != null && (Integer)firstHit["numberOfResults"] > 0) {
                    params["firstHit"] = firstHit["results"]["docs"][0].id
                }

                //lastHit
                //Workaround, find id of last hit when calling last hit.
                //Set id to "lasthit" to signal ItemController to find id of lasthit.
                params["lastHit"] = "lasthit"

            }

            //Replacing the mediatype images when not coming from backend server
            resultsItems = searchService.checkAndReplaceMediaTypeImages(resultsItems)

            //create cookie with search parameters
            response.addCookie(searchService.createSearchCookie(request, params, additionalParams))

            //Calculating results details info (number of results in page, total results number)
            def resultsOverallIndex = (urlQuery["offset"].toInteger()+1)+' - ' +
                    ((urlQuery["offset"].toInteger()+
                    urlQuery["rows"].toInteger()>resultsItems.numberOfResults)? resultsItems.numberOfResults:urlQuery["offset"].toInteger()+urlQuery["rows"].toInteger())

            def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))

            //Calculating results pagination (previous page, next page, first page, and last page)
            def page = ((int)Math.floor(urlQuery["offset"].toInteger()/urlQuery["rows"].toInteger())+1).toString()
            def totalPages = (Math.ceil(resultsItems.numberOfResults/urlQuery["rows"].toInteger()).toInteger())
            def totalPagesFormatted = String.format(locale, "%,d", totalPages.toInteger())

            def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
            def numberOfResultsFormatted = String.format(locale, "%,d", resultsItems.numberOfResults.toInteger())

            def queryString = request.getQueryString()

            if(!queryString?.contains("sort=random") && urlQuery["randomSeed"])
                queryString = queryString+"&sort="+urlQuery["randomSeed"]

            def gndItems = getGndItems(resultsItems, page)

            if(params.reqType=="ajax"){
                def resultsHTML = ""
                resultsHTML = g.render(template:"/search/resultsList",model:[results: resultsItems.results["docs"], gndResults: gndItems, viewType:  urlQuery["viewType"],confBinary: request.getContextPath(),
                    offset: params["offset"]]).replaceAll("\r\n", '')
                def jsonReturn = [results: resultsHTML,
                    resultsPaginatorOptions: resultsPaginatorOptions,
                    resultsOverallIndex:resultsOverallIndex,
                    page: page,
                    totalPages: totalPagesFormatted,
                    paginationURL: searchService.buildPagination(resultsItems.numberOfResults, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")),
                    numberOfResults: numberOfResultsFormatted,
                    offset: params["offset"]
                ]
                render (contentType:"text/json"){jsonReturn}
            }else{
                //We want to build the subfacets urls only if a main facet has been selected
                def keepFiltersChecked = ""
                if (searchParametersMap["keepFilters"] && searchParametersMap["keepFilters"] == "true") {
                    keepFiltersChecked = "checked=\"checked\""
                }
                def subFacetsUrl = [:]
                def selectedFacets = searchService.buildSubFacets(urlQuery)
                if(urlQuery["facet"]){
                    subFacetsUrl = searchService.buildSubFacetsUrl(selectedFacets, mainFacetsUrl, urlQuery)
                }

                def roleFacetsUrl = [:]
                def selectedRoleFacets = searchService.buildRoleFacets(urlQuery)
                if(urlQuery["facet"]){
                    roleFacetsUrl = searchService.buildRoleFacetsUrl(selectedRoleFacets, mainFacetsUrl, subFacetsUrl, urlQuery)
                }

                render(view: "results", model: [
                    title: urlQuery["query"],
                    results: resultsItems,
                    gndResults: gndItems,
                    isThumbnailFiltered: params.isThumbnailFiltered,
                    clearFilters: searchService.buildClearFilter(urlQuery, request.forwardURI),
                    correctedQuery:resultsItems["correctedQuery"],
                    viewType:  urlQuery["viewType"],
                    facets: [selectedFacets: selectedFacets, mainFacetsUrl: mainFacetsUrl, subFacetsUrl: subFacetsUrl, selectedRoleFacets: selectedRoleFacets, roleFacetsUrl: roleFacetsUrl],
                    resultsPaginatorOptions: resultsPaginatorOptions,
                    resultsOverallIndex:resultsOverallIndex,
                    page: page,
                    totalPages: totalPages,
                    paginationURL: searchService.buildPagination(resultsItems.numberOfResults, urlQuery, request.forwardURI+'?'+queryString),
                    numberOfResultsFormatted: numberOfResultsFormatted,
                    offset: params["offset"],
                    keepFiltersChecked: keepFiltersChecked
                ])
            }
        } catch (BadRequestException e) {
            //BadRequestException corresponds to 400-Error,
            //in this case will be caused by invalid query-syntax
            log.error("Bad Request: ${e.getMessage()}")
            def errors = []
            errors.add("ddbnext.Error_Invalid_Search_Query")
            render(view: "/message/message", model: [errors: errors])
        }
    }

    private def getGndItems(resultItems, page) {
        def gndItems = null
        if(configurationService.isCulturegraphFeaturesEnabled()){

            if(page == "1"){

                def gndLinkItems = []
                resultItems.facets.each { facet ->
                    if(facet.field == "affiliate_fct_involved_normdata" || facet.field == "affiliate_fct_subject") {
                        facet.facetValues.each { entry ->
                            if(cultureGraphService.isValidGndUri(entry.value)){
                                gndLinkItems.addAll(entry)
                            }
                        }
                    }
                }

                int initialGndItemsToDisplay = 2
                int maxGndIdsAvailable = gndLinkItems.size()
                int displayCount = Math.min(maxGndIdsAvailable, initialGndItemsToDisplay)

                gndItems = []

                int gndItemsRetrieved = 0
                int i = 0
                while (gndItemsRetrieved < displayCount) {
                    def gndId = cultureGraphService.getGndIdFromGndUri(gndLinkItems.get(i).value)

                    def gndData = cultureGraphService.getCultureGraph(gndId)
                    if(gndData != null) {
                        gndItems.add(gndData)
                        gndItemsRetrieved ++
                    }

                    i ++

                    if(i == maxGndIdsAvailable){
                        break
                    }

                }
            }


        }

        return gndItems

    }

    def informationItem(){
        def newInformationItem = ApiConsumer.getJson(configurationService.getBackendUrl() ,'/items/'+params.id+'/indexing-profile').getResponse()
        def jsonSubresp = new JsonSlurper().parseText(newInformationItem.toString())

        def properties = [:]

        if(jsonSubresp.facet){
            //iterate over all facets
            jsonSubresp.facet.each(){ facet ->

                if(facet['@name'] == 'time_fct') {
                    addFacetItems(properties, facet,'time_fct','ddbnext.time_fct_')
                }
                else if(facet['@name'] == 'place_fct') {
                    addFacetItems(properties, facet,'place_fct',null)
                }
                else if(facet['@name'] == 'affiliate_fct') {
                    addFacetItems(properties, facet,'affiliate_fct',null)
                }
                else if(facet['@name'] == 'keywords_fct') {
                    addFacetItems(properties, facet,'keywords_fct',null)
                }
                else if(facet['@name'] == 'type_fct') {
                    addFacetItems(properties, facet,'type_fct','ddbnext.type_fct_')
                }
                else if(facet['@name'] == 'sector_fct') {
                    addFacetItems(properties, facet,'sector_fct','ddbnext.sector_fct_')
                }
                else if(facet['@name'] == 'provider_fct') {
                    addFacetItems(properties, facet,'provider_fct', null)
                }
                else if(facet['@name'] == 'language_fct') {
                    addFacetItems(properties, facet,'language_fct', 'ddbnext.language_fct_')
                }
            }
        }
        render (contentType:"text/json"){properties}
    }


    /**
     * Adds the value(s) of a facet-type to a Map
     *
     * @param properties a map that holds all facet items (for rendering)
     * @param facetMap the facet map containing a key and a value element for one facet type. The value can be a single String or a List of Strings
     * @param facetName the name of the facet-type to process
     * @param i18nCode the i18ncode is concatenated with the value if internationalization is used for the facet-type; can be <code>null</code>
     *
     */
    private addFacetItems(Map properties, Map facetMap, String facetName, String i18nCode) {
        properties[facetName]=[]

        if(facetMap['value'] instanceof String) {
            if (i18nCode != null) {
                properties[facetName].add(message(code:i18nCode+facetMap['value']))
            } else {
                properties[facetName].add(facetMap['value'])
            }
        } else if(facetMap['value'] instanceof List) {
            facetMap['value'].each() { value ->
                if (i18nCode != null) {
                    properties[facetName].add(message(code:i18nCode+value))
                } else {
                    properties[facetName].add(value)
                }
            }
        }
    }

}
