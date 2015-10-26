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
<div class="noresults">
  <div>
    <p>
      <strong>
        <g:message code="ddbcommon.No_results_found_for_the_search.Title"/>
      </strong>
    </p>
    <div class="reset-selection ${resetSelectionUrl ? "" : "off"}">
        <g:link class="reset-selection-url" url="${resetSelectionUrl}">
          <g:message code="ddbcommon.No_results_found_for_the_search.ResetSelection"/>
        </g:link>
    </div>
    <p>
      <g:message iencode="none" code="ddbcommon.No_results_found_for_the_search.Text"/>
    </p>
  </div>
</div>