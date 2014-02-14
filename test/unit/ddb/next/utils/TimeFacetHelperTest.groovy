package ddb.next.utils

import java.text.DateFormat
import java.text.SimpleDateFormat

import de.ddb.next.TimeFacetHelper


class TimeFacetHelperTest extends GroovyTestCase {

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    void setUp() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    void testMillisecondsFor_1_1_1970() {
        Date date = dateFormat.parse("1970-01-01");
        assert 0 == date.getTime()
    }

    void testCalculateDaysForTimeFacetFor1_1_1970() {
        Date date = dateFormat.parse("1970-01-01");

        def days = TimeFacetHelper.calculateDaysForTimeFacet(date)

        assert TimeFacetHelper.DAYS_FROM_YEAR_0_TO_1970 == days
    }

    void testCalculateDaysForTimeFacetFor1_1_1643() {
        Date date = dateFormat.parse("1643-01-01");

        def days = TimeFacetHelper.calculateDaysForTimeFacet(date)

        assert  599730 == days
    }

    void testCalculateTimeFromTimeFacetDaysFor_1_1_1970() {
        def time = TimeFacetHelper.calculateTimeFromTimeFacetDays(719164)

        assert 0 == time
    }
}