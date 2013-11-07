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
package de.ddb.next.constants

public enum CortexNamespace {

    RDF("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
    NS2("ns2", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
    NS3("ns3", "http://www.deutsche-digitale-bibliothek.de/item"),
    NS4("ns4", "http://www.deutsche-digitale-bibliothek.de/cortex")

    String namespace
    String uri

    private CortexNamespace(namespace, uri) {
        this.namespace = namespace
        this.uri = uri
    }
}
