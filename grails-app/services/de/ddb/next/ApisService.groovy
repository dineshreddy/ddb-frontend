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

import static groovyx.net.http.ContentType.*
import groovy.json.*

/**
 * Set of services used in the ApisController for views/search
 *
 * @author ema
 *
 */

class ApisService {

    //Autowire the grails application bean
    def grailsApplication
    def configurationService

    def transactional=false

    def getQueryParameters(Map queryParameters){

        def query = [ query: queryParameters.query ]

        if(queryParameters.offset)
            query["offset"]= queryParameters.offset

        if(queryParameters.rows)
            query["rows"] = queryParameters.rows

		if(queryParameters.callback)
			query["callback"] = queryParameters.callback

        if(queryParameters.facet){
            if(queryParameters.facet.getClass().isArray()){
                query["facet"] = []
                queryParameters.facet.each {
                    query["facet"].add(it)
                }
            }else query["facet"]=queryParameters.facet
        }
        if(queryParameters.minDocs)
            query["minDocs"] = queryParameters.minDocs

        if(queryParameters.sort)
            query["sort"] = queryParameters.sort
        if(queryParameters.time_fct){
            if(queryParameters.time_fct.getClass().isArray()){
                query["time_fct"] = []
                queryParameters.time_fct.each {
                    query["time_fct"].add(it)
                }
            }else query["time_fct"]=queryParameters.time_fct
        }

        if(queryParameters.place_fct){
            if(queryParameters.place_fct.getClass().isArray()){
                query["place_fct"] = []
                queryParameters.place_fct.each {
                    query["place_fct"].add(it)
                }
            }else query["place_fct"]=queryParameters.place_fct
        }

        if(queryParameters.affiliate_fct){
            if(queryParameters.affiliate_fct.getClass().isArray()){
                query["affiliate_fct"] = []
                queryParameters.affiliate_fct.each {
                    query["affiliate_fct"].add(it)
                }
            }else query["affiliate_fct"]=queryParameters.affiliate_fct
        }

        if(queryParameters.keywords_fct){
            if(queryParameters.keywords_fct.getClass().isArray()){
                query["keywords_fct"] = []
                queryParameters.keywords_fct.each {
                    query["keywords_fct"].add(it)
                }
            }else query["keywords_fct"]=queryParameters.keywords_fct
        }


        if(queryParameters.language_fct){
            if(queryParameters.language_fct.getClass().isArray()){
                query["language_fct"] = []
                queryParameters.language_fct.each {
                    query["language_fct"].add(it)
                }
            }else query["language_fct"]=queryParameters.language_fct
        }

        if(queryParameters.type_fct){
            if(queryParameters.type_fct.getClass().isArray()){
                query["type_fct"] = []
                queryParameters.type_fct.each {
                    query["type_fct"].add(it)
                }
            }else query["type_fct"]=queryParameters.type_fct
        }

        if(queryParameters.sector_fct){
            if(queryParameters.sector_fct.getClass().isArray()){
                query["sector_fct"] = []
                queryParameters.sector_fct.each {
                    query["sector_fct"].add(it)
                }
            }else query["sector_fct"]=queryParameters.sector_fct
        }

        if(queryParameters.provider_fct){
            if(queryParameters.provider_fct.getClass().isArray()){
                query["provider_fct"] = []
                queryParameters.provider_fct.each {
                    query["provider_fct"].add(it)
                }
            }else query["provider_fct"]=queryParameters.provider_fct
        }

        if(queryParameters.grid_preview){
            query["grid_preview"]=queryParameters.grid_preview
        }

        if(queryParameters["facet.limit"]){
            query["facet.limit"] = queryParameters["facet.limit"]
        }

        return query
    }
}
