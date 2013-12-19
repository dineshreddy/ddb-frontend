package de.ddb.next.cluster

class Triangle {

    def edges
    def descendants
    def vertices
    def radius_squared
    def center

    Triangle(edges) {
        this.edges = edges
        this.setVertices()
        this.descendants = []
    }

    def getTriple(e) {
        def i = arrayIndex(this.edges, e)
        return [
            e_s : this.edges[(i + 1) % 3],
            e_p : this.edges[(i + 2) % 3],
            u : this.vertices[(i + 2) % 3]
        ]
    }

    def leftOf(e) {
        def i = arrayIndex(this.edges, e)
        if (this.vertices[i].y != this.vertices[(i + 1) % 3].y) {
            return this.vertices[i].y > this.vertices[(i + 1) % 3].y
        }
        return this.vertices[i].y > this.vertices[(i + 2) % 3].y
    }

    def getNext(v) {
        def i = arrayIndex(this.vertices, v)
        return this.vertices[(i + 1) % 3]
    }

    def oppositeEdge(v) {
        def i = arrayIndex(this.vertices, v)
        return this.edges[(i + 1) % 3]
    }

    def contains(v) {
        return arrayIndex(this.vertices, v) != -1
    }

    def replace(e_old, e_new) {
        this.edges[arrayIndex(this.edges, e_old)] = e_new
    }

    def setVertices() {
        if (this.edges[1].v0 == this.edges[0].v0 || this.edges[1].v1 == this.edges[0].v0) {
            this.vertices = [
                this.edges[0].v1,
                this.edges[0].v0
            ]
        } else {
            this.vertices = [
                this.edges[0].v0,
                this.edges[0].v1
            ]
        }
        if (this.edges[2].v0 == this.vertices[0]) {
            this.vertices.push(this.edges[2].v1)
        } else {
            this.vertices.push(this.edges[2].v0)
        }
    }

    def replaceBy(triangles) {
        this.descendants = triangles
        this.edges[0].replaceFace(this, triangles[0])
        this.edges[1].replaceFace(this, triangles[1])
        this.edges[2].replaceFace(this, triangles[2])
    }

    def calcCircumcircle() {
        def v0 = this.vertices[0]
        def v1 = this.vertices[1]
        def v2 = this.vertices[2]
        def A = v1.x - v0.x
        def B = v1.y - v0.y
        def C = v2.x - v0.x
        def D = v2.y - v0.y
        def E = A * (v0.x + v1.x) + B * (v0.y + v1.y)
        def F = C * (v0.x + v2.x) + D * (v0.y + v2.y)
        def G = 2.0 * (A * (v2.y - v1.y) - B * (v2.x - v1.x))
        def cx = (D * E - B * F) / G
        def cy = (A * F - C * E) / G
        this.center = new Vertex(cx, cy)
        def dx = this.center.x - v0.x
        def dy = this.center.y - v0.y
        this.radius_squared = dx * dx + dy * dy
    }

    def inCircumcircle(v) {
        if (this.radius_squared == null) {
            this.calcCircumcircle()
        }
        def dx = this.center.x - v.x
        def dy = this.center.y - v.y
        def dist_squared = dx * dx + dy * dy
        return (dist_squared <= this.radius_squared )
    }

    def interior(v) {
        def v0 = this.vertices[0]
        def v1 = this.vertices[1]
        def v2 = this.vertices[2]
        def dotAB = (v.x - v0.x ) * (v0.y - v1.y ) + (v.y - v0.y ) * (v1.x - v0.x )
        def dotBC = (v.x - v1.x ) * (v1.y - v2.y ) + (v.y - v1.y ) * (v2.x - v1.x )
        def dotCA = (v.x - v2.x ) * (v2.y - v0.y ) + (v.y - v2.y ) * (v0.x - v2.x )
        if (dotAB > 0 || dotBC > 0 || dotCA > 0) {
            return null
        } else if (dotAB < 0 && dotBC < 0 && dotCA < 0) {
            return this
        } else if (dotAB == 0) {
            if (dotBC == 0) {
                return this.vertices[1]
            } else if (dotCA == 0) {
                return this.vertices[0]
            }
            return this.edges[0]
        } else if (dotBC == 0) {
            if (dotCA == 0) {
                return this.vertices[2]
            }
            return this.edges[1]
        } else if (dotCA == 0) {
            return this.edges[2]
        }
    }

    def arrayIndex(array, obj) {
        return array.indexOf(obj)
        //        for (def i = 0; i < array.length; i++) {
        //            if (array[i] == obj) {
        //                return i
        //            }
        //        }
        //        return -1
    }

    String toString() {
        //        def edges
        //        def descendants
        //        def vertices
        //        def radius_squared
        //        def center
        //
        return "Triangle[edges: "+edges.size()+", descendants: "+descendants+", vertices: "+vertices+", radius_squared: "+radius_squared+", center: "+center+"]"
    }
}
