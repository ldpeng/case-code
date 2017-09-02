var Group = function(option){
  this._init(option);
}
Group.prototype = {
  _init: function(option){
    //圆心的坐标
    this.x = option.x || 0;//作用：用来检查前面的数据是否为true，如果为true,那么就返回前面的数据，如果不为就返回0
    this.y = option.y || 0;
    this.innerRadius = option.innerRadius || 0;
    this.outerRadius = option.outerRadius || 0;
    this.innerFill = option.innerFill || "red";
    this.outerFill = option.outerFill || "blue";
    this.outerOpacity = option.outerOpacity || 1;
    this.text = option.text || "";
    this.fontSize = option.fontSize || 20;
    this.textColor = option.textColor || "white";
    //隐式转换的过程Boolean    哪 些在进行转换的时候会转为false , 0,null,undefined,"",NaN
  },
  render: function(){
    this.group = new Konva.Group({
      x: this.x,
      y: this.y
    });
    //内圆
    var innerCircle = new Konva.Circle({
      x: 0,
      y: 0,
      radius: this.innerRadius,
      fill: this.innerFill
    });
    this.group.add(innerCircle);
    //外圆
    var outerCircle = new Konva.Ring({
      x: 0,
      y: 0,
      innerRadius: this.innerRadius,
      outerRadius: this.outerRadius,
      fill: this.outerFill,
      opacity: this.outerOpacity
    });
    this.group.add(outerCircle);
    //添加文字
    var text = new Konva.Text({
      x: - this.innerRadius,
      y: - this.fontSize / 2,
      text: this.text,
      width: this.innerRadius * 2,
      align: "center",
      fill: this.textColor
    });
    this.group.add(text);
  },
  addToGroupOrLayer: function(GroupOrLayer){
    GroupOrLayer.add(this.group);
  }
}