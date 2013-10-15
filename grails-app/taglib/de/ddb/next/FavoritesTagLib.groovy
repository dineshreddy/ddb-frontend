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

class FavoritesTagLib {
    /**
     * Renders the favorites results list.
     *
     * @attrs items REQUIRED data for paginator
     */

    def favoritesResultsRender = { attrs, body ->
        out << render(template:"/user/resultsList", model:[results: attrs.results, confBinary: request.getContextPath(), publicView: false])
    }

    def publicFavoritesResultsRender = { attrs, body ->
        out << render(template:"/user/resultsList", model:[results: attrs.results, confBinary: request.getContextPath(), publicView: true])
    }

    def favoritesEmailResultsRender = { attrs, body ->
        out << render(template:"/user/favoritesemailList", model:[results: attrs.results, userName: attrs.userName,confBinary: request.getContextPath()])
    }
}
