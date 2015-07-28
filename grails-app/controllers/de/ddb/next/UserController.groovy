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

import grails.converters.*

import javax.servlet.http.HttpSession

import net.sf.json.JSONObject

import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.web.json.*
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.openid4java.consumer.ConsumerManager
import org.openid4java.consumer.VerificationResult
import org.openid4java.discovery.DiscoveryInformation
import org.openid4java.discovery.Identifier
import org.openid4java.message.AuthRequest
import org.openid4java.message.ParameterList
import org.openid4java.message.ax.FetchRequest
import org.scribe.model.Token
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.ProxyUtil
import de.ddb.common.Validations
import de.ddb.common.aop.IsLoggedIn
import de.ddb.common.beans.Folder
import de.ddb.common.beans.User
import de.ddb.common.constants.LoginStatus
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.constants.SupportedOauthProvider
import de.ddb.common.constants.SupportedOpenIdProviders
import de.ddb.common.constants.UserStatus
import de.ddb.common.constants.aas.AasPersonSearchQueryParameter
import de.ddb.common.exception.AuthorizationException
import de.ddb.common.exception.BackendErrorException
import de.ddb.common.exception.ConflictException
import de.ddb.common.exception.ItemNotFoundException
import de.ddb.common.oauth.AuthInfo
import de.ddb.common.oauth.GrailsOAuthService
import de.ddb.common.oauth.OAuthProfile

class UserController {
    private final static String SESSION_CONSUMER_MANAGER = "SESSION_CONSUMER_MANAGER_ATTRIBUTE"
    private final static String SESSION_OPENID_PROVIDER = "SESSION_OPENID_PROVIDER_ATTRIBUTE"
    private final static String SESSION_SHIBBOLETH_REFERRER = "shibboleth_originalUrl"
    private final static String SESSION_SHIBBOLETH_HASHCODE = "shibboleth_hashcode"
    private final static String SESSION_SHIBBOLETH_ORIGINAL_HOST = "shibboleth_original_host"
    private final static String SHIBBOLETH_IDP_ATTRIBUTE = "Shib-Identity-Provider"

    def LinkGenerator grailsLinkGenerator
    def aasService
    def bookmarksService
    def configurationService
    def favoritesService
    def languageService
    def messageSource
    def newsletterService
    def savedSearchesService
    def searchService
    def sessionService
    def userService

    def index() {
        log.info "index()"
        render(view: "login",
        model: ['loginStatus': LoginStatus.LOGGED_OUT,
            'referrer': params.referrer,
            'registrationInfoUrl': configurationService.getRegistrationInfoUrl()
        ])
    }

    def doLogin() {
        log.info "doLogin(): login user "
        def loginStatus = LoginStatus.LOGGED_OUT

        // Only perform login, if user is not already logged in
        User user = null
        if(!isCookiesActivated()){
            loginStatus = LoginStatus.NO_COOKIES

        } else if(!userService.isUserLoggedIn()){
            def email = params.email
            def password = params.password

            user = aasService.login(email, password)

            if(user != null){
                loginStatus = LoginStatus.SUCCESS
                user.setNewsletterSubscribed(newsletterService.isSubscriber(user))

                sessionService.createNewSession()
                sessionService.setSessionAttributeIfAvailable(User.SESSION_USER, user)
            }else{
                loginStatus = LoginStatus.FAILURE
            }

        }

        if(loginStatus == LoginStatus.SUCCESS){

            favoritesService.createFavoritesFolderIfNotExisting(user)

            if (user.getStatus().equals(UserStatus.PW_RESET_REQUESTED.toString())) {
                List<String> messages = []
                messages.add("ddbnext.User.PasswordReset_Change")
                redirect(controller: "user", action: "passwordChangePage", params:[messages: messages])
            } else {
                if (params.referrer) {
                    def referrerUrl = params.referrer

                    //Remove the context path from the url, otherwise it will be appear twice in the redirect
                    def contextLength = grailsLinkGenerator.contextPath.length()
                    referrerUrl = referrerUrl.substring(contextLength)

                    log.info "redirect to referrer: " + referrerUrl
                    redirect(uri: referrerUrl)
                } else {
                    redirect(controller: 'favoritesview', action: 'favorites')
                }
            }
        }else{
            render(view: "login", model: ['loginStatus': loginStatus])
        }
    }

    def doLogout() {
        log.info "doLogout(): logout user "

        def user = userService.getUserFromSession()
        logoutUserFromSession()
        if (user != null && user.isShibbolethUser()) {
            redirect (url: configurationService.getShibbolethLogoutUrl() + "?return=" + grailsLinkGenerator.serverBaseURL)
        } else {
            redirect(controller: 'index')
        }
    }




    /* begin saved searches methods */

