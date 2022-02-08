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
package org.summerclounds.common.junit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.TestInfo;
import org.slf4j.LoggerFactory;
import org.summerclounds.common.internal.TUri;
import org.summerclounds.common.internal.TXml;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class TestUtil {

    public static void configureApacheCommonLogging(String logger, Level level) {

        try {
            System.setProperty(
                    "org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
            System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");

            String l = javaToApacheLogLevel(level);
            if (logger == null) {
                System.setProperty("org.apache.commons.logging", l);
                System.out.println("Logging set default: " + l);
                Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
                root.setLevel(Level.INFO);
                return;
            }
            System.setProperty("org.apache.commons.logging." + logger, l);

            System.out.println("Logging set: " + logger + "=" + l);
            final org.slf4j.Logger logger2 = LoggerFactory.getLogger(logger);
            final ch.qos.logback.classic.Logger logger3 = (ch.qos.logback.classic.Logger) logger2;
            logger3.setLevel(ch.qos.logback.classic.Level.toLevel(l));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static String javaToApacheLogLevel(Level level) {
        String l = "FATAL";
        switch (level.getName()) {
            case "INFO":
                l = "INFO";
                break;
            case "WARNING":
                l = "WARN";
                break;
            case "FINE":
                l = "DEBUG";
                break;
            case "FINER":
                l = "TRACE";
                break;
            case "SEVERE":
                l = "WARN";
                break;
        }
        return l;
    }

    public static void configureJavaLogger(String name, Level level) {
        Logger logger = Logger.getLogger(name);
        logger.setLevel(level);
    }

    public static void configureJavaLogger(Level level) {

        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s [%3$s] (%2$s) %5$s %6$s%n");

        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(level);
        }
        // Set root logger level
        rootLogger.setLevel(level);
    }

    public static void enableDebug() {

    }

    public static String getPluginVersion(String uriStr) {
        TUri uri = TUri.toUri(uriStr);
        String[] parts = uri.getPath().split("/");
        return parts[2];
    }

    public static String conrentVersion()
            throws ParserConfigurationException, SAXException, IOException {
        Document doc = TXml.loadXml(new File("pom.xml"));
        String version = TXml.getValue(doc.getDocumentElement(), "/parent/version", "");
        return version;
    }

    public static void start(TestInfo testInfo) {
        if (testInfo == null) {
            System.out.println(">>> unknown");
            return;
        }
        Optional<Class<?>> clazz = testInfo.getTestClass();
        Optional<Method> method = testInfo.getTestMethod();
        System.out.println(
                ">>> "
                        + (clazz == null || clazz.isEmpty() ? "?" : clazz.get().getCanonicalName())
                        + "::"
                        + (method == null || method.isEmpty() ? "?" : method.get().getName()));
    }

    public static void stop(TestInfo testInfo) {
        if (testInfo == null) {
            System.out.println("<<< unknown");
            return;
        }
        Optional<Class<?>> clazz = testInfo.getTestClass();
        Optional<Method> method = testInfo.getTestMethod();
        System.out.println(
                "<<< "
                        + (clazz == null || clazz.isEmpty() ? "?" : clazz.get().getCanonicalName())
                        + "::"
                        + (method == null || method.isEmpty() ? "?" : method.get().getName()));
    }

}
