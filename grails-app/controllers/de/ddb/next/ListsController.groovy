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

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.beans.User
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.constants.SupportedLocales



/**
 * Controller class for list related views
 *  
 * @author boz
 */
class ListsController {
    def favoritesService
    def listsService
    def searchService

    /**
     * Build the model for the lists
     * 
     * @return
     */
    def index() {
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))

        def model = [lists: [], folders: null, selectedListId : null]

        model.lists = createListMenu()

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

        //If a list has no folder, show an error message
        if (model.folders?.size() == 0) {
            model.errorMessage = "ddbnext.lists.listHasNoItems"
        }

        //Handling pagination
        def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
        // convertQueryParametersToSearchParameters modifies params
        params.remove("query")
        urlQuery["offset"] = 0
        def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)

        def folderNumbers = model.folders?.size()

        //Calculating results details info (number of results in page, total results number)
        def resultsOverallIndex = (urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()+1)+' - ' +
                ((urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()+
                urlQuery[SearchParamEnum.ROWS.getName()].toInteger()>folderNumbers)? folderNumbers:urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()+urlQuery[SearchParamEnum.ROWS.getName()].toInteger())

        //Calculating results pagination (previous page, next page, first page, and last page)
        def page = ((int)Math.floor(urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()/urlQuery[SearchParamEnum.ROWS.getName()].toInteger())+1).toString()
        def totalPages = (Math.ceil(folderNumbers/urlQuery[SearchParamEnum.ROWS.getName()].toInteger()).toInteger())
        def totalPagesFormatted = String.format(locale, "%,d", totalPages.toInteger())

        model.resultsPaginatorOptions = resultsPaginatorOptions
        model.resultsOverallIndex = resultsOverallIndex
        model.page = page
        model.totalPages = totalPages

        println model.resultsPaginatorOptions
        println model.resultsOverallIndex
        println model.page
        println model.totalPages

        render(view: "lists", model: model)
    }

    /**
     * 
     * @param model
     */
    private createListMenu() {
        def User user = favoritesService.getUserFromSession()

        def menu = []

        //Initialize the daily favorite lists
        def ddbAllList = listsService.getDdbAllList()
        menu.add(ddbAllList)

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
        } else if (listId == "DdbAllList") {
            folders = listsService.getDdbAllPublicFolders()
        }else if (listId == "DdbDailyList") {
            folders = listsService.getDdbDailyFolders()
        } else {
            folders = listsService.getPublicFoldersForList(listId)
        }

        return folders as JSON
    }

}
