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

class GetFrontendVersionTagLib {

    static namespace = "ddb"

    def configurationService

    def getFrontendVersion = { attrs, body ->
        try{
            String appVersion = g.meta(name: "app.version")
            String buildNumber = g.meta(name: "build.number")

            if(buildNumber == null || buildNumber.trim().isEmpty()) {
                buildNumber = "eclipse"
            }

            String frontendVersion = appVersion
            if(appVersion == "develop" ){
                frontendVersion += "#"+buildNumber
            }

            out << frontendVersion
        }catch(Exception e) {
            out << "error"
        }
    }
}
