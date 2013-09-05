package de.ddb.next

import static org.junit.Assert.*
import groovy.util.GroovyTestCase

import org.junit.*

import de.ddb.next.NewsletterService
import de.ddb.next.beans.User

class SavedSearchServiceIntegrationTests extends GroovyTestCase {

    def savedSearchService

    @Test
    void shouldSavedUserSearch() {
        log.info "should saved user search"
        // should add a cultural item to user's favorite list.
        def userId = UUID.randomUUID() as String
        /*
         def queryString =  '''query=goethe&
         sort=ALPHA_ASC&
         facetValues[]=time_fct%3Dtime_62000&
         facetValues[]=time_fct%3Dtime_61600&
         facetValues[]=keywords_fct%3DFotos&
         facetValues[]=type_fct%3Dmediatype_002&
         facetValues[]=sector_fct%3Dsec_02'''
         */

        def queryString =  ''

        // if the user don't have a favorite list, then the service should create it.
        def savedSearchId = savedSearchService.saveSearch(userId, queryString)
        assert savedSearchId  != null

        log.info "The user ${userId} just saved search with the query string: ${queryString}"
    }
}
