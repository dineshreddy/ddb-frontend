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

import de.ddb.next.constants.SearchParamEnum
import de.ddb.next.exception.EntityNotFoundException


/**
 * Controller class for all entity related views
 *  
 * @author boz
 */
class EntityController {

    def cultureGraphService
    def configurationService
    def entityService

    int PREVIEW_COUNT = 4

    /**
     * Initialize the entity page with content from the Culturegraph service and the cortex backend.
     * 
     * @return the content of an entity page
     */
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

        //------------------------- Object Search -------------------------------

        def searchQuery = [(SearchParamEnum.QUERY.getName()): title, (SearchParamEnum.ROWS.getName()): rows, (SearchParamEnum.OFFSET.getName()): offset, (SearchParamEnum.SORT.getName()): SearchParamEnum.SORT_RELEVANCE.getName()]
        ApiResponse apiResponseSearch = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)
        if(!apiResponseSearch.isOk()){
            log.error "index(): Search response contained error"
            apiResponseSearch.throwException(request)
        }
        def jsonSearchResult = apiResponseSearch.getResponse()

        searchPreview["items"] = jsonSearchResult.results.docs
        searchPreview["resultCount"] = jsonSearchResult.numberOfResults

        //------------------------- Involved Search -------------------------------
        def searchInvolved = entityService.doFacetSearch(title, 0, 4, false, "affiliate_fct_involved", entityId)

        //------------------------- Involved Normdata Search -------------------------------
        def searchInvolvedNormdata = entityService.doFacetSearch(title, 0, 4, true, "affiliate_fct_involved", entityId)

        //------------------------- Subject Search -------------------------------
        def searchSubject = entityService.doFacetSearch(title, 0, 4, false, "affiliate_fct_subject", entityId)

        //------------------------- Subject Normdata Search -------------------------------
        def searchSubjectNormdata = entityService.doFacetSearch(title, 0, 4, true, "affiliate_fct_subject", entityId)

        //------------------------- Search preview media type count -------------------------------
        searchPreview["pictureCount"] = entityService.getResultCountsForFacetType(title, "mediatype_002")
        searchPreview["videoCount"] = entityService.getResultCountsForFacetType(title, "mediatype_005")
        searchPreview["audioCount"] = entityService.getResultCountsForFacetType(title, "mediatype_001")

        def model = ["entity": jsonGraph,
            "entityUri": entityUri,
            "entityId": entityId,
            "searchPreview": searchPreview,
            "searchInvolved": searchInvolved,
            "searchInvolvedNormdata": searchInvolvedNormdata,
            "searchSubject": searchSubject,
            "searchSubjectNormdata": searchSubjectNormdata
        ]

        render(view: 'entity', model: model)
    }

    /**
     * Controller method for rendering AJAX calls for an entity based item search
     * 
     * @return the content of the backend search
     */
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

        def searchPreview = entityService.doItemSearch(query, offset, rows)

        entity["searchPreview"] = searchPreview

        //Replace all the newlines. The resulting html is better parsable by JQuery
        def resultsHTML = g.render(template:"/entity/searchResults", model:["entity": entity]).replaceAll("\r\n", '').replaceAll("\n", '')

        def result = ["html": resultsHTML, "resultCount" : searchPreview?.resultCount]

        render (contentType:"text/json"){result}
    }
}