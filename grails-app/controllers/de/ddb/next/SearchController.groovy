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

import de.ddb.common.ApiConsumer
import de.ddb.common.CultureGraphService
import de.ddb.common.JsonUtil
import de.ddb.common.beans.item.Facet
import de.ddb.common.beans.item.IndexingProfile
import de.ddb.common.constants.CategoryFacetEnum
import de.ddb.common.constants.FacetEnum
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.constants.Type
import de.ddb.common.exception.BadRequestException

class SearchController {

    static defaultAction = "results"

    def configurationService
    def ddbItemService
    def languageService
    def searchService

    def results() {
        try {
            //The list of the NON JS supported facets for items
            def nonJsFacetsList = SearchFacetLists.itemSearchNonJavascriptFacetList

            def cookieParametersMap = searchService.getSearchCookieAsMap(request, request.cookies)
            def additionalParams = [:]

            if (searchService.checkPersistentFacets(cookieParametersMap, params, additionalParams, Type.CULTURAL_ITEM)) {
                redirect(controller: "search", action: "results", params: params)
                return
            }

            def urlQuery = searchService.convertQueryParametersToSearchParameters(params, cookieParametersMap)
            def firstLastQuery = urlQuery.clone()

            //Search should only return documents, no institutions, see DDBNEXT-1504
            searchService.setCategory(urlQuery, CategoryFacetEnum.CULTURE.getName())
            searchService.setCategory(firstLastQuery, CategoryFacetEnum.CULTURE.getName())

            def apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, urlQuery)
            if(!apiResponse.isOk()){
                log.error "Json: Json file was not found"
                apiResponse.throwException(request)
            }
            def resultsItems = apiResponse.getResponse()
            def entities = ""
            def entitiesUrl
            //Return a maximum of 2 entities as search result
            if (resultsItems.results["docs"] && !JsonUtil.isAnyNull(resultsItems.entities) && (params.offset == 0)) {
                if (resultsItems.entities.size() > 2) {
                    entities = resultsItems.entities[0..1]
                    entitiesUrl = g.createLink(controller: "entity", action: "personsearch", params: [query: urlQuery.query])
                }
                else {
                    entities = resultsItems.entities
                }
            }

            def mlocale = RequestContextUtils.getLocale(request)

            for (entity in entities) {
                if (mlocale.toString() == "en") {
                    entity.dateOfBirth = entity.dateOfBirth_en
                    entity.dateOfDeath = entity.dateOfDeath_en
                } else {
                    entity.dateOfBirth = entity.dateOfBirth_de
                    entity.dateOfDeath = entity.dateOfDeath_de
                }
            }

            if(resultsItems["randomSeed"]){
                urlQuery["randomSeed"] = resultsItems["randomSeed"]
                firstLastQuery[SearchParamEnum.SORT.getName()] = resultsItems["randomSeed"]
                if (!params[SearchParamEnum.SORT.getName()]) {
                    params[SearchParamEnum.SORT.getName()] = urlQuery["randomSeed"]
                }
            }

            if (resultsItems && resultsItems["numberOfResults"] && (Integer)resultsItems["numberOfResults"] > 0) {
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
                if (firstHit && firstHit["numberOfResults"] && (Integer)firstHit["numberOfResults"] > 0) {
                    params[SearchParamEnum.FIRSTHIT.getName()] = firstHit["results"]["docs"][0].id
                }

                //lastHit
                //Workaround, find id of last hit when calling last hit.
                //Set id to "lasthit" to signal ItemController to find id of lasthit.
                params[SearchParamEnum.LASTHIT.getName()] = 'lasthit'

            }

            //Replacing the mediatype images when not coming from backend server
            searchService.checkAndReplaceMediaTypeImages(resultsItems.results.docs)

            //create cookie with search parameters
            response.addCookie(searchService.createSearchCookie(request, params, additionalParams, Type.CULTURAL_ITEM))

            //Calculating results details info (number of results in page, total results number)
            def rows = searchService.getNumber(urlQuery[SearchParamEnum.ROWS.getName()],
                    searchService.DEFAULT_ROWS_PER_PAGE)
            def offset = searchService.getNumber(urlQuery[SearchParamEnum.OFFSET.getName()])
            def resultsOverallIndex = (offset + 1) + ' - ' +
                    (offset + rows > resultsItems.numberOfResults ? resultsItems.numberOfResults : offset + rows)
            def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))

            //Calculating results pagination (previous page, next page, first page, and last page)
            def page = Math.floor(offset / rows).intValue() + 1
            def totalPages = Math.ceil(resultsItems.numberOfResults / rows).toInteger()
            def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
            def numberOfResultsFormatted = String.format(locale, "%,d", resultsItems.numberOfResults)
            def queryString = request.getQueryString()

            if (!queryString?.contains(SearchParamEnum.SORT.getName() + "=") && urlQuery["randomSeed"]) {
                queryString += "&" + SearchParamEnum.SORT.getName() + "=" + urlQuery["randomSeed"]
            }

            def resetSelectionUrl

            if (isFacetSearch(urlQuery)) {
                resetSelectionUrl = g.createLink(action: "results", params: [
                    (SearchParamEnum.CLEARFILTER.getName()): true,
                    (SearchParamEnum.IS_THUMBNAILS_FILTERED.getName()): false,
                    (SearchParamEnum.QUERY.getName()): urlQuery.query
                ])
            }

            if(params.reqType=="ajax"){
                def resultsHTML = g.render(template:"/search/resultsList",
                model:[
                    results: resultsItems.results["docs"],
                    entities: entities,
                    entitiesUrl: entitiesUrl,
                    viewType: urlQuery[SearchParamEnum.VIEWTYPE.getName()],
                    confBinary: request.getContextPath(),
                    offset: params[SearchParamEnum.OFFSET.getName()]
                ]).replaceAll("\r\n", '')
                def jsonReturn = [results: resultsHTML,
                    resultsPaginatorOptions: resultsPaginatorOptions,
                    resultsOverallIndex:resultsOverallIndex,
                    page: page,
                    totalPages: totalPages,
                    totalPagesFormatted: String.format(locale, "%,d", totalPages),
                    paginationURL: searchService.buildPagination(resultsItems.numberOfResults, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")),
                    resetSelectionUrl: resetSelectionUrl,
                    numberOfResults: numberOfResultsFormatted,
                    offset: params[SearchParamEnum.OFFSET.getName()]
                ]
                render (contentType:"text/json"){jsonReturn}
            }else{
                def mainFacetsUrl = searchService.buildMainFacetsUrl(params, urlQuery, request, nonJsFacetsList)

                //We want to build the subfacets urls only if a main facet has been selected
                def mainFacets = []
                FacetEnum.values().each {
                    if (it.isSearchFacet()) {
                        mainFacets.add(it)
                    }
                }

                def subFacetsUrl = [:]
                def selectedFacets = searchService.buildSubFacets(urlQuery, nonJsFacetsList)

                if (urlQuery[SearchParamEnum.FACET.getName()]) {
                    subFacetsUrl = searchService.buildSubFacetsUrl(params, selectedFacets, mainFacetsUrl, urlQuery,
                            request)
                }
                render(view: "results", model: [
                    facetsList:mainFacets,
                    title: urlQuery[SearchParamEnum.QUERY.getName()],
                    results: resultsItems,
                    entities: entities,
                    entitiesUrl : entitiesUrl,
                    isThumbnailFiltered: urlQuery[FacetEnum.DIGITALISAT.getName()],
                    clearFilters: searchService.buildClearFilter(urlQuery, request.forwardURI),
                    correctedQuery:resultsItems["correctedQuery"],
                    viewType:  urlQuery[SearchParamEnum.VIEWTYPE.getName()],
                    facets: [selectedFacets: selectedFacets, mainFacetsUrl: mainFacetsUrl, subFacetsUrl: subFacetsUrl],
                    resetSelectionUrl: resetSelectionUrl,
                    resultsPaginatorOptions: resultsPaginatorOptions,
                    resultsOverallIndex:resultsOverallIndex,
                    page: page,
                    totalPages: totalPages,
                    paginationURL: searchService.buildPagination(resultsItems.numberOfResults, urlQuery, request.forwardURI+'?'+queryString),
                    numberOfResultsFormatted: numberOfResultsFormatted,
                    offset: params[SearchParamEnum.OFFSET.getName()]
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

    /**
     * Used to search for entities of Person
     * Mapped to /entities/search/person
     * @return
     */
    def institution() {
        //The list of the NON JS supported facets for institutions
        def nonJsFacetsList = SearchFacetLists.institutionSearchNonJavascriptFacetList

        def cookieParametersMap = searchService.getSearchCookieAsMap(request, request.cookies)

        def additionalParams = [:]


        if (searchService.checkPersistentFacets(cookieParametersMap, params, additionalParams, Type.INSTITUTION)) {
            redirect(controller: "search", action: "institution", params: params)
        }

        //No need for isThumbnailFiltered here: See bug DDBNEXT-1802
        def urlParams = params.clone()
        urlParams.isThumbnailFiltered=false

        def urlQuery = searchService.convertQueryParametersToSearchParameters(urlParams, cookieParametersMap)
        def onlyWithData = urlQuery[FacetEnum.HAS_ITEMS.getName()]
        def clearFilters = searchService.buildClearFilter(urlQuery, request.forwardURI)
        def title = urlQuery[SearchParamEnum.QUERY.getName()]
        def queryString = request.getQueryString()

        //Only select institutions, no documents!
        searchService.setCategory(urlQuery, CategoryFacetEnum.INSTITUTION.getName())

        if(!queryString?.contains(SearchParamEnum.SORT.getName()+"="+SearchParamEnum.SORT_RANDOM.getName()) && urlQuery["randomSeed"]) {
            queryString = queryString+"&"+SearchParamEnum.SORT.getName()+"="+urlQuery["randomSeed"]
        }

        def results = searchService.doInstitutionSearch(urlQuery)

        def correctedQuery = ""
        def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        //Calculating results pagination (previous page, next page, first page, and last page)
        def rows = searchService.getNumber(urlQuery[SearchParamEnum.ROWS.getName()],
                searchService.DEFAULT_ROWS_PER_PAGE)
        def offset = searchService.getNumber(urlQuery[SearchParamEnum.OFFSET.getName()])
        def page = (int)Math.floor(offset / rows) + 1
        def totalPages = Math.ceil(results.totalResults / rows).toInteger()
        //Calculating results details info (number of results in page, total results number)
        def resultsOverallIndex = (offset + 1) +' - ' +
                (offset + rows > results.totalResults ? results.totalResults : offset + rows)
        def numberOfResultsFormatted = String.format(locale, "%,d", results.totalResults)
        def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)

        //create cookie with search parameters
        response.addCookie(searchService.createSearchCookie(request, params, additionalParams, Type.INSTITUTION))

        def getQuery = request.forwardURI + (queryString ? '?' + queryString.replaceAll("&reqType=ajax","") : "")
        def model = [
            title: title,
            facets:[],
            viewType: "list",
            results: results,
            correctedQuery: correctedQuery,
            totalPages: totalPages,
            resultsOverallIndex: resultsOverallIndex,
            numberOfResults: numberOfResultsFormatted,
            onlyWithData: onlyWithData,
            page: page,
            resultsPaginatorOptions: searchService.buildPaginatorOptions(urlQuery),
            paginationURL: searchService.buildPagination(results.totalResults, urlQuery, getQuery),
            cultureGraphUrl: CultureGraphService.GND_URI_PREFIX,
            clearFilters: clearFilters
        ]
        if(params.reqType=="ajax"){
            def resultsHTML = ""
            resultsHTML = g.render(template:"/search/institutionResultsList",model:[results: results, viewType: urlQuery[SearchParamEnum.VIEWTYPE.getName()],confBinary: request.getContextPath(),
                offset: params[SearchParamEnum.OFFSET.getName()]]).replaceAll("\r\n", '')
            def jsonReturn = [results: resultsHTML,
                resultsPaginatorOptions: resultsPaginatorOptions,
                resultsOverallIndex:resultsOverallIndex,
                page: page,
                totalPages: totalPages,
                totalPagesFormatted: String.format(locale, "%,d", totalPages),
                paginationURL: searchService.buildPagination(results.totalResults, urlQuery, getQuery),
                numberOfResults: numberOfResultsFormatted,
                offset: params[SearchParamEnum.OFFSET.getName()]
            ]
            render (contentType:"text/json"){jsonReturn}
        }else {
            def mainFacetsUrl = searchService.buildMainFacetsUrl(params, urlQuery, request, nonJsFacetsList)
            def mainFacets = []
            FacetEnum.values().each {
                if (it.isSearchFacet()) {
                    mainFacets.add(it)
                }
            }

            def subFacetsUrl = [:]
            def selectedFacets = searchService.buildSubFacets(urlQuery, nonJsFacetsList)
            if(urlQuery[SearchParamEnum.FACET.getName()]){
                subFacetsUrl = searchService.buildSubFacetsUrl(params, selectedFacets, mainFacetsUrl, urlQuery, request)
            }

            model["facets"] = [selectedFacets: selectedFacets, mainFacetsUrl: mainFacetsUrl, subFacetsUrl: subFacetsUrl]

            render(view: "searchInstitution", model: model)
        }
    }

    def informationItem(){
        def properties = [:]
        IndexingProfile newInformationItem = ddbItemService.getItemIndexingProfile(params.id)

        if(newInformationItem.facet){
            //iterate over all facets
            newInformationItem.facet.each(){ facet ->
                //iterate over all values of the FacetEnum and add matching names to the information
                for (FacetEnum facetItem : FacetEnum.values()) {
                    if (facet.name == facetItem.getName()) {
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
     * @param facet the facet
     * @param facetEnum the facet type to add
     */
    private addFacetItems(Map properties, Facet facet, FacetEnum facetEnum) {
        properties[facetEnum.getName()]=[]

        facet.value.each() { value ->
            if (facetEnum.getI18nPrefix()) {
                properties[facetEnum.getName()].add(message(code: facetEnum.getI18nPrefix() + value))
            }
            else {
                properties[facetEnum.getName()].add(value)
            }
        }
    }

    /**
     * Check if the given query contains a search criteria other than a query string.
     */
    private boolean isFacetSearch(def query) {
        boolean result = false
        def facetValue = query[SearchParamEnum.FACET.getName()]

        if (facetValue instanceof String) {
            result = isFacetValue(query, FacetEnum.valueOfName(facetValue))
        }
        else {
            facetValue.each {
                result |= isFacetValue(query, FacetEnum.valueOfName(it))
            }
        }
        return result
    }

    /**
     * Check if the given facet value enforces a facet search.
     */
    private boolean isFacetValue(def query, FacetEnum facetValue) {
        boolean result = false

        if (facetValue != FacetEnum.CATEGORY) {
            if (facetValue == FacetEnum.DIGITALISAT) {
                result = query[FacetEnum.DIGITALISAT.getName()] as boolean
            }
            else {
                result = true
            }
        }
        return result
    }
}
