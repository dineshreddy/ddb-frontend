package de.ddb.next.cluster



class Binning {

    def zoomLevels = 19
    def binnings = [:]
    def maximumRadius = 0
    def maximumPoints = 0
    def minArea = 0
    def maxArea = 0
    def mapIndex = 0
    def circleGap = 0
    def circlePackings = true
    def binning = "generic"
    def minimumRadius = 4
    def noBinningRadii = "dynamic"
    def binCount = 10
    def objects = null
    def displayProjection = "EPSG:4326"
    def projection = "EPSG:900913"

    def Binning(){
    }

    def getSet() {
        def type = this.binning
        if (!type) {
            return this.getExactBinning()
        } else if (type == 'generic') {
            return this.getGenericBinning()
        } else if (type == 'square') {
            return this.getSquareBinning()
        } else if (type == 'hexagonal') {
            return this.getHexagonalBinning()
        } else if (type == 'triangular') {
            return this.getTriangularBinning()
        }
    }

    def getExactBinning() {
        //if (this.binnings['exact'].size() == 0) {
        this.exactBinning()
        //}
        return this.binnings['exact']
    }

    def getGenericBinning() {
        //if(this.binnings['generic'].size() == 0) {
        this.genericBinning()
        //}
        return this.binnings['generic']
    }

    def getSquareBinning() {
        //if (this.binnings['square'].size() == 0) {
        this.squareBinning()
        //}
        return this.binnings['square']
    }

    def getHexagonalBinning() {
        //if (this.binnings['hexagonal'].size() == 0) {
        this.hexagonalBinning()
        //}
        return this.binnings['hexagonal']
    }

    def getTriangularBinning() {
        //if (this.binnings['triangular'].size() == 0) {
        this.triangularBinning()
        //}
        return this.binnings['triangular']
    }

    def reset() {
        this.binnings = []
        this.minimumRadius = 4
        this.maximumRadius = 0
        this.maximumPoints = 0
        this.minArea = 0
        this.maxArea = 0
    }

    def getMaxRadius(size) {
        return 4 * Math.log(size) / Math.log(2)
    }

    def setObjects(objects) {
        this.objects = objects
        for (def i = 0; i < this.objects.size(); i++) {
            def weight = 0
            for (def j = 0; j < this.objects[i].size(); j++) {
                if (this.objects[i][j].isGeospatial) {
                    weight += this.objects[i][j].weight
                }
            }
            def r = this.getMaxRadius(weight)
            if (r > this.maximumRadius) {
                this.maximumRadius = r
                this.maximumPoints = weight
                this.maxArea = Math.PI * this.maximumRadius * this.maximumRadius
                this.minArea = Math.PI * this.minimumRadius * this.minimumRadius
            }
        }
    }

    def dist(x1, y1, x2, y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
    }

    def exactBinning() {
        def circleSets = []
        def hashMaps = []
        def selectionHashs = []

        def circleAggregates = []
        def bins = []
        for (def i = 0; i < this.objects.size(); i++) {
            bins.push([])
            circleAggregates.push([])
            for (def j = 0; j < this.objects[i].size(); j++) {
                def o = this.objects[i][j]
                if (o.isGeospatial) {
                    if (circleAggregates[i]['' + o.getLongitude(this.mapIndex)] == null) {
                        circleAggregates[i]['' + o.getLongitude(this.mapIndex)] = []
                    }
                    if (circleAggregates[i][''+o.getLongitude(this.mapIndex)]['' + o.getLatitude(this.mapIndex)] == null) {
                        circleAggregates[i][''+o.getLongitude(this.mapIndex)]['' + o.getLatitude(this.mapIndex)] = []
                        bins[i].push(circleAggregates[i][''+o.getLongitude(this.mapIndex)]['' + o.getLatitude(this.mapIndex)])
                    }
                    circleAggregates[i][''+o.getLongitude(this.mapIndex)]['' + o.getLatitude(this.mapIndex)].push(o)
                }
            }
        }

        def circles = []
        def hashMap = []
        def selectionMap = []
        for (def i = 0; i < bins.size(); i++) {
            circles.push([])
            hashMap.push([])
            selectionMap.push([])
            for (def j = 0; j < bins[i].size(); j++) {
                def bin = bins[i][j]
                //def p = new OpenLayers.Geometry.Point(bin[0].getLongitude(this.mapIndex), bin[0].getLatitude(this.mapIndex), null)
                def p = new Point(bin[0].getLongitude(this.mapIndex), bin[0].getLatitude(this.mapIndex))
                p.transform(this.displayProjection, this.projection)
                def weight = 0
                for (def z = 0; z < bin.size(); z++) {
                    weight += bin[z].weight
                }
                def radius = this.minimumRadius
                if (this.noBinningRadii == 'dynamic') {
                    radius = this.getRadius(weight)
                }
                def circle = new CircleObject(p.x, p.y, 0, 0, bin, radius, i, weight)
                circles[i].push(circle)
                for (def z = 0; z < bin.size(); z++) {
                    hashMap[i][bin[z].index] = circle
                    selectionMap[i][bin[z].index] = false
                }
            }
        }
        for (def k = 0; k < this.zoomLevels; k++) {
            circleSets.push(circles)
            hashMaps.push(hashMap)
            selectionHashs.push(selectionMap)
        }
        this.binnings['exact'] = [
            circleSets : circleSets,
            hashMaps : hashMaps,
            selectionHashs : selectionHashs
        ]
    }

