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

package de.ddb.next

import java.text.DateFormatSymbols

import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.next.constants.SupportedLocales


/**
 * gets the localized Months (for the TimeFacet)
 */
class GetMonthsLocalizedTagLib {

    static namespace = "ddb"

    def getJan = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[0]
    }
    def getFeb = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[1]
    }
    def getMar = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[2]
    }
    def getApr = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[3]
    }
    def getMay = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[4]
    }
    def getJun = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[5]
    }
    def getJul = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[6]
    }
    def getAug = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[7]
    }
    def getSep = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[8]
    }
    def getOct = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[9]
    }
    def getNov = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[10]
    }
    def getDec = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        out << new DateFormatSymbols(locale).getMonths()[11]
    }
}
