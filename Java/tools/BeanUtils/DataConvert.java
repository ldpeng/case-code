import net.cyan.commons.util.bean.*;
import net.cyan.commons.util.json.JsonSerializer;
import net.cyan.commons.util.language.Language;

import java.awt.*;
import java.io.*;
import java.lang.ref.SoftReference;
import java.lang.reflect.*;
import java.math.*;
import java.text.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>Title: 数据转化</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 *
 * @author ccs
 * @version 1.0
 */

public final class DataConvert
{
    //基本类型的正则表达式列表

    //数字
    public static final String NUM_PATTERN = "^[-\\+]?\\d+(\\.\\d+)?(E\\-?\\d+)?$";

    public static final Pattern NUM_PATTERN_ = Pattern.compile(NUM_PATTERN);

    //整数
    public static final String INT_PATTERN = "^[-\\+]?[0-9]+$";

    public static final Pattern INT_PATTERN_ = Pattern.compile(INT_PATTERN);

    //正数
    public static final String POSITIVE_NUM_PATTERN = "^\\d+(\\.\\d+)?(E\\-?\\d+)?$";

    public static final Pattern POSITIVE_NUM_PATTERN_ = Pattern.compile(POSITIVE_NUM_PATTERN);

    //正整数
    public static final String POSITIVE_INT_PATTERN = "^[0-9]+$";

    public static final Pattern POSITIVE_INT_PATTERN_ = Pattern.compile(POSITIVE_INT_PATTERN);


    private static final Map<String, FormatFactory> factorys = new HashMap<String, FormatFactory>();

    private static List<ValueProvider> valueProviders = null;

    private static final Map<Class, Method> valueOfs = new HashMap<Class, Method>();

    @SuppressWarnings("UnusedDeclaration")
    public static final Comparator<Object> COMPARATOR = new Comparator<Object>()
    {
        public int compare(Object o1, Object o2)
        {
            return DataConvert.compare(o1, o2);
        }
    };

    /**
     * 保存所有加载过的枚举数组
     */
    private static final Map<Class<? extends Enum>, Enum<?>[]> ENUMVALUES =
            new HashMap<Class<? extends Enum>, Enum<?>[]>();

    /**
     * 保存所有加载过的枚举
     */
    private static final Map<Class<? extends AdvancedEnum>, EnumValuesProvider> ENUMVALUESPROVIDERS =
            new HashMap<Class<? extends AdvancedEnum>, EnumValuesProvider>();


    private static List<EnumValuesProviderFactory> enumValuesProviderFactorys =
            Collections.singletonList(StaticMethodValuesProvider.FACTORY);

    /**
     * 保存所有加载过的枚举
     */
    private static final Map<Class<? extends Value>, Class<?>> VALUETYPES =
            new HashMap<Class<? extends Value>, Class<?>>();

    private static final ThreadLocal<SoftReference<Collator>> COLLATOR_THREAD_LOCAL =
            new ThreadLocal<SoftReference<Collator>>();

    private DataConvert()
    {
    }

    /**
     * 添加一个命名的格式化工厂
     *
     * @param name    工厂的名字
     * @param factory 格式化工厂
     */
    public static void addFactory(String name, FormatFactory factory)
    {
        synchronized (factorys)
        {
            factorys.put(name, factory);
        }
    }

