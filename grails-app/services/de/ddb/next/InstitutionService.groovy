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

import grails.plugin.cache.Cacheable

import org.codehaus.groovy.grails.web.util.WebUtils

import de.ddb.common.ApiConsumer
import de.ddb.common.ApiResponse
import de.ddb.common.CommonInstitutionService
import de.ddb.common.FavoritesService
import de.ddb.common.beans.User
import de.ddb.next.cluster.Binning
import de.ddb.next.cluster.ClusterCache
import de.ddb.next.cluster.InstitutionMapModel

class InstitutionService extends CommonInstitutionService {

    private static final def LETTERS='A'..'Z'

    private static final def NUMBER_KEY = '0-9'

    // values from Nominatim service
    private static final String GERMANY = "Deutschland"
    private static final String EUROPE = "European Union"
    private static final String EUROPE_GERMAN = "Europäische Union"
    private static final String EUROPE_SHORT = "Europe"

    def transactional = false

    def grailsApplication
    def grailsLinkGenerator
    def servletContext
    def bookmarksService
    def favoritesService

    /**
     * Return all institutions from the backend
     *
     * The value of this method is cached!
     *
     * @return all institutions from the backend
     */
    @Cacheable(value="institutionCache", key="'findAll'")
    def findAll() {
        log.info("findAll()")
        ApiResponse responseWrapper = ApiConsumer.getJson(configurationService.getBackendUrl(), "/institutions", false, [:])
        if(!responseWrapper.isOk()){
            responseWrapper.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }

        return responseWrapper.getResponse()
    }


