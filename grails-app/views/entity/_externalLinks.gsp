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
<div class="external-links">
  <h3><g:message code="ddbnext.External_Links" />:</h3>
  <ul class="unstyled">
  
    <g:each var="link" in="${entity.externalLinks}">
      <li class="external-link">
        <a href="${link.url}" rel="external" class="no-external-link-icon">
          <i class="external-icon"><img src="${link.icon}" alt="" /></i>
          <span>${link.name}</span>         
        </a>
      </li>
    </g:each>
  
    <%--
    <li class="external-link">
      <a href="https://de.wikipedia.org/wiki/Johann_Wolfgang_von_Goethe"
        rel="external">
        <i class="icon-globe"></i>
        Deutschsprachige Wikipedia
      </a>
    </li>
    <li class="external-link">
      <a href="https://en.wikipedia.org/wiki/Johann_Wolfgang_von_Goethe"
        rel="external">
        <i class="icon-globe"></i>
        Englischsprachige Wikipedia
      </a>
    </li>
    <li class="external-link">
      <a href="http://swb.bsz-bw.de/DB=2.104/PPNSET?PPN=161142842&INDEXSET=1"
        rel="external">
        <i class="icon-globe"></i>
        GND Eintrag bei der Deutschen Nationalbibliothek
      </a>
    </li>
    <li class="external-link">
      <a href="https://portal.dnb.de/opac.htm"
        rel="external">
        <i class="icon-globe"></i>
        Katalog der Deutschen Nationalbibliothek
      </a>
    </li>
    <li class="external-link">
      <a href="https://opacplus.bsb-muenchen.de/InfoGuideClient/start.do"
        rel="external">
        <i class="icon-globe"></i>
        Katalog der Bayerischen Staatsbibliothek
      </a>
    </li>
    <li class="external-link">
      <a href="http://viaf.org/viaf/24602065/#Goethe,_Johann_Wolfgang_%C2%98von%C2%9C_1749-1832"
        rel="external">
        <i class="icon-globe"></i>
        Virtual International Authority File (VIAF)
      </a>
    </li>
    
    --%>
  </ul>
</div>