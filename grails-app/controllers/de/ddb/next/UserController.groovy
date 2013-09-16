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
package de.ddb.next

import grails.converters.*

import javax.servlet.http.HttpSession

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
import org.openid4java.util.HttpClientFactory
import org.openid4java.util.ProxyProperties
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.next.beans.User
import de.ddb.next.exception.AuthorizationException
import de.ddb.next.exception.BackendErrorException
import de.ddb.next.exception.ConflictException
import de.ddb.next.exception.ItemNotFoundException

class UserController {
    private final static String SESSION_CONSUMER_MANAGER = "SESSION_CONSUMER_MANAGER_ATTRIBUTE"
    private final static String SESSION_OPENID_PROVIDER = "SESSION_OPENID_PROVIDER_ATTRIBUTE"
    private final static String SESSION_FAVORITES_RESULTS = "SESSION_FAVORITES_RESULTS_ATTRIBUTE"

    def aasService
    def sessionService
    def configurationService
    def messageSource
    def searchService
    def newsletterService
    def favoritesPageService
    def savedSearchesService

    LinkGenerator grailsLinkGenerator

    def index() {

        render(view: "login", model: [
            'loginStatus': LoginStatus.LOGGED_OUT]
        )
    }

    def doLogin() {
        log.info "doLogin(): login user "
        def loginStatus = LoginStatus.LOGGED_OUT

        // Only perform login, if user is not already logged in
        User user = null
        if(!isUserLoggedIn()){
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
            if (user.getStatus().equals(UserStatus.PW_RESET_REQUESTED.toString())) {
                List<String> messages = []
                messages.add("ddbnext.User.PasswordReset_Change")
                redirect(controller: "user", action: "passwordChangePage", params:[messages: messages])
            }
            else {
                redirect(controller: 'user', action: 'favorites')
            }
        }else{
            render(view: "login", model: ['loginStatus': loginStatus])
        }
    }

    def doLogout() {
        log.info "doLogout(): logout user "

        logoutUserFromSession()

        redirect(controller: 'index', action: 'index')

    }

