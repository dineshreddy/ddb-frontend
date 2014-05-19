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
import de.ddb.common.constants.FolderConstants
import de.ddb.next.JsonUtil

@ToString(includeNames=true, excludes="count, creationDateFormatted, oldestBookmarkImage")
class Folder {

    String folderId
    String userId
    String title
    String description
    boolean isMainFolder = false
    boolean isPublic = false
    boolean isBlocked = false
    String blockingToken
    List bookmarks
    String publishingName = FolderConstants.PUBLISHING_NAME_USERNAME.value
    Date creationDate
    Date updatedDate

    // these fields are not stored in elastic search and filled via runtime. They are excluded from serialization!
    def count = null
    def creationDateFormatted = null
    def oldestItemMetaData = null



    public Folder(String folderId, String userId, String title, String description, boolean isPublic,
    String publishingName, boolean isBlocked, String blockingToken, List bookmarks, long creationDateAsLong,
    long updateDateAsLong) {
        Date now = new Date()

        this.folderId = folderId
        this.userId = userId
        this.title = title
        if(JsonUtil.isAnyNull(description)){
            this.description = ""
        }else{
            this.description = description.toString()
        }
        if(JsonUtil.isAnyNull(isPublic)){
            this.isPublic = false
        }else{
            this.isPublic = isPublic
        }
        if(publishingName){
            this.publishingName = publishingName
        }
        if(JsonUtil.isAnyNull(isBlocked)){
            this.isBlocked = false
        }else{
            this.isBlocked = isBlocked
        }
        if(JsonUtil.isAnyNull(blockingToken)){
            this.blockingToken = ""
        }else{
            this.blockingToken = blockingToken.toString()
        }
        if (bookmarks) {
            this.bookmarks = bookmarks
        }
        else {
            this.bookmarks = []
        }
        if(JsonUtil.isAnyNull(creationDateAsLong)){
            this.creationDate = now
        }else{
            this.creationDate = new Date(creationDateAsLong)
        }
        if(JsonUtil.isAnyNull(updateDateAsLong)){
            this.updatedDate = now
        }else{
            this.updatedDate = new Date(updateDateAsLong)
        }
    }

    public void addBookmark(String bookmarkId) {
        bookmarks.add(bookmarkId)
    }

    public void deleteBookmark(String bookmarkId) {
        bookmarks.remove(bookmarkId)
    }

    public void moveBookmarkDown(String bookmarkId) {
        int index = bookmarks.indexOf(bookmarkId)
        if (bookmarks.size() > 1 && index < bookmarks.size() - 1) {
            deleteBookmark(bookmarkId)
            bookmarks.add(index + 1, bookmarkId)
        }
    }

    public void moveBookmarkUp(String bookmarkId) {
        int index = bookmarks.indexOf(bookmarkId)
        if (bookmarks.size() > 1 && index > 0) {
            deleteBookmark(bookmarkId)
            bookmarks.add(index - 1, bookmarkId)
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
        out["bookmarks"] = bookmarks
        out["creationDate"] = creationDate
        out["updatedDate"] = updatedDate
        return out
    }

    boolean isValid(){
        if(folderId != null
        && userId != null) {
            return true
        }
        return false
    }
}
