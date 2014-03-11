package de.ddb.next

import static org.junit.Assert.*

import org.junit.*

class FacetsServiceIntegrationTests extends GroovyTestCase {

    def facetsService

    @Test
    void getAllFacetsTest() {
        def allFacets = facetsService.getAllFacets()

        assert allFacets.size() > 0
    }
}
