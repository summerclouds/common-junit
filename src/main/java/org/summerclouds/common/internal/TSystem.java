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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TSystem {

    public enum OS {
        OTHER,
        UNIX,
        WINDOWS,
        MACOS,
        SOLARIS
    }

    private static TLog log = TLog.getLog(TSystem.class);
    private static ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();
    private static String hostname; // cached hostname

    /**
     * Returns the name of the current system. COMPUTERNAME or HOSTNAME.
     *
     * @return the hosts name
     */
    public static String getHostname() {
        if (hostname == null) {
            String out = System.getenv().get("COMPUTERNAME");
            if (out == null) out = System.getenv().get("HOSTNAME");
            if (out == null) {
                RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
                String name = rt.getName();
                out = TString.afterIndex(name, '@');
            }
            hostname = out;
        }
        return hostname;
    }

    /**
     * Returns the process id of the current application.
     *
     * @return the current process id
     */
    public static String getPid() {
        RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
        String name = rt.getName();
        return TString.beforeIndex(name, '@');
    }

    /** makeshift system beep if awt.Toolkit.beep is not available. Works also in JDK 1.02. */
    public static void beep() {
        System.out.print("\007");
        System.out.flush();
    } // end beep

    public static String findCalling(int returns) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length > returns) return stack[returns].getClassName();
        return "?";
    }

    public static String findCalling() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement step : stack) {
            String n = step.getClassName();
            if (!n.startsWith("java.lang") && !n.startsWith("de.mhus.lib.core")) return n;
        }
        return "?";
    }

    public static String findCallingMethod(int returns) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length > returns) return stack[returns].getMethodName();
        return "?";
    }

    public static String findCallingMethod() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement step : stack) {
            String n = step.getClassName();
            if (!n.startsWith("java.lang") && !n.startsWith("de.mhus.lib.core"))
                return step.getMethodName();
        }
        return "?";
    }

    /**
     * Return the name of the main class or null if not found.
     *
     * @return the main class name
     */
    public static String getMainClassName() {
        for (final Map.Entry<String, String> entry : System.getenv().entrySet()) {
            if (entry.getKey().startsWith("JAVA_MAIN_CLASS")) return entry.getValue();
        }
        return null;
    }

    /**
     * Return the system temp directory.
     *
     * @return path to the tmp directory
     */
    public static String getTmpDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Return a string representation of the data. use this to Implement toString()
     *
     * @param sender
     * @param attributes
     * @return Stringified attributes
     */
    public static String toString(Object sender, Object... attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        if (sender != null)
            sb.append(sender instanceof String ? sender : sender.getClass().getSimpleName())
                    .append(':');
        boolean first = true;
        for (Object a : attributes) {
            if (!first) sb.append(',');
            else first = false;
            TString.serialize(sb, a, null);
        }
        sb.append(']');
        return sb.toString();
    }

    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotation) {
        Class<?> current = clazz;
        while (current != null) {
            if (current.isAnnotationPresent(annotation)) return current.getAnnotation(annotation);
            current = current.getSuperclass();
        }
        return null;
    }

    public static <A extends Annotation> List<A> findAnnotations(
            Class<?> clazz, Class<A> annotation) {
        LinkedList<A> out = new LinkedList<>();
        Class<?> current = clazz;
        while (current != null) {
            if (current.isAnnotationPresent(annotation))
                out.addFirst(current.getAnnotation(annotation));
            current = current.getSuperclass();
        }
        return out;
    }

    public static boolean equals(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    public static class ScriptResult {

        private Throwable exception;

        private int rc;
        private String error;
        private String output;

        @Override
        public String toString() {
            return "[" + output + (error != null ? "," + error : "") + "] " + rc;
        }

        public String[] toArray() {
            return new String[] {output, error};
        }

        public Throwable getException() {
            return exception;
        }

        public int getRc() {
            return rc;
        }

        public String getError() {
            return error;
        }

        public String getOutput() {
            return output;
        }
    }

    /**
     * Get the identification of the application host:pid
     *
     * @return host:pid
     */
    public static String getAppIdent() {
        return getHostname() + ":" + getPid();
    }

    /**
     * Returns the id of the object like the original toString() will do
     *
     * @param o
     * @return the id
     */
    public static String getObjectId(Object o) {
        if (o == null) return "null";
        String name = o.getClass().getName();
        if (name == null) name = getClassName(o);
        return name + "@" + Integer.toHexString(System.identityHashCode(o));
    }

    /**
     * Returns the canonical name of the main class.
     *
     * @param obj Object or class
     * @return The name
     */
    public static String getClassName(Object obj) {
        Class<? extends Object> clazz = getMainClass(obj);
        if (clazz == null) return "null";
        return clazz.getCanonicalName();
    }

    public static String getSimpleName(Object obj) {
        Class<? extends Object> clazz = getMainClass(obj);
        if (clazz == null) return "null";
        return clazz.getSimpleName();
    }

    /**
     * Returns the class of the object or class or if the class is anonymous the surrounding main
     * class.
     *
     * @param obj object or class
     * @return The main class
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Class<?> getMainClass(Object obj) {
        if (obj == null) return null;
        Class<? extends Object> clazz = obj.getClass();
        if (obj instanceof Class) clazz = (Class) obj;
        while (clazz != null && clazz.isAnonymousClass()) clazz = clazz.getEnclosingClass();
        if (clazz == null) return null;
        return clazz;
    }

    /**
     * Return the declared template class for the index. This is only possible if the class itself
     * extends a templated class and declares the templates with classes.
     *
     * @param clazz The class to check
     * @param index Template index, starts at 0
     * @return The canonical name of the defined template
     */
    // https://stackoverflow.com/questions/3437897/how-to-get-a-class-instance-of-generics-type-t#3437930
    public static String getTemplateCanonicalName(Class<?> clazz, int index) {
        Type mySuperclass = clazz.getGenericSuperclass();
        if (mySuperclass instanceof ParameterizedType) {
            Type[] templates = ((ParameterizedType) mySuperclass).getActualTypeArguments();
            if (index >= templates.length) return null;
            Type tType = templates[index];
            String templName = tType.getTypeName();
            return templName;
        }
        return null;
    }

    /**
     * Compares two objects even if they are null.
     *
     * <p>compareTo(null, null ) === 0 compareTo("", "" ) === 0 compareTo(null, "" ) === -1
     * compareTo("", null ) === 1
     *
     * <p>returns s1.comareTo(s2)
     *
     * <p>groovy:000> s1 = new Date(); === Wed Jun 28 12:22:35 CEST 2017 groovy:000> s2 = new
     * Date(); === Wed Jun 28 12:22:40 CEST 2017 groovy:000> groovy:000> s1.compareTo(s2) === -1
     * groovy:000> s2.compareTo(s1) === 1
     *
     * @param s1
     * @param s2
     * @return see Comparator
     */
    public static <T extends Comparable<T>> int compareTo(T s1, T s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
    }

    public static String freeMemoryAsString() {
        long free = freeMemory();
        return TString.toByteDisplayString(free);
    }

    public static String maxMemoryAsString() {
        return TString.toByteDisplayString(Runtime.getRuntime().maxMemory());
    }

    public static String memDisplayString() {
        return freeMemoryAsString() + " / " + maxMemoryAsString();
    }

    /**
     * Return the amount of free not allocated memory
     *
     * @return memory in bytes
     */
    public static long freeMemory() {
        Runtime r = Runtime.getRuntime();
        return r.maxMemory() - r.totalMemory() + r.freeMemory();
    }

    /**
     * Get the field in this or any superclass.
     *
     * @param clazz
     * @param name
     * @return The field or null if not found
     */
    public static Field getDeclaredField(Class<?> clazz, String name) {
        if (clazz == null || name == null) return null;
        try {
            Field field = clazz.getDeclaredField(name);
            return field;
        } catch (NoSuchFieldException e) {
        }
        return getDeclaredField(clazz.getSuperclass(), name);
    }

    /**
     * Load the class for the given type declaration. Also primitive and array declarations are
     * allowed.
     *
     * @param type The type as primitive int, long ... String, Date the class name or as Array with
     *     pending [].
     * @param cl The class loader or null to use the thread context class loader.
     * @return The class instance.
     * @throws ClassNotFoundException
     */
    public static Class<?> loadClass(String type, ClassLoader cl) throws ClassNotFoundException {
        if (cl == null) cl = Thread.currentThread().getContextClassLoader();
        if ("int".equals(type)) return int.class;
        if ("long".equals(type)) return long.class;
        if ("short".equals(type)) return short.class;
        if ("double".equals(type)) return double.class;
        if ("float".equals(type)) return float.class;
        if ("byte".equals(type)) return byte.class;
        if ("boolean".equals(type)) return boolean.class;
        if ("char".equals(type)) return char.class;
        if ("String".equals(type)) return String.class;
        if ("Date".equals(type)) return Date.class;

        if ("int[]".equals(type)) return int[].class;
        if ("long[]".equals(type)) return long[].class;
        if ("short[]".equals(type)) return short[].class;
        if ("double[]".equals(type)) return double[].class;
        if ("float[]".equals(type)) return float[].class;
        if ("byte[]".equals(type)) return byte[].class;
        if ("boolean[]".equals(type)) return boolean[].class;
        if ("char[]".equals(type)) return char[].class;
        if ("String[]".equals(type)) return String[].class;
        if ("Date[]".equals(type)) return Date[].class;

        boolean array = false;
        if (type.endsWith("[]")) {
            array = true;
            type = type.substring(0, type.length() - 2);
        }

        Class<?> clazz = cl.loadClass(type);
        if (array) {
            clazz = Array.newInstance(clazz, 0).getClass();
        }
        return clazz;
    }

    /**
     * Watch for a defined time of milliseconds all threads and returns the used cpu time.
     *
     * @param sleep
     * @return List of found values
     * @throws InterruptedException
     */
    public static List<TopThreadInfo> threadTop(long sleep) throws InterruptedException {
        sleep = Math.max(sleep, 200);

        LinkedList<TopThreadInfo> threads = new LinkedList<>();
        for (Entry<Thread, StackTraceElement[]> thread : Thread.getAllStackTraces().entrySet()) {
            threads.add(new TopThreadInfo(tmxb, thread));
        }

        threads.forEach(t -> t.start());
        Thread.sleep(sleep);
        threads.forEach(t -> t.stop());

        long sumCpu = 0;
        long sumUser = 0;
        for (TopThreadInfo t : threads) {
            sumCpu += t.getCpuTime();
            sumUser += t.getUserTime();
        }
        for (TopThreadInfo t : threads) t.setSumTime(sumUser, sumCpu);

        return threads;
    }

    public static class TopThreadInfo {

        private Thread thread;
        private StackTraceElement[] stacktrace;
        private long startTime;
        private ThreadMXBean tmxb;
        private long startUser;
        private long startCpu;
        private long stopTime;
        private long stopUser;
        private long stopCpu;
        private long diffTime;
        private long diffUser;
        private long diffCpu;
        private double perCpu;
        private double perUser;

        private TopThreadInfo(ThreadMXBean tmxb, Entry<Thread, StackTraceElement[]> thread) {
            this.tmxb = tmxb;
            this.thread = thread.getKey();
            this.stacktrace = thread.getValue();
        }

        private void start() {
            startTime = System.currentTimeMillis();
            startUser = tmxb.getThreadUserTime(thread.getId());
            startCpu = tmxb.getThreadCpuTime(thread.getId());
        }

        private void stop() {
            stopTime = System.currentTimeMillis();
            stopUser = tmxb.getThreadUserTime(thread.getId());
            stopCpu = tmxb.getThreadCpuTime(thread.getId());

            diffTime = stopTime - startTime;
            diffUser = stopUser - startUser;
            diffCpu = stopCpu - startCpu;
        }

        private void setSumTime(long userTime, long cpuTime) {
            if (cpuTime > 0) this.perCpu = (double) (diffCpu * 100) / (double) cpuTime;
            if (userTime > 0) this.perUser = (double) (diffUser * 100) / (double) userTime;
        }

        public long getCpuTime() {
            return diffCpu;
        }

        public long getUserTime() {
            return diffUser;
        }

        public long getInterval() {
            return diffTime;
        }

        public double getCpuPercentage() {
            return perCpu;
        }

        public double getUserPercentage() {
            return perUser;
        }

        public Thread getThread() {
            return thread;
        }

        public StackTraceElement[] getStacktrace() {
            return stacktrace;
        }

        public long getCpuTotal() {
            return stopCpu;
        }
    }

    public static boolean isWindows() {
        final String os = System.getProperty("os.name");
        return os.contains("Windows");
    }

    public static boolean isMac() {
        final String os = System.getProperty("os.name");
        return (os.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        final String os = System.getProperty("os.name");
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        final String os = System.getProperty("os.name");
        return (os.indexOf("sunos") >= 0);
    }

    public static OS getOS() {
        if (isWindows()) {
            return OS.WINDOWS;
        } else if (isMac()) {
            return OS.MACOS;
        } else if (isUnix()) {
            return OS.UNIX;
        } else if (isSolaris()) {
            return OS.SOLARIS;
        } else {
            return OS.OTHER;
        }
    }

    public static File getUserHome() {
        String currentUsersHomeDir = System.getProperty("user.home");
        return new File(currentUsersHomeDir);
    }

    /**
     * Like System.setProperty(). Sets the property with to a value. But this one accepts also null
     * values and will clear the property in this case.
     *
     * @param key
     * @param value
     * @return Previous value
     */
    public static String setProperty(String key, String value) {
        if (value == null) return System.clearProperty(key);
        else return System.setProperty(key, value);
    }

    /**
     * Like System.setProperty(). Sets the property with to a value. But this one accepts also null
     * values and will clear the property in this case. Key is ownerClass + _ + key
     *
     * @param owner
     * @param key
     * @param value
     * @return Previous value
     */
    public static String setProperty(Object owner, String key, String value) {
        String name = getOwnerName(owner) + "_" + key;
        if (value == null) return System.clearProperty(name);
        else return System.setProperty(name, value);
    }

    /**
     * Load from System.getProperty() or the fallback from Sytsem.getenv and with prefix from owner.
     * Key is ownerClass + _ + key
     *
     * @param owner
     * @param key
     * @return Current value or null
     */
    public static String getProperty(Object owner, String key) {
        String name = getOwnerName(owner) + "_" + key;
        String ret = System.getProperty(getOwnerName(owner) + "_" + key);
        if (ret != null) return ret;
        ret = System.getenv(name);
        return ret;
    }

    /**
     * Load from System.getProperty() or the fallback from Sytsem.getenv and with prefix from owner.
     * Key is ownerClass + _ + key
     *
     * @param owner
     * @param key
     * @param def
     * @return Current value or def
     */
    public static String getProperty(Object owner, String key, String def) {
        String name =
                getOwnerName(owner).replace('.', '_').replace('@', '_').replace('$', '_')
                        + "_"
                        + key;
        String ret = System.getProperty(getOwnerName(owner) + "_" + key);
        if (ret != null) return ret;
        ret = System.getenv(name);
        if (ret != null) return ret;
        return def;
    }

    /**
     * Return a unique class name for the class with package, main class name and sub class name.
     *
     * @param clazz The class to analyze
     * @return The name of the class
     */
    public static String getCanonicalClassName(Class<?> clazz) {
        if (clazz == null) return "null";
        if (clazz.isLocalClass()) return clazz.getCanonicalName();

        if (clazz.isAnonymousClass()) return clazz.getName();

        return clazz.getCanonicalName();
    }

    public static boolean isLockedByThread(Object value) {
        if (value == null) return false;
        int objectHash = value.hashCode();
        for (long threadId : tmxb.getAllThreadIds()) {
            ThreadInfo info = tmxb.getThreadInfo(threadId);
            for (LockInfo locks : info.getLockedSynchronizers())
                if (locks.getIdentityHashCode() == objectHash) return true;
        }
        return false;
    }

    public static Object getJavaVersion() {
        return System.getProperty("java.version");
    }

    //    private static final Instrumentation instrumentation = ByteBuddyAgent.install();
    /*
     * Use byte buddy to get the lambda byte code
     */
    //    public static byte[] getBytes(Class<?> c) throws IOException {
    //        //		String name = '/' + c.getName().replace('.', '/')+ ".class";
    //        //		InputStream is = c.getClassLoader().getResourceAsStream(name);
    //        //		byte[] bytes = MFile.readBinary(is);
    //        //		return bytes;
    //        ClassFileLocator locator = ClassFileLocator.AgentBased.of(instrumentation, c);
    //        TypeDescription.ForLoadedType desc = new TypeDescription.ForLoadedType(c);
    //        ClassFileLocator.Resolution resolution = locator.locate(desc.getName());
    //        return resolution.resolve();
    //    }

    public static long getJvmUptime() {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long uptime = rb.getUptime();
        return uptime;
    }

    public static long getJvmStartTime() {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long uptime = rb.getStartTime();
        return uptime;
    }

    public static long getSystemUptime() {
        try {
            long uptime = -1;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Process uptimeProc = Runtime.getRuntime().exec("net stats srv");
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("Statistics since")) {
                        SimpleDateFormat format =
                                new SimpleDateFormat("'Statistics since' MM/dd/yyyy hh:mm:ss a");
                        Date boottime = format.parse(line);
                        uptime = System.currentTimeMillis() - boottime.getTime();
                        break;
                    }
                }
            } else if (os.contains("mac")
                    || os.contains("nix")
                    || os.contains("nux")
                    || os.contains("aix")) {
                Process uptimeProc = Runtime.getRuntime().exec("uptime");
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
                String line = in.readLine();
                if (line != null) {
                    Pattern parse = Pattern.compile("((\\d+) days,)? (\\d+):(\\d+)");
                    Matcher matcher = parse.matcher(line);
                    if (matcher.find()) {
                        String _days = matcher.group(2);
                        String _hours = matcher.group(3);
                        String _minutes = matcher.group(4);
                        int days = _days != null ? Integer.parseInt(_days) : 0;
                        int hours = _hours != null ? Integer.parseInt(_hours) : 0;
                        int minutes = _minutes != null ? Integer.parseInt(_minutes) : 0;
                        uptime = (minutes * 60000) + (hours * 60000 * 60) + (days * 6000 * 60 * 24);
                    }
                }
            }
            return uptime;
        } catch (Exception e) {
            log.d(e);
            return -1;
        }
    }

    public static LinkedList<Method> getMethods(Class<?> clazz) {
        LinkedList<Method> out = new LinkedList<Method>();
        do {
            for (Method m : clazz.getMethods()) {
                out.add(m);
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);

        return out;
    }

    public static LinkedList<Field> getAttributes(Class<?> clazz) {
        LinkedList<Field> out = new LinkedList<Field>();
        do {
            for (Field field : clazz.getDeclaredFields()) out.add(field);
            clazz = clazz.getSuperclass();
        } while (clazz != null);

        return out;
    }

    public static List<Method> findMethodsWithAnnotation(
            Class<?> clazz, Class<? extends Annotation> annotationClass) {
        LinkedList<Method> out = new LinkedList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(annotationClass) != null) out.add(method);
        }
        return out;
    }

    public static List<Field> findFieldsWithAnnotation(
            Class<?> clazz, Class<? extends Annotation> annotationClass) {
        LinkedList<Field> out = new LinkedList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(annotationClass) != null) out.add(field);
        }
        return out;
    }

    public static Class<?> getClass(ClassLoader cl, String type) throws ClassNotFoundException {
        if (type == null) return null;
        if (type.equals("int") || type.equals("integer")) return int.class;
        if (type.equals("bool") || type.equals("boolean")) return boolean.class;
        if (type.equals("long")) return long.class;
        if (type.equals("double")) return double.class;
        if (type.equals("float")) return float.class;
        if (type.equals("char") || type.equals("character")) return char.class;
        if (type.equals("short")) return short.class;
        if (type.equals("byte")) return byte.class;
        if (type.equals("string") || type.equals("text")) return String.class;
        if (type.equals("date")) return Date.class;
        if (type.equals("map")) return Map.class;
        if (type.equals("list")) return List.class;
        return cl.loadClass(type);
    }

    public static String getUsername() {
        return System.getProperty("user.name");
    }

    public static boolean isPasswordName(String key) {
        if (key == null) return false;
        return key.toLowerCase().contains("pass"); // password, ? secret ?
    }

    public static String currentStackTrace(String firstLine) {
        return TCast.toString(firstLine, Thread.currentThread().getStackTrace());
    }

    public static int execute(String name, File rootDir, String cmd, ExecuteControl control)
            throws IOException {

        ProcessBuilder processBuilder = new ProcessBuilder();
        if (rootDir != null) processBuilder.directory(rootDir);
        if (TSystem.isWindows())
            // Windows
            processBuilder.command("cmd.exe", "/c", cmd);
        else
            // Unix
            processBuilder.command("/bin/bash", "-c", cmd);

        try {

            Process process = processBuilder.start();

            control.stdin(new PrintWriter(new BufferedOutputStream(process.getOutputStream())));

            BufferedReader outReader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            final BufferedReader errReader =
                    new BufferedReader(new InputStreamReader(process.getErrorStream()));

            Thread errorWriterTask =
                    new Thread(
                            new Runnable() {

                                @Override
                                public void run() {
                                    String line;
                                    try {
                                        while ((line = errReader.readLine()) != null) {
                                            control.stderr(line);
                                        }
                                    } catch (Throwable t) {
                                    }
                                }
                            });
            errorWriterTask.start();

            String line;
            while ((line = outReader.readLine()) != null) {
                control.stdout(line);
            }

            int exitCode = process.waitFor();

            errorWriterTask.interrupt();

            return exitCode;

        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    public static interface ExecuteControl {

        void stdin(PrintWriter writer);

        void stderr(String line);

        void stdout(String line);
    }

    public static class DefaultExecuteControl implements ExecuteControl {

        @Override
        public void stdin(PrintWriter writer) {}

        @Override
        public void stderr(String line) {
            System.err.println(line);
        }

        @Override
        public void stdout(String line) {
            System.out.println(line);
        }
    }

    public static String getOwnerName(Object owner) {
        if (owner == null) return "?";
        if (owner instanceof Class) return getCanonicalClassName((Class<?>) owner);

        if (owner instanceof String) return (String) owner;

        return getCanonicalClassName(owner.getClass());
    }
}
