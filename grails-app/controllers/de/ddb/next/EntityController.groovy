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

import org.springframework.beans.factory.InitializingBean
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.ApiResponse
import de.ddb.common.constants.EntityFacetEnum
import de.ddb.common.constants.ProjectConstants
import de.ddb.common.constants.RoleFacetEnum
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.constants.Type

/**
 * Controller class for all entity related views
 *
 * @author boz
 */
class EntityController implements InitializingBean {
    private final static int NR_COLUMNS_DESIRED = 5
    private final static int RESULTS_DESIRED_IN_ONE_PERSONS_PAGE = 50
    private final static int MAX_SEED_RANGE=999999999 // Backend accept as high as 2^64 -1
    def cultureGraphService
    def configurationService
    def entityService
    def ddbItemService
    def searchService
    def languageService

    URL ddbUrl

    public void afterPropertiesSet() throws Exception {
        ddbUrl = new URL(configurationService.getDomainCanonic())
    }

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

        try {
            entityService.getEntityDetails(entityId)
        } catch (Exception e) {
            return
        }

        ApiResponse apiResponse = cultureGraphService.getCultureGraph(entityId)
        if (!apiResponse.isOk()) {
            return
        }

        def jsonGraph = apiResponse.getResponse()
        if (jsonGraph == null) {
            return
        }
        else if (!jsonGraph.person) {
            render(view: "/message/message", model: [errors: [
                    "ddbnext.Error_Entity_No_Elements"
                ]])
            return
        }

        def entityUri = request.forwardURI

        //------------------------- Object Search -------------------------------

        def forename = jsonGraph.person.forename
        if(jsonGraph.person.prefix != null && !jsonGraph.person.prefix.trim().isEmpty()){
            forename += " "+jsonGraph.person.prefix
        }

        def searchPreview = entityService.doItemSearch(offset, rows, jsonGraph)

        //------------------------- Involved Search -------------------------------
        def searchInvolved = entityService.doFacetSearch(0, 4, RoleFacetEnum.AFFILIATE_INVOLVED, entityId)

        //------------------------- Subject Search -------------------------------
        def searchSubject = entityService.doFacetSearch(0, 4, RoleFacetEnum.AFFILIATE_SUBJECT, entityId)

        //------------------------- Search preview media type count -------------------------------
        searchPreview["pictureCount"] = entityService.getResultCountsForFacetType("mediatype_002", offset, rows, jsonGraph)
        searchPreview["videoCount"] = entityService.getResultCountsForFacetType("mediatype_005", offset, rows, jsonGraph)
        searchPreview["audioCount"] = entityService.getResultCountsForFacetType("mediatype_001", offset, rows, jsonGraph)

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

        // filter out external links which point to DDB
        jsonGraph.sameAs?.removeAll { link -> new URL(link.'@id').getHost().equals(ddbUrl.getHost())}

        def model = ["entity": jsonGraph,
            "entityUri": entityUri,
            "entityId": entityId,
            "isFavorite": ddbItemService.isFavorite(entityId),
            "searchPreview": searchPreview,
            "searchInvolved": searchInvolved,
            "searchSubject": searchSubject,
            domainCanonic:configurationService.getDomainCanonic(),
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
        if (session?.data) {
            // retrieve data from session
            listRandomSeeds = session.data
            addRandomToSession(listRandomSeeds,rand)
            session.data = listRandomSeeds
        }
        else {
            // first request
            listRandomSeeds.add(random_seed)
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
        //No need for isThumbnailFiltered here: See bug DDBNEXT-1802
        def urlParams = params.clone()
        urlParams.isThumbnailFiltered=false

        def queryString = request.getQueryString()
        def urlQuery = searchService.convertQueryParametersToSearchParameters(urlParams, cookieParametersMap)
        def results = entityService.doEntitySearch(urlQuery)
        def correctedQuery = ""
        def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))

        //The Entity API deliveres a dateBirth_de and a dateBirth_en. In the View we just pass a dateOfBirth without locale
        fixLocalizedDateOfBirth(results)

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
        response.addCookie(searchService.createSearchCookie(request, params, additionalParams, Type.ENTITY))

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
                paginationURL: searchService.buildPagination(results.totalResults, urlQuery,
                request.forwardURI + '?' + queryString?.replaceAll("&reqType=ajax", ""))
            ]
            render(view: "searchPerson", model: model)
        }
    }

    /**
     * Controller method for rendering AJAX calls for an entity based item search
     *
     * @return the content of the backend search
     */
    public def getAjaxSearchResultsAsJson() {
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

            def entity = [:]

            def searchPreview = entityService.doItemSearch(offset, rows, jsonGraph)

            entity["searchPreview"] = searchPreview

            //Replace all the newlines. The resulting html is better parsable by JQuery
            def resultsHTML = g.render(template:"/entity/searchResults", model:["entity": entity]).replaceAll("\r\n", '').replaceAll("\n", '')

            def result = ["html": resultsHTML, "resultCount" : searchPreview?.resultCount]

            render (contentType:"text/json"){result}
        }
        else {
            render(status: response.SC_NOT_FOUND)
        }
    }

    /**
     * This function takes the entity result and modifies entity dates based on the locale
     * @param results
     * @return results
     */
    private fixLocalizedDateOfBirth(results) {
        def mlocale = RequestContextUtils.getLocale(request)
        for (entity in results.entity[0].docs) {
            if (mlocale.toString() == "en"){
                entity.dateOfBirth = entity.dateOfBirth_en
                entity.dateOfDeath = entity.dateOfDeath_en
            } else {
                entity.dateOfBirth = entity.dateOfBirth_de
                entity.dateOfDeath = entity.dateOfDeath_de
            }
        }
    }
}
