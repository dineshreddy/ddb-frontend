package de.ddb.next

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.beans.Folder
import de.ddb.common.beans.User
import de.ddb.common.beans.aas.AasCredential
import de.ddb.common.beans.aas.Person
import de.ddb.common.exception.FavoritelistNotFoundException


/**
 * AJAX requests should be handled by FavoritesController
 *
 * @author boz
 */
class FavoritesviewController {
    def aasPersonService
    def bookmarksService
    def favoritesService
    def configurationService
    def searchService
    def sessionService
    def userService
    def languageService

    def publicFavorites() {
        final def ACTION = "publicFavorites"
        int rows = params.rows ? params.rows.toInteger() : 20
        int offset = params.offset ? params.offset.toInteger() : 0
        String order = params.order ? params.order : favoritesService.ORDER_ASC
        String by = params.by ? params.by : favoritesService.ORDER_BY_NUMBER
        def user = createUser(aasPersonService.getPerson(params.userId, new AasCredential(
                configurationService.getAasAdminUserId(),
                configurationService.getAasAdminPassword())))
        def folderId = params.folderId

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

        publicFolders = sortPublicFoldersAndRemoveSelected(publicFolders, selectedFolder.folderId)

        def tamMax = 20
        def showLinkAllList

        if(publicFolders.size() > tamMax) {
            if(params.showLinkAllList) {
                showLinkAllList = params.showLinkAllList.toBoolean()
            }else {
                showLinkAllList = true
            }

            if(showLinkAllList) {
                publicFolders = publicFolders.subList(0, tamMax)
            }
        }

        List items = bookmarksService.findBookmarksByPublicFolderId(folderId)

        def totalResults= items.size()

        def lastPgOffset=0

        if (totalResults <1){
            render(view: ACTION, model: [
                selectedFolder: selectedFolder,
                resultsNumber: totalResults,
                selectedUserId: user.id,
                selectedUserFirstnameAndLastnameOrNickname: user.getFirstnameAndLastnameOrNickname(),
                selectedUserUserName: user.username,
                publicFolders: publicFolders,
                showLinkAllList: showLinkAllList,
                dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                createAllFavoritesLink:favoritesService.createAllPublicFavoritesLink(0,0,favoritesService.ORDER_DESC,"title",0, user.id, selectedFolder.folderId),
                fullPublicLink: createPublicLink(user.getId(), folderId),
                baseUrl: configurationService.getSelfBaseUrl(),
                contextUrl: configurationService.getContextUrl()
            ])
            return
        }else{
            def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))
            def allRes = favoritesService.retrieveItemMD(items)
            def resultsItems

            def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
            def queryString = request.getQueryString() ? request.getQueryString() : ""

            // convertQueryParametersToSearchParameters modifies params
            params.remove("query")

            //Calculating results pagination (previous page, next page, first page, and last page)
            def page = ((offset/urlQuery["rows"].toInteger())+1).toString()
            def totalPages = (Math.ceil(allRes.size().toInteger()/urlQuery["rows"].toInteger()).toInteger())
            lastPgOffset=((Math.ceil(allRes.size()/rows)*rows)-rows).toInteger()
            if (totalPages.toFloat()<page.toFloat()){
                offset= (Math.ceil((allRes.size()-rows)/10)*10).toInteger()
                if ((Math.ceil((allRes.size()-rows)/10)*10).toInteger()<0){
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

            def orderedFavorites = favoritesService.orderFavorites(allResultsWithAdditionalInfo, selectedFolder.folderId, order, by)
            if (offset != 0){
                resultsItems=orderedFavorites.drop(offset)
                resultsItems=resultsItems.take(rows)
            }else{
                resultsItems=orderedFavorites.take(rows)
            }

            if (request.method=="POST"){
                sendBookmarksPerMail(params.email, orderedFavorites, selectedFolder)
            }

            render(view: ACTION, model: [
                results: resultsItems,
                selectedFolder: selectedFolder,
                mainFavoriteFolder: null,
                allResultsOrdered: allResultsWithAdditionalInfo,
                viewType: urlQuery["viewType"],
                resultsPaginatorOptions: resultsPaginatorOptions,
                paginationURL: searchService.buildPagination(allRes.size(), urlQuery, request.forwardURI+'?'+queryString),
                page: page,
                resultsNumber: totalResults,
                createAllFavoritesLink: favoritesService.createAllPublicFavoritesLink(offset, rows, order, by, lastPgOffset, user.id, selectedFolder.folderId),
                totalPages: totalPages,
                numberOfResultsFormatted: numberOfResultsFormatted,
                offset: offset,
                rows: rows,
                order: order,
                by: by,
                selectedUserId: user.id,
                selectedUserFirstnameAndLastnameOrNickname: user.getFirstnameAndLastnameOrNickname(),
                selectedUserUserName: user.username,
                showLinkAllList: showLinkAllList,
                publicFolders: publicFolders,
                dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                urlsForOrderDate: orderLinks.urlsForOrderDate,
                urlsForOrderNumber: orderLinks.urlsForOrderNumber,
                urlsForOrderTitle: orderLinks.urlsForOrderTitle,
                fullPublicLink: createPublicLink(user.getId(), folderId),
                baseUrl: configurationService.getSelfBaseUrl(),
                contextUrl: configurationService.getContextUrl(),
                createdDateString: favoritesService.formatDate(selectedFolder.creationDate),
                updatedDateString: favoritesService.formatDate(selectedFolder.updatedDate)
            ])
        }

    }

