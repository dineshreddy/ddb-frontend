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

class SavedSearchesController {

    def bookmarksService

    def addSavedSearch() {
        log.info "addSavedSearch " + params.id
        def result = response.SC_BAD_REQUEST
        def User user = getUserFromSession()
        if (user != null) {
            if (bookmarksService.addFavorite(user.getId(), params.id)) {
                result = response.SC_CREATED
            }
        }
        else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "addSavedSearch returns " + result
        render(status: result)
    }

    def deleteSavedSearch() {
        log.info "deleteSavedSearch " + params.id
        def result = response.SC_NOT_FOUND
        def User user = getUserFromSession()
        if (user != null) {
            if (bookmarksService.deleteFavorites(user.getId(), [params.ids])) {
                result = response.SC_NO_CONTENT
            }
        }
        else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteSavedSearch returns " + result
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
            else if (bookmarksService.deleteFavorites(user.getId(), request.JSON)) {
                result = response.SC_OK
            }
        }
        else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteSavedSearches returns " + result
        render(status: result)
    }

    def filterSavedSearches() {
        log.info "filterSavedSearches " + request.JSON
        def User user = getUserFromSession()
        if (user != null) {
            def result = bookmarksService.findFavoritesByItemIds(user.getId(), request.JSON)
            log.info "filterSavedSearches returns " + result
            render(result as JSON)
        }
        else {
            log.info "filterSavedSearches returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def getSavedSearch() {
        log.info "getSavedSearch " + params.id
        def result = response.SC_NOT_FOUND
        def User user = getUserFromSession()
        if (user != null) {
            def bookmark = bookmarksService.findFavoriteByItemId(user.getId(), params.id)
            log.info "getSavedSearch returns " + bookmark
            render(bookmark as JSON)
        }
        else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "getSavedSearch returns " + result
        render(status: result)
    }

    def getSavedSearches() {
        log.info "getSavedSearches"
        def User user = getUserFromSession()
        if (user != null) {
            def result = bookmarksService.findFavoritesByUserId(user.getId())
            log.info "getSavedSearches returns " + result
            render(result as JSON)
        }
        else {
            log.info "getSavedSearches returns " + response.SC_UNAUTHORIZED
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