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
package org.summerclouds.common.internal;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TPipedStream implements Closeable {

    private TCircularByteBuffer byteBuffer = new TCircularByteBuffer(10000);
    private Out out = new Out();
    private In in = new In();
    private long writeTimeout = -1;
    private long readTimeout = -1;
    private boolean closed = false;

    public OutputStream getOut() {
        return out;
    }

    public InputStream getIn() {
        return in;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public long getWriteTimeout() {
        return writeTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    private class Out extends OutputStream {

        @Override
        public void write(int b) throws IOException {

            //            if (closed) throw new EOFException();
            long start = System.currentTimeMillis();
            while (byteBuffer.isNearlyFull()) {
                TThread.sleep(200);
                if (isTimeOut(start, writeTimeout)) throw new IOException("write timeout");
            }
            synchronized (TPipedStream.this) {
                //                System.out.println("Write: " + (char)b + " (" + b + ")");
                byteBuffer.putInt(b);
            }
        }
    }

    private class In extends InputStream {

        @Override
        public int read() throws IOException {

            long start = System.currentTimeMillis();
            while (byteBuffer.isEmpty()) {
                if (closed) return -1; // EOFException ?
                TThread.sleep(200);
                if (isTimeOut(start, readTimeout)) throw new IOException("read timeout");
            }
            synchronized (TPipedStream.this) {
                byte o = byteBuffer.get();
                //               System.err.println("Read: " + (char)o + "(" + o + ")");
                return o;
            }
        }

        @Override
        public int available() throws IOException {
            return byteBuffer.length();
        }
    }

    @Override
    public void close() {
        closed = true;
    }
    
    private static boolean isTimeOut(long start, long timeout) {
        return timeout > -1 && System.currentTimeMillis() - start > timeout;
    }

}
