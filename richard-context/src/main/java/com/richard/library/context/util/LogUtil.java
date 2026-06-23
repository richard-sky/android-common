package com.richard.library.context.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;

import com.richard.library.context.AppContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/09/21
 *     desc  : utils about log
 *     log文件存储规则：根目录/[月-日]/[prefix]-[bizType].txt
 *     如:sdcard/com.richard.library.basic/12-25/Log-login.txt
 * </pre>
 */
public final class LogUtil {

    public static final int V = Log.VERBOSE;
    public static final int D = Log.DEBUG;
    public static final int I = Log.INFO;
    public static final int W = Log.WARN;
    public static final int E = Log.ERROR;
    public static final int A = Log.ASSERT;

    @IntDef({V, D, I, W, E, A})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE {
    }

    private static final char[] T = new char[]{'V', 'D', 'I', 'W', 'E', 'A'};

    private static final int FILE = 0x10;
    private static final int JSON = 0x20;
    private static final int XML = 0x30;

    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String TOP_CORNER = "┌";
    private static final String MIDDLE_CORNER = "├";
    private static final String LEFT_BORDER = "│ ";
    private static final String BOTTOM_CORNER = "└";
    private static final String SIDE_DIVIDER =
            "────────────────────────────────────────────────────────";
    private static final String MIDDLE_DIVIDER =
            "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER = TOP_CORNER + SIDE_DIVIDER + SIDE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + MIDDLE_DIVIDER + MIDDLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_CORNER + SIDE_DIVIDER + SIDE_DIVIDER;
    private static final int MAX_LEN = 3000;
    @SuppressLint("SimpleDateFormat")
    private static final Format FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS ");
    private static final String NOTHING = "log nothing";
    private static final String NULL = "null";
    private static final String ARGS = "args";
    private static final String PLACEHOLDER = " ";
    private static final Config CONFIG = new Config();
    private static final LogPrinter LOG_PRINTER = new LogPrinter();
    private static long lastClearLogFileTime;

    private LogUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Config getConfig() {
        return CONFIG;
    }

    public static void v(final Object... contents) {
        log(V, CONFIG.mGlobalTag, contents);
    }

    public static void vTag(final String tag, final Object... contents) {
        log(V, tag, contents);
    }

    public static void d(final Object... contents) {
        log(D, CONFIG.mGlobalTag, contents);
    }

    public static void dTag(final String tag, final Object... contents) {
        log(D, tag, contents);
    }

    public static void i(final Object... contents) {
        log(I, CONFIG.mGlobalTag, contents);
    }

    public static void iTag(final String tag, final Object... contents) {
        log(I, tag, contents);
    }

    public static void w(final Object... contents) {
        log(W, CONFIG.mGlobalTag, contents);
    }

    public static void wTag(final String tag, final Object... contents) {
        log(W, tag, contents);
    }

    public static void e(final Object... contents) {
        log(E, CONFIG.mGlobalTag, contents);
    }

    public static void eTag(final String tag, final Object... contents) {
        log(E, tag, contents);
    }

    public static void a(final Object... contents) {
        log(A, CONFIG.mGlobalTag, contents);
    }

    public static void aTag(final String tag, final Object... contents) {
        log(A, tag, contents);
    }

    public static void file(final Object content) {
        log(FILE | D, CONFIG.mGlobalTag, content);
    }

    public static void file(@TYPE final int type, final Object content) {
        log(FILE | type, CONFIG.mGlobalTag, content);
    }

    public static void file(final String tag, final Object content) {
        log(FILE | D, tag, content);
    }

    public static void file(@TYPE final int type, final String tag, final Object content) {
        log(FILE | type, tag, content);
    }

    public static void file(final String bizType, @TYPE final int type, final Object content) {
        log(bizType, FILE | type, CONFIG.mGlobalTag, content);
    }

    public static void file(final String bizType, final String tag, final Object content) {
        log(bizType, FILE | D, tag, content);
    }

