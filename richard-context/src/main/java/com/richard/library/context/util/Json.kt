package com.richard.library.context.util

import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.alibaba.fastjson.parser.Feature
import com.alibaba.fastjson.serializer.NumberCodec
import com.alibaba.fastjson.serializer.SerializeConfig
import com.alibaba.fastjson.serializer.SerializerFeature
import com.alibaba.fastjson.util.TypeUtils
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.text.DecimalFormat
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.KClass

/**
 * Created by xiejiao on 2017/1/24.
 * json 转换(alibaba fastJson)
 */
private object JsonConfig {
    init {
        //解决首字母大小写问题
        TypeUtils.compatibleWithJavaBean = true

        //全局关闭循环引用检测
        JSON.DEFAULT_GENERATE_FEATURE =
            JSON.DEFAULT_GENERATE_FEATURE or SerializerFeature.DisableCircularReferenceDetect.mask
        JSON.DEFAULT_PARSER_FEATURE =
            JSON.DEFAULT_PARSER_FEATURE or Feature.DisableCircularReferenceDetect.mask

        //浮点数值序列化配置
        val decimalFormat = DecimalFormat("#.################")
        decimalFormat.isGroupingUsed = false
        SerializeConfig.getGlobalInstance().put(Double::class.java, NumberCodec(decimalFormat))
        SerializeConfig.getGlobalInstance().put(Float::class.java, NumberCodec(decimalFormat))
    }
}

// 触发自动初始化（只要扩展函数被使用，就自动执行配置）
private val autoInit = JsonConfig

/**
 * 转换位json字符串
 */
fun Any?.toJson(): String? {
    if (this == null) {
        return null
    }
    return JSON.toJSONString(this)
}

/**
 * 转换位json字符串
 */
fun Any?.toJsonThrow(): String {
    requireNotNull(this) { "When calling the toJsonThrow() method, the object cannot be empty" }
    return JSON.toJSONString(this)
}

/**
 * 转换为JSONObject
 */
fun Any?.toJSONObject(): JSONObject? {
    if (this == null) {
        return null
    }

    try {
        if (this is String) {
            return JSON.parseObject(this)
        }
        return JSON.parseObject(this.toJson())
    } catch (ex: Exception) {
        ex.printStackTrace()
        return null
    }
}

/**
 * 转换为JSONObject
 */
fun Any?.toJSONObjectThrow(): JSONObject {
    requireNotNull(this) { "When calling the toJSONObjectThrow() method, the object cannot be empty" }
    if (this is String) {
        return JSON.parseObject(this)
    }
    return JSON.parseObject(this.toJson())
}

/**
 * 转为新实体对象
 * @param clazz 新对象类型class
 */
fun <T> Any?.toObject(clazz: Class<T?>?): T? {
    if (this == null) {
        return null
    }

    try {
        if (this is String) {
            return JSON.parseObject<T?>(this, clazz)
        }
        return JSON.parseObject<T?>(this.toJson(), clazz)
    } catch (ex: Exception) {
        ex.printStackTrace()
        return null
    }
}

/**
 * 转为新实体对象
 * @param clazz 新对象类型class
 */
fun <T> Any?.toObjectThrow(clazz: Class<T?>?): T {
    if (this is String) {
        return JSON.parseObject<T?>(this, clazz)!!
    }
    return JSON.parseObject<T?>(this.toJson(), clazz)!!
}

/**
 * 转换为对象
 * @param reference 新对象类型
 */
fun <T> Any?.toObject(reference: TypeReference<T?>): T? {
    if (this == null) {
        return null
    }

    try {
        if (this is String) {
            return JSON.parseObject<T?>(this, reference)
        }
        return JSON.parseObject<T?>(this.toJson(), reference)
    } catch (ex: Exception) {
        ex.printStackTrace()
        return null
    }
}

/**
 * 转换为对象
 * @param reference 新对象类型
 */
fun <T> Any?.toObjectThrow(reference: TypeReference<T?>): T {
    if (this is String) {
        return JSON.parseObject<T?>(this, reference)!!
    }
    return JSON.parseObject<T?>(this.toJson(), reference)!!
}

/**
 * 将byte数组转换为对象
 * @param clazz 新对象类型class
 */
fun <T> ByteArray?.toObject(clazz: Class<T?>?): T? {
    if (this == null) {
        return null
    }

    try {
        return String(this).toObject<T?>(clazz)
    } catch (ex: Exception) {
        ex.printStackTrace()
        return null
    }
}

/**
 * 将byte数组转换为对象
 * @param clazz 新对象类型class
 */
