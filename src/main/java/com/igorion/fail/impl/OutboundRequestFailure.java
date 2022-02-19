/**
 *  Copyright 2016 SmartBear Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.igorion.fail.impl;

import com.igorion.fail.EFailureCode;

@Deprecated
public class OutboundRequestFailure extends C19Failure {

    private static final long serialVersionUID = -4569414685838074701L;

    public OutboundRequestFailure(String message, Throwable cause) {
        super(EFailureCode.INVALID_GATEWAY, message, cause);
    }
}