    public static void file(final String bizType, @TYPE final int type, final String tag, final Object content) {
        log(bizType, FILE | type, tag, content);
    }

    public static void json(final String content) {
        log(JSON | D, CONFIG.mGlobalTag, content);
    }

    public static void json(@TYPE final int type, final String content) {
        log(JSON | type, CONFIG.mGlobalTag, content);
    }

    public static void json(final String tag, final String content) {
        log(JSON | D, tag, content);
    }

    public static void json(@TYPE final int type, final String tag, final String content) {
        log(JSON | type, tag, content);
    }

    public static void xml(final String content) {
        log(XML | D, CONFIG.mGlobalTag, content);
    }

    public static void xml(@TYPE final int type, final String content) {
        log(XML | type, CONFIG.mGlobalTag, content);
    }

    public static void xml(final String tag, final String content) {
        log(XML | D, tag, content);
    }

    public static void xml(@TYPE final int type, final String tag, final String content) {
        log(XML | type, tag, content);
    }

    public static void log(final int type, final String tag, final Object... contents) {
        log(null, type, tag, contents);
    }

    public static void log(final String bizType, final int type, final String tag, final Object... contents) {
        if (!CONFIG.mLogSwitch || (!CONFIG.mLog2ConsoleSwitch && !CONFIG.mLog2FileSwitch)) return;
        int type_low = type & 0x0f;
        if (type_low < CONFIG.mConsoleFilter && type_low < CONFIG.mFileFilter) return;
        LOG_PRINTER.println(bizType, type, tag, contents);
    }

    private static TagHead processTagAndHead(String tag) {
        if (!CONFIG.mTagIsSpace && !CONFIG.mLogHeadSwitch) {
            if (TextUtils.isEmpty(tag)) {
                tag = CONFIG.mGlobalTag;
            }
        } else {
            final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            final int stackIndex = 3 + CONFIG.mStackOffset;
            if (stackIndex >= stackTrace.length) {
                StackTraceElement targetElement = stackTrace[3];
                final String fileName = getFileName(targetElement);
                if (CONFIG.mTagIsSpace && isSpace(tag)) {
                    int index = fileName.indexOf('.');// Use proguard may not find '.'.
                    tag = index == -1 ? fileName : fileName.substring(0, index);
                }
                return new TagHead(tag, null, ": ");
            }
            StackTraceElement targetElement = stackTrace[stackIndex];
            final String fileName = getFileName(targetElement);
            if (CONFIG.mTagIsSpace && isSpace(tag)) {
                int index = fileName.indexOf('.');// Use proguard may not find '.'.
                tag = index == -1 ? fileName : fileName.substring(0, index);
            }
            if (CONFIG.mLogHeadSwitch) {
                String tName = Thread.currentThread().getName();
                final String head = new Formatter()
                        .format("%s, %s.%s(%s:%d)",
                                tName,
                                targetElement.getClassName(),
                                targetElement.getMethodName(),
                                fileName,
                                targetElement.getLineNumber())
                        .toString();
                final String fileHead = " [" + head + "]: ";
                if (CONFIG.mStackDeep <= 1) {
                    return new TagHead(tag, new String[]{head}, fileHead);
                } else {
                    final String[] consoleHead =
                            new String[Math.min(
                                    CONFIG.mStackDeep,
                                    stackTrace.length - stackIndex
                            )];
                    consoleHead[0] = head;
                    int spaceLen = tName.length() + 2;
                    String space = new Formatter().format("%" + spaceLen + "s", "").toString();
                    for (int i = 1, len = consoleHead.length; i < len; ++i) {
                        targetElement = stackTrace[i + stackIndex];
                        consoleHead[i] = new Formatter()
                                .format("%s%s.%s(%s:%d)",
                                        space,
                                        targetElement.getClassName(),
                                        targetElement.getMethodName(),
                                        getFileName(targetElement),
                                        targetElement.getLineNumber())
                                .toString();
                    }
                    return new TagHead(tag, consoleHead, fileHead);
                }
            }
        }
        return new TagHead(tag, null, ": ");
    }

