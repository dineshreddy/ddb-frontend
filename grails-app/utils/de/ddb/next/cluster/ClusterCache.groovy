package de.ddb.next.cluster

class ClusterCache {

    public final static String CONTEXT_ATTRIBUTE_NAME = "ClusterCache"

    def cache = [:]

    public ClusterCache(){
    }

    def addCluster(List selectedSectors, List clusters){
        String key = this.generateKey(selectedSectors)
        this.cache.put(key, clusters)
    }

    def getCluster(List selectedSectors){
        String key = this.generateKey(selectedSectors)
        return this.cache.get(key)
    }

    String generateKey(List selectedSectors){
        String key = "sectors:"
        selectedSectors.each { key += it }
        return key
    }
}
