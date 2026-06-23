@file:Suppress("SENSELESS_COMPARISON")

package com.richard.library.context.util

import android.text.TextUtils
import org.jetbrains.annotations.Contract
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.Locale
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.max
import kotlin.math.min


/**
 * 空字符串 `""`。
 *
 * @since 2.0
 */
const val EMPTY: String = ""

/**
 * 表示索引查找失败。
 *
 * @since 2.1
 */
const val INDEX_NOT_FOUND: Int = -1

/**
 * 填充常量可扩展的最大长度。
 */
private const val PAD_LIMIT = 8192

// 空值检查
//-----------------------------------------------------------------------
/**
 * 检查字符串是否为空（""）或 null。
 *
 * <pre>
 * StringUtils.isEmpty(null)      = true
 * StringUtils.isEmpty("")        = true
 * StringUtils.isEmpty(" ")       = false
 * StringUtils.isEmpty("bob")     = false
 * StringUtils.isEmpty("  bob  ") = false
</pre>
 *
 * 注意：此方法在 Lang 2.0 版本中发生了变化。
 * 它不再自动去除字符串两端的空白。
 * 该功能可在 isBlank() 中使用。
 *
 * @param str 要检查的字符串，可以为 null
 * @return 如果字符串为空或 null，则返回 `true`
 */
@OptIn(ExperimentalContracts::class)
@Contract("returns false -> this != null")
fun String?.isEmpty(): Boolean {
    contract {
        returns(false) implies (this@isEmpty != null)
    }
    return this == null || this.length == 0
}

/**
 * 检查字符串是否非空（""）且非 null。
 *
 * <pre>
 * StringUtils.isNotEmpty(null)      = false
 * StringUtils.isNotEmpty("")        = false
 * StringUtils.isNotEmpty(" ")       = true
 * StringUtils.isNotEmpty("bob")     = true
 * StringUtils.isNotEmpty("  bob  ") = true
</pre>
 *
 * @param str 要检查的字符串，可以为 null
 * @return 如果字符串非空且非 null，则返回 `true`
 */
@OptIn(ExperimentalContracts::class)
@Contract("returns true -> this != null")
fun String?.isNotEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotEmpty != null)
    }
    return !this.isEmpty()
}

/**
 * 检查字符串是否为空白、空（""）或 null。
 *
 * <pre>
 * StringUtils.isBlank(null)      = true
 * StringUtils.isBlank("")        = true
 * StringUtils.isBlank(" ")       = true
 * StringUtils.isBlank("bob")     = false
 * StringUtils.isBlank("  bob  ") = false
</pre>
 *
 * @param str 要检查的字符串，可以为 null
 * @return 如果字符串为 null、空或仅含空白，则返回 `true`
 * @since 2.0
 */
@OptIn(ExperimentalContracts::class)
@Contract("returns false -> this != null")
fun String?.isBlank(): Boolean {
    contract {
        returns(false) implies (this@isBlank != null)
    }
    var strLen: Int = 0
    if (this == null || (this.length.also { strLen = it }) == 0) {
        return true
    }
    for (i in 0 until strLen) {
        if (!Character.isWhitespace(this[i])) {
            return false
        }
    }
    return true
}

/**
 * 检查字符串是否非空（""）、非 null 且非仅空白。
 *
 * <pre>
 * StringUtils.isNotBlank(null)      = false
 * StringUtils.isNotBlank("")        = false
 * StringUtils.isNotBlank(" ")       = false
 * StringUtils.isNotBlank("bob")     = true
 * StringUtils.isNotBlank("  bob  ") = true
</pre>
 *
 * @param str 要检查的字符串，可以为 null
 * @return 如果字符串非空、非 null 且非仅空白，则返回 `true`
 * @since 2.0
 */
@OptIn(ExperimentalContracts::class)
@Contract("returns true -> this != null")
fun String?.isNotBlank(): Boolean {
    contract {
        returns(true) implies (this@isNotBlank != null)
    }
    return !this.isBlank()
}

// 去除两端空白
//-----------------------------------------------------------------------
/**
 * 移除字符串两端的控制字符（字符 <= 32），
 * 若为 null 则返回空字符串（""）。
 *
 * <pre>
 * StringUtils.clean(null)          = ""
 * StringUtils.clean("")            = ""
 * StringUtils.clean("abc")         = "abc"
 * StringUtils.clean("    abc    ") = "abc"
 * StringUtils.clean("     ")       = ""
</pre>
 *
 * @param str 要清理的字符串，可以为 null
 * @return 去除两端空白后的文本，永不为 `null`
 * @see java.lang.String.trim
 */
fun String?.clean(): String {
    return this?.trim { it <= ' ' } ?: EMPTY
}

/**
 * 移除字符串两端的控制字符（字符 <= 32），
 * 若为 null 则返回 `null`。
 *
 * 该方法使用 [String.trim] 去除空白。
 * 去除操作会移除两端 <= 32 的字符。
 * 若要移除空白字符，请使用 [.strip]。
 *
 * 若要自定义移除的字符，请使用 [.strip] 系列方法。
 *
 * <pre>
 * StringUtils.trim(null)          = null
 * StringUtils.trim("")            = ""
 * StringUtils.trim("     ")       = ""
 * StringUtils.trim("abc")         = "abc"
 * StringUtils.trim("    abc    ") = "abc"
</pre>
 *
 * @param str 要去除空白的字符串，可以为 null
 * @return 去除空白后的字符串，若输入为 null 则返回 `null`
 */
fun String?.trim(): String? {
    return this?.trim { it <= ' ' }
}

/**
 * 移除字符串两端的控制字符（字符 <= 32），
 * 若去除后为空（""）或原字符串为 null，则返回 `null`。
 *
 * 该方法使用 [String.trim] 去除空白。
 * 去除操作会移除两端 <= 32 的字符。
 * 若要移除空白字符，请使用 [.stripToNull]。
 *
 * <pre>
 * StringUtils.trimToNull(null)          = null
 * StringUtils.trimToNull("")            = null
 * StringUtils.trimToNull("     ")       = null
 * StringUtils.trimToNull("abc")         = "abc"
 * StringUtils.trimToNull("    abc    ") = "abc"
</pre>
 *
 * @param str 要去除空白的字符串，可以为 null
 * @return 去除空白后的字符串，
 * 若仅含 <=32 的字符、为空或为 null 则返回 `null`
 * @since 2.0
 */
fun String?.trimToNull(): String? {
    val ts = this.trim()
    return if (this.isEmpty()) null else ts
}

/**
 * 移除字符串两端的控制字符（字符 <= 32），
 * 若去除后为空（""）或原字符串为 null，则返回空字符串（""）。
 *
 * 该方法使用 [String.trim] 去除空白。
 * 去除操作会移除两端 <= 32 的字符。
 * 若要移除空白字符，请使用 [.stripToEmpty]。
 *
 * <pre>
 * StringUtils.trimToEmpty(null)          = ""
 * StringUtils.trimToEmpty("")            = ""
 * StringUtils.trimToEmpty("     ")       = ""
 * StringUtils.trimToEmpty("abc")         = "abc"
 * StringUtils.trimToEmpty("    abc    ") = "abc"
</pre>
 *
 * @param str 要去除空白的字符串，可以为 null
 * @return 去除空白后的字符串，若输入为 null 则返回空字符串
 * @since 2.0
 */
fun String?.trimToEmpty(): String {
    return this?.trim { it <= ' ' } ?: EMPTY
}

// 剥离（自定义去除字符）
//-----------------------------------------------------------------------
/**
 * 从字符串的开头和结尾剥离空白字符。
 *
 * 此方法与 [.trim] 类似，但专门移除空白字符。
 * 空白字符由 [Character.isWhitespace] 定义。
 *
 * 输入 `null` 字符串会返回 `null`。
 *
 * <pre>
 * StringUtils.strip(null)     = null
 * StringUtils.strip("")       = ""
 * StringUtils.strip("   ")    = ""
 * StringUtils.strip("abc")    = "abc"
 * StringUtils.strip("  abc")  = "abc"
 * StringUtils.strip("abc  ")  = "abc"
 * StringUtils.strip(" abc ")  = "abc"
 * StringUtils.strip(" ab c ") = "ab c"
</pre>
 *
 * @param str 要移除空白的字符串，可以为 null
 * @return 剥离后的字符串，若输入为 null 则返回 `null`
 */
fun String?.strip(): String? {
    return this.strip(null)
}

/**
 * 从字符串的开头和结尾剥离空白字符，
 * 若剥离后为空（""）则返回 `null`。
 *
 * 此方法与 [.trimToNull] 类似，但专门移除空白字符。
 * 空白字符由 [Character.isWhitespace] 定义。
 *
 * <pre>
 * StringUtils.stripToNull(null)     = null
 * StringUtils.stripToNull("")       = null
 * StringUtils.stripToNull("   ")    = null
 * StringUtils.stripToNull("abc")    = "abc"
 * StringUtils.stripToNull("  abc")  = "abc"
 * StringUtils.stripToNull("abc  ")  = "abc"
 * StringUtils.stripToNull(" abc ")  = "abc"
 * StringUtils.stripToNull(" ab c ") = "ab c"
</pre>
 *
 * @param str 要剥离的字符串，可以为 null
 * @return 剥离后的字符串，
 * 若仅含空白、为空或为 null 则返回 `null`
 * @since 2.0
 */
fun String?.stripToNull(): String? {
    var str = this
    if (str == null) {
        return null
    }
    str = this.strip(null)
    return if (str?.length == 0) null else str
}

/**
 * 从字符串的开头和结尾剥离空白字符，
 * 若输入为 null 则返回空字符串。
 *
 * 此方法与 [.trimToEmpty] 类似，但专门移除空白字符。
 * 空白字符由 [Character.isWhitespace] 定义。
 *
 * <pre>
 * StringUtils.stripToEmpty(null)     = ""
 * StringUtils.stripToEmpty("")       = ""
 * StringUtils.stripToEmpty("   ")    = ""
 * StringUtils.stripToEmpty("abc")    = "abc"
 * StringUtils.stripToEmpty("  abc")  = "abc"
 * StringUtils.stripToEmpty("abc  ")  = "abc"
 * StringUtils.stripToEmpty(" abc ")  = "abc"
 * StringUtils.stripToEmpty(" ab c ") = "ab c"
</pre>
 *
 * @param str 要剥离的字符串，可以为 null
 * @return 剥离后的字符串，若输入为 null 则返回空字符串
 * @since 2.0
 */
fun String?.stripToEmpty(): String? {
    return if (this == null) EMPTY else this.strip(null)
}

/**
 * 从字符串的开头和结尾剥离指定的一组字符。
 * 此方法与 [String.trim] 类似，但允许自定义要移除的字符。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 *
 * 若 stripChars 为 `null`，则移除由 [Character.isWhitespace] 定义的空白。
 * 也可以直接使用 [.strip]。
 *
 * <pre>
 * StringUtils.strip(null, *)          = null
 * StringUtils.strip("", *)            = ""
 * StringUtils.strip("abc", null)      = "abc"
 * StringUtils.strip("  abc", null)    = "abc"
 * StringUtils.strip("abc  ", null)    = "abc"
 * StringUtils.strip(" abc ", null)    = "abc"
 * StringUtils.strip("  abcyx", "xyz") = "  abc"
</pre>
 *
 * @param str        要移除字符的字符串，可以为 null
 * @param stripChars 要移除的字符集，为 null 时表示移除空白
 * @return 剥离后的字符串，若输入为 null 则返回 `null`
 */
fun String?.strip(stripChars: String?): String? {
    var str = this
    if (str.isEmpty()) {
        return str
    }
    str = str.stripStart(stripChars)
    return str.stripEnd(stripChars)
}

/**
 * 从字符串的开头剥离指定的一组字符。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 *
 * 若 stripChars 为 `null`，则移除由 [Character.isWhitespace] 定义的空白。
 *
 * <pre>
 * StringUtils.stripStart(null, *)          = null
 * StringUtils.stripStart("", *)            = ""
 * StringUtils.stripStart("abc", "")        = "abc"
 * StringUtils.stripStart("abc", null)      = "abc"
 * StringUtils.stripStart("  abc", null)    = "abc"
 * StringUtils.stripStart("abc  ", null)    = "abc  "
 * StringUtils.stripStart(" abc ", null)    = "abc "
 * StringUtils.stripStart("yxabc  ", "xyz") = "abc  "
</pre>
 *
 * @param str        要移除字符的字符串，可以为 null
 * @param stripChars 要移除的字符集，为 null 时表示移除空白
 * @return 剥离后的字符串，若输入为 null 则返回 `null`
 */
fun String?.stripStart(stripChars: String?): String? {
    var strLen = 0
    if (this == null || (this.length.also { strLen = it }) == 0) {
        return this
    }
    var start = 0
    if (stripChars == null) {
        while ((start != strLen) && Character.isWhitespace(this[start])) {
            start++
        }
    } else if (stripChars.isEmpty()) {
        return this
    } else {
        while ((start != strLen) && (stripChars.indexOf(this[start]) != INDEX_NOT_FOUND)) {
            start++
        }
    }
    return this.substring(start)
}

/**
 * 从字符串的结尾剥离指定的一组字符。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 *
 * 若 stripChars 为 `null`，则移除由 [Character.isWhitespace] 定义的空白。
 *
 * <pre>
 * StringUtils.stripEnd(null, *)          = null
 * StringUtils.stripEnd("", *)            = ""
 * StringUtils.stripEnd("abc", "")        = "abc"
 * StringUtils.stripEnd("abc", null)      = "abc"
 * StringUtils.stripEnd("  abc", null)    = "  abc"
 * StringUtils.stripEnd("abc  ", null)    = "abc"
 * StringUtils.stripEnd(" abc ", null)    = " abc"
 * StringUtils.stripEnd("  abcyx", "xyz") = "  abc"
 * StringUtils.stripEnd("120.00", ".0")   = "12"
</pre>
 *
 * @param str        要移除字符的字符串，可以为 null
 * @param stripChars 要移除的字符集，为 null 时表示移除空白
 * @return 剥离后的字符串，若输入为 null 则返回 `null`
 */
fun String?.stripEnd(stripChars: String?): String? {
    var end = 0
    if (this == null || (this.length.also { end = it }) == 0) {
        return this
    }

    if (stripChars == null) {
        while ((end != 0) && Character.isWhitespace(this[end - 1])) {
            end--
        }
    } else if (stripChars.isEmpty()) {
        return this
    } else {
        while ((end != 0) && (stripChars.indexOf(this[end - 1]) != INDEX_NOT_FOUND)) {
            end--
        }
    }
    return this.substring(0, end)
}

// 批量剥离
//-----------------------------------------------------------------------
/**
 * 对数组中的每个字符串，去除开头和结尾的空白。
 * 空白字符由 [Character.isWhitespace] 定义。
 *
 * 每次都会返回一个新数组，长度为 0 时除外。
 * 输入 `null` 数组会返回 `null`。
 * 空数组会返回自身。
 * 数组中的 `null` 元素会被保留。
 *
 * <pre>
 * StringUtils.stripAll(null)             = null
 * StringUtils.stripAll([])               = []
 * StringUtils.stripAll(["abc", "  abc"]) = ["abc", "abc"]
 * StringUtils.stripAll(["abc  ", null])  = ["abc", null]
</pre>
 *
 * @param strs 要去除空白的数组，可以为 null
 * @return 处理后的字符串数组，若输入为 null 则返回 `null`
 */
fun Array<String?>?.stripAll(): Array<String?>? {
    return this.stripAll(null)
}

/**
 * 对数组中的每个字符串，从开头和结尾剥离指定的一组字符。
 * 空白字符由 [Character.isWhitespace] 定义。
 *
 * 每次都会返回一个新数组，长度为 0 时除外。
 * 输入 `null` 数组会返回 `null`。
 * 空数组会返回自身。
 * 数组中的 `null` 元素会被保留。
 * 若 stripChars 为 `null`，则移除空白字符。
 *
 * <pre>
 * StringUtils.stripAll(null, *)                = null
 * StringUtils.stripAll([], *)                  = []
 * StringUtils.stripAll(["abc", "  abc"], null) = ["abc", "abc"]
 * StringUtils.stripAll(["abc  ", null], null)  = ["abc", null]
 * StringUtils.stripAll(["abc  ", null], "yz")  = ["abc  ", null]
 * StringUtils.stripAll(["yabcz", null], "yz")  = ["abc", null]
</pre>
 *
 * @param strs       要移除字符的数组，可以为 null
 * @param stripChars 要移除的字符集，为 null 时表示移除空白
 * @return 处理后的字符串数组，若输入为 null 则返回 `null`
 */
fun Array<String?>?.stripAll(stripChars: String?): Array<String?>? {
    var strsLen = 0
    if (this == null || (this.size.also { strsLen = it }) == 0) {
        return this
    }
    val newArr = arrayOfNulls<String>(strsLen)
    for (i in 0..strsLen) {
        newArr[i] = this[i].strip(stripChars)
    }
    return newArr
}

// 相等判断
//-----------------------------------------------------------------------
/**
 * 比较两个字符串，相等则返回 `true`。
 *
 * 处理 `null` 不会抛出异常。两个 `null` 视为相等。
 * 比较区分大小写。
 *
 * <pre>
 * StringUtils.equals(null, null)   = true
 * StringUtils.equals(null, "abc")  = false
 * StringUtils.equals("abc", null)  = false
 * StringUtils.equals("abc", "abc") = true
 * StringUtils.equals("abc", "ABC") = false
</pre>
 *
 * @param str1 第一个字符串，可以为 null
 * @param str2 第二个字符串，可以为 null
 * @return 如果字符串相等（区分大小写）或都为 `null`，则返回 `true`
 * @see java.lang.String.equals
 */
fun String?.equals(str2: String?): Boolean {
    return if (this == null) str2 == null else (this == str2)
}

/**
 * 比较两个字符串，忽略大小写相等则返回 `true`。
 *
 * 处理 `null` 不会抛出异常。两个 `null` 视为相等。
 * 比较不区分大小写。
 *
 * <pre>
 * StringUtils.equalsIgnoreCase(null, null)   = true
 * StringUtils.equalsIgnoreCase(null, "abc")  = false
 * StringUtils.equalsIgnoreCase("abc", null)  = false
 * StringUtils.equalsIgnoreCase("abc", "abc") = true
 * StringUtils.equalsIgnoreCase("abc", "ABC") = true
</pre>
 *
 * @param str1 第一个字符串，可以为 null
 * @param str2 第二个字符串，可以为 null
 * @return 如果字符串相等（不区分大小写）或都为 `null`，则返回 `true`
 * @see java.lang.String.equalsIgnoreCase
 */
fun String?.equalsIgnoreCase(str2: String?): Boolean {
    return this?.equals(str2, ignoreCase = true) ?: (str2 == null)
}

// 索引查找
//-----------------------------------------------------------------------
/**
 * 查找字符串中指定字符第一次出现的位置，兼容 `null`。
 * 此方法使用 [String.indexOf]。
 *
 * 输入 `null` 或空字符串（""）会返回 `INDEX_NOT_FOUND (-1)`。
 *
 * <pre>
 * StringUtils.indexOf(null, *)         = -1
 * StringUtils.indexOf("", *)           = -1
 * StringUtils.indexOf("aabaabaa", 'a') = 0
 * StringUtils.indexOf("aabaabaa", 'b') = 2
</pre>
 *
 * @param str        要检查的字符串，可以为 null
 * @param searchChar 要查找的字符
 * @return 目标字符第一次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 -1
 * @since 2.0
 */
fun String?.indexOf(searchChar: Char): Int {
    if (this.isEmpty()) {
        return INDEX_NOT_FOUND
    }
    return (this as CharSequence).indexOf(searchChar)
}

/**
 * 从指定起始位置开始，查找字符串中指定字符第一次出现的位置，兼容 `null`。
 * 此方法使用 [String.indexOf]。
 *
 * 输入 `null` 或空字符串（""）会返回 `-1`。
 * 负数起始位置视为 0。
 * 起始位置大于字符串长度则返回 `-1`。
 *
 * <pre>
 * StringUtils.indexOf(null, *, *)          = -1
 * StringUtils.indexOf("", *, *)            = -1
 * StringUtils.indexOf("aabaabaa", 'b', 0)  = 2
 * StringUtils.indexOf("aabaabaa", 'b', 3)  = 5
 * StringUtils.indexOf("aabaabaa", 'b', 9)  = -1
 * StringUtils.indexOf("aabaabaa", 'b', -1) = 2
</pre>
 *
 * @param str        要检查的字符串，可以为 null
 * @param searchChar 要查找的字符
 * @param startPos   起始位置，负数视为 0
 * @return 目标字符第一次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 -1
 * @since 2.0
 */
fun String?.indexOf(searchChar: Char, startPos: Int): Int {
    if (this.isEmpty()) {
        return INDEX_NOT_FOUND
    }
    return (this as CharSequence).indexOf(searchChar, startPos)
}

/**
 * 查找字符串中指定子串第一次出现的位置，兼容 `null`。
 * 此方法使用 [String.indexOf]。
 *
 * 输入 `null` 字符串会返回 `-1`。
 *
 * <pre>
 * StringUtils.indexOf(null, *)          = -1
 * StringUtils.indexOf(*, null)          = -1
 * StringUtils.indexOf("", "")           = 0
 * StringUtils.indexOf("", *)            = -1（除 * 为 "" 时）
 * StringUtils.indexOf("aabaabaa", "a")  = 0
 * StringUtils.indexOf("aabaabaa", "b")  = 2
 * StringUtils.indexOf("aabaabaa", "ab") = 1
 * StringUtils.indexOf("aabaabaa", "")   = 0
</pre>
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @return 目标子串第一次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 -1
 * @since 2.0
 */
fun String?.indexOf(searchStr: String?): Int {
    if (this == null || searchStr == null) {
        return INDEX_NOT_FOUND
    }
    return (this as CharSequence).indexOf(searchStr)
}

/**
 * 查找字符串中指定子串第 N 次出现的位置，兼容 `null`。
 * 此方法使用 [String.indexOf]。
 *
 * 输入 `null` 字符串会返回 `-1`。
 *
 * <pre>
 * StringUtils.ordinalIndexOf(null, *, *)          = -1
 * StringUtils.ordinalIndexOf(*, null, *)          = -1
 * StringUtils.ordinalIndexOf("", "", *)           = 0
 * StringUtils.ordinalIndexOf("aabaabaa", "a", 1)  = 0
 * StringUtils.ordinalIndexOf("aabaabaa", "a", 2)  = 1
 * StringUtils.ordinalIndexOf("aabaabaa", "b", 1)  = 2
 * StringUtils.ordinalIndexOf("aabaabaa", "b", 2)  = 5
 * StringUtils.ordinalIndexOf("aabaabaa", "ab", 1) = 1
 * StringUtils.ordinalIndexOf("aabaabaa", "ab", 2) = 4
 * StringUtils.ordinalIndexOf("aabaabaa", "", 1)   = 0
 * StringUtils.ordinalIndexOf("aabaabaa", "", 2)   = 0
</pre>
 *
 * 注意：'head(String str, int n)' 可按如下方式实现：
 *
 * <pre>
 * str.substring(0, lastOrdinalIndexOf(str, "\n", n))
</pre>
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @param ordinal   要查找的第 N 个 `searchStr`
 * @return 目标子串第 N 次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 `-1`
 * @since 2.1
 */
fun String?.ordinalIndexOf(searchStr: String?, ordinal: Int): Int {
    return ordinalIndexOf(this, searchStr, ordinal, false)
}

/**
 * 查找字符串中指定子串第 N 次出现的位置，兼容 `null`。
 * 此方法使用 [String.indexOf]。
 *
 * 输入 `null` 字符串会返回 `-1`。
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @param ordinal   要查找的第 N 个 `searchStr`
 * @param lastIndex true 表示反向查找第 N 个，false 表示正向查找
 * @return 目标子串第 N 次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 `-1`
 */
// ordinalIndexOf 与 lastOrdinalIndexOf 的共享代码
private fun ordinalIndexOf(
    str: String?,
    searchStr: String?,
    ordinal: Int,
    lastIndex: Boolean
): Int {
    if (str == null || searchStr == null || ordinal <= 0) {
        return INDEX_NOT_FOUND
    }
    if (searchStr.length == 0) {
        return if (lastIndex) str.length else 0
    }
    var found = 0
    var index = if (lastIndex) str.length else INDEX_NOT_FOUND
    do {
        if (lastIndex) {
            index = str.lastIndexOf(searchStr, index - 1)
        } else {
            index = str.indexOf(searchStr, index + 1)
        }
        if (index < 0) {
            return index
        }
        found++
    } while (found < ordinal)
    return index
}

