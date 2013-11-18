package ddb.next.controllers

import grails.test.mixin.TestFor
import de.ddb.next.FavoritesController
import de.ddb.next.beans.Folder
import de.ddb.next.constants.FolderConstants

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(FavoritesController)
class FavoritesControllerTests {

    void testSortFolders_WithEmptyList() {
        List sortedFolders = controller.sortFolders([])
        assert sortedFolders.size() == 0
    }

    void testSortFolders_WithOneFolder() {
        def folder1 = createFolder("first folder")

        assert controller.sortFolders([folder1]) == [folder1]
    }

    void testSortFolders_WithEmptyFolderTitle_GetsRenamed() {
        def folder1 = createFolder(" \t\r\n")

        assert controller.sortFolders([folder1])[0].folder.title == "-"
    }

    void testSortFolders_WithTwoFoldersInWrongOrder_GetSortedInCorrectOrder() {
        def folder1 = createFolder("first folder")
        def folder2 = createFolder("second folder")

        assert controller.sortFolders([folder2, folder1]) == [folder1, folder2]
    }

    void testSortFolders_IncludingMainFolder_PullsMainFolderToTheBeginning() {
        def folder1 = createFolder(FolderConstants.MAIN_BOOKMARKS_FOLDER.value)
        def folder2 = createFolder("aaaaa first normal folder")

        assert controller.sortFolders([folder2, folder1]) == [folder1, folder2]
    }

    void testSortFolders_IncludingUmlautFolder() {
        def folder1 = createFolder("Ägypten")
        def folder2 = createFolder("zzzz last folder")

        assert controller.sortFolders([folder2, folder1]) == [folder1, folder2]
    }

    private def createFolder(String title) {
        def folder = [:]
        folder["folder"] = new Folder("folder id", "user", title, null, false, null)
        folder["count"] = 0
        return folder
    }
}
