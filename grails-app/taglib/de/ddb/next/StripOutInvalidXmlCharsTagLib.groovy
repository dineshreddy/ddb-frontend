package de.ddb.next
import java.util.regex.Pattern

class StripOutInvalidXmlCharsTagLib {
    static namespace = "ddb"

    def stripOutInvalidXmlChars = { attrs, body ->
        def text = attrs.text
        println text 
        
        Pattern AMERSAND = Pattern.compile("&", Pattern.LITERAL)
        Pattern TAGOPEN = Pattern.compile("<", Pattern.LITERAL)
        Pattern TAGCLOSE = Pattern.compile(">", Pattern.LITERAL)
        Pattern WRONGCLOSETAGA = Pattern.compile("<a/>", Pattern.LITERAL)

        text = AMERSAND.matcher(text.toString()).replaceAll("&amp;");
        text = TAGOPEN.matcher(text.toString()).replaceAll("&lt;");
        text = TAGCLOSE.matcher(text.toString()).replaceAll("&gt;");
        text = WRONGCLOSETAGA.matcher(text.toString()).replaceAll("</a>");
        out << text.toString()
    }
}
