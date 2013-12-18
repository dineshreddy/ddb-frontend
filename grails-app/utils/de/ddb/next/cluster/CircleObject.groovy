package de.ddb.next.cluster

class CircleObject {

    def originX
    def originY
    def shiftX
    def shiftY
    def elements
    def radius
    def search
    def weight
    def overlay
    def smoothness
    def fatherBin
    def feature
    def olFeature
    def percentage
    def selected

    def CircleObject(originX, originY, shiftX, shiftY, elements, radius, search, weight, fatherBin) {
        this.originX = originX
        this.originY = originY
        this.shiftX = shiftX
        this.shiftY = shiftY
        this.elements = elements
        this.radius = radius
        this.search = search
        this.weight = weight
        this.overlay = 0
        this.smoothness = 0
        this.fatherBin = fatherBin
        this.percentage = 0
        this.selected = false
    }


    /**
     * sets the OpenLayers point feature for this point object
     * @param {OpenLayers.Feature} pointFeature the point feature for this object
     */
    def setFeature(feature) {
        this.feature = feature
    }

    /**
     * sets the OpenLayers point feature for this point object to manage its selection status
     * @param {OpenLayers.Feature} olPointFeature the overlay point feature for this object
     */
    def setOlFeature(olFeature) {
        this.olFeature = olFeature
    }

    def reset() {
        this.overlay = 0
        this.smoothness = 0
    }

    def setSelection(s) {
        this.selected = s
    }

    def toggleSelection() {
        this.selected = !this.selected
    }

    String toString() {
        return "CircleObject[originX: "+originX+", originY: "+originY+", shiftX: "+shiftX+", shiftY: "+shiftY+", radius: "+radius+", elements: "+elements+"]"
    }
}