/**
 * 从指定起始位置开始，查找字符串中指定子串第一次出现的位置，兼容 `null`。
 * 此方法使用 [String.indexOf]。
 *
 * 输入 `null` 字符串会返回 `-1`。
 * 负数起始位置视为 0。
 * 空搜索串总能匹配。
 * 起始位置大于字符串长度时，仅能匹配空搜索串。
 *
 * <pre>
 * StringUtils.indexOf(null, *, *)          = -1
 * StringUtils.indexOf(*, null, *)          = -1
 * StringUtils.indexOf("", "", 0)           = 0
 * StringUtils.indexOf("", *, 0)            = -1（除 * 为 "" 时）
 * StringUtils.indexOf("aabaabaa", "a", 0)  = 0
 * StringUtils.indexOf("aabaabaa", "b", 0)  = 2
 * StringUtils.indexOf("aabaabaa", "ab", 0) = 1
 * StringUtils.indexOf("aabaabaa", "b", 3)  = 5
 * StringUtils.indexOf("aabaabaa", "b", 9)  = -1
 * StringUtils.indexOf("aabaabaa", "b", -1) = 2
 * StringUtils.indexOf("aabaabaa", "", 2)   = 2
 * StringUtils.indexOf("abc", "", 9)        = 3
</pre>
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @param startPos  起始位置，负数视为 0
 * @return 目标子串第一次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 -1
 * @since 2.0
 */
fun String?.indexOf(searchStr: String?, startPos: Int): Int {
    if (this == null || searchStr == null) {
        return INDEX_NOT_FOUND
    }
    // JDK1.2/JDK1.3 存在 bug，当 startPos > 字符串长度且搜索串为空时需特殊处理
    if (searchStr.length == 0 && startPos >= this.length) {
        return this.length
    }
    return (this as CharSequence).indexOf(searchStr, startPos)
}

/**
 * 不区分大小写查找字符串中指定子串第一次出现的位置。
 *
 * 输入 `null` 字符串会返回 `-1`。
 * 负数起始位置视为 0。
 * 空搜索串总能匹配。
 * 起始位置大于字符串长度时，仅能匹配空搜索串。
 *
 * <pre>
 * StringUtils.indexOfIgnoreCase(null, *)          = -1
 * StringUtils.indexOfIgnoreCase(*, null)          = -1
 * StringUtils.indexOfIgnoreCase("", "")           = 0
 * StringUtils.indexOfIgnoreCase("aabaabaa", "a")  = 0
 * StringUtils.indexOfIgnoreCase("aabaabaa", "b")  = 2
 * StringUtils.indexOfIgnoreCase("aabaabaa", "ab") = 1
</pre>
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @return 目标子串第一次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 -1
 * @since 2.5
 */
@JvmOverloads
fun String?.indexOfIgnoreCase(searchStr: String?, startPos: Int = 0): Int {
    var startPos = startPos
    if (this == null || searchStr == null) {
        return INDEX_NOT_FOUND
    }
    if (startPos < 0) {
        startPos = 0
    }
    val endLimit = (this.length - searchStr.length) + 1
    if (startPos > endLimit) {
        return INDEX_NOT_FOUND
    }
    if (searchStr.length == 0) {
        return startPos
    }
    for (i in startPos..<endLimit) {
        if (this.regionMatches(i, searchStr, 0, searchStr.length, ignoreCase = true)) {
            return i
        }
    }
    return INDEX_NOT_FOUND
}

// 最后索引查找
//-----------------------------------------------------------------------
/**
 * 查找字符串中指定字符最后一次出现的位置，兼容 `null`。
 * 此方法使用 [String.lastIndexOf]。
 *
 * 输入 `null` 或空字符串（""）会返回 `-1`。
 *
 * <pre>
 * StringUtils.lastIndexOf(null, *)         = -1
 * StringUtils.lastIndexOf("", *)           = -1
 * StringUtils.lastIndexOf("aabaabaa", 'a') = 7
 * StringUtils.lastIndexOf("aabaabaa", 'b') = 5
</pre>
 *
 * @param str        要检查的字符串，可以为 null
 * @param searchChar 要查找的字符
 * @return 目标字符最后一次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 -1
 * @since 2.0
 */
fun String?.lastIndexOf(searchChar: Char): Int {
    if (this.isEmpty()) {
        return INDEX_NOT_FOUND
    }
    return (this as CharSequence).lastIndexOf(searchChar)
}

/**
 * 从指定起始位置反向查找，字符串中指定字符最后一次出现的位置，兼容 `null`。
 * 此方法使用 [String.lastIndexOf]。
 *
 * 输入 `null` 或空字符串（""）会返回 `-1`。
 * 负数起始位置返回 `-1`。
 * 起始位置大于字符串长度则搜索整个字符串。
 *
 * <pre>
 * StringUtils.lastIndexOf(null, *, *)          = -1
 * StringUtils.lastIndexOf("", *,  *)           = -1
 * StringUtils.lastIndexOf("aabaabaa", 'b', 8)  = 5
 * StringUtils.lastIndexOf("aabaabaa", 'b', 4)  = 2
 * StringUtils.lastIndexOf("aabaabaa", 'b', 0)  = -1
 * StringUtils.lastIndexOf("aabaabaa", 'b', 9)  = 5
 * StringUtils.lastIndexOf("aabaabaa", 'b', -1) = -1
 * StringUtils.lastIndexOf("aabaabaa", 'a', 0)  = 0
</pre>
 *
 * @param str        要检查的字符串，可以为 null
 * @param searchChar 要查找的字符
 * @param startPos   起始位置
 * @return 目标字符最后一次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 -1
 * @since 2.0
 */
fun String?.lastIndexOf(searchChar: Char, startPos: Int): Int {
    if (this.isEmpty()) {
        return INDEX_NOT_FOUND
    }
    return (this as CharSequence).lastIndexOf(searchChar, startPos)
}

/**
 * 查找字符串中指定子串最后一次出现的位置，兼容 `null`。
 * 此方法使用 [String.lastIndexOf]。
 *
 * 输入 `null` 字符串会返回 `-1`。
 *
 * <pre>
 * StringUtils.lastIndexOf(null, *)          = -1
 * StringUtils.lastIndexOf(*, null)          = -1
 * StringUtils.lastIndexOf("", "")           = 0
 * StringUtils.lastIndexOf("aabaabaa", "a")  = 7
 * StringUtils.lastIndexOf("aabaabaa", "b")  = 5
 * StringUtils.lastIndexOf("aabaabaa", "ab") = 4
 * StringUtils.lastIndexOf("aabaabaa", "")   = 8
</pre>
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @return 目标子串最后一次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 -1
 * @since 2.0
 */
fun String?.lastIndexOf(searchStr: String?): Int {
    if (this == null || searchStr == null) {
        return INDEX_NOT_FOUND
    }
    return (this as CharSequence).lastIndexOf(searchStr)
}

/**
 * 查找字符串中指定子串倒数第 N 次出现的位置，兼容 `null`。
 * 此方法使用 [String.lastIndexOf]。
 *
 * 输入 `null` 字符串会返回 `-1`。
 *
 * <pre>
 * StringUtils.lastOrdinalIndexOf(null, *, *)          = -1
 * StringUtils.lastOrdinalIndexOf(*, null, *)          = -1
 * StringUtils.lastOrdinalIndexOf("", "", *)           = 0
 * StringUtils.lastOrdinalIndexOf("aabaabaa", "a", 1)  = 7
 * StringUtils.lastOrdinalIndexOf("aabaabaa", "a", 2)  = 6
 * StringUtils.lastOrdinalIndexOf("aabaabaa", "b", 1)  = 5
 * StringUtils.lastOrdinalIndexOf("aabaabaa", "b", 2)  = 2
 * StringUtils.lastOrdinalIndexOf("aabaabaa", "ab", 1) = 4
 * StringUtils.lastOrdinalIndexOf("aabaabaa", "ab", 2) = 1
 * StringUtils.lastOrdinalIndexOf("aabaabaa", "", 1)   = 8
 * StringUtils.lastOrdinalIndexOf("aabaabaa", "", 2)   = 8
</pre>
 *
 * 注意：'tail(String str, int n)' 可按如下方式实现：
 *
 * <pre>
 * str.substring(lastOrdinalIndexOf(str, "\n", n) + 1)
</pre>
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @param ordinal   要查找的倒数第 N 个 `searchStr`
 * @return 目标子串倒数第 N 次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 `-1`
 * @since 2.5
 */
fun String?.lastOrdinalIndexOf(searchStr: String?, ordinal: Int): Int {
    return ordinalIndexOf(this, searchStr, ordinal, true)
}

/**
 * 从指定起始位置反向查找，字符串中指定子串最后一次出现的位置，兼容 `null`。
 * 此方法使用 [String.lastIndexOf]。
 *
 * 输入 `null` 字符串会返回 `-1`。
 * 负数起始位置返回 `-1`。
 * 空搜索串总能匹配（起始位置非负时）。
 * 起始位置大于字符串长度则搜索整个字符串。
 *
 * <pre>
 * StringUtils.lastIndexOf(null, *, *)          = -1
 * StringUtils.lastIndexOf(*, null, *)          = -1
 * StringUtils.lastIndexOf("aabaabaa", "a", 8)  = 7
 * StringUtils.lastIndexOf("aabaabaa", "b", 8)  = 5
 * StringUtils.lastIndexOf("aabaabaa", "ab", 8) = 4
 * StringUtils.lastIndexOf("aabaabaa", "b", 9)  = 5
 * StringUtils.lastIndexOf("aabaabaa", "b", -1) = -1
 * StringUtils.lastIndexOf("aabaabaa", "a", 0)  = 0
 * StringUtils.lastIndexOf("aabaabaa", "b", 0)  = -1
</pre>
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @param startPos  起始位置
 * @return 目标子串最后一次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 -1
 * @since 2.0
 */
fun String?.lastIndexOf(searchStr: String?, startPos: Int): Int {
    if (this == null || searchStr == null) {
        return INDEX_NOT_FOUND
    }
    return (this as CharSequence).lastIndexOf(searchStr, startPos)
}

/**
 * 不区分大小写查找字符串中指定子串最后一次出现的位置。
 *
 * 输入 `null` 字符串会返回 `-1`。
 * 负数起始位置返回 `-1`。
 * 空搜索串总能匹配（起始位置非负时）。
 * 起始位置大于字符串长度则搜索整个字符串。
 *
 * <pre>
 * StringUtils.lastIndexOfIgnoreCase(null, *)          = -1
 * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A")  = 7
 * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B")  = 5
 * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "AB") = 4
</pre>
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @return 目标子串最后一次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 -1
 * @since 2.5
 */
fun String?.lastIndexOfIgnoreCase(searchStr: String?): Int {
    if (this == null || searchStr == null) {
        return INDEX_NOT_FOUND
    }
    return this.lastIndexOfIgnoreCase(searchStr, this.length)
}

/**
 * 从指定位置反向、不区分大小写查找字符串中指定子串最后一次出现的位置。
 *
 * 输入 `null` 字符串会返回 `-1`。
 * 负数起始位置返回 `-1`。
 * 空搜索串总能匹配（起始位置非负时）。
 * 起始位置大于字符串长度则搜索整个字符串。
 *
 * <pre>
 * StringUtils.lastIndexOfIgnoreCase(null, *, *)          = -1
 * StringUtils.lastIndexOfIgnoreCase(*, null, *)          = -1
 * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A", 8)  = 7
 * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 8)  = 5
 * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "AB", 8) = 4
 * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 9)  = 5
 * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", -1) = -1
 * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A", 0)  = 0
 * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 0)  = -1
</pre>
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @param startPos  起始位置
 * @return 目标子串最后一次出现的索引，
 * 若无匹配或输入字符串为 `null` 则返回 -1
 * @since 2.5
 */
fun String?.lastIndexOfIgnoreCase(searchStr: String?, startPos: Int): Int {
    var startPos = startPos
    if (this == null || searchStr == null) {
        return INDEX_NOT_FOUND
    }
    if (startPos > (this.length - searchStr.length)) {
        startPos = this.length - searchStr.length
    }
    if (startPos < 0) {
        return INDEX_NOT_FOUND
    }
    if (searchStr.length == 0) {
        return startPos
    }

    for (i in startPos downTo 0) {
        if (this.regionMatches(i, searchStr, 0, searchStr.length, ignoreCase = true)) {
            return i
        }
    }
    return INDEX_NOT_FOUND
}

// 包含判断
//-----------------------------------------------------------------------
/**
 * 检查字符串是否包含指定字符，兼容 `null`。
 * 此方法使用 [String.indexOf]。
 *
 * 输入 `null` 或空字符串（""）会返回 `false`。
 *
 * <pre>
 * StringUtils.contains(null, *)    = false
 * StringUtils.contains("", *)      = false
 * StringUtils.contains("abc", 'a') = true
 * StringUtils.contains("abc", 'z') = false
</pre>
 *
 * @param str        要检查的字符串，可以为 null
 * @param searchChar 要查找的字符
 * @return 若包含目标字符返回 true，
 * 不包含或输入字符串为 `null` 则返回 false
 * @since 2.0
 */
fun String?.contains(searchChar: Char): Boolean {
    if (this.isEmpty()) {
        return false
    }
    return this.indexOf(searchChar) >= 0
}

/**
 * 检查字符串是否包含指定子串，兼容 `null`。
 * 此方法使用 [String.indexOf]。
 *
 * 输入 `null` 字符串会返回 `false`。
 *
 * <pre>
 * StringUtils.contains(null, *)     = false
 * StringUtils.contains(*, null)     = false
 * StringUtils.contains("", "")      = true
 * StringUtils.contains("abc", "")   = true
 * StringUtils.contains("abc", "a")  = true
 * StringUtils.contains("abc", "z")  = false
</pre>
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @return 若包含目标子串返回 true，
 * 不包含或输入字符串为 `null` 则返回 false
 * @since 2.0
 */
fun String?.contains(searchStr: String?): Boolean {
    if (this == null || searchStr == null) {
        return false
    }
    return this.indexOf(searchStr) >= 0
}

/**
 * 不区分大小写检查字符串是否包含指定子串，兼容 `null`。
 * 不区分大小写规则与 [String.equalsIgnoreCase] 一致。
 *
 * 输入 `null` 字符串会返回 `false`。
 *
 * <pre>
 * StringUtils.contains(null, *) = false
 * StringUtils.contains(*, null) = false
 * StringUtils.contains("", "") = true
 * StringUtils.contains("abc", "") = true
 * StringUtils.contains("abc", "a") = true
 * StringUtils.contains("abc", "z") = false
 * StringUtils.contains("abc", "A") = true
 * StringUtils.contains("abc", "Z") = false
</pre>
 *
 * @param str       要检查的字符串，可以为 null
 * @param searchStr 要查找的子串，可以为 null
 * @return 不区分大小写包含目标子串则返回 true，
 * 不包含或输入字符串为 `null` 则返回 false
 */
fun String?.containsIgnoreCase(searchStr: String?): Boolean {
    if (this == null || searchStr == null) {
        return false
    }
    val len = searchStr.length
    val max = this.length - len
    for (i in 0..max) {
        if (this.regionMatches(i, searchStr, 0, len, ignoreCase = true)) {
            return true
        }
    }
    return false
}

// 查找任意子串索引
//-----------------------------------------------------------------------
/**
 * 查找一组候选子串中任意一个第一次出现的索引。
 *
 * 输入 `null` 字符串会返回 `-1`。
 * 输入 `null` 或长度为 0 的搜索数组会返回 `-1`。
 * 搜索数组中的 `null` 元素会被忽略，但如果搜索数组包含 ""，
 * 且 `str` 不为 null，则返回 `0`。
 * 此方法使用 [String.indexOf]。
 *
 * <pre>
 * StringUtils.indexOfAny(null, *)                     = -1
 * StringUtils.indexOfAny(*, null)                     = -1
 * StringUtils.indexOfAny(*, [])                       = -1
 * StringUtils.indexOfAny("zzabyycdxx", ["ab","cd"])   = 2
 * StringUtils.indexOfAny("zzabyycdxx", ["cd","ab"])   = 2
 * StringUtils.indexOfAny("zzabyycdxx", ["mn","op"])   = -1
 * StringUtils.indexOfAny("zzabyycdxx", ["zab","aby"]) = 1
 * StringUtils.indexOfAny("zzabyycdxx", [""])          = 0
 * StringUtils.indexOfAny("", [""])                    = 0
 * StringUtils.indexOfAny("", ["a"])                   = -1
</pre>
 *
 * @param str        要检查的字符串，可以为 null
 * @param searchStrs 要搜索的字符串数组，可以为 null
 * @return 任意子串在字符串中第一次出现的索引，无匹配则返回 -1
 */
fun String?.indexOfAny(searchStrs: Array<String?>?): Int {
    if ((this == null) || (searchStrs == null)) {
        return INDEX_NOT_FOUND
    }
    val sz = searchStrs.size

    // 字符串不可能有 MAX_VALUE 索引。
    var ret = Int.Companion.MAX_VALUE

    var tmp = 0
    for (i in 0..<sz) {
        val search = searchStrs[i]
        if (search == null) {
            continue
        }
        tmp = this.indexOf(search)
        if (tmp == INDEX_NOT_FOUND) {
            continue
        }

        if (tmp < ret) {
            ret = tmp
        }
    }

    return if (ret == Int.Companion.MAX_VALUE) INDEX_NOT_FOUND else ret
}

/**
 * 查找一组候选子串中任意一个最后一次出现的索引。
 *
 * 输入 `null` 字符串会返回 `-1`。
 * 输入 `null` 搜索数组会返回 `-1`。
 * 搜索数组中的 `null` 或长度为 0 的元素会被忽略，
 * 但如果搜索数组包含 ""，且 `str` 不为 null，则返回字符串长度。
 * 此方法使用 [String.indexOf]
 *
 * <pre>
 * StringUtils.lastIndexOfAny(null, *)                   = -1
 * StringUtils.lastIndexOfAny(*, null)                   = -1
 * StringUtils.lastIndexOfAny(*, [])                     = -1
 * StringUtils.lastIndexOfAny(*, [null])                 = -1
 * StringUtils.lastIndexOfAny("zzabyycdxx", ["ab","cd"]) = 6
 * StringUtils.lastIndexOfAny("zzabyycdxx", ["cd","ab"]) = 6
 * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
 * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
 * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn",""])   = 10
</pre>
 *
 * @param str        要检查的字符串，可以为 null
 * @param searchStrs 要搜索的字符串数组，可以为 null
 * @return 任意子串最后一次出现的索引，无匹配则返回 -1
 */
fun String?.lastIndexOfAny(searchStrs: Array<String?>?): Int {
    if ((this == null) || (searchStrs == null)) {
        return INDEX_NOT_FOUND
    }
    val sz = searchStrs.size
    var ret = INDEX_NOT_FOUND
    var tmp = 0
    for (i in 0..<sz) {
        val search = searchStrs[i]
        if (search == null) {
            continue
        }
        tmp = this.lastIndexOf(search)
        if (tmp > ret) {
            ret = tmp
        }
    }
    return ret
}

// 截取子串
//-----------------------------------------------------------------------
/**
 * 从指定字符串安全获取子串，避免异常。
 *
 * 负数起始位置表示从字符串末尾倒数 `n` 个字符开始。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入空字符串（""）会返回 ""。
 *
 * <pre>
 * StringUtils.substring(null, *)   = null
 * StringUtils.substring("", *)     = ""
 * StringUtils.substring("abc", 0)  = "abc"
 * StringUtils.substring("abc", 2)  = "c"
 * StringUtils.substring("abc", 4)  = ""
 * StringUtils.substring("abc", -2) = "bc"
 * StringUtils.substring("abc", -4) = "abc"
</pre>
 *
 * @param str   要截取子串的字符串，可以为 null
 * @param start 起始位置，负数表示从字符串末尾倒数
 * @return 从起始位置开始的子串，输入为 null 则返回 `null`
 */
fun String?.substring(start: Int): String? {
    var start = start
    if (this == null) {
        return null
    }

    // 处理负数，表示倒数 n 个字符
    if (start < 0) {
        start = this.length + start // 记住 start 是负数
    }

    if (start < 0) {
        start = 0
    }
    if (start > this.length) {
        return EMPTY
    }

    return (this as CharSequence).substring(start)
}

/**
 * 从指定字符串安全获取子串，避免异常。
 *
 * 负数起始/结束位置表示从字符串末尾倒数 `n` 个字符。
 *
 * 返回的子串从 `start` 位置开始，到 `end` 位置前结束。
 * 所有位置从 0 开始计数——即从字符串开头开始请使用 `start = 0`。
 * 负数的 start 和 end 可用于指定相对于字符串末尾的偏移量。
 *
 * 如果 `start` 不在 `end` 左侧，则返回 ""。
 *
 * <pre>
 * StringUtils.substring(null, *, *)    = null
 * StringUtils.substring("", * ,  *)    = "";
 * StringUtils.substring("abc", 0, 2)   = "ab"
 * StringUtils.substring("abc", 2, 0)   = ""
 * StringUtils.substring("abc", 2, 4)   = "c"
 * StringUtils.substring("abc", 4, 6)   = ""
 * StringUtils.substring("abc", 2, 2)   = ""
 * StringUtils.substring("abc", -2, -1) = "b"
 * StringUtils.substring("abc", -4, 2)  = "ab"
</pre>
 *
 * @param str   要截取子串的字符串，可以为 null
 * @param start 起始位置，负数表示从字符串末尾倒数
 * @param end   结束位置（不包含），负数表示从字符串末尾倒数
 * @return 从起始位置到结束位置的子串，输入为 null 则返回 `null`
 */
fun String?.substring(start: Int, end: Int): String? {
    var start = start
    var end = end
    if (this == null) {
        return null
    }

    // 处理负数
    if (end < 0) {
        end += this.length // 记住 end 是负数
    }
    if (start < 0) {
        start += this.length // 记住 start 是负数
    }

    // 检查长度
    if (end > this.length) {
        end = this.length
    }

    // 如果 start 大于 end，返回 ""
    if (start > end) {
        return EMPTY
    }

    if (start < 0) {
        start = 0
    }
    if (end < 0) {
        end = 0
    }

    return (this as CharSequence).substring(start, end)
}

// 左/右/中间截取
//-----------------------------------------------------------------------
/**
 * 获取字符串最左边 `len` 个字符。
 *
 * 如果无法获取 `len` 个字符，或字符串为 `null`，
 * 会直接返回字符串，不会抛出异常。
 * 如果 len 为负数，则返回空字符串。
 *
 * <pre>
 * StringUtils.left(null, *)    = null
 * StringUtils.left(*, -ve)     = ""
 * StringUtils.left("", *)      = ""
 * StringUtils.left("abc", 0)   = ""
 * StringUtils.left("abc", 2)   = "ab"
 * StringUtils.left("abc", 4)   = "abc"
</pre>
 *
 * @param str 要获取左侧字符的字符串，可以为 null
 * @param len 需要的字符串长度
 * @return 最左侧的字符，输入为 null 则返回 `null`
 */
fun String?.left(len: Int): String? {
    if (this == null) {
        return null
    }
    if (len < 0) {
        return EMPTY
    }
    if (this.length <= len) {
        return this
    }
    return this.substring(0, len)
}

/**
 * 获取字符串最右边 `len` 个字符。
 *
 * 如果无法获取 `len` 个字符，或字符串为 `null`，
 * 会直接返回字符串，不会抛出异常。
 * 如果 len 为负数，则返回空字符串。
 *
 * <pre>
 * StringUtils.right(null, *)    = null
 * StringUtils.right(*, -ve)     = ""
 * StringUtils.right("", *)      = ""
 * StringUtils.right("abc", 0)   = ""
 * StringUtils.right("abc", 2)   = "bc"
 * StringUtils.right("abc", 4)   = "abc"
</pre>
 *
 * @param str 要获取右侧字符的字符串，可以为 null
 * @param len 需要的字符串长度
 * @return 最右侧的字符，输入为 null 则返回 `null`
 */
fun String?.right(len: Int): String? {
    if (this == null) {
        return null
    }
    if (len < 0) {
        return EMPTY
    }
    if (this.length <= len) {
        return this
    }
    return this.substring(this.length - len)
}

/**
 * 从字符串中间获取 `len` 个字符。
 *
 * 如果无法获取 `len` 个字符，会返回剩余部分，不会抛出异常。
 * 如果字符串为 `null`，则返回 `null`。
 * 如果 len 为负数或超过字符串长度，则返回空字符串。
 *
 * <pre>
 * StringUtils.mid(null, *, *)    = null
 * StringUtils.mid(*, *, -ve)     = ""
 * StringUtils.mid("", 0, *)      = ""
 * StringUtils.mid("abc", 0, 2)   = "ab"
 * StringUtils.mid("abc", 0, 4)   = "abc"
 * StringUtils.mid("abc", 2, 4)   = "c"
 * StringUtils.mid("abc", 4, 2)   = ""
 * StringUtils.mid("abc", -2, 2)  = "ab"
</pre>
 *
 * @param str 要获取字符的字符串，可以为 null
 * @param pos 起始位置，负数视为 0
 * @param len 需要的字符串长度
 * @return 中间的字符，输入为 null 则返回 `null`
 */
