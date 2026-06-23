@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")

package com.richard.library.context.util

import android.annotation.SuppressLint
import android.os.Build
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import android.util.SparseLongArray
import androidx.annotation.RequiresApi
import androidx.collection.ArrayMap
import androidx.collection.LongSparseArray
import androidx.collection.SimpleArrayMap
import androidx.core.util.size
import org.jetbrains.annotations.Contract
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.lang.reflect.Array
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Return whether object is empty.
 *
 * @param obj The object.
 * @return `true`: yes<br></br>`false`: no
 */
@SuppressLint("ObsoleteSdkInt")
fun Any?.isEmpty(): Boolean {
    if (this == null) {
        return true
    }
    if (this.javaClass.isArray && Array.getLength(this) == 0) {
        return true
    }
    if (this is CharSequence && this.toString().length == 0) {
        return true
    }
    if (this is Collection<*> && this.isEmpty()) {
        return true
    }
    if (this is MutableMap<*, *> && this.isEmpty()) {
        return true
    }
    if (this is SimpleArrayMap<*, *> && this.isEmpty()) {
        return true
    }
    if (this is SparseArray<*> && this.size == 0) {
        return true
    }
    if (this is SparseBooleanArray && this.size == 0) {
        return true
    }
    if (this is SparseIntArray && this.size == 0) {
        return true
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        if (this is SparseLongArray && this.size == 0) {
            return true
        }
    }
    if (this is LongSparseArray<*> && this.size() == 0) {
        return true
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        if (this is android.util.LongSparseArray<*>
            && this.size == 0
        ) {
            return true
        }
    }
    if (this is Iterator<*> && this.hasNext()) {
        return false;
    }

    return false
}

@OptIn(ExperimentalContracts::class)
@Contract("returns false -> this != null")
fun Any?.isNull(): Boolean {
    contract {
        returns(false) implies (this@isNull != null)
    }
    return this == null
}

@OptIn(ExperimentalContracts::class)
@Contract("returns true -> this != null")
fun Any?.isNotNull(): Boolean {
    contract {
        returns(true) implies (this@isNotNull != null)
    }
    return this != null
}

fun CharSequence?.isEmpty(): Boolean = this == null || this.toString().length == 0

fun MutableMap<*, *>?.isEmpty(): Boolean = this.isNullOrEmpty()

fun SimpleArrayMap<*, *>?.isEmpty(): Boolean = this == null || this.isEmpty()

fun SparseArray<*>?.isEmpty(): Boolean = this == null || this.size == 0

fun SparseBooleanArray?.isEmpty(): Boolean = this == null || this.size == 0

fun SparseIntArray?.isEmpty(): Boolean = this == null || this.size == 0

fun LongSparseArray<*>?.isEmpty(): Boolean = this == null || this.size() == 0

@SuppressLint("ObsoleteSdkInt")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
fun SparseLongArray?.isEmpty(): Boolean = this == null || this.size == 0

@SuppressLint("ObsoleteSdkInt")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
fun android.util.LongSparseArray<*>?.isEmpty(): Boolean = this == null || this.size == 0

fun ArrayMap<*, *>?.isEmpty(): Boolean = this == null || this.isEmpty()

fun Collection<*>?.isEmpty(): Boolean = this.isNullOrEmpty()

fun Iterator<*>?.isEmpty(): Boolean = this == null || !this.hasNext()

/**
 * Return whether object is not empty.
 *
 * @param obj The object.
 * @return `true`: yes<br></br>`false`: no
 */
fun Any?.isNotEmpty(): Boolean = !this.isEmpty()

fun CharSequence?.isNotEmpty(): Boolean = !this.isEmpty()

fun MutableMap<*, *>?.isNotEmpty(): Boolean = !this.isEmpty()

fun SimpleArrayMap<*, *>?.isNotEmpty(): Boolean = !this.isEmpty()

fun SparseArray<*>?.isNotEmpty(): Boolean = !this.isEmpty()

fun SparseBooleanArray?.isNotEmpty(): Boolean = !this.isEmpty()

fun SparseIntArray?.isNotEmpty(): Boolean = !this.isEmpty()

fun LongSparseArray<*>?.isNotEmpty(): Boolean = !this.isEmpty()

@SuppressLint("ObsoleteSdkInt")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
fun SparseLongArray?.isNotEmpty(): Boolean = !this.isEmpty()

@SuppressLint("ObsoleteSdkInt")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
fun android.util.LongSparseArray<*>?.isNotEmpty(): Boolean = !this.isEmpty()

fun ArrayMap<*, *>?.isNotEmpty(): Boolean = !this.isEmpty()

fun Collection<*>?.isNotEmpty(): Boolean = !this.isNullOrEmpty()

fun Iterator<*>?.isNotEmpty(): Boolean = !this.isEmpty()

fun kotlin.Array<out Any?>.isNullOr(): Boolean = any { it == null }

fun kotlin.Array<out Any?>.isNullAnd(): Boolean = any { it != null }

fun kotlin.Array<out Any?>.isEmptyOr(): Boolean = any { it.isEmpty() }

fun kotlin.Array<out Any?>.isEmptyAnd(): Boolean = any { it.isNotEmpty() }

