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
<div class="works rolefacet">
  <div class="row">
    <div class="span5">
      <h3><g:message code="ddbnext.Entity_Involved_In"/></h3>
    </div>
    <div class="span4 header-objects">
        <input type="checkbox" id="normdata_involved_checkbox" checked="checked">
        <span><g:message code="ddbnext.Entity_Only_Normdata_Objects"/></span>
        <span class="objects-help hidden-phone hidden-tablet"></span>
    </div>
  </div>
  <div id="searchInvolved">
  	<g:render template="roleSearchResults" model="[data:searchInvolved]"/>
  </div>
  <div id="searchInvolvedNormdata">
  	<g:render template="roleSearchResults" model="[data:searchInvolvedNormdata]"/>
  </div>  
</div>
