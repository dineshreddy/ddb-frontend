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
import de.ddb.common.constants.ConfirmType
import de.ddb.common.exception.ConflictException
import de.ddb.common.exception.ItemNotFoundException

class NewsletterController {
    def newsletterService

    @IsNewsletterEditor
    def getNewsletters() {
        String result = ""
        def newsletters = newsletterService.getNewsletters(params.size, params.offset)

        newsletters.eachWithIndex {newsletter, index ->
            if (index > 0) {
                result += ","
            }
            result += newsletter.source.email
        }
        response.setHeader("Content-disposition", "attachment; filename=Newsletters.csv")
        render(contentType: "text/csv", text: result)
    }

    def index() {
        render(view: "newsletter")
    }

    def subscribe() {
        ConfirmType confirmType = ConfirmType.NEWSLETTER_SUBSCRIBE_ERROR

        if (params.email) {
            try {
                String confirmationLink = newsletterService.subscribe(params.email)

                newsletterService.sendConfirmationMail(params.email, confirmationLink)
                confirmType = ConfirmType.NEWSLETTER_SUBSCRIBE_SUCCESS
            }
            catch (ConflictException e) {
                confirmType = ConfirmType.NEWSLETTER_SUBSCRIBE_CONFLICT
            }
            catch (Exception e) {
                // no error message defined yet
                log.error "subscribe failed", e
            }
        }
        else {
            confirmType = ConfirmType.NEWSLETTER_SUBSCRIBE_EMAIL_REQUIRED
        }
        redirect(controller: "user", action: "confirmationPage", id: confirmType.name)
    }

    def unsubscribe() {
        ConfirmType confirmType = ConfirmType.NEWSLETTER_UNSUBSCRIBE_ERROR

        if (params.email) {
            try {
                newsletterService.unsubscribe(params.email)
                confirmType = ConfirmType.NEWSLETTER_UNSUBSCRIBE_SUCCESS
            }
            catch (ItemNotFoundException e) {
                confirmType = ConfirmType.NEWSLETTER_UNSUBSCRIBE_EMAIL_NOT_FOUND
            }
            catch (Exception e) {
                // no error message defined yet
                log.error "unsubscribe failed", e
            }
        }
        else {
            confirmType = ConfirmType.NEWSLETTER_UNSUBSCRIBE_EMAIL_REQUIRED
        }
        redirect(controller: "user", action: "confirmationPage", id: confirmType.name)
    }
}