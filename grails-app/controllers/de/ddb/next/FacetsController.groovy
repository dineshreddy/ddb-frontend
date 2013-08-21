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

import org.springframework.web.servlet.support.RequestContextUtils


/**
 * Invoked from ajax request during the selection of filters for the search results page
 * 
 * @author ema
 *
 */
class FacetsController {

    static defaultAction = "facets"

    def searchService
    def configurationService


    def facetsList() {
        def urlQuery = searchService.convertQueryParametersToSearchFacetsParameters(params)

        urlQuery["query"] = (params.query)?params.query:""
        urlQuery["sort"] = "count_desc"

        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(),'/search/facets/'+params.name, false, urlQuery)
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(request)
        }

        def resultsItems = apiResponse.getResponse()

        def numberOfElements = (urlQuery["rows"])?urlQuery["rows"].toInteger():310

        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))

        def facetValues = searchService.getSelectedFacetValues(resultsItems, params.name, numberOfElements, params.query, locale)

        render (contentType:"text/json"){facetValues}
    }
}
