// 把所有的回调全部放到这个对象上
// window.callbacks = {}; // 解决污染全局作用域的问题

// jsonp 和 xhr 没有关系
function crossDomain(url, params, fn) {
  // 0. 处理回调函数挂载问题(不能覆盖)
  var cbName = `jsonp_${(Math.random()*Math.random()).toString().substr(2)}`;
  // console.log(cbName);
  window[cbName] = function (data) {
    fn(data);
    // 不断创建标签，最终可能太多，可以在这个脚本执行完成过后移除
    //
    document.body.removeChild(scriptElement);
  };
  // 1. 组合最终请求的url地址
  // 将params转换为 {key1:val, key2:val} => key1=val&key2=val
  var querystring = '';
  for (var key in params) {
    querystring += `${key}=${params[key]}&`;
  }
  // 告诉服务端我的回调叫什么
  querystring += `callback=${cbName}`;
  url = `${url}?${querystring}`;
  // 2. 创建一个script标签，并将src设置为url地址
  var scriptElement = document.createElement('script');
  scriptElement.src = url;
  // 3. appendChild(执行)
  document.body.appendChild(scriptElement);
}

// JSONP只能做GET请求，原因是JSONP背后是script实现的
// GET 请求 提交的数据有限

// CORS
// 被跨域的站点设置响应头  Access-Control-Allow-Origin
