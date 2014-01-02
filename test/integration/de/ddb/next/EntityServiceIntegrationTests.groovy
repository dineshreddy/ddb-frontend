package de.ddb.next

import static org.junit.Assert.*

import org.junit.*

class EntityServiceIntegrationTests extends GroovyTestCase {

    private static final def SIZE = 99999

    def entityService

    @Test void shouldDoAffiliateFacetInvolvedSearch() {
        String entityTitle = "Armin Scholl"
        String entityId = "115843523"
        String facetName = "affiliate_fct_involved"


        def model = entityService.doFacetSearch(entityTitle, 0, 4, false, facetName, entityId)
    }
}
