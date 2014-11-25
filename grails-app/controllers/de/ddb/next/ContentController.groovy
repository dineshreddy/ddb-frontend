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

import org.ccil.cowan.tagsoup.Parser
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.ApiConsumer
import de.ddb.common.constants.SupportedLocales
import de.ddb.common.exception.ItemNotFoundException

class ContentController {
    static defaultAction = "staticcontent"

    def configurationService

    def staticcontent() {
        try {
            def browserUrl = request.forwardURI.substring(request.contextPath.size())
            def location = browserUrl.substring("/content".length())
            while (location.endsWith("/")) {
                location = location.substring(0, location.length() - 1)
            }

            /* If first level dir is missing use a default context dir from contentDefault. */
            if (!location) {
                redirect uri: new File(browserUrl, configurationService.getDefaultStaticPage()).toString() + "/"
                return
            }

            def url = configurationService.getStaticUrl()
            def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request)).getLanguage()

            /* Load the the file from $content-location.html. If not found then load it from $content-location/index.html */
            def path = locale.toString() + "/" + location + ".html"
            def response
            def apiResponse = ApiConsumer.getText(url, path, false)
            if (apiResponse.isOk()) {
                response = apiResponse.getResponse()
            } else {
                if (!browserUrl.endsWith ("/")) {
                    redirect uri: browserUrl + "/"
                    return
                }
                path = locale.toString() + "/" + location + "/index.html"
                apiResponse = ApiConsumer.getText(url, path, false)
                if(apiResponse.isOk()){
                    response = apiResponse.getResponse()
                }
                else {
                    throw new ItemNotFoundException()
                }
            }
            def map = retrieveArguments(response)
            render(view: "staticcontent", model: map)
        } catch (ItemNotFoundException infe) {
            log.error "staticcontent(): Request for nonexisting item with id: '" + params?.dir + "'. Going 404..."
            forward controller: "error", action: "itemNotFound"
        }
    }

    private def retrieveArguments(def content){
        def title = fetchTitle(content)
        def author = fetchAuthor(content)
        def keywords = fetchKeywords(content)
        def robot = fetchRobots(content)
        def metaDescription = fetchMetaDescription(content)
        def body = fetchBody(content)
        return [
            title:title,
            author:author,
            keywords:keywords,
            robot:robot,
            metaDescription:metaDescription,
            content:rewriteUrls(body)
        ]
    }

    private def fetchBody(content) {
        def bodyMatch = content =~ /(?s)<body\b[^>]*>(.*?)<\/body>/
        return bodyMatch[0][1]
    }

    private def fetchAuthor(content) {
        def authorMatch = content =~ /(?s)<meta (.*?)name="author" (.*?)content="(.*?)"(.*?)\/>/
        if (authorMatch)
            return authorMatch[0][3]
    }

    private def fetchTitle(content) {
        def titleMatch = content =~ /(?s)<title\b[^>]*>(.*?)<\/title>/
        if (titleMatch)
            return titleMatch[0][1]
    }

    private def fetchKeywords(content) {
        def keywordMatch = content =~ /(?s)<meta (.*?)name="keywords" (.*?)content="(.*?)"(.*?)\/>/
        if (keywordMatch)
            return keywordMatch[0][3]
    }

    private def fetchMetaDescription(content) {
        def keywordMatch = content =~ /(?s)<meta (.*?)name="description" (.*?)content="(.*?)"(.*?)\/>/
        if (keywordMatch)
            return keywordMatch[0][3]
    }

    private def fetchRobots(content) {
        def robotMatch = content =~ /(?s)<meta (.*?)name="robots" (.*?)content="(.*?)"(.*?)\/>/
        if (robotMatch)
            return robotMatch[0][3]
    }

    /**
     * Rewrite CMS server URLs so they suit our needs.
     *
     * @param content content with CMS URLs
     * @return content with modified URLs
     */
    private def rewriteUrls(def content) {
        Parser tagsoupParser = new Parser()
        tagsoupParser.setFeature(Parser.namespacesFeature, false)
        tagsoupParser.setFeature(Parser.namespacePrefixesFeature, false)
        def result = new XmlSlurper(tagsoupParser).parseText(content)
        result.depthFirst().collect {it}.findAll {it}.each {element ->
            if (element.name() == "a") {
                // URLs need our context path in front
                String href = element.@href.text()
                String pattern = "/content"
                if (href.startsWith(pattern)) {
                    element.@href = configurationService.getContextPath() + href
                }
            }
            else if (element.name() == "img") {
                // image URLs from CMS are absolute URLs
                String src = element.@src.text()
                String pattern = "/sites/default"
                int index = src.indexOf(pattern)
                if (index >= 0) {
                    element.@src = configurationService.getContextPath() + "/static/" +
                            src.substring(index + pattern.length() + 1).toString()
                }
            }
        }
        return XmlUtil.serialize(result)
    }
}
