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

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.next.constants.CortexConstants
import de.ddb.next.constants.FacetEnum
import de.ddb.next.constants.SearchParamEnum
import de.ddb.next.constants.SupportedLocales;


/**
 * Invoked from ajax request during the selection of filters for the search results page
 * 
 * @author ema
 *
 */
class FacetsController {

    static defaultAction = "facets"

    def searchService
    def configurationService


    def facetsList() {

        def facetName = params.name
        def facetQuery = params[SearchParamEnum.QUERY.getName()]

        def facetValues
        def maxResults = CortexConstants.MAX_FACET_SEARCH_RESULTS

        // Key based facet value -> Search filtering must be done in the frontend
        if(facetName == FacetEnum.TIME.getName() || facetName == FacetEnum.SECTOR.getName() || facetName == FacetEnum.LANGUAGE.getName() || facetName == FacetEnum.TYPE.getName()){
            def urlQuery = searchService.convertFacetQueryParametersToFacetSearchParameters(params) // facet.limit: 1000

            //resultsItems = ApiConsumer.getTextAsJson(grailsApplication.config.ddb.apis.url.toString() ,'/apis/search', urlQuery).facets
            def apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, urlQuery)
            if(!apiResponse.isOk()){
                log.error "Json: Json file was not found"
                apiResponse.throwException(request)
            }

            def resultsItems = apiResponse.getResponse().facets

            //            //def numberOfElements = (urlQuery[SearchParamEnum.ROWS.getName()])?urlQuery[SearchParamEnum.ROWS.getName()].toInteger():-1
            //            def numberOfElements = 0
            //            if(resultsItems.size() < maxResults){
            //                numberOfElements = resultsItems.size()
            //            }else{
            //                numberOfElements = maxResults
            //                resultsItems = resultsItems.subList(0,301)
            //            }

            def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))

            facetValues = searchService.getSelectedFacetValuesFromOldApi(resultsItems, facetName, maxResults, facetQuery, locale)

        }else{

            def urlQuery = searchService.convertQueryParametersToSearchFacetsParameters(params)
            urlQuery[SearchParamEnum.QUERY.getName()] = (facetQuery)?facetQuery:""
            urlQuery[SearchParamEnum.SORT.getName()] = "count_desc"

            def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(),'/search/facets/'+facetName, false, urlQuery)
            if(!apiResponse.isOk()){
                log.error "Json: Json file was not found"
                apiResponse.throwException(request)
            }

            def resultsItems = apiResponse.getResponse()

            //            def numberOfElements = (urlQuery[SearchParamEnum.ROWS.getName()])?urlQuery[SearchParamEnum.ROWS.getName()].toInteger():maxResults

            def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))

            facetValues = searchService.getSelectedFacetValues(resultsItems, facetName, maxResults, facetQuery, locale)
        }

        render (contentType:"text/json"){facetValues}
    }

    /**
     * Returns all role facets from the backend
     * 
     * @return a list of all role facets in the json format
     */
    def roleFacets() {
        def roleFacets = searchService.getRoleFacets()

        render (contentType:"text/json"){roleFacets}
    }

}
