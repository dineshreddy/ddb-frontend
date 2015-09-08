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

import org.jsoup.Jsoup

import de.ddb.common.ApiConsumer
import de.ddb.common.exception.ItemNotFoundException

class ContentService {
    def configurationService

    private def fetchAuthor(content) {
        def authorMatch = content =~ /(?s)<meta (.*?)name="author" (.*?)content="(.*?)"(.*?)\/>/
        if (authorMatch)
            return authorMatch[0][3]
    }

    private def fetchBody(content) {
        def bodyMatch = content =~ /(?s)<body\b[^>]*>(.*?)<\/body>/
        return bodyMatch[0][1]
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

    private def fetchTitle(content) {
        def titleMatch = content =~ /(?s)<title\b[^>]*>(.*?)<\/title>/
        if (titleMatch)
            return titleMatch[0][1]
    }

    /**
     * Check if a directory "location/" exists. If not, then check if "location.html" exists.
     * If that is true then redirect to "location" (remove the trailing slash).
     *
     * This is a workaround for a Drupal issue (see DDBNEXT-2052).
     *
     * @param location location string from URL
     * @param locale current locale
     */
    public void checkIfRedirectNeeded(String location, String locale) {
        if (location.endsWith("/")) {
            String url = configurationService.getStaticUrl()
            def apiResponse = ApiConsumer.getText(url, locale + "/" + location, false)

            if (!apiResponse.isOk()) {
                String newLocation = location.substring(0, location.length() - 1)

                apiResponse = ApiConsumer.getText(url, locale + "/" + newLocation + ".html", false)
                if (apiResponse.isOk()) {
                    throw new RedirectException("/" + params.controller + "/" + newLocation)
                }
            }
        }
    }

    public def retrieveArguments(def content) {
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

    /**
     * Retrieve a static file from the web server.
     *
     * First try $locale/$location.html and then $locale/$location/index.html.
     *
     * @param controller name of calling controller
     * @param browserUrl request URL without context path
     * @param location file location
     * @param locale locale
     *
     * @return HTML content
     */
    public def retrieveFile(String controller, String browserUrl, String location, String locale)
    throws RedirectException {
        def result
        def url = configurationService.getStaticUrl()
        def path = locale + "/" + location + ".html"
        def apiResponse = ApiConsumer.getText(url, path, false)
        if (apiResponse.isOk()) {
            result = apiResponse.getResponse()
        }
        else {
            if (!browserUrl.endsWith("/")) {
                throw new RedirectException("/" + controller + "/" + location + "/")
            }
            path = locale + "/" + location + "/index.html"
            apiResponse = ApiConsumer.getText(url, path, false)
            if (!apiResponse.isOk()) {
                throw new ItemNotFoundException()
            }
            result = apiResponse.getResponse()
        }
        return result
    }

    /**
     * Rewrite CMS server URLs so they suit our needs.
     *
     * @param content content with CMS URLs
     * @return content with modified URLs
     */
    private def rewriteUrls(def content) {
        def result = Jsoup.parse(content)
        result.select("a").each {element ->
            // URLs need our context path in front
            String href = element.attr("href")
            String pattern = "/content"
            if (href.startsWith(pattern)) {
                element.attr("href", configurationService.getContextPath() + href)
            }
        }
        result.select("img").each {element ->
            // image URLs from CMS are absolute URLs
            String src = element.attr("src")
            String pattern = "/sites/default"
            int index = src.indexOf(pattern)
            if (index >= 0) {
                element.attr("src", configurationService.getContextPath() + "/static/" +
                        src.substring(index + pattern.length() + 1).toString())
            }
        }
        return result.toString()
    }

    static class RedirectException extends Exception {
        private String uri

        public RedirectException(String uri) {
            this.uri = uri
        }
    }
}
