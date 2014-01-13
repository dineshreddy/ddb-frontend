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
    <title>Multipolygone - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>

    <meta name="layout" content="main" />

    <r:require module="multipolygone"/>
    <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.1/leaflet.css" />
    <script src="http://cdn.leafletjs.com/leaflet-0.7.1/leaflet.js"></script>

  </head>

  <body>
    <div class="row map-prototype">
      <div class="row">
        <div class="span12">
          <div id="map">
          </div>
        </div>
      </div>
    </div>  
  </body>
  <script type="text/javascript">
    var map = L.map('map').setView([51.505, -0.09], 13);
    L.tileLayer('http://{s}.tile.cloudmade.com/API-key/997/256/{z}/{x}/{y}.png', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://cloudmade.com">CloudMade</a>',
        maxZoom: 18
    }).addTo(map);
  </script>
</html>
