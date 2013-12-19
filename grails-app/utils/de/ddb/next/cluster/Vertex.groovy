package de.ddb.next.cluster

class Vertex {

    def x
    def y
    def radius
    def size
    def elements
    def radii
    def weights
    def legal
    def binning

    def Vertex(x, y, categories = null, binning = null) {
        this.x = x
        this.y = y
        this.radius
        this.size = 0
        this.elements = []
        this.radii = []
        this.weights = []
        this.legal = true
        this.binning = binning
        if (categories != null) {
            for (def i = 0; i < categories; i++) {
                this.elements.push([])
                this.weights.push(0)
            }
        }
    }


    def merge(v0, v1) {
        for (def i = 0; i < v0.elements.size(); i++) {
            //this.elements[i] = v0.elements[i].concat(v1.elements[i])
            def newElement = [] //TODO
            newElement.addAll(v0.elements[i])
            newElement.addAll(v1.elements[i])
            this.elements[i] = newElement
            this.weights[i] = v0.weights[i] + v1.weights[i]
            this.size += this.weights[i]
        }
    }

    def calculateRadius(resolution) {
        this.radii = []
        for (def i = 0; i<this.elements.size(); i++ ) {
            this.radii.push(this.binning.getRadius(this.weights[i]))
        }
        if (this.radii.size() == 1) {
            this.radius = this.radii[0] * resolution
        } else {
            def count = 0
            def max1 = 0
            def max2 = 0
            for (def i=0; i<this.radii.size(); i++ ) {
                if (this.radii[i] != 0) {
                    count++
                }
                if (this.radii[i] > max1) {
                    if (max1 > max2) {
                        max2 = max1
                    }
                    max1 = this.radii[i]
                } else if (this.radii[i] > max2) {
                    max2 = this.radii[i]
                }
            }
            if (count == 1) {
                this.radius = max1 * resolution
            } else if (count == 2) {
                this.radius = (max1 + max2) * resolution
            } else if (count == 3) {
                def d = (2 / 3 * Math.sqrt(3) - 1) * max1
                this.radius = (d + max1 + max2) * resolution
            } else if (count == 4) {
                def d = (Math.sqrt(2) - 1) * max2
                this.radius = (d + max1 + max2) * resolution
            }
        }
    }

    def addElement(e, weight, index) {
        this.elements[index].push(e)
        this.size += weight
        this.weights[index] += weight
    }

    String toString(){
        return "Vertex[x: "+x+", y: "+y+", radius: "+radius+", size: "+size+", elements: "+elements+", radii: "+radii+", weights: "+weights+", legal: "+legal+", binning: "+binning+"]"
    }
}
