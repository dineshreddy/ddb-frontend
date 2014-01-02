package de.ddb.next.cluster

class Edge {

    def v0
    def v1
    def leftFace
    def rightFace
    def legal
    double pLength
    double weight

    def Edge(v0, v1) {
        this.v0 = v0
        this.v1 = v1
        this.leftFace
        this.rightFace
        this.legal = true
        this.setLength()
    }

    def setLength() {
        double dx = this.v0.x - this.v1.x
        double dy = this.v0.y - this.v1.y
        this.pLength = Math.sqrt(dx * dx + dy * dy)
    }

    def contains(v) {
        if (this.v0 == v || this.v1 == v) {
            return true
        }
        return false
    }

    def replaceFace(f_old, f_new) {
        if (this.leftFace == f_old) {
            this.leftFace = f_new
        } else if (this.rightFace == f_old) {
            this.rightFace = f_new
        }
    }

    def setFace(f) {
        if (f.leftOf(this)) {
            this.leftFace = f
        } else {
            this.rightFace = f
        }
    }

    def setFaces(f1, f2) {
        if (f1.leftOf(this)) {
            this.leftFace = f1
            this.rightFace = f2
        } else {
            this.leftFace = f2
            this.rightFace = f1
        }
    }

    def removeFace(f) {
        if (this.leftFace == f) {
            this.leftFace = null
        } else {
            this.rightFace = null
        }
    }

    boolean equals(e) {
        if (this.v0 == e.v0 && this.v1 == e.v1 || this.v0 == e.v1 && this.v1 == e.v0) {
            return true
        }
        return false
    }

    String toString(){
        return "Edge[v0: "+v0+", v1: "+v1+", leftFace: "+leftFace+", rightFace: "+rightFace+", legal: "+legal+", pLength: "+pLength+", weight: "+weight+"]"
    }
}
