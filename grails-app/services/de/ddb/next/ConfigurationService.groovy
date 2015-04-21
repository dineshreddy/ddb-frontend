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

import de.ddb.common.CommonConfigurationService

/**
 * Service for accessing the configuration.
 *
 * @author hla
 */
class ConfigurationService extends CommonConfigurationService {
    String getApiKeyDocUrl() {
        return getConfigValue("ddb.apikey.doc.url")
    }

    String getApiKeyTermsUrl() {
        return getConfigValue("ddb.apikey.terms.url")
    }

    String getCmsUrl() {
        return getConfigValue("ddb.cms.url")
    }

    String getCulturegraphUrl() {
        return getConfigValue("ddb.culturegraph.url")
    }

    String getDomainCanonic() {
        return getConfigValue("ddb.domain.canonic")
    }

    String getFooterMenu() {
        return getConfigValue("ddb.footerMenu")
    }

    String getGrailsMailHost() {
        return getConfigValue("grails.mail.host")
    }

    int getGrailsMailPort() {
        return getIntegerConfigValue("grails.mail.port")
    }

    String getLoadbalancerHeaderName() {
        return getConfigValue("ddb.loadbalancer.header.name")
    }

    String getLoadbalancerHeaderValue() {
        return getConfigValue("ddb.loadbalancer.header.value")
    }

    String getMainMenu() {
        return getConfigValue("ddb.mainMenu")
    }

    String getSocialIconsFacebookUrl() {
        return getConfigValue("ddb.socialIcons.url.facebook")
    }

    String getSocialIconsTwitterUrl() {
        return getConfigValue("ddb.socialIcons.url.twitter")
    }

    boolean isCulturegraphFeaturesEnabled() {
        //FIXME dev.escidoc.org and dev.escidoc.org/current shares the same ddb-next.property file
        //      Because we will show the entity features on dev.escidoc.org/current and not on dev.escidoc.org
        //      we will return always true in this method of the develop branch.
        //      Later versions of develop might reactivate the code.

        //        def value = getExistingConfigValue("ddb.culturegraph.features.enabled")
        //        return Boolean.parseBoolean(value.toString())

        return true
    }

    boolean isEntitySearchFeaturesEnabled() {
        return getBooleanConfigValue("ddb.search.entities.features.enabled")
    }

    boolean isExhibitionsFeaturesEnabled() {
        return getBooleanConfigValue("ddb.exhibitions.features.enabled")
    }

    boolean isInstitutionSearchFeaturesEnabled() {
        return getBooleanConfigValue("ddb.search.institutions.features.enabled")
    }

    boolean isRightsFacetFeaturesEnabled() {
        return getBooleanConfigValue("ddb.rights.facet.features.enabled")
    }

    def logConfigurationSettings() {
        //Call Common Configuration
        super.logConfigurationSettings()

        log.info "------------- ddb-next.properties ---------------------"
        log.info "ddb.apikey.doc.url = " + getApiKeyDocUrl()
        log.info "ddb.apikey.terms.url = " + getApiKeyTermsUrl()
        log.info "ddb.cms.url = " + getCmsUrl()
        log.info "ddb.culturegraph.url = " + getCulturegraphUrl()
        log.info "ddb.domain.canonic = " + getDomainCanonic()
        log.info "grails.mail.host = " + getGrailsMailHost()
        log.info "grails.mail.port = " + getGrailsMailPort()
        log.info "ddb.loadbalancer.header.name = " + getLoadbalancerHeaderName()
        log.info "ddb.loadbalancer.header.value = " + getLoadbalancerHeaderValue()
        log.info "ddb.culturegraph.features.enabled = " + isCulturegraphFeaturesEnabled()
        log.info "ddb.entities.features.enabled = " + isEntitySearchFeaturesEnabled()
        log.info "ddb.exhibitions.features.enabled = " + isExhibitionsFeaturesEnabled()
        log.info "ddb.footerMenu = " + getFooterMenu()
        log.info "ddb.institutions.features.enabled = " + isInstitutionSearchFeaturesEnabled()
        log.info "ddb.mainMenu = " + getMainMenu()
        log.info "ddb.rights.facet.features.enabled = " + isRightsFacetFeaturesEnabled()
        log.info "ddb.socialIcons.url.facebook = " + getSocialIconsFacebookUrl()
        log.info "ddb.socialIcons.url.twitter = " + getSocialIconsTwitterUrl()

        log.info "-------------------------------------------------------"
    }
}
