package de.ddb.next

class ReturnSearchUrlTagLib {
    static namespace = "ddb"
    static defaultEncodeAs = 'html'

    def getSearchUrl={attrs, body ->
        def url =g.createLink(controller:'search', action:'results')
        if ((attrs.controllerName=="search" && attrs.actionName=="institution") ||(attrs.controllerName=="entity" && attrs.actionName=="personsearch")) {
            url=g.createLink(controller:attrs.controllerName, action:attrs.actionName)
        } 
        out << url.toString()
    }
}
