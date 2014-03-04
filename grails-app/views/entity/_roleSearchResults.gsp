<%--
Copyright (C) 2014 FIZ Karlsruhe
 
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
<ol class="unstyled">
  <g:each var="item" in="${data?.items}">	  
    <li class="theme">
       <g:link controller="item" action="findById" params="${["id": item.id]}">
         <i class="icon-theme"></i>
         <span class="item-title"><ddb:getTruncatedItemTitle title="${item.preview.title}" length="${ 200 }" /></span><br>
       </g:link>
       <span class="item-subtitle">${item.preview.subtitle}</span><br>
    </li>  	
  </g:each>
</ol>

<g:link controller="search" action="results" params="${data?.searchUrlParameter }">
  <span class="all-objects-link"><g:message code="ddbnext.Entity_All_Objects"/> (${data?.resultCount})</span>
</g:link>