    static
    {
        //给几个中文格式化工厂命名
        addFactory("gbk", new Chinese.ChineseFormatFactory(0));
        addFactory("gb5", new Chinese.ChineseFormatFactory(1));
        addFactory("gbb", new Chinese.ChineseFormatFactory(2));
        addFactory("trunc", new Chinese.TruncateFormatFactory());

        try
        {
            addFactory("html", HtmlUtils.HTMLFORMATFACTORY);
            addFactory("json", JsonSerializer.JSONFORMATFACTORY);
            addFactory("bytesize", IOUtils.BYTESIZEFORMATFACTORY);
        }
        catch (Throwable ex)
        {
            //webutils加载错误
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertToType(Type type, Object value) throws Exception
    {
        if (value == null)
            return null;

        if (value instanceof Convertable)
            return (T) ((Convertable) value).convert(type);

        Class c = BeanUtils.toClass(type);

        if (c.isArray())
        {
            return convertToArray(BeanUtils.getArrayElementType(type), value);
        }
        else if (Collection.class.isAssignableFrom(c))
        {
            return convertToCollection(type, value);
        }
        else
        {
            return (T) convertType(c, value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertToArray(Type componentType, Object value) throws Exception
    {
        if (value == null)
            return null;

        Class c = BeanUtils.toClass(componentType);
        Class c0 = value.getClass();

        if (c0.isArray())
        {
            if (c0.getComponentType() == c)
                return (T) value;

            if (value instanceof String[])
                return convertArray(c, (String[]) value);

            int n = Array.getLength(value);
            Object array = Array.newInstance(c, n);
            for (int i = 0; i < n; i++)
                Array.set(array, i, convertToType(componentType, Array.get(value, i)));

            return (T) array;
        }
        else if (value instanceof Collection)
        {
            Collection collection = (Collection) value;
            int n = collection.size();

            Object array = Array.newInstance(c, n);
            int i = 0;
            for (Object obj : collection)
            {
                Array.set(array, i++, convertToType(componentType, obj));
            }

            return (T) array;
        }
        else if (value instanceof String)
        {
            return convertArray(c, ((String) value).split(","));
        }
        else
        {
            Object array = Array.newInstance(c, 1);
            Array.set(array, 0, convertToType(componentType, value));

            return (T) array;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertToCollection(Type collectionType, Object value) throws Exception
    {
        if (value == null)
            return null;

        Class c = BeanUtils.toClass(collectionType);
        Type elementType = BeanUtils.getCollectionElementType(collectionType);
        Class c0 = value.getClass();

        if (c0.isArray())
        {
            int n = Array.getLength(value);
            Collection collection = CollectionUtils.createCollection(collectionType, n);

            for (int i = 0; i < n; i++)
                collection.add(convertToType(elementType, Array.get(value, i)));

            return (T) collection;
        }
        else if (value instanceof Collection)
        {
            Collection collection0 = (Collection) value;
            int n = collection0.size();

            Collection collection = CollectionUtils.createCollection(collectionType, n);
            for (Object obj : collection0)
            {
                collection.add(convertToType(elementType, obj));
            }

            return (T) collection;
        }
        else if (value instanceof String)
        {
            String[] ss = ((String) value).split(",");

            Collection collection = CollectionUtils.createCollection(collectionType, ss.length);
            Class c1 = BeanUtils.toClass(elementType);
            for (String s : ss)
            {
                collection.add(convertValue(c1, s));
            }

            return (T) collection;
        }
        else
        {
            Collection collection = CollectionUtils.createCollection(collectionType, 1);
            collection.add(convertToType(elementType, value));

            return (T) collection;
        }
    }

    /**
     * 转化数据类型，支持范型化
     *
     * @param c     转化后的类型
     * @param value 被转化的数据a
     * @return 转化后的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertType(Class<T> c, Object value)
    {
        return (T) convertType0(c, value);
    }

    /**
     * 转化数据类型
     *
     * @param c     转化后的类型
     * @param value 被转化的数据
     * @return 转化后的值
     */
    @SuppressWarnings("unchecked")
    private static Object convertType0(Class c, Object value)
    {
        if (value != null)
        {
            if (c == Object.class)
                return value;

            if (c.isInstance(value))
                return value;

            if (value instanceof Value)
                value = ((Value) value).valueOf();

            if (value instanceof String[])
            {
                String[] ss = (String[]) value;
                if (ss.length == 0)
                    return null;
                else
                    return convertValue0(c, ss[0]);
            }

            if (c == String.class)
                return toString(value);

            if (value instanceof String)
                return convertValue0(c, (String) value);

            if (c == Integer.class || c == int.class)
            {
                if (value instanceof Number)
                    return ((Number) value).intValue();

                if (value instanceof Enum)
                    return ((Enum) value).ordinal();

                if (value instanceof Boolean)
                    return ((Boolean) value) ? 1 : 0;
            }
            else if (c == Long.class || c == long.class)
            {
                if (value instanceof Number)
                    return ((Number) value).longValue();

                if (value instanceof Enum)
                    return (long) ((Enum) value).ordinal();

                if (value instanceof Boolean)
                    return (long) (((Boolean) value) ? 1 : 0);
            }
            else if (c == Short.class || c == short.class)
            {
                if (value instanceof Number)
                    return ((Number) value).shortValue();

                if (value instanceof Enum)
                    return (short) ((Enum) value).ordinal();

                if (value instanceof Boolean)
                    return (short) (((Boolean) value) ? 1 : 0);
            }
            else if (c == Byte.class || c == byte.class)
            {
                if (value instanceof Number)
                    return ((Number) value).byteValue();

                if (value instanceof Enum)
                    return (byte) ((Enum) value).ordinal();

                if (value instanceof Boolean)
                    return (byte) (((Boolean) value) ? 1 : 0);
            }
            else if (c == Float.class || c == float.class)
            {
                if (value instanceof Number)
                    return ((Number) value).floatValue();

                if (value instanceof Enum)
                    return (float) ((Enum) value).ordinal();

                if (value instanceof Boolean)
                    return (float) (((Boolean) value) ? 1 : 0);
            }
            else if (c == Double.class || c == double.class)
            {
                if (value instanceof Number)
                    return ((Number) value).doubleValue();

                if (value instanceof Enum)
                    return (float) ((Enum) value).ordinal();

                if (value instanceof Boolean)
                    return (double) (((Boolean) value) ? 1 : 0);
            }
            else if (c == Character.class || c == char.class)
            {
                if (value instanceof Number)
                    return (char) ((Number) value).intValue();

                return value.toString().charAt(0);
            }
            else if (c == Boolean.class || c == boolean.class)
            {
                return value instanceof Boolean ? value :
                        !(value instanceof Number) || ((Number) value).doubleValue() != 0;
            }
            else if (c == Date.class)
            {
                if (value instanceof Number)
                    return new Date(((Number) value).longValue());
                else if (value instanceof Calendar)
                    return ((Calendar) value).getTime();
            }
            else if (c == java.sql.Time.class)
            {
                if (value instanceof Number)
                    return new java.sql.Time(((Number) value).longValue());
                else if (value instanceof Date)
                    return new java.sql.Time(((Date) value).getTime());
                else if (value instanceof Calendar)
                    return new java.sql.Time(((Calendar) value).getTimeInMillis());
            }
            else if (c == java.sql.Timestamp.class)
            {
                if (value instanceof Number)
                    return new java.sql.Timestamp(((Number) value).longValue());
                else if (value instanceof Date)
                    return new java.sql.Timestamp(((Date) value).getTime());
                else if (value instanceof Calendar)
                    return new java.sql.Timestamp(((Calendar) value).getTimeInMillis());
            }
            else if (c == java.sql.Date.class)
            {
                if (value instanceof Number)
                    return new java.sql.Date(((Number) value).longValue());
                else if (value instanceof Date)
                    return new java.sql.Date(((Date) value).getTime());
                else if (value instanceof Calendar)
                    return new java.sql.Date(((Calendar) value).getTimeInMillis());
            }
            else if (c == Calendar.class)
            {
                if (value instanceof Number)
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(((Number) value).longValue());
                    return calendar;
                }
                else if (value instanceof Date)
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(((Date) value));
                    return calendar;
                }
            }
            else if (c == char[].class)
            {
                return toString(value).toCharArray();
            }
            else if (c == StringBuilder.class)
            {
                return new StringBuilder(toString(value));
            }
            else if (c == StringBuffer.class)
            {
                return new StringBuilder(toString(value));
            }
            else if (c == byte[].class)
            {
                if (value instanceof InputStream)
                {
                    try
                    {
                        return IOUtils.streamToBytes((InputStream) value);
                    }
                    catch (IOException ex)
                    {
                        ExceptionUtils.wrapException(ex);
                    }
                }
                else if (value instanceof File)
                {
                    try
                    {
                        return IOUtils.fileToBytes((File) value);
                    }
                    catch (IOException ex)
                    {
                        ExceptionUtils.wrapException(ex);
                    }
                }
            }
            else if (c.isEnum())
            {
                try
                {
                    if (value instanceof Number)
                        return getEnumValue(c, ((Number) value).intValue());

                    if (value instanceof Enum)
                        return getEnumValue(c, ((Enum) value).ordinal());
                }
                catch (Exception ex)
                {
                    //数组越界，跳过，返回空
                    return null;
                }
            }
            else if (c == BigInteger.class)
            {
                if (value instanceof Number)
                    return new BigInteger(value.toString());

                if (value instanceof Enum)
                    return new BigInteger(Integer.toString(((Enum) value).ordinal()));

                if (value instanceof Boolean)
                    return ((Boolean) value) ? BigInteger.ONE : BigInteger.ZERO;
            }
            else if (c == BigDecimal.class)
            {
                if (value instanceof Number)
                    return new BigDecimal(value.toString());

                if (value instanceof Enum)
                    return new BigDecimal(Integer.toString(((Enum) value).ordinal()));

                if (value instanceof Boolean)
                    return ((Boolean) value) ? BigDecimal.ONE : BigDecimal.ZERO;
            }
            else if (AdvancedEnum.class.isAssignableFrom(c))
            {
                try
                {
                    return getEnumValuesProvider(c).getEnumObject(convertType0(getValueType(c), value));
                }
                catch (Exception ex)
                {
                    ExceptionUtils.wrapException(ex);
                    return null;
                }
            }
            else
            {
                return value;
            }
        }
        else
        {
            if (c == boolean.class)
                return false;
            else if (c == int.class)
                return 0;
            else if (c == short.class)
                return (short) 0;
            else if (c == byte.class)
                return (byte) 0;
            else if (c == long.class)
                return 0L;
            else if (c == double.class)
                return 0.0;
            else if (c == float.class)
                return (float) 0.0;
            else if (c == char.class)
                return '\0';
        }

        return value;
    }

    /**
     * 将字符串转化为各种值，支持泛型
     *
     * @param c 值得类型
     * @param s 字符串
     * @return 转化后的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertValue(Class<T> c, String s)
    {
        return (T) convertValue0(c, s);
    }

    /**
     * 将字符串转化为各种值
     *
     * @param c 值得类型
     * @param s 字符串
     * @return 转化后的值
     */
    @SuppressWarnings("unchecked")
    private static Object convertValue0(Class c, String s)
    {
        if (c == String.class)
            return s;

        if (s == null || "".equals(s))
        {
            if (c.isPrimitive())
            {
                if (c == char.class)
                    return '\0';
                else if (c == boolean.class)
                    return Boolean.FALSE;
                else
                    s = "0";
            }
            else
            {
                if (s != null)
                {
                    for (Object value : Null.NULLVALUES)
                    {
                        if (c.isInstance(value))
                            return value;
                    }
                }

                return null;
            }
        }

        if (c == Integer.class || c == int.class)
        {
            if ("null".equals(s))
                return null;

            try
            {
                return Integer.valueOf(s);
            }
            catch (NumberFormatException ex)
            {
                try
                {
                    return (int) SimpleEval.eval(s);
                }
                catch (Throwable ex1)
                {
                    throw ex;
                }
            }
        }
        else if (c == Long.class || c == long.class)
        {
            if ("null".equals(s))
                return null;

            try
            {
                return Long.valueOf(s);
            }
            catch (NumberFormatException ex)
            {
                try
                {
                    return (long) SimpleEval.eval(s);
                }
                catch (Throwable ex1)
                {
                    throw ex;
                }
            }
        }
        else if (c == Short.class || c == short.class)
        {
            if ("null".equals(s))
                return null;

            try
            {
                return Short.valueOf(s);
            }
            catch (NumberFormatException ex)
            {
                try
                {
                    return (short) SimpleEval.eval(s);
                }
                catch (Throwable ex1)
                {
                    throw ex;
                }
            }
        }
        else if (c == Byte.class || c == byte.class)
        {
            if ("null".equals(s))
                return null;

            try
            {
                return Byte.valueOf(s);
            }
            catch (NumberFormatException ex)
            {
                try
                {
                    return (byte) SimpleEval.eval(s);
                }
                catch (Throwable ex1)
                {
                    throw ex;
                }
            }
        }
        else if (c == Float.class || c == float.class)
        {
            if ("null".equals(s))
                return null;

            try
            {
                return Float.valueOf(s);
            }
            catch (NumberFormatException ex)
            {
                try
                {
                    return (float) SimpleEval.eval(s);
                }
                catch (Throwable ex1)
                {
                    throw ex;
                }
            }
        }
        else if (c == Double.class || c == double.class)
        {
            if ("null".equals(s))
                return null;

            try
            {
                return Double.valueOf(s);
            }
            catch (NumberFormatException ex)
            {
                try
                {
                    return SimpleEval.eval(s);
                }
                catch (Throwable ex1)
                {
                    throw ex;
                }
            }
        }
        else if (c == Character.class || c == char.class)
        {
            return s.charAt(0);
        }
        else if (c == Boolean.class || c == boolean.class)
        {
            return convertBoolean(s);
        }
        else if (c == Date.class)
        {
            return "null".equals(s) ? null : DateUtils.toDate(s);
        }
        else if (c == java.sql.Time.class)
        {
            return "null".equals(s) ? null : new java.sql.Time(DateUtils.toDate(s).getTime());
        }
        else if (c == java.sql.Timestamp.class)
        {
            return "null".equals(s) ? null : new java.sql.Timestamp(DateUtils.toDate(s).getTime());
        }
        else if (c == java.sql.Date.class)
        {
            return "null".equals(s) ? null : new java.sql.Date(DateUtils.toDate(s).getTime());
        }
        else if (c == Calendar.class)
        {
            if ("null".equals(s))
                return null;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateUtils.toDate(s));
            return calendar;
        }
        else if (c == byte[].class)
        {
            String charset = SystemConfig.getInstance().getCharset();
            try
            {
                return charset != null ? s.getBytes(charset) : s.getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException ex)
            {
                ExceptionUtils.logException(ex);
                return s.getBytes();
            }
        }
        else if (c == char[].class)
            return s.toCharArray();
        else if (c.isEnum())
        {
            return convertStringToEnum(c, s);
        }
        else if (c == BigInteger.class)
        {
            return "null".equals(s) ? null : new BigInteger(s);
        }
        else if (c == BigDecimal.class)
        {
            return "null".equals(s) ? null : new BigDecimal(s);
        }
        else if (c == StringBuilder.class)
            return new StringBuilder(s);
        else if (c == StringBuffer.class)
            return new StringBuffer(s);
        else if (AdvancedEnum.class.isAssignableFrom(c))
        {
            try
            {
                return getEnumValuesProvider(c).valueOf(s);
            }
            catch (Exception ex)
            {
                ExceptionUtils.wrapException(ex);
                return null;
            }
        }
        else if (c == Color.class)
        {
            return "null".equals(s) ? null : HtmlUtils.decodeColor(s);
        }
        else if (c == Class.class)
        {
            try
            {
                return BeanUtils.getClassForName(s);
            }
            catch (ClassNotFoundException ex)
            {
                ExceptionUtils.wrapException(ex);
                return null;
            }
        }
        else if (c.isArray() && c.getComponentType().isEnum())
        {
            String[] ss = s.split(",");
            Class enumType = c.getComponentType();
            Enum[] result = (Enum[]) Array.newInstance(enumType, ss.length);
            for (int i = 0; i < ss.length; i++)
            {
                result[i] = convertStringToEnum(enumType, ss[i]);
            }

            return result;
        }
        else
        {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Enum convertStringToEnum(Class c, String s)
    {
        try
        {
            return Enum.valueOf(c, s);
        }
        catch (IllegalArgumentException ex)
        {
            try
            {
                //尝试大写
                return Enum.valueOf(c, s.toUpperCase());
            }
            catch (IllegalArgumentException ex1)
            {
                //不是对应的enum成员，将其作为序号处理
                try
                {
                    return getEnumValue(c, Integer.parseInt(s));
                }
                catch (NumberFormatException ex2)
                {
                    //不是整数
                    return getEnumValue(c, s);
                }
                catch (Exception ex2)
                {
                    return null;
                    //传入参数不是整数或者数组越界，跳过返回空
                }
            }
        }
    }

    /**
     * 将字符串数组转化为各种数组
     *
     * @param c  数组元素的类型
     * @param ss 字符数组
     * @return 转化后的数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertArray(Class c, String[] ss)
    {
        if (c == String.class)
        {
            return (T) ss;
        }
        else if (isBaseType(c) || c == char[].class || AdvancedEnum.class.isAssignableFrom(c))
        {
            Object array = Array.newInstance(c, ss.length);
            for (int i = 0; i < ss.length; i++)
                Array.set(array, i, convertValue(c, ss[i]));

            return (T) array;
        }
        else
        {
            return null;
        }
    }

    /**
     * 将字符数组转化为各种类型的List
     *
     * @param c  要转化的类型
     * @param ss 字符数组
     * @return 转化后的List
     */
    public static <T> List<T> convertList(Class<T> c, String[] ss)
    {
        if (isBaseType(c) || c == char[].class || AdvancedEnum.class.isAssignableFrom(c))
        {
            List<T> list = new ArrayList<T>();
            for (String s : ss)
                list.add(convertValue(c, s));

            return list;
        }
        else
        {
            return null;
        }
    }

    /**
     * 将字符转化为布尔型
     *
     * @param s 字符串
     * @return 转化后的结果
     */
    public static Boolean convertBoolean(String s)
    {
        if (s == null || "null".equals(s))
            return null;

        if ("true".equals(s.toLowerCase()) || "yes".equals(s.toLowerCase()))
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }

    /**
     * 将各种类型的对象转化为字符串
     *
     * @param obj 要转化的数据
     * @return 转化后的字符串
     */
    public static String toString(Object obj)
    {
        if (obj == null)
            return "";

        if (obj instanceof String)
        {
            return (String) obj;
        }
        else if (obj instanceof Boolean)
        {
            String s = obj.toString();
            Language language = Language.getLanguage();
            if (language != null)
                return language.getWord("java.lang.Boolean." + s, s);
            else
                return s;
        }
        else if (obj instanceof Date)
        {
            return DateUtils.toString((Date) obj);
        }
        else if (obj instanceof Calendar)
        {
            return DateUtils.toString(((Calendar) obj).getTime());
        }
        else if (obj instanceof Double)
        {
            return toString(((Double) obj).doubleValue());
        }
        else if (obj instanceof Float)
        {
            return toString(((Float) obj).floatValue());
        }
        else if (obj instanceof char[])
        {
            return new String((char[]) obj);
        }
        else if (obj instanceof byte[])
        {
            String charset = SystemConfig.getInstance().getCharset();
            try
            {
                return charset != null ? new String((byte[]) obj, charset) : new String((byte[]) obj, "UTF-8");
            }
            catch (UnsupportedEncodingException ex)
            {
                ExceptionUtils.logException(ex);
                return new String((byte[]) obj);
            }
        }
        else if (obj instanceof Map.Entry)
        {
            return toString(((Map.Entry) obj).getValue());
        }
        else if (obj instanceof Object[])
        {
            StringBuilder buffer = new StringBuilder();

            for (Object item : (Object[]) obj)
            {
                if (buffer.length() > 0)
                    buffer.append(",");
                buffer.append(toString(item));
            }

            return buffer.toString();
        }
        else if (obj instanceof Collection)
        {
            StringBuilder buffer = new StringBuilder();

            for (Object item : (Collection) obj)
            {
                if (buffer.length() > 0)
                    buffer.append(",");
                buffer.append(toString(item));
            }

            return buffer.toString();
        }
        else if (obj instanceof Map && ((Map) obj).containsKey("text"))
        {
            return toString(((Map) obj).get("text"));
        }
        else if (obj instanceof Enum)
        {
            return getEnumString((Enum) obj);
        }
        else if (obj instanceof Color)
        {
            return HtmlUtils.encodeColor((Color) obj);
        }
        else if (obj instanceof Class)
        {
            return BeanUtils.getClassName((Class) obj);
        }
        else
        {
            return obj.toString();
        }
    }

    public static String getEnumString(Enum obj)
    {
        try
        {
            Language language = Language.getLanguage();
            if (language != null)
                return language.getWord(BeanUtils.getClassName(obj.getClass()) + "." + (obj).name(),
                        obj.toString());
        }
        catch (Throwable ex)
        {
            ExceptionUtils.logException(ex);
        }

        return obj.toString();
    }

    public static String toString(double d)
    {
        if (Double.isNaN(d))
            return "";

        if (d == (long) d)
        {
            return Long.toString((long) d);
        }
        else
        {
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setGroupingSize(0);
            return decimalFormat.format(d);
        }
    }

    public static String toString(float f)
    {
        if (Float.isNaN(f))
            return "";

        if (f == (long) f)
            return Long.toString((long) f);
        else
            return Float.toString(f);
    }

    /**
     * 判断一个类型是否为基本类型，基本类型包括数字，字符串，日期和布尔型
     *
     * @param c 要判断的类型
     * @return 基本类型返回true，非基本类型返回false
     */
    public static boolean isBaseType(Class c)
    {
        return c.isPrimitive() || c == Boolean.class || CharSequence.class.isAssignableFrom(c) ||
                Number.class.isAssignableFrom(c) || c == Character.class || Date.class.isAssignableFrom(c) ||
                Calendar.class.isAssignableFrom(c) || c.isEnum();
    }

    public static boolean isBaseType(Object obj)
    {
        return obj instanceof Number || obj instanceof CharSequence || obj instanceof Boolean || obj instanceof Date ||
                obj instanceof Calendar || obj instanceof Enum;
    }

    public static Format getFormat(Class c, final String formatString)
    {
        String factoryName;
        int index = formatString.indexOf("(");
        if (index > 0)
            factoryName = formatString.substring(0, index);
        else
            factoryName = formatString;

        FormatFactory factory;
        synchronized (factorys)
        {
            factory = factorys.get(factoryName);
        }

        if (factory != null)
        {
            String content = null;
            if (index > 0)
            {
                String s = formatString.substring(index + 1);
                index = s.indexOf(")");
                if (index >= 0)
                    content = s.substring(0, index);
            }
            else
            {
                content = "";
            }

            if (content != null)
            {
                Format format = factory.getFormat(c, content);

                if (format != null)
                    return format;
            }
        }

        if (byte[].class == c)
        {
            return new Format()
            {
                private static final long serialVersionUID = -8141102239704957773L;

                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
                {
                    try
                    {
                        toAppendTo.append(new String((byte[]) obj, formatString));
                    }
                    catch (UnsupportedEncodingException ex)
                    {
                        ExceptionUtils.wrapException(ex);
                    }
                    return toAppendTo;
                }

                @Override
                public Object parseObject(String source, ParsePosition pos)
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }
            };
        }
        if (Date.class.isAssignableFrom(c))
        {
            return DateUtils.getDateFormat(formatString);
        }
        else if (Number.class.isAssignableFrom(c))
        {
            if ("$".equals(formatString))
            {
                return NumberFormat.getCurrencyInstance(Locale.US);
            }
            else if ("￥".equals(formatString))
            {
                return NumberFormat.getCurrencyInstance(Locale.CHINA);
            }
            else if ("NT".equals(formatString))
            {
                return NumberFormat.getCurrencyInstance(Locale.TAIWAN);
            }
            else if ("money".equals(formatString) || "currency".equals(formatString))
            {
                return NumberFormat.getCurrencyInstance();
            }

            return new DecimalFormat(formatString);
        }

        return null;
    }

    public static String format(Object value, String formatString)
    {
        if (value == null)
            return "";

        if (StringUtils.isEmpty(formatString))
            return toString(value);

        if ("trim".equals(formatString))
            return toString(value).trim();

        Format format = getFormat(value.getClass(), formatString);

        if (format != null)
            return format.format(value);
        else
            return toString(value);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum> T getEnumValue(Class<T> c, int ordinal)
    {
        T[] values = getEnumValues(c);
        if (ordinal < 0 || ordinal >= values.length)
            return null;

        for (T value : values)
        {
            if (value.ordinal() == ordinal)
                return value;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum> T getEnumValue(Class<T> c, String s)
    {
        T[] values = getEnumValues(c);
        for (T value : values)
        {
            if (s.equals(getEnumString(value)))
                return value;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum> T[] getEnumValues(Class<T> c)
    {
        if (!c.isEnum())
            throw new ClassCastException(c.getName());

        try
        {
            synchronized (c)
            {
                T[] values;
                synchronized (ENUMVALUES)
                {
                    values = (T[]) ENUMVALUES.get(c);
                }

                if (values == null)
                {
                    values = c.getEnumConstants();

                    if (values.length > 0)
                    {
                        if (Comparable.class.isAssignableFrom(c))
                            Arrays.sort(values);
                        else if (Comparator.class.isAssignableFrom(c))
                            Arrays.sort(values, (Comparator<T>) values[0]);
                    }


                    synchronized (ENUMVALUES)
                    {
                        ENUMVALUES.put(c, values);
                    }
                }

                return values;
            }
        }
        catch (Exception ex)
        {
            //这里不会出错
            ExceptionUtils.wrapException(ex);
            return null;
        }
    }

    /**
     * 判断一个类型是否为数字类型
     *
     * @param c class
     * @return 是返回true，不是返回false
     */
    public static boolean isNumber(Class c)
    {
        return c.isPrimitive() && c != boolean.class && c != char.class || Number.class.isAssignableFrom(c);
    }

    /**
     * 判断一个类型是否为整数类型
     *
     * @param c class
     * @return 整数类型返回true，不是整数类型返回false
     */
    public static boolean isInteger(Class c)
    {
        return c == Long.class || c == long.class || c == Integer.class || c == int.class ||
                c == Short.class || c == short.class || c == Byte.class || c == byte.class || c == BigInteger.class;
    }

    /**
     * 判断一个类型是否为字符串类型
     *
     * @param c class
     * @return 字符串类型返回true，否则返回false
     */
    public static boolean isString(Class c)
    {
        return c == Character.class || CharSequence.class.isAssignableFrom(c) || c == char.class || c == char[].class;
    }

    /**
     * 判断一个类型是否为日期类型
     *
     * @param c class
     * @return 日期类型返回true，否则返回false
     */
    public static boolean isDate(Class c)
    {
        return Calendar.class.isAssignableFrom(c) || Date.class.isAssignableFrom(c);
    }

    /**
     * c2能否转化为c1
     *
     * @param c1 c1
     * @param c2 c2
     * @return 能转化返回true，否则返回false
     */
    @SuppressWarnings("unchecked")
    public static boolean isConvertable(Class c1, Class c2)
    {
        if (c1 == Object.class)
            return true;

        if (c1.isAssignableFrom(c2))
            return true;

        if (Convertable.class.isAssignableFrom(c2))
            return true;

        if (AdvancedEnum.class.isAssignableFrom(c1))
            c1 = getValueType(c1);

        if (AdvancedEnum.class.isAssignableFrom(c2))
            c2 = getValueType(c2);

        //任何类型均可转化为字符串
        if (String.class == c1)
            return true;

        if (Character.class == c1)
            return c2 == char.class;

        if (c1 == boolean.class || c1 == Boolean.class)
        {
            return c2 == boolean.class || c2 == Boolean.class || (c2.isPrimitive() && c2 != char.class) ||
                    Number.class.isAssignableFrom(c2);
        }

        if (c1.isPrimitive())
        {
            if (c1 == char.class)
                return c2 == Character.class;
            else
                return isNumber(c2) || c2.isEnum();
        }

        if (Number.class.isAssignableFrom(c1))
            return isNumber(c2) || c2.isEnum();

        if (Date.class.isAssignableFrom(c1))
            return Date.class.isAssignableFrom(c2);

        if (c1.isEnum())
            return String.class == c2 || isNumber(c2);

        return c1.isArray() && c1.getComponentType().isEnum() && String.class == c2;
    }

    public static int toInt(Object value)
    {
        if (value == null)
            return 0;

        if (value instanceof Number)
            return ((Number) value).intValue();

        if (value instanceof Enum)
            return ((Enum) value).ordinal();

        if (value instanceof Boolean)
            return ((Boolean) value) ? 1 : 0;

        if (value instanceof String)
            return Integer.parseInt((String) value);

        throw new NumberFormatException("cannot convert " + value.getClass().getName() + " to int");
    }

    public static byte toByte(Object value)
    {
        if (value == null)
            return 0;

        if (value instanceof Number)
            return ((Number) value).byteValue();

        if (value instanceof Enum)
            return (byte) ((Enum) value).ordinal();

        if (value instanceof Boolean)
            return (byte) (((Boolean) value) ? 1 : 0);

        if (value instanceof String)
            return Byte.parseByte((String) value);

        throw new NumberFormatException("cannot convert " + value.getClass().getName() + " to byte");
    }

    public static long toLong(Object value)
    {
        if (value == null)
            return 0;

        if (value instanceof Number)
            return ((Number) value).longValue();

        if (value instanceof Enum)
            return ((Enum) value).ordinal();

        if (value instanceof Boolean)
            return ((Boolean) value) ? 1 : 0;

        if (value instanceof String)
            return Long.parseLong((String) value);

        throw new NumberFormatException("cannot convert " + value.getClass().getName() + " to long");
    }

    public static short toShort(Object value)
    {
        if (value == null)
            return 0;

        if (value instanceof Number)
            return ((Number) value).shortValue();

        if (value instanceof Enum)
            return (short) ((Enum) value).ordinal();

        if (value instanceof Boolean)
            return (short) (((Boolean) value) ? 1 : 0);

        if (value instanceof String)
            return Short.parseShort((String) value);

        throw new NumberFormatException("cannot convert " + value.getClass().getName() + " to short");
    }

    public static float toFloat(Object value)
    {
        if (value == null)
            return 0;

        if (value instanceof Number)
            return ((Number) value).floatValue();

        if (value instanceof Enum)
            return ((Enum) value).ordinal();

        if (value instanceof Boolean)
            return ((Boolean) value) ? 1 : 0;

        if (value instanceof String)
            return Float.parseFloat((String) value);

        throw new NumberFormatException("cannot convert " + value.getClass().getName() + " to float");
    }

    public static double toDouble(Object value)
    {
        if (value == null)
            return 0;

        if (value instanceof Number)
            return ((Number) value).doubleValue();

        if (value instanceof Enum)
            return ((Enum) value).ordinal();

        if (value instanceof Boolean)
            return ((Boolean) value) ? 1 : 0;

        if (value instanceof String)
            return Double.parseDouble((String) value);

        throw new NumberFormatException("cannot convert " + value.getClass().getName() + " to double");
    }

    public static boolean toBoolean(Object value)
    {
        if (value == null)
            return false;

        if (value instanceof Boolean)
            return (Boolean) value;

        if (value instanceof String)
        {
            String s = ((String) value).trim();

            return !s.isEmpty() && !"false".equals(s) && !"0".equals(s);
        }

        return (!(value instanceof Number) || ((Number) value).doubleValue() != 0);
    }

    /**
     * 从字符串中解析最前面的整数，如5元解析为5
     *
     * @param s 要解析的字符串
     * @return 字符串中最前面的整数
     */
    public static Integer parseInt(String s)
    {
        int index = 0;
        int n = s.length();
        for (; index < n; index++)
        {
            char c = s.charAt(index);
            if (c < '0' || c > '9')
                break;
        }

        if (index == 0)
            return null;

        return Integer.valueOf(s.substring(0, index));
    }

    public static Float parseFloat(String s)
    {
        int index = 0;
        int n = s.length();
        for (; index < n; index++)
        {
            char c = s.charAt(index);
            if ((c < '0' || c > '9') && (c != '.'))
                break;
        }

        if (index == 0)
            return null;

        return Float.valueOf(s.substring(0, index));
    }

    public static Collator getCollator()
    {
        SoftReference<Collator> reference = COLLATOR_THREAD_LOCAL.get();

        Collator collator = null;
        if (reference != null)
        {
            collator = reference.get();
        }


        if (collator == null)
        {
            collator = Collator.getInstance();
            COLLATOR_THREAD_LOCAL.set(new SoftReference<Collator>(collator));
        }

        return collator;
    }

    @SuppressWarnings("unchecked")
    public static int compare(Object o1, Object o2)
    {
        if (o1 == o2)
            return 0;

        if (o1 == null)
            return -1;

        if (o2 == null)
            return 1;

        if (o1 instanceof byte[] && o2 instanceof byte[])
            return compare((byte[]) o1, (byte[]) o2);

        if (o1 instanceof Number && o2 instanceof Number)
            return Double.compare(((Number) o1).doubleValue(), ((Number) o2).doubleValue());

        if (o1 instanceof Date && o2 instanceof Date)
            return ((Date) o1).compareTo((Date) o2);

        if (o1 instanceof String && o2 instanceof String)
            return getCollator().compare((String) o1, (String) o2);

        if (o1 instanceof Comparable && o2 instanceof Comparable)
            return ((Comparable) o1).compareTo(o2);

        return getCollator().compare(o1.toString(), o2.toString());
    }

    public static int compare(byte[] bytes1, byte[] bytes2)
    {
        int len1 = bytes1.length;
        int len2 = bytes2.length;
        int n = Math.min(len1, len2);

        for (int i = 0; i < n; i++)
        {
            byte b1 = bytes1[i];
            byte b2 = bytes2[i];
            if (b1 != b2)
                return b1 - b2;
        }

        return len1 - len2;
    }

    public static boolean equal(Object o1, Object o2)
    {
        return o1 == o2 || o1 != null && o2 != null && o1.equals(o2);
    }

    public static boolean equal(double d1, double d2)
    {
        return Math.abs(d1 - d2) < 10e-12;
    }

    public static Object valueOf(Object obj)
    {
        if (obj == null)
        {
            return null;
        }
        else if (obj instanceof Value)
        {
            return ((Value) obj).valueOf();
        }
        else if (obj instanceof Object[])
        {
            Object[] objs = (Object[]) obj;
            if (objs.length == 0)
                return null;
            else
                return objs[0];
        }
        else if (obj instanceof Enum)
        {
            return ((Enum) obj).name();
        }
        else if (obj instanceof Map.Entry)
        {
            return ((Map.Entry) obj).getKey();
        }
        else if (DataConvert.isBaseType(obj))
        {
            return obj;
        }
        else if (obj instanceof Map && ((Map) obj).containsKey("value"))
        {
            return ((Map) obj).get("value");
        }

        List<ValueProvider> providers = valueProviders;
        if (providers != null)
        {
            for (ValueProvider provider : providers)
            {
                if (provider.accpet(obj))
                {
                    try
                    {
                        return provider.getValue(obj);
                    }
                    catch (Exception ex)
                    {
                        ExceptionUtils.wrapException(ex);
                    }
                }
            }
        }

        Method method;
        Class c = obj.getClass();
        synchronized (valueOfs)
        {
            if (valueOfs.containsKey(c))
            {
                method = valueOfs.get(c);
            }
            else
            {
                try
                {
                    method = c.getMethod("valueOf");
                    method.setAccessible(true);
                }
                catch (NoSuchMethodException ex)
                {
                    //不存在此方法
                    method = null;
                }
                catch (NoSuchFieldError ex)
                {
                    //不存在此方法
                    method = null;
                }

                valueOfs.put(c, method);
            }
        }

        if (method != null)
        {
            try
            {
                return method.invoke(obj);
            }
            catch (Exception ex)
            {
                ExceptionUtils.wrapException(ex);
            }
        }

        return obj;
    }

    public static String getValueString(Object obj)
    {
        if (obj == null)
            return "";

        if (obj instanceof Boolean)
            return obj.toString();
        else if (obj instanceof Enum)
            return ((Enum) obj).name();
        else if (obj instanceof Date)
            return DateUtils.toString((Date) obj);
        else if (obj instanceof Double)
        {
            return toString(((Double) obj).doubleValue());
        }
        else if (obj instanceof Float)
        {
            return toString(((Float) obj).floatValue());
        }
        else if (isBaseType(obj))
            return obj.toString();
        else if (obj instanceof char[])
            return new String((char[]) obj);
        else if (obj instanceof byte[])
        {
            try
            {
                return new String((byte[]) obj, "UTF-8");
            }
            catch (UnsupportedEncodingException ex)
            {
                //UTF-8编码不可能抛出异常
                ExceptionUtils.wrapException(ex);
                return null;
            }
        }
        else if (obj instanceof Color)
        {
            return HtmlUtils.encodeColor((Color) obj);
        }
        else
        {
            Object value = valueOf(obj);
            if (value == obj)
                return StringUtils.toString(value);
            else
                return getValueString(value);
        }
    }

    public static synchronized void addValueProvider(ValueProvider provider)
    {
        if (provider != null)
        {
            if (valueProviders == null)
            {
                valueProviders = Collections.singletonList(provider);
            }
            else
            {
                List<ValueProvider> providers = new ArrayList<ValueProvider>(valueProviders);
                providers.add(provider);
                valueProviders = providers;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static Object union(Object obj1, Object obj2) throws Exception
    {
        if (obj1 == null)
            return BeanUtils.cloneBean(obj2);
        else if (obj2 == null)
            return obj1;

        if (obj1 instanceof Collection && obj2 instanceof Collection)
        {
            Collection c = (Collection) obj1;
            for (Object obj : (Collection) obj2)
            {
                if (!c.contains(obj))
                    c.add(obj);
            }
        }
        else if (obj1 instanceof Map && obj2 instanceof Map)
        {
            Map map = (Map) obj1;
            for (Map.Entry entry : (Set<Map.Entry>) ((Map) obj2).entrySet())
            {
                Object value = map.get(entry.getKey());
                if (value == null)
                    map.put(entry.getKey(), entry.getValue());
                else
                    union(value, entry.getValue());
            }
        }
        else if (obj1 instanceof Unionable)
        {
            try
            {
                ((Unionable) obj1).union(obj2);
            }
            catch (ClassCastException ex)
            {
                //类转换异常，obj1不能union obj2，跳过
            }
        }

        return obj1;
    }

    public static Class<?> getWrapClass(Class<?> primitiveClass) throws Exception
    {
        if (primitiveClass == int.class)
            return Integer.class;

        if (primitiveClass == char.class)
            return Character.class;

        String name = primitiveClass.getName();

        return Class.forName("java.lang." + Character.toUpperCase(name.charAt(0)) + name.substring(1));
    }

    public static Collection toKeyValueList(Collection c) throws Exception
    {
        return toKeyValueList(c, null, null);
    }

    @SuppressWarnings("unchecked")
    public static Collection<KeyValue<String>> toKeyValueList(Collection c, String textExpression,
                                                              String valueExpression) throws Exception
    {
        int size = c.size();
        if (size > 0)
        {
            List<KeyValue<String>> list = null;
            for (Object obj : c)
            {
                if (list == null)
                {
                    //集合中的内容本身是KeyValue，直接返回
                    if (obj instanceof KeyValue)
                        return c;

                    list = new ArrayList<KeyValue<String>>();
                }

                String key;
                String value;

                if (obj instanceof Map.Entry)
                {
                    key = StringUtils.toStringWithNullToEmpty(((Map.Entry) obj).getKey());
                    value = StringUtils.toStringWithNullToEmpty(((Map.Entry) obj).getValue());
                }
                else
                {
                    if (valueExpression != null)
                        key = DataConvert.getValueString(BeanUtils.eval(valueExpression, obj, null));
                    else
                        key = DataConvert.getValueString(obj);

                    if (textExpression != null)
                        value = DataConvert.toString(BeanUtils.eval(textExpression, obj, null));
                    else
                        value = DataConvert.toString(obj);
                }

                list.add(new KeyValue<String>(key, value));
            }

            if (list != null)
                return list;
        }

        return c;
    }

    public static boolean isNumber(String s)
    {
        return NUM_PATTERN_.matcher(s).matches();
    }

    public static boolean isInteger(String s)
    {
        return INT_PATTERN_.matcher(s).matches();
    }

    public static boolean isPositiveNumber(String s)
    {
        return POSITIVE_NUM_PATTERN_.matcher(s).matches();
    }

    public static boolean isPositiveInteger(String s)
    {
        return POSITIVE_INT_PATTERN_.matcher(s).matches();
    }

    public static synchronized void addEnumValuesProviderFactory(EnumValuesProviderFactory factory)
    {
        if (factory != null)
        {
            List<EnumValuesProviderFactory> factorys =
                    new ArrayList<EnumValuesProviderFactory>(enumValuesProviderFactorys);
            factorys.add(factory);
            enumValuesProviderFactorys = factorys;
        }
    }

    private static <E extends AdvancedEnum> EnumValuesProvider<E> createDefaultEnumValuesProvider(Class<E> enumType)
    {
        List<EnumValuesProviderFactory> factorys = enumValuesProviderFactorys;
        for (EnumValuesProviderFactory factory : factorys)
        {
            EnumValuesProvider<E> provider = factory.create(enumType);
            if (provider != null)
                return provider;
        }

        return new ListEnumValuesProvider<E>(enumType, SystemConfig.getInstance());
    }

    @SuppressWarnings("unchecked")
    public static <E extends AdvancedEnum> EnumValuesProvider<E> getEnumValuesProvider(Class<E> enumType)
            throws Exception
    {
        EnumValuesProvider<E> provider = SystemConfig.getInstance().get(EnumValuesProvider.class, enumType.getName());
        if (provider == null)
        {
            synchronized (enumType)
            {
                synchronized (ENUMVALUESPROVIDERS)
                {
                    provider = ENUMVALUESPROVIDERS.get(enumType);
                }

                if (provider == null)
                {
                    provider = createDefaultEnumValuesProvider(enumType);

                    synchronized (ENUMVALUESPROVIDERS)
                    {
                        ENUMVALUESPROVIDERS.put(enumType, provider);
                    }
                }
            }
        }

        return provider;
    }

    @SuppressWarnings("unchecked")
    public static Class getValueType(Class<? extends Value> type)
    {
        synchronized (type)
        {
            Class valueType;
            synchronized (VALUETYPES)
            {
                valueType = VALUETYPES.get(type);
            }

            if (valueType == null)
            {
                try
                {
                    valueType = BeanUtils.toClass(BeanUtils.getRealType(Value.class, "V", type));
                }
                catch (UtilException ex)
                {
                    ExceptionUtils.wrapException(ex);
                }

                synchronized (ENUMVALUESPROVIDERS)
                {
                    VALUETYPES.put(type, valueType);
                }
            }

            return valueType;
        }
    }

    public static Object[] boxArray(Object array)
    {
        if (array instanceof byte[])
        {
            byte[] bytes = (byte[]) array;
            Byte[] result = new Byte[bytes.length];

            for (int i = 0; i < bytes.length; i++)
            {
                result[i] = bytes[i];
            }

            return result;
        }
        else if (array instanceof boolean[])
        {
            boolean[] bs = (boolean[]) array;
            Boolean[] result = new Boolean[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof char[])
        {
            char[] bs = (char[]) array;
            Character[] result = new Character[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof short[])
        {
            short[] bs = (short[]) array;
            Short[] result = new Short[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof int[])
        {
            int[] bs = (int[]) array;
            Integer[] result = new Integer[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof long[])
        {
            long[] bs = (long[]) array;
            Long[] result = new Long[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof float[])
        {
            float[] bs = (float[]) array;
            Float[] result = new Float[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof double[])
        {
            double[] bs = (double[]) array;
            Double[] result = new Double[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }

        return null;
    }

    public static Object unBoxArray(Object[] array)
    {
        if (array instanceof Byte[])
        {
            Byte[] bytes = (Byte[]) array;
            byte[] result = new byte[bytes.length];

            for (int i = 0; i < bytes.length; i++)
            {
                result[i] = bytes[i];
            }

            return result;
        }
        else if (array instanceof Boolean[])
        {
            Boolean[] bs = (Boolean[]) array;
            boolean[] result = new boolean[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof Character[])
        {
            Character[] bs = (Character[]) array;
            char[] result = new char[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof Short[])
        {
            Short[] bs = (Short[]) array;
            short[] result = new short[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof Integer[])
        {
            Integer[] bs = (Integer[]) array;
            int[] result = new int[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof Long[])
        {
            Long[] bs = (Long[]) array;
            long[] result = new long[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof Float[])
        {
            Float[] bs = (Float[]) array;
            float[] result = new float[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }
        else if (array instanceof Double[])
        {
            Double[] bs = (Double[]) array;
            double[] result = new double[bs.length];

            for (int i = 0; i < bs.length; i++)
            {
                result[i] = bs[i];
            }

            return result;
        }

        return null;
    }
}
