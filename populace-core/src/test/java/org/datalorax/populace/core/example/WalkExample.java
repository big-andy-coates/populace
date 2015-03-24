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

package org.datalorax.populace.core.example;

import org.datalorax.populace.core.example.domain.Contacts;
import org.datalorax.populace.core.populate.GraphPopulator;
import org.datalorax.populace.core.walk.GraphWalker;
import org.datalorax.populace.core.walk.visitor.FieldVisitor;
import org.datalorax.populace.core.walk.visitor.FieldVisitors;
import org.datalorax.populace.core.walk.visitor.SetAccessibleFieldVisitor;
import org.testng.annotations.Test;

/**
 * @author Andrew Coates - 20/03/2015.
 */
public class WalkExample {
    private static Contacts createPopulatedContacts() {
        return GraphPopulator.newBuilder().build().populate(Contacts.class);
    }

    @Test
    public void shouldWalkContacts() throws Exception {
        // Example used in main README.md
        Contacts contacts = createPopulatedContacts();
        GraphWalker walker = GraphWalker.newBuilder().build();

        FieldVisitor visitor = FieldVisitors.chain(
            SetAccessibleFieldVisitor.INSTANCE,
            field -> System.out.println(field.getName() + "=" + field.getValue()));

        walker.walk(contacts, visitor);
    }
}
