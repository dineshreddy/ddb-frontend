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
modules = {
    ddbnext {
        defaultBundle 'ddbnext'
        dependsOn "images, cssscreen, cssprint, javascript, autocomplete"
    }

    images {  resource url:'/images/favicon.ico' }

    cssscreen {
        resource url:'/css/bootstrap.css', bundle: 'screen'
        resource url:'/css/bootstrap-responsive.css', bundle: 'screen'
        resource url:'/css/ddb.css', bundle: 'screen'
        resource url:'/css/item.css', bundle: 'screen'
        resource url:'/css/institutionList.css', bundle: 'screen'
        resource url:'/css/institution.css', bundle: 'screen'
        resource url:'/css/institution-map.css', bundle: 'screen'
        resource url:'/css/results.css', bundle: 'screen'
        resource url:'/css/favorites.css', bundle: 'screen'
        resource url:'/css/staticcontent.css', bundle: 'screen'
        resource url:'/css/error.css', bundle: 'screen'
        resource url:'/css/institution.css', bundle: 'screen'
        resource url:'/css/jquery.fancybox.css', bundle: 'screen'
        resource url:'/css/viewer.css', bundle: 'screen'
        resource url:'/css/item-hierarchy.css', bundle: 'screen'
        resource url:'/css/modalDialog.css', bundle: 'screen'
        resource url:'/css/advancedsearch.css', bundle: 'screen'
        resource url:'/css/entity.css', bundle: 'screen'
        resource url:'/css/registration.css', bundle: 'screen'
        resource url:'/css/user-profile.css', bundle: 'screen'
        resource url:'/css/login.css', bundle: 'screen'
        resource url:'/css/confirmation.css', bundle: 'screen'
        resource url:'/css/saved-searches.css', bundle: 'screen'
        resource url:'/css/socialmedia.css', bundle: 'screen'
        resource url:'/css/apikey.css', bundle: 'screen'
        resource url:'/third-party/map/css/style.css', bundle: 'screen'
        resource url:'/third-party/map/css/ddbPlacenamePopupList.css', bundle: 'screen'
    }

    cssprint {
        resource url:'/css/ddb.css', attrs:[media:'print'], bundle: 'print'
        resource url:'/css/item.css', attrs:[media:'print'], bundle: 'print'
        resource url:'/css/institutionList.css', attrs:[media:'print'], bundle: 'print'
        resource url:'/css/results.css', attrs:[media:'print'], bundle: 'print'
        resource url:'/css/print.css', attrs:[media:'print'], bundle: 'print'
        resource url:'/third-party/map/css/style.css', attrs:[media:'print'], bundle: 'print'
        resource url:'/third-party/map/css/ddbPlacenamePopupList.css', attrs:[media:'print'], bundle: 'print'
    }

    javascript {
        resource url:'/js/vendor/jquery-1.8.2/jquery-1.8.2.min.js'
        resource url:'/js/vendor/jquery.dotdotdot-1.5.1/jquery.dotdotdot-1.5.1.js'
        resource url:'/js/vendor/jquery.carouFredSel-6.2.0/jquery.carouFredSel-6.2.0-packed.js'
        resource url:'/js/vendor/bootstrap-2.2.2/bootstrap.js'
        resource url:'/js/vendor/bootstrap-2.2.2/bootstrap-collapse.js'
        resource url:'/js/vendor/bootstrap-2.2.2/bootstrap-button.js'
        resource url:'/js/vendor/bootstrap-2.2.2/bootstrap-modal.js'
        resource url:'/js/vendor/bootstrap-2.2.2/bootstrap-multiselect.js'
        resource url:'/js/vendor/jquery.validate-1.11.1/jquery.validate.min.js'
        resource url:'/js/vendor/jquery.fancybox-2.1.4/jquery.fancybox.pack.js'
        resource url:'/js/vendor/jquery.checkall-1.4/jquery.checkall.js'
        resource url:'/js/vendor/jquery.cookies-2.2.0/jquery.cookies.2.2.0.min.js'
        // resource url:'/js/vendor/respond-1.1.0/respond.src.js'
        resource url:'/js/socialMediaManager2.js'
        resource url:'/js/stringBuilder2.js'
        resource url:'/js/vendor/underscore-1.3.1/underscore-min.js'
        resource url:'/js/vendor/jwplayer-6.2.3115/jwplayer.js'
        resource url:'/js/jwplayerKey2.js'
        resource url:'/js/largeCookie2.js'
        resource url:'/js/header.js'
        resource url:'/js/tooltip.js'
        resource url:'/js/vendor/json2-2010.11.17/json2.js'
        resource url:'/js/globalVariables2.js'
        resource url:'/js/globalScripts2.js'
        resource url:'/js/institutionList2.js'
        resource url:'/js/startPage2.js'
        resource url:'/js/itemHierarchy2.js'
        resource url:'/js/persistentLinksModalDialog2.js'
        resource url:'/js/binariesViewer2.js'
        resource url:'/js/advancedSearchPage2.js'
        resource url:'/js/searchResults2.js'
        resource url:'/js/favorites.js'
        resource url:'/js/registration.js'
        resource url:'/js/changeFavorite2.js'
        resource url:'/js/profile.js'
        resource url:'/js/passwordChange2.js'
        resource url:'/js/vendor/history-1.7.1/scripts/bundled/html4+html5/jquery.history.js'
        resource url:'/js/entity.js'
        resource url:'/js/item.js'
        resource url:'/js/savedSearches2.js'
        resource url:'/js/apiKey2.js'


    }

    autocomplete {
        resource url:'/css/autocomplete/css/blitzer/jquery-ui-1.10.2.custom.min.css', bundle: 'screen'
        resource url:'/js/vendor/autocomplete-1.10.2/js/jquery-ui-1.10.2.custom.min.js'
        resource url:'/js/myAutocomplete2.js'
    }
    // These are page specific bundles which should be merged back into ddbnext in the second step

    startpage {
        resource url:'/css/start-page.css', bundle: 'startpage'
        resource url:'/css/start-page.css', attrs:[media:'print'], bundle: 'startpageprint'
    }

    //These are pages that include third party components
    institution {
        resource url:'/js/institutionsMapAdapter2.js'
        resource url:'/third-party/map/geotemco_InstitutionItemMap.js'
    }

    institutionlist {
        resource url:'/js/institutionsMapAdapter2.js'
        resource url:'/third-party/map/geotemco_InstitutionsMap.js'
    }


    pdf {
        // This is the only working variant found! You must exclude 'zip,bundle' from the mappers list and
        // the CSS attributed with the exclude statement must NOT be used anywhere else in normal pages
        // or otherwise the resource plugin will produce invalid imports. It is best to provide a CSS exclusively used
        // for the PDF generation. Remember to to make sure that no other compression tool like the compress-plugin
        // is compressing the css after the resource-plugin, otherwise the PDF-export will fail, since
        // the plugin can not handle zipped ressources (see Config.groovy for that).
        resource url:'/css/itemPdf.css', exclude:'zip,bundle', attrs:[media:'print']
    }
}
