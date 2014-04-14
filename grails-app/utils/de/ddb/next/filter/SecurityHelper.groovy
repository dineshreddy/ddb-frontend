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

package de.ddb.next.filter

import javax.servlet.http.HttpServletResponse

import org.apache.commons.logging.LogFactory


/**
 * Helper class to perform security issues on the request and response objects
 * 
 * @author hla
 */
class SecurityHelper {

    private def log = LogFactory.getLog(this.class)
    private final CSPPoliciesApplier cspPoliciesApplier = new CSPPoliciesApplier()

    /**
     * Performs a set of security tasks defined in separated methods.
     * 
     * @param request The wrapped request object
     */
    void performSecurityTasks(ServletRequestWrapper request, HttpServletResponse response){
        addReponseSecurityHeaders(request, response)
        cspPoliciesApplier.applyPolicies(request, response)
    }

    private void addReponseSecurityHeaders(ServletRequestWrapper request, HttpServletResponse response) {
        response.addHeader("X-Frame-Options", "SAMEORIGIN")
        response.addHeader("X-Content-Type-Options", "nosniff")
    }
}
