cBoard.controller('renderCtrl', function ($timeout, $rootScope, $scope, $state, $location, $http, ModalUtils, chartService) {

    $scope.loading = true;
    $scope.l = 1;
    $scope.persistFinish = false;


    var buildRender = function (w, reload) {
        w.render = function (content, optionFilter, scope) {
            w.persist = {};
            var chartType = w.widget.data.config.chart_type;
            if(chartType == 'chinaMapBmap'){
                chartService.render(content, w.widget.data, optionFilter, scope, reload, w.persist);
                w.loading = false;
                $scope.l--;
            } else {
                chartService.render(content, w.widget.data, optionFilter, scope, reload, w.persist).then(function (d) {
                    w.realTimeTicket = d;
                    w.loading = false;
                    $scope.l--;
                }, function (error) {
                    $scope.l--;
                });
            }
        };
    };

    $scope.$watch('l', function (newValue) {
        console.log(newValue);
        if (newValue == 0) {
            $timeout(function () {
                runTask();
            }, 3000);
        }
    });

    var runTask = function () {
        var result = {};
        _.each($scope.board.layout.rows, function (row) {
            _.each(row.widgets, function (widget) {
                result[widget.widgetId] = widget.persist;
            });
        });

        html2canvas($('body')[0], {
            onrendered: function (canvas) {
                result['img'] = canvas.toDataURL("image/jpeg");
                var obj = {
                    persistId: $location.search().pid,
                    data: result
                };
                var xmlhttp = new XMLHttpRequest();
                xmlhttp.open("POST", "commons/persist.do", false);
                xmlhttp.send(angular.toJson(obj));
                $scope.$apply(function () {
                    $scope.persistFinish = true;
                });
            }
        });
    };


    $scope.load = function (reload) {
        $scope.loading = true;

        if ($scope.board) {
            _.each($scope.board.layout.rows, function (row) {
                _.each(row.widgets, function (widget) {
                    widget.show = false;
                });
            });
        }
        $http.get("dashboard/getBoardData.do?id=" + $location.search().id).success(function (response) {
            $scope.loading = false;
            $scope.board = response;
            _.each($scope.board.layout.rows, function (row) {
                _.each(row.widgets, function (widget) {
                    if (!_.isUndefined(widget.hasRole) && !widget.hasRole) {
                        return;
                    }
                    buildRender(widget, reload);
                    widget.loading = true;
                    widget.show = true;
                    $scope.l++;
                });
            });
            $scope.l--;
        });
    };
    $scope.load(false);
    $scope.showExit = true;
    //导出
    $scope.export = function () {
        if ($scope.exportStatus) {
            return;
        }
        $scope.exportStatus = true;
        $http({
            url: "dashboard/exportBoard.do?id=" + $location.search().id,
            method: "POST",
            headers: {
                'Content-type': 'application/json'
            },
            responseType: 'arraybuffer'
        }).success(function (data) {
            var blob = new Blob([data], {type: "application/vnd.ms-excel"});
            var objectUrl = URL.createObjectURL(blob);
            var aForExcel = $("<a><span class='forExcel'>下载excel</span></a>").attr("href", objectUrl);
            aForExcel.attr("download", $scope.board.name);
            $("body").append(aForExcel);
            $(".forExcel").click();
            aForExcel.remove();
            $scope.exportStatus = false;
        }).error(function (data, status, headers, config) {
            $scope.exportStatus = false;
        });
    };
});