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

import de.ddb.next.exception.ItemNotFoundException

class ItemController {
    static defaultAction = "findById"
    def fileService
    def itemService
    def configurationService

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
//                // inline images via data uris
//                model = itemService.prepareImagesForPdf(model)
//
//                try {
//                    renderPdf(template: "itemPdfTable", model: model, filename: "DDB-Item-${id}.pdf")
//                } catch (grails.plugin.rendering.document.XmlParseException e) {
//                    log.error "findById(): PDF Generation failed due to XmlParseException: " + e.getMessage() + ". Going 404..."
//                    forward controller: "error", action: "pdfNotFound"
//                }
                //render(view: "_itemPdfTable", model: model) //(Do not remove for the moment)
            } else {
                render(view: "item", model: model)
            }

        } catch(ItemNotFoundException infe) {
            
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
    
    def sendPdf() {
        def itemId = params.id
        def url = configurationService.getSelfBaseUrl() +g.createLink(controller: 'item', params:[id:itemId]).toString()+"?pdf=1"
        def message = g.message(code:'ddbnext.item.sendPdfMailSuccess')
        try {
            def fileBytes = fileService.downloadFile(url)
            try {
                sendMail {
                    multipart true
                    to params.email
                    subject g.message(code: 'ddbnext.item.sendPdfSubjectOnEmail')
                    body g.message(code: 'ddbnext.item.sendPdfBodyOnEmail')
                    attach(params.id+".pdf", "application/pdf", fileBytes)
                }
            } catch (Exception e) {
                log.error "Failed Sending PDF per Email "+ e.getLocalizedMessage()
                message = g.message(code: 'ddbnext.item.sendPdfFailsToSendMailPDF')
            }
        } catch (FileNotFoundException e) {
            log.error "Failed Sending PDF per Email! Reason!? Cannot retrieve PDF file "+ e.getLocalizedMessage()
            message = g.message(code: 'ddbnext.item.sendPdfFailsToSendMailPDF')
        }
        

        render(contentType:"application/json", text: message)
    }
}
