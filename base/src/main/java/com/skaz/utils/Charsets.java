package com.skaz.utils;

import java.nio.charset.Charset;

/**
 * @author jungle
 */
public abstract class Charsets {

    private Charsets() {
    }

    public static Charset forName(String charset) {
        return Charset.forName(charset);
    }

    /**
     * 获得当前系统默认编码
     * 中文操作系统中打印GBK
     */
    public static Charset getDefaultCharset() {
        return Charset.defaultCharset();
    }

    /**
     * US-ASCII: seven-bit ASCII, the Basic Latin block of the Unicode character set (ISO646-US).
     */
    public static final Charset US_ASCII = Charset.forName("US-ASCII");

    /**
     * ISO-8859-1: ISO Latin Alphabet Number 1 (ISO-LATIN-1).
     */

    public static final String ISO_8859_1_VALUE = "ISO-8859-1";

    public static final Charset ISO_8859_1 = Charset.forName(ISO_8859_1_VALUE);

    /**
     * UTF-8: eight-bit UCS Transformation Format.
     */
    public static final String UTF_8_VALUE = "UTF-8";

    public static final Charset UTF_8 = Charset.forName(UTF_8_VALUE);

    /**
     * UTF-16BE: sixteen-bit UCS Transformation Format, big-endian byte order.
     */
    public static final Charset UTF_16BE = Charset.forName("UTF-16BE");

    /**
     * UTF-16LE: sixteen-bit UCS Transformation Format, little-endian byte order.
     */
    public static final Charset UTF_16LE = Charset.forName("UTF-16LE");

    /**
     * UTF-16: sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order
     * mark.
     */
    public static final Charset UTF_16 = Charset.forName("UTF-16");

    public static final String GBK_VALUE = "GBK";

    public static final Charset GBK = Charset.forName(GBK_VALUE);

}
