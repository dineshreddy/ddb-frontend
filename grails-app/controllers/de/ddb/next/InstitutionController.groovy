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
import grails.converters.JSON

import org.apache.commons.logging.LogFactory

import groovy.json.JsonSlurper;
import groovyx.net.http.ContentType;
import groovyx.net.http.Method

class InstitutionController {

    private static final log = LogFactory.getLog(this)

    def institutionService
    def configurationService

    def show() {
        def allInstitution = institutionService.findAll()
        def institutionByFirstLetter = allInstitution.data

        // TODO: make this more idiomatic Groovy
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

        // TODO: move to service
        def index = []
        institutionByFirstLetter.each { index.add(it) }

        render (view: 'institutionList',  model: [index: index, all: all, total: allInstitution?.total])
    }

    def getJson() {
        render institutionService.findAll() as JSON
    }


    def showInstitutionsTreeByItemId() {
        def id = params.id;
        def itemId = id;
        def vApiInstitution = new ApiInstitution();
        log.debug("read insitution by item id: ${id}");
        def selectedOrgXML = vApiInstitution.getInstitutionViewByItemId(id, configurationService.getBackendUrl());
        def pageUrl = configurationService.getSelfBaseUrl() + request.forwardURI
        if (selectedOrgXML) {
            selectedOrgXML = selectedOrgXML["cortex-institution"] // fix for the changed xml-format in the new backend api
            def jsonOrgParentHierarchy = vApiInstitution.getParentsOfInstitutionByItemId(id, configurationService.getBackendUrl())
            log.debug("jsonOrgParentHierarchy: ${jsonOrgParentHierarchy}");
            if (jsonOrgParentHierarchy.size() == 1) {
                if (jsonOrgParentHierarchy[0].id != id) {
                    log.error("ERROR: id:${id} != OrgParent.id:${jsonOrgParentHierarchy[0].id}");
                    forward controller: 'error', action: "ERROR: id:${id} != OrgParent.id:${jsonOrgParentHierarchy[0].id}"
                }
            }
            else if (jsonOrgParentHierarchy.size() > 1) {
                itemId = jsonOrgParentHierarchy[jsonOrgParentHierarchy.size() - 1].id;
            }
            log.debug("root itemId = ${itemId}");
            def jsonOrgSubHierarchy = vApiInstitution.getChildrenOfInstitutionByItemId(itemId, configurationService.getBackendUrl())
            log.debug("jsonOrgSubHierarchy: ${jsonOrgSubHierarchy}")
            def jsonFacets = vApiInstitution.getFacetValues(selectedOrgXML.name.text(), configurationService.getBackendUrl())
            int countObjectsForProv = 0;
            if ((jsonFacets != null)&&(jsonFacets.facetValues != null)&&(jsonFacets.facetValues.count != null)&&(jsonFacets.facetValues.count[0] != null)) {
                try {
                    countObjectsForProv = jsonFacets.facetValues.count[0].intValue()
                }
                catch (NumberFormatException ex) {
                    countObjectsForProv = -1;
                }
            }

            def organisationLogo
            if(selectedOrgXML.logo == null || selectedOrgXML.logo.toString().trim().isEmpty()){
                organisationLogo = g.resource("dir": "images", "file": "/placeholder/search_result_media_institution.png").toString()
            }else{
                organisationLogo = selectedOrgXML.logo
            }
            render(view: "institution", model: [itemId: itemId, selectedItemId: id, selectedOrgXML: selectedOrgXML, organisationLogo: organisationLogo, subOrg: jsonOrgSubHierarchy, parentOrg: jsonOrgParentHierarchy, countObjcs: countObjectsForProv, vApiInst: vApiInstitution, url: pageUrl])
        }
        else {
            forward controller: 'error', action: "notFound"
        }

    }
}
