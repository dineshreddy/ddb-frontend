package de.ddb.next

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.constants.SupportedLocales
import de.ddb.next.beans.Folder
import de.ddb.next.exception.FavoritelistNotFoundException


/**
 * AJAX requests should be handled by FavoritesController
 * 
 * @author boz
 */
class FavoritesviewController {
    private static final String ORDER_ASC = "asc"
    private static final String ORDER_DESC = "desc"

    private static final String ORDER_BY_DATE = "date"
    private static final String ORDER_BY_NUMBER = "number"
    private static final String ORDER_BY_TITLE = "title"

    def aasService
    def bookmarksService
    def favoritesService
    def configurationService
    def commonConfigurationService
    def searchService
    def sessionService
    def userService

    def publicFavorites() {
        final def ACTION = "publicFavorites"
        def rows=20 //default
        if (params.rows){
            rows = params.rows.toInteger()
        }
        def offset = 0 // default
        if(params.offset){
            offset = params.offset.toInteger()
        }
        def folderId = params.folderId
        def by = params.by
        def order = params.order
        def user = aasService.getPersonAsAdmin(params.userId)

        // A user want to report this list to DDB
        if(params.report){
            reportFavoritesList(user.id, folderId)
            redirect(action: ACTION, params: [userId: user.id, folderId: folderId])
            return
        }

        if(params.blockingToken) {
            blockFavoritesList(user.id, folderId, params.blockingToken)
            redirect(action: ACTION, params: [userId: user.id, folderId: folderId])
            return
        }

        if(params.unblockingToken) {
            unblockFavoritesList(user.id, folderId, params.unblockingToken)
            redirect(action: ACTION, params: [userId: user.id, folderId: folderId])
            return
        }

        def selectedFolder = bookmarksService.findPublicFolderById(folderId)

        // If the folder does not exist (maybe deleted) or the user does not exist -> 404
        if(selectedFolder == null || user == null){
            throw new FavoritelistNotFoundException("publicFavorites(): favorites list or user do not exist")
        }

        List publicFolders = bookmarksService.findAllPublicFolders(user.getId())
        publicFolders.sort{ a, b ->
            a.title <=> b.title
        }

        List items = bookmarksService.findBookmarksByPublicFolderId(folderId)

        def totalResults= items.size()

        def lastPgOffset=0


        if (totalResults <1){

            def fullPublicLink = g.createLink(action: ACTION, params: [userId: user.getId(), folderId: folderId])

            render(view: ACTION, model: [
                selectedFolder: selectedFolder,
                resultsNumber: totalResults,
                selectedUser: user,
                publicFolders: publicFolders,
                dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                createAllFavoritesLink:favoritesService.createAllPublicFavoritesLink(0,0,ORDER_DESC,"title",0, user.id, selectedFolder.folderId),
                fullPublicLink: fullPublicLink,
                baseUrl: commonConfigurationService.getSelfBaseUrl(),
                contextUrl: commonConfigurationService.getContextUrl()
            ])
            return
        }else{
            def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
            def allRes = favoritesService.retriveItemMD(items,locale)
            def resultsItems

            def urlQuery = searchService.convertQueryParametersToSearchParameters(params)

            // convertQueryParametersToSearchParameters modifies params
            params.remove("query")

            urlQuery["offset"] = 0
            //Calculating results pagination (previous page, next page, first page, and last page)
            def page = ((offset/urlQuery["rows"].toInteger())+1).toString()
            def totalPages = (Math.ceil(items.size()/urlQuery["rows"].toInteger()).toInteger())
            lastPgOffset=((Math.ceil(items.size()/rows)*rows)-rows).toInteger()

            if (totalPages.toFloat()<page.toFloat()){
                offset= (Math.ceil((items.size()-rows)/10)*10).toInteger()
                if ((Math.ceil((items.size()-rows)/10)*10).toInteger()<0){
                    lastPgOffset=20
                }
                page=totalPages
            }
            def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
            def numberOfResultsFormatted = String.format(locale, "%,d", allRes.size().toInteger())

            def allResultsWithAdditionalInfo = favoritesService.addBookmarkToFavResults(allRes, items, locale)
            allResultsWithAdditionalInfo = favoritesService.addFolderToFavResults(allResultsWithAdditionalInfo, selectedFolder)
            allResultsWithAdditionalInfo = favoritesService.addCurrentUserToFavResults(allResultsWithAdditionalInfo, user)

            def orderLinks = createOrderLinks([
                action : ACTION,
                userId : user.id,
                folderId : selectedFolder.folderId,
                rows : rows,
                offset : offset
            ])

            def orderedFavorites = orderFavorites(allResultsWithAdditionalInfo, selectedFolder.folderId, order, by)
            if (offset != 0){
                resultsItems=orderedFavorites.drop(offset)
                resultsItems=resultsItems.take(rows)
            }else{
                resultsItems=orderedFavorites.take(rows)
            }

            if (request.method=="POST"){
                sendBookmarkPerMail(params.email,allResultsWithAdditionalInfo)
            }
            def fullPublicLink = g.createLink(action: ACTION, params: [userId: user.getId(), folderId: folderId])

            render(view: ACTION, model: [
                results: resultsItems,
                selectedFolder: selectedFolder,
                mainFavoriteFolder: null,
                allResultsOrdered: allResultsWithAdditionalInfo,
                viewType: urlQuery["viewType"],
                resultsPaginatorOptions: resultsPaginatorOptions,
                page: page,
                resultsNumber: totalResults,
                createAllFavoritesLink: favoritesService.createAllPublicFavoritesLink(offset, rows, order, by, lastPgOffset, user.id, selectedFolder.folderId),
                totalPages: totalPages,
                numberOfResultsFormatted: numberOfResultsFormatted,
                offset: offset,
                rows: rows,
                selectedUser: user,
                publicFolders: publicFolders,
                dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                urlsForOrderDate: orderLinks.urlsForOrderDate,
                urlsForOrderNumber: orderLinks.urlsForOrderNumber,
                urlsForOrderTitle: orderLinks.urlsForOrderTitle,
                fullPublicLink: fullPublicLink,
                baseUrl: commonConfigurationService.getSelfBaseUrl(),
                contextUrl: commonConfigurationService.getContextUrl()
            ])
        }

    }

