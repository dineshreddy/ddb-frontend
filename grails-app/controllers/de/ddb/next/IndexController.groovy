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
import de.ddb.common.ApiResponse

class IndexController {

    def configurationService
    def ddbItemService
    def languageService

    def index() {
        // Fetch the DDB teaser from static server.
        String staticUrl = configurationService.getStaticUrl()
        String locale = languageService.getBestMatchingLocale(RCU.getLocale(request)).getLanguage()
        String path = locale + "/ddb-services/teaser.xml"
        ApiResponse apiResponse = ApiConsumer.getXml(staticUrl, path)
        def articles

        if (apiResponse.isOk()) {
            articles = apiResponse.getResponse().articles.children()
        }
        render(view: "index", model: [
            articles: rewriteUrls(articles),
            domainCanonic: configurationService.getDomainCanonic(),
            stats: ddbItemService.getNumberOfItems()
        ])
    }

    /**
     * Rewrite CMS server URLs so they suit our needs.
     *
     * @param articles articles with CMS URLs
     * @return articles with modified URLs
     */
    private def rewriteUrls(def articles) {
        if (articles) {
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
        }
        return articles
    }
}
