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

class SearchQueryTerm {
    final String key
    final String value

    public SearchQueryTerm(String termString) {
        def termParts = termString.split('=')

        this.key = termParts[0]
        this.value = termParts[1]? URLDecoder.decode(termParts[1], "UTF-8") : null
    }
}
