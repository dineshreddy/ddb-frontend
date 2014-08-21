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

import org.codehaus.groovy.grails.web.mapping.LinkGenerator

/**
 * Service for accessing the configuration.
 *
 * @author hla
 */
class ConfigurationService {

    def grailsApplication
    def LinkGenerator grailsLinkGenerator
    def commonConfigurationService

    def transactional=false

    public String getBinaryUrl() {
        return commonConfigurationService.getConfigValue("ddb.binary.url")
    }

    String getDefaultStaticPage(){
        return commonConfigurationService.getConfigValue("ddb.default.staticPage")
    }

    public String getStaticUrl(){
        return commonConfigurationService.getConfigValue("ddb.static.url")
    }

    public String getApisUrl(){
        return commonConfigurationService.getConfigValue("ddb.apis.url")
    }

    public String getBackendUrl(){
        return commonConfigurationService.getConfigValue("ddb.backend.url")
    }

    public String getCulturegraphUrl(){
        return commonConfigurationService.getConfigValue("ddb.culturegraph.url")
    }


    public String getFavoritesSendMailFrom(){
        return commonConfigurationService.getConfigValue("ddb.favorites.sendmailfrom")
    }

    public String getFavoritesReportMailTo(){
        return commonConfigurationService.getConfigValue("ddb.favorites.reportMailTo")
    }

    public List getFacetsFilter(){
        return commonConfigurationService.getConfigValue("ddb.backend.facets.filter", List)
    }

    public String getPiwikTrackingFile(){
        return commonConfigurationService.getExistingConfigValue("ddb.tracking.piwikfile")
    }

    public String getApiKeyDocUrl(){
        return commonConfigurationService.getConfigValue("ddb.apikey.doc.url")
    }

    public String getApiKeyTermsUrl(){
        return commonConfigurationService.getConfigValue("ddb.apikey.terms.url")
    }

    public String getEncoding(){
        return commonConfigurationService.getConfigValue("grails.views.gsp.encoding")
    }

    public String getMimeTypeHtml(){
        return commonConfigurationService.getConfigValue("grails.mime.types['html'][0]", String, grailsApplication.config.grails?.mime?.types["html"][0])
    }

    public String getLoggingFolder(){
        return commonConfigurationService.getConfigValue("ddb.logging.folder")
    }

    public String getLoadbalancerHeaderName(){
        return commonConfigurationService.getConfigValue("ddb.loadbalancer.header.name")
    }

    public String getLoadbalancerHeaderValue(){
        return commonConfigurationService.getConfigValue("ddb.loadbalancer.header.value")
    }

    public String getGrailsMailHost(){
        return commonConfigurationService.getConfigValue("grails.mail.host")
    }

    public String getProxyHost(){
        return commonConfigurationService.getSystemProperty("http.proxyHost")
    }

    public String getProxyPort(){
        return commonConfigurationService.getSystemProperty("http.proxyPort")
    }

    public String getNonProxyHosts(){
        return commonConfigurationService.getSystemProperty("http.nonProxyHosts")
    }


    /**
     * Get the authorization key to access restricted API calls.
     *
     * This property is optional. Leave it blank if you do not want to set an API key.
     *
     * @return the authorization key
     */
    public String getBackendApikey(){
        return commonConfigurationService.getProperlyTypedConfigValue("ddb.backend.apikey")
    }

    public int getSearchGroupCount() {
        return commonConfigurationService.getIntegerConfigValue("ddb.advancedSearch.searchGroupCount")
    }

    public int getSearchFieldCount() {
        return commonConfigurationService.getIntegerConfigValue("ddb.advancedSearch.searchFieldCount")
    }

    public int getSearchOffset() {
        return commonConfigurationService.getIntegerConfigValue("ddb.advancedSearch.defaultOffset")
    }

    public int getSearchRows() {
        return commonConfigurationService.getIntegerConfigValue("ddb.advancedSearch.defaultRows")
    }

    public int getGrailsMailPort() {
        return commonConfigurationService.getIntegerConfigValue("grails.mail.port")
    }

