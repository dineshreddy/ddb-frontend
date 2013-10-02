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
        } else {
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
        } else {
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
            if(request.JSON == null || request.JSON.ids == null || request.JSON.ids.size() == 0) {
                result = response.SC_OK
            }else if (bookmarksService.deleteFavorites(user.getId(), request.JSON)) {
                result = response.SC_OK
            }
        } else {
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
        } else {
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
        } else {
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
        } else {
            log.info "getFavorites returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def createFavoritesFolder() {
        log.info "createFavoritesFolder " + request.JSON

        def title = request.JSON.title
        def description = request.JSON.description

        def result = response.SC_BAD_REQUEST
        def User user = getUserFromSession()
        if (user != null) {
            if (bookmarksService.newFolder(user.getId(), title, false, description)) {
                result = response.SC_CREATED
                flash.message = "ddbnext.favorites_folder_create_succ"
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "createFavoritesFolder returns " + result
        render(status: result)
    }

    def deleteFavoritesFolder() {
        log.info "deleteFavoritesFolder " + request.JSON
        boolean deleteItems = request.JSON.deleteItems
        def folderId = request.JSON.folderId
        def result = response.SC_BAD_REQUEST

        def User user = getUserFromSession()
        if (user != null) {
            def foldersOfUser = bookmarksService.findAllFolders(user.getId())

            // 1) Check if the current user is really the owner of this folder, else deny
            // 2) Check if the folder is a default favorites folder
            boolean isFolderOfUser = false
            boolean isDefaultFavoritesFolder = false
            foldersOfUser.each {
                if(it.folderId == folderId){
                    isFolderOfUser = true
                    if(it.title == "favorites"){
                        isDefaultFavoritesFolder = true
                    }
                }
            }
            if(isFolderOfUser){
                if(isDefaultFavoritesFolder){
                    result = response.SC_FORBIDDEN

                }else{
                    if(deleteItems){
                    }

                    bookmarksService.deleteFolder(folderId)
                    result = response.SC_OK
                    flash.message = "ddbnext.favorites_folder_delete_succ"
                }
            } else {
                result = response.SC_UNAUTHORIZED
                flash.error = "ddbnext.favorites_folder_delete_unauth"
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteFavoritesFolder returns " + result
        render(status: result)
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