package net.cyan.commons.util;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

/**
 * 集合的基本操作
 *
 * @author camel
 * @date 2008-4-9
 */
public final class CollectionUtils
{
    public static class EmptySortedSet<E> extends AbstractSet<E> implements Serializable, SortedSet<E>
    {
        private static final long serialVersionUID = -1769525914440249560L;

        private Comparator<? super E> comparator;

        public EmptySortedSet(Comparator<? super E> comparator)
        {
            this.comparator = comparator;
        }

        @Override
        public Iterator<E> iterator()
        {
            return emptyIterator();
        }

        @Override
        public int size()
        {
            return 0;
        }

        @Override
        public Comparator<? super E> comparator()
        {
            return comparator;
        }

        @Override
        public SortedSet<E> subSet(Object fromElement, Object toElement)
        {
            return this;
        }

        @Override
        public SortedSet<E> headSet(Object toElement)
        {
            return this;
        }

        @Override
        public SortedSet<E> tailSet(Object fromElement)
        {
            return this;
        }

        @Override
        public E first()
        {
            throw new NoSuchElementException();
        }

        @Override
        public E last()
        {
            throw new NoSuchElementException();
        }
    }

    /**
     * 简单的Map，key必须为String，并只有get功能
     *
     * @author camel
     * @date 2008-3-11
     */
    public abstract static class SimpleMap implements Map<String, Object>
    {
        public SimpleMap()
        {
        }

        public int size()
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.SimpleMap.size not yet implemented.");
        }