fun String?.mid(pos: Int, len: Int): String? {
    var pos = pos
    if (this == null) {
        return null
    }
    if (len < 0 || pos > this.length) {
        return EMPTY
    }
    if (pos < 0) {
        pos = 0
    }
    if (this.length <= (pos + len)) {
        return this.substring(pos)
    }
    return this.substring(pos, pos + len)
}

// 分隔符前/后截取
//-----------------------------------------------------------------------
/**
 * 获取分隔符第一次出现之前的子串。
 * 分隔符不会被返回。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 * 输入 `null` 分隔符会返回原字符串。
 *
 * 如果未找到分隔符，返回原字符串。
 *
 * <pre>
 * StringUtils.substringBefore(null, *)      = null
 * StringUtils.substringBefore("", *)        = ""
 * StringUtils.substringBefore("abc", "a")   = ""
 * StringUtils.substringBefore("abcba", "b") = "a"
 * StringUtils.substringBefore("abc", "c")   = "ab"
 * StringUtils.substringBefore("abc", "d")   = "abc"
 * StringUtils.substringBefore("abc", "")    = ""
 * StringUtils.substringBefore("abc", null)  = "abc"
</pre>
 *
 * @param str       要截取子串的字符串，可以为 null
 * @param separator 要搜索的分隔符，可以为 null
 * @return 分隔符第一次出现前的子串，输入为 null 则返回 `null`
 * @since 2.0
 */
fun String?.substringBefore(separator: String?): String? {
    if (this.isEmpty() || separator == null) {
        return this
    }
    if (separator.isEmpty()) {
        return EMPTY
    }
    val pos = this.indexOf(separator)
    if (pos == INDEX_NOT_FOUND) {
        return this
    }
    return this.substring(0, pos)
}

/**
 * 获取分隔符第一次出现之后的子串。
 * 分隔符不会被返回。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 * 输入 `null` 分隔符会返回空字符串（如果原字符串不为 null）。
 *
 * 如果未找到分隔符，返回空字符串。
 *
 * <pre>
 * StringUtils.substringAfter(null, *)      = null
 * StringUtils.substringAfter("", *)        = ""
 * StringUtils.substringAfter(*, null)      = ""
 * StringUtils.substringAfter("abc", "a")   = "bc"
 * StringUtils.substringAfter("abcba", "b") = "cba"
 * StringUtils.substringAfter("abc", "c")   = ""
 * StringUtils.substringAfter("abc", "d")   = ""
 * StringUtils.substringAfter("abc", "")    = "abc"
</pre>
 *
 * @param str       要截取子串的字符串，可以为 null
 * @param separator 要搜索的分隔符，可以为 null
 * @return 分隔符第一次出现后的子串，输入为 null 则返回 `null`
 * @since 2.0
 */
fun String?.substringAfter(separator: String?): String? {
    if (this.isEmpty()) {
        return this
    }
    if (separator == null) {
        return EMPTY
    }
    val pos = this.indexOf(separator)
    if (pos == INDEX_NOT_FOUND) {
        return EMPTY
    }
    return this.substring(pos + separator.length)
}

/**
 * 获取分隔符最后一次出现之前的子串。
 * 分隔符不会被返回。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 * 输入空或 `null` 分隔符会返回原字符串。
 *
 * 如果未找到分隔符，返回原字符串。
 *
 * <pre>
 * StringUtils.substringBeforeLast(null, *)      = null
 * StringUtils.substringBeforeLast("", *)        = ""
 * StringUtils.substringBeforeLast("abcba", "b") = "abc"
 * StringUtils.substringBeforeLast("abc", "c")   = "ab"
 * StringUtils.substringBeforeLast("a", "a")     = ""
 * StringUtils.substringBeforeLast("a", "z")     = "a"
 * StringUtils.substringBeforeLast("a", null)    = "a"
 * StringUtils.substringBeforeLast("a", "")      = "a"
</pre>
 *
 * @param str       要截取子串的字符串，可以为 null
 * @param separator 要搜索的分隔符，可以为 null
 * @return 分隔符最后一次出现前的子串，输入为 null 则返回 `null`
 * @since 2.0
 */
fun String?.substringBeforeLast(separator: String?): String? {
    if (this.isEmpty() || separator.isEmpty()) {
        return this
    }
    val pos = this.lastIndexOf(separator!!)
    if (pos == INDEX_NOT_FOUND) {
        return this
    }
    return this.substring(0, pos)
}

/**
 * 获取分隔符最后一次出现之后的子串。
 * 分隔符不会被返回。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 * 输入空或 `null` 分隔符会返回空字符串（如果原字符串不为 null）。
 *
 * 如果未找到分隔符，返回空字符串。
 *
 * <pre>
 * StringUtils.substringAfterLast(null, *)      = null
 * StringUtils.substringAfterLast("", *)        = ""
 * StringUtils.substringAfterLast(*, "")        = ""
 * StringUtils.substringAfterLast(*, null)      = ""
 * StringUtils.substringAfterLast("abc", "a")   = "bc"
 * StringUtils.substringAfterLast("abcba", "b") = "a"
 * StringUtils.substringAfterLast("abc", "c")   = ""
 * StringUtils.substringAfterLast("a", "a")     = ""
 * StringUtils.substringAfterLast("a", "z")     = ""
</pre>
 *
 * @param str       要截取子串的字符串，可以为 null
 * @param separator 要搜索的分隔符，可以为 null
 * @return 分隔符最后一次出现后的子串，输入为 null 则返回 `null`
 * @since 2.0
 */
fun String?.substringAfterLast(separator: String?): String? {
    if (this.isEmpty()) {
        return this
    }
    if (separator.isEmpty()) {
        return EMPTY
    }
    val pos = this.lastIndexOf(separator)
    if (pos == INDEX_NOT_FOUND || pos == (this!!.length - separator!!.length)) {
        return EMPTY
    }
    return this.substring(pos + separator.length)
}

// 截取中间子串
//-----------------------------------------------------------------------
/**
 * 获取被两个相同字符串包裹的中间子串。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 标记会返回 `null`。
 *
 * <pre>
 * StringUtils.substringBetween(null, *)            = null
 * StringUtils.substringBetween("", "")             = ""
 * StringUtils.substringBetween("", "tag")          = null
 * StringUtils.substringBetween("tagabctag", null)  = null
 * StringUtils.substringBetween("tagabctag", "")    = ""
 * StringUtils.substringBetween("tagabctag", "tag") = "abc"
</pre>
 *
 * @param str 包含目标子串的字符串，可以为 null
 * @param tag 子串前后的标记字符串，可以为 null
 * @return 中间子串，无匹配则返回 `null`
 * @since 2.0
 */
fun String?.substringBetween(tag: String?): String? {
    return this.substringBetween(tag, tag)
}

/**
 * 获取被两个不同字符串包裹的中间子串。
 * 只返回第一次匹配的结果。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 开始/结束标记会返回 `null`（无匹配）。
 * 输入空字符串（""）作为开始和结束标记会返回空字符串。
 *
 * <pre>
 * StringUtils.substringBetween("wx[b]yz", "[", "]") = "b"
 * StringUtils.substringBetween(null, *, *)          = null
 * StringUtils.substringBetween(*, null, *)          = null
 * StringUtils.substringBetween(*, *, null)          = null
 * StringUtils.substringBetween("", "", "")          = ""
 * StringUtils.substringBetween("", "", "]")         = null
 * StringUtils.substringBetween("", "[", "]")        = null
 * StringUtils.substringBetween("yabcz", "", "")     = ""
 * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
 * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
</pre>
 *
 * @param str   包含目标子串的字符串，可以为 null
 * @param open  子串前面的开始标记，可以为 null
 * @param close 子串后面的结束标记，可以为 null
 * @return 中间子串，无匹配则返回 `null`
 * @since 2.0
 */
fun String?.substringBetween(open: String?, close: String?): String? {
    if (this == null || open == null || close == null) {
        return null
    }
    val start = this.indexOf(open)
    if (start != INDEX_NOT_FOUND) {
        val end = this.indexOf(close, start + open.length)
        if (end != INDEX_NOT_FOUND) {
            return this.substring(start + open.length, end)
        }
    }
    return null
}

/**
 * 在字符串中搜索由开始和结束标记分隔的子串，
 * 将所有匹配的子串以数组形式返回。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 开始/结束标记会返回 `null`（无匹配）。
 * 输入空字符串（""）作为开始/结束标记会返回 `null`（无匹配）。
 *
 * <pre>
 * StringUtils.substringsBetween("[a][b][c]", "[", "]") = ["a","b","c"]
 * StringUtils.substringsBetween(null, *, *)            = null
 * StringUtils.substringsBetween(*, null, *)            = null
 * StringUtils.substringsBetween(*, *, null)            = null
 * StringUtils.substringsBetween("", "[", "]")          = []
</pre>
 *
 * @param str   包含子串的字符串，null 返回 null，空返回空
 * @param open  标识子串开始的字符串，空返回 null
 * @param close 标识子串结束的字符串，空返回 null
 * @return 子串数组，无匹配则返回 `null`
 * @since 2.3
 */
fun String?.substringsBetween(open: String?, close: String?): Array<String?>? {
    if (this == null || open.isEmpty() || close.isEmpty()) {
        return null
    }
    val strLen = this.length
    if (strLen == 0) {
        return arrayOf<String?>()
    }
    val closeLen = close!!.length
    val openLen = open!!.length
    val list = ArrayList<String?>()
    var pos = 0
    while (pos < (strLen - closeLen)) {
        var start = this.indexOf(open, pos)
        if (start < 0) {
            break
        }
        start += openLen
        val end = this.indexOf(close, start)
        if (end < 0) {
            break
        }
        list.add(this.substring(start, end))
        pos = end + closeLen
    }
    if (list.isEmpty()) {
        return null
    }
    return list.toTypedArray()
}

// 嵌套提取
//-----------------------------------------------------------------------
/**
 * 获取被两个相同字符串嵌套包裹的字符串。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 标记会返回 `null`。
 *
 * <pre>
 * StringUtils.getNestedString(null, *)            = null
 * StringUtils.getNestedString("", "")             = ""
 * StringUtils.getNestedString("", "tag")          = null
 * StringUtils.getNestedString("tagabctag", null)  = null
 * StringUtils.getNestedString("tagabctag", "")    = ""
 * StringUtils.getNestedString("tagabctag", "tag") = "abc"
</pre>
 *
 * @param str 包含嵌套字符串的字符串，可以为 null
 * @param tag 嵌套字符串前后的标记字符串，可以为 null
 * @return 嵌套的字符串，无匹配则返回 `null`
 * //@deprecated 使用命名更清晰的 [.substringBetween]。
 * 此方法将在 Commons Lang 3.0 中移除。
 */
fun String?.getNestedString(tag: String?): String? {
    return this.substringBetween(tag, tag)
}

/**
 * 获取被两个字符串嵌套包裹的字符串。
 * 只返回第一次匹配的结果。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 开始/结束标记会返回 `null`（无匹配）。
 * 输入空字符串（""）作为开始/结束标记会返回空字符串。
 *
 * <pre>
 * StringUtils.getNestedString(null, *, *)          = null
 * StringUtils.getNestedString("", "", "")          = ""
 * StringUtils.getNestedString("", "", "tag")       = null
 * StringUtils.getNestedString("", "tag", "tag")    = null
 * StringUtils.getNestedString("yabcz", null, null) = null
 * StringUtils.getNestedString("yabcz", "", "")     = ""
 * StringUtils.getNestedString("yabcz", "y", "z")   = "abc"
 * StringUtils.getNestedString("yabczyabcz", "y", "z")   = "abc"
</pre>
 *
 * @param str   包含嵌套字符串的字符串，可以为 null
 * @param open  嵌套字符串前面的标记，可以为 null
 * @param close 嵌套字符串后面的标记，可以为 null
 * @return 嵌套的字符串，无匹配则返回 `null`
 * //@deprecated 使用命名更清晰的 [.substringBetween]。
 * 此方法将在 Commons Lang 3.0 中移除。
 */
fun String?.getNestedString(open: String?, close: String?): String? {
    return this.substringBetween(open, close)
}

/**
 * 使用指定分隔符将文本拆分为数组。
 * 这是 StringTokenizer 的替代方案。
 *
 * 分隔符不会包含在返回的字符串数组中。
 * 相邻的分隔符会被视为一个分隔符。
 * 如需更精细的拆分控制，请使用 StrTokenizer 类。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 分隔符会按空白字符拆分。
 *
 * <pre>
 * StringUtils.split(null, *)         = null
 * StringUtils.split("", *)           = []
 * StringUtils.split("abc def", null) = ["abc", "def"]
 * StringUtils.split("abc def", " ")  = ["abc", "def"]
 * StringUtils.split("abc  def", " ") = ["abc", "def"]
 * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
</pre>
 *
 * @param str            要解析的字符串，可以为 null
 * @param separatorChars 用作分隔符的字符，`null` 表示按空白字符拆分
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 */
fun String?.split(separatorChars: String?): Array<String?>? {
    return splitWorker(this, separatorChars, -1, false)
}

// 拆分
//-----------------------------------------------------------------------
/**
 * 使用空白字符作为分隔符将文本拆分为数组。
 * 空白字符由 [Character.isWhitespace] 定义。
 *
 * 分隔符不会包含在返回的字符串数组中。
 * 相邻的分隔符会被视为一个分隔符。
 * 如需更精细的拆分控制，请使用 StrTokenizer 类。
 *
 * 输入 `null` 字符串会返回 `null`。
 *
 * <pre>
 * StringUtils.split(null)       = null
 * StringUtils.split("")         = []
 * StringUtils.split("abc def")  = ["abc", "def"]
 * StringUtils.split("abc  def") = ["abc", "def"]
 * StringUtils.split(" abc ")    = ["abc"]
</pre>
 *
 * @param str 要解析的字符串，可以为 null
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 */
fun String?.split(separatorChars: String, max: Int): Array<String?>? {
    return splitWorker(this, separatorChars, max, false)
}

/**
 * 拆分字符串到列表
 *
 * @param text      原字符串
 * @param splitText 拆分字符
 */
fun String?.splitToList(splitText: String?): List<String> {
    if (this.isEmpty()) {
        return emptyList()
    }

    val result = ArrayList<String>()
    if (TextUtils.isEmpty(splitText)) {
        result.add(this!!)
    } else {
        result.addAll(this!!.split(splitText!!.toRegex()))
    }

    return result
}

/**
 * 使用指定分隔符字符串将文本拆分为数组。
 *
 * 分隔符不会包含在返回的字符串数组中。
 * 相邻的分隔符会被视为一个分隔符。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 分隔符会按空白字符拆分。
 *
 * <pre>
 * StringUtils.splitByWholeSeparator(null, *)               = null
 * StringUtils.splitByWholeSeparator("", *)                 = []
 * StringUtils.splitByWholeSeparator("ab de fg", null)      = ["ab", "de", "fg"]
 * StringUtils.splitByWholeSeparator("ab   de fg", null)    = ["ab", "de", "fg"]
 * StringUtils.splitByWholeSeparator("ab:cd:ef", ":")       = ["ab", "cd", "ef"]
 * StringUtils.splitByWholeSeparator("ab-!-cd-!-ef", "-!-") = ["ab", "cd", "ef"]
</pre>
 *
 * @param str       要解析的字符串，可以为 null
 * @param separator 用作分隔符的字符串，
 * `null` 表示按空白字符拆分
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 */
fun String?.splitByWholeSeparator(separator: String?): Array<String?>? {
    return splitByWholeSeparatorWorker(this, separator, -1, false)
}

/**
 * 使用指定分隔符字符串将文本拆分为数组，
 * 最多返回 `max` 个子串。
 *
 * 分隔符不会包含在返回的字符串数组中。
 * 相邻的分隔符会被视为一个分隔符。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 分隔符会按空白字符拆分。
 *
 * <pre>
 * StringUtils.splitByWholeSeparator(null, *, *)               = null
 * StringUtils.splitByWholeSeparator("", *, *)                 = []
 * StringUtils.splitByWholeSeparator("ab de fg", null, 0)      = ["ab", "de", "fg"]
 * StringUtils.splitByWholeSeparator("ab   de fg", null, 0)    = ["ab", "de", "fg"]
 * StringUtils.splitByWholeSeparator("ab:cd:ef", ":", 2)       = ["ab", "cd:ef"]
 * StringUtils.splitByWholeSeparator("ab-!-cd-!-ef", "-!-", 5) = ["ab", "cd", "ef"]
 * StringUtils.splitByWholeSeparator("ab-!-cd-!-ef", "-!-", 2) = ["ab", "cd-!-ef"]
</pre>
 *
 * @param str       要解析的字符串，可以为 null
 * @param separator 用作分隔符的字符串，`null` 表示按空白字符拆分
 * @param max       返回数组中包含的最大元素数量。0 或负数表示无限制。
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 */
fun String?.splitByWholeSeparator(separator: String?, max: Int): Array<String?>? {
    return splitByWholeSeparatorWorker(this, separator, max, false)
}

/**
 * 使用指定分隔符字符串将文本拆分为数组。
 *
 * 分隔符不会包含在返回的字符串数组中。
 * 相邻的分隔符会被视为空标记的分隔符。
 * 如需更精细的拆分控制，请使用 StrTokenizer 类。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 分隔符会按空白字符拆分。
 *
 * <pre>
 * StringUtils.splitByWholeSeparatorPreserveAllTokens(null, *)               = null
 * StringUtils.splitByWholeSeparatorPreserveAllTokens("", *)                 = []
 * StringUtils.splitByWholeSeparatorPreserveAllTokens("ab de fg", null)      = ["ab", "de", "fg"]
 * StringUtils.splitByWholeSeparatorPreserveAllTokens("ab   de fg", null)    = ["ab", "", "", "de", "fg"]
 * StringUtils.splitByWholeSeparatorPreserveAllTokens("ab:cd:ef", ":")       = ["ab", "cd", "ef"]
 * StringUtils.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-") = ["ab", "cd", "ef"]
</pre>
 *
 * @param str       要解析的字符串，可以为 null
 * @param separator 用作分隔符的字符串，`null` 表示按空白字符拆分
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 * @since 2.4
 */
fun String?.splitByWholeSeparatorPreserveAllTokens(separator: String?): Array<String?>? {
    return splitByWholeSeparatorWorker(this, separator, -1, true)
}

/**
 * 使用指定分隔符字符串将文本拆分为数组，
 * 最多返回 `max` 个子串。
 *
 * 分隔符不会包含在返回的字符串数组中。
 * 相邻的分隔符会被视为空标记的分隔符。
 * 如需更精细的拆分控制，请使用 StrTokenizer 类。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 分隔符会按空白字符拆分。
 *
 * <pre>
 * StringUtils.splitByWholeSeparatorPreserveAllTokens(null, *, *)               = null
 * StringUtils.splitByWholeSeparatorPreserveAllTokens("", *, *)                 = []
 * StringUtils.splitByWholeSeparatorPreserveAllTokens("ab de fg", null, 0)      = ["ab", "de", "fg"]
 * StringUtils.splitByWholeSeparatorPreserveAllTokens("ab   de fg", null, 0)    = ["ab", "", "", "de", "fg"]
 * StringUtils.splitByWholeSeparatorPreserveAllTokens("ab:cd:ef", ":", 2)       = ["ab", "cd:ef"]
 * StringUtils.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-", 5) = ["ab", "cd", "ef"]
 * StringUtils.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-", 2) = ["ab", "cd-!-ef"]
</pre>
 *
 * @param str       要解析的字符串，可以为 null
 * @param separator 用作分隔符的字符串，`null` 表示按空白字符拆分
 * @param max       返回数组中包含的最大元素数量。0 或负数表示无限制。
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 * @since 2.4
 */
fun String?.splitByWholeSeparatorPreserveAllTokens(
    separator: String?,
    max: Int
): Array<String?>? {
    return splitByWholeSeparatorWorker(this, separator, max, true)
}

/**
 * 为 `splitByWholeSeparatorPreserveAllTokens` 系列方法执行核心逻辑。
 *
 * @param str               要解析的字符串，可以为 `null`
 * @param separator         用作分隔符的字符串，`null` 表示按空白字符拆分
 * @param max               返回数组中包含的最大元素数量。0 或负数表示无限制。
 * @param preserveAllTokens 如果为 `true`，相邻分隔符视为空标记；如果为 `false`，相邻分隔符视为一个分隔符。
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 * @since 2.4
 */
private fun splitByWholeSeparatorWorker(
    str: String?, separator: String?, max: Int,
    preserveAllTokens: Boolean
): Array<String?>? {
    if (str == null) {
        return null
    }

    val len = str.length

    if (len == 0) {
        return arrayOf<String?>()
    }

    if ((separator == null) || (EMPTY == separator)) {
        // 按空白字符拆分
        return splitWorker(str, null, max, preserveAllTokens)
    }

    val separatorLength = separator.length

    val substrings = ArrayList<String?>()
    var numberOfSubstrings = 0
    var beg = 0
    var end = 0
    while (end < len) {
        end = str.indexOf(separator, beg)

        if (end > -1) {
            if (end > beg) {
                numberOfSubstrings += 1

                if (numberOfSubstrings == max) {
                    end = len
                    substrings.add(str.substring(beg))
                } else {
                    // 以下代码有效，因为 String.substring( beg, end ) 不包含 'end' 位置的字符
                    substrings.add(str.substring(beg, end))

                    // 设置下一次搜索的起始点
                    beg = end + separatorLength
                }
            } else {
                // 发现连续的分隔符，跳过
                if (preserveAllTokens) {
                    numberOfSubstrings += 1
                    if (numberOfSubstrings == max) {
                        end = len
                        substrings.add(str.substring(beg))
                    } else {
                        substrings.add(EMPTY)
                    }
                }
                beg = end + separatorLength
            }
        } else {
            // String.substring( beg ) 从 'beg' 截取到字符串末尾
            substrings.add(str.substring(beg))
            end = len
        }
    }

    return substrings.toTypedArray()
}

// -----------------------------------------------------------------------
/**
 * 使用空白字符作为分隔符将文本拆分为数组，保留所有标记，
 * 包括由相邻分隔符产生的空标记。这是 StringTokenizer 的替代方案。
 * 空白字符由 [Character.isWhitespace] 定义。
 *
 * 分隔符不会包含在返回的字符串数组中。
 * 相邻的分隔符会被视为空标记的分隔符。
 * 如需更精细的拆分控制，请使用 StrTokenizer 类。
 *
 * 输入 `null` 字符串会返回 `null`。
 *
 * <pre>
 * StringUtils.splitPreserveAllTokens(null)       = null
 * StringUtils.splitPreserveAllTokens("")         = []
 * StringUtils.splitPreserveAllTokens("abc def")  = ["abc", "def"]
 * StringUtils.splitPreserveAllTokens("abc  def") = ["abc", "", "def"]
 * StringUtils.splitPreserveAllTokens(" abc ")    = ["", "abc", ""]
</pre>
 *
 * @param str 要解析的字符串，可以为 `null`
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 * @since 2.1
 */
fun String?.splitPreserveAllTokens(): Array<String?>? {
    return splitWorker(this, null, -1, true)
}

/**
 * 使用指定字符作为分隔符将文本拆分为数组，保留所有标记，
 * 包括由相邻分隔符产生的空标记。这是 StringTokenizer 的替代方案。
 *
 * 分隔符不会包含在返回的字符串数组中。
 * 相邻的分隔符会被视为空标记的分隔符。
 * 如需更精细的拆分控制，请使用 StrTokenizer 类。
 *
 * 输入 `null` 字符串会返回 `null`。
 *
 * <pre>
 * StringUtils.splitPreserveAllTokens(null, *)         = null
 * StringUtils.splitPreserveAllTokens("", *)           = []
 * StringUtils.splitPreserveAllTokens("a.b.c", '.')    = ["a", "b", "c"]
 * StringUtils.splitPreserveAllTokens("a..b.c", '.')   = ["a", "", "b", "c"]
 * StringUtils.splitPreserveAllTokens("a:b:c", '.')    = ["a:b:c"]
 * StringUtils.splitPreserveAllTokens("a\tb\nc", null) = ["a", "b", "c"]
 * StringUtils.splitPreserveAllTokens("a b c", ' ')    = ["a", "b", "c"]
 * StringUtils.splitPreserveAllTokens("a b c ", ' ')   = ["a", "b", "c", ""]
 * StringUtils.splitPreserveAllTokens("a b c  ", ' ')   = ["a", "b", "c", "", ""]
 * StringUtils.splitPreserveAllTokens(" a b c", ' ')   = ["", "a", "b", "c"]
 * StringUtils.splitPreserveAllTokens("  a b c", ' ')  = ["", "", "a", "b", "c"]
 * StringUtils.splitPreserveAllTokens(" a b c ", ' ')  = ["", "a", "b", "c", ""]
</pre>
 *
 * @param str           要解析的字符串，可以为 `null`
 * @param separatorChar 用作分隔符的字符，
 * `null` 表示按空白字符拆分
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 * @since 2.1
 */