    public boolean isCulturegraphFeaturesEnabled() {
        //FIXME dev.escidoc.org and dev.escidoc.org/current shares the same ddb-next.property file
        //      Because we will show the entity features on dev.escidoc.org/current and not on dev.escidoc.org
        //      we will return always true in this method of the develop branch.
        //      Later versions of develop might reactivate the code.

        //        def value = commonConfigurationService.getExistingConfigValue("ddb.culturegraph.features.enabled")
        //        return Boolean.parseBoolean(value.toString())

        return true
    }

    public boolean isExhibitionsFeaturesEnabled() {
        def value = commonConfigurationService.getExistingConfigValue("ddb.exhibitions.features.enabled")
        return Boolean.parseBoolean(value.toString())
    }

    public def logConfigurationSettings() {
        log.info "------------- System.properties -----------------------"
        log.info "proxyHost = " + getProxyHost()
        log.info "proxyPort = " + getProxyPort()
        log.info "nonProxyHosts = " + getNonProxyHosts()
        log.info "------------- application.properties ------------------"
        log.info "app.grails.version = "+grailsApplication.metadata["app.grails.version"]
        log.info "app.name = "+grailsApplication.metadata["app.name"]
        log.info "app.version = "+grailsApplication.metadata["app.version"]
        log.info "build.number = "+grailsApplication.metadata["build.number"]
        log.info "build.id = "+grailsApplication.metadata["build.id"]
        log.info "build.url = "+grailsApplication.metadata["build.url"]
        log.info "build.git.commit = "+grailsApplication.metadata["build.git.commit"]
        log.info "build.bit.branch = "+grailsApplication.metadata["build.bit.branch"]

        //Call Common Configuration
        commonConfigurationService.logConfigurationSettings()

        log.info "------------- ddb-next.properties ---------------------"
        log.info "ddb.binary.url = " + getBinaryUrl()
        log.info "ddb.static.url = " + getStaticUrl()
        log.info "ddb.backend.apikey = " + getBackendApikey()
        log.info "ddb.culturegraph.url = " + getCulturegraphUrl()
        log.info "ddb.favorites.sendmailfrom = " + getFavoritesSendMailFrom()
        log.info "ddb.favorites.reportMailTo = " + getFavoritesReportMailTo()
        log.info "ddb.backend.facets.filter = " + getFacetsFilter()
        log.info "ddb.tracking.piwikfile = " + getPiwikTrackingFile()
        log.info "grails.views.gsp.encoding = " + getEncoding()
        log.info "grails.mime.types['html'][0] = " + getMimeTypeHtml()
        log.info "ddb.advancedSearch.searchGroupCount = " + getSearchGroupCount()
        log.info "ddb.advancedSearch.searchFieldCount = " + getSearchFieldCount()
        log.info "ddb.advancedSearch.defaultOffset = " + getSearchOffset()
        log.info "ddb.advancedSearch.defaultRows = " + getSearchRows()
        log.info "ddb.session.timeout = " + commonConfigurationService.getSessionTimeout()
        log.info "ddb.logging.folder = " + getLoggingFolder()
        log.info "ddb.loadbalancer.header.name = " + getLoadbalancerHeaderName()
        log.info "ddb.loadbalancer.header.value = " + getLoadbalancerHeaderValue()
        log.info "ddb.culturegraph.features.enabled = " + isCulturegraphFeaturesEnabled()
        log.info "ddb.exhibitions.features.enabled = " + isExhibitionsFeaturesEnabled()
        log.info "ddb.apikey.doc.url = " + getApiKeyDocUrl()
        log.info "ddb.apikey.terms.url = " + getApiKeyTermsUrl()
        log.info "ddb.registration.info.url = " + commonConfigurationService.getRegistrationInfoUrl()
        log.info "ddb.account.terms.url = " + commonConfigurationService.getAccountTermsUrl()
        log.info "ddb.account.privacy.url = " + commonConfigurationService.getAccountPrivacyUrl()
        log.info "grails.mail.host = " + getGrailsMailHost()
        log.info "grails.mail.port = " + getGrailsMailPort()
        log.info "-------------------------------------------------------"
    }
}
