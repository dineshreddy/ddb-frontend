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

import de.ddb.common.constants.FacetEnum;
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
        FacetEnum.SEARCH_ALL.getName(),
        FacetEnum.TITLE.getName(),
        FacetEnum.DESCRIPTION.getName(),
        FacetEnum.PLACE.getName(),
        FacetEnum.AFFILIATE.getName(),
        FacetEnum.KEYWORDS.getName(),
        FacetEnum.LANGUAGE.getName(),
        FacetEnum.TYPE.getName(),
        FacetEnum.SECTOR.getName(),
        FacetEnum.PROVIDER.getName(),
        FacetEnum.LICENSE_GROUP.getName()
    ]

    private static final String ENUM_SEARCH_TYPE = "ENUM"
    private static final String TEXT_SEARCH_TYPE = "TEXT"
    private static final String I18N_FACET_VALUE_PREFIX = "ddbnext."
    private static final String I18N_FACET_NAME_PREFIX =  "ddbnext.facet_"
    private static final String FACET_NAME_SUFFIX = "_fct"
    private static final String LABEL_SORT_TYPE = "ALPHA_LABEL"

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
        facetSearchfields = facetsService.filterOnlyAdvancedSearchFacets(facetSearchfields, allowedFacets)
        Map facetValuesMap = facetsService.getFacetValues(facetSearchfields, I18N_FACET_VALUE_PREFIX)

        render(view: "/search/advancedsearch", model: [searchGroupCount: searchGroupCount,
            searchFieldCount: searchFieldCount,
            facetSearchfields: facetSearchfields,
            facetValuesMap : facetValuesMap,
            textSearchType : TEXT_SEARCH_TYPE,
            languageTagPrefix : I18N_FACET_NAME_PREFIX,
            facetNameSuffix : FACET_NAME_SUFFIX,
            labelSortType : LABEL_SORT_TYPE,
            enumSearchType : ENUM_SEARCH_TYPE])
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
        facetSearchfields = facetsService.filterOnlyAdvancedSearchFacets(facetSearchfields, allowedFacets)

        AdvancedSearchFormToQueryConverter converter =
                new AdvancedSearchFormToQueryConverter(params, searchGroupCount, searchFieldCount, facetSearchfields)
        String query = converter.convertFormParameters()
        redirect(uri: "/searchresults?"+SearchParamEnum.QUERY.getName()+"=" + query + "&"+SearchParamEnum.OFFSET.getName()+"=" + offset + "&"+SearchParamEnum.ROWS.getName()+"=" + rows)
    }
}
