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

<g:render template="/item/institution" />
<g:render template="/item/itemLinks" />
<div class="row item-detail item-content">
	<div class="<g:if test="${binaryList}">span6</g:if><g:else>span12</g:else> item-description">
		<h2>
			${title}
		</h2>
		<g:render template="/item/fields" />
		<g:render template="/item/rights" />
		<g:render template="/item/license" />
		<g:render template="/item/origin" />
		<g:render template="/item/share" />
	</div>
</div>
<g:render template="/item/linkurl" />
