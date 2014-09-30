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
<g:set var="config" bean="configurationService"/>
<g:set var="jsonHierarchyChildrenOfOrg" value="${vApiInst.getChildrenOfInstitutionByItemId(itemId, config.getBackendUrl())}" />
<g:if test="${((jsonHierarchyChildrenOfOrg)&&(jsonHierarchyChildrenOfOrg.size() > 0))}">
    <ol class="institution-list">
       
       <g:if test="${(loopCount++ < maxDepthOfLoop)}">
           <g:each in="${jsonHierarchyChildrenOfOrg}" >
               <li class="institution-listitem" data-sector="${ it?.sector }" data-institution-id="${ it?.id }" >
                   <g:if test="${(selectedItemId == it.id)}">
                       <i class="icon-institution"></i>
                       <div>
                         <b>${it.label}</b>
                       </div>
                   </g:if>
                   <g:else>
                       <i class="icon-child-institution"></i>
                       <div>
                         <g:link controller="institution" action="showInstitutionsTreeByItemId" params="[id: it.id]">${it.label}</g:link>
                       </div>
                   </g:else>
                   <g:if test="${!(it.aggregationEntity)}">
                       <g:set var="itemId" value="${it.id}" />
                       <g:render template="subinstitutions" />
                   </g:if>
               </li>
           </g:each>
       </g:if>
       <g:else>
           <g:message encodeAs="html" code=" | broken loop at a depth of more than ${maxDepthOfLoop}" />
       </g:else>

    </ol>
</g:if>
