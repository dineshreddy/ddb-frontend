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
<ul class="results-list unstyled entity-list">

  <g:set var="pageGndHitCounter" value="${0}"/>
  <g:each in="${gndResults}" var="gndItem">
    <g:set var="pageGndHitCounter" value="${pageGndHitCounter + 1}" />
    <li class="entity bt ">
      <div class="summary row">
      
      
        <div class="summary-main-wrapper span7">
          <div class="summary-main summary-entity">
            <div class="entity-type">
                <g:message code="ddbnext.Entity_Page_Person"/>
            </div>
            <h2 class="title">
              <g:link class="persist" controller="entity" action="index" params="${params + [id:gndItem.id]}" >
                <strong><g:truncateItemTitle title="${ gndItem.person.name }" length="${ 100 }"></g:truncateItemTitle></strong>
              </g:link>
            </h2>
            <div class="subtitle">
                ${ gndItem.person.description }
            </div>
          </div>
          <div class="extra">
          </div>
        </div>
        
        
        <div class="thumbnail-wrapper span2">
          <div class="thumbnail">
            <g:link class="persist" controller="entity" action="index" params="${params + [id: gndItem.id]}" class="no-external-link-icon">
              <%-- 
              <img src="${gndItem.thumbnail.link}" alt="${ gndItem.person.name }" />
              --%>
              <g:img dir="images/placeholder" file="search_result_entity.png" alt="${ gndItem.person.name }"/>
            </g:link>
          </div>
          <%-- 
          <g:isLoggedIn>
            <div id="favorite-${gndItem.id}" class="add-to-favorites" title="<g:message code="ddbnext.Add_To_Favorites"/>" ></div>
            <div id="favorite-confirmation" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
              <div class="modal-body">
                <p><g:message code="ddbnext.Added_To_Favorites"/></p>
              </div>
            </div>
          </g:isLoggedIn>
          --%>
        </div>


      </div>
    </li>
  </g:each>
  
</ul>