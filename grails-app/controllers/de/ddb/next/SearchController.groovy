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
import net.sf.json.JSONNull

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.ApiConsumer
import de.ddb.common.SearchService
import de.ddb.common.constants.CategoryFacetEnum
import de.ddb.common.constants.FacetEnum
import de.ddb.common.constants.ProjectConstants
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.constants.Type
import de.ddb.common.exception.BadRequestException

class SearchController {

    static defaultAction = "results"

    def entityService
    def searchService
    def configurationService
    def cultureGraphService
    def languageService

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
            //Return a maximum of 2 entities as search result
            if(! (resultsItems.entities instanceof JSONNull) && (params.offset == 0)) {
                entities = resultsItems.entities.size() > 2 ? resultsItems.entities[0..1] : resultsItems.entities
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
            def page = Math.floor(offset / rows) + 1
            def totalPages = Math.ceil(resultsItems.numberOfResults / rows).toInteger()
            def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
            def numberOfResultsFormatted = String.format(locale, "%,d", resultsItems.numberOfResults)
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
                    totalPages: totalPages,
                    totalPagesFormatted: String.format(locale, "%,d", totalPages),
                    paginationURL: searchService.buildPagination(resultsItems.numberOfResults, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")),
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
                if(urlQuery[SearchParamEnum.FACET.getName()]){
                    subFacetsUrl = searchService.buildSubFacetsUrl(params, selectedFacets, mainFacetsUrl, urlQuery, request)
                }

                render(view: "results", model: [
                    facetsList:mainFacets,
                    title: urlQuery[SearchParamEnum.QUERY.getName()],
                    results: resultsItems,
                    entities: entities,
                    isThumbnailFiltered: urlQuery[SearchService.THUMBNAIL_FACET],
                    clearFilters: searchService.buildClearFilter(urlQuery, request.forwardURI),
                    correctedQuery:resultsItems["correctedQuery"],
                    viewType:  urlQuery[SearchParamEnum.VIEWTYPE.getName()],
                    facets: [selectedFacets: selectedFacets, mainFacetsUrl: mainFacetsUrl, subFacetsUrl: subFacetsUrl],
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

        def model = [
            title: title,
            facets:[],
            viewType: "list",
            results: results,
            correctedQuery: correctedQuery,
            totalPages: totalPages,
            resultsOverallIndex: resultsOverallIndex,
            numberOfResults: numberOfResultsFormatted,
            page: page,
            resultsPaginatorOptions:searchService.buildPaginatorOptions(urlQuery),
            paginationURL:searchService.buildPagination(results.totalResults, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")),
            cultureGraphUrl:ProjectConstants.CULTURE_GRAPH_URL,
            clearFilters: clearFilters,

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
                paginationURL: searchService.buildPagination(results.totalResults, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")),
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
        def jsonSubresp = ApiConsumer.getJson(configurationService.getBackendUrl() ,'/items/'+params.id+'/indexing-profile').getResponse()
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