    def orderBalls(b1, b2) {
        if (b1.radius > b2.radius) {
            return -1
        }
        if (b2.radius > b1.radius) {
            return 1
        }
        return 0
    }

    def createCircle(sx, sy, ball, point, fatherBin, circles, hashMap, selectionMap) {
        //def index = id ? id : ball.search
        def index = ball.search
        def circle = new CircleObject(point.x, point.y, sx, sy, ball.elements, ball.radius, index, ball.weight, fatherBin)
        circles[ball.search].push(circle)
        fatherBin.circles[index] = circle
        fatherBin.length = fatherBin.length ++
        for (def k = 0; k < ball.elements.size(); k++) {
            hashMap[ball.search][ball.elements[k].index] = circle
            selectionMap[ball.search][ball.elements[k].index] = false
        }
    }

    def getResolutionForZoom(zoom){
        def resolutionMap = [:]
        resolutionMap["0"] = 0.5971642833948135
        resolutionMap["1"] = 1.194328566789627
        resolutionMap["2"] = 2.388657133579254
        resolutionMap["3"] = 4.777314267158508
        resolutionMap["4"] = 9.554628534317017
        resolutionMap["5"] = 19.109257068634033
        resolutionMap["6"] = 38.218514137268066
        resolutionMap["7"] = 76.43702827453613
        resolutionMap["8"] = 152.87405654907226
        resolutionMap["9"] = 305.74811309814453
        resolutionMap["10"] = 611.4962261962891
        resolutionMap["11"] = 1222.9924523925781
        resolutionMap["12"] = 2445.9849047851562
        resolutionMap["13"] = 4891.9698095703125
        resolutionMap["14"] = 9783.939619140625
        resolutionMap["15"] = 19567.87923828125
        resolutionMap["16"] = 39135.7584765625
        resolutionMap["17"] = 78271.516953125
        resolutionMap["18"] = 156543.03390625

        return resolutionMap[""+zoom]
    }

