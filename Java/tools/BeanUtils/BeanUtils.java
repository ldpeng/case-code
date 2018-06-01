import com.gzzm.lwyd.common.Function;
import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.commons.util.Filter;

import java.beans.*;
import java.lang.reflect.Constructor;
import java.util.Collection;

/**
 * @author LDP
 * @date 2015/12/10
 */
public class BeanUtils {
    public BeanUtils() {
    }

    /**
     * 对象属性拷贝
     *
     * @param src    元数据对象
     * @param target 目标对象
     * @throws IntrospectionException 内省期间异常
     */
    public static void copyBeanProperty(Object src, Object target) throws IntrospectionException {
        copyBeanProperty(src, target, null, false);
    }

    /**
     * 对象属性拷贝
     *
     * @param src    元数据对象
     * @param target 目标对象
     * @param filter 属性类型过滤器
     * @throws IntrospectionException 内省期间异常
     */
    public static void copyBeanProperty(Object src, Object target, Filter<PropertyDescriptor> filter) throws IntrospectionException {
        copyBeanProperty(src, target, filter, false);
    }

    /**
     * 对象属性拷贝
     *
     * @param src        元数据对象
     * @param target     目标对象
     * @param expNullVal 是否排除空值
     * @throws IntrospectionException 内省期间异常
     */
    public static void copyBeanProperty(Object src, Object target, boolean expNullVal) throws IntrospectionException {
        copyBeanProperty(src, target, null, expNullVal);
    }

    /**
     * 对象属性拷贝
     *
     * @param src        元数据对象
     * @param target     目标对象
     * @param filter     属性类型过滤器
     * @param expNullVal 是否排除空值
     * @throws IntrospectionException 内省期间异常
     */
    public static void copyBeanProperty(Object src, Object target, Filter<PropertyDescriptor> filter, boolean expNullVal)
            throws IntrospectionException {
        if (src == null || target == null) return;

        BeanInfo beanInfo = Introspector.getBeanInfo(src.getClass());
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor pd : pds) {
            try {
                Object val = pd.getReadMethod().invoke(src);

                //排除空值
                if (expNullVal && val == null) continue;

                if (filter == null || filter.accept(pd)) {
                    PropertyDescriptor pd1 = new PropertyDescriptor(pd.getName(), target.getClass());
                    if (pd1 != null) {
                        pd1.getWriteMethod().invoke(target, val);
                    }
                }
            } catch (IntrospectionException e) {//目标对象没有对应的属性
            } catch (Exception e) {
                Tools.log(e);
            }
        }
    }

    /**
     * 将集合中的数据转换成指定类型的集合数据
     *
     * @param collection
     * @param targetType
     */
    public static <S, T> Collection<T> convertCollectionData(Collection<S> collection, Class<T> targetType, Function<S, T> function) throws Exception {
        if (CollectionUtils.isEmpty(collection) || targetType == null || function == null) return null;

        Collection<T> targetCollection = CollectionUtils.createCollection(collection.getClass(), collection.size());
        for (S s : collection) {
            targetCollection.add(function.run(s));
        }

        return targetCollection;
    }

    /**
     * 根据构造函数转换，即目标类型存在只接收源类型的构造函数
     *
     * @param collection
     * @param targetType
     */
    public static <S, T> Collection<T> convertByConstructor(Collection<S> collection, final Class<T> targetType) throws Exception {
        return convertCollectionData(collection, targetType, new Function<S, T>() {
            @Override
            public T run(S src) {
                if (src == null) return null;

                try {
                    // src可能是代理对象，需要获取实际的类型
                    Class c = net.cyan.commons.util.BeanUtils.getRealClass(src.getClass());
                    Constructor constructor = targetType.getConstructor(c);
                    return (T) constructor.newInstance(src);
                } catch (Exception e) {
                    Tools.log(e);
                    return null;
                }
            }
        });
    }
}
