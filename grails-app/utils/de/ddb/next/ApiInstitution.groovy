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

import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.web.util.WebUtils

import de.ddb.common.ApiConsumer
import de.ddb.common.constants.FacetEnum
import de.ddb.common.constants.SearchParamEnum


class ApiInstitution {

    private static final log = LogFactory.getLog(this)

    def getInstitutionViewByItemId(String id, String url) {
        log.debug("get insitution view by item id: ${id}")
        def uriPath = "/items/" + id + "/view"
        def apiResponse = ApiConsumer.getXml(url, uriPath)
        if(!apiResponse.isOk()){
            log.error "Xml: xml file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return apiResponse.getResponse()
    }

    def getChildrenOfInstitutionByItemId(String id, String url) {
        log.debug("get children of institution by item id: ${id}")
        def uriPath = "/hierarchy/" + id + "/children"
        def apiResponse = ApiConsumer.getJson(url, uriPath)
        if(!apiResponse.isOk()){
            log.error "Json: json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return apiResponse.getResponse()
    }

    def getParentsOfInstitutionByItemId(String id, String url) {
        log.debug("get parent of institution by item id: ${id}")
        def uriPath = "/hierarchy/" + id + "/parent"
        def apiResponse = ApiConsumer.getJson(url, uriPath)
        if(!apiResponse.isOk()){
            log.error "Json: json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return apiResponse.getResponse()
    }

    def getFacetValuesX(String provName, String url) {
        log.debug("get facets values for: ${provName}")
        int shortLength = 50
        String shortQuery = (provName.length() > shortLength ? provName.substring(0, shortLength) : provName)
        def uriPath = "/search/facets/provider_fct"
        def query = [(SearchParamEnum.QUERY.getName()):"${shortQuery}" ]
        def apiResponse = ApiConsumer.getJson(url, uriPath, false, query)
        if(!apiResponse.isOk()){
            log.error "Json: json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return apiResponse.getResponse()
    }

    def getProviderObjectCount(String provName, String url) {
        def result = 0
        def query = [(SearchParamEnum.QUERY.getName()):"*",
                     (SearchParamEnum.FACET.getName()):FacetEnum.PROVIDER.getName(),
                     (FacetEnum.PROVIDER.getName()):"${provName}",
                     (SearchParamEnum.ROWS.getName()):"0"]
        def apiResponse = ApiConsumer.getJson(url, "/search", false, query)
        if(!apiResponse.isOk()){
            log.error "Json: json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        def jsonResult = apiResponse.getResponse()
        def providerFct = jsonResult.facets.find {e -> e.field == "provider_fct"}
        if (providerFct?.facetValues?.count?.size() > 0) {
            result = providerFct.facetValues.count[0]
        }
        return result
    }
}
