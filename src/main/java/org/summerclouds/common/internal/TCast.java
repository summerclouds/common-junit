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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.summerclouds.common.internal.cast.Caster;
import org.summerclouds.common.internal.cast.DoubleToString;
import org.summerclouds.common.internal.cast.FloatToString;
import org.summerclouds.common.internal.cast.ObjectToBigDecimal;
import org.summerclouds.common.internal.cast.ObjectToBoolean;
import org.summerclouds.common.internal.cast.ObjectToByte;
import org.summerclouds.common.internal.cast.ObjectToCalendar;
import org.summerclouds.common.internal.cast.ObjectToDate;
import org.summerclouds.common.internal.cast.ObjectToDouble;
import org.summerclouds.common.internal.cast.ObjectToFloat;
import org.summerclouds.common.internal.cast.ObjectToInteger;
import org.summerclouds.common.internal.cast.ObjectToLong;
import org.summerclouds.common.internal.cast.ObjectToShort;
import org.summerclouds.common.internal.cast.ObjectToSqlDate;
import org.summerclouds.common.internal.cast.ObjectToString;
import org.summerclouds.common.internal.cast.ObjectToUUID;

/**
 * Smplifies casts between java classes. Some functions in this class only make the code readable.
 * e.g. from string to int.
 *
 * <p>All Funktions are static.
 *
 * @author jesus
 */
public final class TCast {

    //	private static Log log = Log.getLog(MCast.class);
    //	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
    //			"yyyy-MM-dd_HH:mm:ss.SSS z");

    private static final char[] HEX_CHAR_TABLE = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private static TVectorMap<Class<?>, Class<?>, Caster<?, ?>> casters = new TVectorMap<>();

    private static final ObjectToBoolean OBJECT_TO_BOOLEAN = new ObjectToBoolean();
    private static final ObjectToInteger OBJECT_TO_INTEGER = new ObjectToInteger();
    private static final ObjectToByte OBJECT_TO_BYTE = new ObjectToByte();
    private static final ObjectToShort OBJECT_TO_SHORT = new ObjectToShort();
    private static final ObjectToLong OBJECT_TO_LONG = new ObjectToLong();
    private static final ObjectToDouble OBJECT_TO_DOUBLE = new ObjectToDouble();
    private static final ObjectToFloat OBJECT_TO_FLOAT = new ObjectToFloat();
    private static final ObjectToCalendar OBJECT_TO_CALENDAR = new ObjectToCalendar();
    private static final ObjectToDate OBJECT_TO_DATE = new ObjectToDate();
    private static final ObjectToSqlDate OBJECT_TO_SQLDATE = new ObjectToSqlDate();
    private static final DoubleToString DOUBLE_TO_STRING = new DoubleToString();
    private static final FloatToString FLOAT_TO_STRING = new FloatToString();
    private static final ObjectToString OBJECT_TO_STRING = new ObjectToString();
    private static final ObjectToBigDecimal OBJECT_TO_BIGDECIMAL = new ObjectToBigDecimal();

    static {
        addCaster(OBJECT_TO_BOOLEAN, true);
        addCaster(OBJECT_TO_INTEGER, true);
        addCaster(OBJECT_TO_BYTE, true);
        addCaster(OBJECT_TO_SHORT, true);
        addCaster(OBJECT_TO_LONG, true);
        addCaster(OBJECT_TO_DOUBLE, true);
        addCaster(OBJECT_TO_FLOAT, true);
        addCaster(OBJECT_TO_STRING, true);
        addCaster(OBJECT_TO_DATE, true);
        addCaster(OBJECT_TO_SQLDATE, true);
        addCaster(OBJECT_TO_CALENDAR, true);
        addCaster(DOUBLE_TO_STRING, true);
        addCaster(FLOAT_TO_STRING, true);
        addCaster(OBJECT_TO_BIGDECIMAL, true);
        addCaster(new ObjectToUUID(), true);
    }
    /**
     * Will round the value mathematically and return every time a comma as separator and two digits
     * after comma.
     *
     * @param _in
     * @return nice string
     */
    public static String toCurrencyString(double _in) {

        // round
        _in = Math.round(_in * 100d) / 100d;
        // out
        StringBuilder out = new StringBuilder();
        out.append(_in);

        // change "." to ","
        int pos = out.indexOf(".");
        if (pos >= 0) out.setCharAt(pos, ',');
        else pos = out.indexOf(",");

        if (pos <= 0) out.append(",00");
        else if (out.length() - pos <= 2) out.append("0");

        return out.toString();
    }