fun String?.splitPreserveAllTokens(separatorChar: Char): Array<String?>? {
    return splitWorker(this, separatorChar, true)
}

/**
 * 为 `split` 和 `splitPreserveAllTokens` 方法执行核心逻辑，
 * 这些方法不限制返回数组的最大长度。
 *
 * @param str               要解析的字符串，可以为 `null`
 * @param separatorChar     分隔字符
 * @param preserveAllTokens 如果为 `true`，相邻分隔符视为空标记；
 * 如果为 `false`，相邻分隔符视为一个分隔符。
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 */
private fun splitWorker(
    str: String?,
    separatorChar: Char,
    preserveAllTokens: Boolean
): Array<String?>? {
    // 针对 2.0 (JDK1.4) 优化了性能

    if (str == null) {
        return null
    }
    val len = str.length
    if (len == 0) {
        return arrayOf<String?>()
    }
    val list = ArrayList<String?>()
    var i = 0
    var start = 0
    var match = false
    var lastMatch = false
    while (i < len) {
        if (str.get(i) == separatorChar) {
            if (match || preserveAllTokens) {
                list.add(str.substring(start, i))
                match = false
                lastMatch = true
            }
            start = ++i
            continue
        }
        lastMatch = false
        match = true
        i++
    }
    if (match || (preserveAllTokens && lastMatch)) {
        list.add(str.substring(start, i))
    }
    return list.toTypedArray()
}

/**
 * 使用指定分隔符将文本拆分为数组，保留所有标记，
 * 包括由相邻分隔符产生的空标记。这是 StringTokenizer 的替代方案。
 *
 * 分隔符不会包含在返回的字符串数组中。
 * 相邻的分隔符会被视为空标记的分隔符。
 * 如需更精细的拆分控制，请使用 StrTokenizer 类。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 分隔符会按空白字符拆分。
 *
 * <pre>
 * StringUtils.splitPreserveAllTokens(null, *)           = null
 * StringUtils.splitPreserveAllTokens("", *)             = []
 * StringUtils.splitPreserveAllTokens("abc def", null)   = ["abc", "def"]
 * StringUtils.splitPreserveAllTokens("abc def", " ")    = ["abc", "def"]
 * StringUtils.splitPreserveAllTokens("abc  def", " ")   = ["abc", "", "def"]
 * StringUtils.splitPreserveAllTokens("ab:cd:ef", ":")   = ["ab", "cd", "ef"]
 * StringUtils.splitPreserveAllTokens("ab:cd:ef:", ":")  = ["ab", "cd", "ef", ""]
 * StringUtils.splitPreserveAllTokens("ab:cd:ef::", ":") = ["ab", "cd", "ef", "", ""]
 * StringUtils.splitPreserveAllTokens("ab::cd:ef", ":")  = ["ab", "", "cd", "ef"]
 * StringUtils.splitPreserveAllTokens(":cd:ef", ":")     = ["", "cd", "ef"]
 * StringUtils.splitPreserveAllTokens("::cd:ef", ":")    = ["", "", "cd", "ef"]
 * StringUtils.splitPreserveAllTokens(":cd:ef:", ":")    = ["", "cd", "ef", ""]
</pre>
 *
 * @param str            要解析的字符串，可以为 `null`
 * @param separatorChars 用作分隔符的字符，
 * `null` 表示按空白字符拆分
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 * @since 2.1
 */
fun String?.splitPreserveAllTokens(separatorChars: String?): Array<String?>? {
    return splitWorker(this, separatorChars, -1, true)
}

/**
 * 使用指定分隔符将文本拆分为指定最大长度的数组，
 * 保留所有标记，包括由相邻分隔符产生的空标记。
 *
 * 分隔符不会包含在返回的字符串数组中。
 * 相邻的分隔符会被视为空标记的分隔符。
 *
 * 输入 `null` 字符串会返回 `null`。
 * 输入 `null` 分隔符会按空白字符拆分。
 *
 * 如果找到超过 `max` 个分隔子串，最后一个返回的字符串
 * 将包含前 `max - 1` 个返回字符串之后的所有字符（包括分隔符）。
 *
 * <pre>
 * StringUtils.splitPreserveAllTokens(null, *, *)            = null
 * StringUtils.splitPreserveAllTokens("", *, *)              = []
 * StringUtils.splitPreserveAllTokens("ab de fg", null, 0)   = ["ab", "cd", "ef"]
 * StringUtils.splitPreserveAllTokens("ab   de fg", null, 0) = ["ab", "cd", "ef"]
 * StringUtils.splitPreserveAllTokens("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
 * StringUtils.splitPreserveAllTokens("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
 * StringUtils.splitPreserveAllTokens("ab   de fg", null, 2) = ["ab", "  de fg"]
 * StringUtils.splitPreserveAllTokens("ab   de fg", null, 3) = ["ab", "", " de fg"]
 * StringUtils.splitPreserveAllTokens("ab   de fg", null, 4) = ["ab", "", "", "de fg"]
</pre>
 *
 * @param str            要解析的字符串，可以为 `null`
 * @param separatorChars 用作分隔符的字符，
 * `null` 表示按空白字符拆分
 * @param max            返回数组中包含的最大元素数量。
 * 0 或负数表示无限制
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 * @since 2.1
 */
fun String?.splitPreserveAllTokens(separatorChars: String?, max: Int): Array<String?>? {
    return splitWorker(this, separatorChars, max, true)
}

/**
 * 为 `split` 和 `splitPreserveAllTokens` 方法执行核心逻辑，
 * 这些方法限制返回数组的最大长度。
 *
 * @param str               要解析的字符串，可以为 `null`
 * @param separatorChars    分隔字符
 * @param max               返回数组中包含的最大元素数量。
 * 0 或负数表示无限制。
 * @param preserveAllTokens 如果为 `true`，相邻分隔符视为空标记；
 * 如果为 `false`，相邻分隔符视为一个分隔符。
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 */
private fun splitWorker(
    str: String?,
    separatorChars: String?,
    max: Int,
    preserveAllTokens: Boolean
): Array<String?>? {
    // 针对 2.0 (JDK1.4) 优化了性能
    // 直接编写代码比 StringTokenizer 更快
    // 另外，StringTokenizer 使用 isSpace() 而非 isWhitespace()

    if (str == null) {
        return null
    }
    val len = str.length
    if (len == 0) {
        return arrayOf<String?>()
    }
    val list = ArrayList<String?>()
    var sizePlus1 = 1
    var i = 0
    var start = 0
    var match = false
    var lastMatch = false
    if (separatorChars == null) {
        // 空分隔符表示使用空白字符
        while (i < len) {
            if (Character.isWhitespace(str.get(i))) {
                if (match || preserveAllTokens) {
                    lastMatch = true
                    if (sizePlus1++ == max) {
                        i = len
                        lastMatch = false
                    }
                    list.add(str.substring(start, i))
                    match = false
                }
                start = ++i
                continue
            }
            lastMatch = false
            match = true
            i++
        }
    } else if (separatorChars.length == 1) {
        // 优化单字符情况
        val sep = separatorChars.get(0)
        while (i < len) {
            if (str.get(i) == sep) {
                if (match || preserveAllTokens) {
                    lastMatch = true
                    if (sizePlus1++ == max) {
                        i = len
                        lastMatch = false
                    }
                    list.add(str.substring(start, i))
                    match = false
                }
                start = ++i
                continue
            }
            lastMatch = false
            match = true
            i++
        }
    } else {
        // 标准情况
        while (i < len) {
            if (separatorChars.indexOf(str.get(i)) >= 0) {
                if (match || preserveAllTokens) {
                    lastMatch = true
                    if (sizePlus1++ == max) {
                        i = len
                        lastMatch = false
                    }
                    list.add(str.substring(start, i))
                    match = false
                }
                start = ++i
                continue
            }
            lastMatch = false
            match = true
            i++
        }
    }
    if (match || (preserveAllTokens && lastMatch)) {
        list.add(str.substring(start, i))
    }
    return list.toTypedArray()
}

/**
 * 根据字符类型拆分字符串，字符类型由 `java.lang.Character.getType(char)` 返回。
 * 连续相同类型的字符组会作为完整标记返回。
 * <pre>
 * StringUtils.splitByCharacterType(null)         = null
 * StringUtils.splitByCharacterType("")           = []
 * StringUtils.splitByCharacterType("ab de fg")   = ["ab", " ", "de", " ", "fg"]
 * StringUtils.splitByCharacterType("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
 * StringUtils.splitByCharacterType("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
 * StringUtils.splitByCharacterType("number5")    = ["number", "5"]
 * StringUtils.splitByCharacterType("fooBar")     = ["foo", "B", "ar"]
 * StringUtils.splitByCharacterType("foo200Bar")  = ["foo", "200", "B", "ar"]
 * StringUtils.splitByCharacterType("ASFRules")   = ["ASFR", "ules"]
</pre>
 *
 * @param str 要拆分的字符串，可以为 `null`
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 * @since 2.4
 */
fun String?.splitByCharacterType(): Array<String?>? {
    return splitByCharacterType(this, false)
}

/**
 * 根据字符类型拆分字符串，字符类型由 `java.lang.Character.getType(char)` 返回。
 * 连续相同类型的字符组会作为完整标记返回，例外情况如下：
 * 紧接在小写字母标记之前的大写字母字符，将归属于后续标记，而非前面的大写字母标记。
 * <pre>
 * StringUtils.splitByCharacterTypeCamelCase(null)         = null
 * StringUtils.splitByCharacterTypeCamelCase("")           = []
 * StringUtils.splitByCharacterTypeCamelCase("ab de fg")   = ["ab", " ", "de", " ", "fg"]
 * StringUtils.splitByCharacterTypeCamelCase("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
 * StringUtils.splitByCharacterTypeCamelCase("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
 * StringUtils.splitByCharacterTypeCamelCase("number5")    = ["number", "5"]
 * StringUtils.splitByCharacterTypeCamelCase("fooBar")     = ["foo", "Bar"]
 * StringUtils.splitByCharacterTypeCamelCase("foo200Bar")  = ["foo", "200", "Bar"]
 * StringUtils.splitByCharacterTypeCamelCase("ASFRules")   = ["ASF", "Rules"]
</pre>
 *
 * @param str 要拆分的字符串，可以为 `null`
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 * @since 2.4
 */
fun String?.splitByCharacterTypeCamelCase(): Array<String?>? {
    return splitByCharacterType(this, true)
}

/**
 * 根据字符类型拆分字符串，字符类型由 `java.lang.Character.getType(char)` 返回。
 * 连续相同类型的字符组会作为完整标记返回，例外情况如下：
 * 如果 `camelCase` 为 `true`，紧接在小写字母标记之前的大写字母字符，
 * 将归属于后续标记，而非前面的大写字母标记。
 *
 * @param str       要拆分的字符串，可以为 `null`
 * @param camelCase 是否对字母类型使用驼峰命名规则
 * @return 解析后的字符串数组，输入为 null 则返回 `null`
 * @since 2.4
 */
private fun splitByCharacterType(str: String?, camelCase: Boolean): Array<String?>? {
    if (str == null) {
        return null
    }
    if (str.length == 0) {
        return arrayOf<String?>()
    }
    val c = str.toCharArray()
    val list = ArrayList<String?>()
    var tokenStart = 0
    var currentType = Character.getType(c[tokenStart])
    for (pos in tokenStart + 1..<c.size) {
        val type = Character.getType(c[pos])
        if (type == currentType) {
            continue
        }
        if (camelCase && type == Character.LOWERCASE_LETTER.toInt() && currentType == Character.UPPERCASE_LETTER.toInt()) {
            val newTokenStart = pos - 1
            if (newTokenStart != tokenStart) {
                list.add(String(c, tokenStart, newTokenStart - tokenStart))
                tokenStart = newTokenStart
            }
        } else {
            list.add(String(c, tokenStart, pos - tokenStart))
            tokenStart = pos
        }
        currentType = type
    }
    list.add(String(c, tokenStart, c.size - tokenStart))
    return list.toTypedArray()
}

// 拼接
//-----------------------------------------------------------------------
/**
 * 将提供的元素拼接为单个字符串。
 *
 * 拼接后的字符串不添加分隔符。
 * null 对象或空字符串元素会以空字符串表示。
 *
 * <pre>
 * StringUtils.concatenate(null)            = null
 * StringUtils.concatenate([])              = ""
 * StringUtils.concatenate([null])          = ""
 * StringUtils.concatenate(["a", "b", "c"]) = "abc"
 * StringUtils.concatenate([null, "", "a"]) = "a"
</pre>
 *
 * @param array 要拼接的值数组，可以为 null
 * @return 拼接后的字符串，输入为 null 数组则返回 `null`
 * //@deprecated 使用命名更清晰的 [.join] 替代。
 * 此方法将在 Commons Lang 3.0 中移除。
 */
fun Array<Any?>?.concatenate(): String? {
    return this.join(null)
}

/**
 * 将提供的数组元素拼接为包含所有元素的单个字符串。
 *
 * 拼接后的字符串不添加分隔符。
 * 数组中的 null 对象或空字符串会以空字符串表示。
 *
 * <pre>
 * StringUtils.join(null)            = null
 * StringUtils.join([])              = ""
 * StringUtils.join([null])          = ""
 * StringUtils.join(["a", "b", "c"]) = "abc"
 * StringUtils.join([null, "", "a"]) = "a"
</pre>
 *
 * @param array 要拼接的值数组，可以为 null
 * @return 拼接后的字符串，输入为 null 数组则返回 `null`
 * @since 2.0
 */
fun Array<Any?>?.join(): String? {
    return this.join(null)
}

/**
 * 将提供的数组元素拼接为包含所有元素的单个字符串。
 *
 * 列表前后不添加分隔符。
 * 数组中的 null 对象或空字符串会以空字符串表示。
 *
 * <pre>
 * StringUtils.join(null, *)               = null
 * StringUtils.join([], *)                 = ""
 * StringUtils.join([null], *)             = ""
 * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
 * StringUtils.join(["a", "b", "c"], null) = "abc"
 * StringUtils.join([null, "", "a"], ';')  = ";;a"
</pre>
 *
 * @param array     要拼接的值数组，可以为 null
 * @param separator 要使用的分隔字符
 * @return 拼接后的字符串，输入为 null 数组则返回 `null`
 * @since 2.0
 */
fun Array<Any?>?.join(separator: Char): String? {
    if (this == null) {
        return null
    }

    return this.join(separator, 0, this.size)
}

/**
 * 将提供的数组元素拼接为包含所有元素的单个字符串。
 *
 * 列表前后不添加分隔符。
 * 数组中的 null 对象或空字符串会以空字符串表示。
 *
 * <pre>
 * StringUtils.join(null, *)               = null
 * StringUtils.join([], *)                 = ""
 * StringUtils.join([null], *)             = ""
 * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
 * StringUtils.join(["a", "b", "c"], null) = "abc"
 * StringUtils.join([null, "", "a"], ';')  = ";;a"
</pre>
 *
 * @param array      要拼接的值数组，可以为 null
 * @param separator  要使用的分隔字符
 * @param startIndex 开始拼接的第一个索引。
 * 传入超过数组长度的结束索引是错误的
 * @param endIndex   停止拼接的索引（不包含）。
 * 传入超过数组长度的结束索引是错误的
 * @return 拼接后的字符串，输入为 null 数组则返回 `null`
 * @since 2.0
 */
fun Array<Any?>?.join(separator: Char, startIndex: Int, endIndex: Int): String? {
    if (this == null) {
        return null
    }
    var bufSize = (endIndex - startIndex)
    if (bufSize <= 0) {
        return EMPTY
    }

    bufSize *= ((if (this[startIndex] == null) 16 else this[startIndex].toString().length()) + 1)
    val buf = StringBuilder(bufSize)

    for (i in startIndex..<endIndex) {
        if (i > startIndex) {
            buf.append(separator)
        }
        if (this[i] != null) {
            buf.append(this[i])
        }
    }
    return buf.toString()
}


/**
 * 将提供的数组元素拼接为包含所有元素的单个字符串。
 *
 * 列表前后不添加分隔符。
 * `null` 分隔符等同于空字符串（""）。
 * 数组中的 null 对象或空字符串会以空字符串表示。
 *
 * <pre>
 * StringUtils.join(null, *)                = null
 * StringUtils.join([], *)                  = ""
 * StringUtils.join([null], *)              = ""
 * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
 * StringUtils.join(["a", "b", "c"], null)  = "abc"
 * StringUtils.join(["a", "b", "c"], "")    = "abc"
 * StringUtils.join([null, "", "a"], ',')   = ",,a"
</pre>
 *
 * @param array     要拼接的值数组，可以为 null
 * @param separator 要使用的分隔字符，null 视为 ""
 * @return 拼接后的字符串，输入为 null 数组则返回 `null`
 */
fun Array<Any?>?.join(separator: String?): String? {
    if (this == null) {
        return null
    }
    return this.join(separator, 0, this.size)
}

/**
 * 将提供的数组元素拼接为包含所有元素的单个字符串。
 *
 * 列表前后不添加分隔符。
 * `null` 分隔符等同于空字符串（""）。
 * 数组中的 null 对象或空字符串会以空字符串表示。
 *
 * <pre>
 * StringUtils.join(null, *)                = null
 * StringUtils.join([], *)                  = ""
 * StringUtils.join([null], *)              = ""
 * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
 * StringUtils.join(["a", "b", "c"], null)  = "abc"
 * StringUtils.join(["a", "b", "c"], "")    = "abc"
 * StringUtils.join([null, "", "a"], ',')   = ",,a"
</pre>
 *
 * @param array      要拼接的值数组，可以为 null
 * @param separator  要使用的分隔字符，null 视为 ""
 * @param startIndex 开始拼接的第一个索引。
 * 传入超过数组长度的结束索引是错误的
 * @param endIndex   停止拼接的索引（不包含）。
 * 传入超过数组长度的结束索引是错误的
 * @return 拼接后的字符串，输入为 null 数组则返回 `null`
 */
fun Array<Any?>?.join(separator: String?, startIndex: Int, endIndex: Int): String? {
    var separator = separator
    if (this == null) {
        return null
    }
    if (separator == null) {
        separator = EMPTY
    }

    // endIndex - startIndex > 0:   长度 = 字符串数量 *(第一个字符串长度 + 分隔符长度)
    //           (假设所有字符串长度大致相等)
    var bufSize = (endIndex - startIndex)
    if (bufSize <= 0) {
        return EMPTY
    }

    bufSize *= ((if (this[startIndex] == null) 16 else this[startIndex].toString().length())
            + separator.length)

    val buf = StringBuilder(bufSize)

    for (i in startIndex..<endIndex) {
        if (i > startIndex) {
            buf.append(separator)
        }
        if (this[i] != null) {
            buf.append(this[i])
        }
    }
    return buf.toString()
}

/**
 * 将提供的迭代器元素拼接为包含所有元素的单个字符串。
 *
 * 列表前后不添加分隔符。迭代中的 null 对象或空字符串
 * 会以空字符串表示。
 *
 * 查看示例：[.join]。
 *
 * @param iterator  要拼接的值的 `Iterator`，可以为 null
 * @param separator 要使用的分隔字符
 * @return 拼接后的字符串，输入为 null 迭代器则返回 `null`
 * @since 2.0
 */
fun MutableIterator<*>?.join(separator: Char): String? {
    // 在构建缓冲区之前处理 null、零个和一个元素

    if (this == null) {
        return null
    }
    if (!this.hasNext()) {
        return EMPTY
    }
    val first = this.next()
    if (!this.hasNext()) {
        return first.toString()
    }

    // 两个或更多元素
    val buf = StringBuilder(256) // Java 默认是 16，可能太小
    if (first != null) {
        buf.append(first)
    }

    while (this.hasNext()) {
        buf.append(separator)
        val obj = this.next()
        if (obj != null) {
            buf.append(obj)
        }
    }

    return buf.toString()
}

/**
 * 将提供的迭代器元素拼接为包含所有元素的单个字符串。
 *
 * 列表前后不添加分隔符。
 * `null` 分隔符等同于空字符串（""）。
 *
 * 查看示例：[.join]。
 *
 * @param iterator  要拼接的值的 `Iterator`，可以为 null
 * @param separator 要使用的分隔字符，null 视为 ""
 * @return 拼接后的字符串，输入为 null 迭代器则返回 `null`
 */
fun MutableIterator<*>?.join(separator: String?): String? {
    // 在构建缓冲区之前处理 null、零个和一个元素

    if (this == null) {
        return null
    }
    if (!this.hasNext()) {
        return EMPTY
    }
    val first = this.next()
    if (!this.hasNext()) {
        return first.toString()
    }

    // 两个或更多元素
    val buf = StringBuilder(256) // Java 默认是 16，可能太小
    if (first != null) {
        buf.append(first)
    }

    while (this.hasNext()) {
        if (separator != null) {
            buf.append(this)
        }
        val obj = this.next()
        if (obj != null) {
            buf.append(obj)
        }
    }
    return buf.toString()
}

/**
 * 将提供的集合元素拼接为包含所有元素的单个字符串。
 *
 * 列表前后不添加分隔符。迭代中的 null 对象或空字符串
 * 会以空字符串表示。
 *
 * 查看示例：[.join]。
 *
 * @param collection 要拼接的值的 `Collection`，可以为 null
 * @param separator  要使用的分隔字符
 * @return 拼接后的字符串，输入为 null 迭代器则返回 `null`
 * @since 2.3
 */
fun MutableCollection<*>?.join(separator: Char): String? {
    if (this == null) {
        return null
    }
    return this.iterator().join(separator)
}

/**
 * 将提供的集合元素拼接为包含所有元素的单个字符串。
 *
 * 列表前后不添加分隔符。
 * `null` 分隔符等同于空字符串（""）。
 *
 * 查看示例：[.join]。
 *
 * @param collection 要拼接的值的 `Collection`，可以为 null
 * @param separator  要使用的分隔字符，null 视为 ""
 * @return 拼接后的字符串，输入为 null 迭代器则返回 `null`
 * @since 2.3
 */
fun MutableCollection<*>?.join(separator: String?): String? {
    if (this == null) {
        return null
    }
    return this.iterator().join(separator)
}

// 删除
//-----------------------------------------------------------------------
/**
删除字符串中的所有空白字符，由 [Character.isWhitespace] 定义。

 * <pre>
 * StringUtils.deleteWhitespace(null)         = null
 * StringUtils.deleteWhitespace("")           = ""
 * StringUtils.deleteWhitespace("abc")        = "abc"
 * StringUtils.deleteWhitespace("   ab  c  ") = "abc"
</pre>
 *
 * @param str 要删除空白字符的字符串，可以为 null
 * @return 不含空白字符的字符串，输入为 null 则返回 `null`
 */
fun String?.deleteWhitespace(): String? {
    if (this.isEmpty()) {
        return this
    }
    val sz = this!!.length
    val chs = CharArray(sz)
    var count = 0
    for (i in 0..<sz) {
        if (!Character.isWhitespace(this[i])) {
            chs[count++] = this[i]
        }
    }
    if (count == sz) {
        return this
    }
    return String(chs, 0, count)
}

// 移除
//-----------------------------------------------------------------------
/**
 * 仅当子串位于源字符串开头时才移除，否则返回源字符串。
 *
 * 输入 `null` 源字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 * 输入 `null` 搜索字符串会返回源字符串。
 *
 * <pre>
 * StringUtils.removeStart(null, *)      = null
 * StringUtils.removeStart("", *)        = ""
 * StringUtils.removeStart(*, null)      = *
 * StringUtils.removeStart("www.domain.com", "www.")   = "domain.com"
 * StringUtils.removeStart("domain.com", "www.")       = "domain.com"
 * StringUtils.removeStart("www.domain.com", "domain") = "www.domain.com"
 * StringUtils.removeStart("abc", "")    = "abc"
</pre>
 *
 * @param str    要搜索的源字符串，可以为 null
 * @param remove 要搜索并移除的字符串，可以为 null
 * @return 移除指定字符串后的子串（如果找到），
 * 输入为 null 则返回 `null`
 * @since 2.1
 */
fun String?.removeStart(remove: String?): String? {
    if (this.isEmpty() || remove.isEmpty()) {
        return this
    }
    if (this!!.startsWith(remove!!)) {
        return this.substring(remove.length)
    }
    return this
}