    @IsLoggedIn
    def getSavedSearches() {
        log.info "getSavedSearches()"
        def user = userService.getUserFromSession()
        def savedSearches = savedSearchesService.findSavedSearchByUserId(user.getId())
        def offset = params[SearchParamEnum.OFFSET.getName()] ? params[SearchParamEnum.OFFSET.getName()].toInteger() : 0
        def rows = params[SearchParamEnum.ROWS.getName()] ? params[SearchParamEnum.ROWS.getName()].toInteger() : 20
        def totalPages = (savedSearches.size() / rows).toInteger()
        def urlsForOrder
        def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
        def queryString = request.getQueryString()

        if (!params.criteria) {
            params.criteria = "creationDate"
        }
        if (!params[SearchParamEnum.ORDER.getName()]) {
            params[SearchParamEnum.ORDER.getName()] = "desc"
        }
        if (params.criteria == "creationDate") {
            if (params[SearchParamEnum.ORDER.getName()] == "asc") {
                savedSearches.sort {a, b -> a.creationDate <=> b.creationDate}
            }
            else {
                savedSearches.sort {a, b -> b.creationDate <=> a.creationDate}
            }
        }
        else {
            if (params[SearchParamEnum.ORDER.getName()] == "asc") {
                savedSearches.sort {a, b -> a.label.toLowerCase() <=> b.label.toLowerCase()}
            }
            else {
                savedSearches.sort {a, b -> b.label.toLowerCase() <=> a.label.toLowerCase()}
            }
        }
        if (totalPages * rows < savedSearches.size()) {
            totalPages++
        }
        if (params[SearchParamEnum.ORDER.getName()] == "asc") {
            urlsForOrder = [
                desc: g.createLink(controller: "user", action: "savedsearches",
                params: [(SearchParamEnum.OFFSET.getName()): 0, (SearchParamEnum.ROWS.getName()): rows, (SearchParamEnum.ORDER.getName()): "desc"]),
                asc: "#"
            ]
        } else {
            urlsForOrder = [
                desc: "#",
                asc: g.createLink(controller: "user", action: "savedsearches",
                params: [(SearchParamEnum.OFFSET.getName()): 0, (SearchParamEnum.ROWS.getName()): rows, (SearchParamEnum.ORDER.getName()): "asc"])
            ]
        }
        render(view: "savedsearches", model: [
            dateString: g.formatDate(ORDER_DATE: new Date(), format: "dd.MM.yyyy"),
            numberOfResults: savedSearches.size(),
            page: offset / rows + 1,
            paginationUrls: savedSearchesService.getPaginationUrls(offset, rows, params[SearchParamEnum.ORDER.getName()], totalPages),
            paginationURL: searchService.buildPagination(savedSearches.size(), urlQuery, request.forwardURI+'?'+queryString),
            results: savedSearchesService.pageSavedSearches(savedSearches, offset, rows),
            rows: rows,
            totalPages: totalPages,
            urlsForOrder: urlsForOrder,
            user: user
        ])
    }

    @IsLoggedIn
    def sendSavedSearches() {
        log.info "sendSavedSearches()"
        def user = userService.getUserFromSession()
        def List emails = []

        if (params.email.contains(',')) {
            emails = params.email.tokenize(',')
        } else {
            emails.add(params.email)
        }
        try {
            sendMail {
                to emails.toArray()
                from configurationService.getFavoritesSendMailFrom()
                replyTo userService.getUserFromSession().getEmail()
                subject g.message(code: "ddbnext.Savedsearches_Of", args: [
                    user.getFirstnameAndLastnameOrNickname()
                ], encodeAs: "none")
                body(view: "_savedSearchesEmailBody", model: [
                    contextUrl: configurationService.getContextUrl(),
                    results:
                    savedSearchesService.findSavedSearchByUserId(user.getId()).sort { a, b ->
                        a.label.toLowerCase() <=> b.label.toLowerCase()
                    },
                    userName: user.getFirstnameAndLastnameOrNickname()
                ])
            }
            flash.message = "ddbnext.favorites_email_was_sent_succ"
        } catch (e) {
            log.info "An error occurred sending the email "+ e.getMessage()
            flash.email_error = "ddbnext.favorites_email_was_not_sent_succ"
        }
        redirect(controller: "user", action: "getSavedSearches")
    }

    /* end saved searches methods */

    private def getRegistrationUrls() {
        return [
            registrationInfoUrl: configurationService.getRegistrationInfoUrl(),
            accountTermsUrl: configurationService.getContextUrl() + configurationService.getAccountTermsUrl(),
            accountPrivacyUrl: configurationService.getContextUrl() + configurationService.getAccountPrivacyUrl()
        ]
    }

    def registration() {
        log.info "registration()"
        if (configurationService.isUserRegistrationFeaturesEnabled()) {
            render(view: "registration", model: getRegistrationUrls())
        }
        else {
            render(view: "/message/message", model: [errors: [
                    "ddbnext.Error_Registration_Disabled"
                ]])
        }
    }

