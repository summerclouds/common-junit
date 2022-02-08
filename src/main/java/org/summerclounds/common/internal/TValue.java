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
package org.summerclounds.common.internal;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;

public class TValue<T> implements TValueable<T>, Serializable {

    private static final long serialVersionUID = 1L;

    public TValue() {}

    public TValue(T initial) {
        value = initial;
    }

    public volatile T value;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object in) {
        if (in != null && in instanceof TValueable)
            return TSystem.equals(value, ((TValueable<?>) in).getValue());
        return TSystem.equals(value, in);
    }

    @Override
    public T getValue() {
        return value;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        if (value == null) {
            out.writeInt(0);
            return;
        }
        if (value instanceof Serializable) {
            out.writeInt(1);
            out.writeObject(value);
            return;
        }

        throw new NotSerializableException();
    }

    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        value = null;
        int type = in.readInt();
        if (type == 1) value = (T) in.readObject();
    }

    public void setValue(T value) {
        this.value = value;
    }
}
