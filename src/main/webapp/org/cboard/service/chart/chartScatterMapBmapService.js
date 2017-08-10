/**
 * Created by jintian on 2017/8/10.
 */
cBoard.service('chartScatterMapBmapService', function () {
    this.render = function (containerDom, option, scope, persist) {
        return new CboardBMapRender(containerDom, option).chart(null, persist);
    }

    this.parseOption = function (data) {
        var optionData = [];
        var series =[];
        var serieData = [];
        var max = 0;
        var addressN;
        var addressL;
        var addressName;

        // series setting
        for(var j = 0 ; j < data.series.length ; j++){
            max = 0;
            serieData = [];
            for(var i = 0 ; i < data.keys.length ; i++){
                if(data.keys[i].length > 2){
                    addressN = parseFloat(data.keys[i][0]);
                    addressL = parseFloat(data.keys[i][1]);
                    addressName = data.keys[i][2];
                }else if(data.keys[i].length == 2){
                    addressN = parseFloat(data.keys[i][0].split(",")[0]);
                    addressL = parseFloat(data.keys[i][0].split(",")[1]);
                    addressName = data.keys[i][1];
                }else{
                    addressName = null;
                    addressN = null;
                    addressL = null;
                }
                serieData.push({
                    name:addressName,
                    value:[addressN,addressL,data.data[j][i]]
                })

            }
            optionData.push(data.series[j][0]);
            series.push(
                {
                    name: data.series[j][0],
                    type: 'scatter',
                    coordinateSystem: 'geo',
                    data: serieData,
                    symbolSize: function (val) {
                        return val[2] * 20 / max;
                    },
                    label: {
                        normal: {
                            formatter: '{b}',
                            position: 'right',
                            show: false
                        },
                        emphasis: {
                            show: true
                        }
                    }/*,
                 itemStyle: {
                 normal: {
                 color: '#fff'
                 }
                 }*/
                }
            );
        }

        var startPoint = {
            x: 104.114129,
            y: 37.550339
        };
        // 地图自定义样式
        var bmap = {
            center: [startPoint.x, startPoint.y],
            // zoom: 5,
            roam: true,
            mapStyle: {
                styleJson: [{
                    "featureType": "water",
                    "elementType": "all",
                    "stylers": {
                        "color": "#044161"
                    }
                }, {
                    "featureType": "land",
                    "elementType": "all",
                    "stylers": {
                        "color": "#004981"
                    }
                }, {
                    "featureType": "boundary",
                    "elementType": "geometry",
                    "stylers": {
                        "color": "#064f85"
                    }
                }, {
                    "featureType": "railway",
                    "elementType": "all",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "highway",
                    "elementType": "geometry",
                    "stylers": {
                        "color": "#004981"
                    }
                }, {
                    "featureType": "highway",
                    "elementType": "geometry.fill",
                    "stylers": {
                        "color": "#005b96",
                        "lightness": 1
                    }
                }, {
                    "featureType": "highway",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "arterial",
                    "elementType": "geometry",
                    "stylers": {
                        "color": "#004981"
                    }
                }, {
                    "featureType": "arterial",
                    "elementType": "geometry.fill",
                    "stylers": {
                        "color": "#00508b"
                    }
                }, {
                    "featureType": "poi",
                    "elementType": "all",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "green",
                    "elementType": "all",
                    "stylers": {
                        "color": "#056197",
                        "visibility": "off"
                    }
                }, {
                    "featureType": "subway",
                    "elementType": "all",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "manmade",
                    "elementType": "all",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "local",
                    "elementType": "all",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "arterial",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "boundary",
                    "elementType": "geometry.fill",
                    "stylers": {
                        "color": "#029fd4"
                    }
                }, {
                    "featureType": "building",
                    "elementType": "all",
                    "stylers": {
                        "color": "#1a5787"
                    }
                }, {
                    "featureType": "label",
                    "elementType": "all",
                    "stylers": {
                        "visibility": "off"
                    }
                }]
            }
        };

        var mapOption = {
            bmap: bmap,
            legend: {
                orient: 'vertical',
                top: 'top',
                left: 'left',
                selectedMode: 'multiple',
                data: optionData
            },
            tooltip: {
                trigger: 'item'
            },
            series: series
        };
        return mapOption;
    };
});
