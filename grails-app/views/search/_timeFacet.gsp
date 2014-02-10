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
 
<%@page import="de.ddb.next.constants.SearchParamEnum"%>
<%@page import="java.awt.event.ItemEvent"%>
<div class="facets-item time-facet bt bb bl br">
	<div id="timespan-form">
		<a class="h3" href="${""}"><g:message code="ddbnext.facet_time" /></a>
		<hr>
		<div>Von</div>
		<div>
			<input id="fromDay" class="day" type="number" min="0" max="12"
				STEP="1" VALUE="1" SIZE="2" /> 
			<select id="fromMonth" class="month">
				<option value="01">January</option>
				<option value="02">February</option>
				<option value="03">March</option>
				<option value="04">April</option>
				<option value="05">May</option>
				<option value="06">June</option>
				<option value="07">July</option>
				<option value="08">August</option>
				<option value="09">September</option>
				<option value="10">October</option>
				<option value="11">November</option>
				<option value="12">December</option>
			</select>
			<input type="text" pattern="[0-9]*" id="fromYear" class="year" value="1900"/> 
		</div>
		<div>Bis</div>
		<div>
			<input id="tillDay" class="day" type="number" min="0" max="12"
				STEP="1" VALUE="1" SIZE="2" /> 
			<select id="tillMonth" class="month">
				<option value="01">Januar</option>
				<option value="02">Februar</option>
				<option value="03">März</option>
				<option value="04">April</option>
				<option value="05">Mai</option>
				<option value="06">Juni</option>
				<option value="07">Juli</option>
				<option value="08">August</option>
				<option value="09">September</option>
				<option value="10">Oktober</option>
				<option value="11">November</option>
				<option value="12">Dezember</option>
			</select> 
			<input type="text" pattern="[0-9]*" id="tillYear" class="year" value="2014"/>
		</div>
		<div>Eingrenzung</div>
		<div>
			<input type="radio" name="limitation" id="limitationFuzzy"
				value="fuzzy" checked="checked" /> <label for="limitationFuzzy">Unscharf</label>
			<input type="radio" name="limitation" id="limitationExact"
				value="exact" /> <label for="limitationExact">Genau</label>
		</div>
		<div>
			<button>Anwenden</button>
			<button>Zurücksetzen</button>
		</div>
	</div>
</div>