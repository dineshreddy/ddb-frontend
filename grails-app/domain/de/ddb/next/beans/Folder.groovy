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
class Folder {

    String folderId
    String userId
    String title
    String description
    boolean isPublic = false

    public Folder(String folderId, String userId, String title, String description, boolean isPublic) {
        this.folderId = folderId
        this.userId = userId
        this.title = title
        this.description= description
        this.isPublic = isPublic
    }
}
