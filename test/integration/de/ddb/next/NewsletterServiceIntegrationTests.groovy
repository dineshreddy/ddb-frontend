package de.ddb.next

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin

import org.junit.Test

import de.ddb.common.exception.ItemNotFoundException

@TestMixin(ControllerUnitTestMixin)
class NewsletterServiceIntegrationTests {
    private static String EMAIL = 'john.doe@example.com'

    def newsletterService

    void tearDown() {
        try {
            newsletterService.unsubscribe(EMAIL)
        }
        catch (ItemNotFoundException e) {
        }
    }

    @Test
    void shouldAddUserAsSubscriber() {
        newsletterService.subscribe(EMAIL)
        assert newsletterService.isSubscribed(EMAIL)
    }

    @Test
    void shouldRemoveUserAsSubscriber() {
        newsletterService.subscribe(EMAIL)
        assert newsletterService.isSubscribed(EMAIL)

        newsletterService.unsubscribe(EMAIL)
        assert !newsletterService.isSubscribed(EMAIL)
    }

    @Test(expected= ItemNotFoundException.class)
    void shouldReturnFalseIfUserIsNotSubscriber() {
        newsletterService.unsubscribe(EMAIL)
    }
}
