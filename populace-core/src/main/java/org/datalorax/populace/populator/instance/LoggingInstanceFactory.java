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

package org.datalorax.populace.populator.instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A {@link org.datalorax.populace.populator.instance.NullObjectStrategy} that logs the type and returns {@code null}.
 *
 * @author Andrew Coates - 02/03/2015.
 */
public class LoggingInstanceFactory implements NullObjectStrategy {
    public static final LoggingInstanceFactory INSTANCE = new LoggingInstanceFactory();
    private static final Log LOG = LogFactory.getLog(LoggingInstanceFactory.class);

    @Override
    public void onNullObject(final Object parent) {
        LOG.warn("A null value was encountered for which there existed no type information to use to instantiate a new " +
            "instance. parent instance: " + parent);
    }

    @Override
    public boolean equals(final Object that) {
        return this == that || (that != null && getClass() == that.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
