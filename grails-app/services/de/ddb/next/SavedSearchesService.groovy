package de.ddb.next

import de.ddb.next.beans.SavedSearch
import de.ddb.next.beans.User
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.*

class SavedSearchesService {
    def transactional = false
    def savedSearchService
    def sessionService
    def grailsApplication

    def boolean addSavedSearch(String userId, String title, String query) {
        return savedSearchService.saveSearch(userId, query, title)
    }

    def boolean deleteSavedSearches(String userId, ids) {
        def result = false

        if(ids?.size() > 0) {
            result = savedSearchService.deleteSavedSearch(userId, ids)
        }
        return result
    }

    /**
     * Get all saved searches from saved search service.
     *
     * @return list of SavedSearch objects
     */
    def Collection<SavedSearch> getSavedSearches(String userId) {
        def result = []

        // def savedSearches = savedSearchService.findSavedSearchByUserId(userId)
        for (int index = 1; index <= 30; index++) {
            result += new SavedSearch(String.valueOf(index), "goethe " + index, "query=goethe+weimar&facetValues%5B%5D=affiliate_fct%3DGoethe%2C+Johann+Wolfgang+von&facetValues%5B%5D=affiliate_fct%3DGerig%2C+Uwe+(Fotograf)&facetValues%5B%5D=type_fct%3Dmediatype_002&facetValues%5B%5D=keywords_fct%3DFotos&facetValues%5B%5D=time_fct%3Dtime_62100&facetValues%5B%5D=time_fct%3Dtime_62110&offset=10&rows=10", new Date())
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