    def genericClustering(objects, id = null) {
        def binSets = []
        def circleSets = []
        def hashMaps = []
        def selectionHashs = []
        def clustering = new Clustering(-20037508.34, -20037508.34, 20037508.34, 20037508.34)

        def self = this
        def geospatialObjectCounter = 0
        for (def i = 0; i < objects.size(); i++) {
            for (def j = 0; j < objects[i].size(); j++) {
                def o = objects[i][j]
                if (o.isGeospatial) {
                    geospatialObjectCounter++
                    //def p = new OpenLayers.Geometry.Point(o.getLongitude(self.mapIndex), o.getLatitude(self.mapIndex), null)
                    def p = new Point(o.getLongitude(self.mapIndex), o.getLatitude(self.mapIndex), null)
                    //p.transform(self.map.displayProjection, self.map.projection)
                    p.transform(self.displayProjection, self.projection)
                    def point = new Vertex(Math.floor(p.x), Math.floor(p.y), objects.size(), self)
                    point.addElement(o, o.weight, i)
                    clustering.add(point)
                }
            }
        }

        for (def i = 0; i < this.zoomLevels; i++) {
            def bins = []
            def circles = []
            def hashMap = []
            def selectionMap = []
            for (def j = 0; j < objects.size(); j++) {
                circles.push([])
                hashMap.push([:])
                selectionMap.push([:])
            }
            def resolution = this.getResolutionForZoom(i)
            clustering.mergeForResolution(resolution, this.circleGap)
            for (def j = 0; j < clustering.vertices.size(); j++) {
                def point = clustering.vertices[j]
                if (!point.legal) {
                    continue
                }
                def balls = []
                for (def k = 0; k < point.elements.size(); k++) {
                    if (point.elements[k].size() > 0) {
                        balls.push([
                            search : k,
                            elements : point.elements[k],
                            radius : point.radii[k],
                            weight : point.weights[k]
                        ])
                    }
                }
                //                def orderBalls = function(b1, b2) {
                //                    if (b1.radius > b2.radius) {
                //                        return -1
                //                    }
                //                    if (b2.radius > b1.radius) {
                //                        return 1
                //                    }
                //                    return 0
                //                }
                def fatherBin = [
                    circles : [],
                    length : 0,
                    radius : point.radius / resolution,
                    x : point.x,
                    y : point.y
                ]
                for (def k = 0; k < objects.size(); k++) {
                    fatherBin.circles.push(false)
                }
                //                def createCircle = function(sx, sy, ball) {
                //                    def index = id ? id : ball.search
                //                    def circle = new CircleObject(point.x, point.y, sx, sy, ball.elements, ball.radius, index, ball.weight, fatherBin)
                //                    circles[ball.search].push(circle)
                //                    fatherBin.circles[index] = circle
                //                    fatherBin.length++
                //                    for (def k = 0; k < ball.elements.size(); k++) {
                //                        hashMap[ball.search][ball.elements[k].index] = circle
                //                        selectionMap[ball.search][ball.elements[k].index] = false
                //                    }
                //                }
                if (balls.size() == 1) {
                    //createCircle(0, 0, balls[0])
                    createCircle(0, 0, balls[0], point, fatherBin, circles, hashMap, selectionMap)
                } else if (balls.size() == 2) {
                    def r1 = balls[0].radius
                    def r2 = balls[1].radius
                    //createCircle(-1 * r2, 0, balls[0])
                    createCircle(-1 * r2, 0, balls[0], point, fatherBin, circles, hashMap, selectionMap)
                    //createCircle(r1, 0, balls[1])
                    createCircle(r1, 0, balls[1], point, fatherBin, circles, hashMap, selectionMap)
                } else if (balls.size() == 3) {
                    balls.sort(orderBalls)
                    def r1 = balls[0].radius
                    def r2 = balls[1].radius
                    def r3 = balls[2].radius
                    def d = ((2 / 3 * Math.sqrt(3) - 1) / 2) * r2
                    def delta1 = point.radius / resolution - r1 - d
                    def delta2 = r1 - delta1
                    //createCircle(-delta1, 0, balls[0])
                    createCircle(-delta1, 0, balls[0], point, fatherBin, circles, hashMap, selectionMap)
                    //createCircle(delta2 + r2 - 3 * d, r2, balls[1])
                    createCircle(delta2 + r2 - 3 * d, r2, balls[1], point, fatherBin, circles, hashMap, selectionMap)
                    //createCircle(delta2 + r3 - (3 * d * r3 / r2), -1 * r3, balls[2])
                    createCircle(delta2 + r3 - (3 * d * r3 / r2), -1 * r3, balls[2], point, fatherBin, circles, hashMap, selectionMap)
                } else if (balls.size() == 4) {
                    balls.sort(orderBalls)
                    def r1 = balls[0].radius
                    def r2 = balls[1].radius
                    def r3 = balls[2].radius
                    def r4 = balls[3].radius
                    def d = (Math.sqrt(2) - 1) * r2
                    //createCircle(-1 * d - r2, 0, balls[0])
                    createCircle(-1 * d - r2, 0, balls[0], point, fatherBin, circles, hashMap, selectionMap)
                    //createCircle(r1 - r2, -1 * d - r4, balls[3])
                    createCircle(r1 - r2, -1 * d - r4, balls[3], point, fatherBin, circles, hashMap, selectionMap)
                    //createCircle(r1 - r2, d + r3, balls[2])
                    createCircle(r1 - r2, d + r3, balls[2], point, fatherBin, circles, hashMap, selectionMap)
                    //createCircle(d + r1, 0, balls[1])
                    createCircle(d + r1, 0, balls[1], point, fatherBin, circles, hashMap, selectionMap)
                }
                if (fatherBin.length > 1) {
                    bins.push(fatherBin)
                }
            }
            circleSets.push(circles)
            binSets.push(bins)
            hashMaps.push(hashMap)
            selectionHashs.push(selectionMap)
        }
        circleSets = circleSets.reverse()
        binSets = binSets.reverse()
        hashMaps = hashMaps.reverse()
        selectionHashs = selectionHashs.reverse()

        return [
            circleSets : circleSets,
            binSets : binSets,
            hashMaps : hashMaps,
            selectionHashs : selectionHashs
        ]
    }

