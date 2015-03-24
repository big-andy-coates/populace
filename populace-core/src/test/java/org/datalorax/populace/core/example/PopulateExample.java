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
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Andrew Coates - 20/03/2015.
 */
public class PopulateExample {
    @Test
    public void shouldPopulateContacts() throws Exception {
        // Example used in main README.md
        GraphPopulator populator = GraphPopulator.newBuilder().build();
        Contacts contacts = populator.populate(Contacts.class);

        assertThat(contacts.getPeople(), is(not(empty())));
        assertThat(contacts.getPeople().get(0), is(not(nullValue())));
        assertThat(contacts.getPeople().get(0).getName(), is(notNullValue()));
        assertThat(contacts.getPeople().get(0).getName(), is(not("")));
        assertThat(contacts.getPeople().get(0).getAddress(), is(notNullValue()));
        assertThat(contacts.getPeople().get(0).getAddress().getLines(), is(not(empty())));
        assertThat(contacts.getPeople().get(0).getAddress().getLines().get(0), is(not(nullValue())));
        assertThat(contacts.getPeople().get(0).getAddress().getLines().get(0), is(not("")));
    }

    @Test
    public void shouldPopulateExistingContacts() throws Exception {
        // Example used in main README.md
        GraphPopulator populator = GraphPopulator.newBuilder().build();
        Contacts contacts = populator.populate(new Contacts());

        assertThat(contacts.getPeople(), is(not(empty())));
        assertThat(contacts.getPeople().get(0), is(not(nullValue())));
        assertThat(contacts.getPeople().get(0).getName(), is(notNullValue()));
        assertThat(contacts.getPeople().get(0).getName(), is(not("")));
        assertThat(contacts.getPeople().get(0).getAddress(), is(notNullValue()));
        assertThat(contacts.getPeople().get(0).getAddress().getLines(), is(not(empty())));
        assertThat(contacts.getPeople().get(0).getAddress().getLines().get(0), is(not(nullValue())));
        assertThat(contacts.getPeople().get(0).getAddress().getLines().get(0), is(not("")));
    }
}
