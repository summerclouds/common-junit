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

import java.awt.Color;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Pattern;

/** @author hummel */
public class TString {

    public static final String CHARSET_UTF_8 = "UTF-8";
    public static final String CHARSET_UTF_16 = "UTF-16";
    public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";

    public static final Charset CHARSET_CHARSET_UTF_8 = Charset.forName("UTF-8");
    public static final Charset CHARSET_CHARSET_UTF_16 = Charset.forName("UTF-16");
    /**
     * Use this char set to encode binary data as it is in encodings. It supports 8 bits from \u0000
     * to u00FF. The 'US-ASCII' only supports 7 bits, from \u0000 to \u007F.
     *
     * <p>This is also known as Latin-1 and is the western European encoding standard including
     * German characters. If you plant to read and write binary data with character streams there is
     * no matter which encoding you use (as long as it is readable - could fail with utf-8) but you
     * have to read and write it with the same encoding.
     */
    public static final Charset CHARSET_CHARSET_ISO_8859_1 = Charset.forName("ISO-8859-1");

    public static final char[] WHITESPACE = new char[] {' ', '\n', '\r', '\t'};
    public static final String DEFAULT_SEPARATOR = ",";
    private static final String[] DEFAULT_SELECTION_SEPARATOR = new String[] {"\n\n"};
    public static final String CHARSET_DEFAULT = CHARSET_UTF_8;

