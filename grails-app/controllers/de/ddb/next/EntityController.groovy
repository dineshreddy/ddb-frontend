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

class EntityController {

    def cultureGraphService
    def configurationService


    def index() {
        def entityId = params.id
        def rows = params.rows
        def offset = params.offset

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
        def xmlDnb = cultureGraphService.getDNBInformation(entityId)

        def entityUri = request.forwardURI

        def entity = [:]


        //------------------------- Entity id -------------------------------

        entity["id"] = entityId

        //------------------------- External links -------------------------------

        entity["externalLinks"] = jsonGraph.seeAlso

        //------------------------- Thumbnail information -------------------------------

        entity["thumbnail"] = jsonGraph.thumbnail

        //------------------------- Entity type (person, etc) -------------------------------

        def entityType = jsonGraph.type

        //------------------------- Titel (Name of entity) -------------------------------

        entity["title"] = jsonGraph[entityType].name

        //------------------------- Birth/Death date -------------------------------

        //        entity["dateOfBirth"] = xmlDnb.breadthFirst().find {
        //            it.name() == "dateOfBirth"
        //        }[0].text()
        //        entity["dateOfDeath"] = xmlDnb.breadthFirst().find {
        //            it.name() == "dateOfDeath"
        //        }[0].text()

        entity["dateOfBirth"] = jsonGraph[entityType].birth
        entity["dateOfDeath"] = jsonGraph[entityType].death

        //------------------------- Birth/Death place -------------------------------

        def dnbPlaceOfBirthUrl = xmlDnb.breadthFirst().find {it.name() == "placeOfBirth"}?."@rdf:resource"?.text()
        if(dnbPlaceOfBirthUrl){
            def dnbPlaceOfBirthId = dnbPlaceOfBirthUrl.substring(dnbPlaceOfBirthUrl.lastIndexOf("/")+1)
            def xmlDnbPlaceOfBirth = cultureGraphService.getDNBInformation(dnbPlaceOfBirthId)
            entity["placeOfBirth"] = xmlDnbPlaceOfBirth.breadthFirst().find {
                it.name() == "preferredNameForThePlaceOrGeographicName"
            }[0].text()
        }

        def dnbPlaceOfDeathUrl = xmlDnb.breadthFirst().find {it.name() == "placeOfDeath"}?."@rdf:resource"?.text()
        if(dnbPlaceOfDeathUrl){
            def dnbPlaceOfDeathId = dnbPlaceOfDeathUrl.substring(dnbPlaceOfDeathUrl.lastIndexOf("/")+1)
            def xmlDnbPlaceOfDeath = cultureGraphService.getDNBInformation(dnbPlaceOfDeathId)
            entity["placeOfDeath"] = xmlDnbPlaceOfDeath.breadthFirst().find {
                it.name() == "preferredNameForThePlaceOrGeographicName"
            }[0].text()
        }

        //------------------------- Professions -------------------------------

        //        def dnbProfessionUrls = xmlDnb.breadthFirst().findAll {it.name() == "professionOrOccupation"}."@rdf:resource".collect {it.text()}
        //        def dnbProfessionIds = dnbProfessionUrls.collect {it.substring(it.lastIndexOf("/")+1)}
        //
        //        def professions = []
        //        dnbProfessionIds.each {
        //            def xmlProfession = cultureGraphService.getDNBInformation(it)
        //            def professionName = xmlProfession.breadthFirst().find {it.name() == "preferredNameForTheSubjectHeading"}[0].text()
        //            professions.add(professionName)
        //        }
        //
        //        entity["professions"] = professions.join(', ')

        entity["description"] = jsonGraph[entityType].description

        //------------------------- Search preview -------------------------------

        def searchPreview = [:]

        def searchQuery = ["query": entity["title"], "rows": rows, "offset": offset, "sort": "RELEVANCE", "facet": "type_fct", "type_fct": "mediatype_002"]
        ApiResponse apiResponseSearch = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)
        if(!apiResponseSearch.isOk()){
            log.error "index(): Search response contained error"
            apiResponseSearch.throwException(request)
        }
        def jsonSearchResult = apiResponseSearch.getResponse()

        searchPreview["items"] = jsonSearchResult.results.docs
        searchPreview["resultCount"] = jsonSearchResult.numberOfResults

        //------------------------- Search preview media type count -------------------------------

        searchPreview["pictureCount"] = getResultCountsForFacetType(entity["title"], "mediatype_002")

        searchPreview["videoCount"] = getResultCountsForFacetType(entity["title"], "mediatype_005")

        searchPreview["audioCount"] = getResultCountsForFacetType(entity["title"], "mediatype_001")

        entity["searchPreview"] = searchPreview

        render(view: 'entity', model: ["entity": entity, "entityUri": entityUri])
    }

    public def getAjaxSearchResultsAsJson() {

        def query = params.query
        def offset = params.offset
        def rows = params.rows

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

        def searchQuery = ["query": query, "rows": rows, "offset": offset, "sort": "RELEVANCE"]
        ApiResponse apiResponseSearch = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)
        if(!apiResponseSearch.isOk()){
            log.error "getAjaxSearchResultsAsJson(): Search response contained error"
            apiResponseSearch.throwException(request)
        }

        def jsonSearchResult = apiResponseSearch.getResponse()

        searchPreview["items"] = jsonSearchResult.results.docs
        searchPreview["resultCount"] = jsonSearchResult.numberOfResults

        entity["searchPreview"] = searchPreview

        def resultsHTML = g.render(template:"/entity/searchResults", model:["entity": entity]).replaceAll("\r\n", '')

        def result = ["html": resultsHTML]

        render (contentType:"text/json"){result}
    }

    private def getResultCountsForFacetType(def searchString, def facetType) {

        def searchQuery = ["query": searchString, "rows": 0, "offset": 0, "sort": "RELEVANCE", "facet": "type_fct", "type_fct": facetType]
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)
        if(!apiResponse.isOk()){
            log.error "getResultCountsForFacetType(): Search response contained error"
            apiResponse.throwException(request)
        }

        return apiResponse.getResponse().numberOfResults

    }

}
