package de.ddb.next

import java.text.DateFormat
import java.text.SimpleDateFormat


/**
 * All calculations are based on this formula
 * (<Wert> - 719164(Time from 0 to 01.01.1970 in Days)) * 86400000(Milliseconds of a day) = Time in Milliseconds since 01.01.1970
 */
class TimeFacetHelper {

    final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    final static def MILLISECONDS_DAY = 86400000;

    final static def DAYS_FROM_YEAR_0_TO_1970 = 719164;


    def static getDatefromFormattedString(String date) {
        def retVal = null
        if (date) {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            retVal =  dateFormat.parse(date);
        }

        return retVal
    }


    def static calculateDaysForTimeFacet(String dateString) {
        if (dateString) {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(dateString);

            return calculateDaysForTimeFacet(date);
        }

        return "";
    }

    def static formatDate(Date date) {
        if (date) {
            return dateFormat.format(date);
        }

        return "";
    }

    /**
     * 
     * @param date
     * @return
     */
    def static calculateDaysForTimeFacet(Date date) {
        def timeSince1970 = date.getTime()

        def days = (timeSince1970 / MILLISECONDS_DAY) + DAYS_FROM_YEAR_0_TO_1970

        return days;
    }

    /**
     * 
     * @param days
     * @return
     */
    def static calculateTimeFromTimeFacetDays(days) {
        def time = null

        if (days) {
            time = (days.toLong() - DAYS_FROM_YEAR_0_TO_1970) * MILLISECONDS_DAY
            println "calculateTimeFromTimeFacetDays: " + time

            println new Date(time)
        }

        return time;
    }


    /**
     * Creates the time facet values 
     * 
     * @param exact if the time range should be exact
     * @return a list with time facet values
     */
    def static getTimeFacetValues(def dateFrom , def dateTill , boolean exact) {
        def retVal = []

        def daysFrom = '*';
        def daysTill = '*';

        if(dateFrom) {
            daysFrom = calculateDaysForTimeFacet(dateFrom);
        }

        if(dateTill) {
            daysTill = calculateDaysForTimeFacet(dateTill);
        }

        if(exact) {
            if(daysFrom != '*') {
                retVal.add('begin_time=[' + daysFrom + ' TO ' + daysTill + ']');
            }
            if(daysTill != '*') {
                retVal.add('end_time=[' + daysFrom + ' TO ' + daysTill + ']');
            }
        }else{
            //Unscharf
            if(daysTill != '*') {
                retVal.add('begin_time=[* TO '+ daysTill + ']');
            }
            if(daysFrom != '*') {
                retVal.add('end_time=[' + daysFrom + ' TO *]');
            }
        }

        return retVal;
    }

}
