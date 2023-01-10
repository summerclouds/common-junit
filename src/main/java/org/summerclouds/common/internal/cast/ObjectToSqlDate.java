/**
 * Copyright (C) 2022 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.summerclouds.common.internal.cast;

import java.sql.Date;

import org.summerclouds.common.internal.TCast;

public class ObjectToSqlDate implements Caster<Object, Date> {

    @Override
    public Class<? extends Date> getToClass() {
        return Date.class;
    }

    @Override
    public Class<? extends Object> getFromClass() {
        return Object.class;
    }

    @Override
    public Date cast(Object in, Date def) {
        if (in == null) return def;
        try {
            String ins = TCast.toString(in);
            return TCast.toSqlDate(TCast.toDate(ins, def));
        } catch (Throwable t) {
            return def;
        }
    }
}
