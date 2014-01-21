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

import de.ddb.next.exception.ItemNotFoundException

class MapController {

    def index() {
        throw new ItemNotFoundException() // Deactivated maptest for 4.3.RC1, see DDBNEXT-1110
        render(view: "map", model: [])
    }

    def multipolygone() {
        throw new ItemNotFoundException() // Deactivated multipolygone for 4.3.RC1, like for the maptest
        render(view: "multipolygone", model: [])
    }
}
