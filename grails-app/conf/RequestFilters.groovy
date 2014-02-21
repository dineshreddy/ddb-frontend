/*
 * Copyright (C) 2014 FIZ Karlsruhe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap;
//import de.ddb.next.DdbSecurityHelper;


/**
 * This class acts as the default Grails request filter class. 
 * Any filter can be defined by adding a new entry in the filters section.
 * See http://grails.org/doc/2.2.1/guide/single.html#filters for detailed documentation.
 * 
 * @author hla
 */
class RequestFilters {

    def log

    def filters = {

        /**
         * Adds a new entry to the response header of all requested pages for IE compatibility. 
         */
        ieHeaderFilter(controller:'*', action:'*') {
            after = {
                response.addHeader("X-UA-Compatible", "IE=8,9,10")
            }
        }

    }
}
