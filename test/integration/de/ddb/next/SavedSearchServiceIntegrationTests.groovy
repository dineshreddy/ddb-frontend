package de.ddb.next

import static org.junit.Assert.*

import org.junit.*

class SavedSearchServiceIntegrationTests extends GroovyTestCase {

    def savedSearchService

    @Test
    void shouldSavedUserSearch() {
        log.info "should saved user search"

        def userId = UUID.randomUUID() as String
        def queryString = 'query=goethe&sort=ALPHA_ASC&facetValues[]=time_fct%3Dtime_62000&facetValues[]=time_fct%3Dtime_61600&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_02'

        def savedSearchId = savedSearchService.saveSearch(userId, queryString)
        log.info "id: ${savedSearchId}"
        assert savedSearchId  != null

        log.info "The user ${userId} just saved search with the query string: ${queryString}, savedSearchId ${savedSearchId}"
    }

    @Test
    void shouldSavedUserSearchWithTitleAndDescription() {
        log.info "should saved user search"

        def userId = UUID.randomUUID() as String
        def queryString = 'query=goethe&sort=ALPHA_ASC&facetValues[]=time_fct%3Dtime_62000&facetValues[]=time_fct%3Dtime_61600&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_02'

        def savedSearchId = savedSearchService.saveSearch(userId, queryString, 'Goethe Related', 'All things related to Goethe')
        log.info "id: ${savedSearchId}"
        assert savedSearchId  != null

        log.info "The user ${userId} just saved search with the query string: ${queryString}, savedSearchId ${savedSearchId}"
    }

    @Test
    void shouldRetrieveSavedSearch() {
        log.info "should retrieve saved user search"

        def userId = UUID.randomUUID() as String
        def queryString = 'query=goethe&sort=ALPHA_ASC&facetValues[]=time_fct%3Dtime_62000&facetValues[]=time_fct%3Dtime_61600&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_02'

        def savedSearchId = savedSearchService.saveSearch(userId, queryString, 'Goethe Related', 'All things related to Goethe')
        assert savedSearchId  != null

        log.info "The user ${userId} just saved search with the query string: ${queryString}, savedSearchId ${savedSearchId}"
    }
}
