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
<div class="row item-detail">
  <div class="span7 share">
  <%--   Sending PDF per Mail Start    --%>
  <div class="sendmail-block">
  <div id="i18ntranslateSend" data-val="<g:message code="ddbnext.Send_Button" />"></div>
  <div id="i18ntranslateValidEmail" data-val="<g:message code="ddbnext.Enter_A_Valid_Email" />"></div>
      <a class="sendmail-link sendmail-link-popup-anchor" href="${createLink(controller: 'item', action: 'sendpdf', params:[id:itemId])}" title="<g:message code="ddbnext.item.sendPdf" />">
      <span><g:message code="ddbnext.item.sendPdf" /></span>
      </a>
    </div>
<%--   Sending PDF per Mail End    --%>    
    <ddb:getSocialmediaBody />
  </div>
</div>
