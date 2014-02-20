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


class GetTruncatedItemTitleTagLib {

    static namespace = "ddb"

    def searchService

    def getTruncatedItemTitle = { attrs, body ->
        /**
         * arb: This TagLib is "coupled" with the ItemTitles. 
         * They need to be highlighted therefore we leave the strong parameter on
         * Other tags are removed (e.x: title) 
         */
        def allowedTags="strong"
        if(attrs.allowedTags) {
            allowedTags =attrs.allowedTags 
        }
        out << ddb.stripTags(text:searchService.trimTitle(attrs.title.toString(), attrs.length).toString(),allowedTags:allowedTags)
    }
}
