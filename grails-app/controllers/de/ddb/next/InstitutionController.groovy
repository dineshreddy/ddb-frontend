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
import de.ddb.common.ApiInstitution
import de.ddb.common.beans.User
import de.ddb.common.constants.SearchParamEnum

class InstitutionController {

    def institutionService
    def configurationService
    def sessionService
    def bookmarksService

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

        render (view: 'institutionList',  model: [index: index, all: all, total: allInstitution?.total])
    }

    def getJson() {
        render institutionService.findAllByAlphabet() as JSON
    }

    def showInstitutionsTreeByItemId() {
        def id = params.id
        def itemId = id
        def vApiInstitution = new ApiInstitution()
        log.debug("read insitution by item id: ${id}")
        def selectedOrgXML = vApiInstitution.getInstitutionViewByItemId(id, configurationService.getBackendUrl())
        def pageUrl = configurationService.getSelfBaseUrl() + request.forwardURI
        if (selectedOrgXML) {
            selectedOrgXML = selectedOrgXML["cortex-institution"] // fix for the changed xml-format in the new backend api
            def jsonOrgParentHierarchy = vApiInstitution.getParentsOfInstitutionByItemId(id, configurationService.getBackendUrl())
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
            def jsonOrgSubHierarchy = vApiInstitution.getChildrenOfInstitutionByItemId(itemId, configurationService.getBackendUrl())
            log.debug("jsonOrgSubHierarchy: ${jsonOrgSubHierarchy}")
            def countObjectsForProv = vApiInstitution.getProviderObjectCount(selectedOrgXML.name.text(), configurationService.getBackendUrl())

            // logo
            def organisationLogo
            if (!selectedOrgXML?.logo.toString().isEmpty()) {
                organisationLogo = g.resource("dir": "binary", "file": id + "/list/1.jpg")
            }
            else {
                organisationLogo = g.resource("plugin": "ddb-common", "dir": "images",
                "file": "/placeholder/searchResultMediaInstitution.png")
            }

            render(
                    view: "institution",
                    model: [
                        itemId: itemId,
                        itemUri: request.forwardURI,
                        selectedItemId: id,
                        selectedOrgXML: selectedOrgXML,
                        organisationLogo: organisationLogo,
                        subOrg: jsonOrgSubHierarchy,
                        parentOrg: jsonOrgParentHierarchy,
                        countObjcs: countObjectsForProv,
                        vApiInst: vApiInstitution,
                        url: pageUrl,
                        domainCanonic:configurationService.getDomainCanonic(),
                        isFavorite: isFavorite(id)]
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

    private def isFavorite(itemId) {
        def User user = sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
        if(user != null){
            return bookmarksService.isBookmarkOfUser(itemId, user.getId())
        }else{
            return false
        }
    }
}
