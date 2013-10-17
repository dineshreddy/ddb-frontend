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
import groovy.json.*
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import javax.servlet.http.HttpServletResponse

import net.sf.json.JSONNull

import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.util.WebUtils

import de.ddb.next.beans.Bookmark
import de.ddb.next.beans.Folder


/**
 * Set of Methods that encapsulate REST-calls to the BookmarksService
 *
 * @author crh
 *
 */
class BookmarksService {

    public static final def MAIN_BOOKMARKS_FOLDER = 'favorites'
    public static final def IS_PUBLIC = false
    public static final def DEFAULT_SIZE = 9999

    def configurationService
    def transactional = false

    /**
     * Create a new bookmark folder.
     *
     * @param userId    the ID whose the folder belongs to.
     * @param title     the title of the folder.
     * @param isPublic  boolean flag to mark if a folder should be public visible.
     * @return          the newly created folder ID.
     */
    String newFolder(userId, title, isPublic, description = null) {
        log.info "newFolder(): creating a new folder with the title: ${title}"

        String newFolderId = null

        def postBody = [
            user: userId,
            title : title,
            description: description,
            isPublic : isPublic
        ]
        def postBodyAsJson = postBody as JSON

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/folder", false, postBodyAsJson)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            newFolderId = response._id
            refresh()
        }

