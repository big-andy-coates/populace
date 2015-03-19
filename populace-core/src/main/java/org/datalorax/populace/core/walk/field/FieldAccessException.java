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

package org.datalorax.populace.core.walk.field;

import org.datalorax.populace.core.walk.WalkerException;

/**
 * @author Andrew Coates - 04/03/2015.
 */
public class FieldAccessException extends WalkerException {
    public FieldAccessException(final RawField field, final PathProvider path, final Throwable cause) {
        super(String.format("Failed to access field: " + field), path, cause);
    }
}
