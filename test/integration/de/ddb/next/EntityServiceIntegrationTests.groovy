package de.ddb.next

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin

import org.junit.*

@TestMixin(ControllerUnitTestMixin)
class EntityServiceIntegrationTests {

    def entityService

    @Test void doEntitySearch() {
        def result = entityService.doEntitySearch(["query":"Walther Klemm"])
        log.info result
        assertNotNull result
    }

    @Test void getEntityDetails() {
        assertNotNull entityService.getEntityDetails("118563165")
    }
}
