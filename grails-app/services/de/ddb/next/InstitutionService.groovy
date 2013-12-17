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
import de.ddb.next.cluster.InstitutionMapModel
import de.ddb.next.cluster.Point

class InstitutionService {

    private static final def LETTERS='A'..'Z'

    private static final def NUMBERS = 0..9

    private static final def NUMBER_KEY = '0-9'

    def transactional = false

    def grailsApplication

    def grailsLinkGenerator

    def configurationService

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

    def getClusteredInstitutions(def institutions){
        println "####################### 00 getClusteredInstitutions"
        println institutions

        println "####################### 01 transformation"
        Point newPoint = new Point("49.2", "8.3")
        newPoint.transform("EPSG:4326", "EPSG:900913")

        println "####################### 02 mapModel"
        InstitutionMapModel institutionMapModel = new InstitutionMapModel()
        institutionMapModel.prepareInstitutionsData(institutions)
        //def allMapData = institutionMapModel.getAllMapData()

        println "####################### 03 sectors"
        def sectors = [:]
        sectors['selected'] = []
        sectors['deselected'] = []

        def sectorData1 = [:]
        sectorData1['sector'] = "sec_01"
        sectorData1['name'] = "sec_01"
        sectors['deselected'].push(sectorData1)

        def sectorData2 = [:]
        sectorData2['sector'] = "sec_02"
        sectorData2['name'] = "sec_02"
        sectors['deselected'].push(sectorData2)

        def sectorData3 = [:]
        sectorData3['sector'] = "sec_03"
        sectorData3['name'] = "sec_03"
        sectors['deselected'].push(sectorData3)

        def sectorData4 = [:]
        sectorData4['sector'] = "sec_04"
        sectorData4['name'] = "sec_04"
        sectors['deselected'].push(sectorData4)

        def sectorData5 = [:]
        sectorData5['sector'] = "sec_05"
        sectorData5['name'] = "sec_05"
        sectors['deselected'].push(sectorData5)

        def sectorData6 = [:]
        sectorData6['sector'] = "sec_06"
        sectorData6['name'] = "sec_06"
        sectors['deselected'].push(sectorData6)

        def sectorData7 = [:]
        sectorData7['sector'] = "sec_07"
        sectorData7['name'] = "sec_07"
        sectors['deselected'].push(sectorData7)

        def dataSets = institutionMapModel.selectSectors(sectors)
        println dataSets

        def mapObjects = []
        for (def i = 0; i < dataSets.size(); i++) {
            mapObjects.push(dataSets[i].objects)
        }
        println "####################### 04 mapObjects"
        println mapObjects


        println "####################### 05 Binning"
        Binning binning = new Binning()
        binning.setObjects(mapObjects)
        println binning.getSet()
        println "####################### 06 Binning"

        return ["a": "b"]
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