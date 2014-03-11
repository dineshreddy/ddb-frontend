package de.ddb.next

import java.text.DateFormat
import java.text.SimpleDateFormat


/**
 * All calculations are based on this formula
 * (<Wert> - 719164(Time from 0 to 01.01.1970 in Days)) * 86400000(Milliseconds of a day) = Time in Milliseconds since 01.01.1970
 */
class TimeFacetHelper {

    final static DateFormat dateFormat = new SimpleDateFormat("G-yyyy-MM-dd");

    final static def MILLISECONDS_DAY = 86400000;

    final static def DAYS_FROM_YEAR_0_TO_1970 = 719164;


    /**
     * Returns a Date instance for a given formatted string with the form <code>G-yyyy-MM-dd</code>
     * <ul>
     *   <li>G: is the era BC or AD</li>
     *   <li>y: a year jdigit</li>
     *   <li>M: a month digit</li>
     *   <li>d: a day digit</li>
     * </ul>
     * @param date the sting
     * @return a Date instance for a given formatted string
     */
    def static getDatefromFormattedString(String date) {
        def retVal = null
        if (date) {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            retVal =  dateFormat.parse(date);
        }

        return retVal
    }

    /**
     * Calculate the time facet Days representation for a given formatted string with the form <code>G-yyyy-MM-dd</code>
     * <ul>
     *   <li>G: is the era BC or AD</li>
     *   <li>y: a year jdigit</li>
     *   <li>M: a month digit</li>
     *   <li>d: a day digit</li>
     * </ul> 
     * @param dateString the string for which to calculate the days
     * @return the time facet Days representation for a given date
     */
    def static calculateDaysForTimeFacet(String dateString) {
        if (dateString) {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(dateString);

            return calculateDaysForTimeFacet(date);
        }

        return "";
    }

    /**
     * Returns a formated string for a given date
     * 
     * @param date the date to format
     * @return a formated string for a given date
     */
    def static formatDate(Date date) {
        if (date) {
            return dateFormat.format(date);
        }

        return "";
    }

    /**
     * Returns a formated string for a given time.
     * A Calendar instance is used to handle the right era (BC or AD) 
     *
     * @param date the time to format
     * @return a formated string for a given time
     */
    def static formatMillis(def millis) {
        Calendar cal = Calendar.getInstance()
        cal.setTimeInMillis(millis)

        return dateFormat.format(cal.getTime())
    }

    /**
     * Calculate the time facet Days representation for a given date
     * 
     * @param dateString the date instance for which to calculate the Days
     * @return the time facet Day representation for a given date
     */
    def static calculateDaysForTimeFacet(Date date) {
        def timeSince1970 = date.getTime()

        def days = (timeSince1970 / MILLISECONDS_DAY) + DAYS_FROM_YEAR_0_TO_1970

        return days;
    }

    /**
     * Calculate the time for a given time facet Day value
     * 
     * @param day the Day value to convert
     * @return the time for a given time facet Day value
     */
    def static calculateTimeFromTimeFacetDays(days) {
        def time = null

        if (days != null) {
            time = (days.toLong() - DAYS_FROM_YEAR_0_TO_1970) * MILLISECONDS_DAY
        }

        return time;
    }


    /**
     * Creates the time facet url parameter values 
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
