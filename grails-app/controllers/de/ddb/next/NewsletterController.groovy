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
package de.ddb.next

import de.ddb.common.aop.IsNewsletterEditor
import de.ddb.common.exception.ConflictException
import de.ddb.common.exception.ItemNotFoundException

class NewsletterController {
    def newsletterService

    @IsNewsletterEditor
    def getNewsletters() {
        String result = ""
        def newsletters = newsletterService.getNewsletters(params.size, params.offset)

        newsletters.each {newsletter ->
            result += newsletter.source.email + "\n"
        }
        response.setHeader("Content-disposition", "attachment; filename=Newsletters.csv")
        render(contentType: "text/csv", text: result)
    }

    def index() {
        render(view: "newsletter")
    }

    def subscribe() {
        flash.errors = []
        flash.messages = []

        if (params.email) {
            try {
                String confirmationLink = newsletterService.subscribe(params.email)

                newsletterService.sendConfirmationMail(params.email, confirmationLink)
                flash.messages += "ddbcommon.User.Newsletter_Subscribe_Success"
            }
            catch (ConflictException e) {
                flash.errors += "ddbcommon.User.Newsletter_Subscribe_Conflict"
            }
            catch (Exception e) {
                // no error message defined yet
                log.error "subscribe failed", e
            }
        }
        else {
            flash.errors += "ddbcommon.User.Newsletter_Email_Required"
        }
        flash.headline = "ddbnext.Newsletter_Subscribe_Title"
        flash.title = "ddbnext.Newsletter_Subscribe_Title"
        redirect(controller: "user", action: "confirmationPage")
    }

    def unsubscribe() {
        if (params.email) {
            try {
                newsletterService.unsubscribe(params.email)
                flash.headline = "ddbnext.Newsletter_Unsubscribe_Title"
                flash.messages = [
                    "ddbcommon.User.Newsletter_Unsubscribe_Success"
                ]
                flash.title = "ddbnext.Newsletter_Unsubscribe_Title"
            }
            catch (ItemNotFoundException e) {
                flash.errors = [
                    "ddbcommon.User.Newsletter_Unsubscribe_Error"
                ]
                flash.headline = "ddbnext.Newsletter_Unsubscribe_Email_Not_Found"
                flash.title = "ddbnext.Newsletter_Unsubscribe_Email_Not_Found"
            }
            catch (Exception e) {
                // no error message defined yet
                log.error "unsubscribe failed", e
            }
        }
        else {
            flash.errors = [
                "ddbcommon.User.Newsletter_Email_Required"
            ]
        }
        redirect(controller: "user", action: "confirmationPage")
    }
}