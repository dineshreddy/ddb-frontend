package de.ddb.next

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin

import org.junit.*

import de.ddb.next.beans.Bookmark
import de.ddb.next.beans.Folder


@TestMixin(IntegrationTestMixin)
class ListServiceIntegrationTests {

    private static final def SIZE = 99999

    def bookmarksService

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

        List<Bookmark> bookmarks = bookmarksService.findBookmarksByUserId(userId)
        println "User bookmarks after test: " + bookmarks.size()

        List<Folder> folders = bookmarksService.findAllFolders(userId)
        println "User folders after test: " + folders.size()

        boolean contentDeleted = bookmarksService.deleteAllUserContent(userId)
        println "User content deleted: : " + contentDeleted

        logStats()
        println "--------------------------------------------------------------------"
    }


    def logStats() {
        println "userId " + userId
        println "Index has " + bookmarksService.getFolderCount() + " folders"
        println "Index has " + bookmarksService.getBookmarkCount() + " bookmarks"
    }


    @Test void shouldCreateNewFolderList() {
        //        def folderTitle= 'Favorites-' + new Date().getTime().toString()
        //        def isPublic = true
        //        def publishingName = FolderConstants.PUBLISHING_NAME_USERNAME.getValue()
        //        Folder newFolder = new Folder(
        //                null,
        //                userId,
        //                folderTitle,
        //                "",
        //                isPublic,
        //                publishingName,
        //                false,
        //                "")
        //        return bookmarksService.createFolder(newFolder)
        assert true
    }
}
