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

import grails.converters.JSON

import org.codehaus.groovy.grails.web.util.WebUtils

/**
 * @author chh
 *
 */
class SavedSearchService {
    def configurationService
    def transactional = false

    def saveSearch(userId, queryString, title = null, description = null) {
        def result = null
        def baseUrl = configurationService.getElasticSearchUrl()
        def path = "/ddb/savedSearch"
        def postParameter = [
            user: userId,
            queryString: queryString,
            title: title,
            description: description,
            createdAt: new Date().getTime()
        ]
        def ApiResponse apiResponse = ApiConsumer.postJson(baseUrl, path, false, postParameter as JSON)

        if (apiResponse.isOk()) {
            result = apiResponse.response._id
            log.info "Saved Search with the ID ${result} is created."
            refresh()
        }
        else {
            log.error "saveSearch(): Could not post request to " + baseUrl + path
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return result
    }

    def findSavedSearchByUserId(userId) {
        log.info "find saved searches for the user (${userId})"
        return findSavedSearch([q: "user:${userId}"])
    }

    def findSavedSearchByQueryString(userId, queryString) {
        log.info "find saved searches for the user ${userId} and query ${queryString}"
        return findSavedSearch([q: "user:${userId} AND queryString:${queryString}"])
    }

    private def findSavedSearch(Map<String, String> query) {
        def baseUrl = configurationService.getElasticSearchUrl()
        def path = "/ddb/savedSearch/_search"
        def ApiResponse apiResponse = ApiConsumer.getJson(baseUrl, path, false, query)

        if (apiResponse.isOk()) {
            def all = []
            def resultList = apiResponse.response.hits.hits

            resultList.each { it ->
                def savedSearch = [:]

                savedSearch['id'] = it._id
                savedSearch['user'] = it._source.user
                savedSearch['title'] = it._source.title
                savedSearch['description'] = it._source.description
                savedSearch['queryString'] = it._source.queryString
                savedSearch['createdAt'] = it._source.createdAt

                all.add(savedSearch)

                log.info "it: ${it}"
                log.info "Saved Search ID: ${it._id}"
                log.info "user: ${it._source.user}"
                log.info "title: ${it._source.title}"
                log.info "description: ${it._source.description}"
                log.info "query string: ${it._source.queryString}"
            }
            all
        }
        else {
            log.error "findSavedSearch(): Could not get saved searches from " + baseUrl + path
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
    }

    def boolean deleteSavedSearch(userId, savedSearchIdList) {
        def result = false
        def baseUrl = configurationService.getElasticSearchUrl()
        def path = "/ddb/savedSearch/_bulk"
        def postParameter = ''

        savedSearchIdList.each { id ->
            postParameter += '{ "delete" : { "_index" : "ddb", "_type" : "savedSearch", "_id" : "' + id + '" } }\n'
        }

        def ApiResponse apiResponse = ApiConsumer.postJson(baseUrl, path, false, postParameter)

        if (apiResponse.isOk()) {
            refresh()
            result = true
        }
        else {
            log.error "deleteSavedSearch(): Could not post request to " + baseUrl + path
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return result
    }

    // TODO: move to a util class.
    private refresh() {
        log.info "refreshing index ddb..."

        def baseUrl = configurationService.getElasticSearchUrl()
        def path = "/ddb/_refresh"
        def ApiResponse apiResponse = ApiConsumer.postJson(baseUrl, path, false, "")

        if (apiResponse.isOk()) {
            log.info "finished refreshing index ddb."
        }
        else {
            log.error "refresh(): Could not post request to " + baseUrl + path
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
    }
}
