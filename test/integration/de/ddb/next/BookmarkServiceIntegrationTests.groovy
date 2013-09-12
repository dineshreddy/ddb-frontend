package de.ddb.next

import static org.junit.Assert.*

import org.junit.*




class BookmarkServiceIntegrationTests extends GroovyTestCase {

    private static final def SIZE = 99999

    def bookmarksService

    def userId = 'crh'

    // Folder
    @Test void shouldCreateNewFolder() {
        def folderId = createNewFolder()
        assertNotNull folderId
        log.info "Created a bookmark folder with the ID: ${folderId}"
    }

    def createNewFolder() {
        def folderTitle= 'Favorites-' + new Date().getTime().toString()
        def isPublic = true
        return bookmarksService.newFolder(userId, folderTitle, isPublic)
    }

    @Test void shouldGetAllFolders() {
        createNewFolder()
        def folderList = bookmarksService.findAllFolders(userId)
        assertTrue folderList.size() >0

        if(folderList) {
            log.info "The user with the ID: ${userId} has: "
            folderList.each { it ->
                log.info "- ${it}"
            }
        } else {
            log.info 'empty folder.'
        }
    }

    // Bookmark
    @Test void shouldSaveBookmarkInFolder() {
        def folderId = createNewFolder()
        def itemId = 'foobar'
        def bookmarkId = bookmarksService.saveBookmark(userId, folderId, itemId)

        assertNotNull bookmarkId
        log.info 'bookmark is saved, ID is: ' + bookmarkId
    }

    @Test void shouldFindBookmarksByFolderId() {
        def folderId = createNewFolder()
        def itemId = 'foobarbaz'
        def bookmarkId = bookmarksService.saveBookmark(userId, folderId, itemId)
        def bookmarks = bookmarksService.findBookmarksByFolderId(userId, folderId)
        assert bookmarks.size() > 0
    }

    @Test void shouldFindBookmarkedItems() {
        def folderId = createNewFolder()
        def itemId = 'F2D23TGU7NMP5MGVF647Q63X3E32W4YI'
        def bookmarkId = bookmarksService.saveBookmark(userId, folderId, itemId)

        def foundBookmarkedItems = bookmarksService.findBookmarkedItems(userId, [itemId])
        assert foundBookmarkedItems.size() > 0
    }

    // Favorites
    @Test void shouldAddItemToUserFavorite() {
        log.info "should add item to the user's Favorites"
        // should add a cultural item to user's favorite list.
        def userId = UUID.randomUUID() as String
        def itemId = UUID.randomUUID() as String
        // if the user don't have a favorite list, then the service should create it.
        def favoriteId = bookmarksService.addFavorite(userId, itemId)
        assert favoriteId != null

        log.info "The user ${userId} just added item ${itemId} to their Favorites folder favoriteId"
    }

    @Test void shouldFindFoldersByTitle() {
        log.info "the bookmark service should find folders by its title."
        def userId = UUID.randomUUID() as String

        def folderId = bookmarksService.newFolder(userId, BookmarksService.FAVORITES, BookmarksService.IS_PUBLIC)
        log.info "the bookmark service created a ${BookmarksService.FAVORITES} folder(${folderId}) for a user(${userId})"

        def favFolderList = bookmarksService.findFoldersByTitle(userId, BookmarksService.FAVORITES)
        log.info "The user(${userId}) has ${favFolderList.size()} folders with the title `Favorites`"

        if(!favFolderList) {
            favFolderList = bookmarksService.findFoldersByTitle(userId, BookmarksService.FAVORITES)
            log.info "Second try, the user(${userId}) has ${favFolderList.size()} folders with the title `Favorites`"
        }

        assert favFolderList.size() == 1
        assertEquals favFolderList[0].folderId, folderId
    }

    @Test void shouldGetAllUserFavorites() {
        def userId = UUID.randomUUID() as String
        def firstItemId = 'foobarbaz'
        def secondItemId = UUID.randomUUID() as String

        // if the user don't have a favorite list, then the service should create it.
        def firstFav = bookmarksService.addFavorite(userId, firstItemId)

        def allFavs = bookmarksService.findFavoritesByUserId(userId)
        assert allFavs.size() > 0
    }

    @Test void shouldDeleteFavoritesByItemIDs() {
        log.info "the bookmark service should delete favorites by item IDs."
        def userId = UUID.randomUUID() as String
        def firstItemId = 'foobarbaz'
        log.info "adding item (${firstItemId} to the folder Favorite."

        def firstFav = bookmarksService.addFavorite(userId, firstItemId)

        def allFavs = bookmarksService.findFavoritesByUserId(userId)
        assert allFavs.size() == 1

        def itemIds = [:]
        itemIds.ids = [firstItemId]
        bookmarksService.deleteFavorites(userId, itemIds)

        def emptyFavs = bookmarksService.findFavoritesByUserId(userId)
        assert emptyFavs.size() == 0
    }

