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

class FavoritesController {

    def bookmarksService

    def addFavorite() {
        log.info "addFavorite " + params.id
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
        log.info "addFavorite returns " + result
        render(status: result)
    }

    def deleteFavorite() {
        log.info "deleteFavorite " + params.id
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
        log.info "deleteFavorite returns " + result
        render(status: result)
    }

    def deleteFavorites() {
        log.info "deleteFavorites " + request.JSON
        def result = response.SC_NOT_FOUND
        def User user = getUserFromSession()
        if (user != null) {
            if (bookmarksService.deleteFavorites(user.getId(), request.JSON)) {
                result = response.SC_OK
            }
        }
        else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteFavorites returns " + result
        render(status: result)
    }

    def filterFavorites() {
        log.info "filterFavorites " + request.JSON
        def User user = getUserFromSession()
        if (user != null) {
            def result = bookmarksService.findFavoritesByItemIds(user.getId(), request.JSON)
            log.info "filterFavorites returns " + result
            render(result as JSON)
        }
        else {
            log.info "filterFavorites returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def getFavorite() {
        log.info "getFavorite " + params.id
        def result = response.SC_NOT_FOUND
        def User user = getUserFromSession()
        if (user != null) {
            def bookmark = bookmarksService.findFavoriteByItemId(user.getId(), params.id)
            log.info "getFavorite returns " + bookmark
            render(bookmark as JSON)
        }
        else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "getFavorite returns " + result
        render(status: result)
    }

    def getFavorites() {
        log.info "getFavorites"
        def User user = getUserFromSession()
        if (user != null) {
            def result = bookmarksService.findFavoritesByUserId(user.getId())
            log.info "getFavorites returns " + result
            render(result as JSON)
        }
        else {
            log.info "getFavorites returns " + response.SC_UNAUTHORIZED
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