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

class NewsletterController {
    def configurationService
    def newsletterService

    def index() {
        render(view: "newsletter")
    }

    def subscribe() {
        def errors = []
        def messages = []

        if (params.email) {
            try {
                String confirmationToken = newsletterService.subscribe(params.email)
                String confirmationLink = configurationService.getNewsletterConfirmationLink()

                confirmationLink = confirmationLink.replace("|confirmationToken|", confirmationToken)
                confirmationLink = confirmationLink.replace("|id|", "id") // id is not used here
                messages += "ddbcommon.User.Newsletter_Subscribe_Success"
            }
            catch (ConflictException e) {
                errors += "ddbcommon.User.Newsletter_Subscribe_Conflict"
            }
            catch (Exception e) {
                // no error message defined yet
            }
        }
        else {
            errors += "ddbcommon.User.Newsletter_Email_Required"
        }
        redirect(controller: "user", action: "confirmationPage", params: [errors: errors, messages: messages])
    }

    def unsubscribe() {
        def errors = []
        def messages = []

        if (params.email) {
            if (newsletterService.unsubscribe(params.email)) {
                messages += "ddbcommon.User.Newsletter_Unsubscribe_Success"
            }
            else {
                errors += "ddbcommon.User.Newsletter_Unsubscribe_Error"
            }
        }
        else {
            errors += "ddbcommon.User.Newsletter_Email_Required"
        }
        redirect(controller: "user", action: "confirmationPage", params: [errors: errors, messages: messages])
    }
}