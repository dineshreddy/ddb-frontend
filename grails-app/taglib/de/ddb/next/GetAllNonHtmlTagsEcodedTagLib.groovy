package de.ddb.next

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Entities
/**
 * 
 */
class GetAllNonHtmlTagsEcodedTagLib {
    static namespace = "ddb"

    def encodeNonHtmlTags = { attrs, body ->
        def text = attrs.text

        println "orig: " + text

        //def cleaned = Jsoup.clean(text.toString(), Whitelist.none())
        Document doc = Jsoup.parse(text.toString());
        doc.outputSettings().escapeMode(Entities.EscapeMode.base).prettyPrint(false)


        println "cleaned: " + doc.body().text()

        out << text
    }
}
