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

import groovy.json.JsonOutput

import org.codehaus.jackson.map.ObjectMapper
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.ApiConsumer
import de.ddb.common.ApiResponse
import de.ddb.common.TimeFacetHelper
import de.ddb.common.beans.entities.Facet
import de.ddb.common.constants.CortexConstants
import de.ddb.common.constants.FacetEnum
import de.ddb.common.constants.SearchParamEnum

/**
 * Invoked from ajax request during the selection of filters for the search results page
 *
 * @author ema
 *
 */
class FacetsController {

    static defaultAction = "facets"

    def apisService
    def facetsService
    def searchService
    def configurationService
    def languageService


    /**
     * Returns the values for a specific search facet for the item search.
     *
     * @return the values for a specific search facet for the item search.
     */
    def facetsList() {

        def facetName = params.name
        def facetQuery = params[SearchParamEnum.QUERY.getName()]

        def facetValues
        def maxResults = CortexConstants.MAX_FACET_SEARCH_RESULTS

        def enu = FacetEnum.valueOfName(facetName)

        // Key based facets uses the "Search" endpoint (/apis/search)
        if (enu && enu.isI18nFacet()){
            def urlQuery = searchService.convertFacetQueryParametersToFacetSearchParameters(params) // facet.limit: 1000

            //Use query filter if roles were selected
            def filteredQuery = apisService.filterForRoleFacets(urlQuery)

            def apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, filteredQuery)
            if(!apiResponse.isOk()){
                apiResponse.throwException(request)
            }

            def resultsItems = apiResponse.getResponse().facets

            def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))

            facetValues = searchService.getSelectedFacetValuesFromOldApi(resultsItems, facetName, maxResults, facetQuery, locale)
        }

        //All other facets uses the new "Autocomplete facets" endpoint of the backend
        else {
            def urlQuery = searchService.convertQueryParametersToSearchFacetsParameters(params)

            urlQuery[SearchParamEnum.QUERY.getName()] = (facetQuery)?facetQuery:""
            urlQuery[SearchParamEnum.SORT.getName()] = "count_desc"

            //Use query filter if roles were selected
            def filteredQuery = apisService.filterForRoleFacets(urlQuery)

            ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(),
                    "/search/facets/" + facetName, false, filteredQuery)

            if (apiResponse.isOk()) {
                Facet facets = new ObjectMapper().readValue(JsonOutput.toJson(apiResponse.getResponse()), Facet.class)
                Locale locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))

                //Filter the role values for mixed facets like affiliate_facet_role!
                facetValues = searchService.getSelectedFacetValues(facets, facetName, maxResults, facetQuery,
                        locale, facetName.endsWith("role"))
            }
            else {
                //Thrown an exception is useless for the frontend in an Ajax response.
                //We give back instead an empty set of results.
                //apiResponse.throwException(request)
                facetValues = [type: facetName, values: []]
            }
        }
        render (contentType:"text/json"){facetValues}
    }

    /**
     * Returns the values for a specific entity facet for the person search.
     *
     * @return the values for a specific entity facet for the person search.
     */
    def entityFacetsList() {
        def facetName = params.name
        def facetQuery = params[SearchParamEnum.QUERY.getName()]
        def facetValues
        def maxResults = CortexConstants.MAX_FACET_SEARCH_RESULTS
        def urlQuery = searchService.convertQueryParametersToSearchFacetsParameters(params)

        urlQuery[SearchParamEnum.QUERY.getName()] = (facetQuery)?facetQuery:""
        urlQuery[SearchParamEnum.SORT.getName()] = "count_desc"

        //Use query filter if roles were selected
        def filteredQuery = apisService.filterForRoleFacets(urlQuery)

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(),
                "/entities/facets/" + facetName, false, filteredQuery)

        if (apiResponse.isOk()) {
            Facet facets = new ObjectMapper().readValue(JsonOutput.toJson(apiResponse.getResponse()), Facet.class)
            Locale locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))

            facetValues =
                    searchService.getSelectedFacetValues(facets, facetName, maxResults, facetQuery, locale, false)
        }
        else {
            log.error "entityFacetsList: Json file was not found"
            apiResponse.throwException(request)
        }
        render (contentType:"text/json") {facetValues}
    }

    /**
     * Returns the roles for a specific facet value
     *
     * @return the roles for a specific facet value
     */
    def getRolesForFacetValue() {
        def facetName = params.name
        def facetQuery = params[SearchParamEnum.QUERY.getName()]
        def roleValues = null
        def maxResults = CortexConstants.MAX_FACET_SEARCH_RESULTS
        def urlQuery = searchService.convertQueryParametersToSearchFacetsParameters(params)

        urlQuery[SearchParamEnum.QUERY.getName()] = (facetQuery)?facetQuery:""
        urlQuery[SearchParamEnum.SORT.getName()] = "count_desc"

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(),
                "/search/facets/" + facetName, false, urlQuery)

        if (apiResponse.isOk()) {
            Facet facets = new ObjectMapper().readValue(JsonOutput.toJson(apiResponse.getResponse()), Facet.class)
            Locale locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))

            roleValues = searchService.getRolesForFacetValue(facets, facetName, maxResults, locale)
        }
        else {
            log.error "getRolesForFacetValue: Json file was not found"
            apiResponse.throwException(request)
        }
        render (contentType:"text/json"){roleValues}
    }

    /**
     * Returns a list of all defined facets on the backend.
     * The returned json is an array of facet objects.
     *
     * @return a list of all facets in the json format
     */
    def allFacetsList() {
        def allFacets = facetsService.getAllFacets()
        render (contentType:"text/json"){allFacets}
    }

    /**
     * Calculates the time facet day representation for the given dates.
     *
     * @return  the time facet day representations in JSON format
     */
    def calculateTimeFacetDays() {
        def dateFromString = params.dateFrom
        def dateTillString = params.dateTill

        def daysTill = ""
        def daysFrom = ""

        try {
            if(dateFromString) {
                def dateFrom = TimeFacetHelper.parseDateString(dateFromString)
                daysFrom = TimeFacetHelper.calculateDaysForTimeFacet(dateFrom)
            }
            if(dateTillString) {
                def dateTill = TimeFacetHelper.parseDateString(dateTillString)
                daysTill = TimeFacetHelper.calculateDaysForTimeFacet(dateTill)
            }
        }
        catch (Exception e){
            flash.error = "ddbcommon.facet_time_right"
        }
        render (contentType:"text/json"){[daysFrom: daysFrom.toString(),daysTill: daysTill.toString()]}
    }

    /**
     * Calculates the date representation for the given time facet days.
     *
     * @return the date representation in JSON format
     */
    def calculateTimeFacetDates() {
        def endDateStr = null
        def beginDateStr = null

        def beginDays = params.beginDays

        if(beginDays) {
            def beginMilis = TimeFacetHelper.calculateTimeFromTimeFacetDays(beginDays)
            beginDateStr = TimeFacetHelper.formatMillis(beginMilis)
        }

        def endDays = params.endDays
        if(endDays) {
            def endMilis = TimeFacetHelper.calculateTimeFromTimeFacetDays(endDays)
            endDateStr = TimeFacetHelper.formatMillis(endMilis)

        }

        render (contentType:"text/json"){[dateFrom: beginDateStr, dateTill: endDateStr]}
    }

}
