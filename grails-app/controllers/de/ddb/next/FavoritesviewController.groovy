package de.ddb.next

import java.text.Collator

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.next.beans.Folder
import de.ddb.next.beans.User
import de.ddb.next.constants.FolderConstants
import de.ddb.next.constants.SupportedLocales
import de.ddb.next.exception.FavoritelistNotFoundException

class FavoritesviewController {

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
			redirect(controller: "favoritesview", action: "publicFavorites", params: [userId: user.id, folderId: folderId])
			return
		}

		if(params.blockingToken) {
			blockFavoritesList(user.id, folderId, params.blockingToken)
			redirect(controller: "favoritesview", action: "publicFavorites", params: [userId: user.id, folderId: folderId])
			return
		}

		if(params.unblockingToken) {
			unblockFavoritesList(user.id, folderId, params.unblockingToken)
			redirect(controller: "favoritesview", action: "publicFavorites", params: [userId: user.id, folderId: folderId])
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

			def fullPublicLink = g.createLink(controller: "favoritesview", action: "publicFavorites", params: [userId: user.getId(), folderId: folderId])

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

			def urlsForOrder=[desc:"#",asc:g.createLink(controller:'favoritesview',action:'publicFavorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])]
			def urlsForOrderTitle=[desc:"#",asc:g.createLink(controller:'favoritesview',action:'publicFavorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])]
			if (order=="asc"){
				if(by.toString()==ORDER_DATE){
					allResultsWithAdditionalInfo.sort{ a, b ->
						a.bookmark.creationDate.time <=> b.bookmark.creationDate.time
					}
					urlsForOrder["desc"]=g.createLink(controller:'favoritesview',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])
					urlsForOrderTitle["desc"]=g.createLink(controller:'favoritesview',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
				}else{
					allResultsWithAdditionalInfo=allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}.reverse()
					urlsForOrderTitle["desc"]=g.createLink(controller:'favoritesview',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
					urlsForOrder["desc"]=g.createLink(controller:'favoritesview',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])
				}
			}else{
				//desc
				if(by.toString()==ORDER_TITLE){
					urlsForOrderTitle["asc"]=g.createLink(controller:'favoritesview',action:'publicFavorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
					urlsForOrder["asc"]=g.createLink(controller:'favoritesview',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])
					allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}
				}else{
					//by date
					urlsForOrder["desc"]=g.createLink(controller:'favoritesview',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,userId:user.id,folderId:selectedFolder.folderId])
					urlsForOrderTitle["desc"]=g.createLink(controller:'favoritesview',action:'publicFavorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,userId:user.id,folderId:selectedFolder.folderId])
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

			def fullPublicLink = g.createLink(controller: "favoritesview", action: "publicFavorites", params: [userId: user.getId(), folderId: folderId])

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
		if(favoritesService.isUserLoggedIn()){
			def rows=20 //default
			if (params.rows){
				rows = params.rows.toInteger()
			}
			def offset = 0 // default
			if(params.offset){
				offset = params.offset.toInteger()
			}
			def user = favoritesService.getUserFromSession()
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
				redirect(controller: "favoritesview", action: "favorites", id: mainFavoriteFolder.folderId)
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

			def fullPublicLink = g.createLink(controller: "favoritesview", action: "publicFavorites", params: [userId: user.getId(), folderId: folderId])

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

				def urlsForOrder=[desc:"#",asc:g.createLink(controller:'favoritesview',action:'favorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_DATE,id:folderId])]
				def urlsForOrderTitle=[desc:"#",asc:g.createLink(controller:'favoritesview',action:'favorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_TITLE,id:folderId])]
				if (order=="asc"){
					if(by.toString()==ORDER_DATE){
						allResultsWithAdditionalInfo.sort{ a, b->
							a.bookmark.creationDate.time <=> b.bookmark.creationDate.time
						}
						urlsForOrder["desc"]=g.createLink(controller:'favoritesview',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,id:folderId])
						urlsForOrderTitle["desc"]=g.createLink(controller:'favoritesview',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,id:folderId])
					}else{
						allResultsWithAdditionalInfo=allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}.reverse()
						urlsForOrderTitle["desc"]=g.createLink(controller:'favoritesview',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,id:folderId])
						urlsForOrder["desc"]=g.createLink(controller:'favoritesview',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,id:folderId])
					}
				}else{
					//desc
					if(by.toString()==ORDER_TITLE){
						urlsForOrderTitle["asc"]=g.createLink(controller:'favoritesview',action:'favorites',params:[offset:offset,rows:rows,order:"asc",by:ORDER_TITLE,id:folderId])
						urlsForOrder["asc"]=g.createLink(controller:'favoritesview',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,id:folderId])
						allResultsWithAdditionalInfo.sort{it.label.toLowerCase()}
					}else{
						//by date
						urlsForOrder["desc"]=g.createLink(controller:'favoritesview',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_DATE,id:folderId])
						urlsForOrderTitle["desc"]=g.createLink(controller:'favoritesview',action:'favorites',params:[offset:offset,rows:rows,order:"desc",by:ORDER_TITLE,id:folderId])
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

    private def isMainBookmarkFolder(folder) {
        return folder.title == FolderConstants.MAIN_BOOKMARKS_FOLDER.value
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

	private Locale getLocale() {
		return SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
	}

}