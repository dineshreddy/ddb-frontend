<%--
Copyright (C) 2013 FIZ Karlsruhe
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<div class="works todo">
  <div class="row">
    <div class="span6">
      <h3>Werke von:</h3>
    </div>
    <div class="span3">
      <label>
        <input type="checkbox">
        Nur Objekte mit Normdaten
        </label>
      <span class="icon-question-sign"></span>
    </div>
  </div>
  <ol class="unstyled">
    <!-- TODO: replace br tag with CSS -->
    <g:each var="item" in="${involved.results.docs}">
	    <li class="theme">
	      <i class="icon-theme"></i>
	      <span>${item.preview.title}</span><br>
	      <span>${item.preview.subtitle}</span><br>
    	</li>
    </g:each>
  </ol>
  
  <g:link controller="search" action="results" params="${["query":entity.title, "facetValues%5B%5D": ["affiliate_fct%3D"+entity.title, "affiliate_fct_involved%3D"+entity.title]] }">
  	Alle Objekte (${involved.numberOfResults})
  </g:link>

</div>
