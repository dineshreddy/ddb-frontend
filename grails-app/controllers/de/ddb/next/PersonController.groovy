package de.ddb.next

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.next.constants.SearchParamEnum
import de.ddb.next.constants.SupportedLocales

class PersonController {

    def searchService

    def search() {

        def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
        def results = []
        def correctedQuery = ""
        def entities = ""
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        //Calculating results pagination (previous page, next page, first page, and last page)
        def totalPages = 0 //(Math.ceil(resultsItems.numberOfResults/urlQuery[SearchParamEnum.ROWS.getName()].toInteger()).toInteger())
        def totalPagesFormatted = String.format(locale, "%,d", totalPages.toInteger())
        def model = [title: urlQuery[SearchParamEnum.QUERY.getName()], facets:[], viewType: "list", results: results, correctedQuery: correctedQuery, totalPages: totalPagesFormatted]

        render(view: "searchPerson", model: model)

    }

}
