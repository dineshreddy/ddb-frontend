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

import de.ddb.next.constants.FacetEnum
import de.ddb.next.constants.SearchParamEnum
import de.ddb.next.exception.EntityNotFoundException



class EntityController {

    def cultureGraphService
    def configurationService


    def index() {
        log.info "index(): entityId=" + params.id + " / rows=" + params[SearchParamEnum.ROWS.getName()] + " / offset=" + params[SearchParamEnum.OFFSET.getName()]

        if(!configurationService.isCulturegraphFeaturesEnabled()){
            redirect(controller: 'index', action: 'index')
            return
        }

        def entityId = params.id
        def rows = params[SearchParamEnum.ROWS.getName()]?.toInteger()
        def offset = params[SearchParamEnum.OFFSET.getName()]?.toInteger()

        if(!rows) {
            rows = 4
        }
        if(rows < 1){
            rows = 1
        }

        if(!offset) {
            offset = 0
        }
        if(offset < 0){
            offset = 0
        }


        def jsonGraph = cultureGraphService.getCultureGraph(entityId)

        //Forward to a 404 page if the entityId is not known by the culture graph service
        if (jsonGraph == null) {
            throw new EntityNotFoundException()
        }

        def entityUri = request.forwardURI


        def title = jsonGraph.person.preferredName

        def searchPreview = [:]

        def searchQuery = [(SearchParamEnum.QUERY.getName()): title, (SearchParamEnum.ROWS.getName()): rows, (SearchParamEnum.OFFSET.getName()): offset, (SearchParamEnum.SORT.getName()): SearchParamEnum.SORT_RELEVANCE.getName()]
        ApiResponse apiResponseSearch = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)
        if(!apiResponseSearch.isOk()){
            log.error "index(): Search response contained error"
            apiResponseSearch.throwException(request)
        }
        def jsonSearchResult = apiResponseSearch.getResponse()

        searchPreview["items"] = jsonSearchResult.results.docs
        searchPreview["resultCount"] = jsonSearchResult.numberOfResults

        //------------------------- Search preview media type count -------------------------------

        searchPreview["pictureCount"] = getResultCountsForFacetType(title, "mediatype_002")

        searchPreview["videoCount"] = getResultCountsForFacetType(title, "mediatype_005")

        searchPreview["audioCount"] = getResultCountsForFacetType(title, "mediatype_001")

        def model = ["entity": jsonGraph, "entityUri": entityUri, "entityId": entityId, "searchPreview": searchPreview]

