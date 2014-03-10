package ddb.next.utils

import grails.test.mixin.*

import java.text.DateFormat
import java.text.SimpleDateFormat

import de.ddb.next.TimeFacetHelper


class TimeFacetHelperTest extends GroovyTestCase {

    DateFormat dateFormat = new SimpleDateFormat("G-yyyy-MM-dd");

    void setUp() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    void testMillisecondsFor_1_1_1970() {
        Date date = dateFormat.parse("AD-1970-01-01");
        assert 0 == date.getTime()
    }

    void testCalculateDaysForTimeFacetFor1_1_1970() {
        Date date = dateFormat.parse("AD-1970-01-01");

        def days = TimeFacetHelper.calculateDaysForTimeFacet(date)

        assert TimeFacetHelper.DAYS_FROM_YEAR_0_TO_1970 == days
    }

    void testCalculateDaysForTimeFacetFor1_1_1643() {
        Date date = dateFormat.parse("AD-1643-01-01");

        def days = TimeFacetHelper.calculateDaysForTimeFacet(date)

        assert  599730 == days
    }

    void testCalculateDaysForTimeFacetFor29_10_1268() {
        Date date = dateFormat.parse("AD-1268-10-29");

        def days = TimeFacetHelper.calculateDaysForTimeFacet(date)

        assert  463073 == days
    }

    void testCalculateDaysForTimeFacetFor01_01_3500BC() {
        Date date = dateFormat.parse("BC-3500-01-01");

        def days = TimeFacetHelper.calculateDaysForTimeFacet(date)

        assert  -1278375 == days
    }

    void testCalculateDaysForTimeFacetFor01_01_3000BC() {
        Date date = dateFormat.parse("BC-3000-01-01");

        def days = TimeFacetHelper.calculateDaysForTimeFacet(date)

        assert  -1095750 == days
    }

    void testCalculateDaysForTimeFacetFor01_01_475BC() {
        Date date = dateFormat.parse("BC-475-01-01");

        def days = TimeFacetHelper.calculateDaysForTimeFacet(date)

        assert  -173494 == days
    }



    void testCalculateTimeFromTimeFacetDaysFor_0_Days() {
        def time = TimeFacetHelper.calculateTimeFromTimeFacetDays(0)

        Calendar cal = Calendar.getInstance()
        cal.setTimeInMillis(time)
        dateFormat.format(cal.getTime())
        assert "AD-0001-01-01" ==  dateFormat.format(cal.getTime())
    }


    void testCalculateTimeFromTimeFacetDateFor_MINUS1095750_Days() {
        def time = TimeFacetHelper.calculateTimeFromTimeFacetDays(-1095750)
        Calendar cal = Calendar.getInstance()
        cal.setTimeInMillis(time)
        dateFormat.format(cal.getTime())
        assert "BC-3000-01-01" ==  dateFormat.format(cal.getTime())
    }

    void testCalculateTimeFromTimeFacetDateFor_MINUS1278375_Days() {
        def time = TimeFacetHelper.calculateTimeFromTimeFacetDays(-1278375)
        Calendar cal = Calendar.getInstance()
        cal.setTimeInMillis(time)
        dateFormat.format(cal.getTime())
        assert "BC-3500-01-01" ==  dateFormat.format(cal.getTime())
    }



    void testGetFacetValuesWithOneDate() {
        Date date = dateFormat.parse("AD-1970-01-01")

        // Test 1 no dates -> no values!
        def facetValues = TimeFacetHelper.getTimeFacetValues(null, null, false)
        assert facetValues.size() == 0

        // Test 2 Only from date and exact
        facetValues = TimeFacetHelper.getTimeFacetValues(date, null, true)
        assert facetValues.size() == 1
        assert facetValues.contains("begin_time=[719164 TO *]")

        // Test 2 Only from date and fuzzy
        facetValues = TimeFacetHelper.getTimeFacetValues(date, null, false)
        assert facetValues.size() == 1
        assert facetValues.contains("end_time=[719164 TO *]")

        // Test 3 Only till date and exact
        facetValues = TimeFacetHelper.getTimeFacetValues(null, date, true)
        assert facetValues.size() == 1
        assert facetValues.contains("end_time=[* TO 719164]")

        // Test 4 Only till date and fuzzy
        facetValues = TimeFacetHelper.getTimeFacetValues(null, date, false)
        assert facetValues.size() == 1
        assert facetValues.contains("begin_time=[* TO 719164]")
    }

    void testGetFacetValuesWithTwoDates() {
        Date date1 = dateFormat.parse("AD-1800-01-01")
        Date date2 = dateFormat.parse("AD-2012-12-31")

        // Test from and till date exact
        def facetValues = TimeFacetHelper.getTimeFacetValues(date1, date2, true)
        assert facetValues.size() == 2
        assert facetValues.contains("begin_time=[657073 TO 734869]")
        assert facetValues.contains("end_time=[657073 TO 734869]")
    }
}