    public static void addCaster(Caster<?, ?> caster, boolean overwrite) {
        if (!overwrite && casters.containsKey(caster.getFromClass(), caster.getToClass())) return;
        casters.put(caster.getFromClass(), caster.getToClass(), caster);
    }

    /**
     * Try to parse a String and return the equivalent Date object. The string should contain a iso
     * date string like "yyyy-mm-dd" syntax: "yyyy-mm-dd[[ HH:MM:SS].xxx]" where xxx is Millisecond.
     * Milliseconds are ignored. For the date part there are alternative syntax: "dd.mm.yyyy" or
     * "mm/dd/yyyy". Or a timestamp.
     *
     * <p>If the time is not in the string, it will be set to "00:00:00". It is possible to leave
     * year, in this case it will be replaced with the actuall year. If you leave month, it will be
     * replaced with the actuall month.
     *
     * @param in
     * @param def
     * @return In all cases an Date() object. Is getTime() is 0, it occurs an error: ACast.toDate(
     *     in ).getTime == 0.
     */
    public static Date toDate(Object in, Date def) {
        return OBJECT_TO_DATE.cast(in, def);
    }

    public static Date toDate(Object in, Date def, Locale locale) {
        return OBJECT_TO_DATE.cast(in, def, locale);
    }

    public static Calendar toCalendar(Object in) {
        return OBJECT_TO_CALENDAR.cast(in, null);
    }

    public static Calendar toCalendar(Date _in, TimeZone tz, Locale l) {
        Calendar calendar = Calendar.getInstance(tz, l);
        calendar.setTime(_in);
        return calendar;
    }

    /**
     * Parse a time date string.
     *
     * @param in
     * @return the calendar
     */
    public static Calendar toCalendar(String in) {
        return OBJECT_TO_CALENDAR.cast(in, null);
    }

    /**
     * Parse time and date and return calendar.
     *
     * @param in
     * @param def
     * @return the calendar
     */
    public static Calendar toCalendar(String in, Calendar def) {
        return OBJECT_TO_CALENDAR.cast(in, def);
    }

    /**
     * Return the date as string with format: yyyy-MM-dd_HH:mm:ss.SSS using a date formater.
     *
     * @param _in
     * @return
     */
    //	public static String toString(Date _in) {
    //		if (_in == null) return null;
    //		// return _in.getDate() + "." + (_in.getMonth()+1) + "." +
    //		// (_in.getYear() + 1900 );
    //		synchronized (dateFormat) {
    //			return dateFormat.format(_in);
    //		}
    //	}

    /**
     * Return the date as string with format: yyyy-MM-dd_HH:mm:ss.SSS using a date formater.
     *
     * @param _in
     * @return
     */
    //	public static String toString(Calendar _in) {
    //		if (_in == null) return null;
    //		synchronized (dateFormat) {
    //			return dateFormat.format(_in.getTime());
    //		}
    //	}

    /**
     * Convert the byte array to a string representation. It stores for every byte a two letter hex
     * value in the string.
     *
     * @param in
     * @return as binary
     */
    public static String toBinaryString(byte[] in) {
        char[] hex = new char[2 * in.length];
        int index = 0;

        for (byte b : in) {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex);
    }