    public static final char[] CHARS_READABLE = {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
                'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    // TODO enhance mapping
    private static final Map<String, String> ASCII127_M = new TreeMap<>();
    public static final Map<String, String> ASCII127_MAPPING =
            Collections.unmodifiableMap(ASCII127_M);

    static {
        ASCII127_M.put("a", "\u00e4\u00e0\u00e2\u00e1\u0105\u00e3\u1ea3\u1ea1\u0227"); // äàâáąãảạȧ
        ASCII127_M.put("b", "\u1e05\u1e03"); // ḅḃ
        ASCII127_M.put("c", "\u0109\u0107\u00e7\u010b\u010d"); // ĉćçċč
        ASCII127_M.put("d", "\u1e0d\u1e0b\u010f"); // ḍḋď
        ASCII127_M.put(
                "e", "\u00e8\u00ea\u00e9\u0119\u0229\u011b\u1ebd\u1eb9\u1ebb\u0117"); // èêéęȩěẽẹẻė
        ASCII127_M.put("f", "\u1e1f"); // ḟ
        ASCII127_M.put("g", "\u0121"); // ġ
        ASCII127_M.put("h", "\u1e25\u1e23"); // ḥḣ
        ASCII127_M.put("i", "\u00ec\u00ed\u00ee\u01d0\u0129\u1ec9\u1ecb\u0131"); // ìíîǐĩỉịı
        //        ASCII127_M.put("j", ""); //
        ASCII127_M.put("k", "\u1e33"); // ḳ
        ASCII127_M.put("l", "\u013a\u0142\u013c\u013e\u1e37"); // ĺłļľḷ
        ASCII127_M.put("m", "\u1e43\u1e41"); // ṃṁ
        ASCII127_M.put("n", "\u00f1\u0144\u0146\u0148\u1e47\u1e45"); // ñńņňṇṅ
        ASCII127_M.put(
                "o", "\u00f6\u00f4\u00f2\u01a1\u00f3\u01d2\u014f\u1ecf\u1ecd\u022f"); // öôòơóǒŏỏọȯ
        ASCII127_M.put("p", "\u1e57"); // ṗ
        //        ASCII127_M.put("q", ""); //
        ASCII127_M.put("r", "\u1e5b\u1e59\u0155"); // ṛṙŕ
        ASCII127_M.put("s", "\u015b\u015f\u0161\u015d\u1e63\u1e61"); // śşšŝṣṡ
        ASCII127_M.put("t", "\u1e6d\u1e6b\u0165"); // ṭṫť
        ASCII127_M.put("u", "\u00fc\u00f9\u00fb\u01b0\u00fa\u01d4\u016d\u1ee7\u1ee5"); // üùûưúǔŭủụ
        ASCII127_M.put("v", "\u1e7f"); // ṿ
        ASCII127_M.put("w", "\u1e89\u1e87"); // ẉẇ
        ASCII127_M.put("x", "\u1e8b"); // ẋ
        ASCII127_M.put("y", "\u1ef5\u1e8f\u00fd"); // ỵẏý
        ASCII127_M.put("z", "\u017c\u017a\u017e\u1e91\u017c"); // zżźẓż
        ASCII127_M.put("ae", "\u00e6"); // æ
        ASCII127_M.put("ss", "\u00df"); // ß
    }

    /** A pair (index 0 and 1) with empty strings */
    public static final String[] EMPTY_PAIR = new String[] {"", ""};

    /** A pair (index 0 and 1) with null values */
    public static final String[] NULL_PAIR = new String[] {null, null};

    /**
     * Return true if the the string is not empty also trimmed.
     *
     * @param _in The string to test
     * @return true if the string is not null and not an empty string and not only contains
     *     whitespaces
     */
    public static boolean isSetTrim(String _in) {
        return _in != null && _in.trim().length() > 0;
    }

    /**
     * Return true if the the string is not empty.
     *
     * @param _in The string to test
     * @return true if the string is not null and not an empty string
     */
    public static boolean isSet(String _in) {
        return _in != null && _in.length() > 0;
    }

    /**
     * Returns true if the string is null or an empty string.
     *
     * @param _in
     * @return true if empty
     */
    public static boolean isEmpty(String _in) {
        return (_in == null || _in.length() == 0);
    }

    /**
     * Split the string in parts. Separator is a pattern. In difference of the build in split it
     * will not remove empty parts and the pattern is not a regex.
     *
     * @param _in
     * @param _pattern
     * @return parts
     */
    public static String[] split(String _in, String _pattern) {

        if (_in == null) return new String[0];

        int nr = 0;
        int offset = 0;
        while ((offset = _in.indexOf(_pattern, offset)) != -1) {
            nr++;
            offset += _pattern.length();
        }

        String[] out = new String[nr + 1];

        nr = 0;
        offset = 0;
        int oldOffset = 0;
        while ((offset = _in.indexOf(_pattern, offset)) != -1) {
            out[nr] = _in.substring(oldOffset, offset);
            nr++;
            offset += _pattern.length();
            oldOffset = offset;
        }
        out[nr] = _in.substring(oldOffset);

        return out;
    }

    /**
     * Split the string in parts. Separator is a pattern. In difference of the build in the pattern
     * is not a regex.
     *
     * @param _in
     * @param _pattern
     * @return parts
     */
    public static String[] splitIgnoreEmpty(String _in, String _pattern) {
        return splitIgnoreEmpty(_in, _pattern, false);
    }

    public static String[] splitIgnoreEmpty(String _in, String _pattern, boolean trim) {

        if (_in == null) return new String[0];

        int offset = 0;
        int oldOffset = 0;
        Vector<String> out = new Vector<String>();
        while ((offset = _in.indexOf(_pattern, offset)) != -1) {
            String s = _in.substring(oldOffset, offset);
            if (trim) s = s.trim();
            if (s.length() != 0) out.add(s);
            offset += _pattern.length();
            oldOffset = offset;
        }
        String s = _in.substring(oldOffset);
        if (trim) s = s.trim();
        if (s.length() != 0) out.add(s);
        return out.toArray(new String[out.size()]);
    }

    /**
     * Replace all pattern thru replacement strings. Pattern is not a regex.
     *
     * @param _src
     * @param _pattern
     * @param _replacement
     * @return new string
     */
    public static String replaceAll(String _src, String _pattern, String _replacement) {
        StringBuilder sb = new StringBuilder(_src);
        replaceAll(sb, _pattern, _replacement);
        return sb.toString();
    }

    /**
     * Replace all pattern thru replacement strings. Pattern is not a regex.
     *
     * @param _src
     * @param _pattern
     * @param _replacement
     */
    public static void replaceAll(StringBuilder _src, String _pattern, String _replacement) {

        int offset = 0;
        int len = _pattern.length();
        int diff = _replacement.length() - len + 1;

        while ((offset = _src.indexOf(_pattern, offset)) >= 0) {
            _src.replace(offset, offset + len, _replacement);
            offset += diff;
        }
    }

    // --- Split without default

    /**
     * Return, if the char is located in the string (indexOf). If _s is null, return is false.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @return true if index was found
     */
    public static boolean isIndex(String _s, String _c) {
        if (_s == null) return false;
        int p = _s.indexOf(_c);
        if (p < 0) return false;
        else return true;
    }

    /**
     * Return the string before _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @return part before the index
     */
    public static String beforeIndex(String _s, String _c) {
        if (_s == null) return null;
        int p = _s.indexOf(_c);
        if (p < 0) return "";

        return _s.substring(0, p);
    }

    /**
     * Return the string after _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @return part after the index
     */
    public static String afterIndex(String _s, String _c) {
        if (_s == null) return null;
        int p = _s.indexOf(_c);
        if (p < 0) return "";

        return _s.substring(p + _c.length());
    }

    /**
     * Return, if the char is located in the string (indexOf). If _s is null, return is false.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @return true if the index was found
     */
    public static boolean isIndex(String _s, char _c) {
        if (_s == null) return false;
        int p = _s.indexOf(_c);
        if (p < 0) return false;
        else return true;
    }

    /**
     * Return the string before _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @return part before the index
     */
    public static String beforeIndex(String _s, char _c) {
        if (_s == null) return null;
        int p = _s.indexOf(_c);
        if (p < 0) return "";

        return _s.substring(0, p);
    }

    /**
     * Return the string after _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @return part after the index
     */
    public static String afterIndex(String _s, char _c) {
        if (_s == null) return null;
        int p = _s.indexOf(_c);
        if (p < 0) return "";

        return _s.substring(p + 1);
    }

    /**
     * Returns true if one of the chars is found in the string. If _s is null, return is false.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @return true if one of the character was found
     */
    public static boolean isIndex(String _s, char[] _c) {
        if (_s == null) return false;
        for (int i = 0; i < _c.length; i++) {
            if (_s.indexOf(_c[i]) >= 0) return true;
        }
        return false;
    }

    /**
     * Return the string before _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @return part before the first found character
     */
    public static String beforeIndex(String _s, char[] _c) {
        if (_s == null) return null;
        int p = -1;
        for (int i = 0; i < _c.length; i++) {
            if ((p = _s.indexOf(_c[i])) >= 0) return _s.substring(0, p);
        }
        return "";
    }

    /**
     * Return the string after _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @return part after the first found character
     */
    public static String afterIndex(String _s, char[] _c) {
        if (_s == null) return null;
        int p = -1;
        for (int i = 0; i < _c.length; i++) {
            if ((p = _s.indexOf(_c[i])) >= 0) return _s.substring(p + 1);
        }
        return "";
    }

    /**
     * Returns the position where the first of one of the chars is found in the string. If _s is
     * null, return is -1.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @param offset
     * @return position of the first found character
     */
    public static int indexOf(String _s, char[] _c, int offset) {
        if (_s == null) return -1;
        for (int i = offset; i < _s.length(); i++) {
            char c = _s.charAt(i);
            for (int j = 0; j < _c.length; j++) if (c == _c[j]) return i;
        }
        return -1;
    }

    /**
     * Returns the position where the first of one of the chars is found in the string.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @return position of the first found character
     */
    public static int indexOf(String _s, char[] _c) {
        return indexOf(_s, _c, 0);
    }

    /**
     * Return part before last occurrence of the char. If the char is not found it will return the
     * hole string.
     *
     * @param string
     * @param c
     * @return string before the last found character
     */
    public static String beforeLastIndex(String string, char c) {
        if (string == null) return null;
        int pos = string.lastIndexOf(c);
        if (pos < 0) return string;
        return string.substring(0, pos);
    }

    /**
     * Return the part after the last occurrence of the char. If the char is not found it will
     * return the hole string.
     *
     * @param string
     * @param c
     * @return part after last found character
     */
    public static String afterLastIndex(String string, char c) {
        if (string == null) return null;
        int pos = string.lastIndexOf(c);
        if (pos < 0) return string;
        return string.substring(pos + 1);
    }

    /**
     * Return part before last occurrence of the char. If the char is not found it will return the
     * hole string.
     *
     * @param string
     * @param c
     * @return part before last found character
     */
    public static String beforeLastIndex(String string, String c) {
        if (string == null) return null;
        int pos = string.lastIndexOf(c);
        if (pos < 0) return string;
        return string.substring(0, pos);
    }

    /**
     * Return the part after the last occurrence of the char. If the char is not found it will
     * return the hole string.
     *
     * @param string
     * @param c
     * @return part after last found character
     */
    public static String afterLastIndex(String string, String c) {
        if (string == null) return null;
        int pos = string.lastIndexOf(c);
        if (pos < 0) return string;
        return string.substring(pos + c.length());
    }

    // --- Split with default

    /**
     * Return the string before _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @param _def Default value, if _s is null or _c not found
     * @return part before the index
     */
    public static String beforeIndex(String _s, String _c, String _def) {
        if (_s == null) return _def;
        int p = _s.indexOf(_c);
        if (p < 0) return _def;

        return _s.substring(0, p);
    }

    /**
     * Return the string after _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @param _def Default value, if _s is null or _c not found
     * @return part after the index
     */
    public static String afterIndex(String _s, String _c, String _def) {
        if (_s == null) return _def;
        int p = _s.indexOf(_c);
        if (p < 0) return _def;

        return _s.substring(p + _c.length());
    }

    /**
     * Return the string before _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @param _def Default value, if _s is null or _c not found
     * @return part before the index
     */
    public static String beforeIndex(String _s, char _c, String _def) {
        if (_s == null) return _def;
        int p = _s.indexOf(_c);
        if (p < 0) return _def;

        return _s.substring(0, p);
    }

    /**
     * Return the string after _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @param _def Default value, if _s is null or _c not found
     * @return part after the index
     */
    public static String afterIndex(String _s, char _c, String _def) {
        if (_s == null) return _def;
        int p = _s.indexOf(_c);
        if (p < 0) return _def;

        return _s.substring(p + 1);
    }

    /**
     * Return the string before _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @param _def Default value, if _s is null or _c not found
     * @return part before the first found character
     */
    public static String beforeIndex(String _s, char[] _c, String _def) {
        if (_s == null) return _def;
        int p = -1;
        for (int i = 0; i < _c.length; i++) {
            if ((p = _s.indexOf(_c[i])) >= 0) return _s.substring(0, p);
        }
        return _def;
    }

    /**
     * Return the string after _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @param _def Default value, if _s is null or _c not found
     * @return part after the first found character
     */
    public static String afterIndex(String _s, char[] _c, String _def) {
        if (_s == null) return _def;
        int p = -1;
        for (int i = 0; i < _c.length; i++) {
            if ((p = _s.indexOf(_c[i])) >= 0) return _s.substring(p + 1);
        }
        return _def;
    }

    /**
     * Return part before last occurrence of the char. If the char is not found it will return the
     * hole string.
     *
     * @param string
     * @param c
     * @param def Default value, if _s is null or _c not found
     * @return string before the last found character
     */
    public static String beforeLastIndex(String string, char c, String def) {
        if (string == null) return def;
        int pos = string.lastIndexOf(c);
        if (pos < 0) return def;
        return string.substring(0, pos);
    }

    /**
     * Return the part after the last occurrence of the char. If the char is not found it will
     * return the hole string.
     *
     * @param string
     * @param c
     * @param def Default value, if _s is null or _c not found
     * @return part after last found character
     */
    public static String afterLastIndex(String string, char c, String def) {
        if (string == null) return def;
        int pos = string.lastIndexOf(c);
        if (pos < 0) return def;
        return string.substring(pos + 1);
    }

    /**
     * Return part before last occurrence of the char. If the char is not found it will return the
     * hole string.
     *
     * @param string
     * @param c
     * @param def Default value, if _s is null or _c not found
     * @return part before last found character
     */
    public static String beforeLastIndex(String string, String c, String def) {
        if (string == null) return def;
        int pos = string.lastIndexOf(c);
        if (pos < 0) return def;
        return string.substring(0, pos);
    }

    /**
     * Return the part after the last occurrence of the char. If the char is not found it will
     * return the hole string.
     *
     * @param string
     * @param c
     * @param def Default value, if _s is null or _c not found
     * @return part after last found character
     */
    public static String afterLastIndex(String string, String c, String def) {
        if (string == null) return def;
        int pos = string.lastIndexOf(c);
        if (pos < 0) return def;
        return string.substring(pos + c.length());
    }

    // --- Split returning two parts

    /**
     * Return the string before and after _c in _s. If _c is not found in _s, or _s is null then
     * return is _def.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @param _def Default value, if _s is null or _c not found
     * @return 0: part before the index, 1: part after the index
     */
    public static String[] splitIndex(String _s, String _c, String[] _def) {
        if (_s == null) return _def;
        int p = _s.indexOf(_c);
        if (p < 0) return _def;

        return new String[] {_s.substring(0, p), _s.substring(p + 1)};
    }

    /**
     * Return the string before and after _c in _s. If _c is not found in _s, or _s is null then
     * return is _def.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @param _def Default value, if _s is null or _c not found
     * @return 0: part before the index, 1: part after the index
     */
    public static String[] splitIndex(String _s, char _c, String[] _def) {
        if (_s == null) return _def;
        int p = _s.indexOf(_c);
        if (p < 0) return _def;

        return new String[] {_s.substring(0, p), _s.substring(p + 1)};
    }

    /**
     * Return the string before _c in _s. If _c is not found in _s, return is an empty string. If _s
     * is null, return is null.
     *
     * @param _s The string to search in
     * @param _c The delimiter
     * @param _def Default value, if _s is null or _c not found
     * @return part before the first found character
     */
    public static String[] splitIndex(String _s, char[] _c, String[] _def) {
        if (_s == null) return _def;
        int p = -1;
        for (int i = 0; i < _c.length; i++) {
            if ((p = _s.indexOf(_c[i])) >= 0)
                return new String[] {_s.substring(0, p), _s.substring(p + 1)};
        }
        return _def;
    }

    /**
     * Return part before last occurrence of the char. If the char is not found it will return the
     * hole string.
     *
     * @param string
     * @param c
     * @param def Default value, if _s is null or _c not found
     * @return string before the last found character
     */
    public static String[] splitLastIndex(String string, char c, String[] def) {
        if (string == null) return def;
        int pos = string.lastIndexOf(c);
        if (pos < 0) return def;
        return new String[] {string.substring(0, pos), string.substring(pos + 1)};
    }

    /**
     * Return part before last occurrence of the char. If the char is not found it will return the
     * hole string.
     *
     * @param string
     * @param c
     * @param def Default value, if _s is null or _c not found
     * @return part before last found character
     */
    public static String[] splitLastIndex(String string, String c, String[] def) {
        if (string == null) return def;
        int pos = string.lastIndexOf(c);
        if (pos < 0) return def;
        return new String[] {string.substring(0, pos), string.substring(pos + 1)};
    }

    // ---

    /**
     * Encodes for the web. Same like MXml.encode
     *
     * @param _in
     * @return encoded string
     */
    public static String encodeWebUnicode(String _in) {

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < _in.length(); i++) {
            char c = _in.charAt(i);
            if (c > 255) {
                out.append("&#" + (int) c + ";");
            } else if (c == '&') {
                out.append("&amp;");
            } else out.append(c);
        }
        return out.toString();
    }

