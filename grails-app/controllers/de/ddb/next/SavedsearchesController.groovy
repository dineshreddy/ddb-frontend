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
import de.ddb.common.aop.IsAuthorized
import de.ddb.common.constants.Type

class SavedsearchesController {

    def savedSearchesService
    def userService

    @IsAuthorized
    def addSavedSearch() {
        log.info "addSavedSearch(): " + request?.JSON?.query + ", " + request?.JSON?.title + ", " + request?.JSON?.type
        def result = response.SC_BAD_REQUEST
        Type type = Type.valueOfName(request?.JSON?.type)
        if (!type) {
            type = Type.CULTURAL_ITEM
        }
        if (savedSearchesService.addSavedSearch(userService.getUserFromSession().getId(), request?.JSON?.query,
        request?.JSON?.title, null, type)) {
            result = response.SC_CREATED
        }
        log.info "addSavedSearch returns " + result
        render(status: result)
    }

    @IsAuthorized
    def deleteSavedSearches() {
        log.info "deleteSavedSearches(): " + request.JSON
        def result = response.SC_NOT_FOUND
        if (request.JSON == null || request.JSON.ids == null || request.JSON.ids.size() == 0) {
            result = response.SC_OK
        }
        else if (savedSearchesService.deleteSavedSearches(request.JSON.ids)) {
            result = response.SC_OK
        }
        log.info "deleteSavedSearches returns " + result
        render(status: result)
    }

    @IsAuthorized
    def getSavedSearches() {
        log.info "getSavedSearches()"
        def result = savedSearchesService.findSavedSearchesByUserId(userService.getUserFromSession().getId())
        log.info "getSavedSearches returns " + result
        render(result as JSON)
    }

    @IsAuthorized
    def isSavedSearch() {
        log.info "isSavedSearch()"
        def result = savedSearchesService.isSavedSearch(userService.getUserFromSession().getId(), request.JSON.query,
                Type.valueOfName(request.JSON.type))
        log.info "isSavedSearch returns " + result
        if (result) {
            render(status: response.SC_OK)
        }
        else {
            render(status: response.SC_NO_CONTENT)
        }
    }

    @IsAuthorized
    def updateSavedSearch() {
        log.info "updateSavedSearch(): " + params.id + ", " + request?.JSON?.title
        def result = savedSearchesService.updateSavedSearch(params.id, request?.JSON?.title)
        log.info "updateSavedSearch returns " + result
        if (result) {
            render(status: response.SC_OK)
        }
        else {
            render(status: response.SC_NOT_FOUND)
        }
    }

    @IsAuthorized
    def unwatchSavedSearch() {
        log.info "unwatchSavedSearch(): " + params.id
        def result = savedSearchesService.removeWatcher(params.id, userService.getUserFromSession().getId())
        log.info "unwatchSavedSearch returns " + result
        if (result) {
            render(status: response.SC_OK)
        }
        else {
            render(status: response.SC_NOT_FOUND)
        }
    }

    @IsAuthorized
    def watchSavedSearch() {
        log.info "watchSavedSearch(): " + params.id
        def result = savedSearchesService.addWatcher(params.id, userService.getUserFromSession().getId())
        log.info "watchSavedSearch returns " + result
        if (result) {
            render(status: response.SC_OK)
        }
        else {
            render(status: response.SC_NOT_FOUND)
        }
    }
}