    def genericBinning() {
        if (this.circlePackings || this.objects.size() == 1) {
            def resultList = this.genericClustering(this.objects)
            this.binnings['generic'] = resultList
        } else {
            def circleSets = []
            def hashMaps = []
            def selectionHashs = []
            for (def i = 0; i < this.objects.size(); i++) {
                def sets = this.genericClustering([this.objects[i]], i)
                if (i == 0) {
                    circleSets = sets.circleSets
                    hashMaps = sets.hashMaps
                    selectionHashs = sets.selectionHashs
                } else {
                    for (def j = 0; j < circleSets.size(); j++) {
                        circleSets[j] = circleSets[j].concat(sets.circleSets[j])
                        hashMaps[j] = hashMaps[j].concat(sets.hashMaps[j])
                        selectionHashs[j] = selectionHashs[j].concat(sets.selectionHashs[j])
                    }
                }
            }
            this.binnings['generic'] = [
                circleSets : circleSets,
                hashMaps : hashMaps,
                selectionHashs : selectionHashs
            ]
        }
    }

    def getRadius(n) {
        if (n == 0) {
            return 0
        }
        if (n == 1) {
            return this.minimumRadius
        }
        return Math.sqrt((this.minArea + (this.maxArea - this.minArea) / (this.maximumPoints - 1) * (n - 1) ) / Math.PI)
    }

    def getBinRadius(n, r_max, N) {
        if (n == 0) {
            return 0
        }
        /*
         function log2(x) {
         return (Math.log(x)) / (Math.log(2));
         }
         def r0 = this.minimumRadius;
         def r;
         if ( typeof r_max == 'undefined') {
         return r0 + n / Math.sqrt(this.maximumPoints);
         }
         return r0 + (r_max - r0 ) * log2(n) / log2(N);
         */
        def minArea = Math.PI * this.minimumRadius * this.minimumRadius
        def maxArea = Math.PI * r_max * r_max
        return Math.sqrt((minArea + (maxArea - minArea) / (N - 1) * (n - 1) ) / Math.PI)
    }

    def shift(type, bin, radius, elements) {

        def x1 = bin.x, x2 = 0
        def y1 = bin.y, y2 = 0
        for (def i = 0; i < elements.size(); i++) {
            x2 += elements[i].x / elements.size()
            y2 += elements[i].y / elements.size()
        }

        def sx = 0, sy = 0

        if (type == 'square') {
            def dx = Math.abs(x2 - x1)
            def dy = Math.abs(y2 - y1)
            def m = dy / dx
            def n = y1 - m * x1
            if (dx > dy) {
                sx = bin.x - (x1 + bin.r - radius )
                sy = bin.y - (m * bin.x + n )
            } else {
                sy = bin.y - (y1 + bin.r - radius )
                sx = bin.x - (bin.y - n) / m
            }
        }

        return [
            x : sx,
            y : sy
        ]
    }

