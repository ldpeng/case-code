/**
 * 过滤器，用于过滤某些元素，当accept方法返回true时表示能接受此元素，否则表示不接受
 *
 * @author camel
 * @date 2010-7-8
 */
public interface Filter<O>
{
    public boolean accept(O o) throws Exception;
}
