package ddb.next.controllers

import grails.test.mixin.TestFor
import de.ddb.common.constants.FolderConstants
import de.ddb.common.FavoritesController
import de.ddb.next.FavoritesService
import de.ddb.common.beans.Folder

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(FavoritesController)
class FavoritesControllerTests {

    private FavoritesService favoritesService = new FavoritesService()

    void testSortFolders_WithEmptyList() {
        List sortedFolders = favoritesService.sortFolders([])
        assert sortedFolders.size() == 0
    }

    void testSortFolders_WithOneFolder() {
        def folder1 = createFolder("first folder")

        assert favoritesService.sortFolders([folder1]) == [folder1]
    }

    void testSortFolders_WithEmptyFolderTitle_GetsRenamed() {
        def folder1 = createFolder(" \t\r\n")

        assert favoritesService.sortFolders([folder1])[0].title == "-"
    }

    void testSortFolders_WithTwoFoldersInWrongOrder_GetSortedInCorrectOrder() {
        def folder1 = createFolder("first folder")
        def folder2 = createFolder("second folder")

        assert favoritesService.sortFolders([folder2, folder1]) == [folder1, folder2]
    }

    void testSortFolders_IncludingMainFolder_PullsMainFolderToTheBeginning() {
        def folder1 = createFolder(FolderConstants.MAIN_BOOKMARKS_FOLDER.value)
        def folder2 = createFolder("aaaaa first normal folder")

        assert favoritesService.sortFolders([folder2, folder1]) == [folder1, folder2]
    }

    void testSortFolders_IncludingUmlautFolder() {
        def folder1 = createFolder("Ägypten")
        def folder2 = createFolder("zzzz last folder")

        assert favoritesService.sortFolders([folder2, folder1]) == [folder1, folder2]
    }

    private def createFolder(String title) {
        def now = System.currentTimeMillis()
        return new Folder("folder id", "user", title, null, false, null, false, null, null, now, now)
    }
}
