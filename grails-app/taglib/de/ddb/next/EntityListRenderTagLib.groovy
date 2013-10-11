package de.ddb.next

class EntityListRenderTagLib {

    def entityListRender = { attrs, body ->
        out << render(template:"/search/entityList", model:[:])
    }
}
