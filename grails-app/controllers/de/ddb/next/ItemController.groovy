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

import org.springframework.context.NoSuchMessageException
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.next.beans.User
import de.ddb.next.constants.CortexNamespace
import de.ddb.next.exception.ItemNotFoundException

class ItemController {

    private static final def HTTP ='http://'
    private static final def HTTPS ='https://'
    static defaultAction = "findById"

    def itemService
    def searchService
    def grailsLinkGenerator
    def configurationService
    def messageSource
    def bookmarksService
    def sessionService
    def cultureGraphService

    private boolean isFavorite(itemId) {
        def User user = sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
        if(user != null) {
            return bookmarksService.isBookmarkOfUser(itemId, user.getId())
        }else{
            return false
        }
    }

    def delFavorite(itemId) {
        boolean vResult = false
        log.info "non-JavaScript: delFavorite " + itemId
        def User user = sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
        if (user != null) {
            // Bug: DDBNEXT-626: if (bookmarksService.deleteBookmarksByBookmarkIds(user.getId(), [pId])) {
            bookmarksService.deleteBookmarksByItemIds(user.getId(), [itemId])
            def isFavorite = isFavorite(itemId)
            if (isFavorite == response.SC_NOT_FOUND) {
                log.info "non-JavaScript: delFavorite " + itemId + " - success!"
                vResult = true
            }
            else {
                log.info "non-JavaScript: delFavorite " + itemId + " - failed..."
            }
        }
        else {
            log.info "non-JavaScript: addFavorite " + itemId + " - failed (unauthorized)"
        }
        return vResult
    }

    def addFavorite(itemId) {
        boolean vResult = false
        log.info "non-JavaScript: addFavorite " + itemId
        def User user = sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
        if (user != null) {
            if (bookmarksService.addBookmark(user.getId(), itemId)) {
                log.info "non-JavaScript: addFavorite " + itemId + " - success!"
                vResult = true
            }
            else {
                log.info "non-JavaScript: addFavorite " + itemId + " - failed..."
            }
        }
        else {
            log.info "non-JavaScript: addFavorite " + itemId + " - failed (unauthorized)"
        }
        return vResult
    }

    def findById() {
        try {
            //Check if Item-Detail was called from search-result and fill parameters
            def searchResultParameters = handleSearchResultParameters(params, request)
            def id = params.id
            def item = itemService.findItemById(id)

            if("404".equals(item)){
                throw new ItemNotFoundException()
            }

            def isFavorite = isFavorite(id)
            log.info("params.reqActn = ${params.reqActn} --> " + params.reqActn)
            if (params.reqActn) {
                if (params.reqActn.equalsIgnoreCase("add") && (isFavorite == response.SC_NOT_FOUND) && addFavorite(id)) {
                    isFavorite = response.SC_FOUND
                }
                else if (params.reqActn.equalsIgnoreCase("del") && (isFavorite == response.SC_FOUND) && delFavorite(id)) {
                    isFavorite = response.SC_NOT_FOUND
                }
            }

            def binaryList = itemService.findBinariesById(id)
            def binariesCounter = itemService.binariesCounter(binaryList)

            def flashInformation = [:]
            flashInformation.all = [binaryList.size]
            flashInformation.images = [binariesCounter.images]
            flashInformation.audios = [binariesCounter.audios]
            flashInformation.videos = [binariesCounter.videos]

            if (item.pageLabel?.isEmpty()) {
                item.pageLabel = item.title
            }

            def licenseInformation = buildLicenseInformation(item)

            // TODO: handle 404 and failure separately. HTTP Status Code 404, should
            // to `not found` page _and_ Internal Error should go to `internal server
            // error` page. We should send also the HTTP Status Code 404 or 500 to the
            // Client.
            if(item == '404' || item?.failure) {
                redirect(controller: 'error')
            } else {
                def itemUri = request.forwardURI
                def fields = translate(item.fields, convertToHtmlLink)

                if(configurationService.isCulturegraphFeaturesEnabled()){
                    fields = createEntityLinks(fields)
                }

                if(params.print){
                    renderPdf(template: "itemPdf", model: [
                        itemUri: itemUri,
                        viewerUri: item.viewerUri,
                        title: item.title,
                        item: item.item,
                        itemId: id,
                        institution : item.institution,
                        institutionImage: item.institutionImage,
                        originUrl: item.originUrl,
                        fields: fields,
                        binaryList: binaryList,
                        pageLabel: item.pageLabel,
                        firstHit: searchResultParameters["searchParametersMap"]["firstHit"],
                        lastHit: searchResultParameters["searchParametersMap"]["lastHit"],
                        hitNumber: params["hitNumber"],
                        results: searchResultParameters["resultsItems"],
                        searchResultUri: searchResultParameters["searchResultUri"],
                        flashInformation: flashInformation,
                        license: licenseInformation],
                    filename: "Item-Detail.pdf")
                }else{
                    render(view: "item", model: [
                        itemUri: itemUri,
                        viewerUri: item.viewerUri,
                        title: item.title,
                        item: item.item,
                        itemId: id,
                        institution: item.institution,
                        institutionImage: item.institutionImage,
                        originUrl: item.originUrl,
                        fields: fields,
                        binaryList: binaryList,
                        pageLabel: item.pageLabel,
                        firstHit: searchResultParameters["searchParametersMap"]["firstHit"],
                        lastHit: searchResultParameters["searchParametersMap"]["lastHit"],
                        hitNumber: params["hitNumber"],
                        results: searchResultParameters["resultsItems"],
                        searchResultUri: searchResultParameters["searchResultUri"],
                        flashInformation: flashInformation,
                        license: licenseInformation,
                        isFavorite: isFavorite,
                        baseUrl: configurationService.getSelfBaseUrl()
                    ])

                }
            }
        } catch(ItemNotFoundException infe){
            log.error "findById(): Request for nonexisting item with id: '" + params?.id + "'. Going 404..."
            forward controller: "error", action: "itemNotFound"
        }
    }


