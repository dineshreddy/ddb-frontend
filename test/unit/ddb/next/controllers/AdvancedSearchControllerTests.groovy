package ddb.next.controllers


import grails.test.mixin.*
import org.junit.*

import de.ddb.next.AdvancedsearchController;

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(AdvancedsearchController)
class AdvancedSearchControllerTests {

    void testFillValues() {
        controller.fillValues()

        // Check if response status is 200
        assert response.status == 200

        // Check if the controller shows the correct view
        //assert view == "/search/advancedsearch"

    }

    void testExecuteSearch() {
        println "AdvancedSearchControllerTests.testExecuteSearch(): todo"
        assert true
    }
}
