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
// TODO: use ApiConsumer if possible
class BookmarksService {

    public static final def FAVORITES = 'favorites'
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
    def newFolder(userId, title, isPublic, description = null) {
        log.info "newFolder(): creating a new folder with the title: ${title}"

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/folder")
        //        def folderId
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //            body = [
        //                user: userId,
        //                title : title,
        //                description: description,
        //                isPublic : isPublic
        //            ]
        //
        //            response.success = { resp, json ->
        //                folderId = json._id
        //                refresh()
        //            }
        //        }
        //
        //        folderId

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
            def folderId = response._id
            refresh()

            return folderId
        }

    }

    /**
     * List all folders belong to a user.
     *
     * A Folder {@link Folder} contains following properties:
     * - String folderId
     * - String userId
     * - String title
     * - boolean isPublic
     *
     * @param userId    the ID whose the folders belongs to.
     * @return          a list of folders.
     */
    def findAllFolders(userId) {
        log.info "findAllFolders()"

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/folder/_search?q=user:${userId}")
        //        http.request(Method.GET, ContentType.JSON) { req ->
        //            response.success = { resp, json ->
        //                def resultList = json.hits.hits
        //                def folderList = []
        //                resultList.each { it ->
        //                    def folder = new Folder(
        //                            it._id,
        //                            it._source.user,
        //                            it._source.title,
        //                            it._source.description,
        //                            it._source.isPublic
        //                            )
        //                    folderList.add(folder)
        //                }
        //                return folderList
        //            }
        //        }

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBookmarkUrl(), "/ddb/folder/_search", false, ["q":userId])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            def folderList = []
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
            return folderList
        }
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
    def findBookmarksByFolderId(userId, folderId, size = DEFAULT_SIZE) {
        log.info "findBookmarksByFolderId(): find bookmarks for the user (${userId}) in the folder ${folderId}"
        //        def http = new HTTPBuilder(
        //                "${configurationService.getBookmarkUrl()}/ddb/bookmark/_search?q=user:${userId}%20AND%20folder:${folderId}%20&size=${size}")
        //        http.request(Method.GET, ContentType.JSON) { req ->
        //
        //            response.success = { resp, json ->
        //                def all = []
        //                def resultList = json.hits.hits
        //                resultList.each { it ->
        //                    def bookmark = new Bookmark(
        //                            bookmarkId: it._id,
        //                            userId: it._source.user,
        //                            itemId: it._source.item,
        //                            creationDate: new Date(it._source.createdAt.toLong()),
        //                            type: it._source.type as Type,
        //                            folders: it._source.folder
        //                            )
        //                    all.add(bookmark)
        //                }
        //                all
        //            }
        //        }

        def query = ["q":"${userId} AND folder:${folderId}".encodeAsURL(), "size":"${size}"]
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_search", false, query, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def all = []
            def resultList = response.hits.hits
            resultList.each { it ->
                def bookmark = new Bookmark(
                        bookmarkId: it._id,
                        userId: it._source.user,
                        itemId: it._source.item,
                        creationDate: new Date(it._source.createdAt.toLong()),
                        type: it._source.type as Type,
                        folders: it._source.folder
                        )
                all.add(bookmark)
            }
            return all
        }

    }

    /**
     * Bookmark a cultural item in a folder for a certain user.
     *
     * @param userId    the ID whose the folder belongs to.
     * @param folderId  the ID of a certain folder. Use {@link #findAllFolders} to find out the folder IDs.
     * @param itemID    the ID of the DDB cultural item.
     * @return          the created bookmark ID.
     */
    def saveBookmark(userId, folderId, itemId, type = Type.CULTURAL_ITEM) {
        log.info "saveBookmark()"

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark")
        //
        //        def bookmarkId
        //        //TODO: folder Id, now an array
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //            def requestBody = [
        //                user: userId,
        //                folder: folderId,
        //                item: itemId,
        //                createdAt: new Date().getTime(),
        //                type: type
        //            ]
        //            body = requestBody
        //
        //
        //            response.success = { resp, json ->
        //                bookmarkId = json._id
        //                log.info "Bookmark ${bookmarkId} is created."
        //                refresh()
        //            }
        //        }
        //        bookmarkId


        def postBody = [
            user: userId,
            folder: folderId,
            item: itemId,
            createdAt: new Date().getTime(),
            type: type.toString()
        ]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark", false, postBody as JSON)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def bookmarkId = response._id
            log.info "Bookmark ${bookmarkId} is created."
            refresh()

            return bookmarkId
        }

    }

    private refresh() {
        log.info "refresh(): refreshing index ddb..."

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/_refresh")
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //            response.success = { resp, json ->
        //                log.info "Response: ${json}"
        //                log.info "finished refreshing index ddb."
        //            }
        //        }

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
    def findBookmarkedItems(userId, itemIdList) {
        log.info "findBookmarkedItems(): itemIdList ${itemIdList}"

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_search?q=user:${userId}")
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //            body = [
        //                filter: [
        //                    terms: [
        //                        item: itemIdList
        //                    ]
        //                ]
        //            ]
        //
        //            response.success = { resp, json ->
        //                log.info "response as application/json: ${json}"
        //                // TODO: use inject if possible
        //                def items = [] as Set
        //                json.hits.hits.each { it ->
        //                    items.add(it._source.item)
        //                }
        //                items
        //            }
        //        }

        def postBody = [filter: [terms: [item: itemIdList]]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_search", false, postBody as JSON, ["q":"user:${userId}"])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "response as application/json: ${response}"

            def items = [] as Set
            response.hits.hits.each { it ->
                items.add(it._source.item)
            }
            return items
        }

    }

    /**
     * Delete all bookmarks of cultural items in the {bookmarkIdList} belong to the user.
     *
     * @param userId         the ID who bookmarked the cultural items.
     * @param bookmarkIdList a list of bookmark IDs. NOTE: These are _not_ a list of cultural item IDs.
     */
    def deleteBookmarks(userId, bookmarkIdList) {
        log.info "deleteBookmarks()"


        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_bulk")
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //            def reqBody = ''
        //            bookmarkIdList.each { id ->
        //                reqBody = reqBody + '{ "delete" : { "_index" : "ddb", "_type" : "bookmark", "_id" : "' + id + '" } }\n'
        //            }
        //
        //            body = reqBody
        //            response.success = {
        //                refresh()
        //                return true
        //            }
        //
        //        }


        def postBody = ''
        bookmarkIdList.each { id ->
            postBody = postBody + '{ "delete" : { "_index" : "ddb", "_type" : "bookmark", "_id" : "' + id + '" } }\n'
        }
        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_bulk", false, postBody)

        if(apiResponse.isOk()){
            refresh()

            return true
        }

    }

    /**
     *
     */
    def addFavorite(userId, itemId, type = Type.CULTURAL_ITEM, folderIdList = []) {
        log.info "addFavorite()"
        def foundItemIdList =  findFavoritesByItemIds(userId, [itemId])
        if(foundItemIdList.size()>0) {
            log.warn('The item ID (itemId) is already in the Favorites')
            return null
        }
        log.info "type: ${type}"
        return saveBookmark(userId, folderIdList, itemId, type)
    }

    def findFoldersByTitle(userId, title) {
        log.info "findFoldersByTitle(): finding a folder with the title ${title} for the user: ${userId}"

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/folder/_search?q=user:${userId}")
        //
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //
        //            body = [
        //                filter: [
        //                    term: [
        //                        title: title
        //                    ]
        //                ]
        //            ]
        //
        //            response.success = { resp, json ->
        //                log.info json
        //                def resultList = json.hits.hits
        //                def all = []
        //                resultList.each { it ->
        //                    def folder = new Folder(
        //                            it._id,
        //                            it._source.user,
        //                            it._source.title,
        //                            it._source.description,
        //                            it._source.isPublic
        //                            )
        //
        //                    all.add(folder)
        //                }
        //
        //                log.info "found #folder: ${all.size()} with the title ${title}"
        //                return all
        //            }
        //        }

        def postBody = [filter: [term: [title: title]]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/folder/_search", false, postBody as JSON, ["q":"user:${userId}"])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            def all = []
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
            return all
        }

    }

    def findFavoritesByUserId(userId, size = DEFAULT_SIZE) {
        log.info "findFavoritesByUserId()"


        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_search?q=user:${userId}&size=${DEFAULT_SIZE}")
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //            response.success = { resp, json ->
        //                log.info "response as application/json: ${json}"
        //                def all = [] //as Set
        //                def resultList = json.hits.hits
        //
        //                resultList.each { it ->
        //                    def bookmark = new Bookmark(
        //                            it._id,
        //                            it._source.user,
        //                            it._source.item,
        //                            new Date(it._source.createdAt.toLong()),
        //                            it._source.type as Type,
        //                            it._source.folder as Collection)
        //                    all.add(bookmark)
        //                }
        //                all
        //            }
        //        }

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_search", false, "", ["q":"user:${userId}", "size":"${DEFAULT_SIZE}"])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "response as application/json: ${response}"
            def all = [] //as Set
            def resultList = response.hits.hits

            resultList.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        new Date(it._source.createdAt.toLong()),
                        it._source.type as Type,
                        it._source.folder)
                all.add(bookmark)
            }


            return all
        }

    }

    def deleteFavorites(userId, itemIds) {
        log.info "deleteFavorites()"
        def bookmarkIds = []
        def allFavorites = findFavoritesByUserId(userId, DEFAULT_SIZE)
        allFavorites.each { it ->
            if(it.itemId  in itemIds.ids) {
                bookmarkIds.add(it.bookmarkId)
            }
        }
        log.info "delete favorites for the items ${itemIds}"
        log.info "delete favorites with the bookmarkIds ${bookmarkIds}"
        return deleteBookmarks(userId, bookmarkIds)
    }

    def deleteAllUserFavorites(userId) {
        log.info "deleteAllUserFavorites()"
        def bookmarkIds = []
        def allFavorites = findFavoritesByUserId(userId, DEFAULT_SIZE)
        allFavorites.each { it ->
            bookmarkIds.add(it.bookmarkId)
        }
        return deleteBookmarks(userId, bookmarkIds)
    }

    def findFavoritesByItemIds(userId, itemIdList) {
        log.info "findFavoritesByItemIds(): itemIdList ${itemIdList}"
        return findBookmarkedItemsInFolder(userId, itemIdList )
    }

    // TODO refactor this method, duplicate with findFavoritesByItemIds
    def findBookmarkedItemsInFolder(userId, itemIdList, folderId = null) {
        log.info "findBookmarkedItemsInFolder(): itemIdList ${itemIdList}"

        //        def http
        //        if(folderId) {
        //            http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_search?q=user:${userId}%20AND%20folder:${folderId}&size=${DEFAULT_SIZE}")
        //        } else {
        //            http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_search?q=user:${userId}&size=${DEFAULT_SIZE}")
        //        }
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //            body = [
        //                filter: [
        //                    terms: [
        //                        item: itemIdList
        //                    ]
        //                ]
        //            ]
        //
        //            response.success = { resp, json ->
        //                log.info "response as application/json: ${json}"
        //                // TODO: use inject if possible
        //                def items = [] as Set
        //                json.hits.hits.each { it ->
        //                    items.add(it._source.item)
        //                }
        //                items
        //            }
        //        }


        def queryParameter = [:]
        if(folderId) {
            queryParameter = ["q":"user:${userId} AND folder:${folderId}".encodeAsURL(),"size":"${DEFAULT_SIZE}"]
        } else {
            queryParameter = ["q":"user:${userId}".encodeAsURL(),"size":"${DEFAULT_SIZE}"]
        }

        def postBody = [filter: [terms: [item: itemIdList]]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_search", false, postBody as JSON, queryParameter, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "response as application/json: ${response}"
            def items = [] as Set
            def resultList = response.hits.hits
            resultList.each { it ->
                items.add(it._source.item)
            }
            return items
        }

    }

    def findFavoriteByItemId(userId, itemId, folderId=null) {
        log.info "findFavoriteByItemId(): itemId: ${itemId}"

        //        def http
        //        if(folderId) {
        //            http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_search?q=user:${userId}%20AND%20folder:${folderId}&size=${DEFAULT_SIZE}")
        //        } else {
        //            http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_search?q=user:${userId}&size=${DEFAULT_SIZE}")
        //        }
        //
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //            body = [
        //                filter: [
        //                    terms: [
        //                        item: [itemId]]
        //                ]
        //            ]
        //
        //            response.success = { resp, json ->
        //                log.info "response as application/json: ${json}"
        //                def all = [] //as Set
        //                def resultList = json.hits.hits
        //
        //                resultList.each { it ->
        //                    def bookmark = new Bookmark(
        //                            it._id,
        //                            it._source.user,
        //                            it._source.item,
        //                            new Date(it._source.createdAt.toLong()),
        //                            it._source.type as Type,
        //                            it._source.folder)
        //                    all.add(bookmark)
        //                }
        //                assert all.size() <= 1
        //                all[0]
        //            }
        //        }

        def queryParameter = [:]
        if(folderId) {
            queryParameter = ["q":"user:${userId} AND folder:${folderId}".encodeAsURL(),"size":"${DEFAULT_SIZE}"]
        } else {
            queryParameter = ["q":"user:${userId}".encodeAsURL(),"size":"${DEFAULT_SIZE}"]
        }

        def postBody = [filter: [terms: [item: [itemId]]]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_search", false, postBody as JSON, queryParameter, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "response as application/json: ${response}"
            def all = [] //as Set
            def resultList = response.hits.hits

            resultList.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        new Date(it._source.createdAt.toLong()),
                        it._source.type as Type,
                        it._source.folder
                        )
                all.add(bookmark)
            }
            assert all.size() <= 1
            return all[0]
        }

    }

    def isFavorite(pId, user) {
        log.info "isFavorite()"
        def vResult = null
        if (user != null) {
            def favorites = findFavoritesByItemIds(user.getId(), [pId])
            log.info "isFavorite findFavoritesByUserId(${user.getId()}, ${pId}): favorites = " + favorites
            if (favorites && (favorites.size() > 0)) {
                vResult = HttpServletResponse.SC_FOUND
            }
            else {
                vResult = HttpServletResponse.SC_NOT_FOUND
            }
        }
        else {
            vResult = HttpServletResponse.SC_UNAUTHORIZED
        }
        log.info "isFavorite ${pId} returns: " + vResult
        return vResult
    }

    /*
     * Given a list of bookmark ID, update its folder values to [folderId]
     *
     * bookmarkIds, list of bookmarks to update
     * folderIds, list of folderId as input
     */
    def copyFavoritesToFolders(List<String> favoriteIds, List<String> folderIds) {
        log.info "copyFavoritesToFolders()"

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_bulk")
        //
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //            /**
        //             *  NOTE: the return carriage is important after each line. See Also:
        //             *  [_bulk endopint fails when on single index]
        //             *  (http://elasticsearch-users.115913.n3.nabble.com/bulk-endopint-fails-when-on-single-index-td4030411.html)
        //             */
        //            def reqBody = ''
        //            favoriteIds.each { it ->
        //                reqBody = reqBody + '{ "update" : {"_id" : "'+ it + '", "_type" : "bookmark", "_index" : "ddb"} }\n'+
        //                        '{ "script" : "ctx._source.folder += otherFolder", "params" : { "otherFolder" : ' + surroundWithQuotes(folderIds)+ '} }\n'
        //            }
        //
        //            body = reqBody
        //
        //            response.success = { resp, json ->
        //                refresh()
        //            }
        //        }

        def postBody = ''
        favoriteIds.each { it ->
            postBody = postBody + '{ "update" : {"_id" : "'+ it + '", "_type" : "bookmark", "_index" : "ddb"} }\n'+
                    '{ "script" : "ctx._source.folder += otherFolder", "params" : { "otherFolder" : ' + surroundWithQuotes(folderIds)+ '} }\n'
        }
        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_bulk", false, postBody)

        if(apiResponse.isOk()){
            refresh()
        }

    }

    private def surroundWithQuotes(stringInList) {
        stringInList.collect { it ->  '"' + it + '"'}
    }

    def findFavoriteById(favoriteId) {
        log.info "findFavoriteById()"

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/${favoriteId}")
        //
        //        http.request(Method.GET, ContentType.JSON) { req ->
        //            response.success = { resp, it->
        //                return  new Bookmark(
        //                it._id,
        //                it._source.user,
        //                it._source.item,
        //                new Date(it._source.createdAt.toLong()),
        //                it._source.type as Type,
        //                it._source.folder
        //                )
        //            }
        //        }

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/${favoriteId}", false, [:])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            Bookmark bookmark = new Bookmark(
                    response._id,
                    response._source.user,
                    response._source.item,
                    new Date(response._source.createdAt.toLong()),
                    response._source.type as Type,
                    response._source.folder
                    )
            return bookmark
        }

    }


    def findFolderById(folderId) {
        log.info "findFolderById()"

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/folder/${folderId}")
        //
        //        http.request(Method.GET, ContentType.JSON) { req ->
        //            response.success = { resp, it->
        //                return new Folder(
        //                it._id,
        //                it._source.user,
        //                it._source.title,
        //                it._source.description,
        //                it._source.isPublic
        //                )
        //            }
        //
        //            response.'404' = { null }
        //        }

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBookmarkUrl(), "/ddb/folder/${folderId}", false, [:])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def description = "null"
            if(!(response._source.description instanceof JSONNull) && (response._source.description != null)){
                description = response._source.description
            }
            Folder folder = new Folder(
                    response._id,
                    response._source.user,
                    response._source.title,
                    description,
                    response._source.isPublic
                    )
            return folder
        } else {
            return null
        }

    }

    def updateFolder(folderId, newTitle, newDescription = null) {
        log.info "updateFolder()"

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/folder/${folderId}/_update")
        //
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //            /*
        //             {
        //             "doc" : {
        //             "title": "foo",
        //             "description": "bar"
        //             }
        //             }
        //             */
        //            if(newDescription) {
        //                body = '''{
        //                            "doc" : {
        //                              "title": "''' + newTitle + '''",
        //                              "description": "''' + newDescription + '''"
        //                            }
        //                         }
        //                      '''
        //            } else {
        //                body = '''{
        //                            "doc" : {
        //                              "title": "''' + newTitle + '''"
        //                            }
        //                         }
        //                      '''
        //            }
        //
        //            response.success = { resp, json ->
        //                refresh()
        //            }
        //        }


        def postBody = ""
        if(newDescription) {
            //postBody = '''{"doc" : {"title": "''' + newTitle + '''", "description": "''' + newDescription + '''"}}'''
            postBody = [doc: [title: newTitle, description: newDescription]]
        } else {
            //postBody = '''{"doc" : {"title": "''' + newTitle + '''"}}'''
            postBody = [doc: [title: newTitle]]
        }

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/folder/${folderId}/_update", false, postBody as JSON)

        if(apiResponse.isOk()){
            refresh()
        }

    }

    def removeFavoritesFromFolder(favoriteIds, folderId) {
        log.info "removeFavoritesFromFolder()"

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_bulk")
        //
        //        http.request(Method.POST, ContentType.JSON) { req ->
        //            /**
        //             *  NOTE: the return carriage is important after each line. See Also:
        //             *  [_bulk endopint fails when on single index]
        //             *  (http://elasticsearch-users.115913.n3.nabble.com/bulk-endopint-fails-when-on-single-index-td4030411.html)
        //             */
        //            def reqBody = ''
        //            favoriteIds.each { it ->
        //                reqBody = reqBody + '{ "update" : {"_id" : "'+ it + '", "_type" : "bookmark", "_index" : "ddb"} }\n'+
        //                        '{ "script" : "ctx._source.folder.remove(otherFolder);", "params" : { "otherFolder" : "' + folderId + '"} }\n'
        //            }
        //
        //            body = reqBody
        //
        //            response.success = { resp, json ->
        //                refresh()
        //            }
        //        }

        def postBody = ''
        favoriteIds.each { it ->
            postBody = postBody + '{ "update" : {"_id" : "'+ it + '", "_type" : "bookmark", "_index" : "ddb"} }\n'+
                    '{ "script" : "ctx._source.folder.remove(otherFolder);", "params" : { "otherFolder" : "' + folderId + '"} }\n'
        }
        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getBookmarkUrl(), "/ddb/bookmark/_bulk", false, postBody)

        if(apiResponse.isOk()){
            refresh()
        }

    }

    def deleteFolder(folderId) {
        log.info "deleteFolder()"

        //        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/folder/${folderId}")
        //
        //        http.request(Method.DELETE, ContentType.JSON) { req ->
        //            response.success = { resp, json ->
        //                log.info "Is folder with the ID ${folderId} deleted(true/false)? ${json.ok}"
        //            }
        //        }

        ApiResponse apiResponse = ApiConsumer.deleteJson(configurationService.getBookmarkUrl(), "/ddb/folder/${folderId}", false)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "Is folder with the ID ${folderId} deleted(true/false)? ${response.ok}"
            refresh()
        }

    }
}
