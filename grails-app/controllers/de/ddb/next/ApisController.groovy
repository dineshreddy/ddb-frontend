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

import grails.converters.JSON

import java.text.SimpleDateFormat

import net.sf.json.JSONNull
import de.ddb.common.ApiConsumer
import de.ddb.common.ApiResponse
import de.ddb.common.JsonUtil
import de.ddb.common.ApiResponse.HttpStatus

class ApisController {
    private static final int MAX_AGE = 1 * 24 * 60 * 60 // 1 day
    private static final SimpleDateFormat RFC822DATEFORMAT =
    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)

    static {
        RFC822DATEFORMAT.setTimeZone(TimeZone.getTimeZone("GMT"))
    }

    def apisService
    def configurationService
    def institutionService

    def search() {
        def resultList = [:]
        def docs = []
        def query = apisService.getQueryParameters(params)

        //Use query filter if roles were selected
        def filteredQuery = apisService.filterForRoleFacets(query)

        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(),'/search', false, filteredQuery)
        if(!apiResponse.isOk()){
            if(apiResponse.getStatus() == HttpStatus.HTTP_400){
                render(status: response.SC_BAD_REQUEST)
                return
            }
            else {
                log.error "Json: Json file was not found"
                apiResponse.throwException(request)
            }
        }
        def jsonResp = apiResponse.getResponse()
        jsonResp.results["docs"].get(0).each{

            def tmpResult = [:]
            String title
            String subtitle
            def thumbnail
            def media = []

            title = (it.title instanceof JSONNull)?"":it.title
            subtitle = (it.subtitle instanceof JSONNull)?"":it.subtitle

            thumbnail = (it.thumbnail instanceof JSONNull)?"":it.thumbnail
            if(!JsonUtil.isAnyNull(it.media)){
                it.media.split (",").each{ media.add(it) }
            }

            tmpResult["id"] = it.id

            tmpResult["view"] = (it.view instanceof JSONNull)?"":it.view
            tmpResult["label"] = (it.label instanceof JSONNull)?"":it.label
            tmpResult["latitude"] = (it.latitude instanceof JSONNull)?"":it.latitude
            tmpResult["longitude"] = (it.longitude instanceof JSONNull)?"":it.longitude
            tmpResult["category"] = (it.category instanceof JSONNull)?"":it.category
            tmpResult["preview"] = [title:title, subtitle: subtitle, media: media, thumbnail: thumbnail]
            docs.add(tmpResult)
        }
        resultList["facets"] = jsonResp.facets
        resultList["facets"] = removeNullValue(resultList["facets"])
        resultList["highlightedTerms"] = jsonResp.highlightedTerms
        resultList["correctedQuery"] = jsonResp.correctedQuery
        resultList["numberOfResults"] = jsonResp.numberOfResults
        resultList["randomSeed"] = jsonResp.randomSeed
        resultList["entities"] = jsonResp?.entities
        resultList["results"] = [ name:jsonResp.results.name,
            docs:docs,
            numberOfDocs:jsonResp.results.numberOfDocs]


        // Fix for Bug DDBNEXT-740: search for "null" causes 500 exception
        for(int i=0; i<resultList["highlightedTerms"].size(); i++) {
            if(resultList["highlightedTerms"][i] == "null") {
                resultList["highlightedTerms"][i] = "<null>"
            }
        }

        render resultList as JSON
    }

    private def removeNullValue(facets) {
        facets.each {
            if(it.facetValues.size()) {
                it.facetValues.each { facetVal ->
                    if(facetVal.value instanceof JSONNull) {
                        log.info "null...."
                        facetVal.value = ''
                    }
                }
            }
        }
        facets
    }

    def institutionsmap(){
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(),'/institutions/map', false, params)
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(request)
        }
        def jsonResp = apiResponse.getResponse()
        render (contentType:"text/json"){jsonResp}
    }

    def clusteredInstitutionsmap(){
        boolean onlyInstitutionsWithData = Boolean.parseBoolean(params.onlyInstitutionsWithData)

        // parse selected sector information from request
        def selectedSectors = params.selectedSectors
        def sectors = selectedSectors.tokenize(',[]')
        for(int i=0; i<sectors.size(); i++){
            sectors[i] = sectors[i].replaceAll("\"", "")
        }

        // get all available institutions from cortex
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(),'/institutions/map', false, ["clusterid":"-1","hasItems":"${onlyInstitutionsWithData}"])
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(request)
        }
        def institutions = apiResponse.getResponse()

        // get the clustered institutions
        def clusteredInstitutions =
                institutionService.getClusteredInstitutions(institutions, sectors, onlyInstitutionsWithData)

        // set cache headers for caching the ajax request
        response.addHeader("Last-Modified", getRfc2822Date())
        response.addHeader("Expires", getRfc2822Date(MAX_AGE))
        response.addHeader("Cache-Control", "max-age=" + MAX_AGE)
        response.addHeader("Pragma", "cache")


        // render the data as json
        render (contentType:"text/json"){clusteredInstitutions}
    }

    def autocomplete (){
        callSuggestEndpoint('/search/suggest')
    }

    def entitiesAutocomplete (){
        callSuggestEndpoint('/entities/suggest')
    }

    /**
     * This function should be obsolete once the
     * url : "http://backend.deutsche-digitale-bibliothek.de:9998/search/suggest/", would support JSONP and return the callback function
     * If that happens, the "myautocomplete.js" script should refer to the backend URL and not to this URL.
     * @return
     */
    private callSuggestEndpoint(def path) {
        def query = apisService.getQueryParameters(params)

        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(),path, false, query)
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(request)
        }
        def result = apiResponse.getResponse()
        if (query) {
            render "${params.callback}(${result as JSON})"
        } else {
            render (contentType:"text/json"){result}
        }
    }

    /**
     * Wrapper to support streaming of files from the backend
     * @return OutPutStream
     */
    def binary() {
        if (request.method == "HEAD") {
            render SC_OK
        }
        else {
            ApiResponse apiResponse = ApiConsumer.getBinaryStreaming(configurationService.getBackendUrl(),
                    "/binary/" + getFileNamePath(), response.outputStream)

            if (apiResponse.isOk()) {
                def responseObject = apiResponse.getResponse()
                response.setHeader("Cache-Control", "max-age=" + MAX_AGE)
                response.setHeader("Expires", getRfc2822Date(MAX_AGE))
                response.setHeader("Content-Disposition", "inline; filename=" + getFileNamePath().tokenize('/')[-1])
                response.setContentType(responseObject.get("Content-Type"))
                response.setContentLength(responseObject.get("Content-Length").toInteger())
            }
        }
    }

    def staticFiles() {
        def apiResponse = ApiConsumer.getBinaryStreaming(configurationService.getStaticUrl(), getFileNamePath() , response.outputStream)

        if (!apiResponse.isOk()) {
            if (apiResponse.status != ApiResponse.HttpStatus.HTTP_404) {
                log.error "fetching static content failed"
            }
            apiResponse.throwException(request)
        }

        def responseObject = apiResponse.getResponse()
        response.setHeader("Cache-Control", "max-age=" + MAX_AGE)
        response.setHeader("Expires", getRfc2822Date(MAX_AGE))
        response.setHeader("Content-Disposition", "inline; filename=" + ('/static/' + getFileNamePath()).tokenize('/')[-1])
        response.setContentType(responseObject.get("Content-Type"))
        response.setContentLength(responseObject.get("Content-Length").toInteger())
    }
    /**
     * Format RFC 2822 date
     *
     * @param seconds seconds to be added to current date
     *
     * @return RFC 2822 formatted date string
     */
    private String getRfc2822Date(int seconds = 0) {
        Calendar date = Calendar.getInstance()
        date.add(Calendar.SECOND, seconds)
        return RFC822DATEFORMAT.format(date.getTime())
    }

    private def getFileNamePath() {
        return cleanHtml(params.filename, 'none')
    }
}
