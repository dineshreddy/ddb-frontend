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

/* Search namespace  */
de.ddb.next.search = de.ddb.next.search || {};

de.ddb.next.search.getLocalizedFacetValue = function(facetField, facetValue) {
  if (facetField === 'affiliate_fct_role' || facetField === 'keywords_fct' || facetField === 'place_fct' || facetField === 'provider_fct') {
    return facetValue.toString();
  }
  if (facetField === 'type_fct') {
    return messages.ddbnext['type_fct_' + facetValue];
  }
  if (facetField === 'time_fct') {
    return messages.ddbnext['time_fct_' + facetValue];
  }
  if (facetField === 'language_fct') {
    return messages.ddbnext['language_fct_' + facetValue];
  }
  if (facetField === 'sector_fct') {
    return messages.ddbnext['sector_fct_' + facetValue];
  }
  if (facetField === 'license_group') {
    return messages.ddbnext['license_group_' + facetValue];
  }
  return '';
};

de.ddb.next.search.getLocalizedFacetField = function(facetField) {
  return messages.ddbnext['facet_' + facetField];
};