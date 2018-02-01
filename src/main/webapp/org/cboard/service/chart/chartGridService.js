'use strict';
cBoard.service('chartGridService', function () {

    this.gridOption;

    this.render = function (containerDom, option, scope, persist, gridConfig) {
        this.gridOption = option;
        if (option == null) {
            containerDom.html("<div class=\"alert alert-danger\" role=\"alert\">No Data!</div>");
            return;
        }
        var height;
        scope ? height = scope.myheight - 20 : null;
        return new CBoardGridRender(containerDom, option).do(height, persist, gridConfig);
    };

    this.parseOption = function (data) {
        return gridDataProcess(data.chartConfig, data.keys, data.series, data.data, data.seriesConfig);
    };

});

function gridDataProcess(chartConfig, casted_keys, casted_values, aggregate_data, newValuesConfig) {
    var columnDefs = [],
        rowData = [];
    var newValue = changeJsonToArr(newValuesConfig);
    var align;
    var option = chartConfig.option;
    for (var i = 0; i < chartConfig.keys.length; i++) {
        columnDefs.push({
            headerName: chartConfig.keys[i].col,
            field: removePoint(chartConfig.keys[i].col),
            enableRowGroup: true,
            enableValue: true,
            enablePivot: true,
            filter: 'text',
            //rowGroup: true,
            cellStyle: {"text-align": (chartConfig.keys[i].align ? chartConfig.keys[i].align : 'center')}
        })
    }
    //如果含有列维
    if (chartConfig.groups.length > 0 && aggregate_data.length > 0) {
        var map = [];
        var arr = [];
        for (var i = 0; i < casted_values[0].length; i++) {
            map.push({});
            for (var j = 0; j < casted_values.length; j++) {
                if (!map[i][casted_values[j][i] + "a"]) {
                    map[i][casted_values[j][i] + "a"] = casted_values[j][i];
                }
            }
        }
        var getChildren = function (name, index) {
            index++;
            var new_arr = [];
            if (index == map.length - 1) {
                var cols_id = 0;
                for (var j in map[index]) {
                    var field = name + "-" + map[index][j];
                    new_arr.push({
                        headerName: map[index][j],
                        field: removePoint(field),
                        enableRowGroup: true,
                        enableValue: true,
                        enablePivot: true,
                        filter: 'text',
                        cellStyle: {"text-align": chartConfig.values[0].cols[cols_id].align ? chartConfig.values[0].cols[cols_id].align : 'center'}
                    })
                    cols_id++;
                }
            } else {
                for (var j in map[index]) {
                    var field = name + "-" + map[index][j];
                    new_arr.push({
                        headerName: map[index][j],
                        groupId: map[index][j],
                        children: getChildren(field, index)
                    })
                }
            }
            return new_arr;
        }
        for (var i in map[0]) {
            var index = 0;
            var name = map[0][i];
            arr.push({
                headerName: map[0][i],
                groupId: map[0][i],
                children: getChildren(name, index)
            })
        }
        for (var i = 0; i < arr.length; i++) {
            columnDefs.push(arr[i]);
        }
    } else {
        for (var i = 0; i < casted_values.length; i++) {
            columnDefs.push({
                headerName: casted_values[i][0],
                field: removePoint(casted_values[i][0]),
                enableRowGroup: true,
                enableValue: true,
                enablePivot: true,
                filter: 'number',
                cellStyle: {"text-align": chartConfig.values[0].cols[i].align ? chartConfig.values[0].cols[i].align : 'center'}
            })
        }
    }

    for (var i = 0; i < casted_keys.length; i++) {
        var rowItem = {};
        for (var z = 0; z < chartConfig.keys.length; z++) {
            if (checkNumber(casted_keys[i][z])) {
                rowItem[removePoint(chartConfig.keys[z].col)] = parseFloat(casted_keys[i][z]);
            } else {
                rowItem[removePoint(chartConfig.keys[z].col)] = casted_keys[i][z];
            }
        }
        for (var j = 0; j < newValue.length; j++) {
            if (checkNumber(aggregate_data[j][i])) {
                rowItem[removePoint(newValue[j])] = parseFloat(aggregate_data[j][i]);
            } else {
                rowItem[removePoint(newValue[j])] = aggregate_data[j][i];
            }
        }
        rowData.push(rowItem);
    }
    var gridOption = {
        columnDefs: columnDefs,
        rowData: rowData,
        enableFilter: true,
        enableSorting: true,
        animateRows: true,
        floatingFilter: true,
        showToolPanel: false,
        enableRangeSelection: true,
        rowSelection: 'multiple',
        suppressRowClickSelection: true,
        defaultColDef: {
            headerCheckboxSelection: isFirstColumn,
            checkboxSelection: isFirstColumn,
        },
    }

    //设置option区域的功能
    for (var i in option) {
        gridOption[i] = option[i];
    }
    return gridOption;
}

function getJsonLength(jsonData) {
    var length = 0;
    for (var l in jsonData) {
        length++;
    }
    return length;
}

function changeJsonToArr(jsonData) {
    var arr = [];
    for (var l in jsonData) {
        arr.push(l);
    }
    return arr;
}

function checkNumber(theObj) {
    var reg = /^[0-9]+.?[0-9]*$/;
    if (reg.test(theObj)) {
        return true;
    }
    return false;
}

//去除小数点，主要用于防止ag-Grid因为属性名含‘.’无法读取数据
function removePoint(str) {
    return str.replace(/['.']/g, '');
}

function isFirstColumn(params) {
    var displayedColumns = params.columnApi.getAllDisplayedColumns();
    var thisIsFirstColumn = displayedColumns[0] === params.column;
    return thisIsFirstColumn;
}