    def favorites(){
        if(userService.isUserLoggedIn()){
            final def ACTION = "favorites"
            int rows = params.rows ? params.rows.toInteger() : 20
            int offset = params.offset ? params.offset.toInteger() : 0
            String order = params.order ? params.order : favoritesService.ORDER_ASC
            String by = params.by ? params.by : favoritesService.ORDER_BY_NUMBER
            def user = userService.getUserFromSession()
            def mainFavoriteFolder = bookmarksService.findMainBookmarksFolder(user.getId())
            def folderId = mainFavoriteFolder.folderId
            if(params.id){
                folderId = params.id
            }

            Folder selectedFolder = bookmarksService.findFolderById(folderId)

            // If the folder does not exist (maybe deleted) -> redirect to main favorites folder
            if(selectedFolder == null){
                redirect(action: ACTION, id: mainFavoriteFolder.folderId)
                return
            }

            List favorites = favoritesService.getFavoriteList(user, selectedFolder, order, by)
            def totalResults= favorites.size()

            def userName = user.getFirstnameAndLastnameOrNickname()
            def nickName = user.getUsername()
            def fullName = null
            if(user.getFirstname() || user.getLastname()){
                fullName = user.getFirstname() + " " + user.getLastname()
            }
            def lastPgOffset=0

            if (totalResults <1){
                render(view: ACTION, model: [
                    selectedFolder: selectedFolder,
                    mainFavoriteFolder: mainFavoriteFolder,
                    resultsNumber: totalResults,
                    allFolders: favoritesService.getFolderList(user.id),
                    userName: userName,
                    fullName: fullName,
                    nickName: nickName,
                    fullPublicLink: createPublicLink(user.getId(), folderId),
                    dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                    baseUrl: configurationService.getSelfBaseUrl(),
                    createAllFavoritesLink:favoritesService.createAllFavoritesLink(0,0,favoritesService.ORDER_DESC,"title",0,folderId),
                ])
                return
            }else{
                def locale = favoritesService.getLocale()
                def resultsItems
                def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
                def queryString = request.getQueryString() ? request.getQueryString() : ""

                // convertQueryParametersToSearchParameters modifies params
                params.remove("query")

                //urlQuery["offset"] = 0
                //Calculating results pagination (previous page, next page, first page, and last page)
                def page = ((offset/urlQuery["rows"].toInteger())+1).toString()
                def totalPages = (Math.ceil(favorites.size()/urlQuery["rows"].toInteger()).toInteger())
                lastPgOffset=((Math.ceil(favorites.size()/rows)*rows)-rows).toInteger()

                if (totalPages.toFloat()<page.toFloat()){
                    offset= (Math.ceil((favorites.size()-rows)/10)*10).toInteger()
                    if ((Math.ceil((favorites.size()-rows)/10)*10).toInteger()<0){
                        lastPgOffset=20
                    }
                    page=totalPages
                }
                def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
                def numberOfResultsFormatted = String.format(locale, "%,d", favorites.size().toInteger())
                def orderLinks = createOrderLinks([
                    action : ACTION,
                    userId : user.id,
                    folderId : selectedFolder.folderId,
                    rows : rows,
                    offset : offset
                ])

                if (offset != 0){
                    resultsItems = favorites.drop(offset)
                    resultsItems = resultsItems.take( rows)
                }else{
                    resultsItems = favorites.take( rows)
                }

                if (request.method=="POST"){
                    sendBookmarksPerMail(params.email, allResultsWithAdditionalInfo, selectedFolder)
                }
                render(view: ACTION, model: [
                    results: resultsItems,
                    selectedFolder: selectedFolder,
                    mainFavoriteFolder: mainFavoriteFolder,
                    allResultsOrdered: favorites,
                    allFolders: favoritesService.getFolderList(user.id),
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
                    order: order,
                    by: by,
                    userName: userName,
                    fullName: fullName,
                    nickName: nickName,
                    fullPublicLink: createPublicLink(user.getId(), folderId),
                    dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                    urlsForOrderDate:orderLinks.urlsForOrderDate,
                    urlsForOrderNumber:orderLinks.urlsForOrderNumber,
                    urlsForOrderTitle:orderLinks.urlsForOrderTitle,
                    baseUrl: configurationService.getSelfBaseUrl(),
                    contextUrl: configurationService.getContextUrl()
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
        for (order in [
            favoritesService.ORDER_ASC,
            favoritesService.ORDER_DESC
        ]) {
            urlsForOrderDate[order] = g.createLink(
                    action : parameters.action,
                    id : parameters.folderId,
                    params : [
                        offset : parameters.offset,
                        rows : parameters.rows,
                        order : order,
                        by : favoritesService.ORDER_BY_DATE,
                        userId : parameters.userId,
                        folderId : parameters.folderId
                    ]
                    )
            urlsForOrderNumber[order] = g.createLink(
                    action : parameters.action,
                    id : parameters.folderId,
                    params : [
                        offset : parameters.offset,
                        rows : parameters.rows,
                        order : order,
                        by : favoritesService.ORDER_BY_NUMBER,
                        userId : parameters.userId,
                        folderId : parameters.folderId
                    ]
                    )
            urlsForOrderTitle[order] = g.createLink(
                    action : parameters.action,
                    id : parameters.folderId,
                    params : [
                        offset : parameters.offset,
                        rows : parameters.rows,
                        order : order,
                        by : favoritesService.ORDER_BY_TITLE,
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

    /**
     * Create link to a public folder list
     *
     * @return
     */
    private def createPublicLink(String userId, String folderId) {
        return g.createLink(action: "publicFavorites", params: [userId: userId, folderId: folderId])
    }

    private User createUser(Person person) {
        User result = new User()

        result.setId(person?.id)
        result.setFirstname(person?.foreName)
        result.setLastname(person?.surName)
        result.setUsername(person?.getNickname())
        return result
    }

    private sendBookmarksPerMail(String paramEmails, List allResultsOrdered, Folder selectedFolder) {
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
                        baseUrl: configurationService.getSelfBaseUrl(),
                        contextUrl: configurationService.getContextUrl(),
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
                    body( view:"_favoritesReportEmailBody", model:[
                        userId: userId,
                        folderId: folderId,
                        publicLink: g.createLink(action: "publicFavorites", params: [userId: userId, folderId: folderId]),
                        blockingLink: g.createLink(action: "publicFavorites", params: [userId: userId, folderId: folderId, blockingToken: folder.getBlockingToken()]),
                        unblockingLink: g.createLink(action: "publicFavorites", params: [userId: userId, folderId: folderId, unblockingToken: folder.getBlockingToken()]),
                        selfBaseUrl: configurationService.getSelfBaseUrl()
                    ])
                }
                flash.message = "ddbnext.favorites_list_reported"
            } catch (e) {
                log.error "An error occurred while reporting a favorites list: "+ e.getMessage(), e
                flash.error = "ddbnext.favorites_list_notreported"
            }
        }
    }

    def allpublicfolders() {
        List publicFolders = bookmarksService.findAllPublicFolders(params.userId)

        publicFolders = sortPublicFoldersAndRemoveSelected(publicFolders, params.selectedFolderId)

        render(template: "favoritesAllFolders", model: [publicFolders: publicFolders, selectedUserId : params.userId])
    }

    private sortPublicFoldersAndRemoveSelected(publicFolders, selectedFolderId) {
        publicFolders.sort{ a, b ->
            b.updatedDate <=> a.updatedDate
        }

        def aux
        publicFolders.each() {
            if(it.folderId == selectedFolderId) {
                aux = it
            }
        }
        publicFolders.remove(aux)

        return publicFolders
    }
}