    //TODO Refactor in a new service most of the assisting code
    def favorites(){
        if(isUserLoggedIn()){
            def rows=20 //default
            if (params.rows){
                rows = params.rows.toInteger()
            }
            def String result = favoritesPageService.getFavorites()

            List items = JSON.parse(result) as List
            def totalResults= items.length()

            def dateTime = new Date()
            dateTime = g.formatDate(date: dateTime, format: 'dd.MM.yyyy')
            def userName = session.getAttribute(User.SESSION_USER).getFirstnameAndLastnameOrNickname()
            def lastPgOffset=0
            if (totalResults <1){
                render(view: "favorites", model: [
                    resultsNumber: totalResults,
                    userName: userName,
                    dateString: dateTime,
                    createAllFavoritesLink:favoritesPageService.createAllFavoritesLink(0,0,"desc",0),
                ])
                return
            }else{
                def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
                def allRes = favoritesPageService.retriveItemMD(items,locale)
                def resultsItems

                def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
                urlQuery["offset"]=0
                //Calculating results pagination (previous page, next page, first page, and last page)
                def page = ((params.offset.toInteger()/urlQuery["rows"].toInteger())+1).toString()
                def totalPages = (Math.ceil(items.size()/urlQuery["rows"].toInteger()).toInteger())
                def totalPagesFormatted = String.format(locale, "%,d", totalPages.toInteger())
                lastPgOffset=((Math.ceil(items.size()/rows)*rows)-rows).toInteger()

                if (totalPages.toFloat()<page.toFloat()){
                    params.offset= (Math.ceil((items.size()-rows)/10)*10).toInteger()
                    if ((Math.ceil((items.size()-rows)/10)*10).toInteger()<0){
                        lastPgOffset=20
                    }
                    page=totalPages
                }
                def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
                def numberOfResultsFormatted = String.format(locale, "%,d", allRes.size().toInteger())

                def allResultsWithDate = favoritesPageService.addDateToFavResults(allRes, items, locale)
                //Default ordering is newest on top == DESC
                allResultsWithDate.sort{a,b-> b.serverDate<=>a.serverDate}
                sessionService.setSessionAttributeIfAvailable(SESSION_FAVORITES_RESULTS, allResultsWithDate)
                def allResultsOrdered = allResultsWithDate //Used in the send-favorites listing

                def urlsForOrder=[desc:"#",asc:g.createLink(controller:'user',action:'favorites',params:[offset:0,rows:rows,order:"asc"])]
                if (params.order=="asc"){
                    allResultsWithDate.sort{a,b-> a.serverDate<=>b.serverDate}
                    urlsForOrder["desc"]=g.createLink(controller:'user',action:'favorites',params:[offset:0,rows:rows,order:"desc"])
                    urlsForOrder["asc"]="#"
                }else{
                    params.order="desc"
                }

                if (params.offset){
                    resultsItems=allResultsWithDate.drop(params.offset.toInteger())
                    resultsItems=resultsItems.take( rows)
                }else{
                    params.offset=0
                    resultsItems=allResultsWithDate.take( rows)
                }

                if (request.method=="POST"){
                    def List emails = []
                    if (params.email.contains(',')){
                        emails=params.email.tokenize(',')
                    }else{
                        emails.add(params.email)
                    }
                    try {
                        sendMail {
                            to emails.toArray()
                            from configurationService.getFavoritesSendMailFrom()
                            replyTo getUserFromSession().getEmail()
                            subject g.message(code:"ddbnext.send_favorites_subject_mail")+ getUserFromSession().getFirstnameAndLastnameOrNickname()
                            body( view:"_favoritesEmailBody",
                            model:[results: allResultsOrdered,dateString: dateTime,userName:getUserFromSession().getFirstnameAndLastnameOrNickname()])
                        }
                        flash.message = "ddbnext.favorites_email_was_sent_succ"
                    } catch (e) {
                        log.info "An error occurred sending the email "+ e.getMessage()
                        flash.email_error = "ddbnext.favorites_email_was_not_sent_succ"
                    }
                }

                render(view: "favorites", model: [
                    title: urlQuery["query"],
                    results: resultsItems,
                    allResultsOrdered:allResultsOrdered,
                    allFolders:favoritesPageService.getAllFoldersPerUser(),
                    isThumbnailFiltered: params.isThumbnailFiltered,
                    clearFilters: searchService.buildClearFilter(urlQuery, request.forwardURI),
                    viewType:  urlQuery["viewType"],
                    resultsPaginatorOptions: resultsPaginatorOptions,
                    page: page,
                    resultsNumber: totalResults,
                    createAllFavoritesLink:favoritesPageService.createAllFavoritesLink(params.offset,params.rows,params.order,lastPgOffset),
                    totalPages: totalPages,
                    numberOfResultsFormatted: numberOfResultsFormatted,
                    offset: params["offset"],
                    rows: rows,
                    userName: userName,
                    dateString: dateTime,
                    urlsForOrder:urlsForOrder
                ])
            }
        }
        else{
            redirect(controller:"user", action:"index")
        }
    }

    def sendfavorites(){
        def results = sessionService.getSessionAttributeIfAvailable(SESSION_FAVORITES_RESULTS)
        def dateTime = new Date()
        dateTime = g.formatDate(date: dateTime, format: 'dd MM yyyy')
        render(view: "sendfavorites", model: [results: results, userName:getUserFromSession().getFirstnameAndLastnameOrNickname(),dateString:dateTime])
    }

    /* end favorites methods */

    def deleteSavedSearches() {
        println "XXX deleteSavedSearches"
    }

    def getSavedSearches() {
        if (isUserLoggedIn()) {
            def savedSearches = savedSearchesService.getSavedSearches()
            def offset = params.offset ? params.offset.toInteger() : 0
            def rows = params.rows ? params.rows.toInteger() : 20
            def totalPages = (savedSearches.size() / rows).toInteger()

            if (totalPages * rows < savedSearches.size()) {
                totalPages++
            }
            if (!params.order) {
                params.order = "desc"
            }
            render(view: "savedsearches", model: [
                paginationUrls: savedSearchesService.getPaginationUrls(offset, rows, params.order, totalPages),
                numberOfResults: savedSearches.size(),
                page: offset / rows + 1,
                rows: rows,
                totalPages: totalPages,
                results: savedSearchesService.pageSavedSearches(savedSearches, offset, rows),
                userName: session.getAttribute(User.SESSION_USER).getFirstnameAndLastnameOrNickname()
            ])
        }
        else {
            redirect(controller: "user", action: "index")
        }
    }

