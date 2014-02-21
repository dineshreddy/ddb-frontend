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
<div class="time-facet bt bb bl br">
	<a class="h3" href="${""}"><g:message code="ddbnext.facet_time" /></a>
	
	<div id="timespan-form">
		<hr>
		<div><g:message code="ddbnext.facet_time_from" /></div>
		<div>
			<select id="fromDay" class="day">
				<option value="" disabled selected><g:message code="ddbnext.facet_time_day"/></option>
				<option value="01">1.</option>
				<option value="02">2.</option>
				<option value="03">3.</option>
				<option value="04">4.</option>
				<option value="05">5.</option>
				<option value="06">6.</option>												
				<option value="07">7.</option>
				<option value="08">8.</option>
				<option value="09">9.</option>
				<option value="10">10.</option>
				<option value="11">11.</option>
				<option value="12">12.</option>
				<option value="13">13.</option>
				<option value="14">14.</option>
				<option value="15">15.</option>
				<option value="16">16.</option>
				<option value="17">17.</option>
				<option value="18">18.</option>												
				<option value="19">19.</option>
				<option value="20">20.</option>
				<option value="21">21.</option>
				<option value="22">22.</option>
				<option value="23">23.</option>
				<option value="24">24.</option>
				<option value="25">25.</option>
				<option value="26">26.</option>
				<option value="27">27.</option>
				<option value="28">28.</option>
				<option value="29">29.</option>
				<option value="30">30.</option>												
				<option value="31">31.</option>								
			</select>
			
			<select id="fromMonth" class="month">
				<option value="" disabled selected><g:message code="ddbnext.facet_time_month"/></option>
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
			<input type="text" pattern="-?\[0-9]*" id="fromYear" class="year" placeholder="<g:message code="ddbnext.facet_time_year"/>"/>
		</div>
		<div><g:message code="ddbnext.facet_time_to"/></div>
		<div>
			<select id="tillDay" class="day">
				<option value="" disabled selected><g:message code="ddbnext.facet_time_day"/></option>
				<option value="01">1.</option>
				<option value="02">2.</option>
				<option value="03">3.</option>
				<option value="04">4.</option>
				<option value="05">5.</option>
				<option value="06">6.</option>												
				<option value="07">7.</option>
				<option value="08">8.</option>
				<option value="09">9.</option>
				<option value="10">10.</option>
				<option value="11">11.</option>
				<option value="12">12.</option>
				<option value="13">13.</option>
				<option value="14">14.</option>
				<option value="15">15.</option>
				<option value="16">16.</option>
				<option value="17">17.</option>
				<option value="18">18.</option>												
				<option value="19">19.</option>
				<option value="20">20.</option>
				<option value="21">21.</option>
				<option value="22">22.</option>
				<option value="23">23.</option>
				<option value="24">24.</option>
				<option value="25">25.</option>
				<option value="26">26.</option>
				<option value="27">27.</option>
				<option value="28">28.</option>
				<option value="29">29.</option>
				<option value="30">30.</option>												
				<option value="31">31.</option>								
			</select>
			<select id="tillMonth" class="month">
				<option value="" disabled selected><g:message code="ddbnext.facet_time_month"/></option>
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
			<input type="text" pattern="-?\[0-9]*" id="tillYear" class="year" placeholder="<g:message code="ddbnext.facet_time_year"/>"/>
		</div>
		<div>Eingrenzung</div>
		<div>
			<input type="radio" name="limitation" id="limitationFuzzy" value="fuzzy" checked="checked" /> <label for="limitationFuzzy">Unscharf</label>
			<input type="radio" name="limitation" id="limitationExact" value="exact" /> <label for="limitationExact">Genau</label>
		</div>
		<div>
			<button id="add-timespan">Anwenden</button>
			<button id="reset-timefacet">Zurücksetzen</button>
		</div>
	</div>
</div>