    def signup() {
        log.info "signup()"
        List<String> errors = []
        List<String> messages = []
        errors = Validations.validatorRegistration(params.username, params.email, params.passwd, params.conpasswd)
        if (errors == null || errors.isEmpty()) {
            def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))
            def template = messageSource.getMessage("ddbnext.User.Create_Account_Mailtext", null, locale)
            JSONObject userjson = aasService.getPersonJson(params.username, null, null, params.lname, params.fname, null, null, params.email, params.passwd, configurationService.getCreateConfirmationLink(), template, null, null)
            try {
                aasService.createPerson(userjson)
                messages.add("ddbnext.User.Create_Success")
                redirect(controller: "user",action: "confirmationPage" , params: [errors: errors, messages: messages])
            } catch (ConflictException e) {
                log.error "Conflict: user with given data already exists. username:" + params.username + ",email:" + params.email, e
                String conflictField = e.getMessage().replaceFirst(".*?'(.*?)'.*", "\$1")
                if (params.username.equals(conflictField)) {
                    errors.add("ddbcommon.Conflict_User_Name")
                } else if (params.email.equals(conflictField)) {
                    errors.add("ddbcommon.Conflict_User_Email")
                } else {
                    errors.add("ddbcommon.Conflict_User_Common")
                }
                render(view: "registration" , model: [errors: errors, messages: messages, params: params] << getRegistrationUrls())
            }
        } else {
            render(view: "registration" , model: [errors: errors, messages: messages, params: params] << getRegistrationUrls())
        }
    }

    def passwordResetPage() {
        log.info "passwordResetPage()"
        List<String> errors = []
        List<String> messages = []
        if (params.errors != null) {
            if (params.errors instanceof String) {
                errors.add(params.errors)
            } else {
                errors.addAll(params.errors)
            }
        }
        if (params.messages != null) {
            if (params.messages instanceof String) {
                messages.add(params.messages)
            } else {
                messages.addAll(params.messages)
            }
        }
        render(view: "resetpassword", model: [errors: errors, messages: messages])
    }

    def passwordReset() {
        log.info "passwordReset()"
        List<String> messages = []
        List<String> errors = []
        if (StringUtils.isBlank(params.username)) {
            errors.add("ddbcommon.Error_Username_Empty")
        }
        if (errors == null || errors.isEmpty()) {
            try {
                def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))
                def template = messageSource.getMessage("ddbnext.User.PasswordReset_Mailtext", null, locale)
                aasService.resetPassword(params.username, aasService.getResetPasswordJson(configurationService.getPasswordResetConfirmationLink(), template, null))
                messages.add("ddbcommon.User.PasswordReset_Success")
            } catch (ItemNotFoundException e) {
                log.error "NotFound: a user with given name " + params.username + " was not found", e
                errors.add("ddbcommon.Error_Username_Notfound")
            }
        }
        if (!messages.isEmpty()) {
            params.messages = messages
        }
        if (!errors.isEmpty()) {
            params.errors = errors
        }
        redirect(controller: "user",action: "passwordResetPage" , params: params)
    }

    @IsLoggedIn
    def profile() {
        log.info "profile()"
        User user = userService.getUserFromSession().clone()
        if (params.username) {
            user.setUsername(params.username)
            user.setFirstname(params.fname)
            user.setLastname(params.lname)
            user.setEmail(params.email)
        }
        if (!user.isConsistent()) {
            throw new BackendErrorException("user-attributes are not consistent")
        }
        List<String> errors = []
        List<String> messages = []
        if (params.errors != null) {
            if (params.errors instanceof String) {
                errors.add(params.errors)
            } else {
                errors.addAll(params.errors)
            }
        }
        if (params.messages != null) {
            if (params.messages instanceof String) {
                messages.add(params.messages)
            } else {
                messages.addAll(params.messages)
            }
        }
        render(view: "profile", model: [
            favoritesCount: getFavoriteCount(user),
            savedSearchesCount: savedSearchesService.getSavedSearchesCount(),
            user: user,
            errors:errors,
            messages: messages])
    }

    private getFavoriteCount(User user) {
        Folder mainFavoritesFolder = bookmarksService.findMainBookmarksFolder(user.getId())
        List favorites = favoritesService.getFavoriteList(user, mainFavoritesFolder)
        def favoritesCount = favorites.size()

        return favoritesCount
    }

    @IsLoggedIn
    def saveProfile() {
        log.info "saveProfile()"
        List<String> errors = []
        List<String> messages = []
        boolean eMailDifference = false
        boolean profileDifference = false
        boolean newsletterDifference = false
        User user = userService.getUserFromSession().clone()

        if (!user.isConsistent()) {
            throw new BackendErrorException("user-attributes are not consistent")
        }
        if (!user.isOpenIdUser()) {
            if (StringUtils.isBlank(params.username) || params.username.length() < 2) {
                errors.add("ddbcommon.Error_Username_Empty")
            }
            if (StringUtils.isBlank(params.email)) {
                errors.add("ddbcommon.Error_Email_Empty")
            }
            if (!Validations.validatorEmail(params.email)) {
                errors.add("ddbcommon.Error_Valid_Email_Address")
            }
        }
        if (errors == null || errors.isEmpty()) {
            if (!user.isOpenIdUser()) {
                if (Validations.isDifferent(user.getFirstname(), params.fname)
                || Validations.isDifferent(user.getLastname(), params.lname)
                || Validations.isDifferent(user.getUsername(), params.username)) {
                    profileDifference = true
                }
                if (Validations.isDifferent(user.getEmail(), params.email)) {
                    eMailDifference = true
                }
            }
            if ((params.newsletter && !user.newsletterSubscribed)
            || (!params.newsletter && user.newsletterSubscribed)) {
                newsletterDifference = true
            }

            if (!profileDifference && !eMailDifference && !newsletterDifference) {
                errors.add("ddbcommon.User.Profile_NoValuesChanged")
            }

            if (profileDifference) {
                //update user in aas
                JSONObject aasUser = aasService.getPerson(user.getId())
                aasUser.put(AasPersonSearchQueryParameter.NICKNAME, params.username)
                aasUser.put(AasPersonSearchQueryParameter.FORENAME, params.fname)
                aasUser.put(AasPersonSearchQueryParameter.SURNAME, params.lname)
                try {
                    user.setUsername(params.username)
                    user.setFirstname(params.fname)
                    user.setLastname(params.lname)
                    aasService.updatePerson(user.getId(), aasUser)
                    messages.add("ddbcommon.User.Profile_Update_Success")
                } catch (ConflictException e) {
                    log.error "Conflict: user with given data already exists. username:" + params.username, e
                    errors.add("ddbcommon.Conflict_User_Name")
                }
            }
            if (eMailDifference && (errors == null || errors.isEmpty())) {
                try {
                    //update email in aas
                    def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))
                    def template = messageSource.getMessage("ddbnext.User.Email_Update_Mailtext", null, locale)
                    aasService.updateEmail(user.getId(), aasService.getUpdateEmailJson(params.email, configurationService.getEmailUpdateConfirmationLink(), template, null))
                    messages.add("ddbcommon.User.Email_Update_Success")
                } catch (ConflictException e) {
                    user.setEmail(params.email)
                    log.error "Conflict: user with given data already exists. email:" + params.email, e
                    errors.add("ddbcommon.Conflict_User_Email")
                }
            }
            if (newsletterDifference && (errors == null || errors.isEmpty())) {
                log.info "parameter newsletter: ${params.newsletter}"
                updateNewsletterSubscription(user, messages, errors)
            }
            if (errors == null || errors.isEmpty()) {
                //adapt user-attributes in session
                sessionService.setSessionAttributeIfAvailable(User.SESSION_USER, user)
            }
        }
        if (!messages.isEmpty()) {
            params.messages = messages
        }
        if (!errors.isEmpty()) {
            params.errors = errors
        }
        redirect(controller:"user", action:"profile", params:params)
    }

    private updateNewsletterSubscription(user, messages, errors) {
        try {
            if (params.newsletter) {
                newsletterService.addSubscriber(user)
                user.setNewsletterSubscribed(true)
            } else {
                newsletterService.removeSubscriber(user)
                user.setNewsletterSubscribed(false)
            }

            messages.add("ddbnext.User.Newsletter_Update_Success")
        } catch (Exception e) {
            log.error "fail to update newsletter subscription", e
            errors.add("fail to update newsletter subscription")
        }
    }

    @IsLoggedIn
    def passwordChangePage() {
        log.info "passwordChangePage()"
        User user = userService.getUserFromSession()
        if (user.isOpenIdUser()) {
            //password-change is only for aas-users
            redirect(controller:"index")
        }
        if (!user.isConsistent()) {
            throw new BackendErrorException("user-attributes are not consistent")
        }
        List<String> errors = []
        List<String> messages = []
        if (params.errors != null) {
            if (params.errors instanceof String) {
                errors.add(params.errors)
            } else {
                errors.addAll(params.errors)
            }
        }
        if (params.messages != null) {
            if (params.messages instanceof String) {
                messages.add(params.messages)
            } else {
                messages.addAll(params.messages)
            }
        }
        render(view: "changepassword", model: [
            favoritesCount: getFavoriteCount(user),
            savedSearchesCount: savedSearchesService.getSavedSearchesCount(),
            user: user,
            errors: errors,
            messages: messages])
    }

    def passwordChange() {
        log.info "passwordChange()"
        List<String> errors = []
        List<String> messages = []
        User user = userService.getUserFromSession().clone()
        if (user.isOpenIdUser()) {
            //password-change is only for aas-users
            redirect(controller:"index")
        }
        if (user?.getPassword() == null) {
            forward controller: "error", action: "serverError"
        }
        errors = Validations.validatorPasswordChange(user?.getPassword(), params.oldpassword, params.newpassword, params.confnewpassword)
        if (errors == null || errors.isEmpty()) {
            //change password in AAS
            aasService.changePassword(user?.getId(), aasService.getChangePasswordJson(params.newpassword))
            messages.add("ddbcommon.User.Password_Change_Success")
            //adapt user-attributes in session
            user.setPassword(params.newpassword)
            sessionService.setSessionAttributeIfAvailable(User.SESSION_USER, user)
            params.remove("oldpassword")
            params.remove("newpassword")
            params.remove("confnewpassword")
        }
        if (!messages.isEmpty()) {
            params.messages = messages
        }
        if (!errors.isEmpty()) {
            params.errors = errors
        }

        render(view: "profile", model: [
            favoritesCount: getFavoriteCount(user),
            savedSearchesCount: savedSearchesService.getSavedSearchesCount(),
            user: user,
            errors: errors,
            messages: messages])
    }

    @IsLoggedIn
    def delete() {
        log.info "delete()"
        List<String> errors = []
        List<String> messages = []
        User user = userService.getUserFromSession().clone()
        if (!user.isConsistent()) {
            throw new BackendErrorException("user-attributes are not consistent")
        }
        if (user.isOpenIdUser()) {
            //password-change is only for aas-users
            redirect(controller:"index")
        }
        try {
            aasService.deletePerson(user.id)

            //remove all saved searches
            log.info "delete SavedSearches"
            savedSearchesService.deleteSavedSearchesByUserId(user.id)

            //remove all bookmark related content
            log.info "delete bookmark content"
            bookmarksService.deleteAllUserContent(user.id)

        } catch (AuthorizationException e) {
            forward controller: "error", action: "auth"
        }
        logoutUserFromSession()

        messages.add("ddbnext.User.Delete_Confirm")
        redirect(controller: "user", action: "confirmationPage" , params: [errors: errors, messages: messages])
    }

    def confirmationPage() {
        log.info "confirmationPage()"
        List<String> errors = []
        List<String> messages = []
        if (params.errors != null) {
            if (params.errors instanceof String) {
                errors.add(params.errors)
            } else {
                errors.addAll(params.errors)
            }
        }
        if (params.messages != null) {
            if (params.messages instanceof String) {
                messages.add(params.messages)
            } else {
                messages.addAll(params.messages)
            }
        }
        render(view: "confirm", model: [errors: errors, messages: messages])
    }

    def confirm() {
        log.info "confirm()"
        if (StringUtils.isBlank(params.type)) {
            forward controller: "error", action: "serverError"
        }
        List<String> messages = []
        List<String> errors = []
        def jsonuser
        try {
            jsonuser = aasService.confirm(params.id, params.token)
            if (params.type.equals("emailupdate")) {
                messages.add("ddbcommon.User.Email_Confirm_Success")
            } else if (params.type.equals("passwordreset")) {
                messages.add("ddbcommon.User.Pwreset_Confirm_Success")
            } else if (params.type.equals("create")) {
                messages.add("ddbcommon.User.Create_Confirm_Success")
            }
            // set changed attributes in user-object in session
            if (userService.isUserLoggedIn()) {
                User user = userService.getUserFromSession().clone()
                if (!user.isConsistent()
                || StringUtils.isBlank(jsonuser.getString(AasPersonSearchQueryParameter.EMAIL))) {
                    throw new BackendErrorException("user-attributes are not consistent")
                }
                user.setEmail(jsonuser.getString(AasPersonSearchQueryParameter.EMAIL))
                if (jsonuser.containsKey(AasPersonSearchQueryParameter.PSWD)) {
                    user.setPassword(jsonuser.getString(AasPersonSearchQueryParameter.PSWD))
                }
                sessionService.setSessionAttributeIfAvailable(User.SESSION_USER, user)
            }
        }
        catch (ItemNotFoundException e) {
            log.error "NotFound: confirmation does not exist. uid:" + params.id + ", token:" + params.token, e
            errors.add("ddbcommon.Error.Confirmation_Not_Found")
        }
        redirect(controller: "user",action: "confirmationPage" , params: [errors: errors, messages: messages])
    }

    @IsLoggedIn
    def dashboard() {
        render(view: "dashboard", model: [institutions: searchService.getNewestInstitutions()])
    }

    def requestOauthLogin() {
        SupportedOauthProvider provider = SupportedOauthProvider.valueOfName(params.provider)

        new ProxyUtil().setProxy(true)
        if (provider) {
            GrailsOAuthService service = resolveService(provider.name)

            if (!service) {
                redirect(controller: "index")
            }

            sessionService.createNewSession()
            sessionService.setSessionAttributeIfAvailable("${provider.name}_originalUrl", params.referrer)

            AuthInfo authInfo = service.getAuthInfo(new URL(new URL(configurationService.getPublicUrl()),
                    "login/doOauthLogin?provider=" + provider.name).toString())

            sessionService.setSessionAttributeIfAvailable("${provider.name}_authInfo", authInfo)
            redirect(url: authInfo.authUrl)
        }
        else {
            render(view: "login", model: ['loginStatus': LoginStatus.AUTH_PROVIDER_UNKNOWN])
        }
    }

    def requestOpenIdLogin() {
        log.info "requestOpenIdLogin()"
        def provider = params.provider
        def loginStatus = LoginStatus.AUTH_PROVIDER_REQUEST

        String discoveryUrl = ""

        new ProxyUtil().setProxy(false)

        FetchRequest fetch = FetchRequest.createFetchRequest()

        if (provider == SupportedOpenIdProviders.YAHOO.toString()) {
            discoveryUrl = "https://me.yahoo.com"
            fetch.addAttribute("Email", "http://axschema.org/contact/email", true)
            fetch.addAttribute("Fullname", "http://axschema.org/namePerson", true)
        }else {
            loginStatus = LoginStatus.AUTH_PROVIDER_UNKNOWN
        }

        if(loginStatus != LoginStatus.AUTH_PROVIDER_REQUEST) {
            render(view: "login", model: ['loginStatus': loginStatus])
            return
        }


        log.info "requestOpenIdLogin(): discoveryUrl="+discoveryUrl
        ConsumerManager manager = new ConsumerManager()

        sessionService.createNewSession()
        sessionService.setSessionAttributeIfAvailable(SESSION_OPENID_PROVIDER, provider)
        sessionService.setSessionAttributeIfAvailable(SESSION_CONSUMER_MANAGER, manager)

        // Delete problem with url page with # and manager.authenticate
        def referrerUrl = params.referrer.replaceAll("#.*", "")

        String returnURL = configurationService.getContextUrl() + "/login/doOpenIdLogin?referrer=" + referrerUrl
        List discoveries = manager.discover(discoveryUrl)
        DiscoveryInformation discovered = manager.associate(discoveries)
        AuthRequest authReq = manager.authenticate(discovered, returnURL)
        authReq.addExtension(fetch)


        // Leave DDB for login on OpenID-provider
        redirect(url: authReq.getDestinationUrl(true))

    }

    def doOpenIdLogin() {
        def loginStatus = LoginStatus.LOGGED_OUT

        log.info "doOpenIdLogin(): got OpenID login request"

        ConsumerManager manager = sessionService.getSessionAttributeIfAvailable(SESSION_CONSUMER_MANAGER)
        if(manager) {
            def provider = sessionService.getSessionAttributeIfAvailable(SESSION_OPENID_PROVIDER)

            ParameterList openidResp = ParameterList.createFromQueryString(request.getQueryString())
            DiscoveryInformation discovered = (DiscoveryInformation) sessionService.getSessionAttributeIfAvailable("discovered")
            String returnURL = configurationService.getContextUrl() + "/login/doOpenIdLogin"
            String receivingURL =  returnURL + "?" + request.getQueryString()
            VerificationResult verification = manager.verify(receivingURL.toString(), openidResp, discovered)
            Identifier verified = verification.getVerifiedId()

            if (verified != null) {
                log.info "doOpenIdLogin(): success verification"

                def username = null
                def firstName = null
                def lastName = null
                def email = null
                def identifier = null

                if (provider == SupportedOpenIdProviders.YAHOO.toString()) {
                    username = params["openid.ax.value.fullname"]
                    def index = username.trim().lastIndexOf(' ')
                    if (index > 0) {
                        firstName = username.substring(0, index)
                        lastName = username.substring(index + 1)
                    }
                    email = params["openid.ax.value.email"]
                    identifier = verified.getIdentifier()
                }else {
                    render(view: "login", model: [
                        'loginStatus': LoginStatus.AUTH_PROVIDER_UNKNOWN]
                    )
                    return
                }


                //log.info "doOpenIdLogin(): credentials:  " + username + " / " + email + " / " + identifier

                // Create new session, because the old one might be corrupt due to the redirect to the OpenID provider
                //                getSessionObject(false)?.invalidate()
                //                HttpSession newSession = getSessionObject(true)
                sessionService.destroySession()
                HttpSession newSession = sessionService.createNewSession()

                User user = new User()
                user.setId(userService.encodeAsMD5(identifier))
                user.setEmail(email)
                user.setUsername(username)
                user.setFirstname(firstName)
                user.setLastname(lastName)
                user.setPassword(null)
                user.setOpenIdUser(true)
                user.setNewsletterSubscribed(newsletterService.isSubscriber(user))
                log.info(user.toString())

                sessionService.setSessionAttribute(newSession, User.SESSION_USER, user)

                loginStatus = LoginStatus.SUCCESS

                favoritesService.createFavoritesFolderIfNotExisting(user)

                // deactivated until we have unique id for both OpenId and AAS
                // aasService.createOrUpdatePersonAsAdmin(user)
            }else {
                log.info "doOpenIdLogin(): failure verification"
                loginStatus = LoginStatus.AUTH_PROVIDER_DENIED
            }
        }

        if(loginStatus == LoginStatus.SUCCESS) {
            if (params.referrer) {
                def referrerUrl = params.referrer

                //Remove the context path from the url, otherwise it will be appear twice in the redirect
                def contextLength = grailsLinkGenerator.contextPath.length()
                referrerUrl = referrerUrl.substring(contextLength)

                log.info "redirect to referrer: " + referrerUrl
                redirect(uri: referrerUrl)
            }
            else {
                redirect(controller: 'favoritesview', action: 'favorites')
            }
        }else {
            render(view: "login", model: ['loginStatus': loginStatus])
        }
    }

    def requestShibbolethLogin() {
        sessionService.createNewSession()
        sessionService.setSessionAttributeIfAvailable(SESSION_SHIBBOLETH_ORIGINAL_HOST, grailsLinkGenerator.serverBaseURL)
        if (params.referrer) {
            sessionService.setSessionAttributeIfAvailable(SESSION_SHIBBOLETH_REFERRER, params.referrer)
        }
        String hashcode = UUID.randomUUID().toString()
        sessionService.setSessionAttributeIfAvailable(SESSION_SHIBBOLETH_HASHCODE, hashcode)
        redirect (url: configurationService.getShibbolethLoginUrl() + "?hashcode=" + hashcode)
        return
    }

    def doShibbolethLogin() {
        Map shibAtts = configurationService.getShibbolethAttributes()
        if (!shibAtts.targetedId || !request.getAttribute(shibAtts.targetedId)) {
            println "Shibboleth PrimaryKey-Attribute not available"
            throw new BackendErrorException("Shibboleth PrimaryKey-Attribute not available")
        }
        if (!shibAtts.mail || !request.getAttribute(shibAtts.mail)) {
            println "Shibboleth Mail-Attribute not available"
            throw new BackendErrorException("Shibboleth Mail-Attribute not available")
        }
        if (!request.getAttribute(SHIBBOLETH_IDP_ATTRIBUTE)) {
            println "Shibboleth IDP-Attribute not available"
            throw new BackendErrorException("Shibboleth IDP-Attribute not available")
        }
        if (!sessionService.getSessionAttributeIfAvailable(SESSION_SHIBBOLETH_ORIGINAL_HOST)) {
            println "Shibboleth Original Host not available"
            throw new BackendErrorException("Shibboleth Original Host not available")
        }
        if (!params.hashcode || !sessionService.getSessionAttributeIfAvailable(SESSION_SHIBBOLETH_HASHCODE)
        || !params.hashcode.equals(sessionService.getSessionAttributeIfAvailable(SESSION_SHIBBOLETH_HASHCODE))) {
            println "Shibboleth Hashcode not correct"
            throw new BackendErrorException("Shibboleth Hashcode not correct")
        }

        User user = new User()
        user.setId(String.format("%040x", new BigInteger(1, (request.getAttribute(SHIBBOLETH_IDP_ATTRIBUTE) + shibAtts.mail).getBytes("UTF-8"))))
        user.setUsername(String.format("%040x", new BigInteger(1, (request.getAttribute(SHIBBOLETH_IDP_ATTRIBUTE) + shibAtts.mail).getBytes("UTF-8"))))
        user.setEmail(request.getAttribute(shibAtts.mail))
        if (shibAtts.cn && request.getAttribute(shibAtts.cn)) {
            user.setFirstname(new String(request.getAttribute(shibAtts.cn).getBytes("ISO-8859-1"), "UTF-8"))
        }
        if (shibAtts.sn && request.getAttribute(shibAtts.sn)) {
            user.setLastname(new String(request.getAttribute(shibAtts.sn).getBytes("ISO-8859-1"), "UTF-8"))
        }
        user.setShibbolethUser(true)
        user.setNewsletterSubscribed(newsletterService.isSubscriber(user))

        //save user in session
        sessionService.setSessionAttributeIfAvailable(User.SESSION_USER, user)

        favoritesService.createFavoritesFolderIfNotExisting(user)

        // deactivated until we have unique id for both OpenId and AAS
        // aasService.createOrUpdatePersonAsAdmin(user)

        if (session[SESSION_SHIBBOLETH_REFERRER]) {
            //Remove the context path from the url, otherwise it will be appear twice in the redirect
            def contextLength = grailsLinkGenerator.contextPath.length()
            def referrerUrl = session[SESSION_SHIBBOLETH_REFERRER]
            referrerUrl = referrerUrl.substring(contextLength)
            redirect (url: sessionService.getSessionAttributeIfAvailable(SESSION_SHIBBOLETH_ORIGINAL_HOST) + referrerUrl)
        } else {
            redirect (url: sessionService.getSessionAttributeIfAvailable(SESSION_SHIBBOLETH_ORIGINAL_HOST) + "/user/favorites/" + user.getId())
        }
        return
    }

    private def resolveService(provider) {
        def serviceName = "${ provider as String }AuthService"
        grailsApplication.mainContext.getBean(serviceName)
    }

    def doOauthLogin() {
        GrailsOAuthService service = resolveService(params.provider)

        if (!service) {
            redirect(controller: "index")
        }

        new ProxyUtil().setProxy(true)

        AuthInfo authInfo = sessionService.getSessionAttributeIfAvailable("${params.provider}_authInfo")
        Token accessToken = service.getAccessToken(authInfo.service, params, authInfo.requestToken)
        OAuthProfile profile = service.getProfile(authInfo.service, accessToken)

        sessionService.setSessionAttributeIfAvailable("${params.provider}_authToken", accessToken)
        sessionService.setSessionAttributeIfAvailable("${params.provider}_profile", profile)

        User user = new User()

        user.setId(params.provider + "_" + userService.encodeAsMD5(profile.uid))
        user.setEmail(profile.email)
        user.setUsername(profile.username)
        user.setFirstname(profile.firstname)
        user.setLastname(profile.lastname)
        user.setOpenIdUser(true)
        user.setNewsletterSubscribed(newsletterService.isSubscriber(user))
        sessionService.setSessionAttributeIfAvailable(User.SESSION_USER, user)

        favoritesService.createFavoritesFolderIfNotExisting(user)

        // deactivated until we have unique id for both OpenId and AAS
        // aasService.createOrUpdatePersonAsAdmin(user)
        redirect(uri: (session["${params.provider}_originalUrl"] ?: '/') - request.contextPath)
    }

    @IsLoggedIn
    def showApiKey() {
        log.info "showApiKey()"
        User user = userService.getUserFromSession()
        def apiKey = user.apiKey
        String apiKeyTermsUrl = configurationService.getContextUrl() + configurationService.getApiKeyTermsUrl()

        if(apiKey) {
            render(view: "apiKey", model: [
                favoritesCount: getFavoriteCount(user),
                savedSearchesCount: savedSearchesService.getSavedSearchesCount(),
                user: user,
                apiKeyDocUrl: configurationService.getApiKeyDocUrl(),
                apiKeyTermsUrl: apiKeyTermsUrl
            ])
        }else {
            render(view: "requestApiKey", model: [
                favoritesCount: getFavoriteCount(user),
                savedSearchesCount: savedSearchesService.getSavedSearchesCount(),
                apiKeyDocUrl: configurationService.getApiKeyDocUrl(),
                apiKeyTermsUrl: apiKeyTermsUrl
            ])
        }
    }

    @IsLoggedIn
    def requestApiKey() {
        log.info "requestApiKey()"
        def isConfirmed = false
        if(params.apiConfirmation){
            isConfirmed = true
        }
        if(isConfirmed){
            User user = userService.getUserFromSession()
            String newApiKey = aasService.createApiKey()
            JSONObject aasUser = aasService.getPerson(user.getId())
            aasUser.put(AasPersonSearchQueryParameter.APIKEY, newApiKey)
            aasService.updatePerson(user.getId(), aasUser)
            user.setApiKey(newApiKey)
            sendApiKeyPerMail(user)
        }else{
            flash.error = "ddbnext.Api_Not_Confirmed"
        }
        redirect(controller: 'user', action: 'showApiKey')
    }

    @IsLoggedIn
    def deleteApiKey() {
        log.info "deleteApiKey()"
        User user = userService.getUserFromSession()
        JSONObject aasUser = aasService.getPerson(user.getId())
        aasUser.put(AasPersonSearchQueryParameter.APIKEY, null)
        aasService.updatePerson(user.getId(), aasUser)
        user.setApiKey(null)

        List<String> messages = []
        List<String> errors = []
        messages.add("ddbnext.Api_Deleted")
        redirect(controller: "user",action: "confirmationPage" , params: [errors: errors, messages: messages])
    }

    private def sendApiKeyPerMail(User user) {
        log.info "sendApiKeyPerMail()"
        if (user != null) {

            String apiKeyTermsUrl = configurationService.getContextUrl() + configurationService.getApiKeyTermsUrl()

            def List emails = []
            emails.add(user.email)
            try {
                sendMail {
                    to emails.toArray()
                    from configurationService.getFavoritesSendMailFrom()
                    replyTo configurationService.getFavoritesSendMailFrom()
                    subject g.message(code:"ddbnext.Api_Key_Send_Mail_Subject", encodeAs: "none")
                    body( view:"_apiKeyEmailBody", model:[
                        user: user,
                        apiKeyDocUrl: configurationService.getApiKeyDocUrl(),
                        apiKeyTermsUrl: apiKeyTermsUrl
                    ])
                }
            } catch (e) {
                log.info "sendApiKeyPerMail(): An error occurred sending the email "+ e.getMessage()
            }
        }
    }

    private boolean logoutUserFromSession() {
        sessionService.removeSessionAttributeIfAvailable(User.SESSION_USER)
        sessionService.destroySession()
    }

    private boolean isCookiesActivated() {
        if(request.getCookies() != null && request.getCookies().length > 0){
            return true
        }else{
            return false
        }
    }
}