    def binSize(elements) {
        def size = 0
        for (def i=0;i<elements.size();i++ ) {
            size += elements[i].weight
        }
        return size
    }

    def setCircleSet(id, binData) {
        def circleSets = []
        def hashMaps = []
        def selectionHashs = []
        for (def i = 0; i < binData.size(); i++) {
            def circles = []
            def hashMap = []
            def selectionMap = []
            for (def j = 0; j < this.objects.size(); j++) {
                circles.push([])
                hashMap.push([])
                selectionMap.push([])
            }
            def points = []
            def max = 0
            def radius = 0
            def resolution = this.map.getResolutionForZoom(i)
            for (def j = 0; j < binData[i].size(); j++) {
                for (def k = 0; k < binData[i][j].bin.size(); k++) {
                    def bs = this.binSize(binData[i][j].bin[k])
                    if (bs > max) {
                        max = bs
                        radius = binData[i][j].r / resolution
                    }
                }
            }
            for (def j = 0; j < binData[i].size(); j++) {
                def bin = binData[i][j]
                for (def k = 0; k < bin.bin.size(); k++) {
                    if (bin.bin[k].size() == 0) {
                        continue
                    }
                    def weight = this.binSize(bin.bin[k])
                    def r = this.getBinRadius(weight, radius, max)
                    def shift = this.shift(id, bin, r * resolution, bin.bin[k], i)
                    def circle = new CircleObject(bin.x - shift.x, bin.y - shift.y, 0, 0, bin.bin[k], r, k, weight)
                    circles[k].push(circle)
                    for (def z = 0; z < bin.bin[k].size(); z++) {
                        hashMap[k][bin.bin[k][z].index] = circle
                        selectionMap[k][bin.bin[k][z].index] = false
                    }
                }
            }
            circleSets.push(circles)
            hashMaps.push(hashMap)
            selectionHashs.push(selectionMap)
        }
        this.binnings[id] = [
            circleSets : circleSets,
            hashMaps : hashMaps,
            selectionHashs : selectionHashs
        ]
    }

    def squareBinning() {

        def l = 20037508.34
        def area0 = l * l * 4
        def binCount = this.binCount

        def bins = []
        def binData = []
        for (def k = 0; k < this.zoomLevels; k++) {
            bins.push([])
            binData.push([])
        }

        for (def i = 0; i < this.objects.size(); i++) {
            for (def j = 0; j < this.objects[i].size(); j++) {
                def o = this.objects[i][j]
                if (!o.isGeospatial) {
                    continue
                }
                //def p = new OpenLayers.Geometry.Point(o.getLongitude(this.mapIndex), o.getLatitude(this.mapIndex), null)
                def p = new Point(o.getLongitude(this.mapIndex), o.getLatitude(this.mapIndex), null)
                p.transform(this.displayProjection, this.projection)
                o.x = p.x
                o.y = p.y
                for (def k = 0; k < this.zoomLevels; k++) {
                    def bc = binCount * Math.pow(2, k)
                    def a = 2 * l / bc
                    def binX = Math.floor((p.x + l) / (2 * l) * bc)
                    def binY = Math.floor((p.y + l) / (2 * l) * bc)
                    if (bins[k]['' + binX] == null) {
                        bins[k]['' + binX] = []
                    }
                    if (bins[k][''+binX]['' + binY] == null) {
                        bins[k][''+binX]['' + binY] = []
                        for (def z = 0; z < this.objects.size(); z++) {
                            bins[k][''+binX]['' + binY].push([])
                        }
                        def x = binX * a + a / 2 - l
                        def y = binY * a + a / 2 - l
                        binData[k].push([
                            bin : bins[k][''+binX]['' + binY],
                            x : x,
                            y : y,
                            a : a,
                            r : a / 2
                        ])
                    }
                    bins[k][''+binX][''+binY][i].push(o)
                }
            }
        }

        this.setCircleSet('square', binData)
    }

