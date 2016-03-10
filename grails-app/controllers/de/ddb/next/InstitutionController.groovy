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
import de.ddb.common.beans.item.CortexInstitution
import de.ddb.common.constants.SearchParamEnum

class InstitutionController {
    private static final String PAGENAME = "institutionList"

    def bookmarksService
    def configurationService
    def favoritesService
    def institutionService
    def itemService

    def show() {
        def allInstitution = institutionService.findAllByAlphabet()
        def institutionByFirstLetter = allInstitution.data

        def all = []
        institutionByFirstLetter?.each { all.addAll(it.value) }

        // no institutions
        institutionByFirstLetter.each { k,v ->
            if(institutionByFirstLetter[k]?.size() == 0) {
                institutionByFirstLetter[k] = true
            } else {
                institutionByFirstLetter[k] = false
            }
        }

        def index = []
        institutionByFirstLetter.each { index.add(it) }

        render (view: PAGENAME,  model: [
            domainCanonic: configurationService.getDomainCanonic(),
            index: index,
            pageName: PAGENAME,
            all: all,
            total: allInstitution?.total
        ])
    }

    def getJson() {
        render institutionService.findAllByAlphabet() as JSON
    }

    def showInstitutionsTreeByItemId() {
        def id = params.id
        def itemId = id
        log.debug("read insitution by item id: ${id}")
        CortexInstitution institution = institutionService.getInstitutionViewByItemId(id)
        def pageUrl = configurationService.getSelfBaseUrl() + request.forwardURI
        if (institution) {
            def jsonOrgParentHierarchy = institutionService.getParentsOfInstitution(itemService.getParent(id))
            log.debug("jsonOrgParentHierarchy: ${jsonOrgParentHierarchy}")
            if (jsonOrgParentHierarchy.size() == 1) {
                if (jsonOrgParentHierarchy[0].id != id) {
                    log.error("ERROR: id:${id} != OrgParent.id:${jsonOrgParentHierarchy[0].id}")
                    forward controller: 'error', action: "ERROR: id:${id} != OrgParent.id:${jsonOrgParentHierarchy[0].id}"
                }
            }
            else if (jsonOrgParentHierarchy.size() > 1) {
                itemId = jsonOrgParentHierarchy[jsonOrgParentHierarchy.size() - 1].id
            }
            log.debug("root itemId = ${itemId}")
            def countObjectsForProv = institutionService.getProviderObjectCount(id)

            // logo
            def organisationLogo
            if (institution.logo.isEmpty()) {
                organisationLogo = g.resource("plugin": "ddb-common", "dir": "images",
                "file": "/placeholder/searchResultMediaInstitution.png")
            }
            else {
                organisationLogo = g.resource("dir": "binary", "file": id + "/list/1.jpg")
            }

            render(
                    view: "institution",
                    model: [
                        itemId: itemId,
                        itemUri: request.forwardURI,
                        selectedItemId: id,
                        institution: institution,
                        organisationLogo: organisationLogo,
                        subinstitutions: institutionService.getChildren(itemId),
                        parentOrg: jsonOrgParentHierarchy,
                        countObjcs: countObjectsForProv,
                        url: pageUrl,
                        domainCanonic:configurationService.getDomainCanonic(),
                        isFavorite: favoritesService.isFavorite(id),
                        folder: bookmarksService.findFolderByInstitutionId(itemId)]
                    )
        } else {
            forward controller: 'error', action: "defaultNotFound"
        }

    }

    /**
     * Controller method for rendering AJAX calls for an entity based item search
     *
     * @return the content of the backend search
     */
    public def getInstitutionHighlights() {
        def institutionid = params["institutionid"]
        int offset = params.int(SearchParamEnum.OFFSET.getName())
        int rows = params.int(SearchParamEnum.ROWS.getName())

        if(!rows) {
            rows = 4
        }
        if(rows < 1){
            rows = 1
        }

        if(!offset) {
            offset = 0
        }
        if(offset < 0){
            offset = 0
        }

        def institution = [:]

        def highLights = institutionService.getInstitutionHighlights(institutionid, offset, rows)
        institution["searchPreview"] = highLights

        //Replace all the newlines. The resulting html is better parsable by JQuery
        def resultsHTML = g.render(template:"/institution/searchResults", model:["institution": institution]).replaceAll("\r\n", '').replaceAll("\n", '')

        def result = ["html": resultsHTML, "resultCount" : highLights?.resultCount]

        render (contentType:"text/json"){result}
    }
}
