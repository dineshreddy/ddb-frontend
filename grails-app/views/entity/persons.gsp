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


<html>
<head>
<title>
  ${title} - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>
<meta name="page" content="persons" />
<meta name="layout" content="main" />
</head>
<body>
  <div class="span12 personEntities bb">
    <div class="row ">
      <div class="span3">
        <h1>
          <g:message code="ddbnext.entities.personspage.personspageheader" />
        </h1>
      </div>
      <div class="span9 paddinTopPerson">
      
        <div class="link-block">
          <a class="page-link page-link-popup-anchor" href="<g:createLink controller='entity' action='persons' />"
            title="<g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Title" />"> <span><g:message encodeAs="html"
                code="ddbnext.CulturalItem_LinkToThisPage_Label" /></span>
          </a>
        </div>
        <ddb:getSocialmediaBody />
      </div>

    </div>
  </div>
  <div class="row">
    <div class="span3 noleftMargin correctleftMargin30 persons-font">
      <p>
        <g:message code="ddbnext.entities.personspage.description" />
      </p>
      <p>
        <g:message code="ddbnext.entities.personspage.sourceInfo" />
      </p>
      <p>
        <g:message code="ddbnext.entities.personspage.licenceInfo" />
      </p>
    </div>
    <div class="span-imgcontainer search-results-content">
      <div class="row">
        <div class="search-results">
          <div class="persons-list">
            <g:if test="${results}">
              <g:each var="person" in="${results[0]}">
                  <div class="span-img persons-font">
                    <a href="<g:createLink controller='entity' action='index' />/<ddb:getGndIdFromGndUri id="${person.id}"/>"
                      title=" ${person.preferredName}"><img src="<ddb:fixWikimediaImageWidth thumbnail="${person.thumbnail}" />"></a>
                    <a href="<g:createLink controller='entity' action='index' />/<ddb:getGndIdFromGndUri id="${person.id}"/>"
                      alt=" ${person.preferredName}">${person.preferredName}</a>
                  </div>
              </g:each>
            </g:if>
          </div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>