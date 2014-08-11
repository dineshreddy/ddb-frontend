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
import net.sf.json.JSONArray

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.ApiResponse
import de.ddb.common.ApiResponse.HttpStatus
import de.ddb.common.constants.EntityFacetEnum
import de.ddb.common.constants.ProjectConstants
import de.ddb.common.constants.RoleFacetEnum
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.constants.SupportedLocales
import de.ddb.common.constants.Type
import de.ddb.common.exception.CultureGraphException
import de.ddb.common.exception.CultureGraphException.CultureGraphExceptionType

/**
 * Controller class for all entity related views
 *
 * @author boz
 */
class EntityController {
    private final static int NR_COLUMNS_DESIRED = 5
    private final static int RESULTS_DESIRED_IN_ONE_PERSONS_PAGE = 50
    private final static int MAX_SEED_RANGE=999999999 // Backend accept as high as 2^64 -1
    def cultureGraphService
    def configurationService
    def entityService
    def ddbItemService
    def searchService

    int PREVIEW_COUNT = 4

    /**
     * Initialize the entity page with content from the Culturegraph service and the cortex backend.
     *
     * @return the content of an entity page
     */
    def index() {
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
                CultureGraphException errorPageException = new CultureGraphException(CultureGraphExceptionType.RESPONSE_404)
                request.setAttribute(ApiResponse.REQUEST_ATTRIBUTE_APIRESPONSE, errorPageException)
                throw errorPageException
            }else{
                CultureGraphException errorPageException = new CultureGraphException(CultureGraphExceptionType.RESPONSE_500)
                request.setAttribute(ApiResponse.REQUEST_ATTRIBUTE_APIRESPONSE, errorPageException)
                throw errorPageException
            }
        }

        def jsonGraph = apiResponse.getResponse()

        if (jsonGraph == null) {
            // Should never be null. If null, something unexpected happened
            CultureGraphException errorPageException = new CultureGraphException(CultureGraphExceptionType.RESPONSE_500)
            request.setAttribute(ApiResponse.REQUEST_ATTRIBUTE_APIRESPONSE, errorPageException)
            throw errorPageException
        }

        def entityUri = request.forwardURI
        def title = jsonGraph?.person?.preferredName

        //------------------------- Object Search -------------------------------

        def forename = jsonGraph.person.forename
        if(jsonGraph.person.prefix != null && !jsonGraph.person.prefix.trim().isEmpty()){
            forename += " "+jsonGraph.person.prefix
        }
        def surname = jsonGraph.person.surname
        def queryName = forename+" "+surname

        def searchPreview = entityService.doItemSearch(queryName, offset, rows, jsonGraph)

        //------------------------- Involved Search -------------------------------
        def searchInvolved = entityService.doFacetSearch(0, 4, RoleFacetEnum.AFFILIATE_INVOLVED, forename, surname, entityId)

        //------------------------- Subject Search -------------------------------
        def searchSubject = entityService.doFacetSearch(0, 4, RoleFacetEnum.AFFILIATE_SUBJECT, forename, surname, entityId)

        //------------------------- Search preview media type count -------------------------------
        searchPreview["pictureCount"] = entityService.getResultCountsForFacetType(title, "mediatype_002", offset, rows, jsonGraph)
        searchPreview["videoCount"] = entityService.getResultCountsForFacetType(title, "mediatype_005", offset, rows, jsonGraph)
        searchPreview["audioCount"] = entityService.getResultCountsForFacetType(title, "mediatype_001", offset, rows, jsonGraph)

        searchPreview["linkQuery"] = entityService.getResultLinkQuery(offset, rows, jsonGraph)

        //------------------------- Check for entity picture -------------------------------
        def entityImageUrl = null
        def thumbnailUrl = (jsonGraph?.person?.depiction?.thumbnail instanceof JSONArray) ? jsonGraph?.person?.depiction?.thumbnail[0] : jsonGraph?.person?.depiction?.thumbnail
        def imageUrl =  (jsonGraph?.person?.depiction?.image instanceof JSONArray) ? jsonGraph?.person?.depiction?.image[0] : jsonGraph?.person?.depiction?.image

        //Check first for depiction.thumbnail (normalized 270px width), than for depiction.image (can have another value than 270 px width)
        if (thumbnailUrl){
            entityImageUrl = thumbnailUrl
        } else if (imageUrl) {
            entityImageUrl = imageUrl
        }

        def model = ["entity": jsonGraph,
            "entityUri": entityUri,
            "entityId": entityId,
            "isFavorite": ddbItemService.isFavorite(entityId),
            "searchPreview": searchPreview,
            "searchInvolved": searchInvolved,
            "searchSubject": searchSubject,
            "entityImageUrl": entityImageUrl
        ]

        render(view: 'entity', model: model)
    }

    /**
     * Present a list of persons by their picture
     * https://jira.deutsche-digitale-bibliothek.de/browse/DDBNEXT-1339
     */
    def persons() {
        def randomSeed
        if (params.sort){
            randomSeed=params.sort
        }else {
            randomSeed = getRandomSeed()
        }
        def entitiesOnPage = 50
        def results = entityService.doEntitySearch([
            rows : entitiesOnPage,
            query : "thumbnail:*",
            sort : "random_" + randomSeed
        ])

        //There are entities with no thumbnail. We leave them out...
        def resultsWithThumbnails = results.entity.docs[0].findAll { it.thumbnail!=null }

        //Since the result after removing items with no thumnbails is is different from entitiesOnPage (ex: 38)
        //let's make sure we have a list which will have full columns when nrColumnsDesired = x
        def total= resultsWithThumbnails.size() -resultsWithThumbnails.size().mod(NR_COLUMNS_DESIRED)
        if (total>RESULTS_DESIRED_IN_ONE_PERSONS_PAGE) {total=RESULTS_DESIRED_IN_ONE_PERSONS_PAGE}

        render(view: "persons", model: [title: g.message(code:"ddbnext.entities.personspage.personspageheader"), results: resultsWithThumbnails.collate(total), randomSeed:randomSeed])
    }

    /**
     * Gets a RandomSeedString Used in the search for persons
     * Seed is stored in a session (in a list) to guarantee that next try is for a unique string
     * Retrieves only last element of the List which is turn used for the search
     * @return Integer
     */
    private getRandomSeed() {
        Random rand = new Random()
        def listRandomSeeds = []
        //
        def random_seed = rand.nextInt(MAX_SEED_RANGE)+1
        if (!session?.data) {
            // first request
            listRandomSeeds.add(random_seed)
            session.data = listRandomSeeds
        } else {
            // retrieve data from session
            listRandomSeeds = session.data
            addRandomToSession(listRandomSeeds,rand)
            session.data = listRandomSeeds
        }
        return listRandomSeeds.pop()
    }

    /**
     * Populate a list of integers by taking care that values are not dublicated
     * @param listRandomSeeds
     * @param rand
     * @return List<Integer>
     */
    def private addRandomToSession(listRandomSeeds,rand) {
        def random_seed = rand.nextInt(MAX_SEED_RANGE)+1
        if (listRandomSeeds.contains(random_seed)&&(listRandomSeeds.size()<MAX_SEED_RANGE)) {
            addRandomToSession(listRandomSeeds,rand)
        }else {
            listRandomSeeds.add(random_seed)
        }
        return listRandomSeeds
    }
    /**
     * Used to search for entities of Person
     * Mapped to /entities/search/person
     * @return
     */
    def personsearch() {
        //The list of the NON JS supported facets for institutions
        def nonJsFacetsList = [
            EntityFacetEnum.PERSON_OCCUPATION_FCT.getName(),
            EntityFacetEnum.PERSON_PLACE_FCT.getName(),
            EntityFacetEnum.PERSON_GENDER_FCT.getName()
        ]

        def cookieParametersMap = searchService.getSearchCookieAsMap(request, request.cookies)

        def additionalParams = [:]

        if (searchService.checkPersistentFacets(cookieParametersMap, params, additionalParams, Type.ENTITY)) {
            redirect(controller: "entity", action: "personsearch", params: params)
        }

        def queryString = request.getQueryString()
        def urlQuery = searchService.convertQueryParametersToSearchParameters(params, cookieParametersMap)
        def results = entityService.doEntitySearch(urlQuery)
        def correctedQuery = ""
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))

        //Calculating results pagination (previous page, next page, first page, and last page)
        def page = ((int)Math.floor(urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()/urlQuery[SearchParamEnum.ROWS.getName()].toInteger())+1).toString()
        def totalPages = (Math.ceil(results.totalResults/urlQuery[SearchParamEnum.ROWS.getName()].toInteger()).toInteger())
        def totalPagesFormatted = String.format(locale, "%,d", totalPages.toInteger())

        //Calculating results details info (number of results in page, total results number)
        def resultsOverallIndex = (urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()+1)+' - ' +
                ((urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()+
                urlQuery[SearchParamEnum.ROWS.getName()].toInteger()>results.totalResults)? results.totalResults:urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()+urlQuery[SearchParamEnum.ROWS.getName()].toInteger())
        def numberOfResultsFormatted = String.format(locale, "%,d", results.totalResults.toInteger())
        def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)

        //create cookie with search parameters
        response.addCookie(searchService.createSearchCookie(request, params, additionalParams, cookieParametersMap, Type.ENTITY))

        if(params.reqType=="ajax"){
            def model = [title: urlQuery[SearchParamEnum.QUERY.getName()], entities: results, correctedQuery: correctedQuery, totalPages: totalPagesFormatted, cultureGraphUrl:ProjectConstants.CULTURE_GRAPH_URL]
            def resultsHTML = ""
            resultsHTML = g.render(template:"/entity/entityResultsList",model:model)
            def jsonReturn = [results: resultsHTML,
                resultsPaginatorOptions: resultsPaginatorOptions,
                resultsOverallIndex:resultsOverallIndex,
                page: page,
                totalPages: totalPagesFormatted,
                paginationURL: searchService.buildPagination(results.totalResults, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")),
                numberOfResults: numberOfResultsFormatted,
                resultsPaginatorOptions:searchService.buildPaginatorOptions(urlQuery),
                offset: params[SearchParamEnum.OFFSET.getName()]
            ]
            render (contentType:"text/json"){jsonReturn}
        }else {
            def mainFacetsUrl = searchService.buildMainFacetsUrl(params, urlQuery, request, nonJsFacetsList)

            def mainFacets = []
            EntityFacetEnum.values().each {
                if (it.isSearchFacet()) {
                    mainFacets.add(it)
                }
            }

            def keepFiltersChecked = ""
            if (cookieParametersMap[SearchParamEnum.KEEPFILTERS.getName()] && cookieParametersMap[SearchParamEnum.KEEPFILTERS.getName()] == "true") {
                keepFiltersChecked = "checked=\"checked\""
            }
            def subFacetsUrl = [:]
            def selectedFacets = entityService.buildSubFacets(urlQuery, nonJsFacetsList)
            if(urlQuery[SearchParamEnum.FACET.getName()]){
                subFacetsUrl = searchService.buildSubFacetsUrl(params, selectedFacets, mainFacetsUrl, urlQuery, request)
            }
            def model = [
                title: urlQuery[SearchParamEnum.QUERY.getName()],
                facets: [selectedFacets: selectedFacets, mainFacetsUrl: mainFacetsUrl, subFacetsUrl: subFacetsUrl],
                results: results,
                correctedQuery: correctedQuery,
                page: page,
                resultsOverallIndex:resultsOverallIndex,
                totalPages: totalPages,
                cultureGraphUrl:ProjectConstants.CULTURE_GRAPH_URL,
                resultsPaginatorOptions:searchService.buildPaginatorOptions(urlQuery),
                clearFilters: searchService.buildClearFilter(urlQuery, request.forwardURI),
                paginationURL: searchService.buildPagination(results.totalResults, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")),
                keepFiltersChecked: keepFiltersChecked]
            render(view: "searchPerson", model: model)
        }


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
