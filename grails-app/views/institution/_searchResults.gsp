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

<g:each var="item" in="${institution?.searchPreview}">
    <div class="preview-item">
      <div class="preview-image">
      <g:link controller="item" action="findById" params="${["id": item?.id]}">
        <img src="${item?.preview?.thumbnail}" title="${ddb.getWithoutTags(title: item?.preview?.title)}"
             alt="${ddb.getWithoutTags(title: item?.preview?.title)}"/>
      </g:link>
      </div>
      <div class="preview-caption">
        <g:link controller="item" action="findById" params="${["id": item?.id]}">
          <ddbcommon:getTruncatedItemTitle title="${item?.preview?.title}" length="${ 40 }" />
        </g:link>
      </div>
    </div>
</g:each>
