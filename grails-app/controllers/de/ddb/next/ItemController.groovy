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

    def findById() {
        try {
            def id = params.id
            def model = itemService.getFullItemModel(id)

            if("404".equals(model)){
                throw new ItemNotFoundException()
            }

            // TODO: handle 404 and failure separately. HTTP Status Code 404, should
            // to `not found` page _and_ Internal Error should go to `internal server
            // error` page. We should send also the HTTP Status Code 404 or 500 to the
            // Client.
            if(model == '404' || model?.failure) {
                redirect(controller: 'error')
            } else {
                render(view: "item", model: model)
            }
        } catch(ItemNotFoundException infe){
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
