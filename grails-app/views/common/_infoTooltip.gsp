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

<%--<span class="contextual-help hidden-phone hidden-tablet" --%>
<%--  data-content="Geben Sie Ihren Suchbegriff in das Suchfeld ein. Klicken Sie auf das Lupensymbol oder drücken Sie die Eingabetaste. --%>
<%--  <a href="/current/content/help/search-simple"> Hilfe zur einfachen Suche </a>"> --%>
<%--</span>--%>

<span class="contextual-help hidden-phone hidden-tablet" 
title="${g.message(code: messageCode, args: [('<a href="' + createLink(controller: "content", params: [dir: 'help', id: infoPath]) + '">').encodeAsHTML(), '</a>'], encodeAs: "none")}" 
data-content="${g.message(code: messageCode, args: [('<a href="' + createLink(controller: "content", params: [dir: 'help', id: infoPath]) + '">').encodeAsHTML(), '</a>'], encodeAs: "none")}"> 
</span>
<div class="tooltip off"></div>