        public boolean containsValue(Object value)
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.SimpleMap.containsValue not yet implemented.");
        }

        public Object remove(Object key)
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.SimpleMap.remove not yet implemented.");
        }

        public void putAll(Map<? extends String, ?> t)
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.SimpleMap.putAll not yet implemented.");
        }

        public void clear()
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.SimpleMap.clear not yet implemented.");
        }

        public Set<String> keySet()
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.SimpleMap.keySet not yet implemented.");
        }

        public Collection<Object> values()
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.SimpleMap.values not yet implemented.");
        }

        public Set<Entry<String, Object>> entrySet()
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.SimpleMap.entrySet not yet implemented.");
        }
    }

    public interface ObjectProvider
    {
        Object getIterator(Object obj);
    }

    /**
     * 有默认值的Map
     *
     * @author camel
     * @date 2008-3-16
     */
    @SuppressWarnings("UnusedDeclaration")
    public abstract static class DefaultValueMap<K, V>
    {
        private final Map<K, V> map = new HashMap<K, V>();

        private boolean syn = true;

        private SynchronizedObjects<K> syns;

        public DefaultValueMap()
        {
            syns = new SynchronizedObjects<K>();
        }

        public DefaultValueMap(boolean syn)
        {
            this.syn = syn;
            if (syn)
                syns = new SynchronizedObjects<K>();
        }

        public V get(K key)
        {
            if (syn)
            {
                synchronized (syns.get(key))
                {
                    V obj;
                    synchronized (map)
                    {
                        obj = map.get(key);
                    }

                    if (obj == null)
                    {
                        obj = getDefaultValue(key);
                        synchronized (map)
                        {
                            map.put(key, obj);
                        }
                    }

                    return obj;
                }
            }
            else
            {
                V obj = map.get(key);
                if (obj == null)
                {
                    obj = getDefaultValue(key);
                    map.put(key, obj);
                }

                return obj;
            }
        }

        public void clear()
        {
            if (syn)
            {
                synchronized (map)
                {
                    map.clear();
                }
            }
            else
            {
                map.clear();
            }
        }

        public abstract V getDefaultValue(K key);
    }

    /**
     * 忽略大小写的Map
     *
     * @author camel
     * @date 2008-5-16
     */
    public static class IgnoreCaseMap<T> extends HashMap<String, T>
    {
        private static final long serialVersionUID = 837830839382355286L;

        public IgnoreCaseMap()
        {
        }

        public T get(Object key)
        {
            return super.get(key == null ? null : key.toString().toUpperCase());
        }

        public boolean containsKey(Object key)
        {
            return super.containsKey(key == null ? null : key.toString().toUpperCase());
        }

        public T put(String key, T value)
        {
            return super.put(key == null ? null : key.toUpperCase(), value);
        }

        public T remove(Object key)
        {
            return super.remove(key == null ? null : key.toString().toUpperCase());
        }

        public void putAll(Map<? extends String, ? extends T> map)
        {
            for (Map.Entry<? extends String, ? extends T> entry : map.entrySet())
                put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 忽略大小写的Map
     *
     * @author camel
     * @date 2008-5-16
     */
    public static class IgnoreIdentityCaseMap<T> extends HashMap<String, T>
    {
        private static final long serialVersionUID = 837830839382355286L;

        public IgnoreIdentityCaseMap()
        {
        }

        public T get(Object key)
        {
            if (key instanceof String)
            {
                if (StringUtils.isIdentifier((String) key))
                    key = ((String) key).toUpperCase();
            }

            return super.get(key);
        }

        public boolean containsKey(Object key)
        {
            if (key instanceof String)
            {
                if (StringUtils.isIdentifier((String) key))
                    key = ((String) key).toUpperCase();
            }

            return super.containsKey(key);
        }

        public T put(String key, T value)
        {
            if (key != null && StringUtils.isIdentifier(key))
                key = key.toUpperCase();

            return super.put(key, value);
        }

        public T remove(Object key)
        {
            if (key instanceof String)
            {
                if (StringUtils.isIdentifier((String) key))
                    key = ((String) key).toUpperCase();
            }

            return super.remove(key == null ? null : key);
        }

        public void putAll(Map<? extends String, ? extends T> map)
        {
            for (Map.Entry<? extends String, ? extends T> entry : map.entrySet())
                put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 将对象转化为Map
     *
     * @author camel
     * @date 2008-6-4
     */
    public static class ObjectMap extends SimpleMap
    {
        /**
         * 对象
         */
        protected Object obj;

        private Class type;

        /**
         * 上下文，包括一些额外的属性和缓存加载过的数据
         */
        protected Map<String, Object> context;

        /**
         * 保存加载过的属性
         */
        private Map<String, Method> getters;

        public ObjectMap(Object obj, Map<String, Object> context)
        {
            this.obj = obj;
            this.type = obj.getClass();
            this.context = context;
        }

        public ObjectMap(Object obj)
        {
            this.obj = obj;
            this.type = obj.getClass();
            if (!(obj instanceof Map))
                this.context = new HashMap<String, Object>();
        }

        public boolean isEmpty()
        {
            return false;
        }

        public boolean containsKey(Object key)
        {
            if (obj instanceof Map)
                return ((Map) obj).containsKey(key);
            else
                return context.containsKey(key) || getGetter((String) key) != null;
        }

        public Object get(Object key)
        {
            if (obj instanceof Map)
                return ((Map) obj).get(key);

            if (context.containsKey(key))
                return context.get(key);

            Method getter = getGetter((String) key);
            Object value = null;
            if (getter != null)
            {
                try
                {
                    value = getter.invoke(obj);
                }
                catch (Exception ex)
                {
                    //获取属性出现异常，将异常抛出
                    ExceptionUtils.wrapException(ex);
                }
            }

            context.put((String) key, value);
            return value;
        }

        @SuppressWarnings("unchecked")
        public Object put(String key, Object value)
        {
            if (obj instanceof Map)
                return ((Map) obj).put(key, value);

            return context.put(key, value);
        }

        private Method getGetter(String name)
        {
            if (getters == null)
                getters = new HashMap<String, Method>();
            else if (getters.containsKey(name))
                return getters.get(name);

            Method getter = null;
            try
            {
                getter = BeanUtils.getGetter(type, name);
                getter.setAccessible(true);
            }
            catch (NoSuchMethodException ex)
            {
                //属性不存在，返回空
            }

            getters.put(name, getter);
            return getter;
        }
    }

    public static class EvalMap extends SimpleMap
    {
        private Object obj;

        private Map context;

        /**
         * 缓存计算过的表达式
         */
        private Map<String, Object> cache;

        public EvalMap(Object obj, Map context)
        {
            this.obj = obj;
            this.context = context;
        }

        public EvalMap(Object obj)
        {
            this.obj = obj;
        }

        public boolean isEmpty()
        {
            return false;
        }

        public boolean containsKey(Object key)
        {
            return true;
        }

        @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
        public Object get(Object key)
        {
            if ("self".equals(key))
                return obj;

            if (context != null && context.containsKey(key))
                return context.get(key);

            if (cache != null && cache.containsKey(key))
                return cache.get(key);

            try
            {
                Integer.parseInt((String) key);
                return null;
            }
            catch (NumberFormatException ex)
            {
                //不是，数字，继续计算表达式
            }

            try
            {
                String s = (String) key;
                Object value = BeanUtils.eval(s, obj, context);
                if (cache == null)
                    cache = new HashMap<String, Object>();
                cache.put(s, value);
                return value;
            }
            catch (UtilException ex)
            {
                ExceptionUtils.wrapException(ex);
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        public Object put(String key, Object value)
        {
            if (context != null)
            {
                return context.put(key, value);
            }
            else
            {
                if (cache == null)
                    cache = new HashMap<String, Object>();
                return cache.put(key, value);
            }
        }

        public void clearCache()
        {
            if (cache != null)
                cache.clear();
        }
    }

    /**
     * 数组迭代
     *
     * @author camel
     * @date 2008-3-30
     */
    private static class ArrayIterator<E> implements Iterator<E>
    {
        private Object array;

        private int index;

        private int length;

        public ArrayIterator(Object array)
        {
            this.array = array;
            this.length = Array.getLength(array);
        }

        public boolean hasNext()
        {
            return index < length;
        }

        @SuppressWarnings("unchecked")
        public E next()
        {
            return (E) Array.get(array, index++);
        }

        public void remove()
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.ArrayIterator.remove not yet implemented.");
        }
    }

    private static class ArraySet<E> implements Set<E>
    {
        private E[] array;

        private ArraySet(E[] array)
        {
            this.array = array;
        }

        public int size()
        {
            return array.length;
        }

        public boolean isEmpty()
        {
            return array.length == 0;
        }

        public boolean contains(Object o)
        {
            for (Object obj : array)
            {
                if (obj == null)
                {
                    if (o == null)
                        return true;
                }
                else if (o != null && obj.equals(o))
                {
                    return true;
                }
            }

            return false;
        }

        public Iterator<E> iterator()
        {
            return new Iterator<E>()
            {
                private int index;

                public boolean hasNext()
                {
                    return index < array.length;
                }

                public E next()
                {
                    return array[index++];
                }

                public void remove()
                {
                    throw new UnsupportedOperationException("Method .remove not yet implemented.");
                }
            };
        }

        public Object[] toArray()
        {
            return array.clone();
        }

        @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
        public <T> T[] toArray(T[] a)
        {
            int size = size();
            if (a.length < size)
                return Arrays.copyOf(array, size, (Class<? extends T[]>) a.getClass());
            System.arraycopy(array, 0, a, 0, size);
            if (a.length > size)
                a[size] = null;
            return a;
        }

        public boolean add(E e)
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.ArraySet.add not yet implemented.");
        }

        public boolean remove(Object o)
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.ArraySet.remove not yet implemented.");
        }

        public boolean containsAll(Collection<?> c)
        {
            for (Object o : c)
            {
                if (!contains(o))
                    return false;
            }
            return true;
        }

        public boolean addAll(Collection<? extends E> c)
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.ArraySet.addAll not yet implemented.");
        }

        public boolean retainAll(Collection<?> c)
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.ArraySet.retainAll not yet implemented.");
        }

        public boolean removeAll(Collection<?> c)
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.ArraySet.removeAll not yet implemented.");
        }

        public void clear()
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.ArraySet.clear not yet implemented.");
        }
    }

    private static class ArrayMap<E> implements Map<Object, E>
    {
        private E[] array;

        private int startIndex;

        private Set<Object> keySet;

        private Set<Entry<Object, E>> entrySet;

        private ArrayMap(E[] array, int startIndex)
        {
            this.array = array;
            this.startIndex = startIndex;
        }

        @SuppressWarnings("unchecked")
        public Set<Entry<Object, E>> entrySet()
        {
            if (entrySet == null)
            {
                Entry<Object, E>[] entrys = new Entry[array.length];
                for (int i = 0; i < array.length; i++)
                {
                    final int index = i;
                    entrys[i] = new Entry<Object, E>()
                    {
                        public Object getKey()
                        {
                            return index + startIndex;
                        }

                        public E getValue()
                        {
                            return array[index];
                        }

                        public E setValue(E value)
                        {
                            E old = array[index];
                            array[index] = value;
                            return old;
                        }
                    };
                }
                entrySet = new ArraySet<Entry<Object, E>>(entrys);
            }
            return entrySet;
        }

        public boolean isEmpty()
        {
            return array.length == 0;
        }

        public E get(Object key)
        {
            int index = startIndex - 1;
            if (key instanceof Number)
            {
                index = ((Number) key).intValue();
            }
            else if (key instanceof String)
            {
                try
                {
                    index = Integer.parseInt((String) key);
                }
                catch (NumberFormatException ex)
                {
                    //解析数字错误，没有此键，跳过
                }
            }

            index -= startIndex;

            return (index >= 0 && index < array.length) ? array[index] : null;
        }

        public E put(Object key, E value)
        {
            int index = startIndex - 1;
            if (key instanceof Number)
            {
                index = ((Number) key).intValue();
            }
            else if (key instanceof String)
            {
                try
                {
                    index = Integer.parseInt((String) key);
                }
                catch (NumberFormatException ex)
                {
                    //解析数字错误，没有此键，跳过
                }
            }

            index -= startIndex;

            if (index >= 0 && index < array.length)
            {
                E old = array[index];
                array[index] = value;

                return old;
            }
            else
            {
                return null;
            }
        }

        public boolean containsValue(Object value)
        {
            for (Object obj : array)
            {
                if (obj == null)
                {
                    if (value == null)
                        return true;
                }
                else if (value != null && obj.equals(value))
                {
                    return true;
                }
            }

            return false;
        }

        public boolean containsKey(Object key)
        {
            int index = startIndex - 1;
            if (key instanceof Number)
            {
                index = ((Number) key).intValue();
            }
            else if (key instanceof String)
            {
                try
                {
                    index = Integer.parseInt((String) key);
                }
                catch (NumberFormatException ex)
                {
                    //解析数字错误，没有此键，跳过
                }
            }

            index -= startIndex;

            return index >= 0 && index < array.length;
        }

        public int size()
        {
            return array.length;
        }

        public E remove(Object key)
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.ArrayMap.remove not yet implemented.");
        }

        public void clear()
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.ArrayMap.clear not yet implemented.");
        }

        public Set<Object> keySet()
        {
            if (keySet == null)
            {
                keySet = new Set<Object>()
                {
                    public Iterator<Object> iterator()
                    {
                        return new Iterator<Object>()
                        {
                            private int index = 0;

                            public boolean hasNext()
                            {
                                return index < array.length;
                            }

                            public Object next()
                            {
                                return index++;
                            }

                            public void remove()
                            {
                                throw new UnsupportedOperationException("Method .remove not yet implemented.");
                            }
                        };
                    }

                    public Object[] toArray()
                    {
                        Integer[] keys = new Integer[array.length];
                        for (int i = 0; i < array.length; i++)
                            keys[i] = i;
                        return keys;
                    }

                    @SuppressWarnings("unchecked")
                    public <T> T[] toArray(T[] a)
                    {
                        if (a.length < array.length || a.getClass() != (Class) Integer[].class)
                            return (T[]) toArray();

                        for (int i = 0; i < array.length; i++)
                            a[i] = (T) new Integer(i);
                        if (a.length > array.length)
                            a[array.length] = null;
                        return a;
                    }

                    public boolean containsAll(Collection<?> c)
                    {
                        for (Object o : c)
                        {
                            if (!ArrayMap.this.containsKey(o))
                                return false;
                        }
                        return true;
                    }

                    public boolean addAll(Collection<?> c)
                    {
                        throw new UnsupportedOperationException("Method .addAll not yet implemented.");
                    }

                    public int size()
                    {
                        return array.length;
                    }

                    public boolean isEmpty()
                    {
                        return array.length > 0;
                    }

                    public boolean contains(Object o)
                    {
                        return ArrayMap.this.containsKey(o);
                    }

                    public boolean add(Object o)
                    {
                        throw new UnsupportedOperationException("Method .add not yet implemented.");
                    }

                    public boolean removeAll(Collection<?> c)
                    {
                        throw new UnsupportedOperationException("Method .removeAll not yet implemented.");
                    }

                    public boolean remove(Object o)
                    {
                        throw new UnsupportedOperationException("Method .remove not yet implemented.");
                    }

                    public boolean retainAll(Collection<?> c)
                    {
                        throw new UnsupportedOperationException("Method .retainAll not yet implemented.");
                    }

                    public void clear()
                    {
                        throw new UnsupportedOperationException("Method .clear not yet implemented.");
                    }
                };
            }

            return keySet;
        }

        @SuppressWarnings("unchecked")
        public Collection<E> values()
        {
            return Arrays.asList(array);
        }

        public void putAll(Map<?, ? extends E> m)
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.ArrayMap.putAll not yet implemented.");
        }
    }

    /**
     * @author camel
     * @date 2008-3-11
     */
    private static class ArraysList<E> extends AbstractList<E>
    {
        private Object array;

        public ArraysList(Object array)
        {
            this.array = array;
        }

        @SuppressWarnings("unchecked")
        public E get(int index)
        {
            return (E) Array.get(array, index);
        }

        public int size()
        {
            return Array.getLength(array);
        }

        public E set(int index, E element)
        {
            E old = get(index);
            Array.set(array, index, element);

            return old;
        }
    }

    private static class IteratorCollection<E> extends AbstractCollection<E>
    {
        private Iterator<E> iterator;

        private boolean loaded;

        public IteratorCollection(Iterator<E> iterator)
        {
            this.iterator = iterator;
        }

        public Iterator<E> iterator()
        {
            if (loaded)
                throw new IllegalStateException();

            loaded = true;

            return iterator;
        }

        public int size()
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.IteratorCollection.size not yet implemented.");
        }
    }

    private static class IterableCollection<E> extends AbstractCollection<E>
    {
        private Iterable<E> iterable;

        public IterableCollection(Iterable<E> iterable)
        {
            this.iterable = iterable;
        }

        public Iterator<E> iterator()
        {
            return iterable.iterator();
        }

        public int size()
        {
            throw new UnsupportedOperationException(
                    "Method net.cyan.commons.util.CollectionUtils.IteratorCollection.size not yet implemented.");
        }
    }

    private static class TableIterator<E> implements Iterator<List<E>>
    {
        private Iterator<E> iterator;

        private int col;

        private TableIterator(Iterator<E> iterator, int col)
        {
            this.iterator = iterator;
            this.col = col;
        }

        public boolean hasNext()
        {
            return iterator.hasNext();
        }

        public List<E> next()
        {
            if (!iterator.hasNext())
                throw new NoSuchElementException();

            List<E> list = new ArrayList<E>(col);
            for (int i = 0; i < col; i++)
            {
                if (iterator.hasNext())
                    list.add(iterator.next());
                else
                    list.add(null);
            }
            return list;
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Method not implemented.");
        }
    }

    public static final Iterator EMPTY_ITERATOR = new Iterator()
    {
        public boolean hasNext()
        {
            return false;
        }

        public Object next()
        {
            throw new NoSuchElementException();
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Method not implemented.");
        }
    };

    private static final List<Boolean> BOOLEANS = Arrays.asList(Boolean.FALSE, Boolean.TRUE);

    private CollectionUtils()
    {
    }

    public static <E> List<E> asList(Object array)
    {
        return new ArraysList<E>(array);
    }

    public static <E> Iterator<E> asIterator(Object array)
    {
        return new ArrayIterator<E>(array);
    }

    public static <E> Set<E> asSet(E... array)
    {
        return new ArraySet<E>(array);
    }

    @SuppressWarnings("unchecked")
    public static <E> Map<Object, E> asMap(int startIndex, E... array)
    {
        return array == null || array.length == 0 ? (Map<Object, E>) Collections.EMPTY_MAP :
                new ArrayMap<E>(array, startIndex);
    }

    public static <E> Iterable<E> toIterable(final Iterator<E> iterator)
    {
        return new Iterable<E>()
        {
            private boolean loaded;

            public Iterator<E> iterator()
            {
                if (loaded)
                    throw new IllegalStateException();

                loaded = true;

                return iterator;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <E> Iterator<E> getIterator(Object obj)
    {
        if (obj != null)
        {
            if (obj instanceof Iterable)
                return ((Iterable) obj).iterator();
            else if (obj instanceof Map)
                return ((Map) obj).entrySet().iterator();
            else if (obj.getClass().isArray())
                return new ArrayIterator<E>(obj);
            else if (obj instanceof Iterator)
                return (Iterator) obj;
            else if (obj instanceof Class)
            {
                Class c = (Class) obj;
                if (c == boolean.class || c == Boolean.class)
                {
                    return (Iterator<E>) BOOLEANS.iterator();
                }
                else if (c.isEnum())
                {
                    return new ArrayIterator<E>(DataConvert.getEnumValues(c));
                }
                else if (AdvancedEnum.class.isAssignableFrom(c))
                {
                    try
                    {
                        return (Iterator<E>) DataConvert.getEnumValuesProvider((Class<? extends AdvancedEnum>) c)
                                .getEnumObjects().iterator();
                    }
                    catch (Exception ex)
                    {
                        ExceptionUtils.wrapException(ex);
                    }
                }
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <E> Collection<E> getCollection(Object obj)
    {
        if (obj != null)
        {
            if (obj instanceof Collection)
                return (Collection<E>) obj;
            else if (obj instanceof Map)
                return ((Map) obj).entrySet();
            else if (obj.getClass().isArray())
                return new ArraysList<E>(obj);
            else if (obj instanceof Iterator)
                return new IteratorCollection((Iterator) obj);
            else if (obj instanceof Iterable)
                return new IterableCollection((Iterable) obj);
            else if (obj instanceof Class)
            {
                Class c = (Class) obj;
                if (c == boolean.class || c == Boolean.class)
                {
                    return (List<E>) BOOLEANS;
                }
                else if (c.isEnum())
                {
                    return (List<E>) Arrays.asList(DataConvert.getEnumValues(c));
                }
                else
                {
                    try
                    {
                        return (List<E>) DataConvert.getEnumValuesProvider((Class<? extends AdvancedEnum>) c)
                                .getEnumObjects();
                    }
                    catch (Exception ex)
                    {
                        ExceptionUtils.wrapException(ex);
                    }
                }
            }
        }

        return null;
    }

    /**
     * 比较两个集合是否相同
     *
     * @param c1 集合1
     * @param c2 集合2
     * @return 相同返回true，不相同返回false
     */
    public static boolean equals(Collection c1, Collection c2)
    {
        if (c1.size() != c2.size())
            return false;

        Iterator iterator = c2.iterator();
        for (Object obj : c1)
        {
            Object obj2 = iterator.next();
            if (obj == null && obj2 != null)
                return false;

            if (obj2 == null && obj != null)
                return false;

            if (obj != null && !obj.equals(obj2))
                return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] union(T[]... arrays)
    {
        return (T[]) unionArray((Object[]) arrays);
    }

    /**
     * 合并数组
     *
     * @param arrays 数组列表
     * @return 合并后的数组
     */
    @SuppressWarnings({"SuspiciousSystemArraycopy", "unchecked"})
    public static Object unionArray(Object... arrays)
    {
        int n = arrays.length;
        int size = 0;
        int[] sizes = new int[n];
        int index = -1;
        Class type = null;
        for (int i = 0; i < n; i++)
        {
            Object array = arrays[i];
            if (array != null)
            {
                int m = Array.getLength(array);

                sizes[i] = m;
                size += m;

                Class c = array.getClass().getComponentType();
                if (type == null)
                {
                    type = c;
                }
                else if (c.isAssignableFrom(type))
                {
                    type = c;
                    index = -2;
                }
                else if (!type.isAssignableFrom(c))
                {
                    type = Object.class;
                    index = -2;
                }

                if (m > 0)
                {
                    if (index == -1)
                        index = i;
                    else
                        index = -2;
                }
            }
        }

        if (size == 0)
            return null;

        if (index > 0)
            return arrays[index];

        Object result = Array.newInstance(type, size);
        size = 0;
        for (int i = 0; i < n; i++)
        {
            int m = sizes[i];
            if (m > 0)
            {
                System.arraycopy(arrays[i], 0, result, size, m);
                size += m;
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static Collection createCollection(Type type) throws Exception
    {
        Collection collection = null;

        Class c = BeanUtils.toClass(type);

        if (c == EnumSet.class)
            collection = EnumSet.noneOf((Class<? extends Enum>) BeanUtils.getCollectionElementType(type));
        else if (BeanUtils.isRealClass(c))
            collection = (Collection) c.newInstance();
        else if (c == Set.class)
            collection = new HashSet();
        else if (c == SortedSet.class)
            collection = new TreeSet();
        else if (c == Queue.class || c == Deque.class)
            collection = new ArrayDeque();
        else if (c == Collection.class || c == List.class)
            collection = new ArrayList();

        return collection;
    }

    @SuppressWarnings("unchecked")
    public static Collection createCollection(Type type, Collection c0) throws Exception
    {
        Collection collection = null;

        Class c = BeanUtils.toClass(type);

        if (BeanUtils.isRealClass(c))
        {
            try
            {
                Constructor constructor = c.getConstructor(Collection.class);
                collection = (Collection) constructor.newInstance(c0);
            }
            catch (NoSuchMethodException e)
            {
                //没有构造函数，尝试使用默认构造函数
                collection = (Collection) c.newInstance();
                collection.addAll(c0);
            }
        }
        else if (c == Set.class)
        {
            collection = new HashSet(c0);
        }
        else if (c == SortedSet.class)
        {
            collection = new TreeSet(c0);
        }
        else if (c == Queue.class || c == Deque.class)
        {
            collection = new ArrayDeque(c0);
        }
        else if (c == Collection.class || c == List.class)
        {
            collection = new ArrayList(c0);
        }

        return collection;
    }

    @SuppressWarnings("unchecked")
    public static Collection createCollection(Type type, int size) throws Exception
    {
        Collection collection = null;

        Class c = BeanUtils.toClass(type);

        if (BeanUtils.isRealClass(c))
        {
            try
            {
                Constructor constructor = c.getConstructor(int.class);
                collection = (Collection) constructor.newInstance(size);
            }
            catch (NoSuchMethodException e)
            {
                //没有构造函数，尝试使用默认构造函数
                collection = (Collection) c.newInstance();
            }
        }
        else if (c == Set.class)
        {
            collection = new HashSet(size);
        }
        else if (c == SortedSet.class)
        {
            collection = new TreeSet();
        }
        else if (c == Queue.class || c == Deque.class)
        {
            collection = new ArrayDeque(size);
        }
        else if (c == Collection.class || c == List.class)
        {
            collection = new ArrayList(size);
        }

        return collection;
    }

    public static Map createMap(Class<? extends Map> type) throws Exception
    {
        Map map = null;
        if (!type.isInterface())
            map = type.newInstance();
        else if (type == SortedMap.class)
            map = new TreeMap();
        else if (type == Map.class)
            map = new HashMap();

        return map;
    }

    public static <E> Iterator<E> singletonIterator(final E o)
    {
        return new Iterator<E>()
        {
            private E obj = o;

            public boolean hasNext()
            {
                return obj != null;
            }

            public E next()
            {
                E o = obj;
                obj = null;
                return o;
            }

            public void remove()
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <E> Iterator<E> flattenIterator(final Object obj, final ObjectProvider provider)
    {
        return new Iterator()
        {
            Deque<Iterator> stack = new LinkedList<Iterator>();

            {
                Iterator iterator = getIterator(obj);
                if (iterator == null)
                    iterator = singletonIterator(obj);

                stack.push(iterator);
            }

            private Object current;

            public boolean hasNext()
            {
                while (stack.size() > 0)
                {
                    if (stack.peek().hasNext())
                        return true;

                    stack.pop();
                }

                return false;
            }

            public Object next()
            {
                if (hasNext())
                {
                    getNext();
                    return current;
                }
                else
                {
                    throw new IndexOutOfBoundsException();
                }
            }

            private boolean getNext(Object obj)
            {
                if (provider != null)
                    obj = provider.getIterator(obj);

                Iterator iterator = getIterator(obj);

                if (iterator == null)
                {
                    current = obj;
                    return false;
                }
                else
                {
                    if (iterator.hasNext())
                    {
                        stack.push(iterator);
                        return true;
                    }
                    else
                    {
                        current = null;
                        return false;
                    }
                }
            }

            private void getNext()
            {
                if (getNext(stack.peek().next()))
                    getNext();
            }

            public void remove()
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <E> Iterable<E> flattenIterable(final Object obj, final ObjectProvider provider)
    {
        return new Iterable()
        {
            public Iterator iterator()
            {
                return flattenIterator(obj, provider);
            }
        };
    }

    public static <E> Set<E> unmodifiableSet(final Collection<E> c)
    {
        if (c == null)
            throw new NullPointerException();

        return new Set<E>()
        {
            public int size()
            {
                return c.size();
            }

            public boolean isEmpty()
            {
                return c.isEmpty();
            }

            public boolean contains(Object o)
            {
                return c.contains(o);
            }

            public Object[] toArray()
            {
                return c.toArray();
            }

            @SuppressWarnings("SuspiciousToArrayCall")
            public <T> T[] toArray(T[] a)
            {
                return c.toArray(a);
            }

            public String toString()
            {
                return c.toString();
            }

            public Iterator<E> iterator()
            {
                return new Iterator<E>()
                {
                    Iterator<? extends E> i = c.iterator();

                    public boolean hasNext()
                    {
                        return i.hasNext();
                    }

                    public E next()
                    {
                        return i.next();
                    }

                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            public boolean add(E e)
            {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object o)
            {
                throw new UnsupportedOperationException();
            }

            public boolean containsAll(Collection<?> coll)
            {
                return c.containsAll(coll);
            }

            public boolean addAll(Collection<? extends E> coll)
            {
                throw new UnsupportedOperationException();
            }

            public boolean removeAll(Collection<?> coll)
            {
                throw new UnsupportedOperationException();
            }

            public boolean retainAll(Collection<?> coll)
            {
                throw new UnsupportedOperationException();
            }

            public void clear()
            {
                throw new UnsupportedOperationException();
            }

            public boolean equals(Object o)
            {
                return o == this || c.equals(o);
            }

            public int hashCode()
            {
                return c.hashCode();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <E> TableIterator<E> asTable(Object obj, int col)
    {
        Iterator<E> iterator = obj == null ? EMPTY_ITERATOR : getIterator(obj);
        if (iterator == null)
            throw new UtilRuntimeException("cannot convert " + obj.getClass().getName() + " to iterator");
        return new TableIterator<E>(iterator, col);
    }

    @SuppressWarnings("unchecked")
    public static <E> Iterator<E> emptyIterator()
    {
        return EMPTY_ITERATOR;
    }

    public static <E> Enumeration<E> getEnumeration(final Iterator<E> iterator)
    {
        return new Enumeration<E>()
        {
            public boolean hasMoreElements()
            {
                return iterator.hasNext();
            }

            public E nextElement()
            {
                return iterator.next();
            }
        };
    }

    /**
     * 判断两个集合是否相交
     *
     * @param c1 集合1
     * @param c2 集合2
     * @return 相交返回true，不相交返回false
     */
    public static boolean isIntersectant(Collection c1, Collection c2)
    {
        for (Object obj : c1)
        {
            if (c2.contains(obj))
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static void union(Collection c1, Collection c2)
    {
        for (Object obj : c2)
        {
            if (!c1.contains(obj))
                c1.add(obj);
        }
    }

    public static List<Integer> range(final int start, final int end)
    {
        return range(start, end, 1);
    }

    public static List<Integer> range(final int start, final int end, final int step)
    {
        final int size = start <= end ? (end - start) / step + 1 : 0;

        return new List<Integer>()
        {
            public int size()
            {
                return size;
            }

            public boolean isEmpty()
            {
                return end < start;
            }

            public boolean contains(Object o)
            {
                if (o instanceof Integer)
                {
                    int i = (Integer) o;

                    if (step > 0)
                    {
                        if (i >= start && i <= end && (i - start) % step == 0)
                            return true;
                    }
                    else
                    {
                        if (i >= end && i <= start && (i - start) % step == 0)
                            return true;
                    }
                }

                return false;
            }

            public Iterator<Integer> iterator()
            {
                return listIterator();
            }

            public Object[] toArray()
            {
                Object[] array = new Object[size];
                for (int i = 0; i < size; i++)
                {
                    array[i] = start + size * step;
                }

                return array;
            }

            @SuppressWarnings("unchecked")
            public <T> T[] toArray(T[] a)
            {
                if (a.length < size)
                {
                    Class<?> componentType = a.getClass().getComponentType();
                    a = (T[]) Array.newInstance(componentType, size);
                }

                for (int i = 0; i < size; i++)
                {
                    a[i] = (T) (new Integer(start + size * step));
                }

                return a;
            }

            public boolean add(Integer integer)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public boolean remove(Object o)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public boolean containsAll(Collection<?> c)
            {
                for (Object o : c)
                {
                    if (!contains(o))
                        return false;
                }

                return true;
            }

            public boolean addAll(Collection<? extends Integer> c)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public boolean addAll(int index, Collection<? extends Integer> c)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public boolean removeAll(Collection<?> c)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public boolean retainAll(Collection<?> c)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public void clear()
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public Integer get(int index)
            {
                if (index >= size)
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);

                return start + index * step;
            }

            public Integer set(int index, Integer element)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public void add(int index, Integer element)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public Integer remove(int index)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public int indexOf(Object o)
            {
                if (contains(o))
                {
                    return (((Integer) o) - start) / step;
                }

                return -1;
            }

            public int lastIndexOf(Object o)
            {
                return indexOf(o);
            }

            public ListIterator<Integer> listIterator()
            {
                return stepIterator(start, end, step);
            }

            public ListIterator<Integer> listIterator(int index)
            {
                if (index < 0)
                    throw new IndexOutOfBoundsException("index: " + index);

                return stepIterator(start + index * step, end, step);
            }

            public List<Integer> subList(int fromIndex, int toIndex)
            {
                if (fromIndex < 0)
                    throw new IndexOutOfBoundsException("fromIndex: " + fromIndex);

                if (toIndex >= size)
                    throw new IndexOutOfBoundsException("toIndex: " + toIndex + ", Size: " + size);

                if (fromIndex > toIndex)
                    throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");

                return range(start + step * fromIndex, start + step * toIndex, step);
            }

            @Override
            public String toString()
            {
                return "Range(" + start + "," + end + "," + step + ")";
            }
        };
    }

    public static ListIterator<Integer> stepIterator(final int start, final int end, final int step)
    {
        return new ListIterator<Integer>()
        {
            private int value = start;

            public boolean hasNext()
            {
                if (step > 0)
                    return value <= end;
                else
                    return value >= end;
            }

            public Integer next()
            {
                if (step > 0)
                {
                    if (value > end)
                        throw new NoSuchElementException();
                }
                else
                {
                    if (value < end)
                        throw new NoSuchElementException();
                }

                int value = this.value;

                this.value += step;

                return value;
            }

            public void remove()
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public boolean hasPrevious()
            {
                if (step > 0)
                    return value > start;
                else
                    return value < start;
            }

            public Integer previous()
            {
                if (step > 0)
                {
                    if (value <= start)
                        throw new NoSuchElementException();
                }
                else
                {
                    if (value >= start)
                        throw new NoSuchElementException();
                }

                value -= step;

                return value;
            }

            public int nextIndex()
            {
                return (value - start) / step;
            }

            public int previousIndex()
            {
                return (value - start) / step - 1;
            }

            public void set(Integer integer)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public void add(Integer integer)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }
        };
    }

    public static ListIterator<Integer> stepIterator(int start, int end)
    {
        return stepIterator(start, end, 1);
    }

    public static Iterable<Integer> stepIterable(final int start, final int end, final int step)
    {
        return range(start, end, step);
    }

    public static Iterable<Integer> stepIterable(final int start, final int end)
    {
        return stepIterable(start, end, 1);
    }

    public static boolean isEmpty(Collection c)
    {
        if (c == null)
            return true;

        try
        {
            if (c.isEmpty())
            {
                return true;
            }
        }
        catch (NoSuchMethodError ex)
        {
            //方法不存在，此类的本版不支持此方法，使用下面的size = 0;

            if (c.size() == 0)
                return true;
        }

        return false;
    }

    public static boolean isNotEmpty(Collection c)
    {
        return !isEmpty(c);
    }

    public static boolean isEmpty(Map map)
    {
        if (map == null)
            return true;

        try
        {
            if (map.isEmpty())
            {
                return true;
            }
        }
        catch (NoSuchMethodError ex)
        {
            //方法不存在，此类的本版不支持此方法，使用下面的size = 0;

            if (map.size() == 0)
                return true;
        }

        return false;
    }

    public static boolean isNotEmpty(Map map)
    {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Object[] array)
    {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array)
    {
        return !isEmpty(array);
    }

    public static boolean isEmpty(boolean[] array)
    {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(boolean[] array)
    {
        return !isEmpty(array);
    }

    public static boolean isEmpty(char[] array)
    {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(char[] array)
    {
        return !isEmpty(array);
    }

    public static boolean isEmpty(byte[] array)
    {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(byte[] array)
    {
        return !isEmpty(array);
    }

    public static boolean isEmpty(short[] array)
    {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(short[] array)
    {
        return !isEmpty(array);
    }

    public static boolean isEmpty(int[] array)
    {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(int[] array)
    {
        return !isEmpty(array);
    }

    public static boolean isEmpty(long[] array)
    {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(long[] array)
    {
        return !isEmpty(array);
    }

    public static boolean isEmpty(float[] array)
    {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(float[] array)
    {
        return !isEmpty(array);
    }

    public static boolean isEmpty(double[] array)
    {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(double[] array)
    {
        return !isEmpty(array);
    }

    public static <E> void sortByField(List<E> list, Class<E> type, final String field, final boolean desc)
            throws Exception
    {
        if (BeanUtils.isSimpleVariable(field))
        {
            final PropertyInfo propertyInfo = BeanUtils.getProperty(type, field);

            Collections.sort(list, new Comparator<E>()
            {
                public int compare(E o1, E o2)
                {
                    try
                    {
                        Object v1 = propertyInfo.get(o1);
                        Object v2 = propertyInfo.get(o2);

                        return desc ? DataConvert.compare(v2, v1) : DataConvert.compare(v1, v2);
                    }
                    catch (Exception ex)
                    {
                        ExceptionUtils.wrapException(ex);
                        return 0;
                    }
                }
            });
        }
        else
        {
            Collections.sort(list, new Comparator<E>()
            {
                public int compare(E o1, E o2)
                {
                    try
                    {
                        Object v1 = BeanUtils.eval(field, o1, null);
                        Object v2 = BeanUtils.eval(field, o2, null);

                        return desc ? DataConvert.compare(v2, v1) : DataConvert.compare(v1, v2);
                    }
                    catch (Exception ex)
                    {
                        ExceptionUtils.wrapException(ex);
                        return 0;
                    }
                }
            });
        }
    }

    public static int indexOf(Object[] array, Object e)
    {
        if (array == null)
            return -1;

        int n = array.length;
        for (int i = 0; i < n; i++)
        {
            if (DataConvert.equal(array[i], e))
                return i;
        }

        return -1;
    }

    public static boolean contains(Object[] array, Object e)
    {
        return indexOf(array, e) != -1;
    }

    public static void reverse(byte[] array)
    {
        int n = array.length / 2;
        for (int i = 0; i < n; i++)
        {
            int j = array.length - i - 1;

            byte x = array[i];
            array[i] = array[j];
            array[j] = x;
        }
    }

    public static void reverse(short[] array)
    {
        int n = array.length / 2;
        for (int i = 0; i < n; i++)
        {
            int j = array.length - i - 1;

            short x = array[i];
            array[i] = array[j];
            array[j] = x;
        }
    }

    public static void reverse(int[] array)
    {
        int n = array.length / 2;
        for (int i = 0; i < n; i++)
        {
            int j = array.length - i - 1;

            int x = array[i];
            array[i] = array[j];
            array[j] = x;
        }
    }

    public static void reverse(long[] array)
    {
        int n = array.length / 2;
        for (int i = 0; i < n; i++)
        {
            int j = array.length - i - 1;

            long x = array[i];
            array[i] = array[j];
            array[j] = x;
        }
    }

    public static void reverse(float[] array)
    {
        int n = array.length / 2;
        for (int i = 0; i < n; i++)
        {
            int j = array.length - i - 1;

            float x = array[i];
            array[i] = array[j];
            array[j] = x;
        }
    }

    public static void reverse(double[] array)
    {
        int n = array.length / 2;
        for (int i = 0; i < n; i++)
        {
            int j = array.length - i - 1;

            double x = array[i];
            array[i] = array[j];
            array[j] = x;
        }
    }

    public static void reverse(boolean[] array)
    {
        int n = array.length / 2;
        for (int i = 0; i < n; i++)
        {
            int j = array.length - i - 1;

            boolean x = array[i];
            array[i] = array[j];
            array[j] = x;
        }
    }

    public static void reverse(char[] array)
    {
        int n = array.length / 2;
        for (int i = 0; i < n; i++)
        {
            int j = array.length - i - 1;

            char x = array[i];
            array[i] = array[j];
            array[j] = x;
        }
    }

    @SuppressWarnings({"SuspiciousSystemArraycopy", "unchecked"})
    public static <E> E[] toArray(Object[] src, E[] target)
    {
        if (src == null)
            return target;

        if (target.length >= src.length)
        {
            System.arraycopy(src, 0, target, 0, src.length);
            return target;
        }
        else
        {
            if (target.getClass() == src.getClass())
                return (E[]) src;

            return (E[]) Arrays.copyOf(src, src.length, target.getClass());
        }
    }
}