fun <T> ByteArray?.toObjectThrow(clazz: Class<T?>?): T {
    requireNotNull(this) { "When calling the toObjectThrow() method, the object cannot be empty" }
    return String(this).toObject<T?>(clazz)!!
}

/**
 * 将byte数组转换为对象
 * @param reference 新对象类型
 */
fun <T> ByteArray?.toObject(reference: TypeReference<T?>): T? {
    if (this == null) {
        return null
    }

    try {
        return String(this).toObject<T?>(reference)
    } catch (ex: Exception) {
        ex.printStackTrace()
        return null
    }
}

/**
 * 将byte数组转换为对象
 * @param reference 新对象类型
 */
fun <T> ByteArray?.toObjectThrow(reference: TypeReference<T?>): T {
    requireNotNull(this) { "When calling the toObjectThrow() method, the object cannot be empty" }
    return String(this).toObject<T?>(reference)!!
}

/**
 * 转为对象
 * @param type 新对象类型
 */
fun <T> Any?.toObject(type: Type?): T? {
    if (this == null) {
        return null
    }

    try {
        if (this is String) {
            return JSON.parseObject<T?>(this, type)
        }
        return JSON.parseObject<T?>(this.toJson(), type)
    } catch (ex: Exception) {
        ex.printStackTrace()
        return null
    }
}

/**
 * 转为对象
 * @param type 新对象类型
 */
fun <T> Any?.toObjectThrow(type: Type?): T {
    if (this is String) {
        return JSON.parseObject<T?>(this, type)!!
    }
    return JSON.parseObject<T?>(this.toJson(), type)!!
}

//------------------------array----------------------------
/**
 * 转为JSONArray
 */
