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

import static groovyx.net.http.ContentType.*
import groovy.json.*
import de.ddb.next.constants.FacetEnum
import de.ddb.next.constants.SearchParamEnum


/**
 * Service class for all entity related methods
 *
 * @author boz
 */
class EntityService {

    //Autowire the grails application bean
    def grailsApplication
    def configurationService

    def transactional=false

    /**
     * Returns the result of a special entity facet search.
     * 
     * @param query the name of the entity to search for
     * @param offset the search offset
     * @param rows the number of documents that should be returned
     * @param normdata indicates whether normdata object should be searched or not
     * @param facetName the name of the facet to search for
     * @param entityid the id of the entity
     * 
     * @return the search result
     */
    def doFacetSearch(def query, def offset, def rows, def normdata, def facetName, def entityid) {
        def facetSearch = [:]

        def searchQuery = []

        def searchUrlParameter = []

        def gndUrl = CultureGraphService.GND_URI_PREFIX

        if (normdata) {
            searchQuery = [(SearchParamEnum.QUERY.getName()): query, (SearchParamEnum.ROWS.getName()): rows, (SearchParamEnum.OFFSET.getName()): offset, (SearchParamEnum.FACET.getName()): [], (facetName+'_normdata') : (gndUrl + entityid)]
            searchQuery[SearchParamEnum.FACET.getName()].add(facetName + "_normdata")

            //These parameters are for the frontend to create a search link
            searchUrlParameter = [(SearchParamEnum.QUERY.getName()):query, (SearchParamEnum.FACETVALUES.getName()): [
                    (facetName+'_normdata')+ "="+(gndUrl + entityid)
                ]]
        } else {
            searchQuery = [(SearchParamEnum.QUERY.getName()): query, (SearchParamEnum.ROWS.getName()): rows, (SearchParamEnum.OFFSET.getName()): offset, (SearchParamEnum.SORT.getName()): SearchParamEnum.SORT_RELEVANCE.getName(), (SearchParamEnum.FACET.getName()): [], (FacetEnum.AFFILIATE.getName()) : query]
            searchQuery[facetName] = query
            searchQuery[SearchParamEnum.FACET.getName()].add(FacetEnum.AFFILIATE.getName())
            searchQuery[SearchParamEnum.FACET.getName()].add(facetName)

            //These parameters are for the frontend to create a search link
            searchUrlParameter = [(SearchParamEnum.QUERY.getName()):query, (SearchParamEnum.FACETVALUES.getName()): [
                    FacetEnum.AFFILIATE.getName() + "="+query,
                    FacetEnum.AFFILIATE_INVOLVED.getName()+"="+query
                ]]
        }

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)

        if(!apiResponse.isOk()){
            def message = "doFacetSearch(): Search response contained error"
            log.error message
            throw new RuntimeException(message)
        }

        def jsonSearchResult = apiResponse.getResponse()

        facetSearch["items"] = jsonSearchResult?.results?.docs
        facetSearch["resultCount"] = jsonSearchResult?.numberOfResults
        facetSearch["searchUrlParameter"] = searchUrlParameter

        return facetSearch
    }

    /**
     * Performs a search request on the backend. 
     * 
     * @param query the name of the entity
     * @param offset the search offset
     * @param rows the number of search results
     * 
     * @return the serach result
     */
    def doItemSearch(def query, def offset, def rows) {
        def searchPreview = [:]

        def searchQuery = [(SearchParamEnum.QUERY.getName()): query, (SearchParamEnum.ROWS.getName()): rows, (SearchParamEnum.OFFSET.getName()): offset, (SearchParamEnum.SORT.getName()): SearchParamEnum.SORT_RELEVANCE.getName()]
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)
        if(!apiResponse.isOk()){
            def message = "doItemSearch(): Search response contained error"
            log.error message
            throw new RuntimeException(message)
        }

        def jsonSearchResult = apiResponse.getResponse()

        searchPreview["items"] = jsonSearchResult.results?.docs
        searchPreview["resultCount"] = jsonSearchResult?.numberOfResults

        return searchPreview
    }


    /**
     * Gets the number of results for a given query and facet type
     * 
     * @param searchString the search query
     * @param facetType the facet type
     * 
     * @return the number of results for a given query and facet type
     */
    def getResultCountsForFacetType(def searchString, def facetType) {
        def searchQuery = [(SearchParamEnum.QUERY.getName()): searchString, (SearchParamEnum.ROWS.getName()): 0, (SearchParamEnum.OFFSET.getName()): 0, (SearchParamEnum.SORT.getName()): SearchParamEnum.SORT_RELEVANCE.getName(), (SearchParamEnum.FACET.getName()): FacetEnum.TYPE.getName(), (FacetEnum.TYPE.getName()): facetType]
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)

        if(!apiResponse.isOk()){
            def message = "getResultCountsForFacetType(): Search response contained error"
            log.error message
            throw new RuntimeException(message)
        }

        return apiResponse.getResponse().numberOfResults
    }

}