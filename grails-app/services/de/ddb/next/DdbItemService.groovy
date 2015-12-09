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

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import grails.plugin.cache.Cacheable
import grails.util.Holders

import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.ApiConsumer
import de.ddb.common.beans.User
import de.ddb.common.constants.CategoryFacetEnum
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.exception.ItemNotFoundException

class DdbItemService {
    private static final log = LogFactory.getLog(this)

    // ehcache name
    private static final String CACHE_NAME = "itemCache"

    def transactional = false
    def grailsApplication
    def configurationService
    def grailsLinkGenerator
    def searchService
    def messageSource
    def sessionService
    def cultureGraphService
    def bookmarksService
    def itemService
    def languageService

    /**
     * Used in the preparation of images for PDF
     * @param model
     * @return
     */
    def prepareImagesForPdf(model) {
        //ADD Hierarchy
        model.hierarchy=getHierarchyItem(model.itemId)

        def baseFolder= Holders.getApplicationContext().getResource("/images/").getFile().toString()
        def logoHeaderFile = '/logoHeaderSmall.png'
        def logoHeader = new File(baseFolder + logoHeaderFile)
        model.logo=logoHeader.bytes

        model.institutionImage = itemService.getContent(new URL(new URL(configurationService.getSelfBaseUrl()),
                model.institutionImage))

        if (model.license?.img) {
            model.licenseImage = itemService.getContent(new URL(new URL(configurationService.getSelfBaseUrl()),
                    grailsLinkGenerator.resource("plugin": "ddb-common", "file": model.license.img)))
        }

        //FONT for PDF
        model.fontKarbidWeb=grailsApplication.mainContext.getResourceByPath('/css/fonts/KarbidWeb.woff').file.bytes
        model.fontCalibri=grailsApplication.mainContext.getResourceByPath('/css/fonts/Calibri.ttf').file.bytes

        def viewerContent
        if (model.binaryList.size() > 0) {
            if (model.binaryList.first().preview.uri) {
                viewerContent = itemService.getContent(new URL(new URL(configurationService.getSelfBaseUrl()),
                        model.binaryList.first().preview.uri))
            }
            else if (model.binaryList.first().thumbnail.uri) {
                viewerContent = itemService.getContent(new URL(new URL(configurationService.getSelfBaseUrl()),
                        model.binaryList.first().thumbnail.uri))
            }
        }
        model.put("binariesListViewerContent", viewerContent)
        return model
    }

    /**
     * Gets the hierarchy up to the parent & then adds all siblings to the direct partners
     * Used in the PDF generation of an Item View. The normal Item View uses a JS version
     * Returns a flat list of the parents & all the siblings as "children" of the direct parent
     * @param id
     * @return List
     */
    def getHierarchyItem(String id) {
        def result = []
        def bottomUpHierarchy = getParent(id)

        if (bottomUpHierarchy.size() > 1) {
            def directParent = [:]

            bottomUpHierarchy[1].properties.each { k,v -> directParent[k] = v }
            directParent.id = bottomUpHierarchy[1].id
            result = bottomUpHierarchy.subList(2, bottomUpHierarchy.size()).reverse()
            directParent.children = itemService.getChildren(directParent.id)
            result.add(directParent)
        }
        return result
    }

    /**
     * Return all parents for the given item except institutions.
     * (see DDBNEXT-1885)
     *
     * @param itemId item id
     *
     * @return list of all parents
     */
    def getParent(String itemId) {
        def parents = itemService.getParent(itemId)

        // filter out institutions
        return parents.findAll { parent -> parent.type != "institution" }
    }

    def getFullItemModel(id) {
        def utils = WebUtils.retrieveGrailsWebRequest()
        def request = utils.getCurrentRequest()
        def response = utils.getCurrentResponse()
        def params = utils.getParameterMap()
        def itemId = id
        def itemUri = null

        //Check if Item-Detail was called from search-result and fill parameters
        def searchResultParameters = handleSearchResultParameters(params, request)

        //If the id is lastHit get the item id from the searchResultParameters
        if (itemId == 'lasthit') {
            itemId = searchResultParameters["lastItemId"]
            itemUri = grailsLinkGenerator.link(url: [controller: 'item', action: 'findById', id: itemId ])
        } else {
            itemUri = request.forwardURI
        }
        def item = itemService.findItemById(itemId)

        if("404".equals(item)){
            throw new ItemNotFoundException()
        }

        def isFavorite = isFavorite(itemId)
        def binaryList = itemService.findBinariesById(itemId)
        def binariesCounter = itemService.binariesCounter(binaryList)

        def flashInformation = [:]
        flashInformation.images = [binariesCounter.images]
        flashInformation.audios = [binariesCounter.audios]
        flashInformation.videos = [binariesCounter.videos]

        if (item.pageLabel?.isEmpty()) {
            item.pageLabel = item.title
        }

        def licenseInformation = itemService.buildLicenseInformation(item.item, request)

        def fields = translate(item.fields, itemService.convertToHtmlLink, request)

        if(configurationService.isCulturegraphFeaturesEnabled()){
            itemService.createEntityLinks(fields, configurationService.getContextUrl())
        }

        def similarItems = itemService.getSimilarItems(itemId)
        def itemSource = itemService.getItemXmlSource(itemId)
        def collection = new XmlSlurper().parseText(itemSource)
        def geometry = collection.monument.geoReference.geometry.text()

        return [
            itemUri: itemUri,
            viewerUri: item.viewerUri,
            title: item.title,
            item: item.item,
            itemId: itemId,
            institution: item.institution,
            institutionImage: item.institutionImage,
            originUrl: item.originUrl,
            fields: fields,
            binaryList: binaryList,
            pageLabel: item.pageLabel,
            firstHit: searchResultParameters["searchParametersMap"][SearchParamEnum.FIRSTHIT.getName()],
            lastHit: searchResultParameters["searchParametersMap"][SearchParamEnum.LASTHIT.getName()],
            hitNumber: params["hitNumber"],
            results: searchResultParameters["resultsItems"],
            searchResultUri: searchResultParameters["searchResultUri"],
            flashInformation: flashInformation,
            license: licenseInformation,
            isFavorite: isFavorite,
            baseUrl: configurationService.getSelfBaseUrl(),
            publicUrl: configurationService.getPublicUrl(),
            domainCanonic:configurationService.getDomainCanonic(),
            similarItems : similarItems,
            geometryInput: geometry
        ]
    }

