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

    def index() {
        log.info "ListsController index"

        def model = [title: "Listen", folders:null]

        log.info "getFavoriteFolders"

        def User user = favoritesService.getUserFromSession()
        if (user != null) {
            def mainFolder = bookmarksService.findMainBookmarksFolder(user.getId())
            def folders = bookmarksService.findAllFolders(user.getId())
            folders.find {it.folderId == mainFolder.folderId}.isMainFolder = true
            folders = favoritesService.sortFolders(folders)
            folders.each {it.blockingToken = ""} // Don't expose the blockingToken to Javascript
            log.info "getFavoriteFolders returns " + folders
            model.folders = folders as JSON
            render(view: "lists", model: model)
        } else {
            log.info "getFavoriteFolders returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }
}
