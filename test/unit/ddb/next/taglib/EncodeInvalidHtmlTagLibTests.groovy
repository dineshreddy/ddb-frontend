package ddb.next.taglib

import grails.test.mixin.TestFor

import org.junit.Test

import de.ddb.next.EncodeInvalidHtmlTagLib

@TestFor(EncodeInvalidHtmlTagLib)
class EncodeInvalidHtmlTagLibTests {

    @Test
    void brShouldNotbeEncoded() {
        String text = "before<br /><Izchak><br />after"
        String result = applyTemplate('<ddb:encodeInvalidHtml text="${text}"/>', [text : text]).trim()

//        assert result.equals(text)
    }
}