/**
 * 不区分大小写地移除位于源字符串开头的子串，
 * 否则返回源字符串。
 *
 * 输入 `null` 源字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 * 输入 `null` 搜索字符串会返回源字符串。
 *
 * <pre>
 * StringUtils.removeStartIgnoreCase(null, *)      = null
 * StringUtils.removeStartIgnoreCase("", *)        = ""
 * StringUtils.removeStartIgnoreCase(*, null)      = *
 * StringUtils.removeStartIgnoreCase("www.domain.com", "www.")   = "domain.com"
 * StringUtils.removeStartIgnoreCase("www.domain.com", "WWW.")   = "domain.com"
 * StringUtils.removeStartIgnoreCase("domain.com", "www.")       = "domain.com"
 * StringUtils.removeStartIgnoreCase("www.domain.com", "domain") = "www.domain.com"
 * StringUtils.removeStartIgnoreCase("abc", "")    = "abc"
</pre>
 *
 * @param str    要搜索的源字符串，可以为 null
 * @param remove 要搜索（不区分大小写）并移除的字符串，可以为 null
 * @return 移除指定字符串后的子串（如果找到），
 * 输入为 null 则返回 `null`
 * @since 2.4
 */
fun String?.removeStartIgnoreCase(remove: String?): String? {
    if (this.isEmpty() || remove.isEmpty()) {
        return this
    }
    if (this.startsWithIgnoreCase(remove)) {
        return this.substring(remove!!.length)
    }
    return this
}

/**
 * 仅当子串位于源字符串结尾时才移除，否则返回源字符串。
 *
 * 输入 `null` 源字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 * 输入 `null` 搜索字符串会返回源字符串。
 *
 * <pre>
 * StringUtils.removeEnd(null, *)      = null
 * StringUtils.removeEnd("", *)        = ""
 * StringUtils.removeEnd(*, null)      = *
 * StringUtils.removeEnd("www.domain.com", ".com.")  = "www.domain.com"
 * StringUtils.removeEnd("www.domain.com", ".com")   = "www.domain"
 * StringUtils.removeEnd("www.domain.com", "domain") = "www.domain.com"
 * StringUtils.removeEnd("abc", "")    = "abc"
</pre>
 *
 * @param str    要搜索的源字符串，可以为 null
 * @param remove 要搜索并移除的字符串，可以为 null
 * @return 移除指定字符串后的子串（如果找到），
 * 输入为 null 则返回 `null`
 * @since 2.1
 */
fun String?.removeEnd(remove: String?): String? {
    if (this.isEmpty() || remove.isEmpty()) {
        return this
    }
    if (this!!.endsWith(remove!!)) {
        return this.substring(0, this.length - remove.length)
    }
    return this
}

/**
 * 不区分大小写地移除位于源字符串结尾的子串，
 * 否则返回源字符串。
 *
 * 输入 `null` 源字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 * 输入 `null` 搜索字符串会返回源字符串。
 *
 * <pre>
 * StringUtils.removeEndIgnoreCase(null, *)      = null
 * StringUtils.removeEndIgnoreCase("", *)        = ""
 * StringUtils.removeEndIgnoreCase(*, null)      = *
 * StringUtils.removeEndIgnoreCase("www.domain.com", ".com.")  = "www.domain.com"
 * StringUtils.removeEndIgnoreCase("www.domain.com", ".com")   = "www.domain"
 * StringUtils.removeEndIgnoreCase("www.domain.com", "domain") = "www.domain.com"
 * StringUtils.removeEndIgnoreCase("abc", "")    = "abc"
 * StringUtils.removeEndIgnoreCase("www.domain.com", ".COM") = "www.domain"
 * StringUtils.removeEndIgnoreCase("www.domain.COM", ".com") = "www.domain"
</pre>
 *
 * @param str    要搜索的源字符串，可以为 null
 * @param remove 要搜索（不区分大小写）并移除的字符串，可以为 null
 * @return 移除指定字符串后的子串（如果找到），
 * 输入为 null 则返回 `null`
 * @since 2.4
 */
fun String?.removeEndIgnoreCase(remove: String?): String? {
    if (this.isEmpty() || remove.isEmpty()) {
        return this
    }
    if (this.endsWithIgnoreCase(remove)) {
        return this!!.substring(0, this.length - remove!!.length)
    }
    return this
}

/**
 * 移除源字符串中所有出现的指定子串。
 *
 * 输入 `null` 源字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 * 输入 `null` 要移除的字符串会返回源字符串。
 * 输入空字符串（""）要移除的字符串会返回源字符串。
 *
 * <pre>
 * StringUtils.remove(null, *)        = null
 * StringUtils.remove("", *)          = ""
 * StringUtils.remove(*, null)        = *
 * StringUtils.remove(*, "")          = *
 * StringUtils.remove("queued", "ue") = "qd"
 * StringUtils.remove("queued", "zz") = "queued"
</pre>
 *
 * @param str    要搜索的源字符串，可以为 null
 * @param remove 要搜索并移除的字符串，可以为 null
 * @return 移除指定字符串后的子串（如果找到），
 * 输入为 null 则返回 `null`
 * @since 2.1
 */
fun String?.remove(remove: String?): String? {
    if (this.isEmpty() || remove.isEmpty()) {
        return this
    }
    return this.replace(remove, EMPTY, -1)
}

/**
 * 移除源字符串中所有出现的指定字符。
 *
 * 输入 `null` 源字符串会返回 `null`。
 * 输入空字符串（""）会返回空字符串。
 *
 * <pre>
 * StringUtils.remove(null, *)       = null
 * StringUtils.remove("", *)         = ""
 * StringUtils.remove("queued", 'u') = "qeed"
 * StringUtils.remove("queued", 'z') = "queued"
</pre>
 *
 * @param str    要搜索的源字符串，可以为 null
 * @param remove 要搜索并移除的字符
 * @return 移除指定字符后的子串（如果找到），
 * 输入为 null 则返回 `null`
 * @since 2.1
 */
fun String?.remove(remove: Char): String? {
    if (this.isEmpty() || this.indexOf(remove) == INDEX_NOT_FOUND) {
        return this
    }
    val chars = this!!.toCharArray()
    var pos = 0
    for (i in chars.indices) {
        if (chars[i] != remove) {
            chars[pos++] = chars[i]
        }
    }
    return String(chars, 0, pos)
}

// 替换
//-----------------------------------------------------------------------
/**
 * 在一个大字符串中替换一次指定的子串。
 *
 * 传入 `null` 引用时此方法不执行任何操作。
 *
 * <pre>
 * StringUtils.replaceOnce(null, *, *)        = null
 * StringUtils.replaceOnce("", *, *)          = ""
 * StringUtils.replaceOnce("any", null, *)    = "any"
 * StringUtils.replaceOnce("any", *, null)    = "any"
 * StringUtils.replaceOnce("any", "", *)      = "any"
 * StringUtils.replaceOnce("aba", "a", null)  = "aba"
 * StringUtils.replaceOnce("aba", "a", "")    = "ba"
 * StringUtils.replaceOnce("aba", "a", "z")   = "zba"
</pre>
 *
 * @param text         要搜索和替换的文本，可以为 null
 * @param searchString 要搜索的字符串，可以为 null
 * @param replacement  用于替换的字符串，可以为 null
 * @return 完成替换后的文本，输入为 null 则返回 `null`
 * @see .replace
 */
fun String?.replaceOnce(searchString: String?, replacement: String?): String? {
    return this.replace(searchString, replacement, 1)
}

/**
 * 在一个字符串中替换所有出现的指定子串。
 *
 * 传入 `null` 引用时此方法不执行任何操作。
 *
 * <pre>
 * StringUtils.replace(null, *, *)        = null
 * StringUtils.replace("", *, *)          = ""
 * StringUtils.replace("any", null, *)    = "any"
 * StringUtils.replace("any", *, null)    = "any"
 * StringUtils.replace("any", "", *)      = "any"
 * StringUtils.replace("aba", "a", null)  = "aba"
 * StringUtils.replace("aba", "a", "")    = "b"
 * StringUtils.replace("aba", "a", "z")   = "zbz"
</pre>
 *
 * @param text         要搜索和替换的文本，可以为 null
 * @param searchString 要搜索的字符串，可以为 null
 * @param replacement  用于替换的字符串，可以为 null
 * @return 完成替换后的文本，输入为 null 则返回 `null`
 * @see .replace
 */
fun String?.replace(searchString: String?, replacement: String?): String? {
    return this.replace(searchString, replacement, -1)
}

/**
 * 在一个大字符串中替换指定子串，最多替换前 `max` 次。
 *
 * 传入 `null` 引用时此方法不执行任何操作。
 *
 * <pre>
 * StringUtils.replace(null, *, *, *)         = null
 * StringUtils.replace("", *, *, *)           = ""
 * StringUtils.replace("any", null, *, *)     = "any"
 * StringUtils.replace("any", *, null, *)     = "any"
 * StringUtils.replace("any", "", *, *)       = "any"
 * StringUtils.replace("any", *, *, 0)        = "any"
 * StringUtils.replace("abaa", "a", null, -1) = "abaa"
 * StringUtils.replace("abaa", "a", "", -1)   = "b"
 * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
 * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
 * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
 * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
</pre>
 *
 * @param text         要搜索和替换的文本，可以为 null
 * @param searchString 要搜索的字符串，可以为 null
 * @param replacement  用于替换的字符串，可以为 null
 * @param max          最大替换次数，`-1` 表示无限制
 * @return 完成替换后的文本，输入为 null 则返回 `null`
 */
fun String?.replace(searchString: String?, replacement: String?, max: Int): String? {
    var max = max
    if (this.isEmpty() || searchString.isEmpty() || replacement == null || max == 0) {
        return this
    }
    var start = 0
    var end = this!!.indexOf(searchString!!, start)
    if (end == INDEX_NOT_FOUND) {
        return this
    }
    val replLength = searchString.length
    var increase = replacement.length - replLength
    increase = (if (increase < 0) 0 else increase)
    increase *= (if (max < 0) 16 else (if (max > 64) 64 else max))
    val buf = StringBuilder(this.length + increase)
    while (end != INDEX_NOT_FOUND) {
        buf.append(this.substring(start, end)).append(replacement)
        start = end + replLength
        if (--max == 0) {
            break
        }
        end = this.indexOf(searchString, start)
    }
    buf.append(this.substring(start))
    return buf.toString()
}

/**
 * 在一个字符串中批量替换多个子串。
 *
 * 传入 `null` 引用时此方法不执行任何操作；
 * 如果任意“搜索字符串”或“替换字符串”为 null，则该次替换会被忽略。
 * 此方法不会循环替换。如需循环替换，请调用重载方法。
 *
 * <pre>
 * StringUtils.replaceEach(null, *, *)        = null
 * StringUtils.replaceEach("", *, *)          = ""
 * StringUtils.replaceEach("aba", null, null) = "aba"
 * StringUtils.replaceEach("aba", new String[0], null) = "aba"
 * StringUtils.replaceEach("aba", null, new String[0]) = "aba"
 * StringUtils.replaceEach("aba", new String[]{"a"}, null)  = "aba"
 * StringUtils.replaceEach("aba", new String[]{"a"}, new String[]{""})  = "b"
 * StringUtils.replaceEach("aba", new String[]{null}, new String[]{"a"})  = "aba"
 * StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"})  = "wcte"
 * (示例：不会循环替换)
 * StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"})  = "dcte"
</pre>
 *
 * @param text            要搜索和替换的文本，为 null 时不操作
 * @param searchList      要搜索的字符串数组，为 null 时不操作
 * @param replacementList 用于替换的字符串数组，为 null 时不操作
 * @return 完成替换后的文本，输入为 null 则返回 `null`
 * @throws IndexOutOfBoundsException 如果数组长度不匹配（null 或长度 0 除外）
 * @since 2.4
 */
fun String?.replaceEach(
    searchList: Array<String?>?,
    replacementList: Array<String?>?
): String? {
    return replaceEach(this, searchList, replacementList, false, 0)
}

/**
 * 在一个字符串中批量替换多个子串，并支持循环替换。
 *
 * 传入 `null` 引用时此方法不执行任何操作；
 * 如果任意“搜索字符串”或“替换字符串”为 null，则该次替换会被忽略。
 *
 * <pre>
 * StringUtils.replaceEach(null, *, *, *) = null
 * StringUtils.replaceEach("", *, *, *) = ""
 * StringUtils.replaceEach("aba", null, null, *) = "aba"
 * StringUtils.replaceEach("aba", new String[0], null, *) = "aba"
 * StringUtils.replaceEach("aba", null, new String[0], *) = "aba"
 * StringUtils.replaceEach("aba", new String[]{"a"}, null, *) = "aba"
 * StringUtils.replaceEach("aba", new String[]{"a"}, new String[]{""}, *) = "b"
 * StringUtils.replaceEach("aba", new String[]{null}, new String[]{"a"}, *) = "aba"
 * StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"}, *) = "wcte"
 * (循环替换示例)
 * StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, false) = "dcte"
 * StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, true) = "tcte"
 * StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, true) = IllegalArgumentException
 * StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, false) = "dcabe"
</pre>
 *
 * @param text            要搜索和替换的文本，为 null 时不操作
 * @param searchList      要搜索的字符串数组，为 null 时不操作
 * @param replacementList 用于替换的字符串数组，为 null 时不操作
 * @return 完成替换后的文本，输入为 null 则返回 `null`
 * @throws IllegalArgumentException  如果循环替换时因互相替换产生无限循环
 * @throws IndexOutOfBoundsException 如果数组长度不匹配（null 或长度 0 除外）
 * @since 2.4
 */
fun String?.replaceEachRepeatedly(
    searchList: Array<String?>?,
    replacementList: Array<String?>?
): String? {
    // 如果未使用或无替换内容则 timeToLive 为 0，否则为替换数组长度
    val timeToLive = if (searchList == null) 0 else searchList.size
    return replaceEach(this, searchList, replacementList, true, timeToLive)
}

/**
 * 在一个字符串中批量替换多个子串，支持循环替换与生命周期控制。
 *
 * 传入 `null` 引用时此方法不执行任何操作；
 * 如果任意“搜索字符串”或“替换字符串”为 null，则该次替换会被忽略。
 *
 * <pre>
 * StringUtils.replaceEach(null, *, *, *) = null
 * StringUtils.replaceEach("", *, *, *) = ""
 * StringUtils.replaceEach("aba", null, null, *) = "aba"
 * StringUtils.replaceEach("aba", new String[0], null, *) = "aba"
 * StringUtils.replaceEach("aba", null, new String[0], *) = "aba"
 * StringUtils.replaceEach("aba", new String[]{"a"}, null, *) = "aba"
 * StringUtils.replaceEach("aba", new String[]{"a"}, new String[]{""}, *) = "b"
 * StringUtils.replaceEach("aba", new String[]{null}, new String[]{"a"}, *) = "aba"
 * StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"}, *) = "wcte"
 * (循环替换示例)
 * StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, false) = "dcte"
 * StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, true) = "tcte"
 * StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, *) = IllegalArgumentException
</pre>
 *
 * @param text            要搜索和替换的文本，为 null 时不操作
 * @param searchList      要搜索的字符串数组，为 null 时不操作
 * @param replacementList 用于替换的字符串数组，为 null 时不操作
 * @param repeat          如果为 true，则重复替换直到无匹配或生命周期结束
 * @param timeToLive      小于 0 表示存在循环引用与无限循环
 * @return 完成替换后的文本，输入为 null 则返回 `null`
 * @throws IllegalArgumentException  如果循环替换时因互相替换产生无限循环
 * @throws IndexOutOfBoundsException 如果数组长度不匹配（null 或长度 0 除外）
 * @since 2.4
 */
private fun replaceEach(
    text: String?, searchList: Array<String?>?, replacementList: Array<String?>?,
    repeat: Boolean, timeToLive: Int
): String? {
    // 性能说明：此方法创建极少新对象（核心目标）
    // 如有性能需求可告知，我可编写测试工具进行评估

    if (text == null || text.length == 0 || searchList == null || searchList.size == 0 || replacementList == null || replacementList.size == 0) {
        return text
    }

    // 递归时不应小于 0
    check(timeToLive >= 0) { "TimeToLive of $timeToLive is less than 0: $text" }

    val searchLength = searchList.size
    val replacementLength = replacementList.size

    // 确保长度一致
    require(searchLength == replacementLength) {
        ("Search and Replace array lengths don't match: "
                + searchLength
                + " vs "
                + replacementLength)
    }

    // 记录哪些索引已无匹配
    val noMoreMatchesForReplIndex = BooleanArray(searchLength)

    // 匹配位置索引
    var textIndex = -1
    var replaceIndex = -1
    var tempIndex = -1

    // 查找最早匹配
    for (i in 0..<searchLength) {
        if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i]!!.length == 0 || replacementList[i] == null) {
            continue
        }
        tempIndex = text.indexOf(searchList[i]!!)

        if (tempIndex == -1) {
            noMoreMatchesForReplIndex[i] = true
        } else {
            if (textIndex == -1 || tempIndex < textIndex) {
                textIndex = tempIndex
                replaceIndex = i
            }
        }
    }

    // 无匹配则结束
    if (textIndex == -1) {
        return text
    }

    var start = 0

    // 预估缓冲区大小
    var increase = 0

    // 计算替换后长度增量
    for (i in searchList.indices) {
        if (searchList[i] == null || replacementList[i] == null) {
            continue
        }
        val greater = replacementList[i]!!.length - searchList[i]!!.length
        if (greater > 0) {
            increase += 3 * greater // 假设 3 次匹配
        }
    }
    // 上限设为 20% 增长
    increase = min(increase, text.length / 5)

    val buf = StringBuilder(text.length + increase)

    while (textIndex != -1) {
        for (i in start downTo textIndex) {
            buf.append(text[i])
        }
        buf.append(replacementList[replaceIndex])

        start = textIndex + searchList[replaceIndex]!!.length

        textIndex = -1
        replaceIndex = -1
        tempIndex = -1
        // 查找下一个最早匹配
        for (i in 0..<searchLength) {
            if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i]!!.length == 0 || replacementList[i] == null) {
                continue
            }
            tempIndex = text.indexOf(searchList[i]!!, start)

            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true
            } else {
                if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex
                    replaceIndex = i
                }
            }
        }
    }
    val textLength = text.length
    for (i in start..<textLength) {
        buf.append(text.get(i))
    }
    val result = buf.toString()
    if (!repeat) {
        return result
    }

    return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1)
}

// 基于字符的替换
//-----------------------------------------------------------------------
/**
 * 将字符串中的所有指定字符替换为另一个字符。
 * 这是 [String.replace] 的空安全版本。
 *
 * 输入 `null` 字符串返回 `null`。
 * 输入空字符串返回空字符串。
 *
 * <pre>
 * StringUtils.replaceChars(null, *, *)        = null
 * StringUtils.replaceChars("", *, *)          = ""
 * StringUtils.replaceChars("abcba", 'b', 'y') = "aycya"
 * StringUtils.replaceChars("abcba", 'z', 'y') = "abcba"
</pre>
 *
 * @param str         要替换字符的字符串，可以为 null
 * @param searchChar  要搜索的字符
 * @param replaceChar 用于替换的字符
 * @return 替换后的字符串，输入为 null 则返回 `null`
 * @since 2.0
 */
fun String?.replaceChars(searchChar: Char, replaceChar: Char): String? {
    if (this == null) {
        return null
    }
    return this.replace(searchChar, replaceChar)
}

/**
 * 一次性替换字符串中的多个字符，也可用于删除字符。
 *
 * 例如：
 * `replaceChars("hello", "ho", "jy") = jelly`。
 *
 * 输入 `null` 字符串返回 `null`。
 * 输入空字符串返回空字符串。
 * 搜索字符集为 null 或空则返回原字符串。
 *
 * 搜索字符集长度通常应等于替换字符集长度。
 * 如果搜索字符集更长，多余的字符会被删除。
 * 如果搜索字符集更短，多余的替换字符会被忽略。
 *
 * <pre>
 * StringUtils.replaceChars(null, *, *)           = null
 * StringUtils.replaceChars("", *, *)             = ""
 * StringUtils.replaceChars("abc", null, *)       = "abc"
 * StringUtils.replaceChars("abc", "", *)         = "abc"
 * StringUtils.replaceChars("abc", "b", null)     = "ac"
 * StringUtils.replaceChars("abc", "b", "")       = "ac"
 * StringUtils.replaceChars("abcba", "bc", "yz")  = "ayzya"
 * StringUtils.replaceChars("abcba", "bc", "y")   = "ayya"
 * StringUtils.replaceChars("abcba", "bc", "yzx") = "ayzya"
</pre>
 *
 * @param str          要替换字符的字符串，可以为 null
 * @param searchChars  要搜索的字符集，可以为 null
 * @param replaceChars 用于替换的字符集，可以为 null
 * @return 替换后的字符串，输入为 null 则返回 `null`
 * @since 2.0
 */
fun String?.replaceChars(searchChars: String?, replaceChars: String?): String? {
    var replaceChars = replaceChars
    if (this.isEmpty() || searchChars.isEmpty()) {
        return this
    }
    if (replaceChars == null) {
        replaceChars = EMPTY
    }
    var modified = false
    val replaceCharsLength = replaceChars.length
    val strLength = this!!.length
    val buf = StringBuilder(strLength)
    for (i in 0..<strLength) {
        val ch = this.get(i)
        val index = searchChars!!.indexOf(ch)
        if (index >= 0) {
            modified = true
            if (index < replaceCharsLength) {
                buf.append(replaceChars.get(index))
            }
        } else {
            buf.append(ch)
        }
    }
    if (modified) {
        return buf.toString()
    }
    return this
}

// 覆盖
//-----------------------------------------------------------------------
/**
 * 用另一个字符串覆盖原字符串的指定部分。
 *
 * <pre>
 * StringUtils.overlayString(null, *, *, *)           = NullPointerException
 * StringUtils.overlayString(*, null, *, *)           = NullPointerException
 * StringUtils.overlayString("", "abc", 0, 0)         = "abc"
 * StringUtils.overlayString("abcdef", null, 2, 4)    = "abef"
 * StringUtils.overlayString("abcdef", "", 2, 4)      = "abef"
 * StringUtils.overlayString("abcdef", "zzzz", 2, 4)  = "abzzzzef"
 * StringUtils.overlayString("abcdef", "zzzz", 4, 2)  = "abcdzzzzcdef"
 * StringUtils.overlayString("abcdef", "zzzz", -1, 4) = IndexOutOfBoundsException
 * StringUtils.overlayString("abcdef", "zzzz", 2, 8)  = IndexOutOfBoundsException
</pre>
 *
 * @param text    要进行覆盖的字符串，可以为 null
 * @param overlay 用于覆盖的字符串，可以为 null
 * @param start   开始覆盖的位置，必须有效
 * @param end     结束覆盖的位置（不包含），必须有效
 * @return 覆盖后的字符串，输入为 null 则返回 `null`
 * @throws NullPointerException      如果 text 或 overlay 为 null
 * @throws IndexOutOfBoundsException 如果任一位置无效
 * //@deprecated 使用命名更清晰的 [.overlay] 替代。
 * 此方法将在 Commons Lang 3.0 中移除。
 */
fun String?.overlayString(overlay: String, start: Int, end: Int): String {
    val len = this?.length ?: 0
    return StringBuilder(start + overlay.length + len - end + 1)
        .append(this.substring(0, start))
        .append(overlay)
        .append(this.substring(end))
        .toString()
}

/**
 * 用另一个字符串覆盖原字符串的指定部分。
 *
 * 输入 `null` 字符串返回 `null`。
 * 负索引视为 0。
 * 超出字符串长度的索引视为长度值。
 * 起始索引始终取两个索引中的较小值。
 *
 * <pre>
 * StringUtils.overlay(null, *, *, *)            = null
 * StringUtils.overlay("", "abc", 0, 0)          = "abc"
 * StringUtils.overlay("abcdef", null, 2, 4)     = "abef"
 * StringUtils.overlay("abcdef", "", 2, 4)       = "abef"
 * StringUtils.overlay("abcdef", "", 4, 2)       = "abef"
 * StringUtils.overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
 * StringUtils.overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
 * StringUtils.overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
 * StringUtils.overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
 * StringUtils.overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
 * StringUtils.overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
</pre>
 *
 * @param str     要进行覆盖的字符串，可以为 null
 * @param overlay 用于覆盖的字符串，可以为 null
 * @param start   开始覆盖的位置
 * @param end     结束覆盖的位置（不包含）
 * @return 覆盖后的字符串，输入为 null 则返回 `null`
 * @since 2.0
 */