        return newFolderId
    }



    List findAllPublicFolders(userId) {
        log.info "findAllPublicFolders()"

        def folders = findAllFolders(userId)
        def publicFolders = []
        folders?.each {
            if(it.isPublic){
                publicFolders.add(it)
            }
        }
        return publicFolders
    }

    List findAllFolders(userId) {
        log.info "findAllFolders()"

        def folderList = []

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBookmarkUrl(), "/ddb/folder/_search", false, ["q":userId])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            resultList.each { it ->
                def description = "null"
                if(!(it._source.description instanceof JSONNull) && (it._source.description != null)){
                    description = it._source.description
                }

                def folder = new Folder(
                        it._id,
                        it._source.user,
                        it._source.title,
                        description,
                        it._source.isPublic
                        )
                folderList.add(folder)
            }
        }
        return folderList
    }

    /* TODO: refactor this one
     * URL encode the space character programmatically, _not_ hard code.
     */
    /**
     * List all bookmarks in a folder that belongs to the user.
     *
     * A Bookmark {@link Bookmark} contains following properties:
     * - String bookmarkId,
     * - String userId,
     * - String itemId,
     * - Date creationDate
     *
     * @param userId    the ID whose the folders and bookmarks belongs to.
     * @param folderId  the ID of a certain folder. Use {@link #findAllFolders} to find out the folder IDs.
     * @param size      how many bookmarks the service should return, it is _optional_ by default size=9999
     * @return          a list of bookmarks.
     */
    List findBookmarksByFolderId(userId, folderId, size = DEFAULT_SIZE) {
        log.info "findBookmarksByFolderId(): find bookmarks for the user (${userId}) in the folder ${folderId}"

        def all = []

        def query = ["q":"\"${userId}\" AND folder:\"${folderId}\"".encodeAsURL(), "size":"${size}"]
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_search", false, query, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()

            def resultList = response.hits.hits
            resultList.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type as Type,
                        it._source.folder,
                        it._source.description,
                        it._source.updatedAt
                        )
                all.add(bookmark)
            }
        }
        return all
    }

    List findBookmarksByPublicFolderId(folderId, size = DEFAULT_SIZE) {
        log.info "findBookmarksByPublicFolderId(): find bookmarks in the folder ${folderId}"

        def all = []

        Folder folder = findPublicFolderById(folderId)
        if(folder == null || !folder.isPublic){
            return []
        }

        def query = ["q":"folder:\"${folderId}\"".encodeAsURL(), "size":"${size}"]
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_search", false, query, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            resultList.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type as Type,
                        it._source.folder,
                        it._source.description,
                        it._source.updatedAt
                        )
                all.add(bookmark)
            }
        }

        return all
    }


    /**
     * Bookmark a cultural item in a folder for a certain user.
     *
     * @param userId    the ID whose the folder belongs to.
     * @param folderId  the ID of a certain folder. Use {@link #findAllFolders} to find out the folder IDs.
     * @param itemID    the ID of the DDB cultural item.
     * @return          the created bookmark ID.
     */
    String saveBookmark(userId, folderId, itemId, description = null, type = Type.CULTURAL_ITEM, long createdAt = new Date().getTime()) {
        log.info "saveBookmark()"

        String newBookmarkId = null

        def postBody = [
            user: userId,
            folder: folderId,
            item: itemId,
            createdAt: createdAt,
            type: type.toString(),
            description: description,
            updatedAt: new Date().getTime()
        ]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark", false, postBody as JSON)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            newBookmarkId = response._id
            log.info "Bookmark ${newBookmarkId} is created."
            refresh()
        }

        return newBookmarkId
    }

    private void refresh() {
        log.info "refresh(): refreshing index ddb..."

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/_refresh", false, "")

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "Response: ${response}"
            log.info "finished refreshing index ddb."
        }
    }

    /**
     * Given a list of cultural item IDs, find which are bookmarked by the user.
     *
     * @param userId     the ID who bookmarked the cultural items.
     * @param itemIdList a list of cultural item IDs.
     * @return           the list of bookmarked items.
     */
    List findBookmarkedItems(userId, itemIdList) {
        log.info "findBookmarkedItems(): itemIdList ${itemIdList}"

        def bookmarks = []

        def postBody = [filter: [terms: [item: itemIdList]]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_search", false, postBody as JSON, ["q":"user:\"${userId}\""])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "response as application/json: ${response}"

            response.hits.hits.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type as Type,
                        it._source.folder,
                        it._source.description,
                        it._source.updatedAt
                        )
                bookmarks.add(bookmark)
            }
        }

        return bookmarks
    }

    /**
     * Delete all bookmarks of cultural items in the {bookmarkIdList} belong to the user.
     *
     * @param userId         the ID who bookmarked the cultural items.
     * @param bookmarkIdList a list of bookmark IDs. NOTE: These are _not_ a list of cultural item IDs.
     */
    boolean deleteBookmarksByBookmarkIds(userId, bookmarkIdList) {
        log.info "deleteBookmarksByBookmarkIds()"

        def postBody = ''
        bookmarkIdList.each { id ->
            postBody = postBody + '{ "delete" : { "_index" : "ddb", "_type" : "bookmark", "_id" : "' + id + '" } }\n'
        }
        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_bulk", false, postBody)

        if(apiResponse.isOk()){
            refresh()
            return true
        }else{
            return false
        }
    }

    /**
     *
     */
    String addBookmark(userId, itemId, description = null, type = Type.CULTURAL_ITEM, folderIdList = []) {
        log.info "addBookmark()"
        def foundItemList =  findBookmarkedItemsInFolder(userId, [itemId])
        if(foundItemList.size()>0) {
            log.warn('The item ID (itemId) is already in the Bookmarks')
            return null
        }
        log.info "type: ${type}"
        if(folderIdList.size() == 0){
            def mainBookmarksFolder = findFoldersByTitle(userId, BookmarksService.MAIN_BOOKMARKS_FOLDER)[0]
            folderIdList.add(mainBookmarksFolder.folderId)
        }
        return saveBookmark(userId, folderIdList, itemId, description, type)
    }

    List findFoldersByTitle(userId, title) {
        log.info "findFoldersByTitle(): finding a folder with the title ${title} for the user: ${userId}"

        def all = []

        def postBody = [filter: [term: [title: title]]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/folder/_search", false, postBody as JSON, ["q":"user:\"${userId}\""])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            resultList.each { it ->
                def description = "null"
                if(!(it._source.description instanceof JSONNull) && (it._source.description != null)){
                    description = it._source.description
                }
                def folder = new Folder(
                        it._id,
                        it._source.user,
                        it._source.title,
                        description,
                        it._source.isPublic
                        )

                all.add(folder)
            }

            log.info "found #folder: ${all.size()} with the title ${title}"
        }

        return all
    }

    Folder findMainBookmarksFolder(userId) {
        log.info "findMainBookmarksFolder()"
        Folder folder = null
        List allFolders = findAllFolders(userId)
        allFolders.each {
            if(it.title == BookmarksService.MAIN_BOOKMARKS_FOLDER){
                folder = it
            }
        }
        return folder
    }

    List findBookmarksByUserId(userId, size = DEFAULT_SIZE) {
        log.info "findBookmarksByUserId()"

        def all = []

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_search", false, "", ["q":"user:\"${userId}\"", "size":"${DEFAULT_SIZE}"])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "response as application/json: ${response}"
            def resultList = response.hits.hits

            resultList.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type as Type,
                        it._source.folder,
                        it._source.description,
                        it._source.updatedAt
                        )
                all.add(bookmark)
            }
        }

        return all
    }

    boolean deleteBookmarksByItemIds(userId, itemIds) {
        log.info "deleteBookmarksByItemIds()"
        def bookmarkIds = []
        def allBookmarks = findBookmarksByUserId(userId, DEFAULT_SIZE)
        allBookmarks.each { it ->
            if(it.itemId  in itemIds) {
                bookmarkIds.add(it.bookmarkId)
            }
        }
        log.info "deleteBookmarksByItemIds for the items ${itemIds}"
        log.info "deleteBookmarksByItemIds the bookmarkIds ${bookmarkIds}"
        return deleteBookmarksByBookmarkIds(userId, bookmarkIds)
    }

    boolean deleteAllUserBookmarks(userId) {
        log.info "deleteAllUserBookmarks()"
        def bookmarkIds = []
        def allBookmarksOfUser = findBookmarksByUserId(userId, DEFAULT_SIZE)
        allBookmarksOfUser.each { it ->
            bookmarkIds.add(it.bookmarkId)
        }
        return deleteBookmarksByBookmarkIds(userId, bookmarkIds)
    }

    List findBookmarkedItemsInFolder(userId, itemIdList, folderId = null) {
        log.info "findBookmarkedItemsInFolder(): itemIdList ${itemIdList}"

        def bookmarks = []

        def queryParameter = [:]
        if(folderId) {
            queryParameter = ["q":"user:\"${userId}\" AND folder:\"${folderId}\"".encodeAsURL(),"size":"${DEFAULT_SIZE}"]
        } else {
            queryParameter = ["q":"user:\"${userId}\"".encodeAsURL(),"size":"${DEFAULT_SIZE}"]
        }

        def postBody = [filter: [terms: [item: itemIdList]]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_search", false, postBody as JSON, queryParameter, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "response as application/json: ${response}"
            def resultList = response.hits.hits
            resultList.each { it ->
                //items.add(it._id)
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type as Type,
                        it._source.folder,
                        it._source.description,
                        it._source.updatedAt
                        )
                bookmarks.add(bookmark)
            }
        }

        return bookmarks
    }

    List findBookmarksByItemId(userId, itemId, folderId = null) {
        log.info "findBookmarksByItemId(): itemId: ${itemId}"

        def all = []

        def queryParameter = [:]
        if(folderId) {
            queryParameter = ["q":"user:\"${userId}\" AND folder:\"${folderId}\"".encodeAsURL(),"size":"${DEFAULT_SIZE}"]
        } else {
            queryParameter = ["q":"user:\"${userId}\"".encodeAsURL(),"size":"${DEFAULT_SIZE}"]
        }

        def postBody = [filter: [terms: [item: [itemId]]]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_search", false, postBody as JSON, queryParameter, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "response as application/json: ${response}"
            def resultList = response.hits.hits

            resultList.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type as Type,
                        it._source.folder,
                        it._source.description,
                        it._source.updatedAt
                        )
                all.add(bookmark)
            }
        }

        return all
    }

    boolean isBookmarkOfUser(itemId, userId) {
        log.info "isBookmarkOfUser()"
        boolean result = false
        def bookmarks = findBookmarkedItemsInFolder(userId, [itemId])
        if (bookmarks && (bookmarks.size() > 0)) {
            result = true
        }
        log.info "isBookmarkOfUser ${itemId} returns: " + result
        return result
    }

    //    /*
    //     * Given a list of bookmark ID, update its folder values to [folderId]
    //     *
    //     * bookmarkIds, list of bookmarks to update
    //     * folderIds, list of folderId as input
    //     */
    //    void copyBookmarksToFolders(List<String> bookmarkIds, List<String> folderIds) {
    //        log.info "copyBookmarksToFolders()"
    //
    //        def postBody = ''
    //        bookmarkIds.each { it ->
    //            postBody = postBody + '{ "update" : {"_id" : "'+ it + '", "_type" : "bookmark", "_index" : "ddb"} }\n'+
    //                    '{ "script" : "ctx._source.folder += otherFolder", "params" : { "otherFolder" : ' + surroundWithQuotes(folderIds)+ '} }\n'
    //        }
    //        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_bulk", false, postBody)
    //
    //        if(apiResponse.isOk()){
    //            refresh()
    //        }
    //    }

    private void surroundWithQuotes(stringInList) {
        stringInList.collect { it ->  '"' + it + '"'}
    }

    Bookmark findBookmarkById(bookmarkId) {
        log.info "findBookmarkById()"

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/${bookmarkId}", false, [:])

        if(apiResponse.isOk()){
            def it = apiResponse.getResponse()
            Bookmark bookmark = new Bookmark(
                    it._id,
                    it._source.user,
                    it._source.item,
                    it._source.createdAt,
                    it._source.type as Type,
                    it._source.folder,
                    it._source.description,
                    it._source.updatedAt
                    )
            return bookmark
        }else{
            return null
        }
    }


    Folder findFolderById(folderId) {
        log.info "findFolderById()"

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBookmarkUrl(), "/ddb/folder/${folderId}", false, [:])

        if(apiResponse.isOk()){
            def it = apiResponse.getResponse()
            Folder folder = new Folder(
                    it._id,
                    it._source.user,
                    it._source.title,
                    it._source.description,
                    it._source.isPublic
                    )
            return folder
        } else {
            return null
        }
    }

    Folder findPublicFolderById(folderId) {
        log.info "findPublicFolderById()"

        Folder folder = findFolderById(folderId)
        if(folder?.isPublic){
            return folder
        }else{
            return null
        }
    }

    void updateFolder(folderId, newTitle, newDescription = null, isPublic = false) {
        log.info "updateFolder()"

        def postBody = ""
        if(newDescription) {
            //postBody = '''{"doc" : {"title": "''' + newTitle + '''", "description": "''' + newDescription + '''"}}'''
            postBody = [doc: [title: newTitle, description: newDescription, isPublic: isPublic]]
        } else {
            //postBody = '''{"doc" : {"title": "''' + newTitle + '''"}}'''
            postBody = [doc: [title: newTitle, isPublic: isPublic]]
        }

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/folder/${folderId}/_update", false, postBody as JSON)

        if(apiResponse.isOk()){
            refresh()
        }
    }

    void updateBookmark(bookmarkId, newDescription) {
        log.info "updateBookmark()"

        def postBody = [doc: [description: newDescription, updatedAt: System.currentTimeMillis()]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/${bookmarkId}/_update", false, postBody as JSON)

        if(apiResponse.isOk()){
            refresh()
        }
    }

    void removeBookmarksFromFolder(bookmarkIds, folderId) {
        log.info "removeBookmarksFromFolder(): bookmarkIds="+bookmarkIds

        def postBody = ''
        bookmarkIds.each { it ->
            postBody = postBody +
                    '{ "delete" : {"_id" : "'+ it + '", "_type" : "bookmark", "_index" : "ddb"}}'+
                    '{ "script" : "ctx._source.folder.remove(otherFolder);", "params" : { "otherFolder" : "' + folderId + '"}}\n'
        }
        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_bulk", false, postBody)

        if(apiResponse.isOk()){
            refresh()
        }
    }

    void deleteFolder(folderId) {
        log.info "deleteFolder()"

        ApiResponse apiResponse = ApiConsumer.deleteJson(configurationService.getBookmarkUrl(), "/ddb/folder/${folderId}", false)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "Is folder with the ID ${folderId} deleted(true/false)? ${response.ok}"
            refresh()
        }
    }
}
