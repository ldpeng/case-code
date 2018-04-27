import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Filter;

import java.beans.*;

/**
 * @author LDP
 * @date 2015/12/10
 */
public class BeanUtils
{
    public BeanUtils()
    {
    }

    /**
     * 对象属性拷贝
     *
     * @param src    元数据对象
     * @param target 目标对象
     * @throws IntrospectionException 内省期间异常
     */
    public static void copyBeanProperty(Object src, Object target) throws IntrospectionException
    {
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
    public static void copyBeanProperty(Object src, Object target, Filter<PropertyDescriptor> filter) throws IntrospectionException
    {
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
    public static void copyBeanProperty(Object src, Object target, boolean expNullVal) throws IntrospectionException
    {
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
            throws IntrospectionException
    {
        if(src == null || target == null) return;

        BeanInfo beanInfo = Introspector.getBeanInfo(src.getClass());
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor pd : pds)
        {
            try
            {
                Object val = pd.getReadMethod().invoke(src);

                //排除空值
                if(expNullVal && val == null) continue;

                if(filter == null || filter.accept(pd)))
                {
                    PropertyDescriptor pd1 = new PropertyDescriptor(pd.getName(), target.getClass());
                    if(pd1 != null)
                    {
                        pd1.getWriteMethod().invoke(target, val);
                    }
                }
            }
            catch (IntrospectionException e)
            {//目标对象没有对应的属性
            }
            catch (Exception e)
            {
                Tools.log(e);
            }
        }
    }
}
