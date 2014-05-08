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

import java.util.List;

import grails.converters.JSON

import org.ccil.cowan.tagsoup.Parser

import de.ddb.common.beans.User
import de.ddb.common.constants.FolderConstants
import de.ddb.common.constants.Type
import de.ddb.next.beans.Bookmark
import de.ddb.next.beans.Folder
import de.ddb.next.exception.FavoritelistNotFoundException


/**
 * This controller is mainly used for AJAX calls.
 * All other favorites requests should be handled by FavoritesviewController
 *
 * @author boz
 */
class FavoritesController {
    def bookmarksService
    def favoritesService

    def deleteFavorite() {
        log.info "deleteFavorite " + params.id
        def itemId = params.id
        def result = response.SC_NOT_FOUND
        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            if (bookmarksService.deleteBookmarksByItemIds(user.getId(), [itemId])) {
                result = response.SC_NO_CONTENT
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteFavorite returns " + result
        render(status: result)
    }

    def deleteFavoritesFromFolder() {
        log.info "deleteFavoritesFromFolder " + request.JSON

        def itemIds = null
        def folderId = null
        if(request.JSON) {
            itemIds = request.JSON.ids
            folderId = request.JSON.folderId
        }

        def result = response.SC_NOT_FOUND
        def User user = favoritesService.getUserFromSession()
        if (user != null) {

            // Check if the items all belong to the current user
            boolean itemsAreOwnedByUser = true
            List<Bookmark> allBookmarksInFolder = bookmarksService.findBookmarksByFolderId(user.getId(), folderId)
            def allItemIdsInFolder = allBookmarksInFolder.collect { it.itemId }

            itemIds.each {
                if(!(it in allItemIdsInFolder)){
                    itemsAreOwnedByUser = false
                }
            }

            if(itemsAreOwnedByUser){
                if(itemIds == null || itemIds.size() == 0) {
                    result = response.SC_OK
                }else{
                    //The complete list of folders from where bookmarks has been removed
                    Set<Folder> affectedFolders = []

                    def bookmarksToDelete = []

                    // Special case: if bookmarks are deleted in the main favorites folder -> delete them everywhere
                    //def mainFavoriteFolder = favoritesPageService.getMainFavoritesFolder()
                    def mainFavoriteFolder = bookmarksService.findMainBookmarksFolder(user.getId())

                    //MainFolder
                    if(folderId == mainFavoriteFolder.folderId) {
                        bookmarksToDelete = bookmarksService.findBookmarkedItemsInFolder(user.getId(), itemIds, null)
                        bookmarksToDelete.each { b ->
                            b.folders.each { f ->
                                Folder folder = bookmarksService.findFolderById(f)
                                affectedFolders.add(folder)
                            }
                        }

                        bookmarksService.deleteBookmarksByItemIds(user.getId(), itemIds)
                    }
                    //other folders
                    else{
                        bookmarksToDelete = bookmarksService.findBookmarkedItemsInFolder(user.getId(), itemIds, folderId)

                        def favoriteIds = bookmarksToDelete.collect { it.bookmarkId }
                        bookmarksService.removeBookmarksFromFolder(favoriteIds, folderId)

                        affectedFolders.add(bookmarksService.findFolderById(folderId))
                        result = response.SC_OK
                    }

                    //If the affectedFolders are public and has no items left -> set folder to private DDBNEXT-1517
                    affectedFolders.each { folder ->
                        if (folder.isPublic) {
                            def bookmarkCount = bookmarksService.countBookmarksInFolder(user.getId(), folder.folderId)
                            if (bookmarkCount == 0) {
                                folder.isPublic = false
                                bookmarksService.updateFolder(folder);
                                flash.message = "ddbnext.folder_empty_set_to_private"
                            }
                        }
                    }
                }
            }else{
                result = response.SC_UNAUTHORIZED
            }

        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteFavoritesFromFolder returns " + result
        render(status: result)
    }



    def filterFavorites() {
        log.info "filterFavorites " + request.JSON
        def itemIdList = request.JSON
        User user = favoritesService.getUserFromSession()
        if (user != null) {
            Folder mainFavoritesFolder = bookmarksService.findMainBookmarksFolder(user.getId())
            def bookmarks = bookmarksService.findBookmarkedItemsInFolder(user.getId(), itemIdList, mainFavoritesFolder.folderId)
            List result = bookmarks.collect {it.itemId}
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
        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            def bookmark = bookmarksService.findBookmarkedItemsInFolder(user.getId(), [params.id], null)
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
        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            def result = bookmarksService.findBookmarksByUserId(user.getId())
            log.info "getFavorites returns " + result
            render(result as JSON)
        } else {
            log.info "getFavorites returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def getFavoriteFolder() {
        log.info "getFavoriteFolder " + params.id
        def result = response.SC_NOT_FOUND
        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            Folder folder = bookmarksService.findFolderById(params.id)
            log.info "getFavoriteFolder returns " + folder
            folder.setBlockingToken("") // Don't expose the blockingToken to Javascript!
            render(folder as JSON)
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "getFavoriteFolder returns " + result
        render(status: result)
    }

    /**
     * Get a sorted list of all bookmark folders. The main folder is marked with "isMainFolder".
     *
     * @return sorted list of all bookmark folders
     */
    def getFavoriteFolders() {
        log.info "getFavoriteFolders"
        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            def mainFolder = bookmarksService.findMainBookmarksFolder(user.getId())
            def folders = bookmarksService.findAllFolders(user.getId())
            folders.find {it.folderId == mainFolder.folderId}.isMainFolder = true
            folders = favoritesService.sortFolders(folders)
            folders.each {it.blockingToken = ""} // Don't expose the blockingToken to Javascript
            log.info "getFavoriteFolders returns " + folders
            render(folders as JSON)
        } else {
            log.info "getFavoriteFolders returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def createFavoritesFolder() {
        log.info "createFavoritesFolder " + request.JSON

        def title = request.JSON.title
        def description = request.JSON.description

        title = sanitizeTextInput(title)
        description = sanitizeTextInput(description)

        def result = response.SC_BAD_REQUEST
        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            def publishingName = user.getUsername()
            def now = System.currentTimeMillis()
            Folder newFolder = new Folder(
                    null,
                    user.getId(),
                    title,
                    description,
                    false,
                    publishingName,
                    false,
                    "",
                    now,
                    now)
            String newFolderId = bookmarksService.createFolder(newFolder)
            if(newFolderId){
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

        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            def foldersOfUser = bookmarksService.findAllFolders(user.getId())

            // 1) Check if the current user is really the owner of this folder, else deny
            // 2) Check if the folder is a default favorites folder -> if true, deny
            boolean isFolderOfUser = false
            boolean isDefaultFavoritesFolder = false
            foldersOfUser.each {
                if(it.folderId == folderId){
                    isFolderOfUser = true
                    if(it.title == FolderConstants.MAIN_BOOKMARKS_FOLDER.value){
                        isDefaultFavoritesFolder = true
                    }
                }
            }
            if(isFolderOfUser){
                if(isDefaultFavoritesFolder){
                    result = response.SC_FORBIDDEN

                }else{
                    def favorites = bookmarksService.findBookmarksByFolderId(user.getId(), folderId)

                    // delete items in ALL folders
                    if(deleteItems){
                        // Find itemIDs of the selected folder
                        def itemIds = []
                        favorites.each {
                            itemIds.add(it.itemId)
                        }
                        // Delete itemIds in ALL folders
                        bookmarksService.deleteBookmarksByItemIds(user.getId(), itemIds)
                    }else{
                        // delete items only in the current folder
                        def bookmarkIds = []
                        favorites.each {
                            bookmarkIds.add(it.bookmarkId)
                        }
                        //bookmarksService.deleteBookmarksByBookmarkIds(user.getId(), bookmarkIds)
                        bookmarksService.deleteDocumentsByTypeAndIds(user.getId(), bookmarkIds, "bookmark")
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

    def copyFavorites() {
        log.info "copyFavorites " + request.JSON
        def favoriteIds = request.JSON.ids
        def folderIds = request.JSON.folders

        def result = response.SC_BAD_REQUEST

        def User user = favoritesService.getUserFromSession()
        if (user != null) {

            // Check if the folders to copy to are actually folders owned by this user (security)
            def foldersOfUser = bookmarksService.findAllFolders(user.getId())
            boolean foldersOwnedByUser = true
            def allFolderIds = foldersOfUser.collect { it.folderId }
            folderIds.each {
                if(!(it in allFolderIds)){
                    foldersOwnedByUser = false
                }
            }

            if(foldersOwnedByUser){
                folderIds.each { folderId ->
                    favoriteIds.each { favoriteId ->
                        Bookmark favoriteToCopy = bookmarksService.findBookmarkById(favoriteId)
                        String itemId = favoriteToCopy.itemId
                        // Check if the item already exists in the list
                        List favoritesInTargetFolder = bookmarksService.findBookmarkedItemsInFolder(user.getId(), [itemId], folderId)
                        // if not -> add it
                        if(favoritesInTargetFolder.size() == 0){
                            Bookmark newBookmark = new Bookmark(
                                    null,
                                    user.getId(),
                                    itemId,
                                    favoriteToCopy.creationDate.getTime(),
                                    favoriteToCopy.type,
                                    [folderId],
                                    "",
                                    new Date().getTime())
                            bookmarksService.createBookmark(newBookmark)
                        }
                    }
                }
            }else{
                result = response.SC_UNAUTHORIZED
            }
            result = response.SC_OK
            flash.message = "ddbnext.favorites_copy_succ"
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "copyFavorites returns " + result
        render(status: result)
    }

    def addFavorite() {
        log.info "addFavorite " + params.id
        long timestampStart = System.currentTimeMillis() // This is because of the slow request: See DDBNEXT-932
        def itemId = params.id
        def result = response.SC_BAD_REQUEST
        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            Bookmark newBookmark = new Bookmark(
                    null,
                    user.getId(),
                    itemId,
                    new Date().getTime(),
                    (params.reqObjectType?.equalsIgnoreCase("entity") ? Type.ENTITY : Type.CULTURAL_ITEM),
                    null,
                    "",
                    new Date().getTime())
            String newBookmarkId = bookmarksService.createBookmark(newBookmark)
            if (newBookmarkId) {
                result = response.SC_CREATED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "addFavorite returns " + result
        long timestampStop = System.currentTimeMillis()
        log.info "addFavorite duration: "+(timestampStop-timestampStart)/1000 // This is because of the slow request: See DDBNEXT-932
        render(status: result)
    }



    def addFavoriteToFolder() {
        log.info "addFavoriteToFolder " + params.folderId + "," + params.itemId
        def result = response.SC_BAD_REQUEST
        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            Bookmark newBookmark = new Bookmark(
                    null,
                    user.getId(),
                    params.itemId,
                    new Date().getTime(),
                    Type.CULTURAL_ITEM,
                    [params.folderId],
                    "",
                    new Date().getTime())
            String newBookmarkId = bookmarksService.createBookmark(newBookmark)
            if(newBookmarkId != null){
                result = response.SC_CREATED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "addFavoriteToFolder returns " + result
        render(status: result)
    }


    def editFavoritesFolder() {
        log.info "editFavoritesFolder " + request.JSON

        def id = request.JSON.id
        def title = request.JSON.title
        def description = request.JSON.description
        def publishingType = request.JSON.name
        def isPublic = request.JSON.isPublic

        title = sanitizeTextInput(title)
        description = sanitizeTextInput(description)

        def result = response.SC_BAD_REQUEST

        def User user = favoritesService.getUserFromSession()
        if (user != null) {

            def publishingName = ""
            if(publishingType == FolderConstants.PUBLISHING_NAME_FULLNAME.getValue()) {
                publishingName = user.getFirstnameAndLastnameOrNickname()
            }else{
                publishingName = user.getUsername()
            }


            List foldersOfUser = bookmarksService.findAllFolders(user.getId())
            Folder folder = null

            // 1) Check if the current user is really the owner of this folder, else deny
            // 2) Check if the folder is a default favorites folder -> if true, deny
            // 3) If the user will publish an empty list -> deny
            boolean isFolderOfUser = false
            boolean isDefaultFavoritesFolder = false
            foldersOfUser.each {
                if(it.folderId == id){
                    folder = it
                    // check if the favorites list is blocked
                    if(it.isBlocked){
                        isPublic = false
                    }

                    isFolderOfUser = true
                    if(it.title == FolderConstants.MAIN_BOOKMARKS_FOLDER.value){
                        isDefaultFavoritesFolder = true
                    }
                }
            }

            // Check if folder has at least one bookmark, empty folder cannot set public, see DDBNEXT-1517
            def bookmarkCount = bookmarksService.countBookmarksInFolder(user.getId(), folder.folderId)

            if (!isPublic || bookmarkCount > 0) {
                if(isFolderOfUser && !isDefaultFavoritesFolder){
                    folder.title = title
                    folder.description = description
                    folder.isPublic = isPublic
                    folder.publishingName = publishingName
                    bookmarksService.updateFolder(folder)
                    result = response.SC_OK
                    flash.message = "ddbnext.folder_edit_succ"
                } else {
                    result = response.SC_UNAUTHORIZED
                }
            } else {
                //To work with flash.error, the response code must be 200
                result = response.SC_OK
                flash.error = "ddbnext.folder_empty_cannot_publish"
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }

        log.info "editFavoritesFolder returns " + result
        render(status: result)
    }

    def setComment() {
        log.info "setComment " + request.JSON

        def id = request.JSON.id
        def text = request.JSON.text

        Parser tagsoupParser = new Parser()
        XmlSlurper slurper = new XmlSlurper(tagsoupParser)
        String cleanedText = slurper.parseText(text).text()
        cleanedText = sanitizeTextInput(cleanedText)

        def result = response.SC_BAD_REQUEST

        def User user = favoritesService.getUserFromSession()
        if (user != null) {

            // 1) Check if the current user is really the owner of this bookmark, else deny
            Bookmark bookmark = bookmarksService.findBookmarkById(id)
            boolean isBookmarkOfUser = false
            if(bookmark.userId == user.getId()){
                isBookmarkOfUser = true
            }
            if(isBookmarkOfUser){
                bookmarksService.updateBookmarkDescription(id, cleanedText)
                result = response.SC_OK
            } else {
                result = response.SC_UNAUTHORIZED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }


        log.info "setComment returns " + result
        render(status: result)

    }

    def togglePublish() {
        log.info "togglePublish " + request.JSON

        def id = request.JSON.id

        def result = response.SC_BAD_REQUEST
        def errorMessage

        def User user = favoritesService.getUserFromSession()
        if (user != null) {

            // 1) Check if the current user is really the owner of this folder, else deny
            Folder folder = bookmarksService.findFolderById(id)
            boolean isFolderOfUser = false
            if(folder.userId == user.getId()){
                isFolderOfUser = true
            }

            // 2) Check if folder has at least one bookmark, empty folder cannot be published see DDBNEXT-1517
            def bookmarkCount = bookmarksService.countBookmarksInFolder(user.getId(), folder.folderId)

            if (folder.isPublic || bookmarkCount > 0) {
                if(isFolderOfUser && !folder.isBlocked){
                    folder.isPublic = !folder.isPublic
                    bookmarksService.updateFolder(folder)
                    result = response.SC_OK
                } else {
                    result = response.SC_UNAUTHORIZED
                }
            } else {
                //To work with flash.error, the response code must be 200
                result = response.SC_OK
                flash.error =  "ddbnext.folder_empty_cannot_publish"
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }

        log.info "togglePublish returns " + result
        render(status: result)

    }

    private String sanitizeTextInput(String input){
        String output = ""
        if(input != null) {
            Parser tagsoupParser = new Parser()
            XmlSlurper slurper = new XmlSlurper(tagsoupParser)
            output = input
            output = slurper.parseText(output).text()
            output = output.replaceAll("\\\"", "''")
            output = output.replaceAll("´", "'")
            output = output.replaceAll("`", "'")
        }
        return output
    }

    private Folder getSelectedFolder(String folderId, User user) {
        def selectedFolder = bookmarksService.findPublicFolderById(folderId)

        // If the folder does not exist (maybe deleted) or the user does not exist -> 404
        if(selectedFolder == null || user == null){
            throw new FavoritelistNotFoundException("publicFavorites(): favorites list or user do not exist")
        }
        return selectedFolder
    }
}