    def triangularBinning() {

        def l = 20037508.34
        def a0 = this.binCount
        def a1 = Math.sqrt(4 * a0 * a0 / Math.sqrt(3))
        def binCount = a0 / a1 * a0

        def bins = []
        def binData = []
        for (def k = 0; k < this.zoomLevels; k++) {
            bins.push([])
            binData.push([])
        }

        for (def i = 0; i < this.objects.size(); i++) {
            for (def j = 0; j < this.objects[i].size(); j++) {
                def o = this.objects[i][j]
                if (!o.isGeospatial) {
                    continue
                }
                //def p = new OpenLayers.Geometry.Point(o.getLongitude(this.mapIndex), o.getLatitude(this.mapIndex), null)
                def p = new Point(o.getLongitude(this.mapIndex), o.getLatitude(this.mapIndex), null)
                p.transform(this.displayProjection, this.projection)
                o.x = p.x
                o.y = p.y
                for (def k = 0; k < this.zoomLevels; k++) {
                    def x_bc = binCount * Math.pow(2, k)
                    def y_bc = x_bc * x_bc / Math.sqrt(x_bc * x_bc - x_bc * x_bc / 4)
                    def a = 2 * l / x_bc
                    def h = 2 * l / y_bc
                    def binY = Math.floor((p.y + l) / (2 * l) * y_bc)
                    if (bins[k]['' + binY] == null) {
                        bins[k]['' + binY] = []
                    }
                    def triangleIndex
                    def partitionsX = x_bc * 2
                    def partition = Math.floor((p.x + l) / (2 * l) * partitionsX)
                    def xMax = a / 2
                    def yMax = h
                    def x = p.x + l - partition * a / 2
                    def y = p.y + l - binY * h
                    if (binY % 2 == 0 && partition % 2 == 1 || binY % 2 == 1 && partition % 2 == 0) {
                        if (y + yMax / xMax * x < yMax) {
                            triangleIndex = partition
                        } else {
                            triangleIndex = partition + 1
                        }
                    } else {
                        if (y > yMax / xMax * x) {
                            triangleIndex = partition
                        } else {
                            triangleIndex = partition + 1
                        }
                    }
                    if (bins[k][''+binY]['' + triangleIndex] == null) {
                        bins[k][''+binY]['' + triangleIndex] = []
                        for (def z = 0; z < this.objects.size(); z++) {
                            bins[k][''+binY]['' + triangleIndex].push([])
                        }
                        def r = Math.sqrt(3) / 6 * a
                        def x2 = (triangleIndex - 1) * a / 2 + a / 2 - l
                        def y2
                        if (binY % 2 == 0 && triangleIndex % 2 == 0 || binY % 2 == 1 && triangleIndex % 2 == 1) {
                            y2 = binY * h + h - r - l
                        } else {
                            y2 = binY * h + r - l
                        }
                        binData[k].push([
                            bin : bins[k][''+binY]['' + triangleIndex],
                            x : x2,
                            y : y2,
                            a : a,
                            r : r
                        ])
                    }
                    bins[k][''+binY][''+triangleIndex][i].push(o)
                }
            }
        }

        this.setCircleSet('triangular', binData)
    }

