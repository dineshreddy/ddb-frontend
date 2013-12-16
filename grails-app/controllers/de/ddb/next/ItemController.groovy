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

import de.ddb.next.exception.ItemNotFoundException

class ItemController {
    static defaultAction = "findById"

    def itemService

    /**
     * Handle the default show Item logic
     * The business logic is moved to ItemService
     * Errors and validation are handled in the ItemService. If an error an ItemNotFoundException will be thrown
     */
    def findById() {
        try {
            def id = params.id
            def model = itemService.getFullItemModel(id)

            if(params.pdf){
              render(view: "itemPdf", model: model)
            } else {
                render(view: "item", model: model)
              }

        } catch(ItemNotFoundException infe) {
            log.error "findById(): Request for nonexisting item with id: '" + params?.id + "'. Going 404..."
            forward controller: "error", action: "itemNotFound"
        }
    }

    def parents() {
        def jsonResp = itemService.getParent(params.id)

        render(contentType:"application/json", text: jsonResp)
    }

    def children() {
        def jsonResp = itemService.getChildren( params.id)

        render(contentType:"application/json", text: jsonResp)
    }

    def showXml() {
        def itemId = params.id

        response.contentType = "text/xml"
        response.outputStream << itemService.fetchXMLMetadata(itemId)
    }
}
