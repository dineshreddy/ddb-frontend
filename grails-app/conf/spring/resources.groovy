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

beans = {
    localeResolver(org.springframework.web.servlet.i18n.CookieLocaleResolver) {
        cookieMaxAge = 31536000 //1y

        // set default locale for JAWR plugin
        defaultLocale = new Locale("de","DE")
        java.util.Locale.setDefault(defaultLocale)
    }

    xmlns aop:"http://www.springframework.org/schema/aop"
    aspectBean(de.ddb.next.aop.SecurityInterceptor)
    aop.config("proxy-target-class":true) {
    }
}
