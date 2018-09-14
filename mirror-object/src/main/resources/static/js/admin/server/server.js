$(function () {
    //服务器列表
    var serverList = $('#server_list').DataTable({
        "language": lang,
        "lengthChange": false,
        "searching": false,
        "serverSide": true,
        "destroy": true,
        "info": true,
        "autoWidth": false,
        "order": [],
        "mark": {
            "exclude": [".exclude"]
        },
        "ajax": function (data, callback, settings) {
            var str = "pageSize=" + data.length + "&pageIndex=" + data.start;
            var searchName = $("#searchName").val().trim();
            if (searchName != "") {
                str += '&terms%5b0%5d.column=name&terms%5b0%5d.value=%25' + searchName;
            }
            $.ajax({
                url: BASE_PATH + "server/selectAll",
                type: "GET",
                cache: false,
                data: str,
                dataType: "json",
                crossDomain: true,
                success: function (result) {
                    var resultData = {};
                    resultData.draw = result.data.draw;
                    resultData.recordsTotal = result.total;
                    resultData.recordsFiltered = result.total;
                    resultData.data = result.data;
                    if (resultData.data == null) {
                        resultData.data = [];
                    }
                    callback(resultData);
                },
                error: function () {
                    toastr.warning("请求列表数据失败, 请重试");
                }
            });
        },
        columns: [
            {
                "data": null, "searchable": false, "orderable": false, "className": "exclude",
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {"data": "name"},
            {"data": "serverIp"},
            {"data": "serverPort", "orderable": false},
            {"data": "deviceList", "orderable": false},
            {"data": "note", "searchable": false, "orderable": false, "className": "exclude"},
            {"data": "createTime"}
        ],
        "aoColumnDefs": [
            {
                "sClass": "center",
                "aTargets": [7],
                "mData": "id",
                "mRender": function (a, b, c, d) {//a表示statCleanRevampId对应的值，c表示当前记录行对象
                    // 修改 删除 权限判断
                    var buttons = '';
                    if (accessUpdate) {
                        buttons += '<button type="button" data-id="' + a + '" class="btn btn-default btn-xs btn-add-device">添加设备</button>\n';
                    }
                    buttons += '<button type="button" data-id="' + a + '" class="btn btn-default btn-xs btn-info">详情</button>\n';
                    if (accessDelete) {
                        buttons += '\n<button type="button" data-id="' + a + '" class="btn btn-danger btn-xs btn-delete">删除</button>';
                    }
                    return buttons;

                }
            }
        ]

    });

    //搜索
    $('.btn-search').off('click').on('click', function () {
        serverList.draw();
        serverList.ajax.reload();
    });

    //添加服务器
    $('.btn-add').off('click').on('click', function () {
        $("#server_name").val("");
        $("#server_ip").val("");
        $("#server_port").val("");
        $("#remark").val("");
        $("#modal_server_add").modal('show');
    });


    $("#add_server_form").validate({
        rules: {
            serverName: {required: true},
            serverIP: {required: true},
            serverPort: {required: true}
        },
        messages: {
            serverName: {required: "服务器名称不能为空"},
            serverIP: {required: "IP地址不能为空"},
            serverPort: {required: "端口不能为空"}
        },
        submitHandler: function () {
            var btn = $('#submit-parent');
            btn.attr('disabled', "true");
            btn.html("保存中..请稍后");

            var params = {
                name: $("#server_name").val(),
                serverIp: $("#server_ip").val(),
                serverPort: $("#server_port").val(),
                node: $("#remark").val()
            };

            Request.post("server/add", JSON.stringify(params), function (e) {
                if (e.success) {
                    toastr.info("保存完毕");
                    $("#modal_server_add").modal('hide');
                    serverList.reload().draw();
                } else {
                    toastr.error(e.message);
                }
                btn.html("保存");
                btn.removeAttr('disabled');
            });
        }
    });

    //添加设备
    $("#server_list").off('click', '.btn-add-device').on('click', '.btn-add-device', function () {
        var id = $(this).data("id");
        $("#add_server_device_form").data("id", id);
        loadDeviceList(id);
        $("#checkbox").empty();
        $("#noCheckbox").empty();
        $("#modal_server_device_add").modal('show');
    });

    $("#add_server_device_form").validate({
        submitHandler: function () {
            var checkArray = getCheckAdIds(),
                noCheckArray = cancelCheckAdIds();
            var btn = $('#submit-parent');
            btn.attr('disabled', "true");
            btn.html("保存中..请稍后");

            var params = {
                serverId: $("#add_server_device_form").data("id")
            };

            if(noCheckArray !== null){
                params.cancelDeviceIdList = noCheckArray;
            }
            if(checkArray !== null && checkArray.length > 0){
                params.deviceIdList = checkArray;
            }

            Request.post("server/addDevice", JSON.stringify(params), function (e) {
                if (e.success) {
                    toastr.info("保存完毕");
                    $("#modal_server_device_add").modal('show');
                    serverList.reload().draw();
                } else {
                    toastr.error(e.message);
                }
                btn.html("保存");
                btn.removeAttr('disabled');
                $("#modal_server_device_add").modal('hide');
            });
        }
    });

    //
    $(".checkbox").off('change').on('change', function () {
        var str ="";
        if (event.target.dataset.type === "2") {
            $("#noCheckbox label[data-id='"+event.target.dataset.id+"']").remove();
            str = "<label class='checklabel' data-id='" + event.target.dataset.id + "'><input class='checkbox' type='checkbox' name='vehicle' " +
                "data-id='" + event.target.dataset.id + "' data-name='"+event.target.dataset.name+"' data-status = '" +
                event.target.dataset.status + "' " +
                "checked='checked' data-type='1'>" + event.target.dataset.name + "</label>";
            $("#checkbox").append(str);
        }
        else {
            $("#checkbox label[data-id='"+event.target.dataset.id+"']").remove();
            str = "<label class='checklabel' data-id='" + event.target.dataset.id + "'><input class='checkbox' type='checkbox' name='vehicle' " +
                "data-id='" + event.target.dataset.id + "' data-name='"+event.target.dataset.name+"' data-status = '" +
                event.target.dataset.status + "' " +
                " data-type='2'>" + event.target.dataset.name + "</label>";
            $("#noCheckbox").append(str);
        }
    });

    //全选
    $("#checkAll").off('change').on('change', function () {
        if ($(this).prop("checked")) {
            $(".checkbox").attr("checked", true);
        }
        else {
            $(".checkbox").attr("checked", false);
        }
    });


    //选中设备
    function getCheckAdIds() {
        var arrays = new Array();
        $("#checkbox input:checkbox[name=vehicle]").each(function (i) {
            if($(this).data("status") === 0){
                arrays.push($(this).data("id"));
            }
        });
        return arrays;
    }

    //取消选中设备
    function cancelCheckAdIds() {
        var arrays = new Array();
        $("#noCheckbox input:checkbox[name=vehicle]").each(function (i) {
            if($(this).data("status") === 1){
                arrays.push($(this).data("id"));
            }
        });
        return arrays;
    }

    //删除服务器
    $("#server_list").off('click', '.btn-delete').on('click', '.btn-delete', function () {
        var id = $(this);
        Request.delete("server/deleteServer/" + id, {}, function (e) {
            if (e.success) {
                toastr.info("删除成功!");
                serverList.reload().draw();
            } else {
                toastr.error(e.message);
            }
        });
    });

    //查看服务器详情
    $("#server_list").off('click', '.btn-info').on('click', '.btn-info', function () {
        $("#info-deviceInfo").html("");
        $("#info-note").html("")
        var id = $(this).data("id");
        Request.get('server/selectInfo/' + id, {}, function (e) {
            if (e !== null) {
                var deviceArray = e.deviceList.split(","),
                    len = deviceArray.length,
                    str = "";
                deviceArray.splice(len - 1, 1);
                len = len - 1;
                for (var i = 0; i < len; i++) {
                    str += "<label class=''><input type='checkbox' checked='checked' disabled>" + deviceArray[i] + "</label>";
                }
                $("#info-deviceInfo").append(str);
                $("#info-note").append("备注:" + e.note);
            }
        });
        $("#modal_server_info").modal('show');
    });

    //加载设备列表
    function loadDeviceList(id) {
        Request.get('server/queryDevice/'+id, {}, function (e) {
            var checkList = e.data,
                len = checkList.length,
                checkStr = "",
                noCheckStr = "";
            if (len > 0) {
                for (var i = 0; i < len; i++) {
                    if(checkList[i].status === 1) {
                        checkStr += "<label class='checklabel' data-id='" + checkList[i].id + "'><input class='checkbox' type='checkbox' name='vehicle' " +
                            "data-id='" + checkList[i].id + "' data-name='"+checkList[i].code+"' data-status = '" + checkList[i].status + "' " +
                            "checked='checked' data-type='1'>" + checkList[i].code + "</label>";
                    }else{
                        noCheckStr += "<label class='checklabel' data-id='" + checkList[i].id + "'><input class='checkbox' type='checkbox' name='vehicle' " +
                            "data-id='" + checkList[i].id + "' data-name='"+checkList[i].code+"' data-status = '" + checkList[i].status + "' " +
                            " data-type='2'>" + checkList[i].code + "</label>";
                    }
                }
                $("#checkbox").append(checkStr);
                $("#noCheckbox").append(noCheckStr);
            }
        })
    }


});