    public static boolean isUnicode(String _in, char _joker, boolean encodeCr) {
        for (int i = 0; i < _in.length(); i++) {
            char c = _in.charAt(i);
            if ((c < 32 || c > 127 || c == _joker) && (encodeCr || (c != '\n' && c != '\r')))
                return true;
        }
        return false;
    }

    public static String decodeUnicode(String _in) {

        int mode = 0;
        byte[] buffer = new byte[4];

        if (_in == null) return "";

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < _in.length(); i++) {
            char c = _in.charAt(i);
            switch (mode) {
                case 0:
                    if (c == '\\') mode = 1;
                    else out.append(c);
                    break;
                case 1:
                    if (c == 'u') mode = 2;
                    else if (c == 'n') {
                        out.append('\n');
                        mode = 0;
                    } else if (c == 'r') {
                        out.append('\r');
                        mode = 0;
                    } else if (c == 't') {
                        out.append('\t');
                        mode = 0;
                    } else out.append('\\' + c);
                    break;
                case 2:
                    buffer[0] = (byte) c;
                    mode = 3;
                    break;
                case 3:
                    buffer[1] = (byte) c;
                    mode = 4;
                    break;
                case 4:
                    buffer[2] = (byte) c;
                    mode = 5;
                    break;
                case 5:
                    buffer[3] = (byte) c;
                    out.append((char) TCast.tointFromHex(new String(buffer)));
                    mode = 0;
                    break;
            }
        }
        return out.toString();
    }

    public static String decodeURIComponent(String _in) {

        int mode = 0;
        byte[] buffer = new byte[2];

        if (_in == null) return "";

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < _in.length(); i++) {
            char c = _in.charAt(i);
            switch (mode) {
                case 0:
                    if (c == '%') mode = 1;
                    else out.append(c);
                    break;
                case 1:
                    buffer[0] = (byte) c;
                    mode = 2;
                    break;
                case 2:
                    buffer[1] = (byte) c;
                    out.append((char) TCast.tointFromHex(new String(buffer)));
                    mode = 0;
                    break;
            }
        }
        return out.toString();
    }

    public static String encodeCSVLine(String[] _fields) {

        if (_fields == null || _fields.length == 0) return "";

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < _fields.length; i++) {
            if (i != 0) out.append(',');
            out.append(encodeCSV(_fields[i]));
        }

        return out.toString();
    }

    public static String encodeCSV(String _field) {

        if (_field == null) return "\"\"";
        return '"' + _field.replaceAll("\\\"", "\"\"") + '"';
    }

    /*
     * public static String[] decodeCSVLine( String _fields ) {
     *
     * StringBuilder field = new StringBuilder(); Vector out = new Vector();
     *
     * csvParseSep( _fields, 0 );
     *
     * }
     */

    public static String decodeCSV(String _field) {

        if (_field == null || _field.length() == 0) return "";

        _field = _field.trim();
        if (_field.startsWith("\"") && _field.endsWith("\"")) {
            _field = _field.substring(1, _field.length() - 1);
        }

        if (_field.length() == 0) return "";

        return _field.replaceAll("\\\"\\\"", "\"");
    }

    public static boolean compareRegexPattern(String str, String pattern) {
        if (str == null || pattern == null) return false;
        return str.matches(pattern);
    }

    /**
     * Use % at the beginning and/or the end example: %aloa% or !%aloa%
     *
     * @param str
     * @param pattern
     * @return true if match
     */
    public static boolean compareSQLLikePattern(String str, String pattern) {
        if (str == null || pattern == null) return false;
        boolean policy = true;
        if (pattern.startsWith("!")) {
            policy = false;
            pattern = pattern.substring(1);
        }
        if (pattern.endsWith("%") && pattern.startsWith("%")) {
            if (str.indexOf(pattern.substring(1, pattern.length() - 2)) >= 0) return policy;
        } else if (pattern.startsWith("%")) {
            if (str.endsWith(pattern.substring(1))) return policy;
        } else if (pattern.endsWith("%")) {
            if (str.startsWith(pattern.substring(0, pattern.length() - 1))) return policy;
        } else if (pattern.equals(str)) {
            return policy;
        }
        return !policy;
    }

    /**
     * Use * at the beginning or/and the end and a ! at the beginning for 'not'. e.g. *aloa* !*aloa*
     *
     * @param str
     * @param pattern
     * @return true if match
     */
    public static boolean compareFsLikePattern(String str, String pattern) {
        if (str == null || pattern == null) return false;
        boolean ret = true;
        if (pattern.startsWith("!")) {
            ret = false;
            pattern = pattern.substring(1);
        }

        if (pattern.equals("*")) return ret;

        if (pattern.endsWith("*") && pattern.startsWith("*")) {
            if (str.indexOf(pattern.substring(1, pattern.length() - 2)) >= 0) return ret;
        } else if (pattern.startsWith("*")) {
            if (str.endsWith(pattern.substring(1))) return ret;
        } else if (pattern.endsWith("*")) {
            if (str.startsWith(pattern.substring(0, pattern.length() - 1))) return ret;
        } else if (pattern.equals(str)) {
            return ret;
        }
        return !ret;
    }

    public static String replaceSQLLikePattern(String str, String pattern, String replace) {
        if (pattern.endsWith("%") && pattern.startsWith("%")) {
            int pos = str.indexOf(pattern.substring(1, pattern.length() - 2));
            if (pos >= 0) {
                return str.substring(0, pos) + replace + str.substring(pos + pattern.length() - 2);
            }
        } else if (pattern.startsWith("%")) {
            if (str.endsWith(pattern.substring(1))) {
                return str.substring(0, str.length() - pattern.length() + 1) + replace;
            }
        } else if (pattern.endsWith("%")) {
            if (str.startsWith(pattern.substring(0, pattern.length() - 1))) {
                return replace + str.substring(pattern.length() - 1);
            }
        } else if (pattern.equals(str)) {
            return replace;
        }
        return str;
    }

    public static String join(String[] src, char glue) {
        if (src == null) return null;
        if (src.length == 0) return "";
        if (src.length == 1) return src[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            if (i != 0) sb.append(glue);
            sb.append(src[i]);
        }
        return sb.toString();
    }

    public static String join(String[] src, int from, int to, String glue) {
        if (src == null) return null;
        if (src.length == 0) return "";
        if (src.length == 1) return src[0];
        StringBuilder sb = new StringBuilder();
        if (to >= src.length) to = src.length - 1;
        for (int i = from; i <= to; i++) {
            if (i != from) sb.append(glue);
            sb.append(src[i]);
        }
        return sb.toString();
    }

    public static String join(long[] src, char glue) {
        if (src == null) return null;
        if (src.length == 0) return "";
        if (src.length == 1) return String.valueOf(src[0]);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            if (i != 0) sb.append(glue);
            sb.append(src[i]);
        }
        return sb.toString();
    }

    public static String join(Collection<?> src, String glue) {
        return join(src.iterator(), glue);
    }

    public static String join(Iterator<?> src, String glue) {
        if (src == null) return null;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (src.hasNext()) {
            if (i != 0) sb.append(glue);
            i++;
            sb.append(src.next());
        }
        return sb.toString();
    }

    /**
     * Convert String to canonical standard form. null -> "". Trims lead trail blanks.
     *
     * @param s String to be converted.
     * @return String in canonical form.
     */
    public static String canonical(String s) {
        if (s == null) {
            return "";
        } else {
            return s.trim();
        }
    } // end canonical

    /**
     * Collapse multiple spaces in string down to a single space. Remove lead and trailing spaces.
     *
     * @param s String to strip of blanks.
     * @return String with all blanks, lead/trail/embedded removed.
     */
    public static String condense(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.indexOf("  ") < 0) {
            return s;
        }
        int len = s.length();
        // have to use StringBuilder for JDK 1.1
        StringBuilder b = new StringBuilder(len - 1);
        boolean suppressSpaces = false;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == ' ') {
                if (suppressSpaces) {
                    // subsequent space
                } else {
                    // first space
                    b.append(c);
                    suppressSpaces = true;
                }
            } else {
                // was not a space
                b.append(c);
                suppressSpaces = false;
            }
        } // end for
        return b.toString();
    } // end condense

    /**
     * count of how many leading characters there are on a string matching a given character. It
     * does not remove them.
     *
     * @param text text with possible leading characters, possibly empty, but not null.
     * @param c the leading character of interest, usually ' ' or '\n'
     * @return count of leading matching characters, possibly 0.
     */
    public static int countLeading(String text, char c) {
        // need defined outside the for loop.
        int count;
        for (count = 0; count < text.length() && text.charAt(count) == c; count++) {}
        return count;
    }

    /**
     * count of how many trailing characters there are on a string matching a given character. It
     * does not remove them.
     *
     * @param text text with possible trailing characters, possibly empty, but not null.
     * @param c the trailing character of interest, usually ' ' or '\n'
     * @return count of trailing matching characters, possibly 0.
     */
    public static int countTrailing(String text, char c) {
        int length = text.length();
        // need defined outside the for loop.
        int count;
        for (count = 0; count < length && text.charAt(length - 1 - count) == c; count++) {}
        return count;
    }

    /**
     * Is this string empty?
     *
     * @param s String to be tested for emptiness.
     * @return true if the string is null or equal to the "" null string. or just blanks
     */
    public static boolean isEmptyTrim(String s) {
        return (s == null) || s.trim().length() == 0;
    } // end isEmpty

    /**
     * Ensure the string contains only legal characters
     *
     * @param candidate string to test.
     * @param legalChars characters than are legal for candidate.
     * @return true if candidate is formed only of chars from the legal set.
     */
    public static boolean isLegal(String candidate, String legalChars) {
        for (int i = 0; i < candidate.length(); i++) {
            if (legalChars.indexOf(candidate.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if char is plain ASCII lower case.
     *
     * @param c char to check
     * @return true if char is in range a..z
     * @see Character#isLowerCase(char)
     */
    public static boolean isUnaccentedLowerCase(char c) {
        return 'a' <= c && c <= 'z';
    } // isUnaccentedLowerCase

    /**
     * Check if char is plain ASCII upper case.
     *
     * @param c char to check.
     * @return true if char is in range A..Z.
     * @see Character#isUpperCase(char)
     */
    public static boolean isUnaccentedUpperCase(char c) {
        return 'A' <= c && c <= 'Z';
    } // end isUnaccentedUpperCase

    /**
     * Pads the string out to the given length by applying blanks on the left.
     *
     * @param s String to be padded/chopped.
     * @param newLen length of new String desired.
     * @param chop true if Strings longer than newLen should be truncated to newLen chars.
     * @return String padded on left/chopped to the desired length.
     */
    public static String leftPad(String s, int newLen, boolean chop) {
        int grow = newLen - s.length();
        if (grow <= 0) {
            if (chop) {
                return s.substring(0, newLen);
            } else {
                return s;
            }
        } else if (grow <= 30) {
            return "                              ".substring(0, grow) + s;
        } else {
            return rep(' ', grow) + s;
        }
    } // end leftPad

    /**
     * convert a String to a long. The routine is very forgiving. It ignores invalid chars, lead
     * trail, embedded spaces, decimal points etc. Dash is treated as a minus sign.
     *
     * @param numStr String to be parsed.
     * @return long value of String with junk characters stripped.
     * @throws NumberFormatException if the number is too big to fit in a long.
     */
    public static long parseDirtyLong(String numStr) {
        numStr = numStr.trim();
        // strip commas, spaces, decimals + etc
        StringBuilder b = new StringBuilder(numStr.length());
        boolean negative = false;
        for (int i = 0; i < numStr.length(); i++) {
            char c = numStr.charAt(i);
            if (c == '-') {
                negative = true;
            } else if ('0' <= c && c <= '9') {
                b.append(c);
            }
        } // end for
        numStr = b.toString();
        if (numStr.length() == 0) {
            return 0;
        }
        long num = Long.parseLong(numStr);
        if (negative) {
            num = -num;
        }
        return num;
    } // end parseDirtyLong

    /**
     * convert a String into long pennies. It ignores invalid chars, lead trail, embedded spaces.
     * Dash is treated as a minus sign. 0 or 2 decimal places are permitted.
     *
     * @param numStr String to be parsed.
     * @return long pennies.
     * @throws NumberFormatException if the number is too big to fit in a long.
     */
    public static long parseLongPennies(String numStr) {
        numStr = numStr.trim();
        // strip commas, spaces, + etc
        StringBuilder b = new StringBuilder(numStr.length());
        boolean negative = false;
        int decpl = -1;
        for (int i = 0; i < numStr.length(); i++) {
            char c = numStr.charAt(i);
            switch (c) {
                case '-':
                    negative = true;
                    break;

                case '.':
                    if (decpl == -1) {
                        decpl = 0;
                    } else {
                        throw new NumberFormatException("more than one decimal point");
                    }
                    break;

                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (decpl != -1) {
                        decpl++;
                    }
                    b.append(c);
                    break;

                default:
                    // ignore junk chars
                    break;
            } // end switch
        } // end for
        if (numStr.length() != b.length()) {
            numStr = b.toString();
        }

        if (numStr.length() == 0) {
            return 0;
        }
        long num = Long.parseLong(numStr);
        if (decpl == -1 || decpl == 0) {
            num *= 100;
        } else if (decpl == 2) {
            /* it is fine as is */
        } else {
            throw new NumberFormatException("wrong number of decimal places.");
        }

        if (negative) {
            num = -num;
        }
        return num;
    } // end parseLongPennies

    /**
     * Print dollar currency, stored internally as scaled int. convert pennies to a string with a
     * decorative decimal point.
     *
     * @param pennies long amount in pennies.
     * @return amount with decorative decimal point, but no lead $.
     */
    public static String penniesToString(long pennies) {
        boolean negative;
        if (pennies < 0) {
            pennies = -pennies;
            negative = true;
        } else {
            negative = false;
        }
        String s = Long.toString(pennies);
        int len = s.length();
        switch (len) {
            case 1:
                s = "0.0" + s;
                break;
            case 2:
                s = "0." + s;
                break;
            default:
                s = s.substring(0, len - 2) + "." + s.substring(len - 2, len);
                break;
        } // end switch
        if (negative) {
            s = "-" + s;
        }
        return s;
    } // end penniesToString

    /**
     * Produce a String of a given repeating character.
     *
     * @param c the character to repeat
     * @param count the number of times to repeat
     * @return String, e.g. rep('*',4) returns "****"
     */
    public static String rep(char c, int count) {
        if (count <= 0) return "";
        char[] s = new char[count];
        for (int i = 0; i < count; i++) {
            s[i] = c;
        }
        return new String(s).intern();
    } // end rep

    /**
     * Pads the string out to the given length by applying blanks on the right.
     *
     * @param s String to be padded/chopped.
     * @param newLen length of new String desired.
     * @param chop true if Strings longer than newLen should be truncated to newLen chars.
     * @return String padded on left/chopped to the desired length.
     */
    public static String rightPad(String s, int newLen, boolean chop) {
        int grow = newLen - s.length();
        if (grow <= 0) {
            if (chop) {
                return s.substring(0, newLen);
            } else {
                return s;
            }
        } else if (grow <= 30) {
            return s + "                              ".substring(0, grow);
        } else {
            return s + rep(' ', grow);
        }
    } // end rightPad

    /**
     * Remove all spaces from a String.
     *
     * @param s String to strip of blanks.
     * @return String with all blanks, lead/trail/embedded removed.
     */
    public static String squish(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.indexOf(' ') < 0) {
            return s;
        }
        int len = s.length();
        // have to use StringBuilder for JDK 1.1
        StringBuilder b = new StringBuilder(len - 1);
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c != ' ') {
                b.append(c);
            }
        } // end for
        return b.toString();
    } // end squish

    /**
     * convert to Book Title case, with first letter of each word capitalised. e.g. "handbook to
     * HIGHER consciousness" -> "Handbook to Higher Consciousness" e.g. "THE HISTORY OF THE U.S.A."
     * -> "The History of the U.S.A." e.g. "THE HISTORY OF THE USA" -> "The History of the Usa"
     * (sorry about that.) Don't confuse this with Character.isTitleCase which concerns ligatures.
     *
     * @param s String to convert. May be any mixture of case.
     * @return String with each word capitalised, except embedded words "the" "of" "to"
     */
    public static String toBookTitleCase(String s) {
        char[] ca = s.toCharArray();
        // Track if we changed anything so that
        // we can avoid creating a duplicate String
        // object if the String is already in Title case.
        boolean changed = false;
        boolean capitalise = true;
        boolean firstCap = true;
        for (int i = 0; i < ca.length; i++) {
            char oldLetter = ca[i];
            if (oldLetter <= '/'
                    || ':' <= oldLetter && oldLetter <= '?'
                    || ']' <= oldLetter && oldLetter <= '`') {
                /* whitespace, control chars or punctuation */
                /* Next normal char should be capitalised */
                capitalise = true;
            } else {
                if (capitalise && !firstCap) {
                    // might be the_ of_ or to_
                    capitalise =
                            !(s.substring(i, Math.min(i + 4, s.length())).equalsIgnoreCase("the ")
                                    || s.substring(i, Math.min(i + 3, s.length()))
                                            .equalsIgnoreCase("of ")
                                    || s.substring(i, Math.min(i + 3, s.length()))
                                            .equalsIgnoreCase("to "));
                } // end if
                char newLetter =
                        capitalise
                                ? Character.toUpperCase(oldLetter)
                                : Character.toLowerCase(oldLetter);
                ca[i] = newLetter;
                changed |= (newLetter != oldLetter);
                capitalise = false;
                firstCap = false;
            } // end if
        } // end for
        if (changed) {
            s = new String(ca);
        }
        return s;
    } // end toBookTitleCase

    /**
     * Convert int to hex with lead zeroes
     *
     * @param h number you want to convert to hex
     * @return 0x followed by unsigned hex 8-digit representation
     */
    public static String toHexString(int h) {
        String s = Integer.toHexString(h);
        if (s.length() < 8) { // pad on left with zeros
            s = "00000000".substring(0, 8 - s.length()) + s;
        }
        return "0x" + s;
    }

    /**
     * Convert an integer to a String, with left zeroes.
     *
     * @param i the integer to be converted
     * @param len the length of the resulting string. Warning. It will chop the result on the left
     *     if it is too long.
     * @return String representation of the int e.g. 007
     */
    public static String toLZ(int i, int len) {
        // Since String is final, we could not add this method there.
        String s = Integer.toString(i);
        if (s.length() > len) {
            /* return rightmost len chars */
            return s.substring(s.length() - len);
        } else if (s.length() < len)
        // pad on left with zeros
        {
            return "000000000000000000000000000000".substring(0, len - s.length()) + s;
        } else {
            return s;
        }
    } // end toLZ

    /**
     * Get #ffffff html hex number for a colour
     *
     * @param c Color object whose html colour number you want as a string
     * @return # followed by 6 hex digits
     * @see #toHexString(int)
     */
    public static String colorToString(Color c) {
        String s = Integer.toHexString(c.getRGB() & 0xffffff);
        if (s.length() < 6) { // pad on left with zeros
            s = "000000".substring(0, 6 - s.length()) + s;
        }
        return '#' + s;
    }

    /**
     * Removes white space from beginning this string.
     *
     * <p>All characters that have codes less than or equal to <code>'&#92;u0020'</code> (the space
     * character) are considered to be white space.
     *
     * @param s String to process. As always the original in unchanged.
     * @return this string, with leading white space removed
     */
    public static String trimLeading(String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        int st = 0;
        while ((st < len) && (s.charAt(st) <= ' ')) {
            st++;
        }
        return (st > 0) ? s.substring(st, len) : s;
    } // end trimLeading

    /**
     * Removes white space from end this string.
     *
     * <p>All characters that have codes less than or equal to <code>'&#92;u0020'</code> (the space
     * character) are considered to be white space.
     *
     * @param s String to process. As always the original in unchanged.
     * @return this string, with trailing white space removed
     */
    public static String trimTrailing(String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        int origLen = len;
        while ((len > 0) && (s.charAt(len - 1) <= ' ')) {
            len--;
        }
        return (len != origLen) ? s.substring(0, len) : s;
    } // end trimTrailing

    public static boolean equals(String in1, String in2) {

        return (in1 == null && in2 == null) || (in1 != null && in1.equals(in2));
    }

    /**
     * Split the string in n parts and return the part number nr. If the part is not found the
     * default value will be returned.
     *
     * @param value
     * @param nr
     * @param def
     * @return the part
     */
    public static String getPart(String value, int nr, String def) {
        return getPart(value, DEFAULT_SEPARATOR, nr, def);
    }

    public static String getPart(String value, int nr) {
        return getPart(value, DEFAULT_SEPARATOR, nr);
    }

    public static String getPart(String value, String separator, int nr, String def) {
        String ret = getPart(value, separator, nr);
        return ret == null ? def : ret;
    }

    public static String getPart(String value, String separator, int nr) {
        if (value == null) return null;
        String[] parts = value.split(separator);
        if (parts.length <= nr) return null;
        return parts[nr];
    }

    /**
     * This will truncate the string at the end. If the string is shorter as length it returns the
     * string itself.
     *
     * @param in String to truncate
     * @param length Max length of the string
     * @return truncated string
     */
    public static String truncate(String in, int length) {
        if (in == null || in.length() <= length) return in;
        return in.substring(0, length);
    }

    /**
     * This will truncate the string, but it also adds three dots (...) at the removed position. The
     * truncation is not at the end at all. The resulted string is a nice representation of the
     * original input. Use this function to display a truncated string.
     *
     * <p>abc...xyz
     *
     * @param in String to truncate
     * @param length Max length of the string
     * @return truncated string
     */
    public static String truncateNice(String in, int length) {
        if (in == null || in.length() <= length) return in;
        if (length < 4) return in.substring(0, length);
        if (length > 8 && in.length() - length > 8)
            return in.substring(0, length - 8) + "..." + in.substring(in.length() - 5);
        if (length > 3) return in.substring(0, length - 3) + "...";
        if (length > 0) return in.substring(0, length);
        return "";
    }

    /**
     * This will truncate the string, but it also adds three dots (...) at the removed position. The
     * truncation is not at the end at all. The resulted string is a nice representation of the
     * original input. Use this function to display a truncated string.
     *
     * <p>abc...xyz
     *
     * @param in String to truncate
     * @param length Max length of the string
     * @param cutPos positive position from left or negative position from right (lesser then -3 is
     *     recommended)
     * @return truncated string
     */
    public static String truncateNice(String in, int length, int cutPos) {
        if (in == null || in.length() <= length) return in;
        if (length < 4) return in.substring(0, length);
        if (cutPos < 0) {
            if (cutPos < -3 && length > -cutPos + 3)
                return in.substring(0, length + cutPos)
                        + "..."
                        + in.substring(in.length() + cutPos + 3);
        } else {
            if (length > cutPos + 3)
                return in.substring(0, cutPos)
                        + "..."
                        + in.substring(in.length() - length + cutPos + 3);
        }
        if (length > 3) return in.substring(0, length - 3) + "...";
        if (length > 0) return in.substring(0, length);
        return "";
    }

    public static String truncateNiceLeft(String in, int length) {
        if (in == null || in.length() <= length) return in;
        int s = in.length();
        if (length < 4) return in.substring(s - length, s);
        return "..." + in.substring(s - length + 3, s);
    }

    /**
     * Search in a string the selected paragraph indicated be empty lines. If the caret position
     * outside the string it will return null.
     *
     * @param text The string, must not be null
     * @param caretPosition Position of the caret.
     * @return the position or null if the input string is null. Return is int[] {start, end}
     */
    public static int[] getSelectedPart(String text, int caretPosition) {
        return getSelectedPart(text, caretPosition, DEFAULT_SELECTION_SEPARATOR);
    }

    public static int[] getSelectedPart(String text, int caretPosition, String[] separators) {
        String s = text;
        if (s == null || caretPosition > text.length()) return null;
        int start = -1;
        int near = -1;
        String usedSep = null;
        for (String separator : separators) {
            int cur = s.lastIndexOf(separator, caretPosition);
            if (cur + separator.length() > near) {
                near = cur + separator.length();
                start = cur;
                usedSep = separator;
            }
        }
        if (start < 0) start = 0;
        else start += usedSep.length();

        int end = Integer.MAX_VALUE;
        for (String separator : separators) {
            int cur = s.indexOf(separator, caretPosition);
            if (cur >= 0) end = Math.min(end, cur);
        }
        if (end < 0) end = s.length();
        //		s = s.substring(start, end);
        return new int[] {start, end};
    }

    /**
     * Returns the string part of the selected part. If the part position is outside the bounds of
     * the string it will be trimmed. If the part position is not correct or the returned string is
     * empty then the default string is returned. Good values for the default are null or an empty
     * string.
     *
     * <p>Use this method with the result of getSelectedPart()
     *
     * @param text The text to crop
     * @param part The position in the text, first element is the begin second element the end of
     *     the selection.
     * @param def Return this if a selection is not possible.
     * @return The selected part of the text.
     */
    public static String getSelection(String text, int[] part, String def) {
        if (text == null || part == null || part.length < 2) return def;
        int start = part[0];
        int end = part[1];
        if (end > text.length()) end = text.length();
        if (start < 0) start = 0;
        if (start >= end) return def;
        return text.substring(start, end);
    }

    /**
     * Replace a text in another text.
     *
     * @param text
     * @param part
     * @param replacement
     * @return new String
     */
    public static String replaceSelection(String text, int[] part, String replacement) {
        if (text == null || part == null || part.length < 2 || replacement == null) return null;

        int start = part[0];
        int end = part[1];
        if (end > text.length()) end = text.length();
        if (start < 0) start = 0;
        if (start >= end) return null;

        return text.substring(0, start) + replacement + text.substring(end);
    }

    /**
     * Append the number of times a character to a string buffer.
     *
     * @param amount number of repeatings
     * @param fill char to fill
     * @param buffer target
     */
    public static void appendRepeating(int amount, char fill, StringBuilder buffer) {
        for (int i = 0; i < amount; i++) buffer.append(fill);
    }

    /**
     * Repeat the character a given times.
     *
     * @param amount number of repeating
     * @param fill char to fill
     * @return string n times the character
     */
    public static String getRepeating(int amount, char fill) {
        StringBuilder buffer = new StringBuilder();
        appendRepeating(amount, fill, buffer);
        return buffer.toString();
    }

    //	@Deprecated // legacy
    //	public static String getRepeatig(int amount, char fill) {
    //		return getRepeating(amount, fill);
    //	}

    /**
     * Return true if the character is in the list of whitespace.
     *
     * @param c
     * @return true if character is defined as whitespace
     */
    public static boolean isWhitespace(char c) {
        for (char w : WHITESPACE) if (w == c) return true;
        return false;
    }

    /**
     * Return the count of the character in the string.
     *
     * @param in
     * @param c
     * @return count of chars or 0 if 'in' is null
     */
    public static int countCharacters(String in, char c) {

        int res = 0;
        if (in == null) return 0;
        for (int i = 0; i < in.length(); i++) {
            if (in.charAt(i) == c) res++;
        }
        return res;
    }

    /**
     * Return the count of the characters in the string.
     *
     * @param in
     * @param c
     * @return count of chars or 0 if 'in' is null
     */
    public static int countCharacters(String in, char[] c) {

        int res = 0;
        if (in == null || c == null) return 0;
        for (char cc : c) {
            res += countCharacters(in, cc);
        }
        return res;
    }

    /**
     * Try to find initials out of the given title. Use the first letters after whitspaces for the
     * initials. The returned string is not in upper letters. If you need Upper Letters append
     * toUpper().
     *
     * @param title The input String
     * @param upperOnly Set true if only collect upper characters in title
     * @param digits Set true if digits (after whitespaces) should also be used.
     * @param maxSize Max size in characters of the initilas or 0 for no limit.
     * @return The initials in mixed letters.
     */
    public static String findInitials(
            String title, boolean upperOnly, boolean digits, int maxSize) {
        if (isEmptyTrim(title)) return "?";
        StringBuilder out = new StringBuilder();
        // find first letters
        boolean isSpace = true;
        for (int i = 0; i < title.length(); i++) {
            char c = title.charAt(i);
            if (isWhitespace(c)) {
                isSpace = true;
            } else {
                if (isSpace) {
                    if ((c >= 'A' && c <= 'Z')
                            || !upperOnly && (c >= 'a' && c <= 'z')
                            || digits && (c >= '0' && c <= '9')) out.append(c);
                    if (maxSize > 0 && out.length() >= maxSize) break;
                }
                isSpace = false;
            }
        }
        return out.toString();
    }

    /**
     * Check a string against all characters in another string. If the first string is not build
     * from characters of the other one the method returns false.
     *
     * @param in
     * @param composed
     * @return true if composed
     */
    public static boolean isComposedOf(String in, String composed) {
        if (in == null || composed == null) return false;
        for (int p = 0; p < in.length(); p++) {
            char c = in.charAt(p);
            if (composed.indexOf(c) < 0) return false;
        }
        return true;
    }

    public static String integerPart(String in) {
        if (in == null) return in;
        in = in.trim();
        for (int p = 0; p < in.length(); p++) {
            char c = in.charAt(p);
            if (!(c >= '0' && c <= '9' || (p == 0 && (c == '-' || c == '+')))) {
                if (p == 0) return null;
                return in.substring(0, p);
            }
        }
        return null;
    }

    public static String beforeIndexOrAll(String _s, char _c) {
        int p = _s.indexOf(_c);
        if (p < 0) return _s;

        return _s.substring(0, p);
    }

    /**
     * Encode a string to be used in an regular expression. Use Pattern.quote instead !
     *
     * @param in String
     * @return Encoded String
     */
    @Deprecated
    public static String regexEncode(String in) {
        return Pattern.quote(in);
    }

    public static String wrap(String in, int length) {
        if (in == null || length < 1 || in.length() < length) return in;
        StringBuilder out = new StringBuilder();
        while (true) {

            if (out.length() != 0) out.append('\n');

            if (in.length() <= length) {
                out.append(in);
                break;
            } else {
                out.append(in.substring(0, length));
                in = in.substring(length);
            }
        }
        return out.toString();
    }

    public static String toHexDump(byte[] bc, int lineSize) {
        if (bc == null) return "";
        StringBuilder out = new StringBuilder();
        int off = 0;
        while (off < bc.length) {
            for (int i = 0; i < lineSize; i++) {
                if (off + i < bc.length) out.append(TCast.toHex2String(bc[off + i]).toUpperCase());
                else out.append("  ");
                if (i % 4 == 3) out.append(" ");
            }
            out.append(" ");
            for (int i = 0; i < lineSize; i++) {
                if (off + i < bc.length) out.append(TCast.toReadableChar((char) bc[off + i]));
            }
            out.append('\n');
            off = off + lineSize;
        }
        return out.toString();
    }

    /**
     * Split string into parts after length is reached.
     *
     * @param in
     * @param length
     * @return the spit string into an array
     */
    public static String[] split(String in, int length) {
        if (in == null) return new String[0];
        LinkedList<String> out = new LinkedList<>();
        while (in.length() > length) {
            out.add(in.substring(0, length));
            in = in.substring(length);
        }
        if (in.length() > 0) out.add(in);
        return out.toArray(new String[out.size()]);
    }

    /**
     * Split string into parts after length is reached.
     *
     * @param in
     * @param length
     * @return the split string into a collection
     */
    public static Collection<String> splitCollection(String in, int length) {
        if (in == null) return new ArrayList<>(0);
        LinkedList<String> out = new LinkedList<>();
        while (in.length() > length) {
            out.add(in.substring(0, length));
            in = in.substring(length);
        }
        if (in.length() > 0) out.add(in);
        return out;
    }

    public static Throwable serialize(StringBuilder sb, Object o, Throwable error) {
        try {
            if (o == null) {
                sb.append("[null]");
            } else if (o instanceof Throwable) {
                if (error == null) return (Throwable) o;
                // another error
                sb.append("[").append(o).append("]");
            } else if (o.getClass().isArray()) {
                sb.append("[");
                boolean first = true;
                for (Object p : (Object[]) o) {
                    if (first) first = false;
                    else sb.append(",");
                    error = serialize(sb, p, error);
                }
                sb.append("]");
            } else sb.append("[").append(o).append("]");
        } catch (Exception t) {
        }
        return error;
    }

    public static Throwable serialize(StringBuilder sb, Object[] msg, int maxMsgSize) {
        return serialize(sb, msg, maxMsgSize, null);
    }

    public static Throwable serialize(
            StringBuilder sb, Object[] msg, int maxMsgSize, List<String> exceptions) {
        Throwable error = null;
        if (msg == null) return null;
        for (Object o : msg) {
            error = serialize(sb, o, error);
            if (maxMsgSize > 0 && sb.length() > maxMsgSize) {
                // check for exceptions
                if (exceptions != null) {
                    for (String exception : exceptions)
                        if (sb.indexOf(exception) > -1) {
                            maxMsgSize = Integer.MAX_VALUE;
                            continue;
                        }
                }
                sb.setLength(maxMsgSize);
                sb.append("...");
                break;
            }
        }
        return error;
    }

    /**
     * Search for a pattern and cut the string after size characters. If the string is too short it
     * will be filled with fill characters.
     *
     * @param in The string to cut
     * @param pattern The pattern to search for (not regex!)
     * @param fill Fill character or 0 to disable
     * @param size size after (or negative before) the found pattern
     * @return The cutted string
     */
    public static String cutAfter(String in, String pattern, char fill, int size) {
        int p = in.indexOf(pattern);
        if (p < 0) return in;
        p = p + size;
        if (p <= 0) return "";
        while (in.length() < p) {
            // not efficient
            if (fill == 0) return in;
            in = in + fill;
        }
        return in.substring(0, p);
    }

    public static String toAscii127(String in) {
        if (in == null) return null;
        StringBuilder out = null;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c > 127) {
                if (out == null) {
                    out = new StringBuilder();
                    out.append(in.substring(0, i));
                }
                String s = toAscii127(c);
                out.append(s);
            } else if (out != null) out.append(c);
        }
        return out == null ? in : out.toString();
    }

    public static String toAscii127(char c) {
        if (c <= 127) return String.valueOf(c);
        boolean isUpper = Character.isUpperCase(c);
        if (isUpper) c = Character.toLowerCase(c);
        for (Map.Entry<String, String> map : ASCII127_MAPPING.entrySet()) {
            if (map.getValue().indexOf(c) > -1) {
                String out = map.getKey();
                if (isUpper) {
                    if (out.length() == 1) out = out.toUpperCase();
                    else out = out.substring(0, 1).toUpperCase() + out.substring(1);
                }
                return out;
            }
        }
        return String.valueOf(c);
    }

    /**
     * Return true if the character is a-z or A-Z
     *
     * @param c
     * @return true if alphabetical
     */
    public static boolean isAlphabeticalAscii(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }
    /**
     * Return true if c is between 0-9
     *
     * @param c
     * @return True if digit
     */
    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    /**
     * A lighter version of String.valueOf(Object). It will not create a new object if the object is
     * already a string.
     *
     * @param in Transform this to string
     * @return null or the string representation
     */
    public static String valueOf(Object in) {
        if (in == null) return null;
        if (in instanceof String) return (String) in;
        return in.toString();
    }

    /**
     * Doing a substring like String.substr() but will ever return a string and never throw an
     * exception. If something is wrong like in is null or from is out of scope it will return an
     * empty string.
     *
     * @param in String to do an substring for
     * @param from start index, if smaller zero it will be set to 0
     * @param to end index, if smaller then or zero it will be set to in 0
     * @return The substring or an empty string
     */
    public static String substr(String in, int from, int to) {
        if (in == null) return "";
        if (from < 0) from = 0;
        if (to < 0) to = 0;
        if (from > to) return "";
        if (from > in.length()) return "";
        if (to > in.length()) to = in.length();
        return in.substring(from, to);
    }

    /**
     * Doing a substring like String.substr() but will ever return a string and never throw an
     * exception. If something is wrong like in is null or from is out of scope it will return an
     * empty string.
     *
     * @param in String to do an substring for
     * @param from start index, if smaller zero it will be set to length minus from + 1
     * @param to end index, if smaller then or zero it will be set to in length minus to + 1
     * @return The substring or an empty string
     */
    public static String substrext(String in, int from, int to) {
        if (in == null) return "";
        if (from < 0) from = in.length() + from + 1;
        if (to < 0) to = in.length() + to + 1;
        if (from > to) return "";
        if (from > in.length()) return "";
        if (to > in.length()) to = in.length();
        return in.substring(from, to);
    }

    public static String toByteDisplayString(long bytes) {
        if (bytes < 2048) return bytes + " B";
        if (bytes < (1024l * 1024l)) return bytes / 1024 + " KB";
        if (bytes < (1024l * 1024l * 1024l))
            return truncateDecimals((double) bytes / 1024d / 1024d, 2) + " MB";
        if (bytes < (1024l * 1024l * 1024l * 1024l))
            return truncateDecimals((double) bytes / 1024d / 1024d / 1024d, 2) + " GB";
        if (bytes < (1024l * 1024l * 1024l * 1024l * 1024l))
            return truncateDecimals((double) bytes / 1024d / 1024d / 1024d / 1024d, 2) + " TB";
        if (bytes < (1024l * 1024l * 1024l * 1024l * 1024l * 1024l))
            return truncateDecimals((double) bytes / 1024d / 1024d / 1024d / 1024d / 1024d, 2)
                    + " PB"; // Peta
        if (bytes < (1024l * 1024l * 1024l * 1024l * 1024l * 1024l * 1024l))
            return truncateDecimals(
                            (double) bytes / 1024d / 1024d / 1024d / 1024d / 1024d / 1024d, 2)
                    + " EB"; // Exa
        if (bytes < (1024l * 1024l * 1024l * 1024l * 1024l * 1024l * 1024l * 1024l))
            return truncateDecimals(
                            (double) bytes / 1024d / 1024d / 1024d / 1024d / 1024d / 1024d / 1024d,
                            2)
                    + " ZB"; // Zetta
        // if (bytes < 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024)
        return truncateDecimals(
                        (double) bytes
                                / 1024d
                                / 1024d
                                / 1024d
                                / 1024d
                                / 1024d
                                / 1024d
                                / 1024d
                                / 1024d,
                        2)
                + " YB"; // Yotta
    }

    private static double truncateDecimals(double d, int len) {
        if (len <= 0) return (int) d;
        long p = 1;
        switch (len) {
            case 1:
                p = 10;
                break;
            case 2:
                p = 100;
                break;
            case 3:
                p = 1000;
                break;
            case 4:
                p = 10000;
                break;
            default:
                p = pow(10, len);
        }
        long l = (long) (d * p);
        return (double) l / (double) p;
    }

    private static long pow(long a, int b) {
        long result = 1;
        for (int i = 1; i <= b; i++) {
            result *= a;
        }
        return result;
    }
}
