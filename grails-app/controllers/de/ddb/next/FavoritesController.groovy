package de.ddb.next


import grails.converters.JSON

import java.text.Collator

import org.ccil.cowan.tagsoup.Parser
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.next.beans.Bookmark
import de.ddb.next.beans.Folder
import de.ddb.next.beans.User
import de.ddb.next.constants.FolderConstants
import de.ddb.next.constants.SearchParamEnum
import de.ddb.next.constants.SupportedLocales
import de.ddb.next.constants.Type
import de.ddb.next.exception.FavoritelistNotFoundException


class FavoritesController {

    private final static String ORDER_TITLE = "title"
    private final static String ORDER_DATE = "date"

    def bookmarksService
    def favoritesService
    def configurationService
    def searchService
    def sessionService

    def publicFavorites() {
        log.info "publicFavorites()"

        def rows=20 //default
        if (params.rows){
            rows = params.rows.toInteger()
        }
        def offset = 0 // default
        if(params.offset){
            offset = params.offset.toInteger()
        }
        def folderId = params.folderId
        def by = ORDER_DATE
        if (params.by){
            if (params.by.toString()==ORDER_TITLE){
                by = params.by
            }else{
                params.by= ORDER_DATE
            }
        }
        def order = params.order

        //def user = aasService.getPerson(params.userId) // does not work yet because of security constraints in AAS
        User user = new User()
        user.id = params.userId
        user.username = "TODO"

        // A user want to report this list to DDB
        if(params.report){
            reportFavoritesList(user.id, folderId)
            redirect(controller: "favorites", action: "publicFavorites", params: [userId: user.id, folderId: folderId])
            return
        }

        if(params.blockingToken) {
            blockFavoritesList(user.id, folderId, params.blockingToken)
            redirect(controller: "favorites", action: "publicFavorites", params: [userId: user.id, folderId: folderId])
            return
        }

        if(params.unblockingToken) {
            unblockFavoritesList(user.id, folderId, params.unblockingToken)
            redirect(controller: "favorites", action: "publicFavorites", params: [userId: user.id, folderId: folderId])
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

            def fullPublicLink = g.createLink(controller: "favorites", action: "publicFavorites", params: [userId: user.getId(), folderId: folderId])

            render(view: "publicFavorites", model: [
                selectedFolder: selectedFolder,
                resultsNumber: totalResults,
                selectedUser: user,
                publicFolders: publicFolders,
                dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                createAllFavoritesLink:favoritesService.createAllPublicFavoritesLink(0,0,"desc","title",0, user.id, selectedFolder.folderId),
                fullPublicLink: fullPublicLink,
                baseUrl: configurationService.getSelfBaseUrl(),
                contextUrl: configurationService.getContextUrl()
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
            def totalPagesFormatted = String.format(locale, "%,d", totalPages.toInteger())
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

            //Default ordering is newest on top == DESC
            allResultsWithAdditionalInfo.sort{ a, b ->
                b.bookmark.creationDate.time <=> a.bookmark.creationDate.time
            }
            def allResultsOrdered = allResultsWithAdditionalInfo //Used in the send-favorites listing

            def urlsForOrder=[desc:"#",asc:g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])]
            def urlsForOrderTitle=[desc:"#",asc:g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])]
            if (order=="asc"){
                if(by.toString()==ORDER_DATE){
                    allResultsWithAdditionalInfo.sort{ a, b ->
                        a.bookmark.creationDate.time <=> b.bookmark.creationDate.time
                    }
                    urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])
                    urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
                }else{
                    allResultsWithAdditionalInfo=allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}.reverse()
                    urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
                    urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])
                }
            }else{
                //desc
                if(by.toString()==ORDER_TITLE){
                    urlsForOrderTitle["asc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
                    urlsForOrder["asc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])
                    allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}
                }else{
                    //by date
                    urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])
                    urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
                }
            }

            if (offset != 0){
                resultsItems=allResultsWithAdditionalInfo.drop(offset)
                resultsItems=resultsItems.take(rows)
            }else{
                resultsItems=allResultsWithAdditionalInfo.take(rows)
            }

            if (request.method=="POST"){
                sendBookmarkPerMail(params.email,allResultsOrdered)
            }

            def fullPublicLink = g.createLink(controller: "favorites", action: "publicFavorites", params: [userId: user.getId(), folderId: folderId])

            render(view: "publicFavorites", model: [
                results: resultsItems,
                selectedFolder: selectedFolder,
                mainFavoriteFolder: null,
                allResultsOrdered: allResultsOrdered,
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
                urlsForOrderTitle: urlsForOrderTitle,
                urlsForOrder: urlsForOrder,
                fullPublicLink: fullPublicLink,
                baseUrl: configurationService.getSelfBaseUrl(),
                contextUrl: configurationService.getContextUrl()
            ])
        }

    }

    def favorites(){
        log.info "favorites()"
        if(isUserLoggedIn()){
            def rows=20 //default
            if (params.rows){
                rows = params.rows.toInteger()
            }
            def offset = 0 // default
            if(params.offset){
                offset = params.offset.toInteger()
            }
            def user = getUserFromSession()
            def mainFavoriteFolder = bookmarksService.findMainBookmarksFolder(user.getId())

            def folderId = mainFavoriteFolder.folderId
            if(params.id){
                folderId = params.id
            }
            def by = ORDER_DATE
            if (params.by){
                if (params.by.toString()==ORDER_TITLE){
                    by = params.by
                }else{
                    params.by= ORDER_DATE
                }
            }
            def order = params.order

            Folder selectedFolder = bookmarksService.findFolderById(folderId)
            List items = bookmarksService.findBookmarksByFolderId(user.getId(), folderId)

            // If the folder does not exist (maybe deleted) -> redirect to main favorites folder
            if(selectedFolder == null){
                redirect(controller: "user", action: "favorites", id: mainFavoriteFolder.folderId)
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
            allFoldersInformation = sortFolders(allFoldersInformation) { o -> o.folder }

            def fullPublicLink = g.createLink(controller: "favorites", action: "publicFavorites", params: [userId: user.getId(), folderId: folderId])

            if (totalResults <1){
                render(view: "favorites", model: [
                    selectedFolder: selectedFolder,
                    mainFavoriteFolder: mainFavoriteFolder,
                    resultsNumber: totalResults,
                    allFolders: allFoldersInformation,
                    userName: userName,
                    fullName: fullName,
                    nickName: nickName,
                    fullPublicLink: fullPublicLink,
                    dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                    baseUrl: configurationService.getSelfBaseUrl(),
                    createAllFavoritesLink:favoritesService.createAllFavoritesLink(0,0,"desc","title",0,folderId),
                ])
                return
            }else{
                def locale = getLocale()
                def allRes = favoritesService.retriveItemMD(items,locale)
                def resultsItems
                def urlQuery = searchService.convertQueryParametersToSearchParameters(params)

                // convertQueryParametersToSearchParameters modifies params
                params.remove("query")

                urlQuery["offset"] = 0
                //Calculating results pagination (previous page, next page, first page, and last page)
                def page = ((offset/urlQuery["rows"].toInteger())+1).toString()
                def totalPages = (Math.ceil(items.size()/urlQuery["rows"].toInteger()).toInteger())
                def totalPagesFormatted = String.format(locale, "%,d", totalPages.toInteger())
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

                //Default ordering is newest on top == DESC
                allResultsWithAdditionalInfo.sort{ a, b ->
                    b.bookmark.creationDate.time <=> a.bookmark.creationDate.time
                }
                def allResultsOrdered = allResultsWithAdditionalInfo //Used in the send-favorites listing

                def urlsForOrder=[desc:"#",asc:g.createLink(controller:'favorites',action:'favorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_DATE,id:folderId])]
                def urlsForOrderTitle=[desc:"#",asc:g.createLink(controller:'favorites',action:'favorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_TITLE,id:folderId])]
                if (order=="asc"){
                    if(by.toString()==ORDER_DATE){
                        allResultsWithAdditionalInfo.sort{ a, b->
                            a.bookmark.creationDate.time <=> b.bookmark.creationDate.time
                        }
                        urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,id:folderId])
                        urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,id:folderId])
                    }else{
                        allResultsWithAdditionalInfo=allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}.reverse()
                        urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,id:folderId])
                        urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,id:folderId])
                    }
                }else{
                    //desc
                    if(by.toString()==ORDER_TITLE){
                        urlsForOrderTitle["asc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_TITLE,id:folderId])
                        urlsForOrder["asc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,id:folderId])
                        allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}
                    }else{
                        //by date
                        urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,id:folderId])
                        urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,id:folderId])
                    }
                }

                if (offset != 0){
                    resultsItems=allResultsWithAdditionalInfo.drop(offset)
                    resultsItems=resultsItems.take( rows)
                }else{
                    resultsItems=allResultsWithAdditionalInfo.take( rows)
                }

                if (request.method=="POST"){
                    sendBookmarkPerMail(params.email,allResultsOrdered, selectedFolder)
                }

                render(view: "favorites", model: [
                    results: resultsItems,
                    selectedFolder: selectedFolder,
                    mainFavoriteFolder: mainFavoriteFolder,
                    allResultsOrdered: allResultsOrdered,
                    allFolders: allFoldersInformation,
                    viewType: urlQuery["viewType"],
                    resultsPaginatorOptions: resultsPaginatorOptions,
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
                    urlsForOrderTitle:urlsForOrderTitle,
                    urlsForOrder:urlsForOrder,
                    baseUrl: configurationService.getSelfBaseUrl(),
                    contextUrl: configurationService.getContextUrl()
                ])
            }
        } else{
            redirect(controller:"user", action:"index")
        }
    }

    private def reportFavoritesList(String userId, String folderId){
        log.info "reportFavoritesList()"
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
                        publicLink: g.createLink(controller:"favorites", action: "publicFavorites", params: [userId: userId, folderId: folderId]),
                        blockingLink: g.createLink(controller:"favorites", action: "publicFavorites", params: [userId: userId, folderId: folderId, blockingToken: folder.getBlockingToken()]),
                        unblockingLink: g.createLink(controller:"favorites", action: "publicFavorites", params: [userId: userId, folderId: folderId, unblockingToken: folder.getBlockingToken()]),
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

    private def blockFavoritesList(String userId, String folderId, String blockingToken){
        log.info "blockFavoritesList()"
        Folder folder = bookmarksService.findFolderById(folderId)
        if(folder){
            if(blockingToken == folder.getBlockingToken()){

                try {
                    folder.setIsPublic(false)
                    folder.setIsBlocked(true)
                    bookmarksService.updateFolder(folder)

                    flash.message = "ddbnext.favorites_list_blocked"

                } catch (e) {
                    log.error "An error occurred while blocking a favorites list: " + e.getMessage(), e
                    flash.error = "ddbnext.favorites_list_notblocked"
                }

            }else{
                flash.error = "ddbnext.favorites_list_notblockedtoken"
            }
        }
    }

    private def unblockFavoritesList(String userId, String folderId, String unblockingToken){
        log.info "unblockFavoritesList()"
        Folder folder = bookmarksService.findFolderById(folderId)
        if(folder){
            if(unblockingToken == folder.getBlockingToken()){

                try {
                    folder.setIsBlocked(false)
                    folder.setBlockingToken("")
                    bookmarksService.updateFolder(folder)

                    flash.message = "ddbnext.favorites_list_unblocked"

                } catch (e) {
                    log.error "An error occurred while blocking a favorites list: " + e.getMessage(), e
                    flash.error = "ddbnext.favorites_list_notunblocked"
                }

            }else{
                flash.error = "ddbnext.favorites_list_notunblockedtoken"
            }
        }
    }

    private Locale getLocale() {
        return SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
    }

    private sendBookmarkPerMail(String paramEmails, List allResultsOrdered, Folder selectedFolder) {
        if (isUserLoggedIn()) {
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
                    replyTo getUserFromSession().getEmail()
                    subject (g.message(code:"ddbnext.send_favorites_subject_mail", encodeAs: "none", args: [
                        selectedFolder.title,
                        getUserFromSession().getFirstnameAndLastnameOrNickname()
                    ]))
                    body( view:"_favoritesEmailBody",
                    model:[
                        results: allResultsOrdered,
                        dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                        userName:getUserFromSession().getFirstnameAndLastnameOrNickname(),
                        baseUrl: configurationService.getSelfBaseUrl(),
                        contextUrl: configurationService.getContextUrl(),
                        folderDescription:selectedFolder.description,
                        folderTitle: selectedFolder.title
                    ])

                }
                flash.message = "ddbnext.favorites_email_was_sent_succ"
            } catch (e) {
                log.info "An error occurred sending the email "+ e.getMessage(), e
                flash.error = "ddbnext.favorites_email_was_not_sent_succ"
            }
        }else {
            redirect(controller: "user", action: "index")
        }
    }



    private def sortFolders(allFoldersInformations, Closure folderAccess = { o -> o }){
        allFoldersInformations = allFoldersInformations.sort({ o1, o2 ->
            if (isMainBookmarkFolder(folderAccess(o1))) {
                return -1
            }
            if (isMainBookmarkFolder(folderAccess(o2))) {
                return 1
            }
            return Collator.getInstance(getLocale()).compare(folderAccess(o1).title, folderAccess(o2).title)
        })

        //Check for empty titles
        for (def folderInfo : allFoldersInformations) {
            if(folderAccess(folderInfo).title.trim().isEmpty()){
                folderAccess(folderInfo).title = "-"
            }
        }
        return allFoldersInformations
    }

    private def isMainBookmarkFolder(folder) {
        return folder.title == FolderConstants.MAIN_BOOKMARKS_FOLDER.value
    }

    def addFavorite() {
        log.info "addFavorite " + params.id
        long timestampStart = System.currentTimeMillis() // This is because of the slow request: See DDBNEXT-932
        def itemId = params.id
        def result = response.SC_BAD_REQUEST
        def User user = getUserFromSession()
        if (user != null) {
            Bookmark newBookmark = new Bookmark(
                    null,
                    user.getId(),
                    itemId,
                    new Date().getTime(),
                    Type.CULTURAL_ITEM,
                    null,
                    "",
                    new Date().getTime())
            String newBookmarkId = bookmarksService.createBookmark(newBookmark)
            if (newBookmarkId) {
                result = response.SC_CREATED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "addFavorite returns " + result
        long timestampStop = System.currentTimeMillis()
        log.info "addFavorite duration: "+(timestampStop-timestampStart)/1000 // This is because of the slow request: See DDBNEXT-932
        render(status: result)
    }

    def addFavoriteToFolder() {
        log.info "addFavoriteToFolder " + params.folderId + "," + params.itemId
        def result = response.SC_BAD_REQUEST
        def User user = getUserFromSession()
        if (user != null) {
            Bookmark newBookmark = new Bookmark(
                    null,
                    user.getId(),
                    params.itemId,
                    new Date().getTime(),
                    Type.CULTURAL_ITEM,
                    [params.folderId],
                    "",
                    new Date().getTime())
            String newBookmarkId = bookmarksService.createBookmark(newBookmark)
            if(newBookmarkId != null){
                result = response.SC_CREATED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "addFavoriteToFolder returns " + result
        render(status: result)
    }

    def deleteFavorite() {
        log.info "deleteFavorite " + params.id
        def itemId = params.id
        def result = response.SC_NOT_FOUND
        def User user = getUserFromSession()
        if (user != null) {
            if (bookmarksService.deleteBookmarksByItemIds(user.getId(), [itemId])) {
                result = response.SC_NO_CONTENT
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteFavorite returns " + result
        render(status: result)
    }

    def deleteFavoritesFromFolder() {
        log.info "deleteFavoritesFromFolder " + request.JSON

        def itemIds = null
        def folderId = null
        if(request.JSON) {
            itemIds = request.JSON.ids
            folderId = request.JSON.folderId
        }

        def result = response.SC_NOT_FOUND
        def User user = getUserFromSession()
        if (user != null) {

            // Check if the items all belong to the current user
            boolean itemsAreOwnedByUser = true
            def bookmarks = bookmarksService.findBookmarksByFolderId(user.getId(), folderId)
            def bookmarkIds = bookmarks.collect { it.itemId }
            itemIds.each {
                if(!(it in bookmarkIds)){
                    itemsAreOwnedByUser = false
                }
            }

            if(itemsAreOwnedByUser){
                if(itemIds == null || itemIds.size() == 0) {
                    result = response.SC_OK
                }else{
                    // Special case: if bookmarks are deleted in the main favorites folder -> delete them everywhere
                    //def mainFavoriteFolder = favoritesPageService.getMainFavoritesFolder()
                    def mainFavoriteFolder = bookmarksService.findMainBookmarksFolder(user.getId())

                    if(folderId == mainFavoriteFolder.folderId) {
                        bookmarksService.deleteBookmarksByItemIds(user.getId(), itemIds)
                    }else{
                        def favorites = bookmarksService.findBookmarkedItemsInFolder(user.getId(), itemIds, folderId)
                        def favoriteIds = favorites.collect { it.bookmarkId }
                        bookmarksService.removeBookmarksFromFolder(favoriteIds, folderId)
                    }
                    result = response.SC_OK
                }
            }else{
                result = response.SC_UNAUTHORIZED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteFavoritesFromFolder returns " + result
        render(status: result)
    }



    def filterFavorites() {
        log.info "filterFavorites " + request.JSON
        def itemIdList = request.JSON
        User user = getUserFromSession()
        if (user != null) {
            Folder mainFavoritesFolder = bookmarksService.findMainBookmarksFolder(user.getId())
            def bookmarks = bookmarksService.findBookmarkedItemsInFolder(user.getId(), itemIdList, mainFavoritesFolder.folderId)
            List result = bookmarks.collect {it.itemId}
            log.info "filterFavorites returns " + result
            render(result as JSON)
        } else {
            log.info "filterFavorites returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def getFavorite() {
        log.info "getFavorite " + params.id
        def result = response.SC_NOT_FOUND
        def User user = getUserFromSession()
        if (user != null) {
            def bookmark = bookmarksService.findBookmarkedItemsInFolder(user.getId(), [params.id], null)
            log.info "getFavorite returns " + bookmark
            render(bookmark as JSON)
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "getFavorite returns " + result
        render(status: result)
    }

    def getFavorites() {
        log.info "getFavorites"
        def User user = getUserFromSession()
        if (user != null) {
            def result = bookmarksService.findBookmarksByUserId(user.getId())
            log.info "getFavorites returns " + result
            render(result as JSON)
        } else {
            log.info "getFavorites returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def getFavoriteFolder() {
        log.info "getFavoriteFolder " + params.id
        def result = response.SC_NOT_FOUND
        def User user = getUserFromSession()
        if (user != null) {
            Folder folder = bookmarksService.findFolderById(params.id)
            log.info "getFavoriteFolder returns " + folder
            folder.setBlockingToken("") // Don't expose the blockingToken to Javascript!
            render(folder as JSON)
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "getFavoriteFolder returns " + result
        render(status: result)
    }

    /**
     * Get a sorted list of all bookmark folders. The main folder is marked with "isMainFolder".
     *
     * @return sorted list of all bookmark folders
     */
    def getFavoriteFolders() {
        log.info "getFavoriteFolders"
        def User user = getUserFromSession()
        if (user != null) {
            def mainFolder = bookmarksService.findMainBookmarksFolder(user.getId())
            def folders = bookmarksService.findAllFolders(user.getId())
            folders.find {it.folderId == mainFolder.folderId}.isMainFolder = true
            folders = sortFolders(folders)
            folders.each {it.blockingToken = ""} // Don't expose the blockingToken to Javascript
            log.info "getFavoriteFolders returns " + folders
            render(folders as JSON)
        } else {
            log.info "getFavoriteFolders returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def createFavoritesFolder() {
        log.info "createFavoritesFolder " + request.JSON

        def title = request.JSON.title
        def description = request.JSON.description

        title = sanitizeTextInput(title)
        description = sanitizeTextInput(description)

        def result = response.SC_BAD_REQUEST
        def User user = getUserFromSession()
        if (user != null) {
            def publishingName = user.getUsername()

            Folder newFolder = new Folder(
                    null,
                    user.getId(),
                    title,
                    description,
                    false,
                    publishingName,
                    false,
                    "")
            String newFolderId = bookmarksService.createFolder(newFolder)
            if(newFolderId){
                result = response.SC_CREATED
                flash.message = "ddbnext.favorites_folder_create_succ"
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "createFavoritesFolder returns " + result
        render(status: result)
    }

    def deleteFavoritesFolder() {
        log.info "deleteFavoritesFolder " + request.JSON
        boolean deleteItems = request.JSON.deleteItems
        def folderId = request.JSON.folderId
        def result = response.SC_BAD_REQUEST

        def User user = getUserFromSession()
        if (user != null) {
            def foldersOfUser = bookmarksService.findAllFolders(user.getId())

            // 1) Check if the current user is really the owner of this folder, else deny
            // 2) Check if the folder is a default favorites folder -> if true, deny
            boolean isFolderOfUser = false
            boolean isDefaultFavoritesFolder = false
            foldersOfUser.each {
                if(it.folderId == folderId){
                    isFolderOfUser = true
                    if(it.title == FolderConstants.MAIN_BOOKMARKS_FOLDER.value){
                        isDefaultFavoritesFolder = true
                    }
                }
            }
            if(isFolderOfUser){
                if(isDefaultFavoritesFolder){
                    result = response.SC_FORBIDDEN

                }else{
                    def favorites = bookmarksService.findBookmarksByFolderId(user.getId(), folderId)

                    // delete items in ALL folders
                    if(deleteItems){
                        // Find itemIDs of the selected folder
                        def itemIds = []
                        favorites.each {
                            itemIds.add(it.itemId)
                        }
                        // Delete itemIds in ALL folders
                        bookmarksService.deleteBookmarksByItemIds(user.getId(), itemIds)
                    }else{
                        // delete items only in the current folder
                        def bookmarkIds = []
                        favorites.each {
                            bookmarkIds.add(it.bookmarkId)
                        }
                        //bookmarksService.deleteBookmarksByBookmarkIds(user.getId(), bookmarkIds)
                        bookmarksService.deleteDocumentsByTypeAndIds(user.getId(), bookmarkIds, "bookmark")
                    }

                    bookmarksService.deleteFolder(folderId)
                    result = response.SC_OK
                    flash.message = "ddbnext.favorites_folder_delete_succ"
                }
            } else {
                result = response.SC_UNAUTHORIZED
                flash.error = "ddbnext.favorites_folder_delete_unauth"
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }

        log.info "deleteFavoritesFolder returns " + result
        render(status: result)
    }

    def copyFavorites() {
        log.info "copyFavorites " + request.JSON
        def favoriteIds = request.JSON.ids
        def folderIds = request.JSON.folders

        def result = response.SC_BAD_REQUEST

        def User user = getUserFromSession()
        if (user != null) {

            // Check if the folders to copy to are actually folders owned by this user (security)
            def foldersOfUser = bookmarksService.findAllFolders(user.getId())
            boolean foldersOwnedByUser = true
            def allFolderIds = foldersOfUser.collect { it.folderId }
            folderIds.each {
                if(!(it in allFolderIds)){
                    foldersOwnedByUser = false
                }
            }

            //            if(foldersOwnedByUser){
            //                //def favoriteIds = bookmarksService.findBookmarkedItems(user.getId(), itemIds)
            //                folderIds.each { folderId ->
            //                    favoriteIds.each { favoriteId ->
            //                        Bookmark favoriteToCopy = bookmarksService.findBookmarkById(favoriteId)
            //                        String itemId = favoriteToCopy.itemId
            //                        // Check if the item already exists in the list
            //                        List favoritesInTargetFolder = bookmarksService.findBookmarksByItemId(user.getId(), itemId, folderId)
            //                        // if not -> add it
            //                        if(favoritesInTargetFolder.size() == 0){
            //                            bookmarksService.saveBookmark(user.getId(), [folderId], itemId, null, Type.CULTURAL_ITEM, favoriteToCopy.creationDate.getTime())
            //                        }
            //                    }
            //                }
            //            }else{
            //                result = response.SC_UNAUTHORIZED
            //            }

            if(foldersOwnedByUser){
                folderIds.each { folderId ->
                    favoriteIds.each { favoriteId ->
                        Bookmark favoriteToCopy = bookmarksService.findBookmarkById(favoriteId)
                        String itemId = favoriteToCopy.itemId
                        // Check if the item already exists in the list
                        List favoritesInTargetFolder = bookmarksService.findBookmarkedItemsInFolder(user.getId(), [itemId], folderId)
                        // if not -> add it
                        if(favoritesInTargetFolder.size() == 0){
                            Bookmark newBookmark = new Bookmark(
                                    null,
                                    user.getId(),
                                    itemId,
                                    favoriteToCopy.creationDate.getTime(),
                                    Type.CULTURAL_ITEM,
                                    [folderId],
                                    "",
                                    new Date().getTime())
                            bookmarksService.createBookmark(newBookmark)
                        }
                    }
                }
            }else{
                result = response.SC_UNAUTHORIZED
            }


            result = response.SC_OK

            flash.message = "ddbnext.favorites_copy_succ"

        } else {
            result = response.SC_UNAUTHORIZED
        }

        log.info "copyFavorites returns " + result
        render(status: result)

    }

    def editFavoritesFolder() {
        log.info "editFavoritesFolder " + request.JSON

        def id = request.JSON.id
        def title = request.JSON.title
        def description = request.JSON.description
        def publishingType = request.JSON.name
        def isPublic = request.JSON.isPublic

        title = sanitizeTextInput(title)
        description = sanitizeTextInput(description)

        def result = response.SC_BAD_REQUEST

        def User user = getUserFromSession()
        if (user != null) {

            def publishingName = ""
            if(publishingType == FolderConstants.PUBLISHING_NAME_FULLNAME.getValue()) {
                publishingName = user.getFirstnameAndLastnameOrNickname()
            }else{
                publishingName = user.getUsername()
            }


            List foldersOfUser = bookmarksService.findAllFolders(user.getId())
            Folder folder = null

            // 1) Check if the current user is really the owner of this folder, else deny
            // 2) Check if the folder is a default favorites folder -> if true, deny
            boolean isFolderOfUser = false
            boolean isDefaultFavoritesFolder = false
            foldersOfUser.each {
                if(it.folderId == id){
                    folder = it
                    // check if the favorites list is blocked
                    if(it.isBlocked){
                        isPublic = false
                    }

                    isFolderOfUser = true
                    if(it.title == FolderConstants.MAIN_BOOKMARKS_FOLDER.value){
                        isDefaultFavoritesFolder = true
                    }
                }
            }
            if(isFolderOfUser && !isDefaultFavoritesFolder){
                bookmarksService.updateFolder(
                        folder.getFolderId(),
                        title,
                        description,
                        isPublic,
                        publishingName,
                        folder.getIsBlocked(),
                        folder.getBlockingToken())

                result = response.SC_OK
                flash.message = "ddbnext.folder_edit_succ"
            } else {
                result = response.SC_UNAUTHORIZED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }

        log.info "editFavoritesFolder returns " + result
        render(status: result)
    }

    def setComment() {
        log.info "setComment " + request.JSON

        def id = request.JSON.id
        def text = request.JSON.text

        Parser tagsoupParser = new Parser()
        XmlSlurper slurper = new XmlSlurper(tagsoupParser)
        String cleanedText = slurper.parseText(text).text()
        cleanedText = sanitizeTextInput(cleanedText)

        def result = response.SC_BAD_REQUEST

        def User user = getUserFromSession()
        //        if (user != null) {
        //
        //            // 1) Check if the current user is really the owner of this bookmark, else deny
        //            Bookmark bookmark = bookmarksService.findBookmarkById(id)
        //            boolean isBookmarkOfUser = false
        //            if(bookmark.userId == user.getId()){
        //                isBookmarkOfUser = true
        //            }
        //            if(isBookmarkOfUser){
        //                bookmarksService.updateBookmark(id, cleanedText)
        //                result = response.SC_OK
        //            } else {
        //                result = response.SC_UNAUTHORIZED
        //            }
        //        } else {
        //            result = response.SC_UNAUTHORIZED
        //        }

        if (user != null) {

            // 1) Check if the current user is really the owner of this bookmark, else deny
            Bookmark bookmark = bookmarksService.findBookmarkById(id)
            boolean isBookmarkOfUser = false
            if(bookmark.userId == user.getId()){
                isBookmarkOfUser = true
            }
            if(isBookmarkOfUser){
                bookmarksService.updateBookmarkDescription(id, cleanedText)
                result = response.SC_OK
            } else {
                result = response.SC_UNAUTHORIZED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }


        log.info "setComment returns " + result
        render(status: result)

    }

    def togglePublish() {
        log.info "togglePublish " + request.JSON

        def id = request.JSON.id

        def result = response.SC_BAD_REQUEST

        def User user = getUserFromSession()
        if (user != null) {

            // 1) Check if the current user is really the owner of this folder, else deny
            Folder folder = bookmarksService.findFolderById(id)
            boolean isFolderOfUser = false
            if(folder.userId == user.getId()){
                isFolderOfUser = true
            }
            if(isFolderOfUser && !folder.isBlocked){
                bookmarksService.updateFolder(
                        folder.folderId,
                        folder.title,
                        folder.description,
                        !folder.isPublic,
                        folder.publishingName,
                        folder.isBlocked,
                        folder.blockingToken)
                result = response.SC_OK
            } else {
                result = response.SC_UNAUTHORIZED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }

        log.info "togglePublish returns " + result
        render(status: result)

    }


    private boolean isUserLoggedIn() {
        return sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
    }

    private User getUserFromSession() {
        return sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
    }

    private String sanitizeTextInput(String input){
        String output = ""
        if(input != null) {
            Parser tagsoupParser = new Parser()
            XmlSlurper slurper = new XmlSlurper(tagsoupParser)
            output = input
            output = slurper.parseText(output).text()
            output = output.replaceAll("\\\"", "''")
            output = output.replaceAll("´", "'")
            output = output.replaceAll("`", "'")
        }
        return output
    }

    private int getIntegerParam(String paramKey, int defaultValue) {
        if (params[paramKey]){
            return params[paramKey].toInteger()
        }
        return defaultValue
    }

    private Closure linkGenerator(String action, int offset, int rows, Map extraParams) {
        Map commonParams = [
            (SearchParamEnum.OFFSET.getName()):offset,
            (SearchParamEnum.ROWS.getName()):rows
        ]
        commonParams.putAll(extraParams)
        return { String order, String by ->
            createFavoritesLink(order, by, commonParams, action)
        }
    }

    private def createFavoritesLink(String order, String by, Map commonParams, String action) {
        Map currentParams = [
            (SearchParamEnum.ORDER.getName()):order,
            (SearchParamEnum.BY.getName()):by
        ]
        currentParams.putAll(commonParams)
        return g.createLink(controller:'favorites', action:action, params: currentParams)
    }

    private Folder getSelectedFolder(String folderId, User user) {
        def selectedFolder = bookmarksService.findPublicFolderById(folderId)

        // If the folder does not exist (maybe deleted) or the user does not exist -> 404
        if(selectedFolder == null || user == null){
            throw new FavoritelistNotFoundException("publicFavorites(): favorites list or user do not exist")
        }
        return selectedFolder
    }

    private boolean handleReportingOrBlocking(User user, String folderId, Map params) {
        if(params.report){
            reportFavoritesList(user.id, folderId)
            redirect(controller: "favorites", action: "publicFavorites", params: [userId: user.id, folderId: folderId])
            return true
        }

        if(params.blockingToken) {
            blockFavoritesList(user.id, folderId, params.blockingToken)
            redirect(controller: "favorites", action: "publicFavorites", params: [userId: user.id, folderId: folderId])
            return true
        }

        if(params.unblockingToken) {
            unblockFavoritesList(user.id, folderId, params.unblockingToken)
            redirect(controller: "favorites", action: "publicFavorites", params: [userId: user.id, folderId: folderId])
            return true
        }
        return false
    }



}
