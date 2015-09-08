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

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.exception.ItemNotFoundException
import de.ddb.next.ContentService.RedirectException

class ContentController {
    static defaultAction = "staticcontent"

    def configurationService
    def contentService
    def languageService

    def staticcontent() {
        try {
            def browserUrl = request.forwardURI.substring(request.contextPath.size())
            def location = params.dir ? params.dir : browserUrl.substring("/content".length())
            def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request)).getLanguage()

            try {
                contentService.checkIfRedirectNeeded(location, locale)
            } catch (RedirectException e) {
                redirect uri: e.uri
                return
            }

            while (location.endsWith("/")) {
                location = location.substring(0, location.length() - 1)
            }

            // If first level dir is missing use the default context dir.
            if (!location) {
                redirect uri: new File(browserUrl, configurationService.getDefaultStaticPage()).toString() + "/"
                return
            }


            // Load the the file from $locale/$location.html.
            // If not found then load it from $locale/$location/index.html.
            def response

            try {
                response = contentService.retrieveFile(params.controller, browserUrl, location, locale)
            }
            catch (ItemNotFoundException e) {
                // try default locale
                def defaultLocale = configurationService.getDefaultLanguage().getLanguage()

                if (defaultLocale != locale) {
                    response = contentService.retrieveFile(params.controller, browserUrl, location, defaultLocale)
                }
                else {
                    throw new ItemNotFoundException()
                }
            }
            catch (RedirectException e) {
                redirect uri: e.uri
                return
            }

            def map = contentService.retrieveArguments(response)

            // Needed for the canonicalURL
            map << ["location": location, domainCanonic:configurationService.getDomainCanonic()]

            render(view: "staticcontent", model: map)
        }
        catch (ItemNotFoundException e) {
            forward controller: "error", action: "itemNotFound"
        }
    }
}