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
    String getCmsUrl() {
        return getConfigValue("ddb.cms.url")
    }

    List getFacetsFilter() {
        return getConfigValue("ddb.backend.facets.filter", List)
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

    boolean isEntitySearchFeaturesEnabled() {
        return getBooleanConfigValue("ddb.search.entities.features.enabled")
    }

    boolean isExhibitionsFeaturesEnabled() {
        return getBooleanConfigValue("ddb.exhibitions.features.enabled")
    }

    boolean isInstitutionSearchFeaturesEnabled() {
        return getBooleanConfigValue("ddb.search.institutions.features.enabled")
    }

    boolean isOnlyWithThumbnailsFeaturesEnabled() {
        return getBooleanConfigValue("ddb.search.OnlyWithThumbnails.features.enabled")
    }

    boolean isRightsFacetFeaturesEnabled() {
        return getBooleanConfigValue("ddb.rights.facet.features.enabled")
    }

    boolean isSearchSuggestionFeaturesEnabled() {
        return getBooleanConfigValue("ddb.search.suggestions.features.enabled")
    }

    def logConfigurationSettings() {
        //Call Common Configuration
        super.logConfigurationSettings()

        log.info "------------- ddb-next.properties ---------------------"
        log.info "ddb.cms.url = " + getCmsUrl()
        log.info "ddb.backend.facets.filter = " + getFacetsFilter()
        log.info "grails.mail.host = " + getGrailsMailHost()
        log.info "grails.mail.port = " + getGrailsMailPort()
        log.info "ddb.loadbalancer.header.name = " + getLoadbalancerHeaderName()
        log.info "ddb.loadbalancer.header.value = " + getLoadbalancerHeaderValue()
        log.info "ddb.entities.features.enabled = " + isEntitySearchFeaturesEnabled()
        log.info "ddb.exhibitions.features.enabled = " + isExhibitionsFeaturesEnabled()
        log.info "ddb.footerMenu = " + getFooterMenu()
        log.info "ddb.mainMenu = " + getMainMenu()
        log.info "ddb.rights.facet.features.enabled = " + isRightsFacetFeaturesEnabled()
        log.info "ddb.search.institutions.features.enabled = " + isInstitutionSearchFeaturesEnabled()
        log.info "ddb.search.OnlyWithThumbnails.features.enabled = " + isOnlyWithThumbnailsFeaturesEnabled()
        log.info "ddb.search.suggestions.features.enabled = " + isSearchSuggestionFeaturesEnabled()
        log.info "-------------------------------------------------------"
    }
}
