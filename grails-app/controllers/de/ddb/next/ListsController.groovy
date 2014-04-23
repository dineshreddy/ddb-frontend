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
    def favoritesService
    def listsService

    /**
     * Build the model for the lists
     * 
     * @return
     */
    def index() {
        def model = [title: "Listen", lists: []]

        createListMenu(model)

        if (params.id) {
            createListDetails(model)
        } else {
            model.errorMessage = "ddbnext.lists.pleaseSelectList"
        }

        render(view: "lists", model: model)
    }

    /**
     * 
     * @param model
     */
    private void createListMenu(def model) {
        def User user = favoritesService.getUserFromSession()

        //If the user is logged in initialize his public favorite lists
        if (user != null) {
            // Get the public folder list of the user
            def userList = listsService.getUserList(user.getId())
            model.lists.add(userList)
        }

        //Initialize the daily favorite lists
        def ddbDailyList = listsService.getDdbDailyList()
        model.lists.add(ddbDailyList)

        //Search the elastic search index for further lists
        def lists = listsService.findAllLists()
        lists?.each {
            model.lists.add(it)
        }
    }


    /**
     * 
     * @param model
     */
    private void createListDetails(def model) {
        //Load the folders of a list
        model.folders = getFoldersOfList(params.id)

        if (model?.folders?.size() == 0) {
            model.errorMessage = "ddbnext.lists.listHasNoItems"
        }
    }


    /**
     * Returns the folders for a given list
     * 
     * @param listId the id of the list
     * @return the folders for a given list
     */
    private getFoldersOfList(def listId) {
        def folders = null

        if (listId == "UserList") {
            folders = listsService.getUserFolders()
        } else if (listId == "DdbDailyList") {
            folders = listsService.getDdbDailyFolders()
        } else {
            folders = listsService.getPublicFoldersForList(listId)
        }

        return folders as JSON
    }

}
