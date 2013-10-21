/*
 * Copyright (C) 2013 FIZ Karlsruhe
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

import java.util.List

import grails.converters.JSON

import javax.servlet.http.HttpSession
import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16

import org.ccil.cowan.tagsoup.Parser
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.next.beans.Bookmark
import de.ddb.next.beans.Folder
import de.ddb.next.beans.User
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
        def folderId = params.folderId
        def by = ORDER_DATE
        if (params.by){
            if (params.by.toString()==ORDER_TITLE){
                by = params.by
            }else{
                params.by= ORDER_DATE
            }
        }

        //def user = aasService.getPerson(params.userId) // does not work yet because of security constraints in AAS
        User user = new User()
        user.id = params.userId
        user.username = "TODO"

        def selectedFolder = bookmarksService.findPublicFolderById(folderId)

        // If the folder does not exist (maybe deleted) or the user does not exist -> 404
        if(selectedFolder == null || user == null){
            throw new FavoritelistNotFoundException("publicFavorites(): favorites list or user do not exist")
        }

        List publicFolders = bookmarksService.findAllPublicFolders(user.getId())

        List items = bookmarksService.findBookmarksByPublicFolderId(folderId)

        def totalResults= items.size()

        def lastPgOffset=0

        if (totalResults <1){
            render(view: "publicFavorites", model: [
                selectedFolder: selectedFolder,
                resultsNumber: totalResults,
                selectedUser: user,
                publicFolders: publicFolders,
                dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                createAllFavoritesLink:favoritesService.createAllPublicFavoritesLink(0,0,"desc",0, user.id, selectedFolder.folderId),
                baseDomain: configurationService.getFavoritesBasedomain(),
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
            def page = ((params.offset.toInteger()/urlQuery["rows"].toInteger())+1).toString()
            def totalPages = (Math.ceil(items.size()/urlQuery["rows"].toInteger()).toInteger())
            def totalPagesFormatted = String.format(locale, "%,d", totalPages.toInteger())
            lastPgOffset=((Math.ceil(items.size()/rows)*rows)-rows).toInteger()

            if (totalPages.toFloat()<page.toFloat()){
                params.offset= (Math.ceil((items.size()-rows)/10)*10).toInteger()
                if ((Math.ceil((items.size()-rows)/10)*10).toInteger()<0){
                    lastPgOffset=20
                }
                page=totalPages
            }
            def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
            def numberOfResultsFormatted = String.format(locale, "%,d", allRes.size().toInteger())

            def allResultsWithAdditionalInfo = favoritesService.addBookmarkToFavResults(allRes, items, locale)
            allResultsWithAdditionalInfo = favoritesService.addCurrentUserToFavResults(allResultsWithAdditionalInfo, user)

            //Default ordering is newest on top == DESC
            allResultsWithAdditionalInfo.sort{a,b-> b.bookmark.creationDate<=>a.bookmark.creationDate}
            def allResultsOrdered = allResultsWithAdditionalInfo //Used in the send-favorites listing

            def urlsForOrder=[desc:"#",asc:g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:0,rows:rows,order:"asc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])]
            def urlsForOrderTitle=[desc:"#",asc:g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:0,rows:rows,order:"asc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])]
            if (params.order=="asc"){
                if(by.toString()==ORDER_DATE){
                    allResultsWithAdditionalInfo.sort{a,b-> a.bookmark.creationDate<=>b.bookmark.creationDate}
                    urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])
                    urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
                }else{
                    allResultsWithAdditionalInfo=allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}.reverse()
                    urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
                    urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])
                }
            }else{
                //desc
                if(by.toString()==ORDER_TITLE){
                    urlsForOrderTitle["asc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:0,rows:rows,order:"asc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
                    allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}
                }else{
                    //by date
                    urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])
                    urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'publicFavorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
                }
            }

            if (params.offset){
                resultsItems=allResultsWithAdditionalInfo.drop(params.offset.toInteger())
                resultsItems=resultsItems.take( rows)
            }else{
                params.offset=0
                resultsItems=allResultsWithAdditionalInfo.take( rows)
            }

            if (request.method=="POST"){
                sendBookmarkPerMail(params.email,allResultsOrdered)
            }

            render(view: "publicFavorites", model: [
                results: resultsItems,
                selectedFolder: selectedFolder,
                mainFavoriteFolder: null,
                allResultsOrdered: allResultsOrdered,
                viewType: urlQuery["viewType"],
                resultsPaginatorOptions: resultsPaginatorOptions,
                page: page,
                resultsNumber: totalResults,
                createAllFavoritesLink: favoritesService.createAllPublicFavoritesLink(params.offset, params.rows, params.order, lastPgOffset, user.id, selectedFolder.folderId),
                totalPages: totalPages,
                numberOfResultsFormatted: numberOfResultsFormatted,
                offset: params["offset"],
                rows: rows,
                selectedUser: user,
                publicFolders: publicFolders,
                dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                urlsForOrderTitle: urlsForOrderTitle,
                urlsForOrder: urlsForOrder,
                baseDomain: configurationService.getFavoritesBasedomain(),
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
            def user = getUserFromSession()
            //def mainFavoriteFolder = favoritesPageService.getMainFavoritesFolder()
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

            def selectedFolder = bookmarksService.findFolderById(folderId)
            List items = bookmarksService.findBookmarksByFolderId(user.getId(), folderId)

            // If the folder does not exist (maybe deleted) -> redirect to main favorites folder
            if(selectedFolder == null){
                redirect(controller: "user", action: "favorites", id: mainFavoriteFolder.folderId)
                return
            }

            //List items = JSON.parse(result) as List
            def totalResults= items.size()

            def userName = session.getAttribute(User.SESSION_USER).getFirstnameAndLastnameOrNickname()
            def lastPgOffset=0

            def allFoldersInformation = []
            def allFolders = favoritesService.getAllFoldersPerUser(user)
            allFolders.each {
                def container = [:]
                // def String favoritesObject = favoritesPageService.getFavoritesOfFolder(it.folderId)
                // List favoritesOfFolder = JSON.parse(favoritesObject) as List
                List favoritesOfFolder = bookmarksService.findBookmarksByFolderId(user.getId(), it.folderId)

                container["folder"] = it
                container["count"] = favoritesOfFolder.size()
                allFoldersInformation.add(container)
            }
            allFoldersInformation = sortFolders(allFoldersInformation)

            if (totalResults <1){
                render(view: "favorites", model: [
                    selectedFolder: selectedFolder,
                    mainFavoriteFolder: mainFavoriteFolder,
                    resultsNumber: totalResults,
                    allFolders: allFoldersInformation,
                    userName: userName,
                    dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                    createAllFavoritesLink:favoritesService.createAllFavoritesLink(0,0,"desc",0),
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
                def page = ((params.offset.toInteger()/urlQuery["rows"].toInteger())+1).toString()
                def totalPages = (Math.ceil(items.size()/urlQuery["rows"].toInteger()).toInteger())
                def totalPagesFormatted = String.format(locale, "%,d", totalPages.toInteger())
                lastPgOffset=((Math.ceil(items.size()/rows)*rows)-rows).toInteger()

                if (totalPages.toFloat()<page.toFloat()){
                    params.offset= (Math.ceil((items.size()-rows)/10)*10).toInteger()
                    if ((Math.ceil((items.size()-rows)/10)*10).toInteger()<0){
                        lastPgOffset=20
                    }
                    page=totalPages
                }
                def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
                def numberOfResultsFormatted = String.format(locale, "%,d", allRes.size().toInteger())

                def allResultsWithAdditionalInfo = favoritesService.addBookmarkToFavResults(allRes, items, locale)
                allResultsWithAdditionalInfo = favoritesService.addCurrentUserToFavResults(allResultsWithAdditionalInfo, user)

                //Default ordering is newest on top == DESC
                allResultsWithAdditionalInfo.sort{a,b-> b.bookmark.creationDate<=>a.bookmark.creationDate}
                def allResultsOrdered = allResultsWithAdditionalInfo //Used in the send-favorites listing

                def urlsForOrder=[desc:"#",asc:g.createLink(controller:'favorites',action:'favorites',params:[offset:0,rows:rows,order:"asc",by:ORDER_DATE])]
                def urlsForOrderTitle=[desc:"#",asc:g.createLink(controller:'favorites',action:'favorites',params:[offset:0,rows:rows,order:"asc",by:ORDER_TITLE])]
                if (params.order=="asc"){
                    if(by.toString()==ORDER_DATE){
                        allResultsWithAdditionalInfo.sort{a,b-> a.bookmark.creationDate<=>b.bookmark.creationDate}
                        urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_DATE])
                        urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_TITLE])
                    }else{
                        allResultsWithAdditionalInfo=allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}.reverse()
                        urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_TITLE])
                        urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_DATE])
                    }
                }else{
                    //desc
                    if(by.toString()==ORDER_TITLE){
                        urlsForOrderTitle["asc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:0,rows:rows,order:"asc",by:ORDER_TITLE])
                        allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}
                    }else{
                        //by date
                        urlsForOrder["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_DATE])
                        urlsForOrderTitle["desc"]=g.createLink(controller:'favorites',action:'favorites',params:[offset:0,rows:rows,order:"desc",by:ORDER_TITLE])
                    }
                }

                if (params.offset){
                    resultsItems=allResultsWithAdditionalInfo.drop(params.offset.toInteger())
                    resultsItems=resultsItems.take( rows)
                }else{
                    params.offset=0
                    resultsItems=allResultsWithAdditionalInfo.take( rows)
                }

                if (request.method=="POST"){
                    sendBookmarkPerMail(params.email,allResultsOrdered)
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
                    createAllFavoritesLink:favoritesService.createAllFavoritesLink(params.offset, params.rows, params.order, lastPgOffset),
                    totalPages: totalPages,
                    numberOfResultsFormatted: numberOfResultsFormatted,
                    offset: params["offset"],
                    rows: rows,
                    userName: userName,
                    dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'),
                    urlsForOrderTitle:urlsForOrderTitle,
                    urlsForOrder:urlsForOrder
                ])
            }
        } else{
            redirect(controller:"user", action:"index")
        }
    }


    private sendBookmarkPerMail(String paramEmails, List allResultsOrdered) {
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
                    subject g.message(code:"ddbnext.send_favorites_subject_mail")+ getUserFromSession().getFirstnameAndLastnameOrNickname()
                    body( view:"_favoritesEmailBody",
                    model:[results: allResultsOrdered, dateString: g.formatDate(date: new Date(), format: 'dd.MM.yyyy'), userName:getUserFromSession().getFirstnameAndLastnameOrNickname()])
                }
                flash.message = "ddbnext.favorites_email_was_sent_succ"
            } catch (e) {
                log.info "An error occurred sending the email "+ e.getMessage()
                flash.email_error = "ddbnext.favorites_email_was_not_sent_succ"
            }
        }else {
            redirect(controller: "user", action: "index")
        }
    }

    private def sortFolders(allFoldersInformations){
        def out = []
        //Inefficient sort, but we are expecting only very few entries

        //Go over all allFoldersInformations
        for(int i=0; i<allFoldersInformations.size(); i++){
            String insertTitle = allFoldersInformations[i].folder.title.toLowerCase()
            //and find the right place to fit
            if(out.size() == 0){
                out.add(allFoldersInformations[i])
            }else{
                for(int j=0; j<out.size(); j++){
                    String compareTitle = out[j].folder.title.toLowerCase()
                    if(insertTitle.compareTo(compareTitle) < 0){
                        out.add(j, allFoldersInformations[i])
                        break
                    } else if(j == out.size()-1){
                        out.add(allFoldersInformations[i])
                        break
                    }
                }
            }
        }
        // find the "favorites" and put it first
        for(int i=0; i<out.size(); i++){
            String insertTitle = out[i].folder.title
            if(insertTitle == BookmarksService.MAIN_BOOKMARKS_FOLDER){
                def favoritesEntry = out[i]
                out.remove(i)
                out.add(0, favoritesEntry)
                break
            }
        }
        //Check for empty titles
        for(int i=0; i<out.size(); i++){
            if(out[i].folder.title.trim().isEmpty()){
                out[i].folder.title = "-"
            }
        }
        return out
    }


    def addFavorite() {
        log.info "addFavorite " + params.id
        def itemId = params.id
        def result = response.SC_BAD_REQUEST
        def User user = getUserFromSession()
        if (user != null) {
            if (bookmarksService.addBookmark(user.getId(), itemId)) {
                result = response.SC_CREATED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "addFavorite returns " + result
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

    //    def deleteFavorites() {
    //        log.info "deleteFavorites " + request.JSON
    //        def result = response.SC_NOT_FOUND
    //        def User user = getUserFromSession()
    //        if (user != null) {
    //            if(request.JSON == null || request.JSON.ids == null || request.JSON.ids.size() == 0) {
    //                result = response.SC_OK
    //            }else if (bookmarksService.deleteBookmarks(user.getId(), request.JSON)) {
    //                result = response.SC_OK
    //            }
    //        } else {
    //            result = response.SC_UNAUTHORIZED
    //        }
    //        log.info "deleteFavorites returns " + result
    //        render(status: result)
    //    }

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
            def bookmark = bookmarksService.findBookmarksByItemId(user.getId(), params.id)
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
            render(folder as JSON)
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "getFavoriteFolder returns " + result
        render(status: result)
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
            if (bookmarksService.newFolder(user.getId(), title, false, description)) {
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
                    if(it.title == BookmarksService.MAIN_BOOKMARKS_FOLDER){
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
                        bookmarksService.deleteBookmarksByBookmarkIds(user.getId(), bookmarkIds)
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

            if(foldersOwnedByUser){
                //def favoriteIds = bookmarksService.findBookmarkedItems(user.getId(), itemIds)
                folderIds.each { folderId ->
                    favoriteIds.each { favoriteId ->
                        Bookmark favoriteToCopy = bookmarksService.findBookmarkById(favoriteId)
                        String itemId = favoriteToCopy.itemId
                        // Check if the item already exists in the list
                        List favoritesInTargetFolder = bookmarksService.findBookmarksByItemId(user.getId(), itemId, folderId)
                        // if not -> add it
                        if(favoritesInTargetFolder.size() == 0){
                            bookmarksService.saveBookmark(user.getId(), [folderId], itemId, null, Type.CULTURAL_ITEM, favoriteToCopy.creationDate.getTime())
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

        title = sanitizeTextInput(title)
        description = sanitizeTextInput(description)

        def result = response.SC_BAD_REQUEST

        def User user = getUserFromSession()
        if (user != null) {

            def foldersOfUser = bookmarksService.findAllFolders(user.getId())

            // 1) Check if the current user is really the owner of this folder, else deny
            // 2) Check if the folder is a default favorites folder -> if true, deny
            boolean isFolderOfUser = false
            boolean isDefaultFavoritesFolder = false
            foldersOfUser.each {
                if(it.folderId == id){
                    isFolderOfUser = true
                    if(it.title == BookmarksService.MAIN_BOOKMARKS_FOLDER){
                        isDefaultFavoritesFolder = true
                    }
                }
            }
            if(isFolderOfUser && !isDefaultFavoritesFolder){
                bookmarksService.updateFolder(id, title, description, true)
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
        if (user != null) {

            // 1) Check if the current user is really the owner of this bookmark, else deny
            Bookmark bookmark = bookmarksService.findBookmarkById(id)
            boolean isBookmarkOfUser = false
            if(bookmark.userId == user.getId()){
                isBookmarkOfUser = true
            }
            if(isBookmarkOfUser){
                bookmarksService.updateBookmark(id, cleanedText)
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
            if(isFolderOfUser){
                bookmarksService.updateFolder(folder.folderId, folder.title, folder.description, !folder.isPublic)
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



}