fun Any?.toJSONArray(): JSONArray? {
    try {
        return if (this is String) {
            JSONArray.parseArray(this)
        } else {
            JSONArray.parseArray(this.toJson())
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
        return null
    }
}

/**
 * 转为JSONArray
 */
fun Any?.toJSONArrayThrow(): JSONArray {
    return if (this is String) {
        JSONArray.parseArray(this)
    } else {
        JSONArray.parseArray(this.toJson())
    }
}

/**
 * 转为对象列表
 */
fun <T> Any?.toObjectList(clazz: Class<T?>?): MutableList<T?>? {
    try {
        if (this is String) {
            return JSON.parseArray<T?>(this, clazz)
        }
        return JSON.parseArray<T?>(this.toJson(), clazz)
    } catch (ex: Exception) {
        ex.printStackTrace()
        return null
    }
}

/**
 * 转为对象列表
 */
fun <T> Any?.toObjectListThrow(clazz: Class<T?>?): MutableList<T?> {
    if (this is String) {
        return JSON.parseArray<T?>(this, clazz)
    }
    return JSON.parseArray<T?>(this.toJson(), clazz)
}

/**
 * 是否属于json
 */
fun String?.isJson(): Boolean {
    return this.isJsonArray() || this.isJsonObject()
}

/**
 * 是否属于json数组
 */
fun String?.isJsonArray(): Boolean {
    var str = this
    if (TextUtils.isEmpty(str)) {
        return false
    }
    str = str!!.trim { it <= ' ' }
    return str.startsWith("[") && str.endsWith("]")
}

/**
 * 是否属于json对象
 */
fun String?.isJsonObject(): Boolean {
    var str = this
    if (TextUtils.isEmpty(str)) {
        return false
    }
    str = str!!.trim { it <= ' ' }
    return str.startsWith("{") && str.endsWith("}")
}

/**
 * 是否属于xml
 */
fun String?.isXml(): Boolean {
    var str = this
    if (TextUtils.isEmpty(str)) {
        return false
    }
    str = str!!.trim { it <= ' ' }
    return str.startsWith("<") && str.endsWith(">")
}

/**
 * 是否属于实体类
 */
fun Class<*>.isEntity(): Boolean {
    val simpleTypes = setOf(
        String::class.java,
        CharSequence::class.java,
        Int::class.java,
        Integer::class.java,
        Long::class.java,
        Double::class.java,
        Float::class.java,
        Byte::class.java,
        Short::class.java,
        Boolean::class.java,
        Date::class.java,
        Char::class.java
    )
    return simpleTypes.none { this.isAssignableFrom(it) }
}

/**
 * 是否属于实体类
 */
fun KClass<*>.isEntity(): Boolean {
    return this.java.isEntity()
}


/**
 * 获取对象类型，优先获取类本身的泛型，再获取实现类或接口的泛型
 */
fun Any?.getType(): Type {
    if (this == null) {
        return Any::class.java
    }

    val type = this.javaClass.getSuperClassType()
    if (type != Any::class.java) {
        return type
    }
    return this.getInterfaceType()
}

/**
 * 获取class类泛型
 */
fun Class<*>.getSuperClassType(): Type {
    //--获取这个对象的父类泛型
    val superClass = this.genericSuperclass ?: return Any::class.java
    if (superClass !is ParameterizedType) {
        return Any::class.java
    }

    return fixAndroidType(superClass.actualTypeArguments[0])
}

/**
 * 获取interface类泛型
 */
fun Any?.getInterfaceType(): Type {
    if (this == null) {
        return Any::class.java
    }

    val types = this.javaClass.genericInterfaces
    if (types == null || types.size == 0) {
        return Any::class.java
    }

    val type = types[0]
    if (type !is ParameterizedType) {
        return Any::class.java
    }

    return fixAndroidType(type.actualTypeArguments[0])
}

/**
 * 修复在安卓环境中问题
 */
private fun fixAndroidType(type: Type?): Type {
    if (type == null) {
        return Any::class.java;
    }
    if (type is Class<*>) {
        return type
    } else {
        val classTypeCache: ConcurrentMap<Type?, Type?> =
            ConcurrentHashMap<Type, Type>(16, 0.75f, 1)
        //修复在安卓环境中问题
        var cachedType = classTypeCache[type]
        if (cachedType == null) {
            classTypeCache.putIfAbsent(type, type)
            cachedType = classTypeCache[type]
        }
        return cachedType ?: Any::class.java
    }
}

/**
 * 判断 Type 是否为 集合/列表 类型（List、Set、Collection 等）
 */
fun Type?.isCollectionType(): Boolean {
    if (this is Class<*>) {
        return MutableCollection::class.java.isAssignableFrom(this)
    } else if (this is ParameterizedType) {
        val rawType = this.rawType
        return rawType is Class<*> && MutableCollection::class.java.isAssignableFrom(rawType)
    }
    return false
}

/**
 * 自动获取 Type 对应的真实实体类 Class
 *
 * @param isOuterType true：只获取最外层类型、false：只获取最内层类型
 *
 * 支持：
 * 1. User.class → 返回 User.class
 * 2. List<User>  → 返回 User.class
 * 3. List<List></List><User>> → 也能返回 User.class（可无限嵌套）
 */
fun Type?.getRealClass(isOuterType: Boolean): Class<*> {
    if (this is ParameterizedType) {
        if (isOuterType) {
            return this.rawType as Class<*>
        }
        val actualType = this.actualTypeArguments[0]
        return actualType.getRealClass(false)
    } else if (this is Class<*>) {
        return this
    }
    return Any::class.java
}

//详见https://blog.csdn.net/zhouhengzhe/article/details/142390748
//---序列化配置SerializerFeature的属性：
//QuoteFieldNames：字段名使用双引号括起来，符合 JSON 标准。
//UseSingleQuotes：使用单引号而不是双引号来括起字段名和字符串值。这不是 JSON 标准，但在某些情况下可能更节省空间或符合特定需求。
//WriteMapNullValue：写入值为 null 的字段。默认情况下，值为 null 的字段不会被序列化。
//WriteEnumUsingToString：枚举类型使用 toString() 方法的返回值进行序列化，而不是使用枚举的名字。
//WriteEnumUsingName：枚举类型使用其名字（即 name() 方法的返回值）进行序列化。
//UseISO8601DateFormat：使用 ISO8601 标准的日期格式来序列化日期对象。
//WriteNullListAsEmpty：将 null 值的 List 字段序列化为空数组 []。
//WriteNullStringAsEmpty：将 null 值的字符串字段序列化为空字符串 ""。
//WriteNullNumberAsZero：将 null 值的数字字段序列化为 0。
//WriteNullBooleanAsFalse：将 null 值的布尔字段序列化为 false。
//SkipTransientField：跳过 transient 修饰的字段，不进行序列化。
//SortField：对字段进行排序后再序列化。
//WriteTabAsSpecial（已弃用）：特殊处理 Tab 字符，通常不建议使用。
//PrettyFormat：格式化输出，使 JSON 字符串更易于阅读，包含缩进和换行。
//WriteClassName：写入类名信息，便于反序列化时恢复对象的实际类型。
//DisableCircularReferenceDetect：禁用循环引用检测，避免因为循环引用导致的无限递归。
//WriteSlashAsSpecial：对斜杠 / 进行特殊处理，通常用于确保生成的 JSON 可以作为 URL 的一部分而不被破坏。
//BrowserCompatible：浏览器兼容模式，处理一些与浏览器相关的特殊字符。
//WriteDateUseDateFormat：使用自定义的日期格式来序列化日期对象。
//NotWriteRootClassName：不写入根对象的类名，即使启用了 WriteClassName。
//DisableCheckSpecialChar（已弃用）：禁用特殊字符检查，通常不建议使用以避免潜在的安全问题。
//BeanToArray：将 Java Bean 序列化为数组形式，而不是默认的键值对形式。
//WriteNonStringKeyAsString：将非字符串类型的 Key 也序列化为字符串。
//NotWriteDefaultValue：不写入默认值，即如果字段的值等于其类型的默认值（如数字为 0，布尔为 false），则不进行序列化。
//BrowserSecure：浏览器安全模式，防止 XSS 攻击等安全问题。
//IgnoreNonFieldGetter：忽略非字段的 getter 方法，即只序列化 Java Bean 的字段，不序列化通过 getter 方法暴露的属性（如果该 getter 不是对应某个字段的）。
//WriteNonStringValueAsString：将非字符串类型的值也强制序列化为字符串。
//IgnoreErrorGetter：在序列化过程中，如果 getter 方法抛出异常，则忽略该异常并继续序列化其他字段。
//WriteBigDecimalAsPlain：将 BigDecimal 类型的值序列化为无科学计数法的普通数字字符串。
//MapSortField：对 Map 类型的字段进行排序后再序列化。
//-------反序列化配置Feature枚举的属性：
//AutoCloseSource:自动关闭JSON源输入流。在完成反序列化后，会尝试关闭输入流，如InputStream。
//AllowComment:允许JSON字符串中包含注释。标准的JSON是不支持注释的，开启此特性后，可以解析包含//或/* */注释的JSON。
//AllowUnQuotedFieldNames:允许字段名不使用双引号包围。标准的JSON要求字段名必须使用双引号，但开启此特性后，可以解析不使用双引号的字段名。
//AllowSingleQuotes:允许使用单引号包围字符串值。标准的JSON要求字符串值使用双引号，但开启此特性后，也支持单引号。
//InternFieldNames:对字段名进行字符串驻留（intern）。这有助于减少内存占用，当有很多相同字段名的JSON对象时。
//AllowISO8601DateFormat:允许使用ISO8601格式的日期字符串。例如，2023-04-01T12:00:00Z。
//AllowArbitraryCommas:允许JSON对象中存在多余的逗号。例如，{"a":1,,"b":2}中的逗号。
//UseBigDecimal:使用BigDecimal来解析浮点数，而不是double。这可以提供更精确的数值表示。
//IgnoreNotMatch:忽略不匹配的字段。当JSON中的字段与Java对象的字段不匹配时，不会抛出异常。
//SortFeidFastMatch： (可能是SortFieldFastMatch的拼写错误)对字段进行排序以快速匹配。这可能有助于优化某些情况下的反序列化性能。
//DisableASM:禁用ASM（Java字节码操作和分析框架）优化。在某些情况下，为了避免与ASM相关的问题，可以禁用它。
//DisableCircularReferenceDetect:禁用循环引用检测。当JSON中存在循环引用时，不会抛出异常。
//InitStringFieldAsEmpty:将字符串字段初始化为空字符串，而不是null。
//SupportArrayToBean:支持将JSON数组反序列化为Java Bean。通常，JSON数组会转换为Java的List或array，但开启此特性后，可以将其转换为具有特定字段的Java对象。
//OrderedField:保持字段的顺序。在反序列化时，Java对象的字段将按照JSON中出现的顺序进行设置。
//DisableSpecialKeyDetect:禁用特殊键检测。例如，不处理$ref等用于处理循环引用的特殊键。
//UseObjectArray:使用Object[]来接收反序列化的数组，而不是具体类型的数组。
//SupportNonPublicField:支持反序列化到非公共字段（例如，私有字段）。
//IgnoreAutoType:忽略自动类型识别。这可能与安全性相关，以防止利用类型识别进行攻击。
//DisableFieldSmartMatch:禁用字段智能匹配。这可能影响字段名与Java对象属性名的匹配逻辑。
//SupportAutoType:支持自动类型识别。允许在反序列化时自动确定对象的类型。
//NonStringKeyAsString:将非字符串类型的键也作为字符串处理。
//CustomMapDeserializer:使用自定义的Map反序列化器。允许用户定义如何反序列化JSON对象为Map类型。
//ErrorOnEnumNotMatch:当枚举值不匹配时抛出错误。如果JSON中的枚举值与Java枚举类中的值不匹配，将抛出异常。
//SafeMode:安全模式。在此模式下，Fastjson会采取更严格的安全措施来防止潜在的安全风险，如自动类型识别的限制等。