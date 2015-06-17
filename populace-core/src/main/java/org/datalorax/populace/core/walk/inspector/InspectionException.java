/*
 * Copyright (c) 2015 Andrew Coates
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

package org.datalorax.populace.core.walk.inspector;

/**
 * Thrown by {@link org.datalorax.populace.core.walk.inspector.Inspector inspectors} if there was a problem inspecting
 *
 * @author Andrew Coates - 12/06/2015.
 */
public class InspectionException extends RuntimeException {
    public InspectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InspectionException(final Throwable cause) {
        super(cause);
    }

    public InspectionException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InspectionException() {
    }

    public InspectionException(final String message) {
        super(message);
    }
}
