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

class SearchQuery {
    final String queryString
    final Collection<String> queryMap

    public SearchQuery(String queryString) {
        this.queryString = queryString
        this.queryMap = queryString.split('&').collect {new QueryTerm(it)}
    }

    /**
     * Return the value of the parameter "query"
     *
     * @return value for "query"
     */
    public String getQuery() {
        def result

        queryMap.each {
            if (it.key == "query") {
                result = it.value
            }
        }
        return result
    }

    @ToString(includeNames=true)

    class QueryTerm implements Serializable {
        final String key
        final String value

        public QueryTerm (String termString) {
            def termParts = termString.split('=')

            this.key = termParts[0]
            this.value = termParts[1]? URLDecoder.decode(termParts[1], "UTF-8") : null
        }
    }
}