    private static String getFileName(final StackTraceElement targetElement) {
        String fileName = targetElement.getFileName();
        if (fileName != null) return fileName;
        // If name of file is null, should add
        // "-keepattributes SourceFile,LineNumberTable" in proguard file.
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1];
        }
        int index = className.indexOf('$');
        if (index != -1) {
            className = className.substring(0, index);
        }
        return className + ".java";
    }

    private static String processBody(final int type, final Object... contents) {
        String body = NULL;
        if (contents != null) {
            if (contents.length == 1) {
                Object object = contents[0];
                if (object != null) body = object.toString();
                if (type == JSON) {
                    body = formatJson(body);
                } else if (type == XML) {
                    body = formatXml(body);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, len = contents.length; i < len; ++i) {
                    Object content = contents[i];
                    sb.append(ARGS)
                            .append("[")
                            .append(i)
                            .append("]")
                            .append(" = ")
                            .append(content == null ? NULL : content.toString())
                            .append(LINE_SEP);
                }
                body = sb.toString();
            }
        }
        return body.length() == 0 ? NOTHING : body;
    }

    public static String formatJson(String json) {
        try {
            if (json.startsWith("{") && json.endsWith("}")) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith("[") && json.endsWith("]")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static String formatXml(String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replaceFirst(">", ">" + LINE_SEP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    private static void print2Console(final int type,
                                      final String tag,
                                      final String[] head,
                                      final String msg) {
        if (CONFIG.mSingleTagSwitch) {
            StringBuilder sb = new StringBuilder();
            sb.append(PLACEHOLDER).append(LINE_SEP);
            if (CONFIG.mLogBorderSwitch) {
                sb.append(TOP_BORDER).append(LINE_SEP);
                if (head != null) {
                    for (String aHead : head) {
                        sb.append(LEFT_BORDER).append(aHead).append(LINE_SEP);
                    }
                    sb.append(MIDDLE_BORDER).append(LINE_SEP);
                }
                for (String line : msg.split(LINE_SEP)) {
                    sb.append(LEFT_BORDER).append(line).append(LINE_SEP);
                }
                sb.append(BOTTOM_BORDER);
            } else {
                if (head != null) {
                    for (String aHead : head) {
                        sb.append(aHead).append(LINE_SEP);
                    }
                }
                sb.append(msg);
            }
            printMsgSingleTag(type, tag, sb.toString());
        } else {
            printBorder(type, tag, true);
            printHead(type, tag, head);
            printMsg(type, tag, msg);
            printBorder(type, tag, false);
        }
    }

    private static void printBorder(final int type, final String tag, boolean isTop) {
        if (CONFIG.mLogBorderSwitch) {
            Log.println(type, tag, isTop ? TOP_BORDER : BOTTOM_BORDER);
        }
    }

    private static void printHead(final int type, final String tag, final String[] head) {
        if (head != null) {
            for (String aHead : head) {
                Log.println(type, tag, CONFIG.mLogBorderSwitch ? LEFT_BORDER + aHead : aHead);
            }
            if (CONFIG.mLogBorderSwitch) Log.println(type, tag, MIDDLE_BORDER);
        }
    }

    private static void printMsg(final int type, final String tag, final String msg) {
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            int index = 0;
            for (int i = 0; i < countOfSub; i++) {
                printSubMsg(type, tag, msg.substring(index, index + MAX_LEN));
                index += MAX_LEN;
            }
            if (index != len) {
                printSubMsg(type, tag, msg.substring(index, len));
            }
        } else {
            printSubMsg(type, tag, msg);
        }
    }

    private static void printMsgSingleTag(final int type, final String tag, final String msg) {
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            if (CONFIG.mLogBorderSwitch) {
                Log.println(type, tag, msg.substring(0, MAX_LEN) + LINE_SEP + BOTTOM_BORDER);
                int index = MAX_LEN;
                for (int i = 1; i < countOfSub; i++) {
                    Log.println(type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                            + LEFT_BORDER + msg.substring(index, index + MAX_LEN)
                            + LINE_SEP + BOTTOM_BORDER);
                    index += MAX_LEN;
                }
                if (index != len) {
                    Log.println(type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                            + LEFT_BORDER + msg.substring(index, len));
                }
            } else {
                int index = 0;
                for (int i = 0; i < countOfSub; i++) {
                    Log.println(type, tag, msg.substring(index, index + MAX_LEN));
                    index += MAX_LEN;
                }
                if (index != len) {
                    Log.println(type, tag, msg.substring(index, len));
                }
            }
        } else {
            Log.println(type, tag, msg);
        }
    }

    private static void printSubMsg(final int type, final String tag, final String msg) {
        if (!CONFIG.mLogBorderSwitch) {
            Log.println(type, tag, msg);
            return;
        }
        StringBuilder sb = new StringBuilder();
        String[] lines = msg.split(LINE_SEP);
        for (String line : lines) {
            Log.println(type, tag, LEFT_BORDER + line);
        }
    }

    /**
     * 获取日志文件根目录
     */
    public static String getLogFileDir() {
        return (CONFIG.mDir == null ? CONFIG.mDefaultDir : CONFIG.mDir);
    }

    /**
     * 获取指定日期得日志文件目录
     */
    public static String getLogFileDir(long timeMills) {
        return getLogFileDir() + DateUtil.formatTimeStamp("yyyy-MM-dd", timeMills);
    }

    /**
     * 获取指定时间的日志文件（可能文件不存在）
     *
     * @param timeMills 必填 指定的时间戳
     */
    public static String getLogFile(long timeMills) {
        return getLogFile(timeMills, null);
    }

    /**
     * 获取指定时间的日志文件（可能文件不存在）
     *
     * @param timeMills 必填 指定的时间戳
     * @param bizType   选填 日志业务标识
     */
    public static String getLogFile(long timeMills, String bizType) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getLogFileDir(timeMills))
                .append(File.separator)
                .append(StringUtilKt.defaultIfEmpty(CONFIG.mFilePrefix, "Log"));

        if (!TextUtils.isEmpty(bizType)) {
            pathBuilder.append("_").append(bizType);
        }
        pathBuilder.append(".txt");

        return pathBuilder.toString();
    }

    /**
     * 删除指定某天的全部日志
     *
     * @param timeMills 时间戳（最终提取月-日进行和文件名称匹配）
     */
    public static void deleteLogFile(long timeMills) {
        deleteLogFile(timeMills, null);
    }

    /**
     * 删除指定时间业务类型的日志
     *
     * @param timeMills 必填 时间戳（最终提取月-日进行和文件名称匹配）
     * @param bizType   选填 日志业务类型（为空时，会删除指定日期得全部日志，否则只删除指定日期的某业务类型的日志）
     */
    public static void deleteLogFile(long timeMills, String bizType) {
        File logDir = new File(getLogFileDir());
        if (!logDir.exists() || !logDir.isDirectory()) {
            return;
        }

        File dayLogDir = new File(getLogFileDir(timeMills));
        if (!dayLogDir.exists() || !dayLogDir.isDirectory()) {
            return;
        }

        if (TextUtils.isEmpty(bizType)) {
            FileUtil.delete(dayLogDir);
            return;
        }

        FileUtil.delete(getLogFile(timeMills, bizType));
    }

    /**
     * 获取设备详细信息
     */
    public static String getDeviceInfo() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("\nPackage Name       : ").append(AppContext.get().getPackageName())
                .append("\nDevice Manufacturer: ").append(Build.MANUFACTURER)
                .append("\nDevice Model       : ").append(Build.MODEL)
                .append("\nCPU ABI            : ").append(Arrays.toString(DeviceUtil.getABIs()))
                .append("\nAndroid Version    : ").append(Build.VERSION.RELEASE)
                .append("\nAndroid SDK        : ").append(Build.VERSION.SDK_INT)
                .append("\nDevice ID          : ").append(DeviceUtil.getUniqueDeviceId())
                .append("\nIs Emulator        : ").append((DeviceUtil.isEmulator() ? "是" : "否"))
                .append("\nIs Root            : ").append((DeviceUtil.isDeviceRooted() ? "是" : "否"))
                .append("\nApp VersionName    : ").append(AppUtil.getAppVersionName())
                .append("\nApp VersionCode    : ").append(AppUtil.getAppVersionCode())
                .append("\n").append(getStorageSpaceSizeInfo())
                .append("\n").append(getRunningRAMSpaceSizeInfo());

        return stringBuilder.toString();
    }

    /**
     * 获取运行内存大小信息
     */
    public static String getRunningRAMSpaceSizeInfo() {
        final StringBuilder builder = new StringBuilder();

//        Runtime rt = Runtime.getRuntime();
        ActivityManager activityManager = (ActivityManager) AppContext.get().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);

        float totalRAMSpace = info.totalMem / (1024 * 1024F);//MB
        float totalRemainingSpace = info.availMem / (1024 * 1024F);//MB
        boolean isLowMemory = info.lowMemory;
        float lowSpaceRun = info.threshold / (1024 * 1024F);//MB


        builder.append("------------------ RAM Space Size ------------------")
                .append("\n\tRAM Total Space              : ").append(String.format(
                        "%s MB(%s GB)"
                        , MathUtil.replaceEndZero(totalRAMSpace, 2)
                        , MathUtil.replaceEndZero(totalRAMSpace / 1024, 2)
                ))
                .append("\n\tRAM Remaining Space          : ").append(String.format(
                        "%s MB(%s GB)"
                        , MathUtil.replaceEndZero(totalRemainingSpace, 2)
                        , MathUtil.replaceEndZero(totalRemainingSpace / 1024, 2)
                ))
                .append("\n\tIs Low memory running        : ").append(isLowMemory)
                .append("\n\tLow memory running           : ").append(String.format(
                        "剩余内存低于%s MB时为低内存运行"
                        , MathUtil.replaceEndZero(lowSpaceRun, 2)
                ));

