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

<g:each var="item" in="${entity?.searchPreview?.items}">
    <div class="preview-item">
      <g:link controller="item" action="findById" params="${["id": item?.id]}">
        <g:if test="${ (item?.preview?.thumbnail.toString().contains('binary'))}">
          <img src="${request.getContextPath() + item?.preview?.thumbnail}" title="<ddb:getWithoutTags>${item?.preview?.title}</ddb:getWithoutTags>" alt="<ddb:getWithoutTags>${item?.preview?.title}</ddb:getWithoutTags>" />
        </g:if>
        <g:else>
          <img src="<g:img plugin="ddb-common" dir="images/placeholder" file="searchResultMediaText.png"/>"
               title="<ddb:getWithoutTags>${item?.preview?.title}</ddb:getWithoutTags>"
               alt="<ddb:getWithoutTags>${item?.preview?.title}</ddb:getWithoutTags>" />
        </g:else>
      </g:link>
      <div class="caption">
        <g:link controller="item" action="findById" params="${["id": item?.id]}">
          <ddbcommon:getTruncatedItemTitle title="${item?.preview?.title}" length="${ 40 }" />
        </g:link>
      </div>
    </div>
</g:each>
