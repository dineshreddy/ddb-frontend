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

import net.sf.json.JSON

import grails.converters.JSON

import java.text.SimpleDateFormat

import net.sf.json.JSONNull

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.ApiConsumer
import de.ddb.common.ApiResponse
import de.ddb.common.beans.User
import de.ddb.next.beans.Folder
import de.ddb.next.beans.FolderList

/**
 * Service class for the FolderList mapping in the elastic search
 * 
 * @author boz
 */
class ListsService {

    public static final int DEFAULT_SIZE = 9999

    def bookmarksService
    def favoritesService
    def configurationService
    def searchService
    def elasticSearchService
    def transactional = false


    /**
     * Create a new {@link FolderList}
     *
     * @param newFolder FolderList object to persist
     * @return the id of the created FolderList
     */
    String createList(FolderList newFolderList) {
        log.info "createList(): creating a new folder: ${newFolderList}"

        String newFolderListId = null

        def postBody = [
            title : newFolderList.title,
            description: newFolderList.description,
            createdAt: newFolderList.creationDate.getTime(),
            users: newFolderList.users,
            folders: newFolderList.folders
        ]

        def postBodyAsJson = postBody as JSON
        log.info "postBodyAsJson" + postBodyAsJson
        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/folderList", false, postBodyAsJson)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            newFolderListId = response._id
            elasticSearchService.refresh()
        }

