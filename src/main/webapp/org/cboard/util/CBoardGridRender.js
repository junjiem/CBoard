var CBoardGridRender = function (jqContainer, options) {
    this.container = jqContainer; // jquery object
    this.options = options;
    this.tall;
    var _this = this;
};

CBoardGridRender.prototype.do = function (tall, persist, gridConfig) {
    this.tall = tall;
    tall = _.isUndefined(tall) ? 500 : tall;
    var divHeight = tall - 110;
    var _this = this;
    var render = function (o) {
        _this.options = o;
        _this.do(_this.tall);
    };

    new agGrid.Grid(this.container[0], this.options);
    if(gridConfig){
        this.options.columnApi.setColumnState(gridConfig);
    }

    $(this.container).css({
        height: tall + "px"
    });
    $(this.container).addClass("ag-theme-fresh")
    $("#preview_widget").css("text-align", "left");

    if (persist) {
        persist.data = this.options.data;
        persist.type = "grid"
    }
    return render;
};