    /**
     * Convert a string with hex values in a byte array.
     *
     * @param in
     * @return the array
     */
    public static byte[] fromBinaryString(String in) {
        byte[] out = new byte[in.length() / 2];
        for (int i = 0; i < out.length; i++) out[i] = hexToByte(in, i * 2);
        return out;
    }

    /**
     * Convert a two letter hex value to a single byte value.
     *
     * @param in
     * @param offset
     * @return a byte
     */
    public static byte hexToByte(String in, int offset) {
        int i = Integer.parseInt(in.substring(offset, offset + 2), 16);
        byte b = (byte) (i & 0xFF);
        return b;
    }

    /**
     * Convert a byte to a two letter hex value.
     *
     * @param in
     * @return hex string
     */
    public static String toHex2String(byte in) {
        char[] hex = new char[2];

        int v = in & 0xFF;
        hex[0] = HEX_CHAR_TABLE[v >>> 4];
        hex[1] = HEX_CHAR_TABLE[v & 0xF];

        return new String(hex);
    }

    /**
     * Convert a hex string e.g. from DM5() to a byte array. Every two characters will be converted
     * to one signed byte (-128 to 127)
     *
     * @param s The hex encoded string
     * @return The resulting byte array
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] =
                    (byte)
                            ((Character.digit(s.charAt(i), 16) << 4)
                                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Converts an array of bytes into a hex string representation. Every byte will be presented by
     * two characters.
     *
     * @param bytes Array of bytes
     * @return Hex String
     */
    public static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String out = formatter.toString();
        formatter.close();
        return out;
    }

    /**
     * Convert String to boolean. If the conversion was not possible it returns "_default".
     *
     * <p>Valid true values: yes, true, ja, 1, t
     *
     * <p>valid false values: no, false, nein, 0, f
     *
     * @param _in
     * @param _default
     * @return a boolean
     */
    public static boolean toboolean(Object _in, boolean _default) {
        return OBJECT_TO_BOOLEAN.toBoolean(_in, _default, null);
    }

    /**
     * Convert a string to float. If the string is malformed it returns "_def".
     *
     * @param in
     * @param def
     * @return a float
     */
    public static float tofloat(Object in, float def) {
        return OBJECT_TO_FLOAT.toFloat(in, def, null);
    }

    /**
     * Convert a string to double. If the string is malformed it returns "def". This method will use
     * the default double format without thousands separator and a dot as decimal separator.
     *
     * @param in
     * @param def
     * @return a double
     */
    public static double todouble(Object in, double def) {
        return OBJECT_TO_DOUBLE.toDouble(in, def, null);
    }

    public static BigDecimal toBigDecimal(Object in, BigDecimal def) {
        return OBJECT_TO_BIGDECIMAL.cast(in, def, null);
    }

    /**
     * Convert a string to double. If the string is malformed it returns "def". This method will use
     * the european format expect GB. As thousands separator space or dot and as decimal comma
     * separator. 4 294 967.295,000
     *
     * <p>Reference to http://docs.oracle.com/cd/E19455-01/806-0169/overview-9/index.html
     *
     * @param in
     * @param def
     * @return a double
     */
    public static double todoubleEuropean(String in, double def) {
        if (in == null) return def;
        in = in.replaceAll(" ", "").replaceAll("\\.", "").replace(',', '.');
        return OBJECT_TO_DOUBLE.toDouble(in, def, null);
    }

    /**
     * Convert a string to double. If the string is malformed it returns "def". This method will use
     * the US and GB format. As thousands separator space or comma and as decimal dot separator.
     * 4,294,967,295.00
     *
     * <p>Reference to http://docs.oracle.com/cd/E19455-01/806-0169/overview-9/index.html
     *
     * @param in
     * @param def
     * @return a double
     */
    public static double todoubleUS(String in, double def) {
        if (in == null) return def;
        in = in.replaceAll(" ", "").replaceAll(",", "");
        return OBJECT_TO_DOUBLE.toDouble(in, def, null);
    }

    /**
     * Convert a string to double. If the string is malformed it returns "def". This method will use
     * NumberFormat to parse the string.
     *
     * @param in
     * @param def
     * @param locale Locale or null for default locale
     * @return a double
     */
    public static double todouble(String in, double def, Locale locale) {
        if (in == null) return def;
        if (locale == null) locale = Locale.getDefault();
        try {
            NumberFormat nf = NumberFormat.getInstance(locale);
            return nf.parse(in).doubleValue();
        } catch (Throwable t) {
            return def;
        }
    }

    /**
     * Converts String to int. If the string is malformed then it returns "_def". A valid format is
     * also the hex 0x (e.g. 0xFFFF) variant.
     *
     * @param in
     * @param def
     * @return an integer
     */
    public static int toint(Object in, int def) {
        if (OBJECT_TO_INTEGER == null) {
            try {
                return Integer.parseInt(String.valueOf(in));
            } catch (Throwable t) {
                return def;
            }
        }
        return OBJECT_TO_INTEGER.toInt(in, def, null);
    }

    /**
     * Converts a string to long. If the string is malformed then it returns "_def". A valid format
     * is also the hex 0x (e.g. 0xFFFF) variant.
     *
     * @param in
     * @param def
     * @return a long
     */
    public static long tolong(Object in, long def) {
        if (OBJECT_TO_LONG == null) {
            try {
                return Long.parseLong(String.valueOf(in));
            } catch (Throwable t) {
                return def;
            }
        }
        return OBJECT_TO_LONG.toLong(in, def, null);
    }

    public static byte tobyte(Object in, byte def) {
        return OBJECT_TO_BYTE.toByte(in, def, null);
    }

    public static short toshort(Object in, short def) {
        return OBJECT_TO_SHORT.toShort(in, def, null);
    }

    /**
     * Convert a double to string. The separator is a dot.
     *
     * @param in
     * @return corresponding string
     */
    public static String toString(double in) {
        return DOUBLE_TO_STRING.toString(in);
    }

    /**
     * Convert a double to string. The separator is a dot.
     *
     * @param in
     * @return corresponding string
     */
    public static String toString(float in) {
        return FLOAT_TO_STRING.toString(in);
    }

    /**
     * Convert a boolean to string. Values are "true", "false".
     *
     * @param _in
     * @return corresponding string
     */
    public static String toString(boolean _in) {
        if (_in) return "true";
        else return "false";
    }

    /**
     * Converts integer to String.
     *
     * @param _in
     * @return corresponding string
     */
    public static String toString(int _in) {
        return Integer.toString(_in);
    }

    /**
     * Converts integer to string with the minimum digits.
     *
     * @param _in
     * @param _digits
     * @return corresponding string
     */
    public static String toString(int _in, int _digits) {
        StringBuilder out = new StringBuilder().append(Integer.toString(_in));
        while (out.length() < _digits) out.insert(0, '0');
        return out.toString();
    }

    /**
     * Converts integer to string with the minimum digits.
     *
     * @param _in
     * @param _digits
     * @return corresponding string
     */
    public static String toString(long _in, int _digits) {
        StringBuilder out = new StringBuilder().append(Long.toString(_in));
        while (out.length() < _digits) out.insert(0, '0');
        return out.toString();
    }

    /**
     * Convert long to string.
     *
     * @param _in
     * @return corresponding string
     */
    public static String toString(long _in) {
        return String.valueOf(_in);
    }

    /**
     * Convert integer to two letter hex code. Ignores negative values.
     *
     * @param _in
     * @return corresponding string as hex
     */
    public static String toHex2String(int _in) {
        String out = Integer.toHexString(_in).toUpperCase();
        if (out.length() == 1) out = "0" + out;
        return out;
    }

    public static String toHex2LowerString(int _in) {
        String out = Integer.toHexString(_in);
        if (out.length() == 1) out = "0" + out;
        return out;
    }

    /**
     * Convert integer to four letter hex code. Ignores negative values.
     *
     * @param _in
     * @return corresponding string as hex
     */
    public static String toHex4String(int _in) {
        return toHex2String(_in / 256) + toHex2String(_in % 256);
    }

    /**
     * Put all list elements in a string list. Use the toString method.
     *
     * @param _v
     * @return corresponding string array, never null
     */
    public static String[] toStringArray(List<?> _v) {
        if (_v == null) return new String[0];
        String[] out = new String[_v.size()];
        for (int i = 0; i < _v.size(); i++) {
            Object o = _v.get(i);
            if (o == null) out[i] = null;
            else if (o instanceof String) out[i] = (String) o;
            else if (o instanceof Object[]) out[i] = Arrays.deepToString((Object[]) o);
            else out[i] = o.toString();
        }
        return out;
    }

    public static int tointFromHex(String _in) {

        int out = 0;
        for (int i = 0; i < _in.length(); i++) {
            int x = 0;
            char c = _in.charAt(i);
            if (c >= '0' && c <= '9') x = (c - '0');
            else if (c >= 'a' && c <= 'f') x = (c - 'a' + 10);
            else if (c >= 'A' && c <= 'F') x = (c - 'A' + 10);
            else throw new NumberFormatException(_in);
            out = out * 16 + x;
        }

        return out;
    }

    /**
     * Returns a comma separated list of hex values as string. Not the corresponding char set
     * decoded string.
     *
     * @param in
     * @return List of hex values
     */
    public static String toString(byte[] in) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < in.length; i++) {
            if (i != 0) sb.append(',');
            sb.append(Byte.toString(in[i]));
        }
        return sb.toString();
    }

    public static byte[] toByteArray(String in) {

        if (in.length() == 0) return new byte[0];

        int offset = 0;
        int cnt = 0;

        while ((offset = in.indexOf(',', offset + 1)) >= 0) {
            cnt++;
        }

        offset = 0;
        int old = 0;
        byte[] out = new byte[cnt + 1];
        cnt = 0;
        while ((offset = in.indexOf(',', offset + 1)) >= 0) {
            out[cnt] = Byte.parseByte(in.substring(old, offset));
            cnt++;
            old = offset + 1;
        }

        out[cnt] = Byte.parseByte(in.substring(old));

        return out;
    }

    public static String toString(String firstLine, StackTraceElement[] trace) {
        StringBuilder sb = new StringBuilder();
        if (firstLine != null) sb.append(firstLine).append('\n');
        if (trace == null) return sb.toString();

        for (int i = 0; i < trace.length; i++)
            sb.append("\tat ")
                    .append(trace[i].getClassName())
                    .append('.')
                    .append(trace[i].getMethodName())
                    .append('(')
                    .append(trace[i].getFileName())
                    .append(':')
                    .append(trace[i].getLineNumber())
                    .append(")\n");
        return sb.toString();
    }

    /**
     * Return an indexed map of the values. The first value has the index "0" and so on.
     *
     * @param values
     * @return corresponding map
     */
    public static Map<String, Object> toIndexedMap(Object... values) {
        HashMap<String, Object> out = new HashMap<String, Object>();
        for (int i = 0; i < values.length; i++) {
            out.put(toString(i), values[i]);
        }
        return out;
    }

    public static String objectToString(Object value) {

        if (value == null) return null;

        if (value instanceof Integer) return toString((Integer) value);
        if (value instanceof Long) return toString((Long) value);
        if (value instanceof Double) return toString((Double) value);
        if (value instanceof Float) return toString((Float) value);
        if (value instanceof Date) return toString((Date) value);
        if (value instanceof Calendar) return toString((Calendar) value);

        return value.toString();
    }

    public static Date objectToDate(Object value) {
        if (value == null) return null;
        if (value instanceof Date) return (Date) value;
        if (value instanceof Calendar) return ((Calendar) value).getTime();
        return toDate(String.valueOf(value), null);
    }

    public static java.sql.Date toSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static String toString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    /**
     * Returns a string in every case even if input is null it returns an empty string.
     *
     * @param in
     * @return corresponding string
     */
    public static String toString(Object in) {
        if (in == null) return "";
        return OBJECT_TO_STRING.cast(in, "");
    }

    public static String toString(Object in, String def) {
        if (in == null) return def;
        return OBJECT_TO_STRING.cast(in, def);
    }

    @SuppressWarnings("unchecked")
    public static Object toType(Object in, Class<?> type, Object def) {
        // if null -> return null
        if (in == null) return def;
        // if it's the same type -> return itself
        if (type.isInstance(in)) return in;

        if (type.isArray()) {
            // create new array from in...
            if (in.getClass().isArray()) {
                Class<?> arrayType = type.getComponentType();
                int length = Array.getLength(in);
                Object out = Array.newInstance(arrayType, length);
                // System.arraycopy(in, 0, out, 0, length);
                for (int i = 0; i < length; i++) {
                    Object value = Array.get(in, i);
                    value = toType(value, arrayType, null);
                    Array.set(out, i, value);
                }
                return out;
            }

            if (in instanceof String) {
                String[] parts = ((String) in).split(";");
                Class<?> arrayType = type.getComponentType();
                int length = parts.length;
                Object out = Array.newInstance(arrayType, length);
                // System.arraycopy(in, 0, out, 0, length);
                for (int i = 0; i < length; i++) {
                    Object value = parts[i];
                    value = toType(value, arrayType, null);
                    Array.set(out, i, value);
                }
                return out;
            }

            if (in instanceof Collection<?>) {
                Collection<?> c = (Collection<?>) in;
                Class<?> arrayType = type.getComponentType();
                int length = c.size();
                Object out = Array.newInstance(arrayType, length);
                // System.arraycopy(in, 0, out, 0, length);
                Iterator<?> iter = c.iterator();
                for (int i = 0; i < length; i++) {
                    Object value = iter.next();
                    value = toType(value, arrayType, null);
                    Array.set(out, i, value);
                }
                return out;
            }

            return def;
        }

        if (type.isEnum()) {
            String strValue = String.valueOf(in);
            for (Object c : type.getEnumConstants()) if (c.toString().equals(strValue)) return c;
            return def;
        }

        // is there a exact caster for the from-to pair ?
        Caster<?, ?> caster = casters.get(in.getClass(), type);
        if (caster == null) {

            // not, first try to cast primitives

            if (String.class.isAssignableFrom(type)) return toString(in);
            if (boolean.class.isAssignableFrom(type))
                return toboolean(in, def == null ? false : toboolean(def, false));
            if (int.class.isAssignableFrom(type)) return toint(in, def == null ? 0 : toint(def, 0));
            if (long.class.isAssignableFrom(type))
                return tolong(in, def == null ? 0 : tolong(def, 0));
            if (double.class.isAssignableFrom(type))
                return todouble(in, def == null ? 0 : todouble(def, 0));
            if (byte.class.isAssignableFrom(type))
                return tobyte(in, def == null ? 0 : tobyte(def, (byte) 0));
            if (short.class.isAssignableFrom(type))
                return toshort(in, def == null ? 0 : toshort(def, (short) 0));
            if (float.class.isAssignableFrom(type))
                return tofloat(in, def == null ? 0 : tofloat(def, (short) 0));

            if (type == Map.class) return toMap(in, def);
            if (type == List.class) return toList(in, def);
            // if not found find a default caster (from = Object)

            caster = casters.get(Object.class, type);
        }

        // default also not found -> return default value
        if (caster == null) return def;

        // if found a caster -> cast !
        return ((Caster<Object, Object>) caster).cast(in, def);
    }

    public static Object toList(Object in, Object def) {
        if (in == null) return def;
        String s = String.valueOf(in);
        if (!s.startsWith("[") || !s.endsWith("]")) return def;
        s = s.substring(1, s.length() - 1);
        String[] parts = s.split(",");
        LinkedList<String> out = new LinkedList<>();
        for (String item : parts) out.add(item.trim());
        return out;
    }

    public static Object toMap(Object in, Object def) {
        if (in == null) return def;
        String s = String.valueOf(in);
        if (!s.startsWith("{") || !s.endsWith("}")) return def;
        s = s.substring(1, s.length() - 1);
        String[] parts = s.split(",");
        HashMap<String, String> out = new HashMap<>();
        for (String item : parts) {
            int pos = item.indexOf('=');
            if (pos >= 0) out.put(item.substring(0, pos), item.substring(pos + 1));
        }
        return out;
    }

    /**
     * Return 0 in 7 flavors or null
     *
     * @param type
     * @return the default value for the class
     */
    public static Object getDefaultPrimitive(Class<?> type) {
        if (type == null) return null;
        if (type.isArray()) {
            type = type.getComponentType();
            //			if (type == int.class)
            //				return new int[0];
            //			if (type == long.class)
            //				return new long[0];
            //			if (type == short.class)
            //				return new short[0];
            //			if (type == double.class)
            //				return new double[0];
            //			if (type == float.class)
            //				return new float[0];
            //			if (type == byte.class)
            //				return new byte[0];
            //			if (type == boolean.class)
            //				return new boolean[0];
            //			if (type == char.class)
            //				return new char[0];
            return Array.newInstance(type, 0);
        }

        if (type == int.class) return (Integer) 0;
        if (type == long.class) return (Long) 0L;
        if (type == short.class) return (Short) (short) 0;
        if (type == double.class) return (Double) 0d;
        if (type == float.class) return (Float) 0f;
        if (type == byte.class) return (Byte) (byte) 0;
        if (type == boolean.class) return (Boolean) false;
        if (type == char.class) return ' ';
        return null;
    }

    public static Object getDefaultPrimitive(String type) {
        if (type == null) return null;
        if (type.endsWith("[]")) {
            type = type.substring(0, type.length() - 2);
            if ("int".equals(type)) return new int[0];
            if ("long".equals(type)) return new long[0];
            if ("short".equals(type)) return new short[0];
            if ("double".equals(type)) return new double[0];
            if ("float".equals(type)) return new float[0];
            if ("byte".equals(type)) return new byte[0];
            if ("boolean".equals(type)) return new boolean[0];
            if ("char".equals(type)) return new char[0];
            return null;
        }

        if ("int".equals(type)) return (Integer) 0;
        if ("long".equals(type)) return (Long) 0L;
        if ("short".equals(type)) return (Short) (short) 0;
        if ("double".equals(type)) return (Double) 0d;
        if ("float".equals(type)) return (Float) 0f;
        if ("byte".equals(type)) return (Byte) (byte) 0;
        if ("boolean".equals(type)) return (Boolean) false;
        if ("char".equals(type)) return ' ';
        return null;
    }

    public static byte[] toBinary(Object value) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(value);
        oos.close();
        bos.close();
        return bos.toByteArray();
    }

    public static Object fromBinary(byte[] bin) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bin);
        TObjectInputStream ois = new TObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        bis.close();
        return obj;
    }

    public static <E extends Enum<E>> E toEnum(Object value, E def) {
        if (value == null) return def;
        String str = String.valueOf(value).trim().toUpperCase();
        try {
            return Enum.valueOf(def.getDeclaringClass(), str);
        } catch (Throwable t) {
        }
        return def;
    }

    public static String toReadableChar(char b) {
        if (b == 13) return "<BR>";
        if (b == 10) return "<CR>";
        if (b == '\t') return "<TAB>";
        if (b < ' ') return "<" + TCast.toHex2String(b).toUpperCase() + ">";
        return new String(new char[] {b});
    }

    public static Object unserializeFromString(String value, ClassLoader cl)
            throws IOException, ClassNotFoundException {
        if (value == null) return null;
        byte[] bytes = fromBinaryString(value);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        @SuppressWarnings("resource")
        TObjectInputStream ois = new TObjectInputStream(bais, cl);
        return ois.readObject();
    }

    public static String serializeToString(Object value) throws IOException {
        if (value == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(value);
        oos.flush();
        oos.close();
        byte[] bytes = baos.toByteArray();
        return toBinaryString(bytes);
    }

    private enum UNIT {
        Z,
        E,
        P,
        T,
        G,
        M,
        K,
        X
    }

    private static final int UNIT_MAX = UNIT.values().length - 1;

    public static String toUnit(long p) {
        boolean negative = false;
        if (p < 0) {
            negative = true;
            p = -p;
        }
        int unitId = UNIT_MAX;
        while (p > 100000 && unitId > 0) {
            p = p / 1000;
            unitId--;
        }
        if (unitId > 0 && p > 1000) {
            unitId--;
            double d = p / 1000d;
            return (negative ? "-" : "") + d + (unitId == UNIT_MAX ? "" : UNIT.values()[unitId]);
        }
        return (negative ? "-" : "") + p + (unitId == UNIT_MAX ? "" : UNIT.values()[unitId]);
    }

    public static String toByteUnit(long p) {
        boolean negative = false;
        if (p < 0) {
            negative = true;
            p = -p;
        }
        int unitId = UNIT_MAX;
        while (p > 100000 && unitId > 0) {
            p = p / 1024;
            unitId--;
        }
        if (unitId > 0 && p > 1000) {
            unitId--;
            double d = p / 1000d;
            return (negative ? "-" : "") + d + (unitId == UNIT_MAX ? "" : UNIT.values()[unitId]);
        }
        return (negative ? "-" : "") + p + (unitId == UNIT_MAX ? "" : UNIT.values()[unitId]);
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    public static byte[] toByteArray(List<Byte> in) {
        byte[] out = new byte[in.size()];
        int cnt = 0;
        try {
            for (Byte b : in) {
                out[cnt] = b;
                cnt++;
            }
        } catch (NullPointerException e) {
        } // caused by concurrent access
        return out;
    }

    /**
     * Try to convert the value in a basic value like bool, int; etc. If it's not possible it will
     * return null.
     *
     * @param value The value
     * @param hint The type or hint for the type
     * @return The typed value or null
     */
    public static Object to(Object value, String hint) {
        if (TString.isEmptyTrim(hint)) return null;
        Object val = null;
        switch (hint) {
            case "java.lang.Boolean":
            case "boolean":
            case "bool":
                val = TCast.toboolean(value, false);
                break;
            case "java.lang.Integer":
            case "integer":
            case "int":
                val = TCast.toint(value, 0);
                break;
            case "java.lang.Long":
            case "long":
                val = TCast.tolong(value, 0);
                break;
            case "java.lang.Double":
            case "double":
                val = TCast.todouble(value, 0);
                break;
            case "java.lang.Float":
            case "float":
                val = TCast.tofloat(value, 0);
                break;
            case "java.lang.Short":
            case "short":
                val = TCast.toshort(value, (short) 0);
                break;
            case "java.lang.Character":
            case "character":
            case "char":
                val = TCast.toint(value, 0);
                break;
            case "java.util.Date":
            case "date":
                val = TCast.toDate(value, null);
                break;
            case "java.util.Calendar":
            case "calendar":
                val = TCast.toCalendar(value);
                break;
            case "java.lang.String":
            case "string":
            case "text":
                val = String.valueOf(value);
                break;
        }
        return val;
    }
}
