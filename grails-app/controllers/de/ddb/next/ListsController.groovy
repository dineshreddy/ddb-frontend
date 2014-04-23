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
        def model = [lists: [], folders: "", selectedListId : null]

        model.lists = createListMenu(model)

        //If a list of the menu has been selected take it
        if (params.id) {
            model.selectedListId = params.id
            model.folders = getFoldersOfList(params.id)
        }
        //If the page is loaded for the first time, take the first entry in the menu
        else if (model.lists.size() > 0) {
            def firstList = model.lists.get(0)
            model.selectedListId = firstList.folderListId
            model.folders = getFoldersOfList(firstList.folderListId)
        }
        //Otherwise show an error message
        else {
            model.errorMessage = "Keine Listen verfügbar"
        }

        //If a list has no folder, show an error message
        if (model.folders?.size() == 0) {
            model.errorMessage = "ddbnext.lists.listHasNoItems"
        }

        render(view: "lists", model: model)
    }

    /**
     * 
     * @param model
     */
    private createListMenu(def model) {
        def User user = favoritesService.getUserFromSession()

        def menu = []

        //If the user is logged in initialize his public favorite lists
        if (user != null) {
            // Get the public folder list of the user
            def userList = listsService.getUserList(user.getId())
            menu.add(userList)
        }

        //Initialize the daily favorite lists
        def ddbDailyList = listsService.getDdbDailyList()
        menu.add(ddbDailyList)

        //Search the elastic search index for further lists
        def lists = listsService.findAllLists()
        lists?.each { menu.add(it) }

        return menu
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
