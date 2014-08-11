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
        FacetEnum.PLACE_FCT.getName(),
        FacetEnum.AFFILIATE_FCT.getName(),
        FacetEnum.KEYWORDS_FCT.getName(),
        FacetEnum.LANGUAGE_FCT.getName(),
        FacetEnum.TYPE_FCT.getName(),
        FacetEnum.SECTOR_FCT.getName(),
        FacetEnum.PROVIDER_FCT.getName()
    ]


    public static final List<FacetEnum> itemSearchJavascriptFacetList = [
        FacetEnum.PLACE_FCT.getName(),
        FacetEnum.AFFILIATE_FCT_ROLE.getName(),
        FacetEnum.KEYWORDS_FCT.getName(),
        FacetEnum.LANGUAGE_FCT.getName(),
        FacetEnum.LICENSE.getName(),
        FacetEnum.LICENSE_GROUP.getName(),
        FacetEnum.TYPE_FCT.getName(),
        FacetEnum.SECTOR_FCT.getName(),
        FacetEnum.PROVIDER_FCT.getName()
    ]

    public static final List<FacetEnum> institutionSearchNonJavascriptFacetList = [
        FacetEnum.SECTOR_FCT.getName(),
        FacetEnum.STATE_FCT.getName()
    ]

    public static final List<FacetEnum> institutionSearchJavascriptFacetList = [
        FacetEnum.SECTOR_FCT.getName(),
        FacetEnum.STATE_FCT.getName()
    ]


    public static final List<FacetEnum> entitySearchNonJavascriptFacetList = [
        EntityFacetEnum.PERSON_OCCUPATION_FCT.getName(),
        EntityFacetEnum.PERSON_PLACE_FCT.getName(),
        EntityFacetEnum.PERSON_GENDER_FCT.getName()
    ]

    public static final List<FacetEnum> entitySearchJavascriptFacetList = [
        EntityFacetEnum.PERSON_OCCUPATION_FCT.getName(),
        EntityFacetEnum.PERSON_PLACE_FCT.getName(),
        EntityFacetEnum.PERSON_GENDER_FCT.getName()
    ]
}
