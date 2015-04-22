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

import grails.test.mixin.TestFor

import org.junit.Test

@TestFor(MainMenuService)
class MainMenuServiceIntegrationTests {
    def mainMenuService

    @Test
    void loadFooterMenu() {
        assert mainMenuService.footerMenu.length > 0
    }

    @Test
    void loadHeaderMenu() {
        assert mainMenuService.headerMenu.length > 0
    }
}
