package de.ddb.next

class RemoveUrlProtocolTagLib {
    static namespace = "ddb"
    /**
     * @author ttr
     * Removing the protocol part from an url string
     */
    def removeUrlProtocol = { attrs, body ->
        def url = attrs.url
        out << url.replaceFirst("^(http://|https://)","")
    }
}

