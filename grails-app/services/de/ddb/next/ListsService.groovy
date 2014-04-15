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
import net.sf.json.JSONNull
import de.ddb.common.ApiConsumer
import de.ddb.common.ApiResponse
import de.ddb.next.beans.Folder
import de.ddb.next.beans.FolderList

/**
 * Service class for the FolderList mapping in the elastic search
 * 
 * @author boz
 */
class ListsService {

    public static final int DEFAULT_SIZE = 9999

    def elasticSearchService
    def configurationService
    def bookmarksService
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
            user: newFolderList.userId,
            title : newFolderList.title,
            description: newFolderList.description,
            createdAt: newFolderList.creationDate.getTime(),
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
                def description = "null"
                if(!(it._source.description instanceof JSONNull) && (it._source.description != null)){
                    description = it._source.description
                }

                def folderList = new FolderList(
                        it._id,
                        it._source.user,
                        it._source.title,
                        description,
                        it._source.createdAt,
                        it._source.folders
                        )
                if(folderList.isValid()){
                    folderLists.add(folderList)
                }else{
                    log.error "findAllListsByUserId(): found corrupt folder: "+folder
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
                def description = "null"
                if(!(it._source.description instanceof JSONNull) && (it._source.description != null)){
                    description = it._source.description
                }

                def folderList = new FolderList(
                        it._id,
                        it._source.user,
                        it._source.title,
                        description,
                        it._source.createdAt,
                        it._source.folders
                        )
                if(folderList.isValid()){
                    folderLists.add(folderList)
                }else{
                    log.error "findAllListsByUserId(): found corrupt folder: "+folder
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

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folderList/${listId}", false, [:])

        if(apiResponse.isOk()){
            def it = apiResponse.getResponse()

            def description = "null"
            if(!(it._source.description instanceof JSONNull) && (it._source.description != null)){
                description = it._source.description
            }

            def folderList = new FolderList(
                    it._id,
                    it._source.user,
                    it._source.title,
                    description,
                    it._source.createdAt,
                    it._source.folders
                    )
            if(folderList.isValid()){
                return folderList
            }else{
                log.error "findFolderById(): found corrupt folder: " + folderList
            }
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
                userId,
                "ddbnext.lists.userList",
                "Your public favorite lists",
                null,
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
                "",
                "ddbnext.lists.ddbDailyList",
                "The DDB daily favorite lists",
                null,
                ""
                )
        return folderList
    }

    /**
     * 
     * @param userId
     * @return
     */
    List<Folder> getFoldersForList(String folderId) {
        List<Folder> folders = []
        FolderList folderList = findListById(folderId)

        folderList.folders.each {
            folders.add(bookmarksService.findFolderById(it))
        }

        return folders
    }
}
