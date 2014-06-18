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

import de.ddb.common.constants.EntityFacetEnum
import de.ddb.common.constants.FacetEnum


class SearchFacetLists {

    public static final List<FacetEnum> itemSearchNonJavascriptFacetList = [
        FacetEnum.PLACE.getName(),
        FacetEnum.AFFILIATE.getName(),
        FacetEnum.KEYWORDS.getName(),
        FacetEnum.LANGUAGE.getName(),
        FacetEnum.TYPE.getName(),
        FacetEnum.SECTOR.getName(),
        FacetEnum.PROVIDER.getName()
    ]


    public static final List<FacetEnum> itemSearchJavascriptFacetList = [
        FacetEnum.PLACE.getName(),
        FacetEnum.AFFILIATE_ROLE.getName(),
        FacetEnum.KEYWORDS.getName(),
        FacetEnum.LANGUAGE.getName(),
        FacetEnum.LICENSE.getName(),
        FacetEnum.LICENSE_GROUP.getName(),
        FacetEnum.TYPE.getName(),
        FacetEnum.SECTOR.getName(),
        FacetEnum.PROVIDER.getName()
    ]

    public static final List<FacetEnum> institutionSearchNonJavascriptFacetList = [
        FacetEnum.SECTOR.getName(),
        FacetEnum.STATE.getName()
    ]

    public static final List<FacetEnum> institutionSearchJavascriptFacetList = [
        FacetEnum.SECTOR.getName(),
        FacetEnum.STATE.getName()
    ]


    public static final List<FacetEnum> entitySearchNonJavascriptFacetList = [
        EntityFacetEnum.PERSON_OCCUPATION.getName(),
        EntityFacetEnum.PERSON_PLACE.getName(),
        EntityFacetEnum.PERSON_GENDER.getName()
    ]

    public static final List<FacetEnum> entitySearchJavascriptFacetList = [
        EntityFacetEnum.PERSON_OCCUPATION.getName(),
        EntityFacetEnum.PERSON_PLACE.getName(),
        EntityFacetEnum.PERSON_GENDER.getName()
    ]
}
