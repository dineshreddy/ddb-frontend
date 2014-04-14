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
package de.ddb.next.beans

import groovy.transform.ToString
import de.ddb.next.JsonUtil

/**
 * Class for the elastic search mapping of a "folderList" 
 * 
 * @author boz
 */
@ToString(includeNames=true)
class FolderList {

    String folderListId
    String userId
    String title
    String description
    Date creationDate
    Collection folders

    /**
     * Constructor
     * @param folderListId
     * @param userId
     * @param title
     * @param description
     * @param creationDateAsLong
     */
    public FolderList(String folderListId, String userId, String title, def description, def creationDateAsLong, def folders) {
        this.folderListId = folderListId
        this.userId = userId
        this.title = title

        if(JsonUtil.isAnyNull(description)){
            this.description = ""
        }else{
            this.description = description.toString()
        }

        if(JsonUtil.isAnyNull(creationDateAsLong)){
            this.creationDate = new Date()
        }else{
            this.creationDate = new Date(creationDateAsLong)
        }

        if(JsonUtil.isAnyNull(folders)){
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

    /**
     * Return the objects stae as an map
     * @return
     */
    public def getAsMap() {
        def out = [:]
        out["folderListId"] = folderListId
        out["userId"] = userId
        out["title"] = title
        out["description"] = description
        out["creationDate"] = creationDate
        out["folders"] = folders
        return out
    }

    /**
     * Checks whether the object has an valid state. At least an id and a user must be available.
     * @return <code>true</true> if the object has an valid state
     */
    boolean isValid(){
        if(folderListId != null
        && userId != null) {
            return true
        }
        return false
    }
}
