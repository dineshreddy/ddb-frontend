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
<div class="link-block hidden-phone">
  <g:link class="pdf-link" controller="item" params="${params + [pdf:true]}" title="${message(code: 'ddbnext.export_pdf')}" target="_blank">
    <span><g:message code="ddbnext.export_pdf" /></span>
  </g:link>
</div>