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

import org.codehaus.groovy.grails.web.json.*

import de.ddb.common.ApiConsumer
import de.ddb.common.ApiResponse
import de.ddb.next.beans.FolderList

class ListsService {

    def elasticSearchService
    def configurationService
    def transactional = false


    /**
     * Create a new bookmark folder.
     *
     * @param newFolder Folder object to persist
     * @return          the newly created folder ID.
     */
    String createList(FolderList newFolderList) {
        log.info "createFolder(): creating a new folder: ${newFolder}"

        String newFolderListId = null

        def postBody = [
            user: newFolderList.userId,
            title : newFolderList.title,
            description: newFolderList.description,
            createdAt: newFolderList.creationDate
        ]
        def postBodyAsJson = postBody as JSON

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/folderList", false, postBodyAsJson)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            newFolderListId = response._id
            elasticSearchService.refresh()
        }

        return newFolderListId
    }
}