    @Test void shouldFindFavoritesByItemIds() {
        log.info "the bookmark service should find favorites by item IDs."
        def userId = UUID.randomUUID() as String

        def firstItemId = 'F2D23TGU7NMP5MGVF647Q63X3E32W4YI'
        log.info "adding item ${firstItemId} to the folder Favorite."
        def firstFavId = bookmarksService.addFavorite(userId, firstItemId)

        def secondItemId = 'U3TWCZVFIHOC6A65ICIC3UIEYRCR2LKL'
        log.info "adding item ${secondItemId} to the folder Favorite."
        def secondFavId= bookmarksService.addFavorite(userId, secondItemId)

        def itemIds = [firstItemId, secondItemId]

        def foundBookmarkedItems = bookmarksService.findFavoritesByItemIds(userId, itemIds)
        assert foundBookmarkedItems.size() > 0
    }

    @Test void shouldNotAddSameItemIdMoreThanOnce() {
        log.info "the bookmark service should _not_ add the same item IDs more than once to favorites."
        def userId = UUID.randomUUID() as String

        def firstItemId = 'F2D23TGU7NMP5MGVF647Q63X3E32W4YI'
        log.info "adding item ${firstItemId} to the folder Favorite."
        def firstFavId = bookmarksService.addFavorite(userId, firstItemId)

        def secondItemId = 'F2D23TGU7NMP5MGVF647Q63X3E32W4YI'
        log.info "adding item ${secondItemId} _again_ to the folder Favorite."
        def secondFavId= bookmarksService.addFavorite(userId, secondItemId)

        assert secondFavId == null
    }

    @Test void shouldFindFavoriteByItemId() {
        log.info "the bookmark service should find favorite by item ID"
        def userId = UUID.randomUUID() as String

        def firstItemId = 'F2D23TGU7NMP5MGVF647Q63X3E32W4YI'
        log.info "adding item ${firstItemId} to the folder Favorite."
        def firstFavId = bookmarksService.addFavorite(userId, firstItemId)

        def favoriteForItem = bookmarksService.findFavoriteByItemId(userId, firstItemId)
        assert favoriteForItem != null
        assert favoriteForItem .itemId == firstItemId
        log.info favoriteForItem
    }

    @Test void shouldReturnMoreThanTenFavorites() {
        log.info "the bookmark service should find more than 10 favorites"
        def userId = UUID.randomUUID() as String

        11.times {
            def itemId = UUID.randomUUID() as String
            def favId = bookmarksService.addFavorite(userId, itemId)
            log.info("Bookmark ${favId} is created." )
        }

        def allFavs = bookmarksService.findFavoritesByUserId(userId)
        log.error('all favorites is more than 10: ' + allFavs.size())
        assert allFavs.size() > 10
    }

    @Test void shouldSaveInstitutionAsFavorites() {
        log.info "should save institution as user's Favorites"
        // should add a cultural item to user's favorite list.
        def userId = UUID.randomUUID() as String
        def institutionId = UUID.randomUUID() as String
        // if the user don't have a favorite list, then the service should create it.
        def favoriteId = bookmarksService.addFavorite(userId, institutionId, Type.INSTITUTION)
        assert favoriteId != null
        log.info "The user ${userId} just added an institution ${institutionId} to their Favorites folder(favoriteId)"

        def favoriteForInstitution = bookmarksService.findFavoriteByItemId(userId, institutionId)
        log.info("fav is: ${favoriteForInstitution }")
        assert favoriteForInstitution.itemId == institutionId
        assert favoriteForInstitution.type == Type.INSTITUTION
    }

    // TODO: problem Favorites VS Bookmarks?
    // @Test void shouldSaveFavoritesWithOptionalTitleOrDescription() { assert false }

    @Test void shouldFindFavoritesByUserIdAndFolderId() { assert false }

    @Test void shouldCreateNewFolderWithDescription() {
        def userId = UUID.randomUUID() as String
        def folderTitle = 'Favorites-' + new Date().getTime().toString()
        def description = 'folder description'
        def isPublic = true
        def folderId = bookmarksService.newFolder(userId, folderTitle, isPublic, description)

        def folders = bookmarksService.findAllFolders(userId)
        assert folders[0].description == description

        log.info "folder's description ${folders[0].description}"
    }

    @Test void shouldUpdateFavorites() {
        log.info "should add item to the user's Favorites"
        // should add a cultural item to user's favorite list.
        def userId = UUID.randomUUID() as String
        def itemId = UUID.randomUUID() as String
        // if the user don't have a favorite list, then the service should create it.

        def folderTitle= 'Favorites-' + new Date().getTime().toString()
        def isPublic = true
        def folderId = bookmarksService.newFolder(userId, folderTitle, isPublic)
        def favoriteId = bookmarksService.addFavorite(userId, itemId, folderId)
        assert favoriteId != null
        log.info "The user ${userId} just added item ${itemId} to their Favorites folder favoriteId"
        bookmarksService.updateFavorites([favoriteId], [folderId])
        assert false
    }

    @Test void shouldFindFavoriteById() {
        def userId = UUID.randomUUID() as String
        def itemId = UUID.randomUUID() as String

        def folderTitle= 'Favorites-' + new Date().getTime().toString()
        def isPublic = true
        def folderId = bookmarksService.newFolder(userId, folderTitle, isPublic)
        def favoriteId = bookmarksService.addFavorite(userId, itemId, folderId)
        assert favoriteId != null
        log.info "The user ${userId} just added item ${itemId} to their Favorites folder favoriteId"

        def favorite = bookmarksService.findFavoriteById(favoriteId)
        assert favorite.bookmarkId == favoriteId
    }
}