    def favorites(){
        if(userService.isUserLoggedIn()){
            final def ACTION = "favorites"
            def rows=20 //default
            if (params.rows){
                rows = params.rows.toInteger()
            }
            def offset = 0 // default
            if(params.offset){
                offset = params.offset.toInteger()
            }
            def user = userService.getUserFromSession()
            def mainFavoriteFolder = bookmarksService.findMainBookmarksFolder(user.getId())

            def folderId = mainFavoriteFolder.folderId
            if(params.id){
                folderId = params.id
            }
            def by = params.by
            def order = params.order

            Folder selectedFolder = bookmarksService.findFolderById(folderId)
            List items = bookmarksService.findBookmarksByFolderId(user.getId(), folderId)

            // If the folder does not exist (maybe deleted) -> redirect to main favorites folder
            if(selectedFolder == null){
                redirect(action: ACTION, id: mainFavoriteFolder.folderId)
                return
            }

            def totalResults= items.size()

            def userName = user.getFirstnameAndLastnameOrNickname()
            def nickName = user.getUsername()
            def fullName = null
            if(user.getFirstname() || user.getLastname()){
                fullName = user.getFirstname() + " " + user.getLastname()
            }
            def lastPgOffset=0

            def allFoldersInformation = []
            def allFolders = favoritesService.getAllFoldersPerUser(user)
            allFolders.each {
                def container = [:]
                List favoritesOfFolder = bookmarksService.findBookmarksByFolderId(user.getId(), it.folderId)
                container["folder"] = it
                container["count"] = favoritesOfFolder.size()
                allFoldersInformation.add(container)
            }
            allFoldersInformation = favoritesService.sortFolders(allFoldersInformation) { o -> o.folder }

            def fullPublicLink = g.createLink(action: ACTION, params: [userId: user.getId(), folderId: folderId])

            if (totalResults <1){
                render(view: ACTION, model: [
                    selectedFolder: selectedFolder,
                    mainFavoriteFolder: mainFavoriteFolder,
                    resultsNumber: totalResults,
                    allFolders: allFoldersInformation,
                    userName: userName,
                    fullName: fullName,
                    nickName: nickName,
                    fullPublicLink: fullPublicLink,
                    dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                    baseUrl: commonConfigurationService.getSelfBaseUrl(),
                    createAllFavoritesLink:favoritesService.createAllFavoritesLink(0,0,ORDER_DESC,"title",0,folderId),
                ])
                return
            }else{
                def locale = favoritesService.getLocale()
                def allRes = favoritesService.retriveItemMD(items,locale)
                def resultsItems
                def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
                def queryString = request.getQueryString()

                // convertQueryParametersToSearchParameters modifies params
                params.remove("query")

                //urlQuery["offset"] = 0
                //Calculating results pagination (previous page, next page, first page, and last page)
                def page = ((offset/urlQuery["rows"].toInteger())+1).toString()
                def totalPages = (Math.ceil(items.size()/urlQuery["rows"].toInteger()).toInteger())
                lastPgOffset=((Math.ceil(items.size()/rows)*rows)-rows).toInteger()

                if (totalPages.toFloat()<page.toFloat()){
                    offset= (Math.ceil((items.size()-rows)/10)*10).toInteger()
                    if ((Math.ceil((items.size()-rows)/10)*10).toInteger()<0){
                        lastPgOffset=20
                    }
                    page=totalPages
                }
                def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
                def numberOfResultsFormatted = String.format(locale, "%,d", allRes.size().toInteger())
                def allResultsWithAdditionalInfo = favoritesService.addBookmarkToFavResults(allRes, items, locale)
                allResultsWithAdditionalInfo = favoritesService.addFolderToFavResults(allResultsWithAdditionalInfo, selectedFolder)
                allResultsWithAdditionalInfo = favoritesService.addCurrentUserToFavResults(allResultsWithAdditionalInfo, user)

                def orderLinks = createOrderLinks([
                    action : ACTION,
                    userId : user.id,
                    folderId : selectedFolder.folderId,
                    rows : rows,
                    offset : offset
                ])

                def orderedFavorites = orderFavorites(allResultsWithAdditionalInfo, selectedFolder.folderId, order, by)
                if (offset != 0){
                    resultsItems=orderedFavorites.drop(offset)
                    resultsItems=resultsItems.take( rows)
                }else{
                    resultsItems=orderedFavorites.take( rows)
                }

                if (request.method=="POST"){
                    sendBookmarkPerMail(params.email,allResultsWithAdditionalInfo, selectedFolder)
                }
                render(view: ACTION, model: [
                    results: resultsItems,
                    selectedFolder: selectedFolder,
                    mainFavoriteFolder: mainFavoriteFolder,
                    allResultsOrdered: allResultsWithAdditionalInfo,
                    allFolders: allFoldersInformation,
                    viewType: urlQuery["viewType"],
                    resultsPaginatorOptions: resultsPaginatorOptions,
                    paginationURL: searchService.buildPagination(totalResults, urlQuery, request.forwardURI+'?'+queryString),
                    page: page,
                    resultsNumber: totalResults,
                    createAllFavoritesLink:favoritesService.createAllFavoritesLink(offset, rows, order, by, lastPgOffset,folderId),
                    totalPages: totalPages,
                    numberOfResultsFormatted: numberOfResultsFormatted,
                    offset: offset,
                    rows: rows,
                    userName: userName,
                    fullName: fullName,
                    nickName: nickName,
                    fullPublicLink: fullPublicLink,
                    dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                    urlsForOrderDate:orderLinks.urlsForOrderDate,
                    urlsForOrderNumber:orderLinks.urlsForOrderNumber,
                    urlsForOrderTitle:orderLinks.urlsForOrderTitle,
                    baseUrl: commonConfigurationService.getSelfBaseUrl(),
                    contextUrl: commonConfigurationService.getContextUrl()
                ])
            }
        } else{
            redirect(controller:"user", action:"index", params: [referrer: grailsApplication.mainContext.getBean('de.ddb.common.GetCurrentUrlTagLib').getCurrentUrl()])
        }
    }

