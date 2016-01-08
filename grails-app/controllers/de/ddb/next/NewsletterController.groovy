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

import de.ddb.common.exception.ConflictException
import de.ddb.common.exception.ItemNotFoundException

class NewsletterController {
    def newsletterService

    def index() {
        render(view: "newsletter")
    }

    def subscribe() {
        List<String> errors = []
        List<String> messages = []

        if (params.email) {
            try {
                String confirmationLink = newsletterService.subscribe(params.email)

                newsletterService.sendConfirmationMail(params.email, confirmationLink)
                messages += "ddbcommon.User.Newsletter_Subscribe_Success"
            }
            catch (ConflictException e) {
                errors += "ddbcommon.User.Newsletter_Subscribe_Conflict"
            }
            catch (Exception e) {
                // no error message defined yet
                log.error "subscribe failed", e
            }
        }
        else {
            errors += "ddbcommon.User.Newsletter_Email_Required"
        }
        render(view: "../user/confirm", model: [
            errors: errors,
            headline: "ddbnext.Newsletter_Subscribe_Title",
            messages: messages,
            title: "ddbnext.Newsletter_Subscribe_Title"
        ])
    }

    def unsubscribe() {
        List<String> errors = []
        String headline
        List<String> messages = []
        String title

        if (params.email) {
            try {
                newsletterService.unsubscribe(params.email)
                messages += "ddbcommon.User.Newsletter_Unsubscribe_Success"
                headline = "ddbnext.Newsletter_Unsubscribe_Title"
                title = "ddbnext.Newsletter_Unsubscribe_Title"
            }
            catch (ItemNotFoundException e) {
                errors += "ddbcommon.User.Newsletter_Unsubscribe_Error"
                headline = "ddbnext.Newsletter_Unsubscribe_Email_Not_Found"
                title = "ddbnext.Newsletter_Unsubscribe_Email_Not_Found"
            }
            catch (Exception e) {
                // no error message defined yet
                log.error "unsubscribe failed", e
            }
        }
        else {
            errors += "ddbcommon.User.Newsletter_Email_Required"
        }
        render(view: "../user/confirm", model: [
            errors: errors,
            headline: headline,
            messages: messages,
            title: title
        ])
    }
}