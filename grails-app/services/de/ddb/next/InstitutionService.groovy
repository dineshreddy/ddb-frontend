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

import org.codehaus.groovy.grails.web.util.WebUtils

import de.ddb.next.cluster.Binning
import de.ddb.next.cluster.ClusterCache
import de.ddb.next.cluster.InstitutionMapModel

class InstitutionService {

    private static final def LETTERS='A'..'Z'

    private static final def NUMBERS = 0..9

    private static final def NUMBER_KEY = '0-9'

    def transactional = false

    def grailsApplication

    def grailsLinkGenerator

    def configurationService

    def servletContext

    def findAll() {
        def totalInstitution = 0
        def allInstitutions = [data: [:], total: totalInstitution]
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), '/institutions')
        if (apiResponse.isOk()) {
            def institutionList = apiResponse.getResponse()
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
        }
        else {
            log.error "findAll: Json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }

        return allInstitutions
    }

    def getClusteredInstitutions(def institutions, List selectedSectorList){
        log.info "getClusteredInstitutions(): sectorList="+selectedSectorList

        if(servletContext.getAttribute(ClusterCache.CONTEXT_ATTRIBUTE_NAME) == null){
            servletContext.setAttribute(ClusterCache.CONTEXT_ATTRIBUTE_NAME, new ClusterCache())
        }

        ClusterCache cache = servletContext.getAttribute(ClusterCache.CONTEXT_ATTRIBUTE_NAME)
        if(cache.getCluster(selectedSectorList) == null){
            log.info "getClusteredInstitutions(): no cache available for selected sectors. Calculating...."

            InstitutionMapModel institutionMapModel = new InstitutionMapModel()
            institutionMapModel.prepareInstitutionsData(institutions)

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
            def dataSets = institutionMapModel.selectSectors(sectors)



            def mapObjects = []
            for (def i = 0; i < dataSets.size(); i++) {
                mapObjects.push(dataSets[i].objects)
            }


            Binning binning = new Binning()
            binning.setObjects(mapObjects)
            def circleSets = binning.getSet().circleSets

            // Remove the "fatherBin" variable from the circles, otherwise the JSON-ing can throw errors.
            for(int i=0;i<circleSets.size(); i++){
                for(int j=0;j<circleSets[i][0].size();j++){
                    circleSets[i][0][j].fatherBin = null
                }
            }


            cache.addCluster(selectedSectorList, circleSets)
        }else{
            log.info "getClusteredInstitutions(): cache found. Answering with cached result."
        }

        def result = ["circleSets": cache.getCluster(selectedSectorList)]
        return result
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

}