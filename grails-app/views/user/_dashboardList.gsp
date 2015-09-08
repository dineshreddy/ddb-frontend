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
<ul class="results-list unstyled <g:if test="${viewType == SearchParamEnum.VIEWTYPE_GRID.getName()}">grid</g:if>">
  <g:set var="offset" value="${0}"/>
  <g:set var="index" value="${0}"/>
  <g:each in="${results}" >
    <g:set var="controller" value="item" />
    <g:set var="action" value="findById" />
    <g:if test="${it.category == 'Institution'}">
        <g:set var="controller" value="institution" />
        <g:set var="action" value="showInstitutionsTreeByItemId" />
    </g:if>
    <li class="item bt-green">
      <div class="summary <g:if test="${viewType != SearchParamEnum.VIEWTYPE_GRID.getName()}">row</g:if>">
        <g:if test="${viewType == SearchParamEnum.VIEWTYPE_GRID.getName()}">
          <div class="thumbnail-wrapper">
            <div class="thumbnail" id="thumbnail-${it.id}">
              <g:if test="${it.category == "orphaned"}">
                <a>
                  <img src="${it.preview.thumbnail}" alt="<ddb:getWithoutTags>${it.preview.title}</ddb:getWithoutTags>" />
                </a>
              </g:if>
              <g:else>
                <g:if test="${it.preview.media[0] == "entity"}">
                  <g:set var="entityLink" value="persist entity-link" />
                </g:if>
                <g:else>
                  <g:set var="entityLink" value="persist" />
                </g:else>
                <g:link class="${entityLink}" controller="${ controller }" action="${ action }" params="[id: it.id]">
                  <img src="${it.preview.thumbnail}" alt="<ddb:getWithoutTags>${it.preview.title}</ddb:getWithoutTags>" />
                </g:link>
              </g:else>
            </div>
            <div class="item-options br-green"></div>
          </div>
          <div class="summary-main-wrapper">
            <div class="summary-main">
              <h2 class="title">
                <g:if test="${it.category == "orphaned"}">
                  <a title="${ddbcommon.getTruncatedHovercardTitle(title: it.label, length: 350)}">
                    <ddbcommon:getTruncatedItemTitle title="${ it.preview.title }" length="${ 60 }" />
                  </a>
                </g:if>
                <g:else>
                  <g:link class="persist" controller="${ controller }" action="${ action }" params="[id: it.id]" title="${ddbcommon.getTruncatedHovercardTitle(title: it.label, length: 350)}">
                    <ddbcommon:getTruncatedItemTitle title="${ it.preview.title }" length="${ 60 }" />
                  </g:link>
                </g:else>
              </h2>
              <div class="subtitle">
                <g:if test="${(it.preview?.subtitle != null) && (it.preview?.subtitle?.toString() != "null")}">
                  <ddbcommon:stripTags text="${it.preview.subtitle.replaceAll('match', 'strong')}" allowedTags="strong"/>
                </g:if>
              </div>
              <ul class="matches unstyled">
                <li class="matching-item">
                  <span>
                    <g:each var="match" in="${it.view}">
                      <g:if test="${match instanceof String}">
                        ...<ddbcommon:stripTags text="${match.replaceAll('match', 'strong')}" allowedTags="strong" />...
                      </g:if>
                      <g:else>
                        ...${match}...
                      </g:else>
                    </g:each>
                  </span>
                </li>
              </ul>
            </div>
          </div>
        </g:if>
        <g:else>
          <div class="summary-main-wrapper span7">
            <div class="summary-main">
              <h2 class="title">
                <g:if test="${it.category == "orphaned"}">
                  <a title="${ddbcommon.getTruncatedHovercardTitle(title: it.label, length: 350)}">
                    <ddbcommon:getTruncatedItemTitle title="${ it.preview.title }" length="${ 100 }" />
                  </a>
                </g:if>
                <g:else>
                  <g:link class="persist" controller="${ controller }" action="${ action }" params="[id: it.id]" title="${ddbcommon.getTruncatedHovercardTitle(title: it.label, length: 350)}">
                    <ddbcommon:getTruncatedItemTitle title="${ it.preview.title }" length="${ 100 }" />
                  </g:link>
                </g:else>
              </h2>
              <div class="subtitle">
                <g:if test="${(it.preview?.subtitle != null) && (it.preview?.subtitle?.toString() != "null")}">
                  <ddbcommon:stripTags text="${it.preview.subtitle.replaceAll('match', 'strong')}" allowedTags="strong"/>
                </g:if>
              </div>
              <ul class="matches unstyled">
                <li class="matching-item">
                  <span>
                    <g:each var="match" in="${it.view}">
                      <g:if test="${match instanceof String}">
                        ...<ddbcommon:stripTags text="${match.replaceAll('match', 'strong')}" allowedTags="strong" />...
                      </g:if>
                      <g:else>
                        ...${match}...
                      </g:else>
                    </g:each>
                  </span>
                </li>
              </ul>
            </div>
          </div>
          <div class="thumbnail-wrapper span3">
            <div class="thumbnail" id="thumbnail-${it.id}">
              <g:if test="${it.category == "orphaned"}">
                <a>
                  <img src="${it.preview.thumbnail}" alt="<ddb:getWithoutTags>${it.preview.title}</ddb:getWithoutTags>" />
                </a>
              </g:if>
              <g:else>
                <g:if test="${it.preview.media[0] == "entity"}">
                  <g:set var="entityLink" value="persist entity-link" />
                </g:if>
                <g:else>
                  <g:set var="entityLink" value="persist" />
                </g:else>
                <g:link class="${entityLink}" controller="${ controller }" action="${ action }" params="[id: it.id]">
                  <img src="${it.preview.thumbnail}" alt="<ddb:getWithoutTags>${it.preview.title}</ddb:getWithoutTags>" />
                </g:link>
              </g:else>
            </div>
            <div class="item-options bl-green">
              <ul class="item-options-ul">
                <li>
                  <div class="information<ddbcommon:isLoggedIn> show-favorites</ddbcommon:isLoggedIn> bb-green">
                    <div class="hovercard-info-item" data-iid="${it.id}">
                      <h4><ddbcommon:getTruncatedHovercardTitle title="${ it.preview.title }" length="${ 350 }" /></h4>
                      <ul class="unstyled">
                        <li>
                          <div class="small-loader"></div>
                        </li>
                      </ul>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </g:else>
      </div>
    </li>
  </g:each>
</ul>