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

import org.springframework.web.servlet.support.RequestContextUtils;


class SearchController {

    static defaultAction = "results"

    def searchService
    def configurationService

    def results() {

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

        //create cookie with search parameters
        response.addCookie(searchService.createSearchCookie(request, params, additionalParams))

        //Calculating results details info (number of results in page, total results number)
        def resultsOverallIndex = (urlQuery["offset"].toInteger()+1)+' - ' +
                ((urlQuery["offset"].toInteger()+
                urlQuery["rows"].toInteger()>resultsItems.numberOfResults)? resultsItems.numberOfResults:urlQuery["offset"].toInteger()+urlQuery["rows"].toInteger())

        //Calculating results pagination (previous page, next page, first page, and last page)
        def page = ((urlQuery["offset"].toInteger()/urlQuery["rows"].toInteger())+1).toString()
        def totalPages = (Math.ceil(resultsItems.numberOfResults/urlQuery["rows"].toInteger()).toInteger())

        def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        def numberOfResultsFormatted = String.format(locale, "%,d", resultsItems.numberOfResults.toInteger())

        def queryString = request.getQueryString()

        if(!queryString?.contains("sort=random") && urlQuery["randomSeed"])
            queryString = queryString+"&sort="+urlQuery["randomSeed"]

        if(params.reqType=="ajax"){
            def resultsHTML = ""
            resultsHTML = g.render(template:"/search/resultsList",model:[results: resultsItems.results["docs"], viewType:  urlQuery["viewType"],confBinary: request.getContextPath(),
                offset: params["offset"]]).replaceAll("\r\n", '')
            def jsonReturn = [results: resultsHTML,
                resultsPaginatorOptions: resultsPaginatorOptions,
                resultsOverallIndex:resultsOverallIndex,
                page: page,
                totalPages: totalPages,
                paginationURL: searchService.buildPagination(resultsItems.numberOfResults, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")),
                numberOfResults: numberOfResultsFormatted,
                offset: params["offset"]
            ]
            render (contentType:"text/json"){jsonReturn}
        }
        else{
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
            render(view: "results", model: [
                title: urlQuery["query"],
                results: resultsItems,
                isThumbnailFiltered: params.isThumbnailFiltered,
                clearFilters: searchService.buildClearFilter(urlQuery, request.forwardURI),
                correctedQuery:resultsItems["correctedQuery"],
                viewType:  urlQuery["viewType"],
                facets: [selectedFacets: selectedFacets, mainFacetsUrl: mainFacetsUrl, subFacetsUrl: subFacetsUrl],
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


    }


}
