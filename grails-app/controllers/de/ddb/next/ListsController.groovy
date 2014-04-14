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
import de.ddb.common.beans.User

/**
 * Controller class for list related views
 *  
 * @author boz
 */
class ListsController {
    def aasService
    def bookmarksService
    def favoritesService
    def configurationService
    def searchService
    def sessionService
    def listsService

    /**
     * Build the model for the lists
     * 
     * @return
     */
    def index() {
        def model = [title: "Listen", lists: []]

        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            // Get the public folder list of the user
            def publicFolderList = listsService.getPublicFolderListForUser(user.getId())
            model.lists.add(publicFolderList)
        }

        def lists = listsService.findAllLists()
        lists?.each {
            model.lists.add(it)
        }

        if (params.id) {
            model.folders = getFoldersOfList(params.id)
        }

        render(view: "lists", model: model)
    }

    /**
     * Returns the folders for a given list
     * 
     * @param listId the id of the list
     * @return the folders for a given list
     */
    private getFoldersOfList(def listId) {
        def folders = null

        if (listId == "0") {
            folders = getPublicFoldersForUser()
        } else {
            folders = listsService.getFoldersForList(listId)
        }

        return folders as JSON
    }

    /**
     * Returns the public folders for the already logged in user
     *  
     * @return the public folders for the already logged in user
     */
    private getPublicFoldersForUser() {
        def folders

        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            folders = bookmarksService.findAllPublicFolders(user.getId())

            folders = favoritesService.sortFolders(folders)
            folders.each {
                //Set the blocking token to ""
                it.blockingToken = ""
                //Retrieve the number of favorites
                //TODO: use the elastic search query syntax for doing this!
                List favoritesOfFolder = bookmarksService.findBookmarksByFolderId(user.getId(), it.folderId)
                it.count = favoritesOfFolder.size()
            }
        }

        return folders
    }
}
