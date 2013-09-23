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
package de.ddb.next.beans

import groovy.transform.ToString

@ToString(includeNames=true)

class SavedSearch {
    String id
    String label
    String queryString
    Date creationDate
    final Map<String, Collection<SearchQueryTerm>> queryMap

    public SavedSearch(String id, String label, String queryString, Date creationDate) {
        this.id = id
        this.label = label
        this.queryString = queryString
        this.creationDate = creationDate
        queryMap = toMap(queryString)
    }

    /**
     * Return the query string as collection
     *
     * @return query string as collection
     */
    public def String getQuery() {
        def result

        queryMap.each {
            if (it.key == "query") {
                result = it.value[0].name
            }
        }
        return result
    }

    /**
     * parse query=something&facetValues[]=affiliate_fct:goethe&facetValues[]=affiliate_fct:gerig&facetValues[]=type_fct:mediatype_002
     */
    private def Map<String, SearchQueryTerm> toMap(String queryString) {
        def result = [:]

        // add empty list elements to get the correct order of the facet values
        result.put("facetValues[]", [
            new SearchQueryTerm("time_fct"),
            new SearchQueryTerm("place_fct"),
            new SearchQueryTerm("affiliate_fct"),
            new SearchQueryTerm("keywords_fct"),
            new SearchQueryTerm("language_fct"),
            new SearchQueryTerm("type_fct"),
            new SearchQueryTerm("sector_fct"),
            new SearchQueryTerm("provider_fct")
        ])

        queryString.split('&').each {
            def parameter = it.split('=')
            def parameterName = URLDecoder.decode(parameter[0], "UTF-8")

            if (parameter.size() > 1) {
                def parameterValue = URLDecoder.decode(parameter[1], "UTF-8")
                def term = new SearchQueryTerm(parameterValue)
                def oldTerms = result.get(parameterName)

                if (oldTerms) {
                    def termFound = false

                    oldTerms.each {
                        if (term.name == it.name) {
                            it.values.add(term.values[0])
                            termFound = true
                        }
                    }
                    if (!termFound) {
                        oldTerms.add(term)
                    }
                }
                else {
                    result.put(parameterName, [term])
                }
            }
        }
        return result
    }
}
