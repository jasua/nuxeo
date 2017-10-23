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
 *     Funsho David
 *
 */

package org.nuxeo.ecm.platform.audit.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.nuxeo.ecm.platform.audit.api.ExtendedInfo;
import org.nuxeo.ecm.platform.audit.impl.ExtendedInfoImpl;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * Serializer class for extended info to a JSON object
 * 
 * @since 9.3
 */
public class ExtendedInfoSerializer extends JsonSerializer<ExtendedInfo> {

    @Override
    public void serialize(ExtendedInfo info, JsonGenerator jg,
            SerializerProvider provider) throws IOException {

        if (info instanceof ExtendedInfoImpl.DateInfo) {
            ExtendedInfoImpl.DateInfo dateInfo = (ExtendedInfoImpl.DateInfo) info;
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();
            Instant instant = dateInfo.getDateValue().toInstant();
            jg.writeObject(formatter.format(instant));
        } else if (info instanceof ExtendedInfoImpl.BlobInfo) {
            Serializable value = ((ExtendedInfoImpl.BlobInfo) info).getBlobValue();
            jg.writeObject(Base64.encodeBase64(SerializationUtils.serialize(value)));
        } else {
            jg.writeObject(info.getSerializableValue());
        }

    }

}