    def createEntityLinks(fields){
        fields.each {
            def valueTags = it.value
            valueTags.each { valueTag ->

                def resource = getTagAttribute(valueTag, CortexNamespace.RDF.prefix, "resource")

                if(resource != null && !resource.isEmpty()){
                    if(cultureGraphService.isValidGndUri(resource)){
                        def entityId = cultureGraphService.getGndIdFromGndUri(resource)
                        valueTag.@entityId = entityId
                    }
                }
            }
        }
        return fields
    }

    def translate(fields, convertToHtmlLink) {
        fields.each {
            it = convertToHtmlLink(it)
            def messageKey = 'ddbnext.' + it.'@id'
            def translated = message(code: messageKey)
            if(translated != messageKey) {
                it.name = translated
            } else {
                it.name = it.name.toString().capitalize()
                log.warn 'can not find message property: ' + messageKey + ' use ' + it.name + ' instead.'
            }
        }
    }

    def convertToHtmlLink = { field ->
        for(int i=0; i<field.value.size(); i++) {
            def value = field.value[i]

            def fieldValue = field.value[i].toString()
            if(fieldValue.startsWith(HTTP) || fieldValue.startsWith(HTTPS)) {
                field.value[i] = '<a href="' + fieldValue + '">' + fieldValue + '</a>'
            }
        }

        return field
    }

    def parents() {
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), "/hierarchy/" + params.id + "/parent")
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(request)
        }
        def jsonResp = apiResponse.getResponse()
        render(contentType:"application/json", text: jsonResp)
    }

    def children() {
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(),
                "/hierarchy/" + params.id + "/children", false,
                ["rows":501])
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(request)
        }
        def jsonResp = apiResponse.getResponse()

        render(contentType:"application/json", text: jsonResp)
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
        def resultsItems
        def searchResultUri

        if (reqParameters["hitNumber"] && reqParameters["query"] != null) {
            def urlQuery = searchService.convertQueryParametersToSearchParameters(reqParameters)

            //Search and return 3 Hits: previous, current and last
            reqParameters["hitNumber"] = reqParameters["hitNumber"].toInteger()
            urlQuery["rows"] = 3
            if (reqParameters["hitNumber"] > 1) {
                urlQuery["offset"] = reqParameters["hitNumber"] - 2
            }
            else {
                urlQuery["offset"] = 0
            }
            def apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, urlQuery)
            if(!apiResponse.isOk()){
                log.error "Json: Json file was not found"
                apiResponse.throwException(request)
            }
            resultsItems = apiResponse.getResponse()

            //Workaround for last-hit (Performance-issue)
            if (reqParameters.id && reqParameters.id.equals("lasthit")) {
                reqParameters.id = resultsItems.results["docs"][1].id
            }
            searchResultParameters["resultsItems"] = resultsItems

            //generate link back to search-result. Calculate Offset.
            def searchGetParameters = searchService.getSearchGetParameters(reqParameters)
            def offset = 0
            if (reqParameters["rows"]) {
                offset = ((Integer)((reqParameters["hitNumber"]-1)/reqParameters["rows"]))*reqParameters["rows"]
            }
            searchGetParameters["offset"] = offset
            searchResultUri = grailsLinkGenerator.link(url: [controller: 'search', action: 'results', params: searchGetParameters ])
            searchResultParameters["searchResultUri"] = searchResultUri
            searchResultParameters["searchParametersMap"] = reqParameters
        }

        return searchResultParameters
    }

    private def buildLicenseInformation(def item){
        def licenseInformation

        if(item.item?.license && !item.item.license.isEmpty()){

            def licenseId = getTagAttribute(item.item.license, CortexNamespace.RDF.prefix, "resource")

            def propertyId = convertUriToProperties(licenseId)

            licenseInformation = [:]


            def text
            def url
            def img
            try{
                def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
                text = messageSource.getMessage("ddbnext.license.text."+propertyId, null, locale)
                url = messageSource.getMessage("ddbnext.license.url."+propertyId, null, locale)
                img = messageSource.getMessage("ddbnext.license.img."+propertyId, null, locale)
            }catch(NoSuchMessageException e){
                log.error "findById(): no I18N information for license '"+licenseInformation.id+"' in license.properties"
            }
            if(!text){
                text = item.item.license.toString()
            }
            if(!url){
                url = item.item.license["@url"].toString()
            }

            licenseInformation.text = text
            licenseInformation.url = url
            licenseInformation.img = img

        }

        return licenseInformation
    }

    def convertUriToProperties(def uri){
        if(uri){
            // http://creativecommons.org/licenses/by-nc-nd/3.0/de/

            def converted = uri.toString()
            converted = converted.replaceAll("http://","")
            converted = converted.replaceAll("https://","")
            converted = converted.replaceAll("[^A-Za-z0-9]", ".")
            if(converted.startsWith(".")){
                converted = converted.substring(1)
            }
            if(converted.endsWith(".")){
                converted = converted.substring(0, converted.size()-1)
            }
            return converted
        }else{
            return ""
        }
    }

    private String getTagAttribute(def tag, String namespacePrefix, String attributeName ) {
        String out = null
        out = tag["@"+namespacePrefix+":"+attributeName].toString().trim()
        if(out == null || out.isEmpty()){
            out = tag["@"+CortexNamespace.NS2.prefix+":"+attributeName].toString().trim()
        }
        return out
    }

    def showXml() {

        def itemId = params.id

        response.contentType = "text/xml"
        response.outputStream << itemService.fetchXMLMetadata(itemId)

    }
}
