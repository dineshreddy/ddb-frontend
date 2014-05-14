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

import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import de.ddb.common.constants.SearchParamEnum

class CompareController {
    def searchService
    def commonConfigurationService
    def itemService
    def LinkGenerator grailsLinkGenerator

    def index() {
        log.info "index()"

        def firstId = params.firstId
        def secondId = params.secondId

        def modelItem1 = itemService.getFullItemModel(firstId)+[position: "first"]
        def modelItem2 = itemService.getFullItemModel(secondId)+[position: "second"]

        def searchResultParameters = handleSearchResultParameters(params, request)

        def itemUri = request.forwardURI

        render(view: "compare", model: [
            firstId: firstId,
            secondId: secondId,
            itemUri: itemUri,
            modelItem1: modelItem1,
            modelItem2: modelItem2,
            searchResultUri: searchResultParameters["searchResultUri"],
            baseUrl: commonConfigurationService.getSelfBaseUrl()
        ])
    }


    /**
     * Get Data to build Search Result Navigation Bar for Item Detail View
     *
     * @param reqParameters requestParameters
     * @return Map with searchResult to build back + next links
     *  and searchResultUri for Link "Back to Search Result"
     */
    def handleSearchResultParameters(reqParameters, httpRequest) {
        def searchResultParameters = [:]
        searchResultParameters["searchParametersMap"] = [:]
        def searchResultUri

        if (reqParameters[SearchParamEnum.QUERY.getName()] != null) {
            //generate link back to search-result. Calculate Offset.
            def searchGetParameters = searchService.getSearchGetParameters(reqParameters)
            searchResultUri = grailsLinkGenerator.link(url: [controller: 'search', action: 'results', params: searchGetParameters ])
            searchResultParameters["searchResultUri"] = searchResultUri
            searchResultParameters["searchParametersMap"] = reqParameters
        }

        return searchResultParameters
    }
}
