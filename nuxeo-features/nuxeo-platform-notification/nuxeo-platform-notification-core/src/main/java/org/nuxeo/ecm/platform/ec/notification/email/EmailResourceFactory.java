/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     matic
 */
package org.nuxeo.ecm.platform.ec.notification.email;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.mail.Session;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * @author matic
 */
public class EmailResourceFactory implements ObjectFactory {

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) {
        final Reference ref = (Reference) obj;
        if (!ref.getClassName().equals("javax.mail.Session"))
            return (null);
        ref.getAll();
        final Properties properties = toProperties(ref.getAll());
        return AccessController.doPrivileged(new PrivilegedAction<Session>() {
            public Session run() {
                return EmailHelper.newSession(properties);
            }
        });

    }

    protected Properties toProperties(Enumeration<RefAddr> attributes) {
        Properties props = new Properties();
        while (attributes.hasMoreElements()) {
            RefAddr attribute = attributes.nextElement();
            if ("factory".equals(attribute.getType())) {
                continue;
            }
            props.put(attribute.getType(), attribute.getContent());
        }
        return props;
    }
}
