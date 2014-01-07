package de.ddb.next.cluster


class Clustering {

    def triangles
    def newTriangles
    def bbox
    def edges
    def vertices
    def legalizes
    def collapses
    def boundingTriangle
    def deleteEdges

    def Clustering(xMin, yMin, xMax, yMax) {
        this.triangles = []
        this.newTriangles = []
        this.bbox = [
            x1 : xMin,
            y1 : yMin,
            x2 : xMax,
            y2 : yMax
        ]
        this.createBoundingTriangle()
        this.edges = []
        this.vertices = []
        this.legalizes = 0
        this.collapses = 0
    }

    def locate(v) {
        if (this.boundingTriangle.descendants.size() == 0) {
            return this.boundingTriangle
        }
        def triangles = this.boundingTriangle.descendants
        while (true) {
            for (def i = 0; i < triangles.size(); i++) {
                def simplex = triangles[i].interior(v)
                if (simplex == null) {
                    continue
                }
                if (simplex instanceof Vertex || this.isLeaf(triangles[i])) {
                    return simplex
                }
                triangles = triangles[i].descendants
                break
            }
        }
    }

    def legalize(v, e, t0_old) {
        if (!e.v0.legal && !e.v1.legal) {
            return
        }
        this.legalizes++
        def flip = false
        def t1_old
        if (e.leftFace == t0_old && e.rightFace.inCircumcircle(v)) {
            flip = true
            t1_old = e.rightFace
        } else if (e.rightFace == t0_old && e.leftFace.inCircumcircle(v)) {
            flip = true
            t1_old = e.leftFace
        }
        if (flip) {
            def tr0 = t0_old.getTriple(e)
            def tr1 = t1_old.getTriple(e)
            def e_flip = new Edge(tr0.u, tr1.u)
            def poly = []
            poly.push(e.v0)
            poly.push(e_flip.v0)
            poly.push(e.v1)
            poly.push(e_flip.v1)
            if (!this.jordanTest(poly, e_flip)) {
                return
            }
            e.legal = false
            this.edges.push(e_flip)
            def t0_new = new Triangle([e_flip, tr0.e_p, tr1.e_s])
            def t1_new = new Triangle([e_flip, tr1.e_p, tr0.e_s])
            e_flip.setFaces(t0_new, t1_new)
            tr0.e_p.replaceFace(t0_old, t0_new)
            tr1.e_s.replaceFace(t1_old, t0_new)
            tr1.e_p.replaceFace(t1_old, t1_new)
            tr0.e_s.replaceFace(t0_old, t1_new)
            t0_old.descendants = [t0_new, t1_new]
            t1_old.descendants = [t0_new, t1_new]
            this.legalize(v, t0_new.edges[2], t0_new)
            this.legalize(v, t1_new.edges[1], t1_new)
        }
    }

    def add(v) {
        def located = this.locate(v)
        this.addVertex(v, located)
    }

    def addVertex(v, simplex) {
        if ( simplex instanceof Vertex) {
            simplex.merge(simplex, v)
        } else if ( simplex instanceof Edge) {
            this.vertices.push(v)
            simplex.legal = false
            def tr0 = simplex.leftFace.getTriple(simplex)
            def tr1 = simplex.rightFace.getTriple(simplex)
            def e0 = new Edge(v, tr0.u)
            def e1 = new Edge(v, simplex.leftFace.getNext(tr0.u))
            def e2 = new Edge(v, tr1.u)
            def e3 = new Edge(v, simplex.rightFace.getNext(tr1.u))
            def t0 = new Triangle([e0, tr0.e_p, e1])
            def t1 = new Triangle([e1, tr1.e_s, e2])
            def t2 = new Triangle([e2, tr1.e_p, e3])
            def t3 = new Triangle([e3, tr0.e_s, e0])
            simplex.leftFace.descendants = [t0, t3]
            simplex.rightFace.descendants = [t1, t2]
            this.edges.push(e0)
            this.edges.push(e1)
            this.edges.push(e2)
            this.edges.push(e3)
            e0.setFaces(t0, t3)
            e1.setFaces(t0, t1)
            e2.setFaces(t1, t2)
            e3.setFaces(t2, t3)
            tr0.e_p.replaceFace(simplex.leftFace, t0)
            tr1.e_s.replaceFace(simplex.rightFace, t1)
            tr1.e_p.replaceFace(simplex.rightFace, t2)
            tr0.e_s.replaceFace(simplex.leftFace, t3)
            this.legalize(v, tr0.e_p, t0)
            this.legalize(v, tr1.e_s, t1)
            this.legalize(v, tr1.e_p, t2)
            this.legalize(v, tr0.e_s, t3)
        } else {
            this.vertices.push(v)
            def e_i = new Edge(simplex.vertices[0], v)
            def e_j = new Edge(simplex.vertices[1], v)
            def e_k = new Edge(simplex.vertices[2], v)
            this.edges.push(e_i)
            this.edges.push(e_j)
            this.edges.push(e_k)
            def t0 = new Triangle([e_i, simplex.edges[0], e_j])
            def t1 = new Triangle([e_j, simplex.edges[1], e_k])
            def t2 = new Triangle([e_k, simplex.edges[2], e_i])
            e_i.setFaces(t0, t2)
            e_j.setFaces(t0, t1)
            e_k.setFaces(t1, t2)
            simplex.replaceBy([t0, t1, t2])
            this.legalize(v, simplex.edges[0], t0)
            this.legalize(v, simplex.edges[1], t1)
            this.legalize(v, simplex.edges[2], t2)
        }
    }

