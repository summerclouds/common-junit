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

import org.summerclouds.common.internal.TLog;
import org.summerclouds.common.internal.TValue;

public class ObjectToInteger implements Caster<Object, Integer> {

    private static final TLog log = TLog.getLog(ObjectToInteger.class);

    @Override
    public Class<? extends Integer> getToClass() {
        return Integer.class;
    }

    @Override
    public Class<? extends Object> getFromClass() {
        return Object.class;
    }

    @Override
    public Integer cast(Object in, Integer def) {
        TValue<Integer> ret = new TValue<>(def);
        toInt(in, 0, ret);
        return ret.getValue();
    }

    public int toInt(Object in, int def, TValue<Integer> ret) {
        if (in == null) return def;
        if (in instanceof Integer) {
            if (ret != null) ret.setValue((Integer) in);
            return ((Integer) in).intValue();
        }
        if (in instanceof Number) {
            int r = ((Number) in).intValue();
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
                    out = out * 16 + s;
                }
                if (_in.startsWith("-")) out = -out;
                if (ret != null) ret.setValue(out);
                return out;
            }

            int r = Integer.parseInt(_in);
            if (ret != null) ret.setValue(r);
            return r;
        } catch (Exception e) {
            log.t(_in, e.toString());
            return def;
        }
    }
}