/**
 * 判断两个对象是否相等
 */
fun Any?.eq(o2: Any?): Boolean = this != null && this == o2

/**
 * 判断两个数值对象是否相等
 */
fun Any?.eqNum(o2: Any): Boolean = this != null && this.toDouble() == o2.toDouble()

/**
 * Require the objects are not null.
 *
 * @param objects The object.
 * @throws NullPointerException if any object is null in objects
 */
fun kotlin.Array<out Any?>.requireNonNull() {
    for (`object` in this) {
        if (`object` == null) throw NullPointerException()
    }
}

/**
 * Return the nonnull object or default object.
 *
 * @param object        The object.
 * @param defaultObject The default object to use with the object is null.
 * @param <T>           The value type.
 * @return the nonnull object or default object
</T> */
fun <T> T?.getOrDefault(defaultObject: T?): T? = this ?: defaultObject

/**
 * 获取列表第一个元素
 */
fun <T> MutableList<T?>?.getListFirst(): T? = if (this.isEmpty()) null else this?.get(0)

/**
 * Return the hash code of object.
 *
 * @param o The object.
 * @return the hash code of object
 */
fun Any?.hashCode(): Int = this?.hashCode() ?: 0

/**
 * obj满足等于match中任意一个元素则返回true
 */
fun Any?.orEq(vararg match: Any?): Boolean {
    if (this == null) {
        return false
    }
    for (item in match) {
        if (this.eq(item)) {
            return true
        }
    }

    return false
}

/**
 * obj满足等于match中全部元素则返回true
 */
fun Any?.andEq(vararg match: Any?): Boolean {
    if (this == null) {
        return false
    }
    for (item in match) {
        if (!this.eq(item)) {
            return false
        }
    }

    return true
}

/****************************************以下为类型类型转换相关 */
fun <T> Any?.toT(): T? = if (this == null) null else this as T

@JvmOverloads
fun Any?.toString(devVal: String? = ""): String? = this?.toString() ?: devVal

@JvmOverloads
fun Any?.toBoolean(defVal: Boolean = false): Boolean {
    try {
        if (this == null) {
            return defVal
        }

        if (this.javaClass.isAssignableFrom(Boolean::class.javaPrimitiveType)
            || this.javaClass.isAssignableFrom(Boolean::class.java)
        ) {
            return this as Boolean
        }

        return this.toString().toBooleanStrictOrNull() ?: defVal
    } catch (ex: Exception) {
        ex.printStackTrace()
        return false
    }
}

@JvmOverloads
fun Any?.toDouble(defVal: Double = 0.0): Double {
    try {
        if (this.isEmpty()) {
            return defVal
        }

        if (this!!.javaClass.isAssignableFrom(Double::class.javaPrimitiveType) || this.javaClass.isAssignableFrom(
                Double::class.java
            )
        ) {
            return this as Double
        }

        return this.toString().toDoubleOrNull() ?: defVal
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return defVal
}

@JvmOverloads
fun Any?.toFloat(defVal: Float = 0f): Float {
    try {
        if (this.isEmpty()) {
            return defVal
        }

        if (this!!.javaClass.isAssignableFrom(Float::class.javaPrimitiveType) || this.javaClass.isAssignableFrom(
                Float::class.java
            )
        ) {
            return this as Float
        }

        return this.toDouble().toFloat()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return defVal
}

@JvmOverloads
fun Any?.toInt(defVal: Int = 0): Int {
    try {
        if (this.isEmpty()) {
            return defVal
        }

        if (this!!.javaClass.isAssignableFrom(Int::class.javaPrimitiveType) || this.javaClass.isAssignableFrom(
                Int::class.java
            )
        ) {
            return this as Int
        }

        return this.toDouble().toInt()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return defVal
}

@JvmOverloads
fun Any?.toLong(defVal: Long = 0L): Long {
    try {
        if (this.isEmpty()) {
            return defVal
        }

        if (this!!.javaClass.isAssignableFrom(Long::class.javaPrimitiveType) || this.javaClass.isAssignableFrom(
                Long::class.java
            )
        ) {
            return (this as Int).toLong()
        }

        return this.toDouble().toLong()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return defVal
}

/****************************************以下为对象克隆相关 */
/**
 * Deep clone.
 *
 * @param data The data.
 * @param <T>  The value type.
 * @return The object of cloned
</T> */
fun <T> Serializable?.deepClone(): T? =
    if (this == null) null else bytes2Object(serializable2Bytes(this)) as T?

private fun serializable2Bytes(serializable: Serializable?): ByteArray? {
    if (serializable == null) return null
    val baos: ByteArrayOutputStream?
    var oos: ObjectOutputStream? = null
    try {
        oos = ObjectOutputStream(ByteArrayOutputStream().also { baos = it })
        oos.writeObject(serializable)
        return baos!!.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        try {
            if (oos != null) {
                oos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

private fun bytes2Object(bytes: ByteArray?): Any? {
    if (bytes == null) return null
    var ois: ObjectInputStream? = null
    try {
        ois = ObjectInputStream(ByteArrayInputStream(bytes))
        return ois.readObject()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        try {
            if (ois != null) {
                ois.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}