        return newFolderListId
    }

    /**
     * Finds all {@link FolderList} of the index
     *
     * @return all {@link FolderList} of the index
     */
    List<FolderList> findAllLists() {
        log.info "findAllLists()"

        List<FolderList> folderLists = []

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folderList/_search", false)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits

            resultList.each { it ->
                def folderList = mapJsonToFolderList(it)

                if(folderList && folderList.isValid()){
                    folderLists.add(folderList)
                }else{
                    log.error "findAllListsByUserId(): found corrupt list: " + folderList
                }
            }
        }
        return folderLists
    }


    /**
     * Finds all {@link FolderList} belonging to a userId
     * @param userId the id of a user
     * 
     * @return all {@link FolderList} belonging to a userId
     */
    List<FolderList> findListsByUserId(String userId) {
        log.info "findAllListsByUserId()"

        List<FolderList> folderLists = []

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folderList/_search", false,
                ["q":userId, "size":"${DEFAULT_SIZE}"])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits

            resultList.each { it ->
                def folderList = mapJsonToFolderList(it)

                if(folderList && folderList.isValid()){
                    folderLists.add(folderList)
                }else{
                    log.error "findAllListsByUserId(): found corrupt list: " + folderList
                }
            }
        }
        return folderLists
    }

    /**
     * Finds a {@link FolderList} by its id
     * @param listId the id of the list to search for
     * 
     * @return a {@link FolderList}
     */
    FolderList findListById(String listId) {
        log.info "findFolderById()"
        def retVal = null

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folderList/${listId}", false, [:])

        if(apiResponse.isOk()){
            def it = apiResponse.getResponse()
            retVal = mapJsonToFolderList(it)
        }
        return retVal
    }

    /**
     * Maps the JSON from a elasticsearch request to an {@link FolderList} instance
     * 
     * @return a {@link FolderList} instance of the JSON
     */
    private FolderList mapJsonToFolderList(def json) {
        def description = ""
        if(!(json._source.description instanceof JSONNull) && (json._source.description != null)){
            description = json._source.description
        }

        def folderList = new FolderList(
                json._id,
                json._source.title,
                description,
                json._source.createdAt,
                json._source.users,
                json._source.folders
                )

        if(folderList.isValid()){
            return folderList
        }else{
            log.error "findFolderById(): found corrupt folder: " + folderList
        }

        return null
    }

    /**
     * Returns the number of lists available in the elasticsearch index
     * @return the number of lists in the search index
     */
    int getListCount() {
        int count = -1

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folderList/_search", false)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            count = response.hits.total
        }

        return count
    }

    /**
     * Deletes all lists belonging to a userId
     *
     * @param userId the id of a user
     *
     * @return <code>true</code> if at least one list has been deleted for the given userId
     */
    boolean deleteAllUserLists(String userId) {
        log.info "deleteAllUserLists()"
        List<FolderList> allUserFolders = findListsByUserId(userId)

        List<String> folderIds = []

        allUserFolders.each { it ->
            folderIds.add(it.folderListId)
        }

        return elasticSearchService.deleteTypeEntriesByIds(folderIds, "folderList")
    }

    /**
     * Return a {@link FolderList} containing all public folders for a given user
     * 
     * @return a {@link FolderList} containing all public folders for a given user
     */
    def getUserList(String userId) {
        List<Folder> publicFolders = bookmarksService.findAllPublicFolders(userId)

        def folderIds = []
        publicFolders.each { it ->
            folderIds.add(it.folderId)
        }

        def folderList = new FolderList(
                "UserList",
                "ddbnext.lists.userListTitle",
                "ddbnext.lists.userListDescription",
                null,
                userId,
                folderIds
                )

        return folderList
    }

    /**
     * Return a {@link FolderList} containing the public folders for the current day
     *
     * @return  a {@link FolderList} containing the public folders for the current day
     */
    def getDdbDailyList() {

        def folderList = new FolderList(
                "DdbDailyList",
                "ddbnext.lists.dailyListTitle",
                "ddbnext.lists.dailyListDescription",
                null,
                "",
                ""
                )
        return folderList
    }

    /**
     * Returns the public folders for the already logged in user
     *
     * @return the public folders for the already logged in user
     */
    private getUserFolders() {
        def folders = null

        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            folders = bookmarksService.findAllPublicFolders(user.getId())
            folders = enhanceFolderInformation(folders)
        }

        return folders
    }

    /**
     * Returns the public folders for the already logged in user
     *
     * @return the public folders for the already logged in user
     */
    private getDdbDailyFolders() {
        def folders = null

        folders = bookmarksService.findAllPublicFoldersDaily(new Date())
        return enhanceFolderInformation(folders)
    }

    /**
     * 
     * @param userId
     * @return
     */
    List<Folder> getPublicFoldersForList(String listId) {
        List<Folder> folders = []
        FolderList folderList = findListById(listId)

        folderList?.users?.each {
            folders.addAll(bookmarksService.findAllPublicFolders(it))
        }

        folderList?.folders?.each {
            def folder = bookmarksService.findFolderById(it)
            if (folder.isPublic) {
                folders.add(folder)
            }
        }

        return enhanceFolderInformation(folders)
    }

    /**
     * Sorts the found folders and adds the number of favorites
     * @param folders the folder to enhance
     *
     * @return the enhanced Folder
     */
    private enhanceFolderInformation(def folders) {
        def request = RequestContextHolder.currentRequestAttributes().request
        Locale locale = RequestContextUtils.getLocale(request)
        folders = favoritesService.sortFolders(folders)

        folders.each {
            //Set the blocking token to ""
            it.blockingToken = ""

            //Get the image path of the oldest item in the list
            List favoritesOfFolder = bookmarksService.findBookmarksByPublicFolderId(it.folderId)
            favoritesOfFolder.sort{it.creationDate}
            if (favoritesOfFolder.size() > 0) {
                def itemMd = favoritesService.retriveItemMD([favoritesOfFolder.get(0)], locale)
                it.oldestItemMetaData = itemMd.get(0)
            }

            //Retrieve the number of favorites
            it.count = favoritesOfFolder.size()
            it.creationDateFormatted = formatDate(it.creationDate, locale)
        }
    }

    private String formatDate(Date oldDate, Locale locale) {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd.MM.yyy")
        newFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"))
        return newFormat.format(oldDate)
    }
}
