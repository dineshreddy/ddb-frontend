package de.ddb.next

import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

/**
 * @author boz 
 */
class EncodeInvalidHtmlTagLib {
    static namespace = "ddb"

    /**
     * Html encode all invalid text
     */
    def encodeInvalidHtml = { attrs, body ->
        def text = attrs.text
        def cleaned = text

        if (! Jsoup.isValid(text.toString(), Whitelist.basic())) {
            cleaned = text.encodeAsHTML()
        }

        out << cleaned
    }
}
