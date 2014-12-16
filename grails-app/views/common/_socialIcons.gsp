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

<g:if test="${facebookUrl || twitterUrl}">
  <div class="social-icons">
    <g:message code="ddbnext.follow" />:
    <g:if test="${facebookUrl}">
      <a class="facebook-icon" href="${facebookUrl}" target="_blank"> Facebook </a>
    </g:if>
    <g:if test="${twitterUrl}">
      <a class="twitter-icon" href="${twitterUrl}" target="_blank"> Twitter </a>
    </g:if>
  </div>
</g:if>