//        long maxMemory = rt.maxMemory();
//        Log.e("XMemory", "Dalvik MaxMemory:" + Long.toString(maxMemory / (1024 * 1024)));
//        Log.e("XMemory", "Dalvik MemoryClass:" + Long.toString(activityManager.getMemoryClass()));
//        Log.e("XMemory", "Dalvik LargeMemoryClass:" + Long.toString(activityManager.getLargeMemoryClass()));

//        Log.e("XMemory", "系统总内存:" + (info.totalMem / (1024 * 1024)) + "M");
//        Log.e("XMemory", "系统剩余内存:" + (info.availMem / (1024 * 1024)) + "M");
//        Log.e("XMemory", "系统是否处于低内存运行：" + info.lowMemory);
//        Log.e("XMemory", "系统剩余内存低于" + (info.threshold / (1024 * 1024)) + "M时为低内存运行");

        return builder.toString();
    }

    /**
     * 获取存储空间大小信息
     */
    public static String getStorageSpaceSizeInfo() {
        float externalTotalSpace = DeviceUtil.getExternalTotalSpace() / 1024F / 1024F;
        float externalRemainingSpace = DeviceUtil.getExternalRemainingSpace() / 1024F / 1024F;
        float systemTotalSpace = DeviceUtil.getSystemTotalSpace() / 1024F / 1024F;
        float systemRemainingSpace = DeviceUtil.getSystemRemainingSpace() / 1024F / 1024F;

        final StringBuilder builder = new StringBuilder();
        builder
                .append("----------------- Storage Space Size ---------------")
                .append("\n\tExternal Total Space     : ").append(String.format(
                        "%s MB(%s GB)"
                        , MathUtil.replaceEndZero(externalTotalSpace, 2)
                        , MathUtil.replaceEndZero(externalTotalSpace / 1024F, 2)
                ))
                .append("\n\tExternal Remaining Space : ").append(String.format(
                        "%s MB(%s GB)"
                        , MathUtil.replaceEndZero(externalRemainingSpace, 2)
                        , MathUtil.replaceEndZero(externalRemainingSpace / 1024F, 2)
                ))
                .append("\n\tSystem Total Space       : ").append(String.format(
                        "%s MB(%s GB)"
                        , MathUtil.replaceEndZero(systemTotalSpace, 2)
                        , MathUtil.replaceEndZero(systemTotalSpace / 1024F, 2)
                ))
                .append("\n\tSystem Remaining Space   : ").append(String.format(
                        "%s MB(%s GB)"
                        , MathUtil.replaceEndZero(systemRemainingSpace, 2)
                        , MathUtil.replaceEndZero(systemRemainingSpace / 1024F, 2)
                ));
        return builder.toString();
    }

    /**
     * 获取日志头部内容
     */
    private static String getLogHeadContent() {
        final StringBuilder headBuilder = new StringBuilder();
        headBuilder.append("******************** Log Head ********************")
                .append("\nCreate Date of Log : ").append(DateUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"))
                .append(getDeviceInfo());

        if (CONFIG.callback != null) {
            try {
                List<LogHead> logHeadList = CONFIG.callback.getCustomLogHead();
                if (logHeadList != null && !logHeadList.isEmpty()) {
                    headBuilder.append("\n---------------------- Custom ----------------------");
                    for (LogHead item : logHeadList) {
                        headBuilder.append("\n")
                                .append(StringUtilKt.rightPad(item.tag, 19, ' '))
                                .append(": ")
                                .append(item.content);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        headBuilder.append("\n******************** Log Head ********************\n\n");
        return headBuilder.toString();
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除已满最大保留天数的日志文件
     *
     * @param maxHoldDays 日志文件保留的最近的最大天数
     */
    public static void clearLogFile(int maxHoldDays) {
        if (maxHoldDays <= 0) {
            return;
        }

        File logFileRootDir = new File(getLogFileDir());
        if (!logFileRootDir.exists() || !logFileRootDir.isDirectory()) {
            return;
        }

        long endTimeMills = DateUtil.getDayTimeStamp(-maxHoldDays);
        File[] fileList = logFileRootDir.listFiles(item -> item.lastModified() < endTimeMills);

        if (fileList == null) {
            return;
        }

        for (File item : fileList) {
            FileUtil.delete(item);
        }
    }

    public static class Config {
        private String mDefaultDir;// The default storage directory of log.
        private String mDir;       // The storage directory of log.
        private String mFilePrefix = "Log";// The file prefix of log.
        private boolean mLogSwitch = true;  // The switch of log.
        private boolean mLog2ConsoleSwitch = true;  // The logcat's switch of log.
        private String mGlobalTag = null;  // The global tag of log.
        private boolean mTagIsSpace = true;  // The global tag is space.
        private boolean mLogHeadSwitch = true;  // The head's switch of log.
        private boolean mLog2FileSwitch = false; // The file's switch of log.
        private boolean mLogBorderSwitch = true;  // The border's switch of log.
        private boolean mSingleTagSwitch = true;  // The single tag of log.
        private int mConsoleFilter = V;     // The console's filter of log.
        private int mFileFilter = V;     // The file's filter of log.
        private int mStackDeep = 1;     // The stack's deep of log.
        private int mStackOffset = 0;     // The stack's offset of log.
        private boolean is2FilePrintConsole;//日志记录到文件时是否同时输出到控制台
        private int logFileHoldDays = -1;//本地记录的日志文件可保留的天数,小于等于0时代表永久保存，默认永久保存
        private Callback callback;

        private Config() {
            if (mDefaultDir != null) return;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && AppContext.get().getExternalCacheDir() != null)
                mDefaultDir = AppContext.get().getExternalCacheDir() + FILE_SEP + "log" + FILE_SEP;
            else {
                mDefaultDir = AppContext.get().getCacheDir() + FILE_SEP + "log" + FILE_SEP;
            }
        }

        public Config setLogFileHoldDays(int days) {
            this.logFileHoldDays = days;
            return this;
        }

        public Config setIs2FilePrintConsole(final boolean is2FilePrintConsole) {
            this.is2FilePrintConsole = is2FilePrintConsole;
            return this;
        }

        public Config setLogSwitch(final boolean logSwitch) {
            mLogSwitch = logSwitch;
            return this;
        }

        public Config setConsoleSwitch(final boolean consoleSwitch) {
            mLog2ConsoleSwitch = consoleSwitch;
            return this;
        }

        public Config setGlobalTag(final String tag) {
            if (isSpace(tag)) {
                mGlobalTag = "";
                mTagIsSpace = true;
            } else {
                mGlobalTag = tag;
                mTagIsSpace = false;
            }
            return this;
        }

        public Config setLogHeadSwitch(final boolean logHeadSwitch) {
            mLogHeadSwitch = logHeadSwitch;
            return this;
        }

        public Config setLog2FileSwitch(final boolean log2FileSwitch) {
            mLog2FileSwitch = log2FileSwitch;
            return this;
        }

        public Config setDir(final String dir) {
            if (isSpace(dir)) {
                mDir = null;
            } else {
                mDir = dir.endsWith(FILE_SEP) ? dir : dir + FILE_SEP;
            }
            return this;
        }

        public Config setDir(final File dir) {
            mDir = dir == null ? null : dir.getAbsolutePath() + FILE_SEP;
            return this;
        }

        public Config setFilePrefix(final String filePrefix) {
            if (isSpace(filePrefix)) {
                mFilePrefix = "util";
            } else {
                mFilePrefix = filePrefix;
            }
            return this;
        }

        public Config setBorderSwitch(final boolean borderSwitch) {
            mLogBorderSwitch = borderSwitch;
            return this;
        }

        public Config setSingleTagSwitch(final boolean singleTagSwitch) {
            mSingleTagSwitch = singleTagSwitch;
            return this;
        }

        public Config setConsoleFilter(@TYPE final int consoleFilter) {
            mConsoleFilter = consoleFilter;
            return this;
        }

        public Config setFileFilter(@TYPE final int fileFilter) {
            mFileFilter = fileFilter;
            return this;
        }

        public Config setStackDeep(@IntRange(from = 1) final int stackDeep) {
            mStackDeep = stackDeep;
            return this;
        }

        public Config setStackOffset(@IntRange(from = 0) final int stackOffset) {
            mStackOffset = stackOffset;
            return this;
        }

        public void setCallback(Callback callback) {
            this.callback = callback;
        }

        @Override
        public String toString() {
            return "switch: " + mLogSwitch
                    + LINE_SEP + "console: " + mLog2ConsoleSwitch
                    + LINE_SEP + "tag: " + (mTagIsSpace ? "null" : mGlobalTag)
                    + LINE_SEP + "head: " + mLogHeadSwitch
                    + LINE_SEP + "file: " + mLog2FileSwitch
                    + LINE_SEP + "dir: " + (mDir == null ? mDefaultDir : mDir)
                    + LINE_SEP + "filePrefix: " + mFilePrefix
                    + LINE_SEP + "border: " + mLogBorderSwitch
                    + LINE_SEP + "singleTag: " + mSingleTagSwitch
                    + LINE_SEP + "consoleFilter: " + T[mConsoleFilter - V]
                    + LINE_SEP + "fileFilter: " + T[mFileFilter - V]
                    + LINE_SEP + "stackDeep: " + mStackDeep
                    + LINE_SEP + "mStackOffset: " + mStackOffset;
        }
    }

    private static class TagHead {
        String tag;
        String[] consoleHead;
        String fileHead;

        TagHead(String tag, String[] consoleHead, String fileHead) {
            this.tag = tag;
            this.consoleHead = consoleHead;
            this.fileHead = fileHead;
        }
    }

    public static class LogHead {
        String tag;
        String content;

        public LogHead(String tag, String content) {
            this.tag = tag;
            this.content = content;
        }
    }

    public interface Callback {

        /**
         * 获取自定义log头部信息(创建log文件时回调)
         */
        List<LogHead> getCustomLogHead();

    }

    //----------------------------------------------------------------------------------------------

    private static class LogPrinter {

        private final Worker worker = new Worker();

        public void println(String bizType, int logLevel, String tag, Object... content) {
            try {
                final long timeMillis = System.currentTimeMillis();
                com.richard.library.context.util.ThreadUtil.executeBySingle(new com.richard.library.context.util.ThreadUtil.RunTask() {
                    @Override
                    public void runEvent() {
                        if (!worker.isStarted()) {
                            worker.start();
                        }
                        worker.enqueue(new LogItem(timeMillis, bizType, logLevel, tag, content));
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        /**
         * Do the real job of writing log to file.
         */
        void doPrintln(LogItem logItem) {
            int type_high = logItem.level & 0xf0;
            int type_low = logItem.level & 0x0f;

            final TagHead tagHead = processTagAndHead(logItem.tag);
            String body = processBody(type_high, logItem.content);
            if (CONFIG.is2FilePrintConsole
                    || (CONFIG.mLog2ConsoleSwitch && type_low >= CONFIG.mConsoleFilter && type_high != FILE)) {
                print2Console(type_low, tagHead.tag, tagHead.consoleHead, body);
            }

            if (!((CONFIG.mLog2FileSwitch || type_high == FILE) && type_low >= CONFIG.mFileFilter)) {
                return;
            }

            //删除已满最大保留天数的日志文件
            long currentTime = System.currentTimeMillis();
            if (CONFIG.logFileHoldDays > 0 && currentTime - lastClearLogFileTime >= 1000 * 60 * 60 * 24) {
                clearLogFile(CONFIG.logFileHoldDays);
                lastClearLogFileTime = currentTime;
            }

            String msg = tagHead.fileHead + body;
            long timeMills = System.currentTimeMillis();
            String time = FORMAT.format(new Date(timeMills)).substring(6);
            String fullPath = getLogFile(timeMills, logItem.bizType);

            StringBuilder sb = new StringBuilder();
            sb.append(time)
                    .append(T[type_low - V])
                    .append("|")
                    .append(logItem.tag)
                    .append("|")
                    .append(msg)
                    .append(LINE_SEP);

            try {
                File logFile = new File(fullPath);
                if (!logFile.exists()) {
                    File parent = logFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }

                    if (!logFile.createNewFile()) {
                        return;
                    }
                    FileUtil.saveTextFile(getLogHeadContent(), logFile, true);
                }
                FileUtil.saveTextFile(sb.toString(), logFile, true);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }


        private static class LogItem {

            long timeMillis;
            String bizType;
            int level;
            String tag;
            Object[] content;

            LogItem(long timeMillis, String bizType, int level, String tag, Object... content) {
                this.timeMillis = timeMillis;
                this.bizType = bizType;
                this.level = level;
                this.tag = tag;
                this.content = content;
            }
        }

        /**
         * Work in background, we can enqueue the logs, and the worker will dispatch them.
         */
        private class Worker extends com.richard.library.context.util.ThreadUtil.RunTask {

            private final BlockingQueue<LogItem> logs = new LinkedBlockingQueue<>();

            /**
             * Enqueue the log.
             *
             * @param log the log to be written to file
             */
            void enqueue(LogItem log) {
                try {
                    logs.put(log);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            /**
             * Whether the worker is started.
             *
             * @return true if started, false otherwise
             */
            boolean isStarted() {
                return !isDone();
            }

            /**
             * Start the worker.
             */
            void start() {
                com.richard.library.context.util.ThreadUtil.executeByCached(this);
            }

            @Override
            public void runEvent() {
                LogItem log;
                try {
                    while ((log = logs.take()) != null) {
                        doPrintln(log);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}