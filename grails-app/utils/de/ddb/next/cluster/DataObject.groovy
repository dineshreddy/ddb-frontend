package de.ddb.next.cluster

class DataObject {

    def name
    def description
    def weight
    def tableContent
    def percentage = 0
    def locations
    def isGeospatial = false
    def placeDetails = []
    def dates
    def isTemporal = false
    def timeStart
    def timeEnd
    def index

    def DataObject(name, description, locations, dates, weight, tableContent) {
        this.name = name
        this.description = description
        this.weight = weight
        this.tableContent = tableContent
        this.locations = locations
        this.dates = dates

        if (this.locations.size() > 0) {
            this.isGeospatial = true
        }

        for (def i = 0; i < this.locations.size(); i++) {
            this.placeDetails.push(this.locations[i].place.split("/"))
        }

        if (this.dates.size() > 0) {
            this.isTemporal = true
        }
    }

    def setPercentage(percentage) {
        this.percentage = percentage
    }

    def getLatitude(locationId) {
        return this.locations[locationId].latitude
    }

    def getLongitude(locationId) {
        return this.locations[locationId].longitude
    }

    def getPlace(locationId, level) {
        if (level >= this.placeDetails[locationId].size()) {
            return this.placeDetails[locationId][this.placeDetails[locationId].size() - 1]
        }
        return this.placeDetails[locationId][level]
    }


    def getDate(dateId) {
        return this.dates[dateId].date
    }

    def getTimeGranularity(dateId) {
        return this.dates[dateId].granularity
    }

    def setIndex(index) {
        this.index = index
    }

    String toString() {
        return "DataObject[name: "+name+", weight: "+weight+", locations: "+locations+", index: "+index+", isGeospatial: "+isGeospatial+", description: "+description+"]"
    }
}
