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
package de.ddb.next.cluster

class ClusterCache {

    public final static String CONTEXT_ATTRIBUTE_NAME = "ClusterCache"

    def cache = [:]

    public ClusterCache(){
    }

    def addCluster(List selectedSectors, def clusters, long cacheValidInMillis){
        String key = this.generateKey(selectedSectors)
        def cacheEntry = [:]
        cacheEntry["data"] = clusters
        cacheEntry["validUntil"] = new Date().getTime() + cacheValidInMillis
        this.cache.put(key, cacheEntry)
    }

    def getCluster(List selectedSectors){
        String key = this.generateKey(selectedSectors)
        def cacheEntry = this.cache.get(key)
        if(cacheEntry != null && cacheEntry.validUntil > new Date().getTime()){
            return cacheEntry.data
        }else{
            return null
        }
    }

    String generateKey(List selectedSectors){
        String key = "sectors:"
        selectedSectors.each { key += it }
        return key
    }
}