    private def createOrderLinks(def parameters) {
        def urlsForOrderDate = [:]
        def urlsForOrderNumber = [:]
        def urlsForOrderTitle = [:]
        for (order in [ORDER_ASC, ORDER_DESC]) {
            urlsForOrderDate[order] = g.createLink(
                    action : parameters.action,
                    params : [
                        offset : parameters.offset,
                        rows : parameters.rows,
                        order : order,
                        by : ORDER_BY_DATE,
                        userId : parameters.userId,
                        folderId : parameters.folderId
                    ]
                    )
            urlsForOrderNumber[order] = g.createLink(
                    action : parameters.action,
                    params : [
                        offset : parameters.offset,
                        rows : parameters.rows,
                        order : order,
                        by : ORDER_BY_NUMBER,
                        userId : parameters.userId,
                        folderId : parameters.folderId
                    ]
                    )
            urlsForOrderTitle[order] = g.createLink(
                    action : parameters.action,
                    params : [
                        offset : parameters.offset,
                        rows : parameters.rows,
                        order : order,
                        by : ORDER_BY_TITLE,
                        userId : parameters.userId,
                        folderId : parameters.folderId
                    ]
                    )
        }
        return [
            urlsForOrderDate : urlsForOrderDate,
            urlsForOrderNumber : urlsForOrderNumber,
            urlsForOrderTitle : urlsForOrderTitle]
    }

