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
import de.ddb.next.constants.FacetEnum;
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

    /**
     * Transform the query parameter Map from the SearchService to a backend compatible query Map  
     * 
     * @param queryParameters The map with the original frontend API query parameter
     * 
     * @return query parameter map for the cortex API
     */
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
            
        evaluateFacetParameter(query, queryParameters.time_fct, FacetEnum.TIME.getName())
        
        evaluateFacetParameter(query, queryParameters.place_fct, FacetEnum.PLACE.getName())
        
        evaluateFacetParameter(query, queryParameters.affiliate_fct, FacetEnum.AFFILIATE.getName())
        
        evaluateFacetParameter(query, queryParameters.affiliate_fct_involved, FacetEnum.AFFILIATE_INVOLVED.getName())
        
        evaluateFacetParameter(query, queryParameters.affiliate_fct_subject, FacetEnum.AFFILIATE_SUBJECT.getName())
        
        evaluateFacetParameter(query, queryParameters.affiliate_fct_subject_normdata, FacetEnum.AFFILIATE_SUBJECT_NORMDATA.getName())
        
        evaluateFacetParameter(query, queryParameters.affiliate_fct_involved_normdata, FacetEnum.AFFILIATE_INVOLVED_NORMDATA.getName())
        
        evaluateFacetParameter(query, queryParameters.keywords_fct, FacetEnum.KEYWORDS.getName())

        evaluateFacetParameter(query, queryParameters.language_fct, FacetEnum.LANGUAGE.getName())
               
        evaluateFacetParameter(query, queryParameters.type_fct, FacetEnum.TYPE.getName())
        
        evaluateFacetParameter(query, queryParameters.sector_fct, FacetEnum.SECTOR.getName())
        
        evaluateFacetParameter(query, queryParameters.provider_fct, FacetEnum.PROVIDER.getName())

        if(queryParameters.grid_preview){
            query["grid_preview"]=queryParameters.grid_preview
        }

        if(queryParameters["facet.limit"]){
            query["facet.limit"] = queryParameters["facet.limit"]
        }

        return query
    }
    
    /**
     * 
     * @param query
     * @param queryParameter
     * @param facetName
     * @return
     */
    private def evaluateFacetParameter(Map query, Object queryParameter, String facetName) {
        if(queryParameter){
            if(queryParameter.getClass().isArray()){
                query[facetName] = []
                queryParameter.each {
                    query[facetName].add(it)
                }
            }else { 
                query[facetName]=queryParameter
            }
        }
    }
    
}
