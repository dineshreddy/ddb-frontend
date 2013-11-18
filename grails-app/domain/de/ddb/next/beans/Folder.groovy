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

import de.ddb.next.constants.FolderConstants

import net.sf.json.JSONNull
import grails.converters.JSON
import groovy.transform.ToString

@ToString(includeNames=true)
class Folder {

    String folderId
    String userId
    String title
    String description
    boolean isMainFolder = false
    boolean isPublic = false
    boolean isBlocked = false
    String blockingToken
    String publishingName = FolderConstants.PUBLISHING_NAME_USERNAME.value

    public Folder(String folderId, String userId, String title, def description, boolean isPublic, String publishingName, def isBlocked, def blockingToken) {
        this.folderId = folderId
        this.userId = userId
        this.title = title
        if(isAnyNull(description)){
            this.description = ""
        }else{
            this.description = description.toString()
        }
        this.isPublic = isPublic
        if(publishingName){
            this.publishingName = publishingName
        }
        if(isAnyNull(isBlocked)){
            this.isBlocked = false
        }else{
            this.isBlocked = isBlocked
        }
        if(isAnyNull(blockingToken)){
            this.blockingToken = ""
        }else{
            this.blockingToken = blockingToken.toString()
        }
    }

    public def getAsMap() {
        def out = [:]
        out["folderId"] = folderId
        out["userId"] = userId
        out["title"] = title
        out["description"] = description
        out["isMainFolder"] = isMainFolder
        out["isPublic"] = isPublic
        out["publishingName"] = publishingName
        out["isBlocked"] = isBlocked
        out["blockingToken"] = blockingToken
        return out
    }

    boolean isValid(){
        if(folderId != null
        && userId != null) {
            return true
        }
        return false
    }

    private boolean isAnyNull(def variable){
        if(variable == null || variable instanceof JSONNull || variable instanceof NullObject){
            return true
        }else{
            false
        }
    }
}