    /**
     * Determines the total number of items and the number of items with digitalisat=true.
     *
     * @return item numbers
     */
    @Cacheable(value=DdbItemService.CACHE_NAME, key="'getNumberOfItems'")
    def getNumberOfItems() {
        return itemService.getNumberOfItems()
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

        //Item is called from the result list -> a hitNumber is available
        if (reqParameters["hitNumber"] && reqParameters[SearchParamEnum.QUERY.getName()] != null) {
            def urlQuery = searchService.convertQueryParametersToSearchParameters(reqParameters)

            //Search and return 3 Hits: previous, current and last
            try {
                reqParameters["hitNumber"] = reqParameters["hitNumber"].toInteger()
            }
            catch (NumberFormatException e) {
                reqParameters["hitNumber"] = 1
            }
            urlQuery[SearchParamEnum.ROWS.getName()] = 3
            if (reqParameters["hitNumber"] > 1) {
                urlQuery[SearchParamEnum.OFFSET.getName()] = reqParameters["hitNumber"] - 2
            }
            else {
                urlQuery[SearchParamEnum.OFFSET.getName()] = 0
            }

            //FIXME Sets the item category to objects! If we need the pagination also for institution details we need to build a switch!
            searchService.setCategory(urlQuery, CategoryFacetEnum.CULTURE.getName())

            def apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, urlQuery)
            if(!apiResponse.isOk()){
                log.error "Json: Json file was not found"
                apiResponse.throwException(httpRequest)
            }
            resultsItems = apiResponse.getResponse()

            //Workaround for last-hit (Performance-issue)
            if (reqParameters.id && reqParameters.id.equals("lasthit") && resultsItems.results["docs"]) {
                searchResultParameters["lastItemId"] = resultsItems.results["docs"][resultsItems.results["docs"].size() - 1].id
            }
            searchResultParameters["resultsItems"] = resultsItems

            //generate link back to search-result. Calculate Offset.
            def searchGetParameters = searchService.getSearchGetParameters(reqParameters)
            def offset = 0
            if (reqParameters[SearchParamEnum.ROWS.getName()]) {
                offset = ((Integer)((reqParameters["hitNumber"]-1)/reqParameters[SearchParamEnum.ROWS.getName()]))*reqParameters[SearchParamEnum.ROWS.getName()]
            }
            searchGetParameters[SearchParamEnum.OFFSET.getName()] = offset
            searchResultUri = grailsLinkGenerator.link(url: [controller: 'search', action: 'results', params: searchGetParameters ])
            searchResultParameters["searchResultUri"] = searchResultUri
            searchResultParameters["searchParametersMap"] = reqParameters
        }
        //In all other cases (item call from compare objects etc.) at least a query must be available
        else if (reqParameters[SearchParamEnum.QUERY.getName()] != null) {
            def searchGetParameters = searchService.getSearchGetParameters(reqParameters)
            searchResultUri = grailsLinkGenerator.link(url: [controller: 'search', action: 'results', params: searchGetParameters ])
            searchResultParameters["searchResultUri"] = searchResultUri
            searchResultParameters["searchParametersMap"] = reqParameters
        }

        return searchResultParameters
    }

    def translate(fields, convertToHtmlLink, httpRequest) {
        def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(httpRequest))

        fields.each {
            if (it) {
                it = convertToHtmlLink(it)
                def messageKey = 'ddbnext.' + it.id

                def translated = messageSource.getMessage(messageKey, null, messageKey, locale)
                if(translated != messageKey) {
                    it.name = translated
                } else {
                    it.name = it.name.toString().capitalize()
                    log.warn 'can not find message property: ' + messageKey + ' use ' + it.name + ' instead.'
                }
            }
        }
    }

    def boolean isFavorite(itemId) {
        def User user = sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
        if(user) {
            return bookmarksService.isBookmarkOfUser(itemId, user.getId())
        }else{
            return false
        }
    }
}
