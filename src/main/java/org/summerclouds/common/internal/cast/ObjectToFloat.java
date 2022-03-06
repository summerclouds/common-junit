/**
 * Copyright (C) 2002 Mike Hummel (mh@mhus.de)
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

import org.summerclouds.common.internal.TLog;
import org.summerclouds.common.internal.TValue;

public class ObjectToFloat implements Caster<Object, Float> {

    private static final TLog log = TLog.getLog(ObjectToFloat.class);

    @Override
    public Class<? extends Float> getToClass() {
        return Float.class;
    }

    @Override
    public Class<? extends Object> getFromClass() {
        return Object.class;
    }

    @Override
    public Float cast(Object in, Float def) {
        TValue<Float> ret = new TValue<>(def);
        toFloat(in, 0, ret);
        return ret.getValue();
    }

    public float toFloat(Object in, float def, TValue<Float> ret) {
        if (in == null) return def;
        if (in instanceof Number) {
            float r = ((Number) in).floatValue();
            if (ret != null) ret.setValue(r);
            return r;
        }
        try {
            float r = Float.parseFloat(String.valueOf(in));
            if (ret != null) ret.setValue(r);
            return r;
        } catch (Throwable e) {
            log.t(in, e.toString());
        }
        return def;
    }
}
