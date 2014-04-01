package de.ddb.next

import de.ddb.next.constants.SearchParamEnum

class PersonController {

    def searchService

    def search() {

        def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
        def results = []
        def model = [title: urlQuery[SearchParamEnum.QUERY.getName()], facets:[], results: results]

        render(view: "searchPerson", model: model)

    }

}
