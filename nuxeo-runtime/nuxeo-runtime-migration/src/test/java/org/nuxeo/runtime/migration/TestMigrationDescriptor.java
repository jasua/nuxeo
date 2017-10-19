/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.runtime.migration;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.nuxeo.common.xmap.XMap;
import org.nuxeo.runtime.migration.MigrationDescriptor.MigrationStateDescriptor;
import org.nuxeo.runtime.migration.MigrationDescriptor.MigrationStepDescriptor;

public class TestMigrationDescriptor {

    protected XMap xmap;

    protected static URL getResource(String resource) {
        return Thread.currentThread().getContextClassLoader().getResource(resource);
    }

    @Before
    public void setUp() throws Exception {
        xmap = new XMap();
        xmap.register(MigrationDescriptor.class);
    }

    @Test
    public void testRead() throws Exception {
        MigrationDescriptor desc = (MigrationDescriptor) xmap.load(
                getResource("OSGI-INF/test-migration-descriptor.xml"));
        assertEquals("my_migration", desc.id);
        assertEquals("my_migration", desc.descriptionLabel);
        assertEquals("My Migration", desc.description);

        assertEquals(Arrays.asList("v1", "v2", "v3"), new ArrayList<>(desc.states.keySet()));
        MigrationStateDescriptor state;
        state = desc.states.get("v1");
        assertEquals("v1", state.id);
        assertEquals("my_migration.state.v1", state.descriptionLabel);
        assertEquals("Initial state", state.description);
        state = desc.states.get("v2");
        assertEquals("v2", state.id);
        assertEquals("my_migration.state.v2", state.descriptionLabel);
        assertEquals("Second version", state.description);
        state = desc.states.get("v3");
        assertEquals("v3", state.id);
        assertEquals("my_migration.state.v3", state.descriptionLabel);
        assertEquals("Third version", state.description);

        assertEquals(Arrays.asList("first", "second"), new ArrayList<>(desc.steps.keySet()));
        MigrationStepDescriptor step;
        step = desc.steps.get("first");
        assertEquals("first", step.id);
        assertEquals("v1", step.fromState);
        assertEquals("v2", step.toState);
        assertEquals("my_migration.step.first", step.descriptionLabel);
        assertEquals("First step of the migration, from v1 to v2", step.description);
        assertEquals(java.lang.String.class, step.klass);
        step = desc.steps.get("second");
        assertEquals("second", step.id);
        assertEquals("v2", step.fromState);
        assertEquals("v3", step.toState);
        assertEquals("my_migration.step.second", step.descriptionLabel);
        assertEquals("Second step of the migration, from v2 to v3", step.description);
        assertEquals(java.lang.Integer.class, step.klass);
    }

}
