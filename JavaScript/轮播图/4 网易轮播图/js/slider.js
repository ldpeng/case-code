/**
 * Created by andy on 2015/12/12.
 */
//  获取我们要的任何元素
var js_slider = document.getElementById("js_slider");  // 最大的盒子
var slide_block = js_slider.children[0].children[0];   // 获取的 block 盒子
var imgs = slide_block.children;  // 所有的运动动画图片
var slide_ctrl = js_slider.children[1];   // ctrl
// 1. 先动态生成  6个小span  要遍历循环
for(var i=0;i<imgs.length;i++) {
    // 创建新的节点
    var span = document.createElement("span");  // 生成span
    span.className = "slider-ctrl-con";
    span.innerHTML = imgs.length - i;        //  1   2  3       6-0     6- 1  5  4  32 1
    // 因为 insert 是倒序的方式插入的   所以这里用  6 减去 索引号   倒序的倒序是正值
    //a.insertBefore(b,c);  把  b 放到 c 的前面 ，但是 a 是父级
    slide_ctrl.insertBefore(span,slide_ctrl.children[1]);
}
// 2. 让我们第一个 span 按钮 变成 蓝色
  slide_ctrl.children[1].className = "slider-ctrl-con current";

// 3 第一张图片在舞台中央  ，其余的全部在 右侧    left ： 310
var  sliderWidth = js_slider.offsetWidth;   //  得到宽度是 310
// alert(sliderWidth)
for(var i=1;i<imgs.length;i++) {
    // 为什么从1开始  第0张是不动的   从 第 1 开始
    imgs[i].style.left = sliderWidth + "px";
}

// 4. 开始点击三个按钮

var spans = slide_ctrl.children;   // 得到了所的  8 个 span
var iNow = 0;  // 控制整个盒子程序的实现
for(var k in spans) {    // 遍历数组 spans    k   索引号     spans[k]   里面的值
    spans[k].onclick = function() {
        if(this.className == "slider-ctrl-prev") {
            // 用来判断是否是左侧按钮
            // alert("您点击了左侧按钮")
            animate(imgs[iNow],{left: sliderWidth});
            --iNow < 0  ? iNow = imgs.length - 1 : iNow;
            imgs[iNow].style.left = - sliderWidth + "px";
            animate(imgs[iNow],{left: 0});
            square();
        } else if (this.className == "slider-ctrl-next") {
             autoplay();
        }
        else {
             //  alert("您点击的是 下面的span")
             // 第一步， 先得到当前的索引号
             var that = this.innerHTML - 1;
             //  alert(that);
             // 当我们点击了  当前图片 相应span 的  后面的 span 的时候
             // 此时 ，我们的 图片应该从 右边走向左边   相当于点击了 右侧按钮
            if(that > iNow) {  // iNow  当前图片
                // 当前这一张 先出去
                animate(imgs[iNow],{left: - sliderWidth});
                // 需要的那张， 先跑到右侧
                imgs[that].style.left = sliderWidth + "px";
            }
            else if(that < iNow) {
                //  小于的情况我们按照点击左侧按钮的方法
                animate(imgs[iNow],{left:  sliderWidth});
                imgs[that].style.left = -sliderWidth + "px";
            }
            iNow = that;   // 把 我们需要的那一张， 改为 当前的图片  赋值
            animate(imgs[iNow],{left: 0});
            square();
        }
    }
    // 特别重要的变量 iNow
    function square() {   // 主要用来控制 下面的span  样式
      // 排他思想
       //  去掉所有不合格的，留下当前的
        for(var i=1;i<spans.length-1;i++) {  //  只遍历 6个
           // 从1 开始 去掉了  0   一共是 8个  减去1   到 7  不带7玩      1~6   一共是6 个数
            spans[i].className = "slider-ctrl-con";
        }
        // iNow 是 0-5  但是我们需要  1-6 需要
        spans[iNow+1].className = "slider-ctrl-con current";
    }
}
 // 5  开启定时器
var timer = null;
timer = setInterval(autoplay,2000);
function autoplay() {
    // 用来判断是否是右侧按钮
    // alert("您点击了右侧按钮");
    //当我们点击了右侧按钮， 当前的这个图片，慢慢的走到了左侧，  -310 的位置
    animate(imgs[iNow],{left: - sliderWidth});
    /* iNow++;  // xian ++
     console.log(iNow);
     if(iNow > imgs.length - 1) // 后判断
     {
     iNow = 0;
     }*/
    ++iNow > imgs.length - 1 ? iNow = 0 : iNow;
    imgs[iNow].style.left = sliderWidth + "px";
    // 当前的下一张，先快速的走到右侧
    // 再慢慢的走到舞台中央
    animate(imgs[iNow],{left: 0});
    square();
}

// 鼠标经过大盒子 就停止定时器
js_slider.onmouseover = function() {
    clearInterval(timer);
}
js_slider.onmouseout = function() {
    timer = setInterval(autoplay,2000);
}
