    def isLeaf(t) {
        return t.descendants.size() == 0
    }

    def createBoundingTriangle() {
        def dx = (this.bbox.x2 - this.bbox.x1 ) * 10
        def dy = (this.bbox.y2 - this.bbox.y1 ) * 10
        def v0 = new Vertex(this.bbox.x1 - dx, this.bbox.y1 - dy * 3)
        def v1 = new Vertex(this.bbox.x2 + dx * 3, this.bbox.y2 + dy)
        def v2 = new Vertex(this.bbox.x1 - dx, this.bbox.y2 + dy)
        def e0 = new Edge(v1, v0)
        def e1 = new Edge(v0, v2)
        def e2 = new Edge(v2, v1)
        v0.legal = false
        v1.legal = false
        v2.legal = false
        this.boundingTriangle = new Triangle([e0, e1, e2])
        def inf = new Triangle([e0, e1, e2])
        e0.setFaces(this.boundingTriangle, inf)
        e1.setFaces(this.boundingTriangle, inf)
        e2.setFaces(this.boundingTriangle, inf)
    }

    def traverse(eLeft, eRight, triangle, oldFacets, hole, vertices) {
        eLeft.legal = false
        while(true) {
            def triple
            if (eLeft.leftFace == triangle) {
                triple = eLeft.rightFace.getTriple(eLeft)
                oldFacets.push(eLeft.rightFace)
                triple.e_s.removeFace(eLeft.rightFace)
                triangle = eLeft.rightFace
            } else {
                triple = eLeft.leftFace.getTriple(eLeft)
                oldFacets.push(eLeft.leftFace)
                triple.e_s.removeFace(eLeft.leftFace)
                triangle = eLeft.leftFace
            }
            if (arrayIndex(hole, triple.e_s) == -1) {
                hole.push(triple.e_s)
            }
            vertices.push(triple.u)
            eLeft = triple.e_p
            eLeft.legal = false

            if(eLeft == eRight) {
                break
            }
        }
    }

    def isBoundary(e, hole) {
        for (def i = 0; i < hole.size(); i++) {
            if (hole[i].equals(e)) {
                return i
            }
        }
        return -1
    }


