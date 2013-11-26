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

import org.codehaus.groovy.runtime.NullObject

import net.sf.json.JSONNull
import groovy.transform.ToString

import de.ddb.next.constants.Type;

@ToString(includeNames=true)
class Bookmark {

    String bookmarkId
    String userId
    String itemId
    String description
    Date creationDate
    Date updateDate
    Type type
    Collection folders

    public Bookmark(String bookmarkId, String userId, String itemId, Long creationDateAsLong, Type type, def folders, def description, Long updateDateAsLong) {
        this.bookmarkId = bookmarkId
        this.userId = userId
        this.itemId = itemId
        if(description == null || description instanceof JSONNull || description instanceof NullObject){
            this.description = ""
        }else{
            this.description = description.toString()
        }
        if(creationDateAsLong == null || creationDateAsLong instanceof JSONNull || creationDateAsLong instanceof NullObject){
            this.creationDate = new Date()
        }else{
            this.creationDate = new Date(creationDateAsLong)
        }
        if(updateDateAsLong == null || updateDateAsLong instanceof JSONNull || updateDateAsLong instanceof NullObject){
            this.updateDate = new Date()
        }else{
            this.updateDate = new Date(updateDateAsLong)
        }
        this.type = type
        if(folders instanceof net.sf.json.JSONNull){
            this.folders = null
        }else if(folders instanceof String){
            String folderString = folders.toString()
            if(folderString.startsWith("[")){
                folderString = folderString.substring(1,folderString.length()-1)
                this.folders = Arrays.asList(folderString.split(","))
            }else{
                this.folders = [folders]
            }
        }else{
            this.folders = folders
        }
    }

    public def getAsMap() {
        def out = [:]
        out["bookmarkId"] = bookmarkId
        out["userId"] = userId
        out["itemId"] = itemId
        out["description"] = description
        out["creationDate"] = creationDate
        out["updateDate"] = updateDate
        out["type"] = type
        out["folders"] = folders
        return out
    }

    boolean isValid() {
        if(bookmarkId != null
        && userId != null
        && itemId != null
        && creationDate != null
        && folders != null
        && updateDate != null){
            return true
        }
        return false
    }
}
