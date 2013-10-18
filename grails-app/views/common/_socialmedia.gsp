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

<div class="socialmedia" data-lang-iso2="<g:currentLocale />" data-lang-full="<g:currentLocaleFull />">
  <div class="social-locked">
    <div class="social-overlay-container">
      <div class="social-overlay">
        <b><g:message code="ddbnext.Social_Tooltip_Header"/></b>
        <br />
        <g:message code="ddbnext.Social_Tooltip_Body"/>
        <br />
        <br />
        <a href="javascript:void(0);" class="social-accept" ><g:message code="ddbnext.Social_Tooltip_Accept"/></a>      
      </div>
    </div>
    <ul>
      <li>
        <i class="icon-facebook" ></i>
      </li>
      <li>
        <i class="icon-twitter" ></i>
      </li>
      <li>
        <i class="icon-googleplus" ></i>
      </li>
    </ul>
  </div>
  <div class="social-open">
    <ul>
      <li class="social-facebook">
        <iframe></iframe>
      </li>
      <li class="social-twitter">
        <iframe></iframe>
      </li>
      <li class="social-googleplus">
        <%-- IFrame is created dynamically by Google-JS 
        <iframe></iframe>
        --%>
      </li>
      <li class="social-lockagain" title="<g:message code="ddbnext.Social_Tooltip_Revoke" />">
        <i class="icon-lockagain" ></i>
      </li>
    </ul>
  </div>
</div>