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
<div class="row item-detail">
  <div class="span6 origin">
    <g:if test="${originUrl}">
      <a target="_blank" class="show-origin" href="${originUrl.encodeAsHTML()}" title="<g:message encodeAs="html" code="ddbnext.stat_008" />">
        <span class="has-origin"><g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToOriginalItem_Label" /></span>
      </a>
    </g:if>
    <g:else>
      <span class="no-origin"><g:message encodeAs="html" code="ddbnext.Link_to_data_supplier_not_available" /></span>
    </g:else>
  </div>
</div>
<div class="row item-detail mobile-socialmedia">
  <div class="span6 share">
    <ddb:getSocialmediaBody />
  </div>
</div>