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
import de.ddb.common.beans.FolderList
import de.ddb.common.constants.SearchParamEnum

/**
 * Controller class for list related views
 *  
 * @author boz
 */
class ListsController {
    // predefined lists
    private static final FolderList ALL_LIST =
    new FolderList("DdbAllList", "ddbcommon.lists.allList", null, "", "")

    def configurationService
    def listsService
    def searchService

    /**
     * Build the model for the lists
     * 
     * @return
     */
    def index() {
        def model = [lists: [], folders: null, selectedListId : null]

        //Request parameter handling
        //*********************************************************************
        def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
        def queryString = request.getQueryString() ? request.getQueryString() : ""
        int offset = urlQuery[SearchParamEnum.OFFSET.getName()].toInteger()
        int rows = urlQuery[SearchParamEnum.ROWS.getName()].toInteger()

        //Init menu
        //*********************************************************************
        model.lists = createListMenu()

        //Init folders
        //*********************************************************************
        def folders = null

        // load Daily List per default
        String id = params.id ? params.id : "dailyList"

        model.selectedList = model.lists.find {it.folderListId == id }
        folders = getFoldersOfList(id, offset, rows)

        model.folders = folders.folders  as JSON
        model.folderCount = folders.count

        //If a list has no folder, show an error message
        if (model.folders?.size() == 0) {
            model.errorMessage = "ddbnext.lists.listHasNoItems"
        }

        //Pagination stuff
        //*********************************************************************
        def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
        def folderCount = model.folderCount

        //Calculating results details info (number of results in page, total results number)
        def resultsOverallIndex = (offset+1)+' - ' + ((offset + rows>folderCount)? folderCount:offset + rows)

        //Calculating results pagination (previous page, next page, first page, and last page)
        def page = ((int)Math.floor(offset/rows)+1).toString()
        def totalPages = (Math.ceil(folderCount/rows).toInteger())
        def paginationURL = searchService.buildPagination(folderCount, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax",""))

        model.domainCanonic = configurationService.getDomainCanonic()
        model.resultsPaginatorOptions = resultsPaginatorOptions
        model.resultsOverallIndex = resultsOverallIndex
        model.page = page
        model.totalPages = totalPages
        model.paginationURL = paginationURL
        model.linkUri = request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")

        render(view: "lists", model: model)
    }

    /**
     * 
     * @param model
     */
    private createListMenu() {
        def menu = []

        //Search the elastic search index for further lists
        def lists = listsService.findAllLists()
        lists?.each { menu.add(it) }

        // predefined lists
        menu.add(ALL_LIST)

        return menu
    }

    /**
     * Returns the folders for a given list
     * 
     * @param listId the id of the list
     * @return the folders for a given list
     */
    private getFoldersOfList(def listId, int offset=0, int size=20) {
        def folders = null

        if (listId == ALL_LIST.folderListId) {
            folders = listsService.getDdbAllPublicFolders(offset, size)
        }
        else {
            folders = listsService.getPublicFoldersForList(listId, offset, size)
        }
        return folders
    }
}
