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

import org.springframework.web.servlet.support.RequestContextUtils as RCU

import de.ddb.common.ApiConsumer
import de.ddb.common.constants.SupportedLocales

class IndexController {

    def configurationService
    def institutionService

    def index() {
        // fetch the DDB news from static server.
        def staticUrl = configurationService.getStaticUrl()
        def locale = SupportedLocales.getBestMatchingLocale(RCU.getLocale(request)).getLanguage()
        def path = locale + "/ddb-services/teaser.html"

        // Submit a request via GET
        def apiResponse = ApiConsumer.getXml(staticUrl, path)
        if(!apiResponse.isOk()){
            log.error "text: Text file was not found"
            apiResponse.throwException(request)
        }
        render(view: "index", model: [articles: rewriteUrls(apiResponse.getResponse().articles.children()),
                                      stats: institutionService.getNumberOfItemsAndInstitutionsWithItems()])
    }

    /**
     * Rewrite CMS server URLs so they suit our needs.
     *
     * @param articles articles with CMS URLs
     * @return articles with modified URLs
     */
    private def rewriteUrls(def articles) {
        articles.each { article ->
            // image URLs from CMS are absolute URLs
            String imageUri = article.imageUri.text()
            String pattern = "/sites/default"
            int index = imageUri.indexOf(pattern)
            if (index >= 0) {
                article.imageUri = configurationService.getContextPath() + "/static/" +
                        imageUri.substring(index + pattern.length() + 1)
            }

            // URLs need our context path in front
            article.uri = configurationService.getContextPath() + article.uri.text()
        }
        return articles
    }
}
