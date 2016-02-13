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
<select class="multiselect" multiple>
  <g:each in="${Sector.values()}" var="sector">
    <option value="${sector.getName()}"><g:message code="ddbnext.${sector.getName()}"/></option>
  </g:each>
  <option value="onlyInstitutionsWithData"><g:message encodeAs="html" code="ddbnext.InstitutionPage_OnlyInstitutionsWithData" /></option>
</select>
