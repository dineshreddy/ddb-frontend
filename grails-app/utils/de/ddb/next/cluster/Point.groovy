package de.ddb.next.cluster

import org.geotools.geometry.DirectPosition2D
import org.geotools.referencing.CRS
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.opengis.referencing.operation.MathTransform

class Point {

    def x
    def y

    def Point(lon, lat, something = null){
        if(lon instanceof String){
            this.x = lon.toDouble()
        }else{
            this.x = lon
        }
        if(lat instanceof String){
            this.y = lat.toDouble()
        }else{
            this.y = lat
        }
    }

    def transform(String fromProjection, String toProjection) {
        CoordinateReferenceSystem from = CRS.decode(fromProjection) // e.g. "EPSG:4326"
        CoordinateReferenceSystem to = CRS.decode(toProjection) // e.g. "EPSG:900913"

        MathTransform transform = CRS.findMathTransform(from, to)

        DirectPosition2D srcDirectPosition2D = new DirectPosition2D(from, x, y)
        DirectPosition2D destDirectPosition2D = new DirectPosition2D()
        transform.transform(srcDirectPosition2D, destDirectPosition2D)

        this.x = destDirectPosition2D.x
        this.y = destDirectPosition2D.y

    }

    String toString() {
        return "Point[x: "+x+", y: "+y+"]"
    }
}