fun String?.overlay(overlay: String?, start: Int, end: Int): String? {
    var overlay = overlay
    var start = start
    var end = end
    if (this == null) {
        return null
    }
    if (overlay == null) {
        overlay = EMPTY
    }
    val len = this.length
    if (start < 0) {
        start = 0
    }
    if (start > len) {
        start = len
    }
    if (end < 0) {
        end = 0
    }
    if (end > len) {
        end = len
    }
    if (start > end) {
        val temp = start
        start = end
        end = temp
    }
    return StringBuilder(len + start - end + overlay.length + 1)
        .append(this.substring(0, start))
        .append(overlay)
        .append(this.substring(end))
        .toString()
}

// 截断
//-----------------------------------------------------------------------
/**
 * 如果字符串以指定分隔符结尾，则移除该分隔符；否则保持不变。
 *
 * 注意：此方法在 2.0 版本中发生了变化。
 * 现在更接近 Perl 的 chomp 行为。
 * 如需旧行为，请使用 [.substringBeforeLast]。
 * 此方法使用 [String.endsWith]。
 *
 * <pre>
 * StringUtils.chomp(null, *)         = null
 * StringUtils.chomp("", *)           = ""
 * StringUtils.chomp("foobar", "bar") = "foo"
 * StringUtils.chomp("foobar", "baz") = "foobar"
 * StringUtils.chomp("foo", "foo")    = ""
 * StringUtils.chomp("foo ", "foo")   = "foo "
 * StringUtils.chomp(" foo", "foo")   = " "
 * StringUtils.chomp("foo", "foooo")  = "foo"
 * StringUtils.chomp("foo", "")       = "foo"
 * StringUtils.chomp("foo", null)     = "foo"
</pre>
 *
 * @param str       要截断的字符串，可以为 null
 * @param separator 分隔符字符串，可以为 null
 * @return 移除尾部分隔符后的字符串，输入为 null 则返回 `null`
 */
fun String?.chomp(separator: String?): String? {
    if (this.isEmpty() || separator == null) {
        return this
    }
    if (this?.endsWith(separator) ?: false) {
        return this.substring(0, this.length - separator.length)
    }
    return this
}

/**
 * 仅当字符串以指定值结尾时移除该值。
 *
 * @param str 要截断的字符串，不能为 null
 * @param sep 要移除的字符串，不能为 null
 * @return 移除结尾后的字符串
 * @throws NullPointerException 如果 str 或 sep 为 `null`
 * //@deprecated 使用 [.chomp] 替代。
 * 此方法将在 Commons Lang 3.0 中移除。
 */
@JvmOverloads
fun String.chompLast(sep: String = "\n"): String {
    if (this.isEmpty()) {
        return this
    }
    val sub = this.substring(this.length - sep.length)
    if (sep == sub) {
        return this.substring(0, this.length - sep.length)!!
    }
    return this
}

/**
 * 移除字符串中最后一次出现的指定值及其之后的所有内容并返回。
 *
 * @param str 要处理的字符串，不能为 null
 * @param sep 要截断的字符串，不能为 null
 * @return 处理后的字符串
 * @throws NullPointerException 如果 str 或 sep 为 `null`
 * //@deprecated 改用 [.substringAfterLast]
 * (但此方法不包含分隔符)
 * 此方法将在 Commons Lang 3.0 中移除。
 */
fun String.getChomp(str: String, sep: String): String {
    val idx = str.lastIndexOf(sep)
    return if (idx == str.length - sep.length) {
        sep
    } else if (idx != -1) {
        str.substring(idx)!!
    } else {
        EMPTY
    }
}

/**
 * 移除字符串中第一次出现的指定值及其之前的所有内容。
 *
 * @param str 要处理的字符串，不能为 null
 * @param sep 要截断的字符串，不能为 null
 * @return 处理后的字符串
 * @throws NullPointerException 如果 str 或 sep 为 `null`
 * //@deprecated 改用 [.substringAfter]。
 * 此方法将在 Commons Lang 3.0 中移除。
 */
fun String.prechomp(str: String, sep: String): String {
    val idx = str.indexOf(sep)
    if (idx == -1) {
        return str
    }
    return str.substring(idx + sep.length)!!
}

/**
 * 移除并返回字符串中第一次出现的指定值及其之前的所有内容。
 *
 * @param str 要处理的字符串，不能为 null
 * @param sep 要截断的字符串，不能为 null
 * @return 被移除的内容
 * @throws NullPointerException 如果 str 或 sep 为 `null`
 * //@deprecated 改用 [.substringBefore]
 * (但此方法不包含分隔符)。
 * 此方法将在 Commons Lang 3.0 中移除。
 */
fun String?.getPrechomp(str: String, sep: String): String {
    val idx = str.indexOf(sep)
    if (idx == -1) {
        return EMPTY
    }
    return str.substring(0, idx + sep.length)!!
}

// 填充
//-----------------------------------------------------------------------
/**
 * 将字符串重复 `repeat` 次生成新字符串。
 *
 * <pre>
 * StringUtils.repeat(null, 2) = null
 * StringUtils.repeat("", 0)   = ""
 * StringUtils.repeat("", 2)   = ""
 * StringUtils.repeat("a", 3)  = "aaa"
 * StringUtils.repeat("ab", 2) = "abab"
 * StringUtils.repeat("a", -2) = ""
</pre>
 *
 * @param str    要重复的字符串，可以为 null
 * @param repeat 重复次数，负数视为 0
 * @return 由原字符串重复组成的新字符串，输入为 null 则返回 `null`
 */
fun String?.repeat(repeat: Int): String? {
    // 针对 2.0 (JDK1.4) 优化了性能

    if (this == null) {
        return null
    }
    if (repeat <= 0) {
        return EMPTY
    }
    val inputLength = this.length
    if (repeat == 1 || inputLength == 0) {
        return this
    }
    if (inputLength == 1 && repeat <= PAD_LIMIT) {
        return padding(repeat, this.get(0))
    }

    val outputLength = inputLength * repeat
    when (inputLength) {
        1 -> {
            val ch = this[0]
            val output1 = CharArray(outputLength)
            var i = repeat - 1
            while (i >= 0) {
                output1[i] = ch
                i--
            }
            return String(output1)
        }

        2 -> {
            val ch0 = this[0]
            val ch1 = this[1]
            val output2 = CharArray(outputLength)
            var i = repeat * 2 - 2
            while (i >= 0) {
                output2[i] = ch0
                output2[i + 1] = ch1
                i--
            }
            return String(output2)
        }

        else -> {
            val buf = StringBuilder(outputLength)
            var i = 0
            while (i < repeat) {
                buf.append(this)
                i++
            }
            return buf.toString()
        }
    }
}

/**
 * 将字符串重复 `repeat` 次生成新字符串，每次插入分隔符。
 *
 * <pre>
 * StringUtils.repeat(null, null, 2) = null
 * StringUtils.repeat(null, "x", 2)  = null
 * StringUtils.repeat("", null, 0)   = ""
 * StringUtils.repeat("", "", 2)     = ""
 * StringUtils.repeat("", "x", 3)    = "xxx"
 * StringUtils.repeat("?", ", ", 3)  = "?, ?, ?"
</pre>
 *
 * @param str       要重复的字符串，可以为 null
 * @param separator 要插入的分隔符字符串，可以为 null
 * @param repeat    重复次数，负数视为 0
 * @return 由原字符串重复组成的新字符串，输入为 null 则返回 `null`
 * @since 2.5
 */
fun String?.repeat(separator: String?, repeat: Int): String? {
    if (this == null || separator == null) {
        return this.repeat(repeat)
    } else {
        // 基于 repeat(String, int) 的优化实现
        val result = (this + separator).repeat(repeat)
        return result.removeEnd(separator)
    }
}

/**
 * 使用指定字符重复生成指定长度的填充字符串。
 *
 * <pre>
 * StringUtils.padding(0, 'e')  = ""
 * StringUtils.padding(3, 'e')  = "eee"
 * StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
</pre>
 *
 * 注意：此方法不支持 Unicode 增补字符填充，
 * 因为它们需要一对 char 表示。
 * 如需完整国际化支持，请考虑使用 [.repeat]。
 *
 * @param repeat  重复次数
 * @param padChar 要重复的字符
 * @return 重复字符组成的字符串
 * @throws IndexOutOfBoundsException 如果 `repeat < 0`
 * @see .repeat
 */
@Throws(IndexOutOfBoundsException::class)
private fun padding(repeat: Int, padChar: Char): String {
    if (repeat < 0) {
        throw IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat)
    }
    val buf = CharArray(repeat)
    for (i in buf.indices) {
        buf[i] = padChar
    }
    return String(buf)
}

/**
 * 使用指定字符在字符串右侧填充至目标长度。
 *
 * <pre>
 * StringUtils.rightPad(null, *, *)     = null
 * StringUtils.rightPad("", 3, 'z')     = "zzz"
 * StringUtils.rightPad("bat", 3, 'z')  = "bat"
 * StringUtils.rightPad("bat", 5, 'z')  = "batzz"
 * StringUtils.rightPad("bat", 1, 'z')  = "bat"
 * StringUtils.rightPad("bat", -1, 'z') = "bat"
</pre>
 *
 * @param str     要填充的字符串，可以为 null
 * @param size    目标长度
 * @param padChar 用于填充的字符
 * @return 右侧填充后的字符串，输入为 null 则返回 `null`
 * @since 2.0
 */
@JvmOverloads
fun String?.rightPad(size: Int, padChar: Char = ' '): String? {
    if (this == null) {
        return null
    }
    val pads = size - this.length
    if (pads <= 0) {
        return this // 无需填充时返回原字符串
    }
    if (pads > PAD_LIMIT) {
        return this.rightPad(size, padChar.toString())
    }
    return this + padding(pads, padChar)
}

/**
 * 使用指定字符串在字符串右侧填充至目标长度。
 *
 * <pre>
 * StringUtils.rightPad(null, *, *)      = null
 * StringUtils.rightPad("", 3, "z")      = "zzz"
 * StringUtils.rightPad("bat", 3, "yz")  = "bat"
 * StringUtils.rightPad("bat", 5, "yz")  = "batyz"
 * StringUtils.rightPad("bat", 8, "yz")  = "batyzyzy"
 * StringUtils.rightPad("bat", 1, "yz")  = "bat"
 * StringUtils.rightPad("bat", -1, "yz") = "bat"
 * StringUtils.rightPad("bat", 5, null)  = "bat  "
 * StringUtils.rightPad("bat", 5, "")    = "bat  "
</pre>
 *
 * @param str    要填充的字符串，可以为 null
 * @param size   目标长度
 * @param padStr 用于填充的字符串，null 或空视为空格
 * @return 右侧填充后的字符串，输入为 null 则返回 `null`
 */
fun String?.rightPad(size: Int, padStr: String?): String? {
    var padStr = padStr
    if (this == null) {
        return null
    }
    if (padStr.isEmpty()) {
        padStr = " "
    }
    val padLen = padStr!!.length
    val strLen = this.length
    val pads = size - strLen
    if (pads <= 0) {
        return this // 无需填充时返回原字符串
    }
    if (padLen == 1 && pads <= PAD_LIMIT) {
        return this.rightPad(size, padStr[0])
    }

    if (pads == padLen) {
        return this + padStr
    } else if (pads < padLen) {
        return this + padStr.substring(0, pads)
    } else {
        val padding = CharArray(pads)
        val padChars = padStr.toCharArray()
        for (i in 0..<pads) {
            padding[i] = padChars[i % padLen]
        }
        return this + String(padding)
    }
}

/**
 * 使用指定字符在字符串左侧填充至目标长度。
 *
 * <pre>
 * StringUtils.leftPad(null, *, *)     = null
 * StringUtils.leftPad("", 3, 'z')     = "zzz"
 * StringUtils.leftPad("bat", 3, 'z')  = "bat"
 * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
 * StringUtils.leftPad("bat", 1, 'z')  = "bat"
 * StringUtils.leftPad("bat", -1, 'z') = "bat"
</pre>
 *
 * @param str     要填充的字符串，可以为 null
 * @param size    目标长度
 * @param padChar 用于填充的字符
 * @return 左侧填充后的字符串，输入为 null 则返回 `null`
 * @since 2.0
 */
@JvmOverloads
fun String?.leftPad(size: Int, padChar: Char = ' '): String? {
    if (this == null) {
        return null
    }
    val pads = size - this.length
    if (pads <= 0) {
        return this // 无需填充时返回原字符串
    }
    if (pads > PAD_LIMIT) {
        return this.leftPad(size, padChar.toString())
    }
    return padding(pads, padChar) + this
}

/**
 * 使用指定字符串在字符串左侧填充至目标长度。
 *
 * <pre>
 * StringUtils.leftPad(null, *, *)      = null
 * StringUtils.leftPad("", 3, "z")      = "zzz"
 * StringUtils.leftPad("bat", 3, "yz")  = "bat"
 * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
 * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
 * StringUtils.leftPad("bat", 1, "yz")  = "bat"
 * StringUtils.leftPad("bat", -1, "yz") = "bat"
 * StringUtils.leftPad("bat", 5, null)  = "  bat"
 * StringUtils.leftPad("bat", 5, "")    = "  bat"
</pre>
 *
 * @param str    要填充的字符串，可以为 null
 * @param size   目标长度
 * @param padStr 用于填充的字符串，null 或空视为空格
 * @return 左侧填充后的字符串，输入为 null 则返回 `null`
 */
fun String?.leftPad(size: Int, padStr: String?): String? {
    var padStr = padStr
    if (this == null) {
        return null
    }
    if (padStr.isEmpty()) {
        padStr = " "
    }
    val padLen = padStr!!.length
    val strLen = this.length
    val pads = size - strLen
    if (pads <= 0) {
        return this // 无需填充时返回原字符串
    }
    if (padLen == 1 && pads <= PAD_LIMIT) {
        return this.leftPad(size, padStr.get(0))
    }

    if (pads == padLen) {
        return padStr + this
    } else if (pads < padLen) {
        return padStr.substring(0, pads) + this
    } else {
        val padding = CharArray(pads)
        val padChars = padStr.toCharArray()
        for (i in 0..<pads) {
            padding[i] = padChars[i % padLen]
        }
        return String(padding) + this
    }
}

/**
 * 获取字符串长度；如果为 `null` 则返回 0。
 *
 * @param str 字符串或 `null`
 * @return 字符串长度；如果为 `null` 则返回 0。
 * @since 2.4
 */
fun String?.length(): Int {
    return if (this == null) 0 else this.length
}


// 居中对齐
//-----------------------------------------------------------------------
/**
 *
 * 将字符串居中放置在长度为 `size` 的更大字符串中。
 * 使用指定的字符作为填充字符串的字符。
 *
 *
 * 如果目标长度小于字符串本身长度，则直接返回原字符串。
 * 若输入字符串为 `null`，则返回 `null`。
 * 负数长度按 0 处理。
 *
 * <pre>
 * StringUtils.center(null, *, *)     = null
 * StringUtils.center("", 4, ' ')     = "    "
 * StringUtils.center("ab", -1, ' ')  = "ab"
 * StringUtils.center("ab", 4, ' ')   = " ab"
 * StringUtils.center("abcd", 2, ' ') = "abcd"
 * StringUtils.center("a", 4, ' ')    = " a  "
 * StringUtils.center("a", 4, 'y')    = "yayy"
</pre> *
 *
 * @param str     需要居中的字符串，可以为 null
 * @param size    新字符串的长度，负数按 0 处理
 * @param padChar 用于填充新字符串的字符
 * @return 居中后的字符串，若输入字符串为 null 则返回 null
 * @since 2.0
 */
@JvmOverloads
fun String?.center(size: Int, padChar: Char = ' '): String? {
    var str = this
    if (str == null || size <= 0) {
        return str
    }
    val strLen = str.length
    val pads = size - strLen
    if (pads <= 0) {
        return str
    }
    str = str.leftPad(strLen + pads / 2, padChar)
    str = str.rightPad(size, padChar)
    return str
}

/**
 *
 * 将字符串居中放置在长度为 `size` 的更大字符串中。
 * 使用指定的字符串作为填充字符串。
 *
 *
 * 如果目标长度小于字符串本身长度，则直接返回原字符串。
 * 若输入字符串为 `null`，则返回 `null`。
 * 负数长度按 0 处理。
 *
 * <pre>
 * StringUtils.center(null, *, *)     = null
 * StringUtils.center("", 4, " ")     = "    "
 * StringUtils.center("ab", -1, " ")  = "ab"
 * StringUtils.center("ab", 4, " ")   = " ab"
 * StringUtils.center("abcd", 2, " ") = "abcd"
 * StringUtils.center("a", 4, " ")    = " a  "
 * StringUtils.center("a", 4, "yz")   = "yayz"
 * StringUtils.center("abc", 7, null) = "  abc  "
 * StringUtils.center("abc", 7, "")   = "  abc  "
</pre> *
 *
 * @param str    需要居中的字符串，可以为 null
 * @param size   新字符串的长度，负数按 0 处理
 * @param padStr 用于填充新字符串的字符串，不能为 null 或空
 * @return 居中后的字符串，若输入字符串为 null 则返回 null
 * @throws IllegalArgumentException 如果 padStr 为 `null` 或空字符串
 */
fun String?.center(size: Int, padStr: String?): String? {
    var str = this
    var padStr = padStr
    if (str == null || size <= 0) {
        return str
    }
    if (padStr.isEmpty()) {
        padStr = " "
    }
    val strLen = str.length
    val pads = size - strLen
    if (pads <= 0) {
        return str
    }
    str = str.leftPad(strLen + pads / 2, padStr)
    str = str.rightPad(size, padStr)
    return str
}

// 大小写转换
//-----------------------------------------------------------------------
/**
 *
 * 按照 [String.toUpperCase] 将字符串转换为大写。
 *
 *
 * 若输入字符串为 `null`，则返回 `null`。
 *
 * <pre>
 * StringUtils.upperCase(null)  = null
 * StringUtils.upperCase("")    = ""
 * StringUtils.upperCase("aBc") = "ABC"
</pre> *
 *
 *
 * **注意**：根据 [String.toUpperCase] 文档说明，
 * 该方法的结果会受当前区域设置影响。
 * 如需实现与平台无关的大小写转换，应使用指定区域设置（例如 [Locale.ENGLISH]）的
 * [.lowerCase] 方法。
 *
 * @param str 需要转换为大写的字符串，可以为 null
 * @return 转换为大写后的字符串，若输入字符串为 null 则返回 null
 */
fun String?.upperCase(): String? {
    if (this == null) {
        return null
    }
    return this.uppercase(Locale.getDefault())
}

/**
 *
 * 按照 [String.toUpperCase] 将字符串转换为大写。
 *
 *
 * 若输入字符串为 `null`，则返回 `null`。
 *
 * <pre>
 * StringUtils.upperCase(null, Locale.ENGLISH)  = null
 * StringUtils.upperCase("", Locale.ENGLISH)    = ""
 * StringUtils.upperCase("aBc", Locale.ENGLISH) = "ABC"
</pre> *
 *
 * @param str    需要转换为大写的字符串，可以为 null
 * @param locale 定义大小写转换规则的区域设置，不能为 null
 * @return 转换为大写后的字符串，若输入字符串为 null 则返回 null
 * @since 2.5
 */
fun String?.upperCase(locale: Locale): String? {
    if (this == null) {
        return null
    }
    return this.uppercase(locale)
}

/**
 *
 * 按照 [String.toLowerCase] 将字符串转换为小写。
 *
 *
 * 若输入字符串为 `null`，则返回 `null`。
 *
 * <pre>
 * StringUtils.lowerCase(null)  = null
 * StringUtils.lowerCase("")    = ""
 * StringUtils.lowerCase("aBc") = "abc"
</pre> *
 *
 *
 * **注意**：根据 [String.toLowerCase] 文档说明，
 * 该方法的结果会受当前区域设置影响。
 * 如需实现与平台无关的大小写转换，应使用指定区域设置（例如 [Locale.ENGLISH]）的
 * [.lowerCase] 方法。
 *
 * @param str 需要转换为小写的字符串，可以为 null
 * @return 转换为小写后的字符串，若输入字符串为 null 则返回 null
 */
fun String?.lowerCase(): String? {
    if (this == null) {
        return null
    }
    return this.lowercase(Locale.getDefault())
}

/**
 *
 * 按照 [String.toLowerCase] 将字符串转换为小写。
 *
 *
 * 若输入字符串为 `null`，则返回 `null`。
 *
 * <pre>
 * StringUtils.lowerCase(null, Locale.ENGLISH)  = null
 * StringUtils.lowerCase("", Locale.ENGLISH)    = ""
 * StringUtils.lowerCase("aBc", Locale.ENGLISH) = "abc"
</pre> *
 *
 * @param str    需要转换为小写的字符串，可以为 null
 * @param locale 定义大小写转换规则的区域设置，不能为 null
 * @return 转换为小写后的字符串，若输入字符串为 null 则返回 null
 * @since 2.5
 */
fun String?.lowerCase(locale: Locale): String? {
    if (this == null) {
        return null
    }
    return this.lowercase(locale)
}

/**
 *
 * 首字母大写，将字符串第一个字符转换为标题格式
 * 遵循 [Character.toTitleCase] 规则。其余字符保持不变。
 *
 *
 * 基于单词的大写算法请参考 [WordUtils.capitalize]。
 * 若输入字符串为 `null`，则返回 `null`。
 *
 * <pre>
 * StringUtils.capitalize(null)  = null
 * StringUtils.capitalize("")    = ""
 * StringUtils.capitalize("cat") = "Cat"
 * StringUtils.capitalize("cAt") = "CAt"
</pre> *
 *
 * @param str 需要首字母大写的字符串，可以为 null
 * @return 首字母大写后的字符串，若输入字符串为 null 则返回 null
 * @see WordUtils.capitalize
 * @see .uncapitalize
 * @since 2.0
 */
fun String?.capitalize(): String? {
    var strLen: Int = 0
    if (this == null || (this.length.also { strLen = it }) == 0) {
        return this
    }
    return StringBuilder(strLen)
        .append(this.get(0).titlecaseChar())
        .append(this.substring(1))
        .toString()
}

/**
 *
 * 首字母大写，将字符串第一个字符转换为标题格式
 * 遵循 [Character.toTitleCase] 规则。其余字符保持不变。
 *
 * @param str 需要首字母大写的字符串，可以为 null
 * @return 首字母大写后的字符串，若输入字符串为 null 则返回 null
 * //@deprecated 请使用标准命名的 [.capitalize]。
 * 该方法将在 Commons Lang 3.0 版本中移除。
 */
fun String?.capitalise(): String? {
    return this.capitalize()
}

/**
 *
 * 首字母小写，将字符串第一个字符转换为小写
 * 遵循 [Character.toLowerCase] 规则。其余字符保持不变。
 *
 *
 * 基于单词的小写算法请参考 [WordUtils.uncapitalize]。
 * 若输入字符串为 `null`，则返回 `null`。
 *
 * <pre>
 * StringUtils.uncapitalize(null)  = null
 * StringUtils.uncapitalize("")    = ""
 * StringUtils.uncapitalize("Cat") = "cat"
 * StringUtils.uncapitalize("CAT") = "cAT"
</pre> *
 *
 * @param str 需要首字母小写的字符串，可以为 null
 * @return 首字母小写后的字符串，若输入字符串为 null 则返回 null
 * @see WordUtils.uncapitalize
 * @see .capitalize
 * @since 2.0
 */
fun String?.uncapitalize(): String? {
    var strLen = 0
    if (this == null || (this.length.also { strLen = it }) == 0) {
        return this
    }
    return StringBuilder(strLen)
        .append(this.get(0).lowercaseChar())
        .append(this.substring(1))
        .toString()
}

/**
 *
 * 首字母小写，将字符串第一个字符转换为小写
 * 遵循 [Character.toLowerCase] 规则。其余字符保持不变。
 *
 * @param str 需要首字母小写的字符串，可以为 null
 * @return 首字母小写后的字符串，若输入字符串为 null 则返回 null
 * //@deprecated 请使用标准命名的 [.uncapitalize]。
 * 该方法将在 Commons Lang 3.0 版本中移除。
 */
fun String?.uncapitalise(): String? {
    return this.uncapitalize()
}

/**
 *
 * 交换字符串的大小写：大写和标题格式转为小写，
 * 小写转为大写。
 *
 *
 *  * 大写字符转换为小写
 *  * 标题格式字符转换为小写
 *  * 小写字符转换为大写
 *
 *
 *
 * 基于单词的大小写交换算法请参考 [WordUtils.swapCase]。
 * 若输入字符串为 `null`，则返回 `null`。
 *
 * <pre>
 * StringUtils.swapCase(null)                 = null
 * StringUtils.swapCase("")                   = ""
 * StringUtils.swapCase("The dog has a BONE") = "tHE DOG HAS a bone"
</pre> *
 *
 *
 * 注意：该方法在 Lang 2.0 版本中发生了变更。
 * 不再执行基于单词的算法。
 * 如果你仅使用 ASCII 字符，则不会察觉到变化。
 * 相关功能可在 WordUtils 中找到。
 *
 * @param str 需要交换大小写的字符串，可以为 null
 * @return 大小写交换后的字符串，若输入字符串为 null 则返回 null
 */
