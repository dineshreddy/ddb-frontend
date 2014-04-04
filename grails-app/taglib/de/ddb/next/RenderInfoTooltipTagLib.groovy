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

class RenderInfoTooltipTagLib {

    static namespace = "ddb"

    def grailsLinkGenerator

    /**
     * Renders the infoTooltip template.
     * The tooltip can contain a link. The path of the link is specified via an (infoDir & infoId) or via an controllerAction
     *
     * @attr messageCode, infoId, infoDir, controllerAction
     */
    def renderInfoTooltip = { attrs, body ->
        def link = null
        def infoId = attrs.infoId
        def infoDir = attrs.infoDir
        def controllerAction = attrs.controllerAction

        if(infoId && infoDir) {
            link = grailsLinkGenerator.link(controller: "content", params: [dir: infoDir, id: infoId])
        } else if (controllerAction) {
            link = grailsLinkGenerator.link(controller: "content", action: controllerAction)
        }

        out << render(template:"/common/infoTooltip", model:[messageCode: attrs.messageCode, link: link, hasArrow: attrs.hasArrow])
    }
}
