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

$(document)
    .ready(
        function() {

          // Open all external links in a new window
          $(
              'a[href^="http"]:not([href^="http://localhost"],[href^="http://dev.escidoc.org"],[href^="https://www.deutsche-digitale-bibliothek.de"])')
              .attr('target', '_blank');

        });
