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
<%@ page import="de.ddb.common.beans.institution.Sector" %>
<div class="filter-title"><g:message encodeAs="html" code="ddbnext.InstitutionPage_FilterBySector" /></div>
<g:each in="${Sector.values()}" var="sector">
  <div class="sector-facet">
    <label class="checkbox">
      <input data-sector="${sector.getName()}" value="${sector.getName()}" type="checkbox"><g:message code="ddbnext.${sector.getName()}"/>
    </label>
  </div>
</g:each>
<div class="institution-with-data">
  <label class="checkbox"> <input type="checkbox"><g:message encodeAs="html" code="ddbnext.InstitutionPage_OnlyInstitutionsWithData" /></label>
</div>