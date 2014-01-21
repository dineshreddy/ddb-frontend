/*
 * Copyright (C) 2014 FIZ Karlsruhe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ddb.next.cluster

import org.geotools.geometry.DirectPosition2D
import org.geotools.referencing.CRS
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.opengis.referencing.operation.MathTransform

class Point {

    double x
    double y

    def Point(lat, lon, something = null){
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
