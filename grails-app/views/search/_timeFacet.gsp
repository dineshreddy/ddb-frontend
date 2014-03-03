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

<!-- The time facet should only be available via Javascript. So per default set the class off. -->
<div class="time-facet bt bb bl br off">
<a class="h3" href="${""}"><g:message code="ddbnext.facet_time" /></a>
  
  <div id="timespan-form">
    <hr>
    <div><g:message code="ddbnext.facet_time_from" /></div>
      <div>
        <select id="fromDay" class="day">
          <option value="" disabled selected><g:message code="ddbnext.facet_time_day"/></option>
          <g:set var="i" value="${1}"/>
          <g:while test="${i < 32}">
            <option value="${i}">${i}.</option>
            <g:set var="i" value="${i + 1}" />
          </g:while>
        </select>
        
        <select id="fromMonth" class="month">
          <option value="" disabled selected><g:message code="ddbnext.facet_time_month"/></option>
          <g:set var="i" value="${0}"/>
          <g:while test="${i < 12}">
            <option value="${i + 1}"><ddb:getLocalizedMonth index="${i}"/></option>
            <g:set var="i" value="${i + 1}" />
          </g:while>
        </select>
        <input type="text" pattern="-?\[0-9]*" id="fromYear" class="year" placeholder="<g:message code="ddbnext.facet_time_year"/>"/>
      </div>
      <div><g:message code="ddbnext.facet_time_to"/></div>
      <div>
        <select id="tillDay" class="day">
          <option value="" disabled selected><g:message code="ddbnext.facet_time_day"/></option>
          <g:set var="i" value="${1}"/>
          <g:while test="${i < 32}">
            <option value="${i}">${i}.</option>
            <g:set var="i" value="${i + 1}" />
          </g:while>
        </select>
        <select id="tillMonth" class="month">
          <option value="" disabled selected><g:message code="ddbnext.facet_time_month"/></option>
          <g:set var="i" value="${0}"/>
          <g:while test="${i < 12}">
            <option value="${i+1}"><ddb:getLocalizedMonth index="${i}"/></option>
            <g:set var="i" value="${i+1}" />
          </g:while>
        </select> 
        <input type="text" pattern="-?\[0-9]*" id="tillYear" class="year" placeholder="<g:message code="ddbnext.facet_time_year"/>"/>
      </div>
      <div class="time-restriction">
        <g:message code="ddbnext.facet_time_restrict_to"/>
        <span class="contextual-help hidden-phone hidden-tablet"
            title="${g.message(code: "ddbnext.Time_Restriction_Tooltip")}" data-content="${g.message(code: "ddbnext.Time_Restriction_Tooltip")}">
            </span> 
            <div class="tooltip off hasArrow"></div>
      </div>
      <div>
        <input type="radio" name="limitation" id="limitationFuzzy" value="fuzzy" /> <label for="limitationFuzzy"><g:message code="ddbnext.facet_time_fuzzy"/></label>
        <input type="radio" name="limitation" id="limitationExact" value="exact" /> <label for="limitationExact"><g:message code="ddbnext.facet_time_exactly"/></label>
      </div>
      <div>
        <button class="without-date" id="add-timespan"><g:message code="ddbnext.facet_time_apply"/></button>
        <button id="reset-timefacet"><g:message code="ddbnext.facet_time_reset"/></button>
      </div>
    </div>
  </div>
