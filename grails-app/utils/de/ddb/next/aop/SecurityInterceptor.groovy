/*
 * Copyright (C) 2013 FIZ Karlsruhe
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
package de.ddb.next.aop

import grails.util.Holders

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.context.ApplicationContext

import de.ddb.common.AasPersonService
import de.ddb.common.UserService
import de.ddb.common.beans.User
import de.ddb.common.beans.aas.AasCredential
import de.ddb.common.beans.aas.Privilege
import de.ddb.common.beans.aas.PrivilegeEnum

@Aspect
public class SecurityInterceptor {
    private final ApplicationContext ctx = Holders.grailsApplication.mainContext
    private final LinkGenerator grailsLinkGenerator = ctx.getBean("grailsLinkGenerator")
    private final AasPersonService aasPersonService = (AasPersonService) ctx.getBean("aasPersonService")
    private final UserService userService = (UserService) ctx.getBean("userService")

    @Around("@annotation(de.ddb.common.aop.IsAuthorized)")
    public void IsAuthorized(JoinPoint joinPoint) {
        if (userService.isUserLoggedIn()) {
            joinPoint.proceed()
        }
        else {
            WebUtils.retrieveGrailsWebRequest().getCurrentResponse().sendError(
                    WebUtils.retrieveGrailsWebRequest().getCurrentResponse().SC_UNAUTHORIZED)
        }
    }

    @Around("@annotation(de.ddb.common.aop.IsNewsletterEditor)")
    public void isNewsletterEditor(JoinPoint joinPoint) {
        boolean isNewsletterEditor = false
        User user = userService.getUserFromSession()

        if (user) {
            List<Privilege> privileges = aasPersonService.getPersonPrivileges(
                    user.id, new AasCredential(user.id, user.password))

            privileges.each { privilege ->
                // TODO (sche): replace with NEWSLETTER if that role exists.
                if (privilege.privilege == PrivilegeEnum.ADMIN) {
                    isNewsletterEditor = true
                }
            }
            if (isNewsletterEditor) {
                joinPoint.proceed()
            }
            else {
                WebUtils.retrieveGrailsWebRequest().getCurrentResponse().sendError(
                        WebUtils.retrieveGrailsWebRequest().getCurrentResponse().SC_FORBIDDEN)
            }
        }
        else {
            WebUtils.retrieveGrailsWebRequest().getCurrentResponse().sendRedirect(
                    grailsLinkGenerator.link(controller: "user", action: "index", params: [
                        referrer: Holders.grailsApplication.mainContext.getBean("de.ddb.common.GetCurrentUrlTagLib").getCurrentUrl()
                    ]))
        }
    }

    @Around("@annotation(de.ddb.common.aop.IsLoggedIn)")
    public void isLoggedIn(JoinPoint joinPoint) {
        if (userService.isUserLoggedIn()) {
            joinPoint.proceed()
        }
        else {
            WebUtils.retrieveGrailsWebRequest().getCurrentResponse().sendRedirect(
                    grailsLinkGenerator.link(controller: "user", action: "index", params: [
                        referrer: Holders.grailsApplication.mainContext.getBean("de.ddb.common.GetCurrentUrlTagLib").getCurrentUrl()
                    ]))
        }
    }
}
