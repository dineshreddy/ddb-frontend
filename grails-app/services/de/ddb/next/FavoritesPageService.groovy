package de.ddb.next
import de.ddb.next.beans.Bookmark
import de.ddb.next.beans.Folder
import de.ddb.next.beans.User
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.List
import java.util.Locale

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.servlet.support.RequestContextUtils
import org.springframework.web.context.request.RequestContextHolder

class FavoritesPageService {

    def transactional = false
    def bookmarksService
    def sessionService
    def grailsApplication
    def searchService
    def configurationService
    def messageSource

    def getFavorites() {
        def User user = getUserFromSession()
        if (user != null) {
            def result = bookmarksService.findFavoritesByUserId(user.getId())
            return result as JSON
        } else {
            log.info "getFavorites returns " + response.SC_UNAUTHORIZED
            return null
        }
    }

    def getFavoritesOfFolder(folderId) {
        def User user = getUserFromSession()
        if (user != null) {
            def result = bookmarksService.findBookmarksByFolderId(user.getId(), folderId)
            return result as JSON
        } else {
            log.info "getFavorites returns null"
            return null
        }
    }

    def getMainFavoritesFolder() {
        Folder folder = null
        def User user = getUserFromSession()
        if (user != null) {
            def result = bookmarksService.findAllFolders(user.getId())
            result.each {
                if(it.title == BookmarksService.FAVORITES){
                    folder = it
                }
            }
        }
        log.info "getMainFavoritesFolder returns " +folder
        return folder
    }

    private User getUserFromSession() {
        return sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
    }

    def private createAllFavoritesLink(Integer offset, Integer rows, String order, Integer lastPgOffset){
        def first = createFavoritesLinkNavigation(0, rows, order)
        if (offset < rows){
            first = null
        }
        def last = createFavoritesLinkNavigation(lastPgOffset, rows, order)
        if (offset >= lastPgOffset){
            last = null
        }
        return [
            firstPg: first,
            prevPg: createFavoritesLinkNavigation(offset.toInteger()-rows, rows, order),
            nextPg: createFavoritesLinkNavigation(offset.toInteger()+rows, rows, order),
            lastPg: last
        ]
    }
    def private createFavoritesLinkNavigation(offset,rows,order){
        def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
        return g.createLink(controller:'user', action: 'favorites',params:[offset:offset,rows:rows,order:order])
    }

    def private createAllPublicFavoritesLink(Integer offset, Integer rows, String order, Integer lastPgOffset, String userId, String folderId){
        def first = createPublicFavoritesLinkNavigation(0, rows, order, userId, folderId)
        if (offset < rows){
            first = null
        }
        def last = createPublicFavoritesLinkNavigation(lastPgOffset, rows, order, userId, folderId)
        if (offset >= lastPgOffset){
            last = null
        }
        return [
            firstPg: first,
            prevPg: createPublicFavoritesLinkNavigation(offset.toInteger()-rows, rows, order, userId, folderId),
            nextPg: createPublicFavoritesLinkNavigation(offset.toInteger()+rows, rows, order, userId, folderId),
            lastPg: last
        ]
    }
    def private createPublicFavoritesLinkNavigation(Integer offset, Integer rows, String order, String userId, String folderId){
        def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
        return g.createLink(controller:'user', action: 'publicFavorites', params:[userId: userId, folderId: folderId, offset:offset, rows:rows, order:order])
    }

    /**
     * Retrieve from Backend the Metadata for the items retrieved from the favorites list
     * @param items
     * @return
     */
    def private retriveItemMD(List items, Locale locale){
        def totalResults= items.size()
        def step = 20
        def queryItems
        def orQuery=""
        def allRes = []

        items.eachWithIndex() { it, i ->
            if ( (i==0) || ( ((i>1)&&(i-1)%step==0)) ){
                orQuery=it.itemId
            }else if (i%step==0){
                orQuery=orQuery + " OR "+ it.itemId
                queryBackend(orQuery, locale).each { item ->
                    allRes.add(item)
                }
                orQuery=""
            }else{
                orQuery+=" OR "+ it.itemId
            }
        }
        if (orQuery){
            queryBackend(orQuery,locale).each { item ->
                allRes.add(item)
            }
        }

        // Add empty items for all orphaned elasticsearch bookmarks
        if(items.size() > allRes.size()){
            def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
            def dummyThumbnail = g.resource("dir": "images", "file": "/placeholder/search_result_media_unknown.png").toString()
            def label = messageSource.getMessage("ddbnext.Item_No_Longer_Exists", null, LocaleContextHolder.getLocale())

            def foundItemIds = allRes.collect{ it.id }
            items.each{
                // item not found
                if(!(it.itemId in foundItemIds)){

                    def emptyDummyItem = [:]
                    emptyDummyItem["id"] = it.itemId
                    emptyDummyItem["view"] = []
                    emptyDummyItem["label"] = label
                    emptyDummyItem["latitude"] = ""
                    emptyDummyItem["longitude"] = ""
                    emptyDummyItem["category"] = "orphaned"
                    emptyDummyItem["preview"] = [:]
                    emptyDummyItem["preview"]["title"] = label
                    emptyDummyItem["preview"]["subtitle"] = ""
                    emptyDummyItem["preview"]["media"] = ["unknown"]
                    emptyDummyItem["preview"]["thumbnail"] = dummyThumbnail
                    allRes.add(emptyDummyItem)
                }
            }
        }

        return allRes
    }

    def private queryBackend(String query, Locale locale){
        def params = RequestContextHolder.currentRequestAttributes().params
        params.query = "id:("+query+")"

        def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
        urlQuery["offset"]=0
        urlQuery["rows"]=21
        def apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, urlQuery)
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(request)
        }
        def resultsItems = apiResponse.getResponse()

        //Replacing the mediatype images when not coming from backend server
        resultsItems = searchService.checkAndReplaceMediaTypeImages(resultsItems)

        return resultsItems["results"]["docs"]
    }

    def private getAllFoldersPerUser(){
        def User user = getUserFromSession()
        if (user != null) {
            return bookmarksService.findAllFolders(user.getId())
        }
        else {
            log.info "getFavorites returns " + response.SC_UNAUTHORIZED
            return null
        }
    }

    private List addBookmarkToFavResults(allRes, List items, Locale locale) {
        def all = []
        def temp = []
        allRes.each { searchItem->
            temp = []
            temp = searchItem
            for(int i=0; i<items.size(); i++){
                if(items.get(i).itemId == searchItem.id){
                    temp["bookmark"] = items.get(i).getAsMap()
                    temp["bookmark"]["creationDateFormatted"] = formatDate(items.get(i).creationDate, locale)
                    temp["bookmark"]["updateDateFormatted"] = formatDate(items.get(i).updateDate, locale)
                    break
                }
            }
            all.add(temp)
        }
        return all
    }

    private String formatDate(Date oldDate, Locale locale) {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd.MM.yyy HH:mm")
        newFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"))
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
        return newFormat.format(oldDate)
    }

    private List addCurrentUserToFavResults(allRes, User user) {
        def userJson = [:]
        userJson["id"] = user.id
        userJson["username"] = user.username
        userJson["status"] = user.status
        userJson["firstname"] = user.firstname
        userJson["lastname"] = user.lastname
        userJson["email"] = user.email

        allRes.each { searchItem ->
            searchItem["user"] = userJson
        }
        return allRes
    }

}