    def hexagonalBinning() {

        def l = 20037508.34
        def a0 = this.binCount
        def a2 = Math.sqrt(4 * a0 * a0 / Math.sqrt(3)) / Math.sqrt(6)
        def binCount = a0 / a2 * a0

        def bins = []
        def binData = []
        for (def k = 0; k < this.zoomLevels; k++) {
            bins.push([])
            binData.push([])
        }

        for (def i = 0; i < this.objects.size(); i++) {
            for (def j = 0; j < this.objects[i].size(); j++) {
                def o = this.objects[i][j]
                if (!o.isGeospatial) {
                    continue
                }
                //def p = new OpenLayers.Geometry.Point(o.getLongitude(this.mapIndex), o.getLatitude(this.mapIndex), null)
                def p = new Point(o.getLongitude(this.mapIndex), o.getLatitude(this.mapIndex), null)
                p.transform(this.displayProjection, this.projection)
                o.x = p.x
                o.y = p.y
                for (def k = 0; k < this.zoomLevels; k++) {
                    def x_bc = binCount * Math.pow(2, k)
                    def y_bc = x_bc * x_bc / Math.sqrt(x_bc * x_bc - x_bc * x_bc / 4)
                    def a = 2 * l / x_bc
                    def h = 2 * l / y_bc
                    def binY = Math.floor((p.y + l) / (2 * l) * y_bc)
                    if (bins[k]['' + binY] == null) {
                        bins[k]['' + binY] = []
                    }
                    def triangleIndex
                    def partitionsX = x_bc * 2
                    def partition = Math.floor((p.x + l) / (2 * l) * partitionsX)
                    def xMax = a / 2
                    def yMax = h
                    def x = p.x + l - partition * a / 2
                    def y = p.y + l - binY * h
                    if (binY % 2 == 0 && partition % 2 == 1 || binY % 2 == 1 && partition % 2 == 0) {
                        if (y + yMax / xMax * x < yMax) {
                            triangleIndex = partition
                        } else {
                            triangleIndex = partition + 1
                        }
                    } else {
                        if (y > yMax / xMax * x) {
                            triangleIndex = partition
                        } else {
                            triangleIndex = partition + 1
                        }
                    }
                    if (bins[k][''+binY]['' + triangleIndex] == null) {
                        bins[k][''+binY]['' + triangleIndex] = []
                        for (def z = 0; z < this.objects.size(); z++) {
                            bins[k][''+binY]['' + triangleIndex].push([])
                        }
                        def r = Math.sqrt(3) / 6 * a
                        def x2 = (triangleIndex - 1) * a / 2 + a / 2 - l
                        def y2
                        if (binY % 2 == 0 && triangleIndex % 2 == 0 || binY % 2 == 1 && triangleIndex % 2 == 1) {
                            y2 = binY * h + h - r - l
                        } else {
                            y2 = binY * h + r - l
                        }
                        binData[k].push([
                            bin : bins[k][''+binY]['' + triangleIndex],
                            x : x2,
                            y : y2,
                            a : a,
                            r : r,
                            h : h,
                            binX : triangleIndex,
                            binY : binY
                        ])
                    }
                    bins[k][''+binY][''+triangleIndex][i].push(o)
                }
            }
        }

        def hexaBins = []
        def hexaBinData = []
        for (def k = 0; k < this.zoomLevels; k++) {
            hexaBins.push([])
            hexaBinData.push([])
        }

        for (def i = 0; i < binData.size(); i++) {
            for (def j = 0; j < binData[i].size(); j++) {
                def bin = binData[i][j]
                def binY = Math.floor(bin.binY / 2)
                def binX = Math.floor(bin.binX / 3)
                def x, y
                def a = bin.a
                def h = bin.h
                if (bin.binX % 6 < 3) {
                    if (hexaBins[i]['' + binY] == null) {
                        hexaBins[i]['' + binY] = []
                    }
                    y = binY * 2 * bin.h + bin.h - l
                    x = binX * 1.5 * bin.a + a / 2 - l
                } else {
                    if (bin.binY % 2 == 1) {
                        binY++
                    }
                    if (hexaBins[i]['' + binY] == null) {
                        hexaBins[i]['' + binY] = []
                    }
                    y = binY * 2 * bin.h - l
                    x = binX * 1.5 * bin.a + a / 2 - l
                }
                if (hexaBins[i][''+binY]['' + binX] == null) {
                    hexaBins[i][''+binY]['' + binX] = []
                    for (def z = 0; z < this.objects.size(); z++) {
                        hexaBins[i][''+binY]['' + binX].push([])
                    }
                    hexaBinData[i].push([
                        bin : hexaBins[i][''+binY]['' + binX],
                        x : x,
                        y : y,
                        a : bin.a,
                        r : bin.h
                    ])
                }
                for (def k = 0; k < bin.bin.size(); k++) {
                    for (def m = 0; m < bin.bin[k].size(); m++) {
                        hexaBins[i][''+binY][''+binX][k].push(bin.bin[k][m])
                    }
                }
            }
        }

        this.setCircleSet('hexagonal', hexaBinData)
    }

    String toString() {
        return "Binning[zoomLevels: "+zoomLevels+", minimumRadius: "+minimumRadius+", maximumRadius: "+maximumRadius+", maximumPoints: "+maximumPoints+", minArea: "+minArea+", maxArea: "+maxArea+", binnings: "+binnings.size()+", objects: "+objects[0].size()+"]"
    }
}
