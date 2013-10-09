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

import de.ddb.next.Type

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

    public Bookmark(String bookmarkId, String userId, String itemId, Date creationDate, Type type, def folders, def description, def updateDate) {
        this.bookmarkId = bookmarkId
        this.userId = userId
        this.itemId = itemId
        if(description == null || description instanceof JSONNull || description instanceof NullObject){
            this.description = ""
        }else{
            this.description = description.toString()
        }
        this.creationDate = creationDate
        if(updateDate == null || updateDate instanceof JSONNull || updateDate instanceof NullObject){
            this.updateDate = creationDate
        }else{
            this.updateDate = new Date(updateDate.toLong())
        }
        this.type = type
        if(folders instanceof String){
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
}
