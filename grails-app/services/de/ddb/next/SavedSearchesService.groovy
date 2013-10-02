package de.ddb.next

import net.sf.json.JSONNull

import org.codehaus.groovy.grails.web.json.*

import de.ddb.next.beans.SavedSearch
import de.ddb.next.beans.User

class SavedSearchesService {
    def transactional = false
    def savedSearchService
    def sessionService
    def grailsApplication

    def boolean addSavedSearch(String userId, String title, String queryString) {
        return savedSearchService.saveSearch(userId, reviseQueryString(queryString), title)
    }

    def boolean deleteSavedSearches(ids) {
        def result = false
        if(ids?.size() > 0) {
            result = savedSearchService.deleteSavedSearch(ids)
        }
        return result
    }

    /**
     * Get all saved searches from saved search service. This method is called from profile.gsp.
     *
     * @return list of SavedSearch objects
     */
    def getSavedSearches() {
        def User user = sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
        if (user != null) {
            return getSavedSearches(user.getId())
        }
    }

    /**
     * Get all saved searches from saved search service.
     *
     * @return list of SavedSearch objects
     */
    def Collection<SavedSearch> getSavedSearches(String userId) {
        def result = []
        def savedSearches = savedSearchService.findSavedSearchByUserId(userId)
        savedSearches.each { savedSearch ->
            result.add(new SavedSearch(savedSearch.id,
                    savedSearch.title.class != JSONNull ? savedSearch.title : "",
                    savedSearch.queryString,
                    new Date(savedSearch.createdAt)))
        }
        return result
    }

    /**
     * Check if a query is a saved search for the user.
     */
    def boolean isSavedSearch(String userId, String queryString) {
        def revisedQueryString = reviseQueryString(queryString)
        def result = savedSearchService.findSavedSearchByQueryString(userId, revisedQueryString)
        boolean isSavedSearch = false
        result.each {
            if(it.queryString == revisedQueryString) {
                isSavedSearch = true
            }
        }
        return isSavedSearch
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

    private def String getPaginationUrl(int offset, int rows, String order) {
        def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
        return g.createLink(controller:'user', action: 'savedsearches',
        params: [offset: offset, rows: rows, order: order])
    }

    /**
     * Extract the relevant parameters from the given query string. Remove paging parameters.
     *
     * @param queryString complete query string
     *
     * @return revised query string
     */
    private def String reviseQueryString(String queryString) {
        def result = ""
        def parameters = queryString.split("&").sort()
        parameters.each { parameter ->
            if (parameter.startsWith("query=") || parameter.startsWith("facetValues")) {
                if (result.size() > 0) {
                    result += "&"
                }
                result += parameter
            }
        }
        return result
    }

    def boolean updateSavedSearch(String id, String title) {
        return savedSearchService.updateSearch(id, title)
    }
}