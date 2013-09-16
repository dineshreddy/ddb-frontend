package de.ddb.next

import de.ddb.next.beans.SavedSearch
import de.ddb.next.beans.SearchQuery
import de.ddb.next.beans.User
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.*

class SavedSearchesService {
    def transactional = false
    def bookmarksService
    def sessionService
    def grailsApplication

    /**
     * Get all saved searches from bookmark service.
     *
     * @return list of SavedSearch objects
     */
    def Collection<SavedSearch> getSavedSearches() {
        def result = []
        def User user = getUserFromSession()

        if (user != null) {
            // def savedSearches = bookmarksService.findFavoritesByUserId(user.getId()) as JSON
            for (int index = 1; index <= 30; index++) {
                result += new SavedSearch(String.valueOf(index), "goethe " + index, new SearchQuery("query=goethe+weimar&facetValues%5B%5D=type_fct%3Dmediatype_002&facetValues%5B%5D=affiliate_fct%3DRietschel%2C+Ernst&offset=0"), new Date())
            }
        }
        return result
    }

    /**
     * Return a subset of the given list of saved searches which represents the contents of the current page.
     *
     * @return list of SavedSearch objects
     */
    def Collection<SavedSearch> pageSavedSearches(Collection<SavedSearch> savedSearches, int offset, int rows) {
        def result = []

        if (offset >= 0 && savedSearches.size() > 0) {
            def endIndex = offset + rows - 1

            if (endIndex >= savedSearches.size()) {
                endIndex = -1
            }
            result = savedSearches[offset..endIndex]
        }
        return result
    }

    def private User getUserFromSession() {
        return sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
    }

    def getPaginationUrls(int offset, int rows, String order, int totalPages) {
        def lastPageOffset = (totalPages - 1) * rows
        def first = getPaginationUrl(0, rows, order)
        def last = getPaginationUrl(lastPageOffset, rows, order)

        if (offset < rows) {
            first = null
        }
        if (offset >= lastPageOffset) {
            last = null
        }
        return [
            firstPg: first,
            prevPg: getPaginationUrl(offset - rows, rows, order),
            nextPg: getPaginationUrl(offset + rows, rows, order),
            lastPg: last
        ]
    }

    def private String getPaginationUrl(int offset, int rows, String order) {
        def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')

        return g.createLink(controller:'user', action: 'savedsearches',
        params: [offset: offset, rows: rows, order: order])
    }
}
