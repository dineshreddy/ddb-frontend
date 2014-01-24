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

import de.ddb.next.ApiResponse.HttpStatus
import de.ddb.next.constants.SearchParamEnum
import de.ddb.next.exception.CultureGraphException

/**
 * Controller class for all entity related views
 *  
 * @author boz
 */
class EntityController {

    def cultureGraphService
    def configurationService
    def entityService
    def itemService

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


        ApiResponse apiResponse = cultureGraphService.getCultureGraph(entityId)
        if(!apiResponse.isOk()){
            if(apiResponse.getStatus() == HttpStatus.HTTP_404){
                CultureGraphException errorPageException = new CultureGraphException(CultureGraphException.CULTUREGRAPH_404)
                request.setAttribute(ApiResponse.REQUEST_ATTRIBUTE_APIRESPONSE, errorPageException)
                throw errorPageException
            }else{
                CultureGraphException errorPageException = new CultureGraphException(CultureGraphException.CULTUREGRAPH_500)
                request.setAttribute(ApiResponse.REQUEST_ATTRIBUTE_APIRESPONSE, errorPageException)
                throw errorPageException
            }
        }

        def jsonGraph = apiResponse.getResponse()

        if (jsonGraph == null) {
            // Should never be null. If null, something unexpected happened
            CultureGraphException errorPageException = new CultureGraphException(CultureGraphException.CULTUREGRAPH_500)
            request.setAttribute(ApiResponse.REQUEST_ATTRIBUTE_APIRESPONSE, errorPageException)
            throw errorPageException
        }

        def entityUri = request.forwardURI
        def title = jsonGraph.person.preferredName

        //------------------------- Object Search -------------------------------

        def forename = jsonGraph.person.forename
        if(jsonGraph.person.prefix != null && !jsonGraph.person.prefix.trim().isEmpty()){
            forename += " "+jsonGraph.person.prefix
        }
        def surname = jsonGraph.person.surname
        def queryName = forename+" "+surname

        def searchPreview = entityService.doItemSearch(queryName, offset, rows, jsonGraph)

        //------------------------- Involved Search -------------------------------
        def searchInvolved = entityService.doFacetSearch(title, 0, 4, false, "affiliate_fct_involved", entityId)

        //------------------------- Involved Normdata Search -------------------------------
        def searchInvolvedNormdata = entityService.doFacetSearch(title, 0, 4, true, "affiliate_fct_involved", entityId)

        //------------------------- Subject Search -------------------------------
        def searchSubject = entityService.doFacetSearch(title, 0, 4, false, "affiliate_fct_subject", entityId)

        //------------------------- Subject Normdata Search -------------------------------
        def searchSubjectNormdata = entityService.doFacetSearch(title, 0, 4, true, "affiliate_fct_subject", entityId)

        //------------------------- Search preview media type count -------------------------------
        searchPreview["pictureCount"] = entityService.getResultCountsForFacetType(title, "mediatype_002", offset, rows, jsonGraph)
        searchPreview["videoCount"] = entityService.getResultCountsForFacetType(title, "mediatype_005", offset, rows, jsonGraph)
        searchPreview["audioCount"] = entityService.getResultCountsForFacetType(title, "mediatype_001", offset, rows, jsonGraph)

        searchPreview["linkQuery"] = entityService.getResultLinkQuery(offset, rows, jsonGraph)

        def model = ["entity": jsonGraph,
            "entityUri": entityUri,
            "entityId": entityId,
            "isFavorite": itemService.isFavorite(entityId),
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
        def entityid = params[SearchParamEnum.ENTITY_ID.getName()]
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

        ApiResponse apiResponse = cultureGraphService.getCultureGraph(entityid)
        def jsonGraph = null
        if(apiResponse.isOk()){
            jsonGraph = apiResponse.getResponse()
        }


        def entity = [:]

        def searchPreview = entityService.doItemSearch(query, offset, rows, jsonGraph)

        entity["searchPreview"] = searchPreview

        //Replace all the newlines. The resulting html is better parsable by JQuery
        def resultsHTML = g.render(template:"/entity/searchResults", model:["entity": entity]).replaceAll("\r\n", '').replaceAll("\n", '')

        def result = ["html": resultsHTML, "resultCount" : searchPreview?.resultCount]

        render (contentType:"text/json"){result}
    }
}
