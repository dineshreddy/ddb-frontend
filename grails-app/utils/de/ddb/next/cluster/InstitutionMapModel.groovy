package de.ddb.next.cluster


class InstitutionMapModel {

    def allMapData
    def institutionsById
    def deBoundaries = [
        minLat: 47.2703, // 46.69258,
        minLon:  5.8667, //  2.48328,
        maxLat: 55.0556, // 55.86127,
        maxLon: 15.0419  // 18.5233
    ]

    def InstitutionMapModel(){
    }

    def getAllMapData(){
        return this.allMapData
    }

    def prepareInstitutionsData(institutionMapData) {
        def allInstitutionsArray = flatten(institutionMapData.institutions)
        allMapData = loadJson(allInstitutionsArray)

        institutionsById = [:]
        for (def xel = 0; xel < allMapData.size(); xel++) {
            def el = allMapData[xel]
            institutionsById[el.description.node.id] = el
            el.description.node.detailViewUri = "institutions/item/" + el.description.node.id
        }
    }


    def flatten(aTree) {
        def result = flatten_r(aTree,null,0,[
            count: 0,
            list: []])
        return result.list
    }

    def flatten_r(aTree, superNode, level, result) {
        for (def i = 0; i < aTree.size(); i++) {
            def treeNode = aTree[i]
            if (treeNode != null) {
                def mapElement = tree2mapElement(treeNode,
                        superNode,
                        result.count,
                        level)
                result.count ++
                if (mapElement != null) {
                    result.list.push(mapElement)
                    def children = treeNode.children
                    if (children instanceof List) {
                        flatten_r(children, mapElement.id, level+1, result)
                    }
                }
            }
        }
        return result
    }


    /*
     * make an object that can be handled by
     * GeoTemCoConfig.loadJSON(:Array)
     */
    def tree2mapElement(treeNode,superNode,number,indentLevel) {
        def latitude = treeNode.latitude.toDouble()
        def longitude = treeNode.longitude.toDouble()
        if (latitude < deBoundaries.minLat || latitude > deBoundaries.maxLat
        || longitude < deBoundaries.minLon || longitude > deBoundaries.maxLon
        ) {
            return null
        }

        def descr = [
            indentLevel: indentLevel,
            number: number,
            node: treeNode,
            superNode: superNode
        ]

        return [ // contains required elements and element 'description'
            id: treeNode.id,
            place: treeNode.name,
            sector: treeNode.sector,
            lat: latitude,
            lon: longitude,
            description: descr
        ]
    }

    def loadJson(json) {
        def mapTimeObjects = []
        def runningIndex = 0
        for (def i=0;i<json.size();i++) {
            try {
                def item = json[i]
                //def index = item.index || item.id || runningIndex++
                def index = item.id ? item.id : runningIndex++
                //def name = item.name || ""
                def name = item.name ? item.name : ""
                //def description = item.description || ""
                def description = item.description ? item.description : ""
                //def tableContent = item.tableContent || []
                def tableContent = item.tableContent ? item.tableContent : []
                def locations = []
                //                if (item.location instanceof List) {
                //                    for (def j = 0; j < item.location.size(); j++) {
                //                        def place = item.location[j].place || "unknown"
                //                        def lon = item.location[j].lon || ""
                //                        def lat = item.location[j].lat || ""
                //                        if (lon == "" || lat == "") {
                //                            throw new Exception()
                //                        }
                //                        locations.push([
                //                            longitude : lon,
                //                            latitude : lat,
                //                            place : place
                //                        ])
                //                    }
                //                } else {
                def place = item.place
                def lon = item.lon
                def lat = item.lat
                if (lon == null || lat == null ) {
                    throw new Exception()
                }
                locations.push([
                    longitude : lon,
                    latitude : lat,
                    place : place
                ])
                //                }
                def dates = []
                //                if (item.time instanceof List) {
                //                    for (def j = 0; j < item.time.length; j++) {
                //                        def time = GeoTemConfig.getTimeData(item.time[j])
                //                        if (time == null && !GeoTemConfig.incompleteData) {
                //                            throw "e"
                //                        }
                //                        dates.push(time)
                //                    }
                //                } else {
                //                    def time = GeoTemConfig.getTimeData(item.time)
                //                    if (time == null && !GeoTemConfig.incompleteData) {
                //                        throw "e"
                //                    }
                //                    dates.push(time)
                //                }


                def weight = item.weight ? item.weight : 1
                def mapTimeObject = new DataObject(name, description, locations, dates, weight, tableContent)
                mapTimeObject.setIndex(index)
                mapTimeObjects.push(mapTimeObject)

            } catch(Exception e) {
                continue
            }
        }

        return mapTimeObjects
    }

    def makeSectorsObject(selectorList) {
        def sectorObject = [:]
        for (def xel = 0; xel < selectorList.size(); xel++) {
            def el = selectorList[xel]
            sectorObject[el.sector] = [
                count: 0,
                name: el.name
            ]
        }
        return sectorObject
    }

    def selectSectors(aSectorSelection) {
        def sectorSelection = aSectorSelection // save the published state

        def time0 = (new Date()).getTime()

        def selectedSectors = makeSectorsObject(sectorSelection.selected)
        def deselectedSectors = makeSectorsObject(sectorSelection.deselected)
        def allSectors = []
        //        $.extend(_allSectors, _selectedSectors)
        //        $.extend(_allSectors, deselectedSectors)

        def filterSectors = (sectorSelection.selected.size() == 0) ? deselectedSectors: selectedSectors
        def sectorsMapData = []
        for (def xel = 0; xel < this.allMapData.size(); xel++) {
            def el = this.allMapData[xel]
            def elSector = el.description.node.sector
            if (filterSectors[elSector] != null) {
                sectorsMapData.push(el)
                filterSectors[elSector].count++
            }
        }

        def datasets = []
        datasets.push(new Dataset(sectorsMapData, "institutions"))

        return datasets
    }


}
