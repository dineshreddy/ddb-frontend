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
<html>
  <head>
    <title>3d items prototype - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
    <meta name="layout" content="main" />
    <r:require module="items3d"/>
  </head>
  <body>
    <div class="3d-prototype">
      <div class="row">
        <div class="span12">
          <div class="span6 item-description">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. In laoreet tempus risus ac tincidunt. Vestibulum bibendum porttitor eros, malesuada mattis lectus tristique at. Suspendisse orci massa, rhoncus id est eu, mattis dictum leo. Aliquam vestibulum blandit nibh ac ultricies. Aliquam hendrerit arcu sit amet rutrum iaculis. Sed tincidunt nibh a tortor convallis, in egestas massa commodo. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. In dictum dui a massa egestas porttitor. Vestibulum elit dui, congue sed odio eleifend, fringilla consectetur justo. Sed adipiscing quis nunc at pellentesque. Ut nec tempor nunc, sit amet facilisis massa.

Suspendisse potenti. Suspendisse eget quam risus. Aliquam ac tincidunt quam, sed porta lorem. In posuere cursus consequat. Praesent adipiscing justo ac ligula volutpat facilisis et sit amet mi. Proin et viverra quam. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum vulputate pellentesque ipsum in malesuada. Cras sit amet auctor nunc. Ut porttitor ultricies velit, sit amet eleifend ante accumsan id.

Ut id scelerisque arcu. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla facilisi. Aenean suscipit massa eu lectus sollicitudin auctor. Cras iaculis euismod augue, ac fermentum leo elementum vitae. Morbi id porta sem. Morbi euismod tincidunt neque ut porta. Nullam tincidunt sed ante id iaculis. Phasellus velit nulla, blandit sed convallis eu, vestibulum in eros. Nam vitae velit quis leo congue condimentum. Morbi eget blandit ante. Nulla aliquam enim quis enim elementum, vitae bibendum turpis fringilla.

Curabitur id sem purus. Morbi in felis mollis, ullamcorper augue faucibus, dictum neque. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Suspendisse rhoncus lacinia tellus, eget fringilla lacus faucibus quis. Duis eu lectus dapibus justo consectetur fermentum. Nulla magna lorem, sodales eleifend nibh et, vulputate scelerisque ipsum. Sed ut sapien ac metus hendrerit pellentesque. Pellentesque condimentum in quam at iaculis. Curabitur ligula ante, vestibulum quis facilisis sit amet, congue vel nulla. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Fusce eu tortor tristique, fringilla odio sed, interdum lacus. Curabitur id malesuada velit.
          </div>
          <div class="span6 slide-viewer item-detail">
            <div id="ddb-3d">
              <g:if test="${kanne}">
                <x3d style='width:100%; height:100%; border:0; margin:0; padding:0;'> 
                  <scene>
                    <inline url="${request.contextPath}/x3domCatalogueDDB/Kanne/kanne.x3d"> </inline> 
                  </scene>
                </x3d>
              </g:if>
              <g:if test="${draisine}">
                <x3d style='width:100%; height:100%; border:0; margin:0; padding:0;'> 
                  <scene>
                    <inline url="${request.contextPath}/x3domCatalogueDDB/Draisine/Draisine.x3d"> </inline> 
                  </scene>
                </x3d>
              </g:if>
              <g:if test="${bueste}">
                <x3d style='width:100%; height:100%; border:0; margin:0; padding:0;'> 
                  <scene>
                    <inline url="${request.contextPath}/x3domCatalogueDDB/Bueste/Bueste_200000_Polygone.x3d"> </inline> 
                  </scene>
                </x3d>
              </g:if>
              <g:if test="${elefant}">
                <g:render template="elefant" />
              </g:if>
              <g:if test="${nofretete}">
                <g:render template="nofretete" />
              </g:if>
              <g:if test="${aegyptische_Statue3}">
                <g:render template="aegyptische_Statue3" />
              </g:if>
            </div>
            Press 'R' to reset the displayed item. 
            Use the mouse scroll to zoom.
            <div class="binary-title" title="**Some title**">
                <h3><g:link controller="items3d" action="index" id="kanne">-> Kanne</g:link></h3>
                <h3><g:link controller="items3d" action="index" id="draisine">-> Draisine</g:link></h3>
                <h3><g:link controller="items3d" action="index" id="bueste">-> Bueste</g:link></h3>
                <h3><g:link controller="items3d" action="index" id="elefant">-> Elefant</g:link></h3>
                <h3><g:link controller="items3d" action="index" id="nofretete">-> Nofretete</g:link></h3>
                <h3><g:link controller="items3d" action="index" id="aegyptische_Statue3">-> Aegyptische Statue</g:link></h3>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>