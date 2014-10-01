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
<%@page import="de.ddb.common.constants.SearchParamEnum"%>

<div class="search">
  <h3><g:message encodeAs="html" code="ddbnext.Search" />:</h3>
  <div class="">
    <i class="icon-search-entity"></i>
    <g:link controller="search" action="results" params="${[(SearchParamEnum.QUERY.getName()):entity.person.preferredName] }">
      <g:message encodeAs="html" code="ddbnext.Entity_Search_DDB" args="${[entity.person.preferredName]}"/>
    </g:link>
  </div>
</div>
<hr>
