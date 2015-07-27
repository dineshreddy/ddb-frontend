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

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin

import org.junit.*

import de.ddb.common.beans.FolderList


@TestMixin(ControllerUnitTestMixin)
class ListServiceIntegrationTests {

    private static final def SIZE = 99999

    def bookmarksService
    def listsService

    /** The userId is refreshed in setUp() for every test method*/
    def userId = null


    /**
     * Is called before every test method.
     * Creates a new userId for the test. 
     */
    void setUp() {
        println "--------------------------------------------------------------------"
        println "Setup tests"
        userId = UUID.randomUUID() as String
        logStats()
    }

    /**
     * Is called after every test method.
     * Cleanup user content created by a test method
     */
    void tearDown() {
        println "Cleanup tests"

        List<FolderList> lists = listsService.findListsByUserId(userId)
        println "User lists after test: " + lists.size()

        boolean contentDeleted = listsService.deleteAllUserLists(userId)
        println "User content deleted: : " + contentDeleted

        logStats()
        println "--------------------------------------------------------------------"
    }


    def logStats() {
        println "userId " + userId
        println "Index has " + listsService.getListCount() + " lists"
    }


    @Test void shouldCreateNewList() {
        def listTitle= 'List-' + new Date().getTime().toString()

        FolderList newFolderList = new FolderList(
                null,
                listTitle,
                System.currentTimeMillis(),
                userId,
                "[123]")

        def newList = listsService.createList(newFolderList)

        assert newList
    }

    @Test void shouldFindListById() {
        def listTitle= 'List-' + new Date().getTime().toString()

        FolderList newFolderList = new FolderList(
                null,
                listTitle,
                System.currentTimeMillis(),
                userId,
                [
                    "12313",
                    "234356436",
                    "43654"
                ])

        def listId = listsService.createList(newFolderList)
        assert listId != null

        def folderList = listsService.findListById(listId)
        assert folderList != null
        assert folderList.title == listTitle
        assert folderList.folders.size() == 3
        assert folderList.users.contains(userId)
    }

    @Test void shouldReturnListCount() {
        def listTitle= 'List-' + new Date().getTime().toString()

        FolderList newFolderList = new FolderList(
                null,
                listTitle,
                System.currentTimeMillis(),
                userId,
                [
                    "12313",
                    "234356436",
                    "43654"
                ])

        def listId = listsService.createList(newFolderList)

        assert listsService.getListCount() > 0
    }

    @Test void shouldDeleteUserLists() {
        def listTitle= 'List-' + new Date().getTime().toString()

        FolderList newFolderList = new FolderList(
                null,
                listTitle,
                System.currentTimeMillis(),
                userId,
                "[123]")

        assert listsService.createList(newFolderList)

        assert listsService.findListsByUserId(userId).size() == 1

        assert listsService.deleteAllUserLists(userId)

        assert listsService.findListsByUserId(userId).size() == 0
    }
}
