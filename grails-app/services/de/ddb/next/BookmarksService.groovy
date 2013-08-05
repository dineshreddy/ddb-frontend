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


import groovy.json.*
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import de.ddb.next.beans.Bookmark
import de.ddb.next.beans.Folder
import de.ddb.next.beans.Bookmark.Type


/**
 * Set of Methods that encapsulate REST-calls to the BookmarksService
 *
 * @author mih
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
    def newFolder(userId, title, isPublic) {
        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/folder")

        log.info "creating a new folder with the title: ${title}"
        def folderId
        http.request(Method.POST, ContentType.JSON) { req ->
           body = [
             user: userId,
             title : title,
             isPublic : isPublic
           ]

           response.success = { resp, json ->
               folderId = json._id
               refresh()
           }
        }

        folderId
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
        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/folder/_search?q=user:${userId}")
        http.request(Method.GET, ContentType.JSON) { req ->
           response.success = { resp, json ->
               def resultList = json.hits.hits
               def folderList = []
               resultList.each { it ->
                    def folder = new Folder(
                       folderId: it._id,
                       userId: it._source.user,
                       title: it._source.title,
                       isPublic: it._source.isPublic
                   )
                   folderList.add(folder)
               }
               return folderList
           }
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
        log.info "find bookmarks for the user (${userId}) in the folder ${folderId}"
        def http = new HTTPBuilder(
            "${configurationService.getBookmarkUrl()}/ddb/bookmark/_search?q=user:${userId}%20AND%20folder:${folderId}%20&size=${size}")
        http.request(Method.GET, ContentType.JSON) { req ->

           response.success = { resp, json ->
               def all = []
               def resultList = json.hits.hits
               resultList.each { it ->
                   def bookmark = new Bookmark(
                        bookmarkId: it._id,
                        userId: it._source.user,
                        itemId: it._source.item,
                        creationDate: new Date(it._source.createdAt.toLong())
                   )
                   all.add(bookmark)
               }
               all
           }

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
        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark")

        def bookmarkId
        http.request(Method.POST, ContentType.JSON) { req ->
           body = [
             user: userId,
             folder: folderId,
             item: itemId,
             createdAt: new Date().getTime(),
             type: type
           ]

           response.success = { resp, json ->
               bookmarkId = json._id
               log.info "Bookmark ${bookmarkId} is created."
               refresh()
           }
        }
        bookmarkId
    }

    private refresh() {
        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/_refresh")

        log.info "refreshing index ddb..."
        http.request(Method.POST, ContentType.JSON) { req ->
           response.success = { resp, json ->
               log.info "Response: ${json}"
               log.info "finished refreshing index ddb."
           }
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
        log.info "itemIdList ${itemIdList}"

        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_search?q=user:${userId}")
        http.request(Method.POST, ContentType.JSON) { req ->
            body = [
              filter: [
                terms: [
                  item: itemIdList
                ]
              ]
            ]

            response.success = { resp, json ->
                log.info "response as application/json: ${json}"
                // TODO: use inject if possible
                def items = [] as Set
                json.hits.hits.each { it ->
                    items.add(it._source.item)
                }
                items
            }
        }
    }

    /**
     * Delete all bookmarks of cultural items in the {bookmarkIdList} belong to the user.
     *
     * @param userId         the ID who bookmarked the cultural items.
     * @param bookmarkIdList a list of bookmark IDs. NOTE: These are _not_ a list of cultural item IDs.
     */
    def deleteBookmarks(userId, bookmarkIdList) {
        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_bulk")
        println("#####"+bookmarkIdList)
        http.request(Method.POST, ContentType.JSON) { req ->
            def reqBody = ''
            bookmarkIdList.each { id ->
                reqBody = reqBody + '{ "delete" : { "_index" : "ddb", "_type" : "bookmark", "_id" : "' + id + '" } }\n'
            }

            body = reqBody
            response.success = {
              refresh()
              return true
            }
        }
    }

    def addFavorite(userId, itemId, type = Type.CULTURAL_ITEM) {
        def favoriteFolderId = getFavoritesFolderId(userId)

        def foundItemIdList = findBookmarkedItemsInFolder(userId,[itemId], favoriteFolderId)
        if(foundItemIdList.size()>0) {
          log.warn('The item ID (itemId) is already in the Favorites')
          return null
        }

        def bookmarkId = saveBookmark(userId, favoriteFolderId, itemId, type)
        log.info "Add a bookmark ${bookmarkId} in Favorites"
        return bookmarkId
    }

    // TODO this is _broken_, sometimes it finds more than one folders with the title favorites for a user.
    def findFoldersByTitle(userId, title) {
        log.info "finding a folder with the title ${title} for the user: ${userId}"
        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/folder/_search?q=user:${userId}")

        http.request(Method.POST, ContentType.JSON) { req ->

            body = [
              filter: [
                term: [
                  title: title
                ]
              ]
            ]

           response.success = { resp, json ->
               log.info json
               def resultList = json.hits.hits
               def all = []
               resultList.each { it ->
                   def folder = new Folder(
                        folderId: it._id,
                        userId: it._source.user,
                        title: it._source.title,
                        isPublic: it._source.isPublic
                   )

                   all.add(folder)
               }

               log.info "found #folder: ${all.size()} with the title ${title}"
               return all
           }
       }
    }

    def findFavoritesByUserId(userId, size = DEFAULT_SIZE) {
        def favoriteFolderId = getFavoritesFolderId(userId)
        return findBookmarksByFolderId(userId, favoriteFolderId, size)
    }

    def deleteFavorites(userId, itemIds) {
        def bookmarkIds = []
        def allFavorites = findFavoritesByUserId(userId, DEFAULT_SIZE)
        log.info "favs: ${allFavorites}"
        allFavorites.each { it ->
            log.info "fav: ${it}"
            if(it.itemId  in itemIds.ids) {
                bookmarkIds.add(it.bookmarkId)
            }
        }

        log.info "delete favorites for the items ${itemIds}"
        log.info "delete favorites with the bookmarkIds ${bookmarkIds}"
        return deleteBookmarks(userId, bookmarkIds)
    }

    def findFavoritesByItemIds(userId, itemIdList) {
        def favoriteFolderId = getFavoritesFolderId(userId)
        log.info "fav: ${favoriteFolderId}"
        log.info "itemIdList ${itemIdList}"
        return findBookmarkedItemsInFolder(userId, itemIdList, favoriteFolderId)
    }

    def getFavoritesFolderId(userId) {
        def favoritesFolderList = findFoldersByTitle(userId, BookmarksService.FAVORITES)

        assert favoritesFolderList.size() <= 1 :"There must be max one folder with the title Favorites"

        def favoritesFolderId
        if(favoritesFolderList.size() > 0) {
            favoritesFolderId  = favoritesFolderList[0].folderId
        } else {
            /* The user does not have a 'Favorites' folder, create a folder with
             * the title 'Favorites'
             */
            log.info "The user(${userId}) does not have a 'Favorites' folder, the service is creating it."
            favoritesFolderId = newFolder(userId,FAVORITES, IS_PUBLIC)
            log.info "New Favorites Folder is created and has the ID: ${favoritesFolderId}"
        }
        favoritesFolderId
    }

    // TODO refactor this method, duplicate with findFavoritesByItemIds
    def findBookmarkedItemsInFolder(userId, itemIdList, folderId) {
        log.info "itemIdList ${itemIdList}"

        def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_search?q=user:${userId}%20AND%20folder:${folderId}&size=${DEFAULT_SIZE}")
        http.request(Method.POST, ContentType.JSON) { req ->
            body = [
              filter: [
                terms: [
                  item: itemIdList
                ]
              ]
            ]

            response.success = { resp, json ->
                log.info "response as application/json: ${json}"
                // TODO: use inject if possible
                def items = [] as Set
                json.hits.hits.each { it ->
                    items.add(it._source.item)
                }
                items
            }
        }
    }

    def findFavoriteByItemId(userId, itemId) {
      log.info "itemId: ${itemId}"

      def folderId = getFavoritesFolderId(userId)

      def http = new HTTPBuilder("${configurationService.getBookmarkUrl()}/ddb/bookmark/_search?q=user:${userId}%20AND%20folder:${folderId}&size=${DEFAULT_SIZE}")
      http.request(Method.POST, ContentType.JSON) { req ->
          body = [
            filter: [
              terms: [
                item: [itemId]
              ]
            ]
          ]

          response.success = { resp, json ->
              log.info "response as application/json: ${json}"
              def all = [] //as Set

              def resultList = json.hits.hits
              resultList.each { it ->
                  def bookmark = new Bookmark(
                       bookmarkId: it._id,
                       userId: it._source.user,
                       itemId: it._source.item,
                       creationDate: new Date(it._source.createdAt.toLong())
                  )
                  all.add(bookmark)
              }
              assert all.size() <= 1
              all[0]
          }
      }
    }
}
