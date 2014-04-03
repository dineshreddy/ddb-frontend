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
import de.ddb.common.exception.ItemNotFoundException
import de.ddb.next.constants.SupportedLocales

class ContentController {
    static defaultAction = "staticcontent"

    def configurationService

    def staticcontent(){
        try{
            def firstLvl = "news"
            if (params.dir!=null){
                firstLvl=getFirstLvl()
            }

            def browserUrl = request.forwardURI.substring(request.contextPath.size())

            if(browserUrl.endsWith("content")){
                redirect uri: browserUrl+"/"
                return
            }
            if(browserUrl?.endsWith(firstLvl)){
                redirect uri: browserUrl+"/"
                return
            }

            if(!params.dir){
                redirect uri: browserUrl + firstLvl + "/"
                return
            }else{
                firstLvl = getFirstLvl()
            }

            def prioritySortedLocales = SupportedLocales.getSupportedLocalesByPriority(RCU.getLocale(request))
            def response

            for(int i=0; i < prioritySortedLocales.size(); i++){
                def it = prioritySortedLocales.get(i)

                def secondLvl = getSecLvl()
                def url = configurationService.getStaticUrl()
                def lang = it.getISO2()
                def path = "/static/"+lang+"/"+firstLvl+"/index.html"
                if (params.id!=null){
                    path = "/static/"+lang+"/"+firstLvl+"/"+secondLvl+".html"
                }

                def apiResponse = ApiConsumer.getText(url, path, false)
                if(apiResponse.isOk()){
                    response = apiResponse.getResponse()
                    break
                }
                else if (i == prioritySortedLocales.size()-1) {
                    //A 404 was returned for EVERY supported language
                    throw new ItemNotFoundException()
                }
            }

            def map = retrieveArguments(response)
            render(view: "staticcontent", model: map)
        } catch(ItemNotFoundException infe){
            log.error "staticcontent(): Request for nonexisting item with id: '" + params?.dir + "'. Going 404..."
            forward controller: "error", action: "itemNotFound"
        }
    }

    private def getFirstLvl(){
        String firstLvl = cleanHtml(params.dir, 'none')
        return firstLvl
    }

    private String getSecLvl(){
        if (params.id==null){
            return null
        }
        return cleanHtml(params.id, 'none')
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
            content:body
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
}