fun String?.swapCase(): String? {
    var strLen = 0
    if (this == null || (this.length.also { strLen = it }) == 0) {
        return this
    }
    val buffer = StringBuilder(strLen)

    var ch = 0.toChar()
    for (i in 0..<strLen) {
        ch = this.get(i)
        if (Character.isUpperCase(ch)) {
            ch = ch.lowercaseChar()
        } else if (Character.isTitleCase(ch)) {
            ch = ch.lowercaseChar()
        } else if (Character.isLowerCase(ch)) {
            ch = ch.uppercaseChar()
        }
        buffer.append(ch)
    }
    return buffer.toString()
}

// 统计匹配次数
//-----------------------------------------------------------------------
/**
 *
 * 统计子字符串在原字符串中出现的次数。
 *
 *
 * 若原字符串或子字符串为 `null` 或空字符串（""），则返回 `0`。
 *
 * <pre>
 * StringUtils.countMatches(null, *)       = 0
 * StringUtils.countMatches("", *)         = 0
 * StringUtils.countMatches("abba", null)  = 0
 * StringUtils.countMatches("abba", "")    = 0
 * StringUtils.countMatches("abba", "a")   = 2
 * StringUtils.countMatches("abba", "ab")  = 1
 * StringUtils.countMatches("abba", "xxx") = 0
</pre> *
 *
 * @param str 待检查的原字符串，可以为 null
 * @param sub 待统计的子字符串，可以为 null
 * @return 出现的次数，若任一字符串为 `null` 则返回 0
 */
fun String?.countMatches(sub: String?): Int {
    if (this.isEmpty() || sub.isEmpty()) {
        return 0
    }
    var count = 0
    var idx = 0
    while ((this.indexOf(sub, idx).also { idx = it }) != INDEX_NOT_FOUND) {
        count++
        idx += sub!!.length
    }
    return count
}

// 字符校验
//-----------------------------------------------------------------------
/**
 *
 * 检查字符串是否仅包含 Unicode 字母。
 *
 *
 * `null` 返回 `false`。
 * 空字符串（length()=0）返回 `true`。
 *
 * <pre>
 * StringUtils.isAlpha(null)   = false
 * StringUtils.isAlpha("")     = true
 * StringUtils.isAlpha("  ")   = false
 * StringUtils.isAlpha("abc")  = true
 * StringUtils.isAlpha("ab2c") = false
 * StringUtils.isAlpha("ab-c") = false
</pre> *
 *
 * @param str 待检查的字符串，可以为 null
 * @return 若仅包含字母且非 null 则返回 `true`
 */
fun String?.isAlpha(): Boolean {
    if (this == null) {
        return false
    }
    val sz = this.length
    for (i in 0..<sz) {
        if (Character.isLetter(this.get(i)) == false) {
            return false
        }
    }
    return true
}

/**
 *
 * 检查字符串是否仅包含 Unicode 字母和空格（' '）。
 *
 *
 * `null` 返回 `false`
 * 空字符串（length()=0）返回 `true`。
 *
 * <pre>
 * StringUtils.isAlphaSpace(null)   = false
 * StringUtils.isAlphaSpace("")     = true
 * StringUtils.isAlphaSpace("  ")   = true
 * StringUtils.isAlphaSpace("abc")  = true
 * StringUtils.isAlphaSpace("ab c") = true
 * StringUtils.isAlphaSpace("ab2c") = false
 * StringUtils.isAlphaSpace("ab-c") = false
</pre> *
 *
 * @param str 待检查的字符串，可以为 null
 * @return 若仅包含字母和空格且非 null 则返回 `true`
 */
fun String?.isAlphaSpace(): Boolean {
    if (this == null) {
        return false
    }
    val sz = this.length
    for (i in 0..<sz) {
        if (!Character.isLetter(this[i]) && (this[i] != ' ')) {
            return false
        }
    }
    return true
}

/**
 *
 * 检查字符串是否仅包含 Unicode 字母或数字。
 *
 *
 * `null` 返回 `false`。
 * 空字符串（length()=0）返回 `true`。
 *
 * <pre>
 * StringUtils.isAlphanumeric(null)   = false
 * StringUtils.isAlphanumeric("")     = true
 * StringUtils.isAlphanumeric("  ")   = false
 * StringUtils.isAlphanumeric("abc")  = true
 * StringUtils.isAlphanumeric("ab c") = false
 * StringUtils.isAlphanumeric("ab2c") = true
 * StringUtils.isAlphanumeric("ab-c") = false
</pre> *
 *
 * @param str 待检查的字符串，可以为 null
 * @return 若仅包含字母或数字且非 null 则返回 `true`
 */
fun String?.isAlphanumeric(): Boolean {
    if (this == null) {
        return false
    }
    val sz = this.length
    for (i in 0..<sz) {
        if (Character.isLetterOrDigit(this.get(i)) == false) {
            return false
        }
    }
    return true
}

/**
 *
 * 检查字符串是否仅包含 Unicode 字母、数字或空格（' '）。
 *
 *
 * `null` 返回 `false`。
 * 空字符串（length()=0）返回 `true`。
 *
 * <pre>
 * StringUtils.isAlphanumeric(null)   = false
 * StringUtils.isAlphanumeric("")     = true
 * StringUtils.isAlphanumeric("  ")   = true
 * StringUtils.isAlphanumeric("abc")  = true
 * StringUtils.isAlphanumeric("ab c") = true
 * StringUtils.isAlphanumeric("ab2c") = true
 * StringUtils.isAlphanumeric("ab-c") = false
</pre> *
 *
 * @param str 待检查的字符串，可以为 null
 * @return 若仅包含字母、数字或空格且非 null 则返回 `true`
 */
fun String?.isAlphanumericSpace(): Boolean {
    if (this == null) {
        return false
    }
    val sz = this.length
    for (i in 0..<sz) {
        if (!Character.isLetterOrDigit(this[i]) && (this[i] != ' ')) {
            return false
        }
    }
    return true
}


/**
 *
 * 检查字符串是否仅包含 Unicode 数字。
 * 小数点不属于 Unicode 数字，会返回 false。
 *
 *
 * `null` 返回 `false`。
 * 空字符串（length()=0）返回 `true`。
 *
 * <pre>
 * StringUtils.isNumeric(null)   = false
 * StringUtils.isNumeric("")     = true
 * StringUtils.isNumeric("  ")   = false
 * StringUtils.isNumeric("123")  = true
 * StringUtils.isNumeric("12 3") = false
 * StringUtils.isNumeric("ab2c") = false
 * StringUtils.isNumeric("12-3") = false
 * StringUtils.isNumeric("12.3") = false
</pre> *
 *
 * @param str 待检查的字符串，可以为 null
 * @return 若仅包含数字且非 null 则返回 `true`
 */
fun String?.isNumeric(): Boolean {
    if (this == null) {
        return false
    }
    val sz = this.length
    for (i in 0..<sz) {
        if (!Character.isDigit(this[i])) {
            return false
        }
    }
    return true
}

/**
 *
 * 检查字符串是否仅包含 Unicode 数字或空格（' '）。
 * 小数点不属于 Unicode 数字，会返回 false。
 *
 *
 * `null` 返回 `false`。
 * 空字符串（length()=0）返回 `true`。
 *
 * <pre>
 * StringUtils.isNumeric(null)   = false
 * StringUtils.isNumeric("")     = true
 * StringUtils.isNumeric("  ")   = true
 * StringUtils.isNumeric("123")  = true
 * StringUtils.isNumeric("12 3") = true
 * StringUtils.isNumeric("ab2c") = false
 * StringUtils.isNumeric("12-3") = false
 * StringUtils.isNumeric("12.3") = false
</pre> *
 *
 * @param str 待检查的字符串，可以为 null
 * @return 若仅包含数字或空格且非 null 则返回 `true`
 */
fun String?.isNumericSpace(): Boolean {
    if (this == null) {
        return false
    }
    val sz = this.length
    for (i in 0..<sz) {
        if (!Character.isDigit(this[i]) && (this[i] != ' ')) {
            return false
        }
    }
    return true
}

/**
 *
 * 检查字符串是否仅包含空白字符。
 *
 *
 * `null` 返回 `false`。
 * 空字符串（length()=0）返回 `true`。
 *
 * <pre>
 * StringUtils.isWhitespace(null)   = false
 * StringUtils.isWhitespace("")     = true
 * StringUtils.isWhitespace("  ")   = true
 * StringUtils.isWhitespace("abc")  = false
 * StringUtils.isWhitespace("ab2c") = false
 * StringUtils.isWhitespace("ab-c") = false
</pre> *
 *
 * @param str 待检查的字符串，可以为 null
 * @return 若仅包含空白字符且非 null 则返回 `true`
 * @since 2.0
 */
fun String?.isWhitespace(): Boolean {
    if (this == null) {
        return false
    }
    val sz = this.length
    for (i in 0..<sz) {
        if ((!Character.isWhitespace(this[i]))) {
            return false
        }
    }
    return true
}

/**
 *
 * 检查字符串是否全部为小写字符。
 *
 *
 * `null` 返回 `false`。
 * 空字符串（length()=0）返回 `false`。
 *
 * <pre>
 * StringUtils.isAllLowerCase(null)   = false
 * StringUtils.isAllLowerCase("")     = false
 * StringUtils.isAllLowerCase("  ")   = false
 * StringUtils.isAllLowerCase("abc")  = true
 * StringUtils.isAllLowerCase("abC") = false
</pre> *
 *
 * @param str 待检查的字符串，可以为 null
 * @return 若全部为小写字符且非 null 则返回 `true`
 * @since 2.5
 */
fun String?.isAllLowerCase(): Boolean {
    if (this == null || this.isEmpty()) {
        return false
    }
    val sz = this.length
    for (i in 0..<sz) {
        if (!Character.isLowerCase(this[i])) {
            return false
        }
    }
    return true
}

/**
 *
 * 检查字符串是否全部为大写字符。
 *
 *
 * `null` 返回 `false`。
 * 空字符串（length()=0）返回 `false`。
 *
 * <pre>
 * StringUtils.isAllUpperCase(null)   = false
 * StringUtils.isAllUpperCase("")     = false
 * StringUtils.isAllUpperCase("  ")   = false
 * StringUtils.isAllUpperCase("ABC")  = true
 * StringUtils.isAllUpperCase("aBC") = false
</pre> *
 *
 * @param str 待检查的字符串，可以为 null
 * @return 若全部为大写字符且非 null 则返回 `true`
 * @since 2.5
 */
fun String?.isAllUpperCase(): Boolean {
    if (this.isNullOrEmpty()) {
        return false
    }
    val sz = this.length
    for (i in 0..<sz) {
        if (!Character.isUpperCase(this[i])) {
            return false
        }
    }
    return true
}

// 默认值处理
//-----------------------------------------------------------------------
/**
 *
 * 返回传入的字符串，
 * 若字符串为 `null`，则返回空字符串（""）。
 *
 * <pre>
 * StringUtils.defaultString(null)  = ""
 * StringUtils.defaultString("")    = ""
 * StringUtils.defaultString("bat") = "bat"
</pre> *
 *
 * @param str 待检查的字符串，可以为 null
 * @return 原字符串，若为 `null` 则返回空字符串
 * @see ObjectUtils.toString
 * @see String.valueOf
 */
fun String?.defaultString(): String {
    return this ?: EMPTY
}

/**
 *
 * 返回传入的字符串，若字符串为 `null`，
 * 则返回 `defaultStr` 的值。
 *
 * <pre>
 * StringUtils.defaultString(null, "NULL")  = "NULL"
 * StringUtils.defaultString("", "NULL")    = ""
 * StringUtils.defaultString("bat", "NULL") = "bat"
</pre> *
 *
 * @param str        待检查的字符串，可以为 null
 * @param defaultStr 输入为 `null` 时返回的默认字符串，可以为 null
 * @return 原字符串，若为 `null` 则返回默认字符串
 * @see ObjectUtils.toString
 * @see String.valueOf
 */
fun String?.defaultString(defaultStr: String?): String? {
    return this ?: defaultStr
}

/**
 *
 * 返回传入的字符串，若字符串为空白、空（""）或 `null`，
 * 则返回 `defaultStr` 的值。
 *
 * <pre>
 * StringUtils.defaultIfBlank(null, "NULL")  = "NULL"
 * StringUtils.defaultIfBlank("", "NULL")    = "NULL"
 * StringUtils.defaultIfBlank(" ", "NULL")   = "NULL"
 * StringUtils.defaultIfBlank("bat", "NULL") = "bat"
 * StringUtils.defaultIfBlank("", null)      = null
</pre> *
 *
 * @param str        待检查的字符串，可以为 null
 * @param defaultStr 输入为空白、空或 `null` 时返回的默认字符串，可以为 null
 * @return 原字符串，否则返回默认值
 * @see StringUtil.defaultString
 * @since 2.6
 */
fun String?.defaultIfBlank(defaultStr: String?): String? {
    return if (this.isBlank()) defaultStr else this
}

/**
 *
 * 返回传入的字符串，若字符串为空或 `null`，
 * 则返回 `defaultStr` 的值。
 *
 * <pre>
 * StringUtils.defaultIfEmpty(null, "NULL")  = "NULL"
 * StringUtils.defaultIfEmpty("", "NULL")    = "NULL"
 * StringUtils.defaultIfEmpty("bat", "NULL") = "bat"
 * StringUtils.defaultIfEmpty("", null)      = null
</pre> *
 *
 * @param str        待检查的字符串，可以为 null
 * @param defaultStr 输入为空（""）或 `null` 时返回的默认字符串，可以为 null
 * @return 原字符串，否则返回默认值
 * @see StringUtil.defaultString
 */
fun String?.defaultIfEmpty(defaultStr: String?): String? {
    return if (this.isEmpty()) defaultStr else this
}

// 反转
//-----------------------------------------------------------------------
/**
 *
 * 按照 [StrBuilder.reverse] 反转字符串。
 *
 *
 * 若字符串为 `null`，则返回 `null`。
 *
 * <pre>
 * StringUtils.reverse(null)  = null
 * StringUtils.reverse("")    = ""
 * StringUtils.reverse("bat") = "tab"
</pre> *
 *
 * @param str 需要反转的字符串，可以为 null
 * @return 反转后的字符串，若输入字符串为 null 则返回 null
 */
fun String?.reverse(): String? {
    if (this == null) {
        return null
    }
    return StringBuffer(this).reverse().toString()
}

// 缩略处理
//-----------------------------------------------------------------------
/**
 *
 * 使用省略号缩略字符串。会将
 * "Now is the time for all good men" 转换为 "Now is the time for..."
 *
 *
 * 具体规则：
 *
 *  * 如果 `str` 长度小于 `maxWidth`，直接返回原字符串。
 *  * 否则缩略为 `(substring(str, 0, max-3) + "...")`。
 *  * 如果 `maxWidth` 小于 4，抛出 `IllegalArgumentException`。
 *  * 任何情况下返回的字符串长度都不会大于 `maxWidth`。
 *
 *
 *
 * <pre>
 * StringUtils.abbreviate(null, *)      = null
 * StringUtils.abbreviate("", 4)        = ""
 * StringUtils.abbreviate("abcdefg", 6) = "abc..."
 * StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
 * StringUtils.abbreviate("abcdefg", 8) = "abcdefg"
 * StringUtils.abbreviate("abcdefg", 4) = "a..."
 * StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
</pre> *
 *
 * @param str      待检查的字符串，可以为 null
 * @param maxWidth 结果字符串的最大长度，必须至少为 4
 * @return 缩略后的字符串，若输入字符串为 null 则返回 null
 * @throws IllegalArgumentException 如果长度过小
 * @since 2.0
 */
fun String?.abbreviate(maxWidth: Int): String? {
    return this.abbreviate(0, maxWidth)
}

/**
 *
 * 使用省略号缩略字符串。会将
 * "Now is the time for all good men" 转换为 "...is the time for..."
 *
 *
 * 用法类似 `abbreviate(String, int)`，但允许指定左偏移量。
 * 注意：该左偏移量不一定是结果中的最左侧字符，
 * 也不一定是省略号后的第一个字符，但一定会出现在结果中。
 *
 *
 * 任何情况下返回的字符串长度都不会大于 `maxWidth`。
 *
 * <pre>
 * StringUtils.abbreviate(null, *, *)                = null
 * StringUtils.abbreviate("", 0, 4)                  = ""
 * StringUtils.abbreviate("abcdefghijklmno", -1, 10) = "abcdefg..."
 * StringUtils.abbreviate("abcdefghijklmno", 0, 10)  = "abcdefg..."
 * StringUtils.abbreviate("abcdefghijklmno", 1, 10)  = "abcdefg..."
 * StringUtils.abbreviate("abcdefghijklmno", 4, 10)  = "abcdefg..."
 * StringUtils.abbreviate("abcdefghijklmno", 5, 10)  = "...fghi..."
 * StringUtils.abbreviate("abcdefghijklmno", 6, 10)  = "...ghij..."
 * StringUtils.abbreviate("abcdefghijklmno", 8, 10)  = "...ijklmno"
 * StringUtils.abbreviate("abcdefghijklmno", 10, 10) = "...ijklmno"
 * StringUtils.abbreviate("abcdefghijklmno", 12, 10) = "...ijklmno"
 * StringUtils.abbreviate("abcdefghij", 0, 3)        = IllegalArgumentException
 * StringUtils.abbreviate("abcdefghij", 5, 6)        = IllegalArgumentException
</pre> *
 *
 * @param str      待检查的字符串，可以为 null
 * @param offset   源字符串的左边界偏移量
 * @param maxWidth 结果字符串的最大长度，必须至少为 4
 * @return 缩略后的字符串，若输入字符串为 null 则返回 null
 * @throws IllegalArgumentException 如果长度过小
 * @since 2.0
 */
fun String?.abbreviate(offset: Int, maxWidth: Int): String? {
    var offset = offset
    if (this == null) {
        return null
    }
    require(maxWidth >= 4) { "Minimum abbreviation width is 4" }
    if (this.length <= maxWidth) {
        return this
    }
    if (offset > this.length) {
        offset = this.length
    }
    if ((this.length - offset) < (maxWidth - 3)) {
        offset = this.length - (maxWidth - 3)
    }
    if (offset <= 4) {
        return this.substring(0, maxWidth - 3) + "..."
    }
    require(maxWidth >= 7) { "Minimum abbreviation width with offset is 7" }
    if ((offset + (maxWidth - 3)) < this.length) {
        return "..." + this.substring(offset).abbreviate(maxWidth - 3)
    }
    return "..." + this.substring(this.length - (maxWidth - 3))
}

/**
 *
 * 将字符串缩略到指定长度，并用提供的替换字符串替换中间字符。
 *
 *
 * 仅在满足以下条件时执行缩略：
 *
 *  * 待缩略字符串和替换字符串都不为 null 且非空
 *  * 目标截断长度小于原字符串长度
 *  * 目标截断长度大于 0
 *  * 缩略后的字符串能容纳替换字符串长度 + 原字符串首尾各一个字符
 *
 * 否则返回原字符串。
 *
 *
 * <pre>
 * StringUtils.abbreviateMiddle(null, null, 0)      = null
 * StringUtils.abbreviateMiddle("abc", null, 0)      = "abc"
 * StringUtils.abbreviateMiddle("abc", ".", 0)      = "abc"
 * StringUtils.abbreviateMiddle("abc", ".", 3)      = "abc"
 * StringUtils.abbreviateMiddle("abcdef", ".", 4)     = "ab.f"
</pre> *
 *
 * @param str    待缩略的字符串，可以为 null
 * @param middle 用于替换中间字符的字符串，可以为 null
 * @param length 要将 `str` 缩略到的目标长度
 * @return 满足条件则返回缩略字符串，否则返回原字符串
 * @since 2.5
 */
fun String?.abbreviateMiddle(middle: String?, length: Int): String? {
    if (this.isEmpty() || middle.isEmpty()) {
        return this
    }

    if (length >= this!!.length || length < (middle!!.length + 2)) {
        return this
    }

    val targetSting = length - middle.length
    val startOffset = targetSting / 2 + targetSting % 2
    val endOffset = this.length - targetSting / 2

    val builder = StringBuilder(length)
    builder.append(this.substring(0, startOffset))
    builder.append(middle)
    builder.append(this.substring(endOffset))

    return builder.toString()
}

// 差异对比
//-----------------------------------------------------------------------
/**
 *
 * 比较两个字符串，返回它们不同的部分。
 * （更准确地说，返回第二个字符串从第一个不同位置开始的剩余部分。）
 *
 *
 * 例如：
 * `difference("i am a machine", "i am a robot") -> "robot"`。
 *
 * <pre>
 * StringUtils.difference(null, null) = null
 * StringUtils.difference("", "") = ""
 * StringUtils.difference("", "abc") = "abc"
 * StringUtils.difference("abc", "") = ""
 * StringUtils.difference("abc", "abc") = ""
 * StringUtils.difference("ab", "abxyz") = "xyz"
 * StringUtils.difference("abcde", "abxyz") = "xyz"
 * StringUtils.difference("abcde", "xyz") = "xyz"
</pre> *
 *
 * @param str1 第一个字符串，可以为 null
 * @param str2 第二个字符串，可以为 null
 * @return 第二个字符串中与第一个不同的部分；若相等则返回空字符串
 * @since 2.0
 */
fun String?.difference(str2: String?): String? {
    if (this == null) {
        return str2
    }
    if (str2 == null) {
        return this
    }
    val at = this.indexOfDifference(str2)
    if (at == INDEX_NOT_FOUND) {
        return EMPTY
    }
    return str2.substring(at)
}

/**
 *
 * 比较两个字符串，返回它们开始出现不同的索引位置。
 *
 *
 * 例如：
 * `indexOfDifference("i am a machine", "i am a robot") -> 7`
 *
 * <pre>
 * StringUtils.indexOfDifference(null, null) = -1
 * StringUtils.indexOfDifference("", "") = -1
 * StringUtils.indexOfDifference("", "abc") = 0
 * StringUtils.indexOfDifference("abc", "") = 0
 * StringUtils.indexOfDifference("abc", "abc") = -1
 * StringUtils.indexOfDifference("ab", "abxyz") = 2
 * StringUtils.indexOfDifference("abcde", "abxyz") = 2
 * StringUtils.indexOfDifference("abcde", "xyz") = 0
</pre> *
 *
 * @param str1 第一个字符串，可以为 null
 * @param str2 第二个字符串，可以为 null
 * @return 两个字符串开始不同的索引；若相等则返回 -1
 * @since 2.0
 */
fun String?.indexOfDifference(str2: String?): Int {
    if (this === str2) {
        return INDEX_NOT_FOUND
    }
    if (this == null || str2 == null) {
        return 0
    }
    var i = 0
    while (i < this.length && i < str2.length) {
        if (this[i] != str2[i]) {
            break
        }
        ++i
    }
    if (i < str2.length || i < this.length) {
        return i
    }
    return INDEX_NOT_FOUND
}

/**
 *
 * 比较字符串数组中的所有字符串，返回它们开始出现不同的索引位置。
 *
 *
 * 例如：
 * `indexOfDifference(arrayOf("i am a machine", "i am a robot")) -> 7`
 *
 * <pre>
 * StringUtils.indexOfDifference(null) = -1
 * StringUtils.indexOfDifference(arrayOf()) = -1
 * StringUtils.indexOfDifference(arrayOf("abc")) = -1
 * StringUtils.indexOfDifference(arrayOf(null, null)) = -1
 * StringUtils.indexOfDifference(arrayOf("", "")) = -1
 * StringUtils.indexOfDifference(arrayOf("", null)) = 0
 * StringUtils.indexOfDifference(arrayOf("abc", null, null)) = 0
 * StringUtils.indexOfDifference(arrayOf(null, null, "abc")) = 0
 * StringUtils.indexOfDifference(arrayOf("", "abc")) = 0
 * StringUtils.indexOfDifference(arrayOf("abc", "")) = 0
 * StringUtils.indexOfDifference(arrayOf("abc", "abc")) = -1
 * StringUtils.indexOfDifference(arrayOf("abc", "a")) = 1
 * StringUtils.indexOfDifference(arrayOf("ab", "abxyz")) = 2
 * StringUtils.indexOfDifference(arrayOf("abcde", "abxyz")) = 2
 * StringUtils.indexOfDifference(arrayOf("abcde", "xyz")) = 0
 * StringUtils.indexOfDifference(arrayOf("xyz", "abcde")) = 0
 * StringUtils.indexOfDifference(arrayOf("i am a machine", "i am a robot")) = 7
</pre> *
 *
 * @param strs 字符串数组，元素可以为 null
 * @return 字符串开始不同的索引；若全部相等则返回 -1
 * @since 2.4
 */
