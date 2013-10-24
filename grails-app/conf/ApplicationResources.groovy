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
        resource url:'/js/jquery-1.8.2.min.js'
        resource url:'/js/jquery.dotdotdot-1.5.1.js'
        resource url:'/js/jquery.carouFredSel-6.2.0-packed.js'
        resource url:'/js/vendor/bootstrap.js'
        resource url:'/js/vendor/bootstrap-collapse.js'
        resource url:'/js/vendor/bootstrap-button.js'
        resource url:'/js/vendor/bootstrap-modal.js'
        resource url:'/js/vendor/bootstrap-multiselect.js'
        resource url:'/js/vendor/jquery.validate.min.js'
        // resource url:'/js/vendor/respond.src.js'
        resource url:'/js/jquery.fancybox.pack.js'
        resource url:'/js/jquery.checkall.js'
        resource url:'/js/jquery.cookies.2.2.0.min.js'
        resource url:'/js/SocialMediaManager.js'
        resource url:'/js/StringBuilder.js'
        resource url:'/js/underscore-min.js'
        resource url:'/jwplayer/jwplayer.js'
        resource url:'/js/jwplayer-key.js'
        resource url:'/js/large-cookie.js'
        resource url:'/js/header.js'
        resource url:'/js/tooltip.js'
        resource url:'/js/json2.js'
        resource url:'/js/global-variables.js'
        resource url:'/js/global-scripts.js'
        resource url:'/js/institution-list.js'
        resource url:'/js/start-page.js'
        resource url:'/js/item-hierarchy.js'
        resource url:'/js/persistent-links-modal-dialog.js'
        resource url:'/js/binaries-viewer.js'
        resource url:'/js/advanced-search-page.js'
        resource url:'/js/search-results.js'
        resource url:'/js/favorites.js'
        resource url:'/js/registration.js'
        resource url:'/js/changeFavorite.js'
        resource url:'/js/profile.js'
        resource url:'/js/passwordchange.js'
        resource url:'/js/vendor/history.js/scripts/bundled/html4+html5/jquery.history.js'
        resource url:'/js/entity.js'
        resource url:'/js/item.js'
        resource url:'/js/saved-searches.js'
    }

    autocomplete {
        resource url:'/css/autocomplete/css/blitzer/jquery-ui-1.10.2.custom.min.css', bundle: 'screen'
        resource url:'/js/autocomplete/js/jquery-ui-1.10.2.custom.min.js'
        resource url:'/js/myautocomplete.js'
    }
    // These are page specific bundles which should be merged back into ddbnext in the second step

    startpage {
        resource url:'/css/start-page.css', bundle: 'startpage'
        resource url:'/css/start-page.css', attrs:[media:'print'], bundle: 'startpageprint'
    }

    //These are pages that include third party components
    institution {
        resource url:'/js/InstitutionsMapAdapter.js'
        resource url:'/third-party/map/geotemco_InstitutionItemMap.js'
    }

    institutionlist {
        resource url:'/js/InstitutionsMapAdapter.js'
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