    def mergeVertices(e, resolution) {
        this.collapses = this.collapses + 1
        def s0 = e.v0.size
        def s1 = e.v1.size
        def x = (e.v0.x * s0 + e.v1.x * s1 ) / (s0 + s1 )
        def y = (e.v0.y * s0 + e.v1.y * s1 ) / (s0 + s1 )
        def v = new Vertex(x, y, e.v0.elements.size(), e.v0.binning)
        v.merge(e.v0, e.v1)

        e.v0.legal = false
        e.v1.legal = false

        def hole = []
        def oldFacets = []
        e.legal = false

        def vertices = []
        def tr0 = e.leftFace.getTriple(e)
        def tr1 = e.rightFace.getTriple(e)
        oldFacets.push(e.leftFace)
        oldFacets.push(e.rightFace)
        traverse(tr0.e_p, tr1.e_s, e.leftFace, oldFacets, hole, vertices)
        traverse(tr1.e_p, tr0.e_s, e.rightFace, oldFacets, hole, vertices)

        def hd = new Clustering(this.bbox.x1 - 10, this.bbox.y1 - 10, this.bbox.x2 + 10, this.bbox.y2 + 10)
        def hull = []
        for (def i = 0; i<hole.size(); i++ ) {
            if (!(hole[i].leftFace == null && hole[i].rightFace == null)) {
                hull.push(hole[i].v0)
                hull.push(hole[i].v1)
            }
        }
        def hullVertices = []
        def distinct = []
        for (def i=0; i<vertices.size(); i++ ) {
            if (arrayIndex(distinct, vertices[i]) == -1) {
                hd.add(vertices[i])
                distinct.push(vertices[i])
            }
            if (arrayIndex(hull, vertices[i]) != -1) {
                hullVertices.push(vertices[i])
            }
        }

        def newFacets = []
        //        def isBoundary = function(e) {
        //            for (def i = 0; i < hole.size(); i++) {
        //                if (hole[i].equals(e)) {
        //                    return i
        //                }
        //            }
        //            return -1
        //
        //    }
        //def holeEdges = new Object[hole.size()]
        //def holeEdges = []
        def holeEdges = [:]
        def nonHoleEdges = []

        for (def i = 0; i < hd.edges.size(); i++) {
            def e2 = hd.edges[i]
            def b = isBoundary(e2, hole)
            if (b != -1) {
                if (!e2.legal) {
                    def t1 = e2.leftFace.getTriple(e2)
                    def t2 = e2.rightFace.getTriple(e2)
                    def edge = new Edge(t1.u, t2.u)
                    for (def j = 0; j < hd.edges.size(); j++) {
                        if (hd.edges[j].equals(edge) && hd.edges[j].legal) {
                            hd.edges[j].legal = false
                            break
                        }
                    }
                    t1.e_p.setFace(e2.leftFace)
                    t1.e_s.setFace(e2.leftFace)
                    t2.e_p.setFace(e2.rightFace)
                    t2.e_s.setFace(e2.rightFace)

                    e2.legal = true
                }
                holeEdges[b] = e2
            } else {
                nonHoleEdges.push(e2)
            }
        }


        //for (def i = 0; i < holeEdges.size(); i++) {
        def holeEdgesKeys = holeEdges.keySet()
        for (def i in holeEdgesKeys) {
            def e2 = holeEdges[i]
            if (hole[i].leftFace == null) {
                hole[i].leftFace = e2.leftFace
                hole[i].leftFace.replace(e2, hole[i])
                if (arrayIndex(newFacets, hole[i].leftFace) == -1) {
                    newFacets.push(hole[i].leftFace)
                }
            }
            if (hole[i].rightFace == null) {
                hole[i].rightFace = e2.rightFace
                hole[i].rightFace.replace(e2, hole[i])
                if (arrayIndex(newFacets, hole[i].rightFace) == -1) {
                    newFacets.push(hole[i].rightFace)
                }
            }
        }

        for (def i = 0; i < nonHoleEdges.size(); i++) {
            def e2 = nonHoleEdges[i]
            if (!e2.legal) {
                continue
            }
            if (this.jordanTest(hullVertices, e2)) {
                this.edges.push(e2)
                if (arrayIndex(newFacets, e2.rightFace) == -1) {
                    newFacets.push(e2.rightFace)
                }
                if (arrayIndex(newFacets, e2.leftFace) == -1) {
                    newFacets.push(e2.leftFace)
                }
            }
        }

        for (def i = 0; i<oldFacets.size(); i++ ) {
            oldFacets[i].descendants = newFacets
        }

        for (def i = 0; i < newFacets.size(); i++) {
            def simplex = newFacets[i].interior(v)
            if (simplex == null) {
                continue
            } else {
                this.addVertex(v, simplex)
                break
            }
        }

        return v
    }

    def jordanTest(pol, e) {
        def p = new Vertex((e.v0.x + e.v1.x) * 0.5, (e.v0.y + e.v1.y) * 0.5)
        def inside = false
        def i
        def j = pol.size() - 1
        for ( i = 0; i < pol.size(); j = i++) {
            def p1 = pol[i]
            def p2 = pol[j]
            if ((((p1.y <= p.y) && (p.y < p2.y)) || ((p2.y <= p.y) && (p.y < p1.y))) && (p.x < (p2.x - p1.x) * (p.y - p1.y) / (p2.y - p1.y) + p1.x))
                inside = !inside
        }
        return inside
    }