fun Array<String>?.indexOfDifference(): Int {
    if (this == null || this.size <= 1) {
        return INDEX_NOT_FOUND
    }
    var anyStringNull = false
    var allStringsNull = true
    val arrayLen = this.size
    var shortestStrLen = Int.Companion.MAX_VALUE
    var longestStrLen = 0

    // 找出最短和最长字符串长度；避免在循环中每次检查是否超出字符串长度
    for (i in 0..<arrayLen) {
        if (this[i] == null) {
            anyStringNull = true
            shortestStrLen = 0
        } else {
            allStringsNull = false
            shortestStrLen = min(this[i].length, shortestStrLen)
            longestStrLen = max(this[i].length, longestStrLen)
        }
    }

    // 处理全为 null 或全为空字符串的数组
    if (allStringsNull || (longestStrLen == 0 && !anyStringNull)) {
        return INDEX_NOT_FOUND
    }

    // 处理包含 null 或空字符串的数组
    if (shortestStrLen == 0) {
        return 0
    }

    // 查找所有字符串首次出现不同的位置
    var firstDiff = -1
    for (stringPos in 0..<shortestStrLen) {
        val comparisonChar = this[0].get(stringPos)
        for (arrayPos in 1..<arrayLen) {
            if (this[arrayPos].get(stringPos) != comparisonChar) {
                firstDiff = stringPos
                break
            }
        }
        if (firstDiff != -1) {
            break
        }
    }

    if (firstDiff == -1 && shortestStrLen != longestStrLen) {
        // 对比完最短字符串的所有字符都未发现不同，但字符串长度不同
        // 因此返回最短字符串的长度
        return shortestStrLen
    }
    return firstDiff
}

/**
 *
 * 比较字符串数组中的所有字符串，返回它们共有的前缀字符序列。
 *
 *
 * 例如：
 * `getCommonPrefix(arrayOf("i am a machine", "i am a robot")) -> "i am a "`
 *
 * <pre>
 * StringUtils.getCommonPrefix(null) = ""
 * StringUtils.getCommonPrefix(arrayOf()) = ""
 * StringUtils.getCommonPrefix(arrayOf("abc")) = "abc"
 * StringUtils.getCommonPrefix(arrayOf(null, null)) = ""
 * StringUtils.getCommonPrefix(arrayOf("", "")) = ""
 * StringUtils.getCommonPrefix(arrayOf("", null)) = ""
 * StringUtils.getCommonPrefix(arrayOf("abc", null, null)) = ""
 * StringUtils.getCommonPrefix(arrayOf(null, null, "abc")) = ""
 * StringUtils.getCommonPrefix(arrayOf("", "abc")) = ""
 * StringUtils.getCommonPrefix(arrayOf("abc", "")) = ""
 * StringUtils.getCommonPrefix(arrayOf("abc", "abc")) = "abc"
 * StringUtils.getCommonPrefix(arrayOf("abc", "a")) = "a"
 * StringUtils.getCommonPrefix(arrayOf("ab", "abxyz")) = "ab"
 * StringUtils.getCommonPrefix(arrayOf("abcde", "abxyz")) = "ab"
 * StringUtils.getCommonPrefix(arrayOf("abcde", "xyz")) = ""
 * StringUtils.getCommonPrefix(arrayOf("xyz", "abcde")) = ""
 * StringUtils.getCommonPrefix(arrayOf("i am a machine", "i am a robot")) = "i am a "
</pre> *
 *
 * @param strs 字符串对象数组，元素可以为 null
 * @return 所有字符串共有的初始字符序列；若数组为 null、元素全为 null 或无公共前缀则返回空字符串
 * @since 2.4
 */
fun Array<String>?.getCommonPrefix(): String {
    if (this.isNullOrEmpty()) {
        return EMPTY
    }
    val smallestIndexOfDiff = this.indexOfDifference()
    if (smallestIndexOfDiff == INDEX_NOT_FOUND) {
        // 所有字符串完全相同
        if (this[0] == null) {
            return EMPTY
        }
        return this[0]
    } else if (smallestIndexOfDiff == 0) {
        // 无公共初始字符
        return EMPTY
    } else {
        // 找到公共初始字符序列
        return this[0].substring(0, smallestIndexOfDiff)!!
    }
}

// 杂项
//-----------------------------------------------------------------------
/**
 *
 * 计算两个字符串之间的莱文斯坦距离。
 *
 *
 * 这是将一个字符串转换为另一个字符串所需的最少单字符修改次数
 * （删除、插入或替换）。
 *
 *
 * 此莱文斯坦距离算法的旧版本实现来自
 * [http://www.merriampark.com/ld.htm](http://www.merriampark.com/ld.htm)
 *
 *
 * Chas Emerick 用 Java 编写了一版实现，避免了在处理超大字符串时
 * 可能出现的内存溢出问题。<br></br>
 * 此版本莱文斯坦距离算法实现来自
 * [http://www.merriampark.com/ldjava.htm](http://www.merriampark.com/ldjava.htm)
 *
 * <pre>
 * StringUtils.getLevenshteinDistance(null, *)             = IllegalArgumentException
 * StringUtils.getLevenshteinDistance(*, null)             = IllegalArgumentException
 * StringUtils.getLevenshteinDistance("","")               = 0
 * StringUtils.getLevenshteinDistance("","a")              = 1
 * StringUtils.getLevenshteinDistance("aaapppp", "")       = 7
 * StringUtils.getLevenshteinDistance("frog", "fog")       = 1
 * StringUtils.getLevenshteinDistance("fly", "ant")        = 3
 * StringUtils.getLevenshteinDistance("elephant", "hippo") = 7
 * StringUtils.getLevenshteinDistance("hippo", "elephant") = 7
 * StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
 * StringUtils.getLevenshteinDistance("hello", "hallo")    = 1
</pre> *
 *
 * @param s 第一个字符串，不能为 null
 * @param t 第二个字符串，不能为 null
 * @return 计算出的距离值
 * @throws IllegalArgumentException 如果任一字符串输入为 `null`
 */
fun String.getLevenshteinDistance(t: String): Int {
    var s = this
    var t = t
    require(!(s == null || t == null)) { "Strings must not be null" }

    /*
           此实现与之前的区别在于，不再创建并保留大小为 s.length()+1 × t.length()+1 的矩阵，
           而是维护两个长度为 s.length()+1 的一维数组。第一个 d 是“当前工作”距离数组，
           在遍历字符串 s 的字符时保存最新的距离开销。每次递增要比较的字符串 t 的索引，
           就将 d 复制到第二个 int[] p 中。这样可以保留算法所需的前一次开销
           （取当前开销左侧、上方、左上方开销的最小值）。
           （注意：数组实际上不再复制，只是交换……这显然比每次外循环克隆数组
           或执行 System.arraycopy() 好得多。）
           实际上，两个实现的区别在于：本版本在计算两个超大字符串的莱文斯坦距离时
           不会导致内存溢出。
         */
    var n = s.length // s 的长度
    var m = t.length // t 的长度

    if (n == 0) {
        return m
    } else if (m == 0) {
        return n
    }

    if (n > m) {
        // 交换输入字符串以减少内存消耗
        val tmp = s
        s = t
        t = tmp
        n = m
        m = t.length
    }

    var p: IntArray? = IntArray(n + 1) // 前一行开销数组，水平方向
    var d: IntArray? = IntArray(n + 1) // 当前开销数组，水平方向
    var _d: IntArray? // 用于交换 p 和 d 的占位数组

    // 字符串 s 和 t 的索引
    var i: Int // 遍历 s
    var j: Int // 遍历 t

    var t_j: Char // t 的第 j 个字符

    var cost: Int // 开销

    i = 0
    while (i <= n) {
        p!![i] = i
        i++
    }

    j = 1
    while (j <= m) {
        t_j = t.get(j - 1)
        d!![0] = j

        i = 1
        while (i <= n) {
            cost = if (s.get(i - 1) == t_j) 0 else 1
            // 取左侧+1、上方+1、左上方+开销 三者的最小值
            d[i] = min(min(d[i - 1] + 1, p!![i] + 1), p[i - 1] + cost)
            i++
        }

        // 将当前距离计数复制到“前一行”距离计数
        _d = p
        p = d
        d = _d
        j++
    }

    // 上述循环最后一步是交换 d 和 p，所以 p 现在保存最新的开销计数
    return p!![n]
}

// 前缀判断
//-----------------------------------------------------------------------
/**
 *
 * 检查字符串是否以指定前缀开头。
 *
 *
 * 安全处理 `null`，不会抛出异常。两个 `null` 引用视为相等。
 * 比较区分大小写。
 *
 * <pre>
 * StringUtils.startsWith(null, null)      = true
 * StringUtils.startsWith(null, "abc")     = false
 * StringUtils.startsWith("abcdef", null)  = false
 * StringUtils.startsWith("abcdef", "abc") = true
 * StringUtils.startsWith("ABCDEF", "abc") = false
</pre> *
 *
 * @param str    要检查的字符串，可以为 null
 * @param prefix 要查找的前缀，可以为 null
 * @return `true` 表示字符串以前缀开头（区分大小写），或两者都为 `null`
 * @see java.lang.String.startsWith
 * @since 2.4
 */
fun String?.startsWith(prefix: String?): Boolean {
    return startsWith(this, prefix, false)
}

/**
 *
 * 不区分大小写检查字符串是否以指定前缀开头。
 *
 *
 * 安全处理 `null`，不会抛出异常。两个 `null` 引用视为相等。
 * 比较不区分大小写。
 *
 * <pre>
 * StringUtils.startsWithIgnoreCase(null, null)      = true
 * StringUtils.startsWithIgnoreCase(null, "abc")     = false
 * StringUtils.startsWithIgnoreCase("abcdef", null)  = false
 * StringUtils.startsWithIgnoreCase("abcdef", "abc") = true
 * StringUtils.startsWithIgnoreCase("ABCDEF", "abc") = true
</pre> *
 *
 * @param str    要检查的字符串，可以为 null
 * @param prefix 要查找的前缀，可以为 null
 * @return `true` 表示字符串以前缀开头（不区分大小写），或两者都为 `null`
 * @see java.lang.String.startsWith
 * @since 2.4
 */
fun String?.startsWithIgnoreCase(prefix: String?): Boolean {
    return startsWith(this, prefix, true)
}

/**
 *
 * 检查字符串是否以指定前缀开头（可选择不区分大小写）。
 *
 * @param str        要检查的字符串，可以为 null
 * @param prefix     要查找的前缀，可以为 null
 * @param ignoreCase 是否忽略大小写
 * @return `true` 表示字符串以前缀开头，或两者都为 `null`
 * @see java.lang.String.startsWith
 */
private fun startsWith(str: String?, prefix: String?, ignoreCase: Boolean): Boolean {
    if (str == null || prefix == null) {
        return (str == null && prefix == null)
    }
    if (prefix.length > str.length) {
        return false
    }
    return str.regionMatches(0, prefix, 0, prefix.length, ignoreCase = ignoreCase)
}

/**
 *
 * 检查字符串是否以指定字符串数组中的任意一个开头。
 *
 * <pre>
 * StringUtils.startsWithAny(null, null)      = false
 * StringUtils.startsWithAny(null, new String[] {"abc"})  = false
 * StringUtils.startsWithAny("abcxyz", null)     = false
 * StringUtils.startsWithAny("abcxyz", new String[] {""}) = false
 * StringUtils.startsWithAny("abcxyz", new String[] {"abc"}) = true
 * StringUtils.startsWithAny("abcxyz", new String[] {null, "xyz", "abc"}) = true
</pre> *
 *
 * @param string        要检查的字符串，可以为 null
 * @param searchStrings 要查找的字符串数组，可以为 null 或空
 * @return `true` 表示字符串以任一前缀开头（不区分大小写），或两者都为 `null`
 * @see .startsWith
 * @since 2.5
 */
fun String?.startsWithAny(searchStrings: Array<String?>?): Boolean {
    if (this.isEmpty() || searchStrings.isEmpty()) {
        return false
    }
    for (i in searchStrings!!.indices) {
        val searchString = searchStrings[i]
        if (this.startsWith(searchString)) {
            return true
        }
    }
    return false
}

/**
 *
 * 检查字符串是否以指定字符串数组中的任意一个开头。
 *
 * <pre>
 * StringUtils.startsWithAny(null, null)      = false
 * StringUtils.startsWithAny(null, new String[] {"abc"})  = false
 * StringUtils.startsWithAny("abcxyz", null)     = false
 * StringUtils.startsWithAny("abcxyz", "") = false
 * StringUtils.startsWithAny("abcxyz", "abc") = true
 * StringUtils.startsWithAny("abcxyz", null, "xyz", "abc") = true
</pre> *
 *
 * @param string        要检查的字符串，可以为 null
 * @param searchStrings 要查找的字符串数组，可以为 null 或空
 * @return `true` 表示字符串以任一前缀开头（不区分大小写），或两者都为 `null`
 * @see .startsWith
 * @since 2.5
 */
fun String?.startsWithAnyX(vararg searchStrings: String): Boolean {
    if (this.isEmpty() || searchStrings.isEmpty()) {
        return false
    }
    for (i in searchStrings.indices) {
        val searchString = searchStrings[i]
        if (this.startsWith(searchString)) {
            return true
        }
    }
    return false
}

// 后缀判断
//-----------------------------------------------------------------------
/**
 *
 * 检查字符串是否以指定后缀结尾。
 *
 *
 * 安全处理 `null`，不会抛出异常。两个 `null` 引用视为相等。
 * 比较区分大小写。
 *
 * <pre>
 * StringUtils.endsWith(null, null)      = true
 * StringUtils.endsWith(null, "def")     = false
 * StringUtils.endsWith("abcdef", null)  = false
 * StringUtils.endsWith("abcdef", "def") = true
 * StringUtils.endsWith("ABCDEF", "def") = false
 * StringUtils.endsWith("ABCDEF", "cde") = false
</pre> *
 *
 * @param str    要检查的字符串，可以为 null
 * @param suffix 要查找的后缀，可以为 null
 * @return `true` 表示字符串以后缀结尾（区分大小写），或两者都为 `null`
 * @see java.lang.String.endsWith
 * @since 2.4
 */
fun String?.endsWith(suffix: String?): Boolean {
    return endsWith(this, suffix, false)
}

/**
 *
 * 不区分大小写检查字符串是否以指定后缀结尾。
 *
 *
 * 安全处理 `null`，不会抛出异常。两个 `null` 引用视为相等。
 * 比较不区分大小写。
 *
 * <pre>
 * StringUtils.endsWithIgnoreCase(null, null)      = true
 * StringUtils.endsWithIgnoreCase(null, "def")     = false
 * StringUtils.endsWithIgnoreCase("abcdef", null)  = false
 * StringUtils.endsWithIgnoreCase("abcdef", "def") = true
 * StringUtils.endsWithIgnoreCase("ABCDEF", "def") = true
 * StringUtils.endsWithIgnoreCase("ABCDEF", "cde") = false
</pre> *
 *
 * @param str    要检查的字符串，可以为 null
 * @param suffix 要查找的后缀，可以为 null
 * @return `true` 表示字符串以后缀结尾（不区分大小写），或两者都为 `null`
 * @see java.lang.String.endsWith
 * @since 2.4
 */
fun String?.endsWithIgnoreCase(suffix: String?): Boolean {
    return endsWith(this, suffix, true)
}

/**
 *
 * 检查字符串是否以指定后缀结尾（可选择不区分大小写）。
 *
 * @param str        要检查的字符串，可以为 null
 * @param suffix     要查找的后缀，可以为 null
 * @param ignoreCase 是否忽略大小写
 * @return `true` 表示字符串以后缀开头，或两者都为 `null`
 * @see java.lang.String.endsWith
 */
private fun endsWith(str: String?, suffix: String?, ignoreCase: Boolean): Boolean {
    if (str == null || suffix == null) {
        return (str == null && suffix == null)
    }
    if (suffix.length > str.length) {
        return false
    }
    val strOffset = str.length - suffix.length
    return str.regionMatches(strOffset, suffix, 0, suffix.length, ignoreCase = ignoreCase)
}

/**
 *
 *
 * 类似于 [http://www.w3.org/TR/xpath/#function-normalize
 * -space](http://www.w3.org/TR/xpath/#function-normalize-space)
 *
 *
 *
 * 该函数返回规范化空白字符后的字符串：使用 `[.trim]` 去除首尾空白，
 * 然后将连续的空白字符替换为单个空格。
 *
 * 在 XML 中，空白字符与 [S](http://www.w3.org/TR/REC-xml/#NT-S) 规则一致，
 * 即 S ::= (#x20 | #x9 | #xD | #xA)+
 *
 *
 * 参考 Java 的 [Character.isWhitespace] 判定空白字符。
 *
 *
 * 区别在于：Java 的空白字符包含垂直制表符和换页符，本方法也会对其规范化。
 * 此外 `[.trim]` 会移除字符串两端的控制字符（char <= 32）。
 *
 *
 * @param str 要规范化空白的源字符串，可以为 null
 * @return 空白规范化后的字符串，如果输入为 null 则返回 `null`
 * @see Character.isWhitespace
 * @see .trim
 * @see <ahref></ahref>="http://www.w3.org/TR/xpath/.function-normalize-space">
 * http://www.w3.org/TR/xpath/.function-normalize-space
 *
 * @since 2.6
 */
fun String?.normalizeSpace(): String? {
    var str = this
    str = strip(str)
    if (str == null || str.length <= 2) {
        return str
    }
    val b = StringBuilder(str.length)
    for (i in 0..<str.length) {
        val c = str.get(i)
        if (Character.isWhitespace(c)) {
            if (i > 0 && !Character.isWhitespace(str.get(i - 1))) {
                b.append(' ')
            }
        } else {
            b.append(c)
        }
    }
    return b.toString()
}

/**
 *
 * 检查字符串是否以指定字符串数组中的任意一个结尾。
 *
 * <pre>
 * StringUtils.endsWithAny(null, null)      = false
 * StringUtils.endsWithAny(null, new String[] {"abc"})  = false
 * StringUtils.endsWithAny("abcxyz", null)     = false
 * StringUtils.endsWithAny("abcxyz", new String[] {""}) = true
 * StringUtils.endsWithAny("abcxyz", new String[] {"xyz"}) = true
 * StringUtils.endsWithAny("abcxyz", new String[] {null, "xyz", "abc"}) = true
</pre> *
 *
 * @param string        要检查的字符串，可以为 null
 * @param searchStrings 要查找的字符串数组，可以为 null 或空
 * @return `true` 表示字符串以任一前缀结尾（不区分大小写），或两者都为 `null`
 * @since 2.6
 */
fun String?.endsWithAny(searchStrings: Array<String?>?): Boolean {
    if (this.isEmpty() || searchStrings.isEmpty()) {
        return false
    }
    for (i in searchStrings!!.indices) {
        val searchString = searchStrings[i]
        if (this.endsWith(searchString)) {
            return true
        }
    }
    return false
}

fun String?.getBytes(): ByteArray? {
    return this.getBytes(StandardCharsets.UTF_8)
}

fun String?.getBytes(encoding: String?): ByteArray? {
    if (null != this && null != encoding && encoding.length != 0) {
        val charset = Charset.forName(encoding)
        return this.getBytes(charset)
    } else {
        return null
    }
}

fun String?.getBytes(charset: Charset?): ByteArray? {
    if (null != this && null != charset) {
        if (0 == this.length) {
            return byteArrayOf()
        } else {
            var bytes: ByteArray? = null

            try {
                bytes = this.toByteArray(charset)
            } catch (var4: Throwable) {
                var4.printStackTrace()
            }

            return bytes
        }
    } else {
        return null
    }
}


fun ByteArray?.getString(): String? {
    return this.getString(StandardCharsets.UTF_8)
}

fun ByteArray?.getString(encoding: String?): String? {
    if (null != this && null != encoding && encoding.length != 0) {
        val charset = Charset.forName(encoding)
        return this.getString(charset)
    } else {
        return null
    }
}

fun ByteArray?.getString(charset: Charset?): String? {
    if (null != this && null != charset) {
        if (this.size == 0) {
            return ""
        } else {
            var str: String? = null

            try {
                str = String(this, charset)
            } catch (var4: Throwable) {
                var4.printStackTrace()
            }

            return str
        }
    } else {
        return null
    }
}

/**
 * 调用Android原生split
 *
 * @param str   拆分原字符串
 * @param regex 拆分正则表达式
 * @param limit 结果最大长度
 * @return 拆分结果数组
 */
//----------------------------------------------------------------------------------------------
/**
 * 调用Android原生split
 *
 * @param str   拆分原字符串
 * @param regex 拆分正则表达式
 * @return 拆分结果数组
 */
@JvmOverloads
fun String?.splits(regex: String?, limit: Int = 0): Array<String?>? {
    var str = this
    var regex = regex
    if (str == null) {
        return null
    }
    val len = str.length
    if (len == 0) {
        return arrayOf<String?>()
    }

    if (regex == null) {
        regex = " "
        str = str.replace("\\s+".toRegex(), " ")
    }

    return str.split(regex.toRegex(), limit.coerceAtLeast(0)).toTypedArray()
}

/**
 * 拼接成字符串
 */
fun Array<out Any?>.concat(): String {
    val sb = StringBuilder()
    for (item in this) {
        sb.append(item.toString(""))
    }
    return sb.toString()
}

/**
 * 拼接成字符串
 *
 * @param objects   拼接对象列表
 * @param splitText 分隔字符
 */
fun Array<Any?>?.concatBySplit(splitText: String?): String? {
    if (this == null) {
        return null
    }
    val stringBuilder = StringBuilder()
    for (item in this) {
        stringBuilder.append(item.toString("")).append(splitText)
    }
    return if (stringBuilder.length > 0 && splitText.isNotEmpty()) {
        stringBuilder.substring(0, stringBuilder.length - 1)
    } else {
        stringBuilder.toString()
    }
}

/**
 * 拼接成字符串
 *
 * @param objects   拼接对象列表
 * @param splitText 分隔字符
 */
fun MutableList<String?>?.concatBySplit(splitText: String?): String? {
    if (this == null) {
        return null
    }
    return this.toTypedArray().map { it as Any? }.toTypedArray().concatBySplit(splitText)
}

/**
 * 比较两个value是否相同，（都为null的时视为不相同）
 *
 * @param value1 值1
 * @param value2 值2
 * @return 是否相同
 */
fun String?.eqVal(value2: String?): Boolean {
    return this != null && this == value2
}

/**
 * 忽略大小写比较两个value是否相同，（都为null的时视为不相同）
 *
 * @param value1 值1
 * @param value2 值2
 * @return 是否相同
 */
fun String?.eqValIgnoreCase(value2: String?): Boolean {
    return this != null && this.equals(value2, ignoreCase = true)
}

/**
 * 字符格式化显示
 *
 * @param format 格式
 * @param args   参数组
 */
fun format(format: String, vararg args: Any?): String {
    return formatNull(format, "", *args)
}

/**
 * 字符格式化显示
 *
 * @param format      格式
 * @param placeHolder 参数为null时的占位符
 * @param args        参数组
 */
fun formatNull(format: String, placeHolder: String?, vararg args: Any?): String {
    val newArgs = args.map { it ?: placeHolder }.toTypedArray()
    return String.format(format, *newArgs)
}

/**
 * 字符格式化显示
 *
 * @param format      格式
 * @param placeHolder 参数为Empty时的占位符
 * @param args        参数组
 */
fun formatEmpty(format: String, placeHolder: String?, vararg args: Any?): String {
    val newArgs = args.map {
        if (it.isEmpty()) {
            placeHolder
        } else {
            it
        }
    }.toTypedArray()

    return String.format(format, *newArgs)
}

/**
 * 字符格式化显示
 *
 * @param locale 区域
 * @param format 格式
 * @param args   参数组
 */
fun format(locale: Locale?, format: String, vararg args: Any?): String {
    return formatNull(locale, format, "", *args)
}

/**
 * 字符格式化显示
 *
 * @param locale      区域
 * @param format      格式
 * @param placeHolder 参数为null时的占位符
 * @param args        参数组
 */
fun formatNull(
    locale: Locale?,
    format: String,
    placeHolder: String?,
    vararg args: Any?
): String {
    val newArgs = args.map { it ?: placeHolder }.toTypedArray()
    return String.format(locale, format, *newArgs)
}

/**
 * 字符格式化显示
 *
 * @param locale      区域
 * @param format      格式
 * @param placeHolder 参数为Empty时的占位符
 * @param args        参数组
 */
fun formatEmpty(
    locale: Locale?,
    format: String,
    placeHolder: String?,
    vararg args: Any?
): String {
    val newArgs = args.map {
        if (it.isEmpty()) {
            placeHolder
        } else {
            it
        }
    }.toTypedArray()
    return String.format(locale, format, *newArgs)
}