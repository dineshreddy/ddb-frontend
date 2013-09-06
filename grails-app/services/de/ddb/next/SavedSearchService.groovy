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

import groovy.json.*
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method


/**
 * @author chh
 *
 */
class SavedSearchService {

    def configurationService
    def transactional = false

    def saveSearch(userId, queryString, title = null, description = null) {
        def http = new HTTPBuilder("${configurationService.getElasticSearchUrl()}/ddb/savedSearch")

        def savedSearchId

        http.request(Method.POST, ContentType.JSON) { req ->
            body = [
                user: userId,
                queryString: queryString,
                title: title,
                description: description,
                createdAt: new Date().getTime()
            ]

            response.success = { resp, json ->
                savedSearchId = json._id
                log.info "Saved Search with the ID ${savedSearchId} is created."
                refresh()
            }
        }
        savedSearchId
    }

    // TODO: move to a util class.
    private refresh() {
        def http = new HTTPBuilder("${configurationService.getElasticSearchUrl()}/ddb/_refresh")

        log.info "refreshing index ddb..."
        http.request(Method.POST, ContentType.JSON) { req ->
            response.success = { resp, json ->
                log.info "Response: ${json}"
                log.info "finished refreshing index ddb."
            }
        }
    }
}
