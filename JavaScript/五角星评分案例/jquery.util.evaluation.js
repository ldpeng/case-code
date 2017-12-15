(function ($) {

    $.evaluation = function (el, options) {
        var settings = $.extend({
            count: 5,   //评价级数
            size: 20,   //字体大小
            color: "#ffc002", //字体颜色
            emptyText: "☆",  //空白时文本，可以是一个图片路径，或者一个数组，存储每一个级别的内容
            fillText: "★",   //填充时的文本，可以是一个图片路径，或者一个数组，存储每一个级别的内容
            defaultVal: 0,  //默认级数
            clickable: true //是否可点击进行评价
        }, options || {});

        el = isString(el) ? $("#" + el) : $(el);

        //构建评价html
        var ul = $("<ul>").css({
            listStyle: "none",
            fontSize: settings.size + "px",
            color: settings.color
        });

        if (settings.clickable) ul.css("cursor", "pointer");

        for (var i = 0; i < settings.count; i++) {
            var html = evaluationHtml(settings.emptyText, i);
            if (html) {
                ul.append($("<li>", {evaluationVal: i + 1}).css("float", "left").html(html));
            }
        }
        el.append(ul);

        el.setEvaluationVal = function (val) {
            ul.find("li").each(function () {
                var v = $(this).attr("evaluationVal");
                if (v < val) {
                    $(this).html(evaluationHtml(settings.fillText, v - 1));
                } else if (v == val) {
                    $(this).html(evaluationHtml(settings.fillText, v - 1));
                    $(this).addClass('clicked').siblings().removeClass('clicked');
                } else {
                    $(this).html(evaluationHtml(settings.emptyText, v - 1));
                }
            });
        };

        el.evaluationVal = function () {
            return ul.find(".clicked").attr("evaluationVal") || 0;
        };

        if (settings.clickable) {
            ul.find("li").mouseenter(function () {
                //鼠标移入: 自己和前面的兄弟变实心，其余变空心
                $(this).html(evaluationHtml(settings.fillText, $(this).attr("evaluationVal") - 1));
                $(this).prevAll().each(function () {
                    $(this).html(evaluationHtml(settings.fillText, $(this).attr("evaluationVal") - 1));
                });
                $(this).nextAll().each(function () {
                    $(this).html(evaluationHtml(settings.emptyText, $(this).attr("evaluationVal") - 1));
                });
            }).click(function () {
                //鼠标点击后，把自己添加clicked类，其余的清除clicked类
                $(this).addClass('clicked').siblings().removeClass('clicked');
            });

            //当鼠标移开评分控件时，实心显示到被点击的五角星的上
            ul.mouseleave(function () {
                el.setEvaluationVal(el.evaluationVal());
            });
        }

        if(settings.defaultVal && settings.defaultVal > 0) el.setEvaluationVal(settings.defaultVal);

        return el;
    };

    function evaluationHtml(text, i) {
        if (isString(text)) {
            //length > 3判断为图片路径
            if (text.length > 3) return "<img src='" + text + "'>";
            else return text;
        } else if (isArray(text)) {
            if (isString(text[i])) {
                if (text[i].length > 3) return "<img src='" + text[i] + "'>";
                else return text[i];
            }
        }
    }

    function isString(str) {
        return typeof str === "string";
    }

    function isArray(arr) {
        return ({}).toString.call(arr) === '[object Array]';
    }
})(jQuery);
