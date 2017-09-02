var Schedule = function(option){
	this._init(option);
}
Schedule.prototype = {
	_init: function(option){
		//外部的矩形的坐标，宽，高，边框颜色，圆角
		//内部的矩形的，宽，填充色，圆角
		this.stage = option.stage;
		this.x = option.x || 0;
		this.y = option.y || 0;
		this.innerW = option.innerW || 0;
		this.h = option.h || 0;
		this.fill = option.fill || "blue";
		this.innerCornerRadius = option.innerCornerRadius || 0;
		this.outerW = option.outerW || 0;
		this.outerCornerRadius = option.outerCornerRadius || 0;
		this.stroke = option.stroke || "red";
		this.text = option.text;
		this.fontSize = option.fontSize;
		this.group = new Konva.Group({
			x: this.x,
			y: this.y
		});
		//开始绘制矩形
		this.outerRect = new Konva.Rect({
			x: 0,
			y: 0,
			width: this.outerW,
			height: this.h,
			stroke: this.stroke,
			cornerRadius : this.outerCornerRadius
		});
		this.group.add(this.outerRect);
		this.innerRect = new Konva.Rect({
			x: 0,
			y: 0,
			width: this.innerW,
			height: this.h,
			fill: this.fill,
			cornerRadius : this.innerCornerRadius
		});
		this.group.add(this.innerRect);

		this.text = new Konva.Text({
			  x: 0,
		      y: -this.fontSize - 4,
		      text: this.text,
		      fontSize: this.fontSize,
		      fontFamily: 'Calibri',
		      width: this.outerW,
		      fill: '#555',
		      align: 'center'
		});
		this.group.add(this.text);
	},
	addToGroupOrLayer: function(layerOrGroup){
		layerOrGroup.add(this.group);
	},
	changeVal: function(val){//val是当前进度条的进度  1  .1   10  100
		if(val > 1){
			val /= 100;
		}
		var _this = this;
		this.innerRect.to({
			width: val * _this.outerW,
			durtaion: 2,
		});
		this.text.text("努力加载中：" + val * 100 + "%");
	}
}