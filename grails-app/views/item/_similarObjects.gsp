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
<g:if test="${similarItems?.results}">
<div class="row item-detail">
  <div class="span12 similar-objects bt">

    <div class="similar-objects-header active">
      <h3><g:message encodeAs="html" code="ddbnext.item.similarObjects" /></h3>
    </div>

    <div class="similar-objects-items">
      <ul class="unstyled">
        <g:each var="resultDocs" in="${similarItems.results.docs}">
          <g:each var="doc" in="${resultDocs}">
              <li class="similar-objects-item">
                <g:link class="persist" controller="item" action="findById" params="${[id:doc.id]}" title="${ddb.getTruncatedHovercardTitle(title: doc.title, length: 350)}">
                  <ddb:getTruncatedItemTitle title="${ doc.title }" length="${ 100 }" />
                </g:link>
              </li>
          </g:each>
        </g:each>
      </ul>
    </div>

  </div>
</div>
</g:if>