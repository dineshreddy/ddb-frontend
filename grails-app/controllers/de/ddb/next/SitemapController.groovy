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

import groovy.xml.XmlUtil

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.ApiConsumer

class SitemapController {
    def configurationService
    def languageService

    def index() {
        def staticUrl = configurationService.getStaticUrl()
        def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request)).getLanguage()
        def path = locale + "/ddb-services/sitemap.xml"
        def apiResponse = ApiConsumer.getAny(staticUrl, path)

        if (!apiResponse.isOk()) {
            log.error "sitemap file was not found"
            apiResponse.throwException(request)
        }
        render(contentType: "text/xml", text: rewriteUrls(apiResponse.getResponse(), locale))
    }

    /**
     * Rewrite CMS server URLs so they suit our needs.
     *
     * @param content as XML content with CMS URLs
     * @return content as String with modified URLs
     */
    private String rewriteUrls(def content, String locale) {
        String cmsHost = new URL(configurationService.getCmsUrl()).getHost()
        URL publicUrl = new URL(configurationService.getPublicUrl())

        content.depthFirst().collect {it}.findAll {it}.each {element ->
            if (element.name() == "loc") {
                URL url = new URL(element.text())

                if (url.getHost() == cmsHost) {
                    String path = url.getPath()

                    // remove language from path
                    if (path.startsWith("/" + locale)) {
                        path = path.substring(locale.length() + 1)
                    }

                    // prefix path with "content"
                    if (path.length() > 0) {
                        path = "content" + path
                    }
                    element.parent().loc = new URL(publicUrl, path)
                }
            }
        }
        return XmlUtil.serialize(content)
    }
}
