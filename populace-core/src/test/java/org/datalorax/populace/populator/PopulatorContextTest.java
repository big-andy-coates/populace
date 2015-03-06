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

package org.datalorax.populace.populator;

import org.datalorax.populace.populator.instance.InstanceFactories;
import org.datalorax.populace.populator.mutator.Mutators;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Field;

public class PopulatorContextTest {
    @Mock
    private Mutators mutators;
    @Mock
    private InstanceFactories instanceFactories;
    private Field field;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        field = getClass().getDeclaredField("field");

        config = new PopulatorContext(mutators, instanceFactories);
    }

    // Todo(ac): test
}