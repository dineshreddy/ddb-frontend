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

import groovy.json.*

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.next.constants.FacetEnum
import de.ddb.next.constants.SearchParamEnum
import de.ddb.next.constants.SupportedLocales
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
            def entities = resultsItems.entities

            if(resultsItems["randomSeed"]){
                urlQuery["randomSeed"] = resultsItems["randomSeed"]
                firstLastQuery[SearchParamEnum.SORT.getName()] = resultsItems["randomSeed"]
                if (!params[SearchParamEnum.SORT.getName()]) {
                    params[SearchParamEnum.SORT.getName()] = urlQuery["randomSeed"]
                }
            }

            if (resultsItems != null && resultsItems["numberOfResults"] != null && (Integer)resultsItems["numberOfResults"] > 0) {
                //check for lastHit and firstHit
                //firstHit
                firstLastQuery[SearchParamEnum.ROWS.getName()] = 1
                firstLastQuery[SearchParamEnum.OFFSET.getName()] = 0
                apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, firstLastQuery)
                if(!apiResponse.isOk()){
                    log.error "Json: Json file was not found"
                    apiResponse.throwException(request)
                }
                def firstHit = apiResponse.getResponse()
                if (firstHit != null && firstHit["numberOfResults"] != null && (Integer)firstHit["numberOfResults"] > 0) {
                    params[SearchParamEnum.FIRSTHIT.getName()] = firstHit["results"]["docs"][0].id
                }

                //lastHit
                //Workaround, find id of last hit when calling last hit.
                //Set id to "lasthit" to signal ItemController to find id of lasthit.
                params[SearchParamEnum.LASTHIT.getName()] = 'lasthit'

            }

            //Replacing the mediatype images when not coming from backend server
            resultsItems = searchService.checkAndReplaceMediaTypeImages(resultsItems)

            //create cookie with search parameters
            response.addCookie(searchService.createSearchCookie(request, params, additionalParams))

            //Calculating results details info (number of results in page, total results number)
            def resultsOverallIndex = (urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()+1)+' - ' +
                    ((urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()+
                    urlQuery[SearchParamEnum.ROWS.getName()].toInteger()>resultsItems.numberOfResults)? resultsItems.numberOfResults:urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()+urlQuery[SearchParamEnum.ROWS.getName()].toInteger())

            def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))

            //Calculating results pagination (previous page, next page, first page, and last page)
            def page = ((int)Math.floor(urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()/urlQuery[SearchParamEnum.ROWS.getName()].toInteger())+1).toString()
            def totalPages = (Math.ceil(resultsItems.numberOfResults/urlQuery[SearchParamEnum.ROWS.getName()].toInteger()).toInteger())
            def totalPagesFormatted = String.format(locale, "%,d", totalPages.toInteger())

            def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
            def numberOfResultsFormatted = String.format(locale, "%,d", resultsItems.numberOfResults.toInteger())

            def queryString = request.getQueryString()

            if(!queryString?.contains(SearchParamEnum.SORT.getName()+"="+SearchParamEnum.SORT_RANDOM.getName()) && urlQuery["randomSeed"])
                queryString = queryString+"&"+SearchParamEnum.SORT.getName()+"="+urlQuery["randomSeed"]

            if(params.reqType=="ajax"){
                def resultsHTML = ""
                resultsHTML = g.render(template:"/search/resultsList",model:[results: resultsItems.results["docs"], entities: entities, viewType:  urlQuery[SearchParamEnum.VIEWTYPE.getName()],confBinary: request.getContextPath(),
                    offset: params[SearchParamEnum.OFFSET.getName()]]).replaceAll("\r\n", '')
                def jsonReturn = [results: resultsHTML,
                    resultsPaginatorOptions: resultsPaginatorOptions,
                    resultsOverallIndex:resultsOverallIndex,
                    page: page,
                    totalPages: totalPagesFormatted,
                    paginationURL: searchService.buildPagination(resultsItems.numberOfResults, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")),
                    numberOfResults: numberOfResultsFormatted,
                    offset: params[SearchParamEnum.OFFSET.getName()]
                ]
                render (contentType:"text/json"){jsonReturn}
            }else{
                //We want to build the subfacets urls only if a main facet has been selected
                def mainFacets = []
                FacetEnum.values().each {
                    if (it.isSearchFacet()) {
                        mainFacets.add(it)
                    }
                }

                def keepFiltersChecked = ""
                if (searchParametersMap[SearchParamEnum.KEEPFILTERS.getName()] && searchParametersMap[SearchParamEnum.KEEPFILTERS.getName()] == "true") {
                    keepFiltersChecked = "checked=\"checked\""
                }
                def subFacetsUrl = [:]
                def selectedFacets = searchService.buildSubFacets(urlQuery)
                if(urlQuery[SearchParamEnum.FACET.getName()]){
                    subFacetsUrl = searchService.buildSubFacetsUrl(params, selectedFacets, mainFacetsUrl, urlQuery, request)
                }

                def roleFacetsUrl = [:]
                def selectedRoleFacets = searchService.buildRoleFacets(urlQuery)
                if(urlQuery[SearchParamEnum.FACET.getName()]){
                    roleFacetsUrl = searchService.buildRoleFacetsUrl(selectedRoleFacets, mainFacetsUrl, subFacetsUrl, urlQuery)
                }

                render(view: "results", model: [
                    facetsList:mainFacets,
                    title: urlQuery[SearchParamEnum.QUERY.getName()],
                    results: resultsItems,
                    entities: entities,
                    isThumbnailFiltered: params.isThumbnailFiltered,
                    clearFilters: searchService.buildClearFilter(urlQuery, request.forwardURI),
                    correctedQuery:resultsItems["correctedQuery"],
                    viewType:  urlQuery[SearchParamEnum.VIEWTYPE.getName()],
                    facets: [selectedFacets: selectedFacets, mainFacetsUrl: mainFacetsUrl, subFacetsUrl: subFacetsUrl, selectedRoleFacets: selectedRoleFacets, roleFacetsUrl: roleFacetsUrl],
                    resultsPaginatorOptions: resultsPaginatorOptions,
                    resultsOverallIndex:resultsOverallIndex,
                    page: page,
                    totalPages: totalPages,
                    paginationURL: searchService.buildPagination(resultsItems.numberOfResults, urlQuery, request.forwardURI+'?'+queryString),
                    numberOfResultsFormatted: numberOfResultsFormatted,
                    offset: params[SearchParamEnum.OFFSET.getName()],
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


    def informationItem(){
        def newInformationItem = ApiConsumer.getJson(configurationService.getBackendUrl() ,'/items/'+params.id+'/indexing-profile').getResponse()
        def jsonSubresp = new JsonSlurper().parseText(newInformationItem.toString())

        def properties = [:]

        if(jsonSubresp.facet){
            //iterate over all facets
            jsonSubresp.facet.each(){ facet ->
                //iterate over all values of the FacetEnum and add matching names to the information
                for (FacetEnum facetItem : FacetEnum.values()) {
                    if (facet['@name'] == facetItem.getName()) {
                        addFacetItems(properties, facet, facetItem)
                    }
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
     * @param facet the facet to add
     *
     */
    private addFacetItems(Map properties, Map facetMap, FacetEnum facet) {
        properties[facet.getName()]=[]

        if(facetMap['value'] instanceof String) {
            if (facet.getI18nPrefix() != null) {
                properties[facet.getName()].add(message(code:facet.getI18nPrefix()+facetMap['value']))
            } else {
                properties[facet.getName()].add(facetMap['value'])
            }
        } else if(facetMap['value'] instanceof List) {
            facetMap['value'].each() { value ->
                if (facet.getI18nPrefix() != null) {
                    properties[facet.getName()].add(message(code:facet.getI18nPrefix()+value))
                } else {
                    properties[facet.getName()].add(value)
                }
            }
        }
    }

}