    def sendSavedSearches() {
        if (isUserLoggedIn()) {
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
                    replyTo getUserFromSession().getEmail()
                    subject g.message(code: "ddbnext.Savedsearches_Of", args: [
                        getUserFromSession().getFirstnameAndLastnameOrNickname()
                    ])
                    body(view: "_savedSearchesEmailBody", model: [
                        results: savedSearchesService.getSavedSearches(),
                        userName: getUserFromSession().getFirstnameAndLastnameOrNickname()
                    ])
                }
                flash.message = "ddbnext.favorites_email_was_sent_succ"
            } catch (e) {
                log.info "An error occurred sending the email "+ e.getMessage()
                flash.email_error = "ddbnext.favorites_email_was_not_sent_succ"
            }
            redirect(controller: "user", action: "getSavedSearches")
        } else {
            redirect(controller: "user", action: "index")
        }
    }

    def registration() {
        render(view: "registration", model: [])
    }

    def signup() {
        List<String> errors = []
        List<String> messages = []
        errors = Validations.validatorRegistration(params.username, params.fname, params.lname, params.email, params.passwd, params.conpasswd)
        if (errors == null || errors.isEmpty()) {
            def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
            def template = messageSource.getMessage("ddbnext.User.Create_Account_Mailtext", null, locale)
            JSONObject userjson = aasService.getPersonJson(params.username, null, null, params.lname, params.fname, null, null, params.email, params.passwd, configurationService.getCreateConfirmationLink(), template, null)
            try {
                aasService.createPerson(userjson)
                messages.add("ddbnext.User.Create_Success")
                redirect(controller: "user",action: "confirmationPage" , params: [errors: errors, messages: messages])
            }
            catch (ConflictException e) {
                log.error "Conflict: user with given data already exists. username:" + params.username + ",email:" + params.email, e
                String conflictField = e.getMessage().replaceFirst(".*?'(.*?)'.*", "\$1")
                if (params.username.equals(conflictField)) {
                    errors.add("ddbnext.Conflict_User_Name")
                }
                else if (params.email.equals(conflictField)) {
                    errors.add("ddbnext.Conflict_User_Email")
                }
                else {
                    errors.add("ddbnext.Conflict_User_Common")
                }
                render(view: "registration" , model: [errors: errors, messages: messages, params: params])
            }
        }
        else {
            render(view: "registration" , model: [errors: errors, messages: messages, params: params])
        }
    }

    def passwordResetPage() {
        List<String> errors = []
        List<String> messages = []
        if (params.errors != null) {
            if (params.errors instanceof String) {
                errors.add(params.errors)
            }
            else {
                errors.addAll(params.errors)
            }
        }
        if (params.messages != null) {
            if (params.messages instanceof String) {
                messages.add(params.messages)
            }
            else {
                messages.addAll(params.messages)
            }
        }
        render(view: "resetpassword", model: [errors: errors, messages: messages])
    }

    def passwordReset() {
        List<String> messages = []
        List<String> errors = []
        if (StringUtils.isBlank(params.username)) {
            errors.add("ddbnext.Error_Username_Empty")
        }
        if (errors == null || errors.isEmpty()) {
            try {
                def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
                def template = messageSource.getMessage("ddbnext.User.PasswordReset_Mailtext", null, locale)
                aasService.resetPassword(params.username, aasService.getResetPasswordJson(configurationService.getPasswordResetConfirmationLink(), template, null))
                messages.add("ddbnext.User.PasswordReset_Success")
            }
            catch (ItemNotFoundException e) {
                log.error "NotFound: a user with given name " + params.username + " was not found", e
                errors.add("ddbnext.Error_Username_Notfound")
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

    def profile() {
        if(isUserLoggedIn()){
            User user = getUserFromSession().clone()
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
                }
                else {
                    errors.addAll(params.errors)
                }
            }
            if (params.messages != null) {
                if (params.messages instanceof String) {
                    messages.add(params.messages)
                }
                else {
                    messages.addAll(params.messages)
                }
            }
            //get favorites-count
            def String result = favoritesPageService.getFavorites()
            List items = JSON.parse(result) as List
            def favoritesCount = items.length()

            render(view: "profile", model: [favoritesCount: favoritesCount, user: user, errors:errors, messages: messages])
        }
        else{
            redirect(controller:"user", action:"index")
        }
    }

    def saveProfile() {
        if (isUserLoggedIn()) {
            List<String> errors = []
            List<String> messages = []
            boolean eMailDifference = false
            boolean profileDifference = false
            boolean newsletterDifference = false
            User user = getUserFromSession().clone()

            if (!user.isConsistent()) {
                throw new BackendErrorException("user-attributes are not consistent")
            }
            if (!user.getOpenIdUser()) {
                if (StringUtils.isBlank(params.username) || params.username.length() < 2) {
                    errors.add("ddbnext.Error_Username_Empty")
                }
                if (StringUtils.isBlank(params.email)) {
                    errors.add("ddbnext.Error_Email_Empty")
                }
                if (!Validations.validatorEmail(params.email)) {
                    errors.add("ddbnext.Error_Valid_Email_Address")
                }
            }
            if (errors == null || errors.isEmpty()) {
                if (!user.getOpenIdUser()) {
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
                    errors.add("ddbnext.User.Profile_NoValuesChanged")
                }

                if (profileDifference) {
                    //update user in aas
                    JSONObject aasUser = aasService.getPerson(user.getId())
                    aasUser.put(AasService.NICKNAME_FIELD, params.username)
                    aasUser.put(AasService.FIRSTNAME_FIELD, params.fname)
                    aasUser.put(AasService.LASTNAME_FIELD, params.lname)
                    try {
                        user.setUsername(params.username)
                        user.setFirstname(params.fname)
                        user.setLastname(params.lname)
                        aasService.updatePerson(user.getId(), aasUser)
                        messages.add("ddbnext.User.Profile_Update_Success")
                    }
                    catch (ConflictException e) {
                        log.error "Conflict: user with given data already exists. username:" + params.username, e
                        errors.add("ddbnext.Conflict_User_Name")
                    }
                }
                if (eMailDifference && (errors == null || errors.isEmpty())) {
                    try {
                        //update email in aas
                        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
                        def template = messageSource.getMessage("ddbnext.User.Email_Update_Mailtext", null, locale)
                        aasService.updateEmail(user.getId(), aasService.getUpdateEmailJson(params.email, configurationService.getEmailUpdateConfirmationLink(), template, null))
                        messages.add("ddbnext.User.Email_Update_Success")
                    }
                    catch (ConflictException e) {
                        user.setEmail(params.email)
                        log.error "Conflict: user with given data already exists. email:" + params.email, e
                        errors.add("ddbnext.Conflict_User_Email")
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
        else{
            redirect(controller:"index")
        }
    }

    private updateNewsletterSubscription(user, messages, errors) {
        try {
            if (params.newsletter) {
                newsletterService.addSubscriber(user)
                user.setNewsletterSubscribed(true)
            }
            else {
                newsletterService.removeSubscriber(user)
                user.setNewsletterSubscribed(false)
            }

            messages.add("ddbnext.User.Newsletter_Update_Success")
        }
        catch (Exception e) {
            log.error "fail to update newsletter subscription", e
            errors.add("fail to update newsletter subscription")
        }
    }

    def passwordChangePage() {
        if(isUserLoggedIn()){
            User user = getUserFromSession()
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
                }
                else {
                    errors.addAll(params.errors)
                }
            }
            if (params.messages != null) {
                if (params.messages instanceof String) {
                    messages.add(params.messages)
                }
                else {
                    messages.addAll(params.messages)
                }
            }
            render(view: "changepassword", model: [user: user, errors: errors, messages: messages])
        }
        else{
            redirect(controller:"index")
        }
    }

    def passwordChange() {
        if (isUserLoggedIn()) {
            List<String> errors = []
            List<String> messages = []
            User user = getUserFromSession().clone()
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
                messages.add("ddbnext.User.Password_Change_Success")
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

            def String result = favoritesPageService.getFavorites()
            List items = JSON.parse(result) as List
            def favoritesCount = items.length()

            //render(view: "changepassword", model: [user: user, errors: errors, messages: messages])
            render(view: "profile", model: [favoritesCount: favoritesCount, user: user, errors: errors, messages: messages])
        }
        else{
            redirect(controller:"index")
        }
    }

    def delete() {
        if (isUserLoggedIn()) {
            List<String> errors = []
            List<String> messages = []
            User user = getUserFromSession().clone()
            if (!user.isConsistent()) {
                throw new BackendErrorException("user-attributes are not consistent")
            }
            if (user.isOpenIdUser()) {
                //password-change is only for aas-users
                redirect(controller:"index")
            }
            try {
                aasService.deletePerson(user.id)
            }
            catch (AuthorizationException e) {
                forward controller: "error", action: "auth"
            }
            logoutUserFromSession()

            messages.add("ddbnext.User.Delete_Confirm")
            redirect(controller: "user",action: "confirmationPage" , params: [errors: errors, messages: messages])
        }
    }

    def confirmationPage() {
        List<String> errors = []
        List<String> messages = []
        if (params.errors != null) {
            if (params.errors instanceof String) {
                errors.add(params.errors)
            }
            else {
                errors.addAll(params.errors)
            }
        }
        if (params.messages != null) {
            if (params.messages instanceof String) {
                messages.add(params.messages)
            }
            else {
                messages.addAll(params.messages)
            }
        }
        render(view: "confirm", model: [errors: errors, messages: messages])
    }

    def confirm() {
        if (StringUtils.isBlank(params.type)) {
            forward controller: "error", action: "serverError"
        }
        List<String> messages = []
        List<String> errors = []
        def jsonuser
        try {
            jsonuser = aasService.confirm(params.id, params.token)
            if (params.type.equals("emailupdate")) {
                messages.add("ddbnext.User.Email_Confirm_Success")
            }
            else if (params.type.equals("passwordreset")) {
                messages.add("ddbnext.User.Pwreset_Confirm_Success")
            }
            else if (params.type.equals("create")) {
                messages.add("ddbnext.User.Create_Confirm_Success")
            }
            // set changed attributes in user-object in session
            if (isUserLoggedIn()) {
                User user = getUserFromSession().clone()
                if (!user.isConsistent() || StringUtils.isBlank(jsonuser.getString(AasService.EMAIL_FIELD))) {
                    throw new BackendErrorException("user-attributes are not consistent")
                }
                user.setEmail(jsonuser.getString(AasService.EMAIL_FIELD))
                if (jsonuser.containsKey(AasService.PASSWORD_FIELD)) {
                    user.setPassword(jsonuser.getString(AasService.PASSWORD_FIELD))
                }
                sessionService.setSessionAttributeIfAvailable(User.SESSION_USER, user)
            }
        }
        catch (ItemNotFoundException e) {
            log.error "NotFound: confirmation does not exist. uid:" + params.id + ", token:" + params.token, e
            errors.add("ddbnext.Error.Confirmation_Not_Found")
        }
        redirect(controller: "user",action: "confirmationPage" , params: [errors: errors, messages: messages])
    }

    def requestOpenIdLogin() {
        def provider = params.provider

        String discoveryUrl = ""

        setProxy()

        FetchRequest fetch = FetchRequest.createFetchRequest()

        if(provider == SupportedOpenIdProviders.GOOGLE.toString()){
            discoveryUrl = "https://www.google.com/accounts/o8/id"
            fetch.addAttribute("Email", "http://schema.openid.net/contact/email", true)
            fetch.addAttribute("FirstName", "http://schema.openid.net/namePerson/first", true)
            fetch.addAttribute("LastName", "http://schema.openid.net/namePerson/last", true)
            fetch.setCount("openid.ext1.value.Email", 1)
        }else if(provider == SupportedOpenIdProviders.YAHOO.toString()){
            discoveryUrl = "https://me.yahoo.com"
            fetch.addAttribute("Email", "http://axschema.org/contact/email", true)
            fetch.addAttribute("Fullname", "http://axschema.org/namePerson", true)
        }else{
            render(view: "login", model: [
                'loginStatus': LoginStatus.AUTH_PROVIDER_UNKNOWN]
            )
            return
        }


        log.info "requestOpenIdLogin(): discoveryUrl="+discoveryUrl
        ConsumerManager manager = new ConsumerManager()

        sessionService.createNewSession()
        sessionService.setSessionAttributeIfAvailable(SESSION_OPENID_PROVIDER, provider)
        sessionService.setSessionAttributeIfAvailable(SESSION_CONSUMER_MANAGER, manager)

        String returnURL = grailsLinkGenerator.serverBaseURL + "/login/doOpenIdLogin"
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

        //ConsumerManager manager = getSessionObject(false)?.getAttribute(SESSION_CONSUMER_MANAGER)
        ConsumerManager manager = sessionService.getSessionAttributeIfAvailable(SESSION_CONSUMER_MANAGER)
        if(manager){
            //def provider = getSessionObject(false)?.getAttribute(SESSION_OPENID_PROVIDER)
            def provider = sessionService.getSessionAttributeIfAvailable(SESSION_OPENID_PROVIDER)

            ParameterList openidResp = new ParameterList(request.getParameterMap())
            //DiscoveryInformation discovered = (DiscoveryInformation) getSessionObject(false)?.getAttribute("discovered");
            DiscoveryInformation discovered = (DiscoveryInformation) sessionService.getSessionAttributeIfAvailable("discovered")
            String returnURL = grailsLinkGenerator.serverBaseURL + "/login/doOpenIdLogin"
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

                if(provider == SupportedOpenIdProviders.GOOGLE.toString()){
                    firstName = params["openid.ext1.value.FirstName"]
                    lastName = params["openid.ext1.value.LastName"]
                    username = firstName + " " + lastName
                    email = params["openid.ext1.value.Email"]
                    identifier = verified.getIdentifier()
                }else if(provider == SupportedOpenIdProviders.YAHOO.toString()){
                    username = params["openid.ax.value.fullname"]
                    email = params["openid.ax.value.email"]
                    identifier = verified.getIdentifier()
                }else{
                    render(view: "login", model: [
                        'loginStatus': LoginStatus.AUTH_PROVIDER_UNKNOWN]
                    )
                    return
                }


                log.info "doOpenIdLogin(): credentials:  " + username + " / " + email + " / " + identifier // TODO remove again!!!

                // Create new session, because the old one might be corrupt due to the redirect to the OpenID provider
                //                getSessionObject(false)?.invalidate()
                //                HttpSession newSession = getSessionObject(true)
                sessionService.destroySession()
                HttpSession newSession = sessionService.createNewSession()

                User user = new User()
                user.setId(identifier.encodeAsMD5())
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

            }else {
                log.info "doOpenIdLogin(): failure verification"
                loginStatus = LoginStatus.AUTH_PROVIDER_DENIED
            }
        }

        if(loginStatus == LoginStatus.SUCCESS){
            redirect(controller: 'user', action: 'favorites')
        }else{
            render(view: "login", model: ['loginStatus': loginStatus])
        }

    }

    private def setProxy(){

        def proxyHost = System.getProperty("http.proxyHost")
        def proxyPortString = System.getProperty("http.proxyPort")
        int proxyPort = 80

        if (proxyHost) {
            if (proxyPortString && !proxyPortString.isEmpty()) {
                try{
                    proxyPort = Integer.parseInt(proxyPortString)
                }catch(NumberFormatException e){
                    log.error "setProxy(): The proxyport of the system properties cannot be cast to an int: '"+proxyPortString"'"
                }
            }

            ProxyProperties proxyProps = new ProxyProperties()
            proxyProps.setProxyHostName(proxyHost)
            proxyProps.setProxyPort(proxyPort)
            HttpClientFactory.setProxyProperties(proxyProps)
        }

    }


    private boolean isUserLoggedIn() {
        return sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
    }

    private User getUserFromSession() {
        return sessionService.getSessionAttributeIfAvailable(User.SESSION_USER)
    }

    private boolean logoutUserFromSession() {
        sessionService.removeSessionAttributeIfAvailable(User.SESSION_USER)
        sessionService.destroySession()
    }


}
