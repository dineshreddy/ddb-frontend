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
        savedSearchService.deleteSavedSearch(
                [
                    savedSearchId
                ])
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
        savedSearchService.deleteSavedSearch(
                [
                    savedSearchId
                ])
        log.info "The user ${userId} just saved search with the query string: ${queryString}, savedSearchId ${savedSearchId}"
    }

    @Test
    void shouldFindAllSavedSearchesByUserId() {
        log.info "should find all saved searches by user ID"

        def userId = UUID.randomUUID() as String
        
        def queryStringForGoethe = 'query=goethe&sort=ALPHA_ASC&facetValues[]=time_fct%3Dtime_62000&facetValues[]=time_fct%3Dtime_61600&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_02'
        def goetheSavedSearchId = savedSearchService.saveSearch(userId, queryStringForGoethe , 'Goethe Related', 'All things related to Goethe')
        assert goetheSavedSearchId != null

        def queryStringForMozart = 'query=mozart&sort=ALPHA_ASC&facetValues[]=time_fct%3Dtime_62000&facetValues[]=time_fct%3Dtime_61600&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_02'
        def mozartSavedSearchId = savedSearchService.saveSearch(userId, queryStringForMozart , 'Mozart Related')
        assert mozartSavedSearchId != null

        def results = savedSearchService.findSavedSearchByUserId(userId)
        assert results.size() == 2
        savedSearchService.deleteSavedSearch(
                [
                    goetheSavedSearchId,
                    mozartSavedSearchId
                ])
    }

    @Test
    void shouldDeleteSavedSearches() {
        log.info "should delete saved search by IDs"


        def userId = UUID.randomUUID() as String
        def queryStringForGoethe = 'query=goethe&sort=ALPHA_ASC&facetValues[]=time_fct%3Dtime_62000&facetValues[]=time_fct%3Dtime_61600&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_02'
        def goetheSavedSearchId = savedSearchService.saveSearch(userId, queryStringForGoethe , 'Goethe Related', 'All things related to Goethe')
        assert goetheSavedSearchId != null

        def queryStringForMozart = 'query=mozart&sort=ALPHA_ASC&facetValues[]=time_fct%3Dtime_62000&facetValues[]=time_fct%3Dtime_61600&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_02'
        def mozartSavedSearchId = savedSearchService.saveSearch(userId, queryStringForMozart , 'Mozart Related')
        assert mozartSavedSearchId != null


        def results = savedSearchService.findSavedSearchByUserId(userId)
        assert results.size() == 2

        savedSearchService.deleteSavedSearch(
                [
                    goetheSavedSearchId,
                    mozartSavedSearchId
                ])

        assert savedSearchService.findSavedSearchByUserId(userId).size() == 0
    }

    // TODO: shouldUpdateSavedSearch
}
