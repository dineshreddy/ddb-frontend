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
