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
<div class="fields">
  <g:each var="field" in="${fields}">
    <div class="row">
      <div class="span2"><strong>${field.name}: </strong></div>
      <div class="value span3">
        <div>
          <g:each var="value" in="${field.value}">
            <g:render template="/item/field" model="[value: value]"/>
            <br />
          </g:each>
        </div>
      </div>
    </div>
  </g:each>
</div>