    private def orderFavoritesByNumber(def favorites, String folderId, String order) {
        def result = []

        // first use bookmark list in folder to order the favorites
        Folder folder = bookmarksService.findFolderById(folderId)
        def bookmarkIdsInFolder = folder?.bookmarks
        bookmarkIdsInFolder.each {bookmarkIdInFolder ->
            def favorite = favorites.find {it.bookmark.bookmarkId == bookmarkIdInFolder}
            if (favorite) {
                favorites.remove(favorite)
                result.add(favorite)
            }
        }

        // second add all favorites which are not present in bookmark list of the folder at the end
        result.addAll(favorites)

        // add orderNumber to all favorites
        result.eachWithIndex {favorite, index ->
            favorite.orderNumber = index
        }

        // update bookmark list in folder with the current list
        if (folder) {
            bookmarkIdsInFolder = result*.bookmark.bookmarkId
            folder.bookmarks = bookmarkIdsInFolder
            bookmarksService.updateFolder(folder)
        }

        if (order == ORDER_DESC) {
            result = result.reverse()
        }

        return result
    }

    private def orderFavorites(def favorites, String folderId, String order, String by) {
        // order by number to get the "orderNumber" property filled out
        def result = orderFavoritesByNumber(favorites, folderId, order)
        if (order == ORDER_ASC) {
            if (by == ORDER_BY_DATE) {
                result = result.sort{ a, b ->
                    a.bookmark.creationDate.time <=> b.bookmark.creationDate.time
                }
            }
            else if (by == ORDER_BY_TITLE) {
                result = result.sort{it.label.toLowerCase()}.reverse()
            }
        }
        else { // desc
            if (by == ORDER_BY_TITLE) {
                result = result.sort{it.label.toLowerCase()}
            }
            else if (by == ORDER_BY_DATE) {
                result = result.sort{ a, b ->
                    b.bookmark.creationDate.time <=> a.bookmark.creationDate.time
                }
            }
        }
        return result
    }

    private sendBookmarkPerMail(String paramEmails, List allResultsOrdered, Folder selectedFolder) {
        if (userService.isUserLoggedIn()) {
            def List emails = []
            if (paramEmails.contains(',')){
                emails=paramEmails.tokenize(',')
            }else{
                emails.add(paramEmails)
            }
            try {
                sendMail {
                    to emails.toArray()
                    from configurationService.getFavoritesSendMailFrom()
                    replyTo userService.getUserFromSession().getEmail()
                    subject (g.message(code:"ddbnext.send_favorites_subject_mail", encodeAs: "none", args: [
                        selectedFolder.title,
                        userService.getUserFromSession().getFirstnameAndLastnameOrNickname()
                    ]))
                    body( view:"_favoritesEmailBody",
                    model:[
                        results: allResultsOrdered,
                        dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                        userName:userService.getUserFromSession().getFirstnameAndLastnameOrNickname(),
                        baseUrl: commonConfigurationService.getSelfBaseUrl(),
                        contextUrl: commonConfigurationService.getContextUrl(),
                        folderDescription:selectedFolder.description,
                        folderTitle: selectedFolder.title
                    ])

                }
                flash.message = "ddbnext.favorites_email_was_sent_succ"
            } catch (e) {
                log.error "An error occurred sending the email "+ e.getMessage(), e
                flash.error = "ddbnext.favorites_email_was_not_sent_succ"
            }
        }else {
            redirect(controller: "user", action: "index")
        }
    }

    private def reportFavoritesList(String userId, String folderId){
        Folder folder = bookmarksService.findFolderById(folderId)
        if(folder){
            try {
                // Only when no blockingToken is set.
                if(folder.blockingToken?.isEmpty()){
                    folder.setBlockingToken(UUID.randomUUID().toString())
                    bookmarksService.updateFolder(folder)
                }

                def List emails = [
                    configurationService.getFavoritesReportMailTo()
                ]
                sendMail {
                    to emails.toArray()
                    from configurationService.getFavoritesSendMailFrom()
                    replyTo configurationService.getFavoritesSendMailFrom()
                    subject g.message(code:"ddbnext.Report_Public_List", encodeAs: "none")
                    body( view:"_favoritesReportEmailBody",
                    model:[
                        userId: userId,
                        folderId: folderId,
                        publicLink: g.createLink(action: "publicFavorites", params: [userId: userId, folderId: folderId]),
                        blockingLink: g.createLink(action: "publicFavorites", params: [userId: userId, folderId: folderId, blockingToken: folder.getBlockingToken()]),
                        unblockingLink: g.createLink(action: "publicFavorites", params: [userId: userId, folderId: folderId, unblockingToken: folder.getBlockingToken()]),
                        selfBaseUrl: commonConfigurationService.getSelfBaseUrl()
                    ])
                }
                flash.message = "ddbnext.favorites_list_reported"
            } catch (e) {
                log.error "An error occurred while reporting a favorites list: "+ e.getMessage(), e
                flash.error = "ddbnext.favorites_list_notreported"
            }
        }
    }
}
