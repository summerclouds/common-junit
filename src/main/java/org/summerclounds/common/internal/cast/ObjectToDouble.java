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
package org.summerclounds.common.internal.cast;

import org.summerclounds.common.internal.TLog;
import org.summerclounds.common.internal.TValue;

public class ObjectToDouble implements Caster<Object, Double> {

    private static final TLog log = TLog.getLog(ObjectToDouble.class);

    @Override
    public Class<? extends Double> getToClass() {
        return Double.class;
    }

    @Override
    public Class<? extends Object> getFromClass() {
        return Object.class;
    }

    @Override
    public Double cast(Object in, Double def) {
        TValue<Double> ret = new TValue<>(def);
        toDouble(in, 0, ret);
        return ret.getValue();
    }

    public double toDouble(Object in, double def, TValue<Double> ret) {
        if (in == null) return def;
        if (in instanceof Number) {
            double r = ((Number) in).doubleValue();
            if (ret != null) ret.setValue(r);
            return r;
        }
        try {
            double r = Double.parseDouble(String.valueOf(in));
            if (ret != null) ret.setValue(r);
            return r;
        } catch (Throwable e) {
            log.t(in, e.toString());
        }
        return def;
    }
}
