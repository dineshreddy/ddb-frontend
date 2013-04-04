/*
 * Copyright (C) 2013 FIZ Karlsruhe
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

import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Taglib for I18N issues.
 * @author hla
 */
class I18NHelperTagLib {

    def messageSource

    /**
     * Prints out the currently selected language. The language itself is internationalized. The language must be 
     * available as entry in the message.property files with the format "ddbnext.language_<ISO2-language>".
     */
    def currentLanguage = { attrs, body ->
        def locale = SupportedLocales.getBestMatchingLocale(RequestContextUtils.getLocale(request))

        def localeLanguage = locale.getLanguage()
        def i18nLanguageString = messageSource.getMessage("ddbnext.language_"+localeLanguage, null, locale)

        out << i18nLanguageString
    }

    /**
     * Renders a language switching link dependend on the current url params, the given locale and the internationalized name.
     */
   def languageLink = {attrs, body ->
        def checkLocaleString = attrs.locale
        def localeclass = attrs.islocaleclass
        def locale = RequestContextUtils.getLocale(request)
        if(!locale){
            locale = SupportedLocales.getDefaultLocale()
        }

        boolean isLocale = false

        if(checkLocaleString && locale){
            def localeLanguage = locale.getLanguage()
            if(localeLanguage.equalsIgnoreCase(checkLocaleString)){
                isLocale = true
            }
        }

        if(isLocale){
            out << "<a class=\""+localeclass+"\">"+currentLanguage(attrs)+"</a>"
        }else{
            def linkUrl = createLink("url": attrs.params)
            def cleanedParams = attrs.params.clone()
            if(linkUrl.contains("staticcontent")){
                linkUrl = linkUrl.replaceAll("staticcontent", attrs.params?.dir)
                cleanedParams.remove("dir")
            }
            cleanedParams.remove("controller")
            cleanedParams.remove("action")
            cleanedParams.remove("id")
            cleanedParams.put("lang", checkLocaleString)
            def paramString = "?"
            cleanedParams.each {
                paramString += it.key + "=" + it.value + "&"
            }
            if(paramString.length() > 1){
                paramString = paramString.substring(0, paramString.length()-1)
            }
            out << "<a href=\""+linkUrl+paramString+"\" >"+body()+"</a>"
        }
    }
}
