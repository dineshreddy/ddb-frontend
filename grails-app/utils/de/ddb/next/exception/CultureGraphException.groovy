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

package de.ddb.next.exception


/**
 * Exception indication that a call to the culturegraph service failed (maybe a 404 or 500 response).
 * 
 * @author hla
 */
class CultureGraphException extends Exception {

    public enum CultureGraphExceptionType {
        RESPONSE_404,
        RESPONSE_500
    }

    private CultureGraphExceptionType exceptionType = CultureGraphExceptionType.RESPONSE_500

    CultureGraphException(CultureGraphExceptionType exceptionType) {
        super()
        this.exceptionType = exceptionType
    }

    CultureGraphException(CultureGraphExceptionType exceptionType, String description) {
        super(description)
        this.exceptionType = exceptionType
    }

    public CultureGraphExceptionType getExceptionType() {
        return exceptionType
    }
}
