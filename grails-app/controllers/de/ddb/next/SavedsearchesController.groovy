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
import javax.servlet.http.HttpSession
import de.ddb.next.beans.User

class SavedsearchesController {
    def savedSearchesService

    def addSavedSearch() {
        log.info "addSavedSearch " + request?.JSON?.query + ", " + request?.JSON?.title
        def result = response.SC_BAD_REQUEST
        def User user = getUserFromSession()
        if (user != null) {
            if (savedSearchesService.addSavedSearch(user.getId(), request?.JSON?.title, request?.JSON?.query)) {
                result = response.SC_CREATED
            }
        }
        else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "addSavedSearch returns " + result
        render(status: result)
    }

    def deleteSavedSearches() {
        log.info "deleteSavedSearches " + request.JSON
        def result = response.SC_NOT_FOUND
        def User user = getUserFromSession()
        if (user != null) {
            if(request.JSON == null || request.JSON.ids == null || request.JSON.ids.size() == 0) {
                result = response.SC_OK
            }
            else if (savedSearchesService.deleteSavedSearches(user.getId(), request.JSON.ids)) {
                result = response.SC_OK
            }
        }
        else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteSavedSearches returns " + result
        render(status: result)
    }

    def getSavedSearches() {
        log.info "getSavedSearches"
        def User user = getUserFromSession()
        if (user != null) {
            def result = savedSearchesService.getSavedSearches(user.getId())
            log.info "getSavedSearches returns " + result
            render(result as JSON)
        }
        else {
            log.info "getSavedSearches returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def isSavedSearch() {
        log.info "isSavedSearch"
        def User user = getUserFromSession()
        if (user != null) {
            def result = savedSearchesService.isSavedSearch(user.getId(), request.JSON.query)
            log.info "isSavedSearch returns " + result
            if (result) {
                render(status: response.SC_OK)
            }
            else {
                render(status: response.SC_NOT_FOUND)
            }
        }
        else {
            log.info "isSavedSearch returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    private def getUserFromSession() {
        def result
        def HttpSession session = request.getSession(false)
        if (session != null) {
            result = session.getAttribute(User.SESSION_USER)
        }
        return result
    }
}