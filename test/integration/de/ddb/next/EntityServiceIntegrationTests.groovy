package de.ddb.next

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin

import org.junit.*


@TestMixin(IntegrationTestMixin)
class EntityServiceIntegrationTests {

    def entityService

    @Test void shouldGetResultCountsForFacetType() {
        def entityTitle = 'Johann Wolfgang von Goethe'
        def facetType = 'mediatype_002'

        //TODO to execute the tests a running server under this url ddb.apis.url is needed
        //def numberOfPictures = entityService.getResultCountsForFacetType(entityTitle, facetType)
    }

    @Test void shouldDoAffiliateFacetInvolvedSearch() {
        def entityTitle = 'Armin Scholl'
        def entityId = '115843523'
        def facetName = 'affiliate_fct_involved'

        //TODO to execute the tests a running server under this url ddb.apis.url is needed
        //def model = entityService.doFacetSearch(entityTitle, 0, 4, false, facetName, entityId)
    }

    @Test void getEntityDetails() {
        assertNotNull entityService.getEntityDetails("118540238")
    }
}
