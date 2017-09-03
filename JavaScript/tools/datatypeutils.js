/**
 * 判断变量是不是数值型
 * isFinite() 函数用于检查其参数是否是无穷大
 * 如果 number 是有限数字（或可转换为有限数字），那么返回 true。
 * 如果 number 是 NaN（非数字），或者是正、负无穷大的数，则返回 false
 */
function isNumber(val)
{
    return typeof val === 'number' && isFinite(val);
}

/**
 * 判断变量是不是布尔类型
 */
function isBooleanType(val)
{
    return typeof val === "boolean";
}

/**
 * 判断变量是不是字符串类型
 */
function isStringType(val)
{
    return typeof val === "string";
}

/**
 * 判断变量是不是Undefined
 */
function isUndefined(val)
{
    return typeof val === "undefined";
}

/**
 * 判断变量是不是对象
 */
function isObj(str)
{
    if (str === null || typeof str === 'undefined')
    {
        return false;
    }
    return typeof str === 'object';
}

/**
 * 判断变量是不是null
 */
function isNull(val)
{
    return val === null;
}

/**
 * 判断变量是不是数组
 * 数组类型不可用typeof来判断。因为当变量是数组类型是，typeof会返回object
 * 也可以这样：Object.prototype.toString.call(arr) === '[object Array]'
 */
function isArray(arr)
{
    if (arr === null || typeof arr === 'undefined')
    {
        return false;
    }
    return arr.constructor === Array;
}