package de.ddb.next

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin

import org.junit.Test

@TestMixin(ControllerUnitTestMixin)
class NewsletterServiceIntegrationTests {
    private static String EMAIL = 'john.doe@example.com'

    def newsletterService

    void tearDown() {
        newsletterService.removeSubscriber(EMAIL)
    }

    @Test
    void shouldAddUserAsSubscriber() {
        newsletterService.addSubscriber(EMAIL)
        assert newsletterService.isSubscriber(EMAIL)
    }

    @Test
    void shouldRemoveUserAsSubscriber() {
        newsletterService.addSubscriber(EMAIL)
        assert newsletterService.isSubscriber(EMAIL)

        newsletterService.removeSubscriber(EMAIL)
        assert !newsletterService.isSubscriber(EMAIL)
    }

    @Test
    void shouldReturnFalseIfUserIsNotSubscriber() {
        newsletterService.removeSubscriber(EMAIL)
        assert !newsletterService.isSubscriber(EMAIL)
    }
}
