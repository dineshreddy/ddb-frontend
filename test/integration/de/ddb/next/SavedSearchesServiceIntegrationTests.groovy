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

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin

import org.junit.*

import de.ddb.common.constants.Type

@TestMixin(ControllerUnitTestMixin)
class SavedSearchesServiceIntegrationTests {

    def savedSearchesService

    /** The userId is refreshed in setUp() for every test method*/
    def userId = null

    /**
     * Is called before every test method.
     * Creates a new userId for the test.
     */
    void setUp() {
        println "--------------------------------------------------------------------"
        println "Setup tests"
        userId = UUID.randomUUID() as String
        logStats()
    }

    /**
     * Is called after every test method.
     * Cleanup user content created by a test method
     */
    void tearDown() {
        println "Cleanup tests"

        def results = savedSearchesService.findSavedSearchesByUserId(userId)
        println "Saved user searches after test: " + results.size()

        savedSearchesService.deleteSavedSearchesByUserId(userId)

        logStats()
        println "--------------------------------------------------------------------"
    }


    def logStats() {
        println "userId " + userId
        println "Index has " + savedSearchesService.getSavedSearchesCount(userId) + " searches"
    }

    @Test
    void shouldSavedUserSearch() {
        log.info "should saved user search"

        def queryString = 'query=Goethe&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'

        def savedSearchId = savedSearchesService.addSavedSearch(userId, queryString, Type.CULTURAL_ITEM)
        log.info "id: ${savedSearchId}"
        assert savedSearchId  != null
    }

    @Test
    void shouldSavedUserSearchWithTitleAndDescription() {
        log.info "should saved user search"

        def queryString = 'query=Goethe&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'

        def savedSearchId = savedSearchesService.addSavedSearch(userId, queryString, 'Goethe Related',
                'All things related to Goethe', Type.CULTURAL_ITEM)
        log.info "id: ${savedSearchId}"
        assert savedSearchId  != null
    }

    @Test
    void shouldFindAllSavedSearchesByUserId() {
        log.info "should find all saved searches by user ID"

        def queryStringForGoethe = 'query=Goethe&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def goetheSavedSearchId = savedSearchesService.addSavedSearch(userId, queryStringForGoethe , 'Goethe Related',
                'All things related to Goethe', Type.CULTURAL_ITEM)
        assert goetheSavedSearchId != null

        def queryStringForMozart = 'query=mozart&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def mozartSavedSearchId = savedSearchesService.addSavedSearch(userId, queryStringForMozart , 'Mozart Related',
                Type.CULTURAL_ITEM)
        assert mozartSavedSearchId != null

        def results = savedSearchesService.findSavedSearchesByUserId(userId)
        assert results.size() == 2
    }

    @Test
    void shouldDeleteSavedSearches() {
        log.info "should delete saved search by IDs"

        def queryStringForGoethe = 'query=Goethe&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def goetheSavedSearchId = savedSearchesService.addSavedSearch(userId, queryStringForGoethe ,
                'Goethe Related', 'All things related to Goethe', Type.CULTURAL_ITEM)
        assert goetheSavedSearchId != null

        def queryStringForMozart = 'query=mozart&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def mozartSavedSearchId = savedSearchesService.addSavedSearch(userId, queryStringForMozart , 'Mozart Related',
                Type.CULTURAL_ITEM)
        assert mozartSavedSearchId != null


        def results = savedSearchesService.findSavedSearchesByUserId(userId)
        assert results.size() == 2

        savedSearchesService.deleteSavedSearches(
                userId,
                [
                    goetheSavedSearchId,
                    mozartSavedSearchId
                ])

        assert savedSearchesService.findSavedSearchesByUserId(userId).size() == 0
    }

    @Test
    void shouldDeleteSavedSearchesByUserId() {
        def queryStringForGoethe = 'query=Goethe&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def goetheSavedSearchId = savedSearchesService.addSavedSearch(userId, queryStringForGoethe , 'Goethe Related',
                'All things related to Goethe', Type.CULTURAL_ITEM)
        assert goetheSavedSearchId != null

        def queryStringForMozart = 'query=mozart&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def mozartSavedSearchId = savedSearchesService.addSavedSearch(userId, queryStringForMozart , 'Mozart Related',
                Type.CULTURAL_ITEM)
        assert mozartSavedSearchId != null


        def results = savedSearchesService.findSavedSearchesByUserId(userId)
        assert results.size() == 2

        savedSearchesService.deleteSavedSearchesByUserId(userId)

        assert savedSearchesService.findSavedSearchesByUserId(userId).size() == 0
    }

    // TODO: shouldUpdateSavedSearch
}