    /**
     * Returns all archives that have items.
     *
     * The value of this method is cached!
     *
     * @return all archives that have items.
     */
    @Cacheable(value="institutionCache", key="'findAllArchiveInstitutionsWithItems'")
    def findAllArchiveInstitutionsWithItems() {
        ApiResponse responseWrapper = ApiConsumer.getJson(configurationService.getBackendUrl(), "/institutions", false, ["hasItems": "true"])
        if(!responseWrapper.isOk()){
            responseWrapper.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return responseWrapper.getResponse()
    }

    /**
     * Returns all archives ordered by alphabet.
     *
     * The value of this method is cached!
     *
     * @return all archives 
     */
    def findAllByAlphabet() {
        def totalInstitution = 0
        def allInstitutions = [data: [:], total: totalInstitution]

        def institutionList = grailsApplication.mainContext.institutionService.findAll()
        def institutionByFirstChar = buildIndex()

        institutionList.each { it ->
            totalInstitution++

            def firstChar = it?.name[0]?.toUpperCase()
            it.firstChar = firstChar

            /*
             * mark an institution as the first one that start with the
             * character. We will use it for assigning the id in the HTML.
             * See: views/institutions/_listItem.gsp
             * */
            if (LETTERS.contains(firstChar) && institutionByFirstChar.get(firstChar)?.size() == 0) {
                it.isFirst = true
            }

            it.sectorLabelKey = 'ddbnext.' + it.sector
            buildChildren(it, totalInstitution)
            institutionByFirstChar = putToIndex(institutionByFirstChar, addUri(it), firstChar)
        }

        allInstitutions.data = institutionByFirstChar
        allInstitutions.total = getTotal(institutionList)

        return allInstitutions
    }

    def getChildren(String institutionId) {
        def result = []
        def institutions = grailsApplication.mainContext.institutionService.findAll()
        def institution = institutions.find {it.id == institutionId}

        if (institution) {
            result = institution.children
        }
        return result
    }

    def getClusteredInstitutions(def institutions, List selectedSectorList, int cacheValidInDays, boolean onlyInstitutionWithData){
        log.info "getClusteredInstitutions(): sectorList="+selectedSectorList

        // Get the ClusterCache Object from the application context
        if(servletContext.getAttribute(ClusterCache.CONTEXT_ATTRIBUTE_NAME) == null){
            servletContext.setAttribute(ClusterCache.CONTEXT_ATTRIBUTE_NAME, new ClusterCache())
        }
        ClusterCache cache = servletContext.getAttribute(ClusterCache.CONTEXT_ATTRIBUTE_NAME)

        // If the cache does not yet contain cluster data for the selected sectors
        if(cache.getCluster(selectedSectorList, onlyInstitutionWithData) == null){
            log.info "getClusteredInstitutions(): no cache available for selected sectors. Calculating...."

            // Start of the actual javascript logic from IAIS
            InstitutionMapModel institutionMapModel = new InstitutionMapModel()
            institutionMapModel.prepareInstitutionsData(institutions)

            // Transform sector data to the required format
            def sectors = ["selected":[], "deselected":[]]
            def allSectors = [
                "sec_01",
                "sec_02",
                "sec_03",
                "sec_04",
                "sec_05",
                "sec_06",
                "sec_07"
            ]
            selectedSectorList.each {
                def entry = ["sector": it, "name": it]
                sectors["selected"].push(entry)
            }
            def unselectedSectorList = allSectors.minus(selectedSectorList)
            unselectedSectorList.each {
                def entry = ["sector": it, "name": it]
                sectors["deselected"].push(entry)
            }

            // Filter the institutions with the the selected sectors
            def dataSets = institutionMapModel.selectSectors(sectors)

            // Feed the cluster algorithm with the filtered institutions
            def mapObjects = []
            for (def i = 0; i < dataSets.size(); i++) {
                mapObjects.push(dataSets[i].objects)
            }

            // Perform the actual clustering
            Binning binning = new Binning()
            binning.setObjects(mapObjects)
            def circleSets = binning.getSet().circleSets


            // Transform the resulting data structure to a lot more bandwidth friendly data structure (4MB -> 1MB)
            def clusterContainer = [:]

            // Collect all institutions available for the given selection
            clusterContainer["institutions"] = [:]
            dataSets[0].objects.each {dataObject ->
                def institutionId = dataObject.index
                clusterContainer["institutions"][institutionId] = [:]
                clusterContainer["institutions"][institutionId]["name"] = dataObject.description.node.name
                clusterContainer["institutions"][institutionId]["sector"] = dataObject.description.node.sector
                clusterContainer["institutions"][institutionId]["locationDisplayName"] = fixNominatimIssues(dataObject.description.node.locationDisplayName)
                clusterContainer["institutions"][institutionId]["children"] = []
                clusterContainer["institutions"][institutionId]["parents"] = []
            }

            // Go over all the Cortex institutions and transfer children/parents information
            def childrenIds = []
            institutions.institutions.each {institution ->
                def institutionId = institution.id
                if(institution.children != null){

                    // Transfer child informations, if the institution is in the current selection
                    if(clusterContainer["institutions"][institutionId] != null){
                        for(int j=0;j<institution.children.size();j++){
                            def child = institution.children[j]
                            child.locationDisplayName = fixNominatimIssues(child.locationDisplayName)
                            def childId = child.id
                            if(clusterContainer["institutions"][childId] != null){ // only add child if child is also in sector selection
                                clusterContainer["institutions"][institutionId].children.push(child)
                                childrenIds.push(childId)
                            }
                        }
                    }

                    // Transfer parent information, if a child is in the current selection
                    institution.children.each {child ->
                        def childId = child.id
                        if(clusterContainer["institutions"][childId] != null){ // only add parent if child is also in sector selection
                            clusterContainer["institutions"][childId]["parents"].push(institutionId)

                            // add the parent informations to the list of used institutions
                            if(clusterContainer["institutions"][institutionId] == null){
                                clusterContainer["institutions"][institutionId] = [:]
                                clusterContainer["institutions"][institutionId]["name"] = institution.name
                                clusterContainer["institutions"][institutionId]["sector"] = institution.sector
                                clusterContainer["institutions"][institutionId]["children"] = [child]
                                clusterContainer["institutions"][institutionId]["parents"] = []
                            }else{
                                boolean alreadyContainsChild = clusterContainer["institutions"][institutionId]["children"].contains(child)
                                if(!alreadyContainsChild){
                                    clusterContainer["institutions"][institutionId]["children"].push(child)
                                }
                            }
                        }
                    }
                }
            }

            // Collect the cluster informations with institutionId references to the institution details above
            clusterContainer["clusters"] = []
            for(int zoom=0;zoom<circleSets.size(); zoom++){
                clusterContainer.clusters.push([])
                for(int j=0;j<circleSets[zoom][0].size();j++){
                    def circleObject = circleSets[zoom][0][j]
                    def cluster = [:]
                    cluster["x"] = circleObject.originX
                    cluster["y"] = circleObject.originY
                    cluster["radius"] = circleObject.radius
                    cluster["institutions"] = []
                    for(int k=0; k<circleObject.elements.size(); k++){
                        def institutionId = circleObject.elements[k].index
                        cluster["institutions"].push(institutionId)
                    }
                    clusterContainer["clusters"][zoom].push(cluster)
                }
            }

            cache.addCluster(selectedSectorList, clusterContainer, cacheValidInDays*24*60*60*1000, onlyInstitutionWithData)
        }else{
            log.info "getClusteredInstitutions(): cache found. Answering with cached result."
        }

        def result = ["data": cache.getCluster(selectedSectorList, onlyInstitutionWithData)]
        return result
    }

    /**
     * Fix issues with the Nominatim service:
     * - replace empty values with dummy values
     * - trim value
     * - ensure all entries end with "European Union"
     * - replace "Europäische Union" with "European Union"
     * - remove too detailed values like street name
     * - remove zip code
     * - reverse order
     *
     * @param locationDisplayName value from the Nominatim service
     *
     * @return fixed value
     */
    private String fixNominatimIssues(String locationDisplayName) {
        def result

        if (locationDisplayName) {
            result = locationDisplayName.tokenize(",")*.trim().reverse()
            if (result[0] != EUROPE) {
                if (result[0] == EUROPE_GERMAN || result[0] == EUROPE_SHORT) {
                    result[0] = EUROPE
                }
                else {
                    result.add(0, EUROPE)
                }
            }

            // remove zip code
            result.remove(2)

            // remove too detailed values like street name
            result = result.take(result.size() - 2)
        }
        else {
            result = [
                EUROPE,
                GERMANY
            ]
        }

        // enlarge the list to 7 elements
        for (int index = result.size(); index < 7; index++) {
            result += ""
        }

        return result.join(",")
    }

    private getTotal(rootList) {
        def total = rootList.size()

        for (root in rootList) {
            if (root.children?.size() > 0) {
                total = total + root.children.size()
                total = total + countDescendants(root.children)
            }
        }

        return total
    }

    private countDescendants(children) {
        def totalDescendants = 0

        for (institution in children) {
            if(institution.children) {
                totalDescendants = totalDescendants + institution.children.size()
                totalDescendants = totalDescendants + countDescendants(institution.children)
            }
        }
        return totalDescendants
    }

    private putToIndex(institutionByFirstLetter, institutionWithUri, firstLetter) {
        switch(firstLetter) {
            case 'Ä':
                institutionByFirstLetter['A'].add(institutionWithUri)
                break
            case 'Ö':
                institutionByFirstLetter['O'].add(institutionWithUri)
                break
            case 'Ü':
                institutionByFirstLetter['U'].add(institutionWithUri)
                break
            default:
                institutionByFirstLetter[firstLetter].add(institutionWithUri)
        }
        return institutionByFirstLetter
    }

    private buildChildren(institution, counter) {
        if(institution.children?.size() > 0 ) {
            institution.children.each { child ->
                child.uri = buildUri(child.id)
                child.sectorLabelKey = 'ddbnext.' + child.sector
                child.parentId = institution.id
                child.firstChar = child?.name[0]?.toUpperCase()
                buildChildren(child, counter)
            }
        }
    }

    private def buildIndex() {
        // create a map with empty arrays as initial values.
        def institutionByFirstLetter = [:].withDefault{ []}

        // use A..Z as keys
        LETTERS.each {
            institutionByFirstLetter[it] = []
        }

        // add the '0-9' as the last key for institutions start with a number.
        institutionByFirstLetter[NUMBER_KEY] = []

        return institutionByFirstLetter
    }

    private def addUri(json) {
        json.uri = buildUri(json.id)
        return json
    }

    private def buildUri(id) {
        grailsLinkGenerator.link(url: [controller: 'institution', action: 'showInstitutionsTreeByItemId', id: id ])
    }

    /**
     * Returns the search result for the institution highlight items
     *
     * @param institutionid the institution id to search the highlights
     * @param offset the offset of the search result
     * @param rows the number of items to retrieve
     */
    def getInstitutionHighlights(def institutionid, int offset, int rows) {
        def result = [:]
        def bookmarkfolder = bookmarksService.findPublicFolderByInstitutionId(institutionid)

        if (bookmarkfolder) {
            User user = new User()

            user.id = bookmarkfolder.userId
            result = favoritesService.getFavoriteList(user, bookmarkfolder, FavoritesService.ORDER_ASC,
                    FavoritesService.ORDER_BY_NUMBER)

            // paging
            if (offset != 0) {
                result = result.drop(offset)
                result = result.take(rows)
            }
            else {
                result = result.take(rows)
            }
        }
        return result
    }
}
