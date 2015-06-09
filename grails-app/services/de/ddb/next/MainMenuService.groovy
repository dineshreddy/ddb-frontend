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

import groovy.json.JsonSlurper
import de.ddb.common.ApiConsumer
import de.ddb.next.beans.MenuItem

/**
 * Service for loading the main menu from JSON.
 *
 * @author sche
 */
class MainMenuService {
    private static final String PATH = "de/ddb-services/menu"

    def configurationService
    def transactional = false

    private MenuItem[] footerMenu
    private MenuItem[] headerMenu

    public MenuItem[] getFooterMenu() {
        if (!footerMenu) {
            footerMenu = loadMainMenu(configurationService.getFooterMenu())
        }
        return footerMenu
    }

    public MenuItem[] getHeaderMenu() {
        if (!headerMenu) {
            headerMenu = loadMainMenu()
        }
        return headerMenu
    }

    /**
     * Load the menu JSON file from CMS.
     *
     * @return list of menu items
     */
    private MenuItem[] loadMainMenu() {
        def result
        def apiResponse = ApiConsumer.getJson(configurationService.cmsUrl, PATH)

        if (apiResponse.isOk()) {
            def mainMenu = apiResponse.getResponse().mainmenu

            result = [mainMenu.size()]
            mainMenu.each {menuItem ->
                result[menuItem.position - 1] =
                        new MenuItem(menuItem.deValue, menuItem.enValue, menuItem.ref, menuItem.submenu)
            }
        }
        else {
            log.error "faild to load main menu file from " + configurationService.cmsUrl + PATH
        }
        return result
    }

    /**
     * Load the menu JSON from a file.
     *
     * @param url JSON file to load
     *
     * @return list of menu items
     */
    private MenuItem[] loadMainMenu(String url) {
        def result

        def slurper = new JsonSlurper()
        File menu = new File(url)

        try {
            def jsonMenu = slurper.parse(menu.newReader())
            if (jsonMenu) {
                def mainMenu = jsonMenu.mainmenu
                result = [mainMenu.size()]
                mainMenu.each {menuItem ->
                    result[menuItem.position - 1] =
                            new MenuItem(menuItem.deValue, menuItem.enValue, menuItem.ref, menuItem.submenu)
                }
            }
            else {
                log.error "faild to load main menu file " + menu
            }
        }
        catch (IOException e) {
            log.error "faild to load main menu file: " + e
        }
        return result
    }
}
