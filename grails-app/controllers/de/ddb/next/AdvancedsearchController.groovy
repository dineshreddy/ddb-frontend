/*
 * Copyright (C) 2014 FIZ Karlsruhe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ddb.next

import de.ddb.common.constants.SearchParamEnum



/**
 * Controller for Advanced Search
 * 
 * @author mih
 *
 */
class AdvancedsearchController {

    //The factes for the advanced search
    def allowedFacets = [
        "search_all",
        "title",
        "description",
        "place",
        "affiliate",
        "keywords",
        "language",
        "type",
        "sector",
        "provider",
        "license_group"
    ]

    private static final String enumSearchType = "ENUM"
    private static final String textSearchType = "TEXT"
    private static final String languageTagPrefix = "ddbnext.facet_"
    private static final String facetNameSuffix = "_fct"
    private static final String labelSortType = "ALPHA_LABEL"

    static defaultAction = "fillValues"

    def messageSource
    def configurationService
    def facetsService


    /**
     * render advanced search form
     * 
     * @return
     */
    def fillValues() {
        int searchGroupCount = configurationService.getSearchGroupCount()
        int searchFieldCount = configurationService.getSearchFieldCount()
        List facetSearchfields = facetsService.getAllFacets()
        facetSearchfields = filterOnlyAdvancedSearchFacets(facetSearchfields)
        Map facetValuesMap = getFacetValues(facetSearchfields)

        render(view: "/search/advancedsearch", model: [searchGroupCount: searchGroupCount,
            searchFieldCount: searchFieldCount,
            facetSearchfields: facetSearchfields,
            facetValuesMap : facetValuesMap,
            textSearchType : textSearchType,
            languageTagPrefix : languageTagPrefix,
            facetNameSuffix : facetNameSuffix,
            labelSortType : labelSortType,
            enumSearchType : enumSearchType])
    }

    /**
     * Take form-parameters from advanced-search-form, generate query and call search with query.
     * 
     * @throws IOException
     */
    def executeSearch() throws IOException {

        int searchGroupCount = configurationService.getSearchGroupCount()
        int searchFieldCount = configurationService.getSearchFieldCount()
        int offset = configurationService.getSearchOffset()
        int rows = configurationService.getSearchRows()
        def facetSearchfields = facetsService.getAllFacets()
        facetSearchfields = filterOnlyAdvancedSearchFacets(facetSearchfields)

        AdvancedSearchFormToQueryConverter converter =
                new AdvancedSearchFormToQueryConverter(params, searchGroupCount, searchFieldCount, facetSearchfields)
        String query = converter.convertFormParameters()
        redirect(uri: "/searchresults?"+SearchParamEnum.QUERY.getName()+"=" + query + "&"+SearchParamEnum.OFFSET.getName()+"=" + offset + "&"+SearchParamEnum.ROWS.getName()+"=" + rows)
    }

    /**
     * request facet-values (for select-box) for all facets that are searchable.
     * fill results in global variable facetValuesMap (key: name of facet, value: map with value, display-value, sorted)
     * 
     */
    private Map getFacetValues(facetSearchfields) {
        def facetValuesMap = [:]
        def allFacetFilters = configurationService.getFacetsFilter()

        for ( facetSearchfield in facetSearchfields ) {
            if (facetSearchfield.searchType.equals(enumSearchType)) {
                def facetValues = null
                def facetDisplayValuesMap = new TreeMap()

                //Special handling for "license_group, as this facet has "
                if (facetSearchfield.name == "license_group") {
                    facetValues = facetsService.getFacet(facetSearchfield.name , allFacetFilters)
                    for (facetValue in facetValues) {
                        //translate because of sorting
                        facetDisplayValuesMap[facetValue] = message(code: "ddbnext." + facetSearchfield.name + "_" + facetValue)
                    }
                } else {
                    facetValues = facetsService.getFacet(facetSearchfield.name + facetNameSuffix, allFacetFilters)
                    for (facetValue in facetValues) {
                        //translate because of sorting
                        facetDisplayValuesMap[facetValue] = message(code: "ddbnext." + facetSearchfield.name + facetNameSuffix + "_" + facetValue)
                    }
                }

                if (facetSearchfield.sortType != null && facetSearchfield.sortType.equals(labelSortType)) {
                    facetDisplayValuesMap = facetDisplayValuesMap.sort {it.value}
                }
                else {
                    //workaround for time_fct, sort desc by id
                    if (facetSearchfield.name == "time") {
                        facetDisplayValuesMap = facetDisplayValuesMap.sort {a, b -> b.key <=> a.key}
                    }
                    else {
                        facetDisplayValuesMap = facetDisplayValuesMap.sort {it.key}
                    }
                }

                facetValuesMap[facetSearchfield.name + facetNameSuffix] = facetDisplayValuesMap
            }
        }
        return facetValuesMap
    }

    private List filterOnlyAdvancedSearchFacets(List allFacets){
        List filteredFacets = []

        //To stay with the right order we have to iterate over both facet lists.
        allowedFacets.each { itAllowedFacets ->
            allFacets.each { itAllFacets ->
                if(itAllFacets.name == itAllowedFacets){
                    filteredFacets.add(itAllFacets)
                }
            }
        }
        return filteredFacets
    }
}