        render(view: 'entity', model: model)
    }

    public def getAjaxSearchResultsAsJson() {

        def query = params[SearchParamEnum.QUERY.getName()]
        def offset = params.long(SearchParamEnum.OFFSET.getName())
        def rows = params.long(SearchParamEnum.ROWS.getName())

        if(!rows) {
            rows = 4
        }
        if(rows < 1){
            rows = 1
        }

        if(!offset) {
            offset = 0
        }
        if(offset < 0){
            offset = 0
        }

        def entity = [:]

        def searchPreview = [:]

        def searchQuery = [(SearchParamEnum.QUERY.getName()): query, (SearchParamEnum.ROWS.getName()): rows, (SearchParamEnum.OFFSET.getName()): offset, (SearchParamEnum.SORT.getName()): SearchParamEnum.SORT_RELEVANCE.getName()]
        ApiResponse apiResponseSearch = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)
        if(!apiResponseSearch.isOk()){
            log.error "getAjaxSearchResultsAsJson(): Search response contained error"
            apiResponseSearch.throwException(request)
        }

        def jsonSearchResult = apiResponseSearch.getResponse()

        searchPreview["items"] = jsonSearchResult.results.docs
        searchPreview["resultCount"] = jsonSearchResult.numberOfResults

        entity["searchPreview"] = searchPreview

        //Replace all the newlines. The resulting html is better parsable by JQuery
        def resultsHTML = g.render(template:"/entity/searchResults", model:["entity": entity]).replaceAll("\r\n", '').replaceAll("\n", '')

        def result = ["html": resultsHTML, "resultCount" : jsonSearchResult.numberOfResults]

        render (contentType:"text/json"){result}
    }


    public def getAjaxRoleSearchResultsAsJson() {
        def query = params[SearchParamEnum.QUERY.getName()]
        def offset = params.long(SearchParamEnum.OFFSET.getName())
        def rows = params.long(SearchParamEnum.ROWS.getName())
        def normdata = params.boolean(SearchParamEnum.NORMDATA.getName())
        def rolefacet = params.facetname
        def entityid = params.entityid

        if(!rows) {
            rows = 4
        }
        if(rows < 1){
            rows = 1
        }

        if(!offset) {
            offset = 0
        }
        if(offset < 0){
            offset = 0
        }

        def entity = [:]

        def roleSearch = [:]

        def searchQuery = []

        def searchUrlParameter = []

        def gndUrl = CultureGraphService.GND_URI_PREFIX

        if (normdata) {
            searchQuery = [(SearchParamEnum.QUERY.getName()): query, (SearchParamEnum.ROWS.getName()): rows, (SearchParamEnum.OFFSET.getName()): offset, (SearchParamEnum.FACET.getName()): [], (rolefacet+'_normdata') : (gndUrl + entityid)]
            searchQuery[SearchParamEnum.FACET.getName()].add(rolefacet + "_normdata")

            //These parameters are for the frontend to create a search link
            searchUrlParameter = [(SearchParamEnum.QUERY.getName()):query, (SearchParamEnum.FACETVALUES.getName()): [
                    (rolefacet+'_normdata')+ "="+(gndUrl + entityid)
                ]]
        } else {
            searchQuery = [(SearchParamEnum.QUERY.getName()): query, (SearchParamEnum.ROWS.getName()): rows, (SearchParamEnum.OFFSET.getName()): offset, (SearchParamEnum.SORT.getName()): SearchParamEnum.SORT_RELEVANCE.getName(), (SearchParamEnum.FACET.getName()): [], (FacetEnum.AFFILIATE.getName()) : query]
            searchQuery[rolefacet] = query
            searchQuery[SearchParamEnum.FACET.getName()].add(FacetEnum.AFFILIATE.getName())
            searchQuery[SearchParamEnum.FACET.getName()].add(rolefacet)

            //These parameters are for the frontend to create a search link
            searchUrlParameter = [(SearchParamEnum.QUERY.getName()):query, (SearchParamEnum.FACETVALUES.getName()): [
                    FacetEnum.AFFILIATE.getName() + "="+query,
                    FacetEnum.AFFILIATE_INVOLVED.getName()+"="+query
                ]]
        }


        ApiResponse apiResponseSearch = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)
        if(!apiResponseSearch.isOk()){
            log.error "getAjaxSearchResultsAsJson(): Search response contained error"
            apiResponseSearch.throwException(request)
        }

        def jsonSearchResult = apiResponseSearch.getResponse()

        roleSearch["items"] = jsonSearchResult.results.docs
        roleSearch["resultCount"] = jsonSearchResult.numberOfResults
        roleSearch["searchUrlParameter"] = searchUrlParameter

        entity["roleSearch"] = roleSearch

        //Replace all the newlines. The resulting html is better parsable by JQuery
        def resultsHTML = g.render(template:"/entity/roleSearchResults", model:["entity": entity]).replaceAll("\r\n", '').replaceAll("\n", '')

        def result = ["html": resultsHTML]

        render (contentType:"text/json"){result}
    }

    private def getResultCountsForFacetType(def searchString, def facetType) {

        def searchQuery = [(SearchParamEnum.QUERY.getName()): searchString, (SearchParamEnum.ROWS.getName()): 0, (SearchParamEnum.OFFSET.getName()): 0, (SearchParamEnum.SORT.getName()): SearchParamEnum.SORT_RELEVANCE.getName(), (SearchParamEnum.FACET.getName()): FacetEnum.TYPE.getName(), (FacetEnum.TYPE.getName()): facetType]
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)
        if(!apiResponse.isOk()){
            log.error "getResultCountsForFacetType(): Search response contained error"
            apiResponse.throwException(request)
        }

        return apiResponse.getResponse().numberOfResults

    }

}