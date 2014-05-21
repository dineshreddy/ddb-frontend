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
    private static final String CONTROLLER_NAME = "favoritesview"

    private static final def ORDER_ASC = "asc"
    private static final def ORDER_DESC = "desc"

    private static final String ORDER_BY_TITLE = "title"
    private static final String ORDER_BY_DATE = "date"

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
        def by = ORDER_BY_DATE
        if (params.by){
            if (params.by.toString()==ORDER_BY_TITLE){
                by = params.by
            }else{
                params.by= ORDER_BY_DATE
            }
        }
        def order = params.order

        def user = aasService.getPersonAsAdmin(params.userId)

        // A user want to report this list to DDB
        if(params.report){
            reportFavoritesList(user.id, folderId)
            redirect(controller: CONTROLLER_NAME, action: ACTION, params: [userId: user.id, folderId: folderId])
            return
        }

        if(params.blockingToken) {
            blockFavoritesList(user.id, folderId, params.blockingToken)
            redirect(controller: CONTROLLER_NAME, action: ACTION, params: [userId: user.id, folderId: folderId])
            return
        }

        if(params.unblockingToken) {
            unblockFavoritesList(user.id, folderId, params.unblockingToken)
            redirect(controller: CONTROLLER_NAME, action: ACTION, params: [userId: user.id, folderId: folderId])
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

            def fullPublicLink = g.createLink(controller: CONTROLLER_NAME, action: ACTION, params: [userId: user.getId(), folderId: folderId])

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

            // order results
            def parameters = [
                favorites : allResultsWithAdditionalInfo,
                action : ACTION,
                userId : user.id,
                folderId : selectedFolder.folderId,
                order : order,
                by : by.toString(),
                rows : rows,
                offset : offset
            ]
            def orderResult = orderFavorites(parameters)

            if (offset != 0){
                resultsItems=orderResult.favorites.drop(offset)
                resultsItems=resultsItems.take(rows)
            }else{
                resultsItems=orderResult.favorites.take(rows)
            }

            if (request.method=="POST"){
                sendBookmarkPerMail(params.email,allResultsWithAdditionalInfo)
            }

            def fullPublicLink = g.createLink(controller: CONTROLLER_NAME, action: ACTION, params: [userId: user.getId(), folderId: folderId])

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
                urlsForOrderTitle: orderResult.urlsForOrderTitle,
                urlsForOrder: orderResult.urlsForOrder,
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
            def by = ORDER_BY_DATE
            if (params.by){
                if (params.by.toString()==ORDER_BY_TITLE){
                    by = params.by
                }else{
                    params.by= ORDER_BY_DATE
                }
            }
            def order = params.order

            Folder selectedFolder = bookmarksService.findFolderById(folderId)
            List items = bookmarksService.findBookmarksByFolderId(user.getId(), folderId)

            // If the folder does not exist (maybe deleted) -> redirect to main favorites folder
            if(selectedFolder == null){
                redirect(controller: CONTROLLER_NAME, action: ACTION, id: mainFavoriteFolder.folderId)
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

            def fullPublicLink = g.createLink(controller: CONTROLLER_NAME, action: ACTION, params: [userId: user.getId(), folderId: folderId])

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

                // order results
                def parameters = [
                    favorites : allResultsWithAdditionalInfo,
                    action : ACTION,
                    userId : user.id,
                    folderId : selectedFolder.folderId,
                    order : order,
                    by : by.toString(),
                    rows : rows,
                    offset : offset
                ]
                def orderResult = orderFavorites(parameters)

                if (offset != 0){
                    resultsItems=orderResult.favorites.drop(offset)
                    resultsItems=resultsItems.take( rows)
                }else{
                    resultsItems=orderResult.favorites.take( rows)
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
                    urlsForOrderTitle:orderResult.urlsForOrderTitle,
                    urlsForOrder:orderResult.urlsForOrder,
                    baseUrl: commonConfigurationService.getSelfBaseUrl(),
                    contextUrl: commonConfigurationService.getContextUrl()
                ])
            }
        } else{
            redirect(controller:"user", action:"index", params: [referrer: grailsApplication.mainContext.getBean('de.ddb.common.GetCurrentUrlTagLib').getCurrentUrl()])
        }
    }

    private def orderFavorites(def parameters) {
        def favorites
        def urlsForOrder = [
            desc : "#",
            asc : g.createLink(
            controller : CONTROLLER_NAME,
            action : parameters.action,
            params : [
                offset : parameters.offset,
                rows : parameters.rows,
                order : ORDER_ASC,
                by : ORDER_BY_DATE,
                userId : parameters.userId,
                folderId: parameters.folderId
            ]
            )
        ]
        def urlsForOrderTitle = [
            desc : "#",
            asc : g.createLink(
            controller : CONTROLLER_NAME,
            action : parameters.action,
            params : [
                offset : parameters.offset,
                rows : parameters.rows,
                order : ORDER_ASC,
                by : ORDER_BY_TITLE,
                userId : parameters.userId,
                folderId : parameters.folderId
            ]
            )
        ]
        if (parameters.order == ORDER_ASC) {
            if (parameters.by == ORDER_BY_DATE) {
                favorites = parameters.favorites.sort{ a, b ->
                    a.bookmark.creationDate.time <=> b.bookmark.creationDate.time
                }
                urlsForOrder["desc"] = g.createLink(
                        controller : CONTROLLER_NAME,
                        action : parameters.action,
                        params : [
                            offset : parameters.offset,
                            rows : parameters.rows,
                            order : ORDER_DESC,
                            by : ORDER_BY_DATE,
                            userId : parameters.userId,
                            folderId : parameters.folderId
                        ]
                        )
                urlsForOrderTitle["desc"] = g.createLink(
                        controller : CONTROLLER_NAME,
                        action : parameters.action,
                        params : [
                            offset : parameters.offset,
                            rows : parameters.rows,
                            order : ORDER_DESC,
                            by : ORDER_BY_TITLE,
                            userId : parameters.userId,
                            folderId : parameters.folderId
                        ]
                        )
            }
            else {
                favorites = parameters.favorites.sort{it.label.toLowerCase()}.reverse()
                urlsForOrderTitle["desc"] = g.createLink(
                        controller : CONTROLLER_NAME,
                        action : parameters.action,
                        params : [
                            offset : parameters.offset,
                            rows : parameters.rows,
                            order : ORDER_DESC,
                            by : ORDER_BY_TITLE,
                            userId : parameters.userId,
                            folderId : parameters.folderId
                        ]
                        )
                urlsForOrder["desc"] = g.createLink(
                        controller : CONTROLLER_NAME,
                        action : parameters.action,
                        params : [
                            offset : parameters.offset,
                            rows : parameters.rows,
                            order : ORDER_DESC,
                            by : ORDER_BY_DATE,
                            userId : parameters.userId,
                            folderId : parameters.folderId
                        ]
                        )
            }
        }
        else {
            //desc
            if (parameters.by == ORDER_BY_TITLE) {
                favorites = parameters.favorites.sort{it.label.toLowerCase()}
                urlsForOrder["asc"] = g.createLink(
                        controller : CONTROLLER_NAME,
                        action : parameters.action,
                        params : [
                            offset : parameters.offset,
                            rows : parameters.rows,
                            order : ORDER_DESC,
                            by : ORDER_BY_DATE,
                            userId : parameters.userId,
                            folderId : parameters.folderId
                        ]
                        )
                urlsForOrderTitle["asc"] = g.createLink(
                        controller : CONTROLLER_NAME,
                        action : parameters.action,
                        params : [
                            offset : parameters.offset,
                            rows : parameters.rows,
                            order : ORDER_ASC,
                            by : ORDER_BY_TITLE,
                            userId : parameters.userId,
                            folderId : parameters.folderId
                        ]
                        )
            }
            else {
                //by date
                favorites = parameters.favorites.sort{ a, b ->
                    b.bookmark.creationDate.time <=> a.bookmark.creationDate.time
                }
                urlsForOrder["desc"] = g.createLink(
                        controller : CONTROLLER_NAME,
                        action : parameters.action,
                        params : [
                            offset : parameters.offset,
                            rows : parameters.rows,
                            order : ORDER_DESC,
                            by : ORDER_BY_DATE,
                            userId : parameters.userId,
                            folderId : parameters.folderId
                        ]
                        )
                urlsForOrderTitle["desc"] = g.createLink(
                        controller : CONTROLLER_NAME,
                        action : parameters.action,
                        params : [
                            offset : parameters.offset,
                            rows : parameters.rows,
                            order : ORDER_DESC,
                            by : ORDER_BY_TITLE,
                            userId : parameters.userId,
                            folderId : parameters.folderId
                        ]
                        )
            }
        }
        return [favorites : favorites, urlsForOrder : urlsForOrder, urlsForOrderTitle : urlsForOrderTitle]
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
                        publicLink: g.createLink(controller:CONTROLLER_NAME, action: "publicFavorites", params: [userId: userId, folderId: folderId]),
                        blockingLink: g.createLink(controller:CONTROLLER_NAME, action: "publicFavorites", params: [userId: userId, folderId: folderId, blockingToken: folder.getBlockingToken()]),
                        unblockingLink: g.createLink(controller:CONTROLLER_NAME, action: "publicFavorites", params: [userId: userId, folderId: folderId, unblockingToken: folder.getBlockingToken()]),
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
