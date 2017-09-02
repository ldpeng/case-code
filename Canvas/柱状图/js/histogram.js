var Histogram = function(option){
	this._init(option);
}
Histogram.prototype = {
	_init: function(option){
		this.x = option.x;
		this.y = option.y;
		this.data = option.data;
		//最大的宽度
		this.width = option.width;
		//最大的高度
		this.height = option.height;
		//添加一个新的group
		this.group = new Konva.Group({
			x: this.x,
			y: this.y
		});
		//添加一条线
		var line = new Konva.Line({
			points: [0, 0, this.width , 0],
		    stroke: 'red',
		    strokeWidth: 2,
		    lineCap: 'round',
		    lineJoin: 'round'
		});
		this.group.add(line);
		//得到每个数据所在线段的宽
		var dataWidth = this.width / this.data.length;
		for(var i = 0 ; i<this.data.length; i++){
			//画矩形
			var rect = new Konva.Rect({
				x: (i + 1 / 4) * dataWidth,
				y: -this.data[i].value * this.height,
				width: 1 / 2 * dataWidth,
				height: this.data[i].value * this.height,
				fill: this.data[i].color,
				name: 'rect'
			});
			this.group.add(rect);
			//画上面的文本
			var upText = new Konva.Text({
				x: i * dataWidth,
				y: -this.data[i].value * this.height - 20,
				width: dataWidth,
				fill: this.data[i].color,
				align: "center",
				text: this.data[i].value * 100 + "%",
				name: 'upText'
			});
			this.group.add(upText);
			//下面的文本
			var downText = new Konva.Text({
				x: (i + 1/2) * dataWidth,
				y: 10,
				fill: this.data[i].color,
				align: "center",
				text: this.data[i].name,
				rotation: 30
			});
			this.group.add(downText);
		}
	},
	addToGroupOrLayer: function(GroupOrLayer){
		GroupOrLayer.add(this.group);
	},
	moveTo: function(){
		//得到group中所有的矩形
			var rectArr =  this.group.find(".rect");//
			//得到柱状图的原始高
			rectArr.forEach(function(item,index){
				var oldH = item.height();
				item.height(0);
				var oldY = item.y();
				item.y(0);
				item.to({
					y: oldY,
					height: oldH,
					duration: 1
				});
			});
			var upTextArr =  this.group.find(".upText");//
			//得到柱状图的原始高
			upTextArr.forEach(function(item,index){
				var oldY = item.y();
				item.y( -item.fontSize());
				item.to({
					y: oldY,
					duration: 1
				});
			});
	}
}