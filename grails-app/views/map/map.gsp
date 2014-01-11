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
    <title>Map prototype - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
    
    <meta name="layout" content="main" />
    
    <r:require module="map"/>
    
  </head>
  
  <body>
    <div class="row map-prototype">
      <div class="row">
        <div class="span8">
          <div id="ddb-map">
          </div>
        </div>
        <div class="span4">
          <div class="sector-facet">
            <label class="checkbox"> <input data-sector="sec_01" value="sec_01" type="checkbox">Archive</label>
          </div>
          <div class="sector-facet">
            <label class="checkbox"> <input data-sector="sec_02" value="sec_02" type="checkbox">Library</label>
          </div>
          <div class="sector-facet">
            <label class="checkbox"> <input data-sector="sec_03" value="sec_03" type="checkbox">Monument protection</label>
          </div>
          <div class="sector-facet">
            <label class="checkbox"> <input data-sector="sec_04" value="sec_04" type="checkbox">Research</label>
          </div>
          <div class="sector-facet">
            <label class="checkbox"> <input data-sector="sec_05" value="sec_05" type="checkbox">Media</label>
          </div>
          <div class="sector-facet">
            <label class="checkbox"> <input data-sector="sec_06" value="sec_06" type="checkbox">Museum</label>
          </div>
          <div class="sector-facet">
            <label class="checkbox"> <input data-sector="sec_07" value="sec_07" type="checkbox">Other</label>
          </div>
          
        </div>
      </div>
      
    </div>  
  </body>
</html>
