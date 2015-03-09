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

/**
 * Taglib for checking if the currently displayed page fits a controller/action/dir combination
 * given as params to the tag. If the param-combination matches the the current page, the taglib
 * returns true, otherwise false.
 * @author hla
 */
class IsMappingActiveTagLib {
    static namespace = "ddb"
    static returnObjectForTags = ['isMappingActive']

    def grailsUrlMappingsHolder

    /**
     * Return true if the given controller/action/dir combination fits the currently 
     * displayed page. The following combinations are possible parameters for the tag:
     * 1) controller
     * 2) controller/action
     * 3) controller/dir
     *
     * @attr context The current page context, normally just ${params}
     * @attr testif A list of maps, with each map defining a controller/action/dir combination that 
     *  should be tested against the currently displayed page.
     * @attr testmenu A menu item defining a controller/action combination that 
     *  should be tested against the currently displayed page.
     * @attr testsubmenu boolean value that indicates whether or not submenus should be checked, too
     */
    def isMappingActive = {attrs, body ->
        boolean result = false
        def controller = attrs?.context?.controller
        def action = attrs?.context?.action
        def dir = attrs?.context?.dir
        def id = attrs?.context?.id
        def controllers = attrs?.testif
        def menuItem = attrs?.testmenu
        def testSubMenu = attrs?.testsubmenu

        if (controllers) {
            result = testForControllers(controllers, controller, dir, action)
        }
        else if (menuItem) {
            result = testForMenuItem(menuItem, controller, dir, action, id, testSubMenu)
        }
        return result
    }

    /**
     * Test if the given combination of controller/action/dir matches one of the controllers from the given list.
     *
     * @param controllers controller list to check
     * @param controller the controller
     * @param dir the dir
     * @param action the action
     *
     * @return true if a matching controller was found
     */
    private boolean testForControllers(def controllers, def controller, def dir, def action) {
        boolean result = false

        for (it in controllers) {
            // controller/action combination
            if (it.controller && it.action) {
                if(controller == it.controller && action == it.action) {
                    result = true
                    break
                }
                // controller/dir combination
            } else if (it.controller && it.dir) {
                if (controller == it.controller && dir == it.dir) {
                    result = true
                    break
                }
                // controller combination
            } else if (it.controller) {
                if (controller == it.controller) {
                    result = true
                    break
                }
            }
        }
        return result
    }

    /**
     * Test if the given combination of controller/dir matches the given menu item.
     *
     * @param menuItem menu item to check
     * @param controller the controller
     * @param dir the dir
     * @param action the action
     * @param id the id
     * @param testSubMenu if true then also sub menu items are checked
     *
     * @return true if the menu item matches
     */
    private boolean testForMenuItem(def menuItem, def controller, def dir, def action, def id, def testSubMenu) {
        boolean result

        // check if the menu item itself matches
        result = uriMatches(menuItem.uri, controller, dir, action, id)

        // check if a sub menu item matches
        if (testSubMenu && menuItem.subMenuItems && !result) {
            for (subMenuItem in menuItem.subMenuItems) {
                if (uriMatches(subMenuItem.uri, controller, dir, action, id)) {
                    result = true
                    break
                }
            }
        }
        return result
    }

    /**
     * Test if the given combination of controller/dir matches the given uri.
     *
     * @param uri uri to check
     * @param controller the controller
     * @param dir the dir
     * @param action the action
     * @param id the id
     *
     * @return true if the uri matches
     */
    private boolean uriMatches(def uri, def controller, def dir, def action, def id) {
        boolean result = new URI(uri).equals(new URI("/" + controller + "/" + dir))

        if (!result) {
            def mappingParameters = grailsUrlMappingsHolder.match(uri).getParameters()

            result = mappingParameters.controller == controller &&
                    (mappingParameters.action == action || mappingParameters.action == dir) &&
                    mappingParameters.id == id
        }
        return result
    }
}
