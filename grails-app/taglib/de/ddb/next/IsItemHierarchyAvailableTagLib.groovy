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

import de.ddb.next.beans.Item

class IsItemHierarchyAvailableTagLib {

    static namespace = "ddb"

    def itemService

    /**
     * Checks if a given item-id has a hierarchy. If it has, the body of the tag is rendered. If it has no
     * hierarchy the body of the tag is ignored.
     *
     * @attr item The item-ID (e.g. AYKQ6FKHP6A7KFKCK2K3DP6HCVNZQEQC)
     */
    def isItemHierarchyAvailable = { attrs, body ->
        def itemId = attrs.item

        // Check if the item has parents
        def parentList = itemService.getParent(itemId)

        if(Item.doesParentListContainHierarchy(itemId, parentList)){
            out << body()
        }else{
            out << ""
        }

    }

}
