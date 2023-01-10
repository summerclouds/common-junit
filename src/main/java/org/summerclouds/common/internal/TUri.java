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
package org.summerclouds.common.internal;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * The class encodes/decodes strings in rfc1738 format and provide helpers to handle with URI / URL
 * and URN.
 *
 * <p>German resource for URI and URL: http://t3n.de/news/url-uri-unterschiede-516483/
 * https://tools.ietf.org/html/rfc3986 https://tools.ietf.org/html/rfc1738
 *
 * @author jesus
 */
public abstract class TUri implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";
    public static final String SCHEME_FTP = "ftp";
    public static final String SCHEME_FILE = "file";
    public static final String SCHEME_SFPT = "sftp";

    public static class Query extends TreeMap<String, String> {
        private static final long serialVersionUID = -1;

        public void put(String _key, int _value) {
            put(_key, TCast.toString(_value));
        }

        public int getInt(String _key, int _def) {
            String v = get(_key);
            if (v == null) return _def;
            return TCast.toint(v, 0);
        }

        public int getInt(String _key) {
            return getInt(_key, -1);
        }

        @Override
        public String toString() {
            return implode(this);
        }
    }

    public TUri() {}

    /**
     * Decode a string with rfc1738 spec.
     *
     * @param _in encoded string
     * @return decoded string
     */
    public static String decode(String _in) {

        if (_in == null) return "";

        try {
            return URLDecoder.decode(_in, TString.CHARSET_DEFAULT);
        } catch (UnsupportedEncodingException e) {
            // TODO log it
        }

        StringBuilder sb = new StringBuilder();

        int mode = 0;
        int buffer = 0;
        for (int i = 0; i < _in.length(); i++) {

            char c = _in.charAt(i);

            if (mode == 0) {

                if (c == '%') {
                    mode = 1;
                    buffer = 0;
                } else if (c == '+') sb.append(' ');
                else sb.append(c);

            } else if (mode == 1) {

                if (c >= '0' && c <= '9') buffer = c - '0';
                else if (c >= 'A' && c <= 'F') buffer = c + 10 - 'A';
                else if (c >= 'a' && c <= 'f') buffer = c + 10 - 'a';

                mode = 2;
            } else if (mode == 2) {

                buffer = buffer * 16;

                if (c >= '0' && c <= '9') buffer += c - '0';
                else if (c >= 'A' && c <= 'F') buffer += c + 10 - 'A';
                else if (c >= 'a' && c <= 'f') buffer += c + 10 - 'a';

                sb.append((char) buffer);
                mode = 0;
            }
        }

        return sb.toString();
    }

    /**
     * encode a string in rfc1738 spec
     *
     * @param _in decoded string
     * @return encoded string
     */
    public static String encode(String _in) {
        if (_in == null) return "";
        try {
            return URLEncoder.encode(_in, TString.CHARSET_DEFAULT); // as default charset
        } catch (UnsupportedEncodingException e) {
            // TODO log
        }

        return encodeNoUTF8(_in);
    }

    public static String encodeNoUTF8(String _in) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < _in.length(); i++) {

            char c = _in.charAt(i);

            //			if (c == '%' || c == '&' || c == '=' || c == '+' || c == '\n'
            //					|| c == '\r' || c == '?' || c == ' ' )
            if (!(c >= 'A' && c <= 'Z'
                    || c >= 'a' && c <= 'z'
                    || c >= '0' && c <= '9'
                    || c == ','
                    || c == '.')) {

                encodeNoUTF8(sb, c);

            } else sb.append(c);
        }

        return sb.toString();
    }

    public static String encode(char c) {
        StringBuilder sb = new StringBuilder();
        encodeNoUTF8(sb, c);
        return sb.toString();
    }

    public static void encodeNoUTF8(StringBuilder sb, char c) {

        if (c == ' ') {
            sb.append('+');
            return;
        }

        sb.append('%');

        int buffer = 0;

        int cc = c;
        buffer = cc / 16;
        if (buffer < 10) sb.append((char) ((int) '0' + buffer));
        else sb.append((char) ((int) 'A' - 10 + buffer));

        buffer = cc % 16;
        if (buffer < 10) sb.append((char) ((int) '0' + buffer));
        else sb.append((char) ((int) 'A' - 10 + buffer));
    }

    /**
     * Transform the elements of an array to a string using the rfc1738 sprec.
     *
     * @param in
     * @return encoded string
     */
    public static String implodeArray(String... in) {

        if (in == null) return "";

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String i : in) {
            if (!first) sb.append('&');
            sb.append(encode(i));
            first = false;
        }
        return sb.toString();
    }

    /**
     * Transform the array into a key value list, the even elements are 'keys', followed by the odd
     * 'value'.
     *
     * @param in
     * @return encoded string
     */
    public static String implodeKeyValues(String... in) {

        if (in == null) return "";

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        boolean key = true;
        for (String i : in) {
            if (!first && key) sb.append('&');
            sb.append(encode(i));
            first = false;

            if (key) {
                sb.append('=');
                key = false;
            } else {
                key = true;
            }
        }
        return sb.toString();
    }

    /**
     * Transforms a encoded array of strings back.
     *
     * @param in
     * @return decoded parts
     */
    public static String[] explodeArray(String in) {

        if (in == null || in.length() == 0) return new String[0];

        String[] out = in.split("&");
        for (int i = 0; i < out.length; i++) out[i] = decode(out[i]);

        return out;
    }

    public static String[] explodeArray(String in, char split) {

        if (in == null || in.length() == 0) return new String[0];

        String[] out = TString.split(in, String.valueOf(split));
        for (int i = 0; i < out.length; i++) out[i] = decode(out[i]);

        return out;
    }

    /**
     * Transforms a list encoded map of attributes back.
     *
     * @param _in
     * @return decoded parts
     */
    public static Map<String, String> explode(String _in) {

        if (_in == null) return new TreeMap<String, String>();

        TreeMap<String, String> out = new TreeMap<String, String>();

        String[] obj = _in.split("&");

        for (int i = 0; i < obj.length; i++) {

            String[] kv = obj[i].split("=");

            if (kv.length == 2) {
                if (kv[1] == null) kv[1] = "";
                out.put(decode(kv[0]), decode(kv[1]));
            } else if (kv.length == 1) out.put(decode(kv[0]), "");
        }

        return out;
    }

    /**
     * Encode a list of attributes in a single string
     *
     * @param _in
     * @return encoded string
     */
    public static String implode(Map<String, String> _in) {

        if (_in == null) return "";

        StringBuilder sb = new StringBuilder();

        boolean first = true;

        for (Iterator<String> e = _in.keySet().iterator(); e.hasNext(); ) {

            String key = e.next();
            String value = _in.get(key);

            if (value != null) {
                if (!first) sb.append('&');
                sb.append(encode(key));
                sb.append('=');
                sb.append(encode(value));
                first = false;
            }
        }

        return sb.toString();
    }

    public static void setParameterValue(String url, String name, String value) {

        name = encode(name) + "=";
        value = encode(value);

        int pos = url.indexOf("&" + name);
        if (pos < 0) {
            pos = url.indexOf("?" + name);
        }
        if (pos < 0) {
            if (url.contains("?")) url = url + "&";
            else url = url + "?";
            url = url + name + value;
        } else {
            int pos2 = url.indexOf("&", pos + 1);
            if (pos2 < 0) {
                url = url.substring(0, pos + 1 + name.length()) + value;
            } else {
                url = url.substring(0, pos + 1 + name.length()) + value + url.substring(pos2);
            }
        }
    }

    public static TUri toUri(String path) {
        return new TMutableUri(path);
    }

    public static TUri toUri(File file) {
        return new TMutableUri(SCHEME_FILE + ":" + file.getAbsolutePath());
    }

    public abstract String getScheme();

    public abstract String getLocation();

    public abstract String getUsername();

    public abstract String getPassword();

    public abstract String[] getParams();

    public abstract Map<String, String> getQuery();

    public abstract String getFragment();

    public abstract String getPath();

    public abstract String[] getPathParts();

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        if (getScheme() != null) {
            out.append(getScheme()).append(':');
        }

        if (getUsername() != null) {
            out.append("//").append(getUsername());
            if (getPassword() != null) out.append(':').append(getPassword());
            out.append("@");
            if (getLocation() != null) out.append(getLocation());
        } else if (getLocation() != null) out.append("//").append(getLocation());

        if (TString.isSet(getPath()) && getLocation() != null && !getPath().startsWith("/"))
            out.append('/');
        if (TString.isSet(getPath())) out.append(getPath());

        if (getParams() != null) for (String p : getParams()) out.append(';').append(encode(p));

        if (getQuery() != null) {
            out.append('?');
            boolean first = true;
            for (Entry<String, String> entry : getQuery().entrySet()) {
                if (first) first = false;
                else out.append('&');
                out.append(encode(entry.getKey())).append('=').append(encode(entry.getValue()));
            }
        }

        if (getFragment() != null) out.append('#').append(getFragment());

        return out.toString();
    }

    /**
     * Returns the name of the file in a path name. Using the OS specific separator.
     * '/dir/subdir/file.ext' will return 'file.ext'. This function use only slash as directory
     * separator. If you have a real os filepath and need to use the system directory separator use
     * MFile.getFileName()
     *
     * @param path
     * @return the file name
     */
    public static String getFileName(String path) {
        if (path == null) return null;

        if (TString.isIndex(path, "/")) path = TString.afterLastIndex(path, "/");

        return path;
    }

    /**
     * Returns the directory without file name or current directory. '/dir/subdir/file' will return
     * '/dir/subdir'. This function use only slash as directory separator. If you have a real os
     * filepath and need to use the system directory separator use MFile.getFileDirectory()
     *
     * @param path
     * @return The previous directory name or null
     */
    public static String getFileDirectory(String path) {
        if (path == null) return null;

        if (TString.isIndex(path, "/")) {
            String ret = TString.beforeLastIndex(path, "/");
            while (ret.endsWith("/")) ret = ret.substring(0, ret.length() - 1);
            if (ret.length() == 0) return null;
            return ret;
        }
        return null;
    }
}
