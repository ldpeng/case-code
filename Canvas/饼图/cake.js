var Cake = function(option){
	this._init(option);
}
Cake.prototype = {
	_init: function(option){
		this.data = option.data;
		this.innerR = option.innerR;
		this.outerR = option.outerR;
		this.outerColor = option.outerColor;
		this.x = option.x;
		this.y = option.y;
		this.outerStroke = option.outerStroke;
		this.group = new Konva.Group({
			x: this.x,
			y: this.y
		});
		//画外部的圆
		var circle = new Konva.Circle({
			x: 0,
		    y: 0,
		    radius: this.outerR,
		    stroke: this.outerColor,
		    strokeWidth: this.outerStroke
		});
		this.group.add(circle);
		//画扇形
		var textAngle = 0;//文字书写的角度
		var endAngle = -90;//圆弧结束的角度
		var _this = this;
		this.data.forEach(function(item,index){
			var arc = new Konva.Arc({
		      x: 0,
		      y: 0,
		      outerRadius: _this.innerR,
		      angle: item.value * 360,
		      fill: item.color,
		      rotation: endAngle,
		      name: 'arc'
		    });
		    textAngle = endAngle + 1 / 2 * item.value * 360;
		     var text = new Konva.Text({
			      x:  (_this.outerR + 20) * Math.cos(textAngle * Math.PI / 180),
			      y:  (_this.outerR + 20) * Math.sin(textAngle * Math.PI / 180),
			      text: item.value * 100 + "%",
			      fontSize: 20,
			      fill: item.color,
			      align: 'left'
		    });
		    if(textAngle> 90 && textAngle < 270){
		    	text.x(text.x() - text.width());
		    }
		    endAngle += item.value *360;
		    _this.group.add(text);
		    _this.group.add(arc);
		}); 
	},
	addToGroupOrLayer: function(groupOrLayer){
		groupOrLayer.add(this.group);
	},
	moveTo: function(){
		this.group.find(".arc").forEach(function(item,index){
			var oldAngle = item.angle();
			item.angle(0);
			item.to({
				angle: oldAngle,
				duration: 2
			});
		});
	}	
}