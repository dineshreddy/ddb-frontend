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
class UrlMappings {

    static mappings = {
        //@formatter:off
        "/$controller/$action?/$id?"{ constraints { /* apply constraints here */ } }
        //@formatter:on
        
        "/searchresults/$q?" {
            controller="search"
            action="results"
        }

        "/facets/$q?" {
            controller="facets"
            action="facetsList"
        }
        
        "/informationitem/$id"{
            controller="search"
            action="informationItem"
        }

        "/content/$dir/$id?" {
            controller="content"
            action="staticcontent"
        }

        "/advancedsearch" {
            controller="advancedsearch"
            action="fillValues"
        }

        "/advancedsearch/search" {
            controller="advancedsearch"
            action="executeSearch"
        }

        "/" {
            controller="index"
            action="index"
        }

        "/item/$id" {
            controller="item"
            action="findById"
        }

        "/apis/favorites" {
            controller="favorites"
            action="getFavorites"
        }

        name delFavorites: "/apis/favorites/_delete" {
            controller="favorites"
            action=[POST: "deleteFavorites"]
        }

        "/apis/favorites/_get" {
            controller="favorites"
            action=[POST: "filterFavorites"]
        }

        "/apis/favorites/$id" {
            controller="favorites"
            action=[GET: "getFavorite", POST: "addFavorite", DELETE: "deleteFavorite"]
        }

        "/apis/savedsearches" {
            controller="savedsearches"
            action="getSavedSearches"
        }

        "/apis/savedsearches/$id" {
            controller="savedsearches"
            action=[POST: "addSavedSearch"]
        }

        name delSavedSearches: "/apis/savedsearches/_delete" {
            controller="savedsearches"
            action=[POST: "deleteSavedSearches"]
        }

        "/about-us/institutions" {
            controller="institution"
            action="show"
        }

        "/about-us/institutions/item/$id" {
            controller="institution"
            action="showInstitutionsTreeByItemId"
        }

        "/apis/institutions" {
            controller="institution"
            action="getJson"
        }

        "/entity/$id" {
            controller="entity"
            action="index"
        }

        "/entity/ajax/searchresults" {
            controller="entity"
            action="getAjaxSearchResultsAsJson"
        }

        "/binary/$filename**" {
            controller="apis"
            action="binary"
        }

        "/static/$filename**" {
            controller="apis"
            action="staticFiles"
        }

        "/user/registration" {
            controller="user"
            action="registration"
        }
        
        "/user/resetPassword" {
            controller="user"
            action="passwordResetPage"
        }

        "/user/profile" {
            controller="user"
            action="profile"
        }

        "/user/favorites" {
            controller="user"
            action="favorites"
        }

        "/user/savedsearches" {
            controller="user"
            action=[GET: "getSavedSearches", POST: "sendSavedSearches"]
        }

        "/user/confirm/$id/$token" {
            controller="user"
            action="confirm"
        }

        "/user/changePassword" {
            controller="user"
            action="passwordChangePage"
        }
        "/user/delete" {
            controller="user"
            action="delete"
        }

        "/login" {
            controller="user"
            action="index"
        }

        "/login/doLogin" {
            controller="user"
            action="doLogin"
        }

        "/login/doLogout" {
            controller="user"
            action="doLogout"
        }

        "/login/openId" {
            controller="user"
            action="requestOpenIdLogin"
        }

        "/login/doOpenIdLogin" {
            controller="user"
            action="doOpenIdLogin"
        }

        "500"(controller: "error", action: "badRequest", exception: de.ddb.next.exception.BadRequestException)
        "500"(controller: "error", action: "auth", exception: de.ddb.next.exception.AuthorizationException)
        "500"(controller: "error", action: "notFound", exception: de.ddb.next.exception.ItemNotFoundException)
        "500"(controller: "error", action: "conflict", exception: de.ddb.next.exception.ConflictException)
        "500"(controller: "error", action: "serverError", exception: de.ddb.next.exception.ConfigurationException)
        "500"(controller: "error", action: "serverError", exception: de.ddb.next.exception.BackendErrorException)
        "500"(controller: "error", action: "uncaughtException")

    }
}
