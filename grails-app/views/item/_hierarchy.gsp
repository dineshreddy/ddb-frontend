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
<div class="row item-detail">
  <div class="span12 item-hierarchy off">
    <div class="field-header">
        <span><g:message code="ddbnext.View_related_objects" /></span>
        <span class="contextual-help hidden-phone hidden-tablet"
           title="${g.message(code: "ddbnext.HierarchyHelp_Text",
                              args: [('<a href="' + createLink(controller: "content",
                                                               params: [dir: 'help', id: 'objdetails-hierarchy']) + '">').encodeAsHTML(),
                                     '</a>'],
                              encodeAs: "none")}"
           data-content="${g.message(code: "ddbnext.HierarchyHelp_Text",
                                     args: [('<a href="' + createLink(controller: "content",
                                                                      params: [dir: 'help', id: 'objdetails-hierarchy']) + '">').encodeAsHTML(),
                                            '</a>'],
                                     encodeAs: "none")}">
        </span>
        <div class="tooltip off hasArrow"></div>
    </div>
    <div class="row">
      <div class="span12 item-hierarchy-result"></div>
    </div>
  </div>
</div>
