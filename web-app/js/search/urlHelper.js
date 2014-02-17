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

/**
 * Gets all request params from the window url that starts with facetValues[]
 */
de.ddb.next.search.getFacetValuesFromUrl = function() {
  var facetValuesFromUrl = de.ddb.next.search.getUrlVar('facetValues%5B%5D');
  if (facetValuesFromUrl == null) {
    facetValuesFromUrl = de.ddb.next.search.getUrlVar('facetValues[]');
  }
  
  return facetValuesFromUrl;
};

/**
 * Adds a new facetValue to the facetValues[] params of the window url.
 * An offset param is added to this list too. So a new search request can be performed on the updated acetValues[] params
 * 
 * Returns an aaray with request params.
 */
de.ddb.next.search.addFacetValueToParams = function(facetField, facetValue) {
  var paramsFacetValues = de.ddb.next.search.getFacetValuesFromUrl();
  
  //The facet values will be stored in a two dimensional Array ["facetValues[]",['type_fctyDmediatype_003','time_begin_fct=1014', 'time_end_fct=2014',]]
  var paramsArray = null;
  
  if (paramsFacetValues) {
    $.each(paramsFacetValues, function(key, value) {
      paramsFacetValues[key] = decodeURIComponent(value.replace(/\+/g, '%20'));
    });
    paramsFacetValues.push(facetField + '=' + facetValue);
    paramsArray = new Array(new Array('facetValues[]', paramsFacetValues));
  } else {
    paramsArray = new Array(new Array('facetValues[]', facetField + '=' + facetValue));
  }

  paramsArray.push(new Array('offset', 0));
  
  return paramsArray;
};

/**
 * Returns an array with url params that macht the given name
 */
de.ddb.next.search.getUrlVar = function(name) {
  return de.ddb.next.search.getUrlVars()[name];
};

/**
 * Returns an array with the url params
 */
de.ddb.next.search.getUrlVars = function() {
  var vars = {}, hash;
  var hashes = (historySupport) ? window.location.href.slice(
      window.location.href.indexOf('?') + 1).split('&') : globalUrl.split('&');
  for ( var i = 0; i < hashes.length; i++) {
    hash = hashes[i].split('=');
    if (!Object.prototype.hasOwnProperty.call(vars, hash[0])) {
      vars[hash[0]] = new Array();
    }
    vars[hash[0]].push(hash[1]);
  }
  return vars;
};

