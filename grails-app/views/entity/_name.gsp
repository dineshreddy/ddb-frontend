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
<div class="name fields">
	<h2>
		${entity.person.preferredName}
	</h2>

	<span>
		<g:each var="link" status="i" in="${entity.person.professionOrOccupation}">
			<g:if test="${link.'@id'}">
				<a href="${link.'@id'}" class="no-external-link-icon">
				${link.value}<g:if test="${i < (entity.person.professionOrOccupation.size()-1)}">, </g:if>
				</a>
			</g:if>
			<g:else>
				<span>${link.value}<g:if test="${i < (entity.person.professionOrOccupation.size()-1)}">, </g:if></span>
			</g:else>
		</g:each>
	</span>
</div>