    def mergeForResolution(resolution, circleGap) {
        this.deleteEdges = new BinaryHeap()
        this.weightEdges(resolution, circleGap)

        def index = 0
        while (this.deleteEdges.size() > 0) {
            def e = this.deleteEdges.pop()
            if (e.legal) {
                def l = this.edges.size()
                def newVertex = this.mergeVertices(e, resolution)
                newVertex.calculateRadius(resolution)
                for (def k = l; k < this.edges.size(); k++) {
                    def eNew = this.edges[k]
                    if (eNew.legal) {
                        eNew.weight = 99999
                        if(eNew.pLength != null && eNew.v0.radius != null && eNew.v1.radius != null && circleGap != null && resolution != null){
                            eNew.weight = eNew.pLength / (eNew.v0.radius + eNew.v1.radius + circleGap * resolution )
                        }
                        if (eNew.weight != null && eNew.weight < 1) {
                            this.deleteEdges.push(eNew)
                        }
                    }
                }
            }
        }
    }

    def weightEdges(resolution, circleGap) {
        for (def i = 0; i < this.vertices.size(); i++) {
            if (this.vertices[i].legal) {
                this.vertices[i].calculateRadius(resolution)
            }
        }
        def newEdges = []
        for (def i = 0; i < this.edges.size(); i++) {
            def e = this.edges[i]
            if (e.legal) {
                if (!e.v0.legal || !e.v1.legal) {
                    e.weight = 1
                } else {
                    e.weight = e.pLength / (e.v0.radius + e.v1.radius + circleGap * resolution )
                    if(e.weight == 0){ // TODO? Added this line to fix different behaviour from JS
                        e.weight = 1
                    }
                    if (e.weight < 1) {
                        this.deleteEdges.push(e)
                    }
                }
                newEdges.push(e)
            }
        }
        this.edges = newEdges
    }

    def leftOf(v1, v2, v) {
        def x2 = v1.x - v2.x
        def x3 = v1.x - v.x
        def y2 = v1.y - v2.y
        def y3 = v1.y - v.y
        if (x2 * y3 - y2 * x3 < 0) {
            return true
        }
        return false
    }

    def validityTest() {

        //console.info("Test 2: Edges Facets (null) ...");
        for (def i=0; i<this.edges.size();i++ ) {
            def e = this.edges[i]
            if (e.leftFace == null || e.rightFace == null) {
                //console.info(e);
                //alert();
            }
        }

        def c = 0
        for (def i=0; i<this.edges.size();i++ ) {
            def e = this.edges[i]
            def t1 = e.leftFace.getTriple(e)
            def t2 = e.rightFace.getTriple(e)
            if (e.v0.y == e.v1.y) {
                if (t1.u.y > t2.u.y) {
                    //console.info("equal y conflict ...");
                    //console.info(e);
                    //alert();
                    c++
                }
            } else {
                def v1, v2
                if (e.v0.y > e.v1.y) {
                    v1 = e.v0
                    v2 = e.v1
                } else {
                    v1 = e.v1
                    v2 = e.v0
                }
                if (!leftOf(v1, v2, t1.u)) {
                    //console.info("left right conflict ... left is right");
                    //console.info(e);
                    //alert();
                    c++
                }
                if (leftOf(v1, v2, t2.u)) {
                    //console.info("left right conflict ... right is left");
                    //console.info(e);
                    //alert();
                    c++
                }
            }
        }

        for (def i=0; i<this.edges.size(); i++ ) {
            if (this.edges[i].legal) {
                def e = this.edges[i]
                def tr0 = e.leftFace.getTriple(e)
                def tr1 = e.rightFace.getTriple(e)
                if (!tr0.e_p.legal || !tr0.e_s.legal || !tr1.e_p.legal || !tr1.e_s.legal) {
                    return
                }
            }
        }

    }

    def arrayIndex(array, obj) {
        return array.indexOf(obj)
        //        for (def i = 0; i < array.size(); i++) {
        //            if (array[i] == obj) {
        //                return i
        //            }
        //        }
        //        return -1
    }

    public String toString() {
        return "Clustering[triangles: "+triangles+", edges: "+edges+", vertices: "+vertices+", legalizes: "+legalizes+", bbox: "+bbox+", collapses: "+collapses+", newTriangles: "+newTriangles+", boundingTriangle: "+boundingTriangle+", deleteEdges: "+deleteEdges+"]"
    }

}
