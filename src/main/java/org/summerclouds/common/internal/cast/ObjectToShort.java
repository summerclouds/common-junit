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

public class ObjectToShort implements Caster<Object, Short> {

    private static final TLog log = TLog.getLog(ObjectToShort.class);

    @Override
    public Class<? extends Short> getToClass() {
        return Short.class;
    }

    @Override
    public Class<? extends Object> getFromClass() {
        return Object.class;
    }

    @Override
    public Short cast(Object in, Short def) {
        TValue<Short> ret = new TValue<>(def);
        toShort(in, (short) 0, ret);
        return ret.getValue();
    }

    public short toShort(Object in, short def, TValue<Short> ret) {
        if (in == null) return def;
        if (in instanceof Short) {
            if (ret != null) ret.setValue((Short) in);
            return ((Short) in).shortValue();
        }
        if (in instanceof Number) {
            short r = ((Number) in).shortValue();
            if (ret != null) ret.setValue(r);
            return r;
        }

        String _in = String.valueOf(in);
        try {
            if (_in.startsWith("0x") || _in.startsWith("-0x") || _in.startsWith("+0x")) {
                int start = 2;
                if (_in.startsWith("-")) start = 3;
                int out = 0;
                for (int i = start; i < _in.length(); i++) {
                    int s = -1;
                    char c = _in.charAt(i);
                    if (c >= '0' && c <= '9') s = c - '0';
                    else if (c >= 'a' && c <= 'f') s = c - 'a' + 10;
                    else if (c >= 'A' && c <= 'F') s = c - 'A' + 10;

                    if (s == -1) throw new NumberFormatException(_in);

                    out = out * (short) 16 + (short) s;
                }
                if (_in.startsWith("-")) out = -out;
                if (out > Short.MAX_VALUE) out = Short.MAX_VALUE;
                if (out < Short.MIN_VALUE) out = Short.MIN_VALUE;
                if (ret != null) ret.setValue((short) out);
                return (short) out;
            }

            short r = Short.parseShort(_in);
            if (ret != null) ret.setValue(r);
            return r;
        } catch (Throwable e) {
            log.t(_in, e.toString());
            return def;
        }
    }
}
