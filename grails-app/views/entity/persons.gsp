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
<title>${title} - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>
<meta name="page" content="persons" />
<meta name="layout" content="main" />
<canonical:exclude params="['sort']"/>
<canonical:show/>
</head>
<body>
  <div id="var-to-js" data-random-seed="${randomSeed}" data-pgTitle="${title}"></div>

  <div class="row persons-head">
    <div class="span12">
      <div>
        <h1>
          <g:message code="ddbnext.entities.personspage.personspageheader" />
        </h1>
      </div>
      <div class="right-container">
          <a class="page-link page-link-popup-anchor"
             href="${g.createLink(controller: 'entity', action: 'persons', params: [sort: randomSeed])}"
             title="${g.message(code: 'ddbnext.CulturalItem_LinkToThisPage_Title')}">
            <span>
              <g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Label"/>
            </span>
          </a>
          <ddb:getSocialmediaBody />
      </div>
    </div>
  </div>
  <div class="row persons-body">
    <div class="span3 persons-font">
      <p>
        <g:message code="ddbnext.entities.personspage.description"/> <g:link controller="entity" action="persons"><g:message code="ddbnext.entities.personspage.descriptionLink" /></g:link> 
      </p>
      <p>
        <g:message code="ddbnext.entities.personspage.sourceInfo" />
      </p>
      <p>
        <g:message code="ddbnext.entities.personspage.licenceInfo" />
      </p>
    </div>
    <div class="span9 persons-overview-content">
      <div>
        <div class="persons-overview-overlay-modal"></div>
        <div class="persons-overview-overlay-waiting">
          <div class="small-loader"></div>
        </div>
        <div id="columns">
          <g:each var="person" in="${results[0]}">
            <div class="pin">
              <a href="${g.createLink(controller: 'entity', action: 'index') + '/' + ddb.getGndIdFromGndUri(id: person.id)}"
                 title="${person.preferredName}">
                <img src="${ddb.fixWikimediaImageWidth(thumbnail: person.thumbnail, desiredWidth: '150px')}"
                     alt="${person.preferredName}"/>
              </a>
              <p>
                <a href="${g.createLink(controller: 'entity', action: 'index') + '/' + ddb.getGndIdFromGndUri(id: person.id)}"
                   title="${person.preferredName}">
                  ${person.preferredName}
                </a>
              </p>
            </div>
          </g:each>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
