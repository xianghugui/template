$(function () {
    //加载服务器列表
    serverList = $('#server_list').DataTable({
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
                // encodeURI 中文转码
                str += '&terms%5b0%5d.column=name&terms%5b0%5d.value=%25' + encodeURI(searchName) + '%25';
            }
            $.ajax({
                url: BASE_PATH + "server/selectAll",
                type: "GET",
                cache: false,
                data: str,
                dataType: "json",
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
                    if(accessCreate){
                        buttons += '<button type="button" data-id="' + a + '" class="btn btn-info btn-xs btn-add-device">关联设备</button>\n';
                    }
                    if (accessUpdate) {
                        buttons += '<button type="button" data-rowIndex="' + d.row + '" class="btn btn-info btn-xs btn-update">编辑</button>\n';
                    }
                    buttons += '<button type="button" data-id="' + a + '" class="btn btn-default btn-xs btn-server-info">详情</button>\n';
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
        $("#server_title").html("添加服务器");
        $("#server_name").val("");
        $("#server_ip").val("");
        $("#server_port").val("");
        $("#remark").val("");
        $("#add_server_form").data("type", "0");
        $("#modal_server_add").modal('show');
    });

    //编辑服务器
    $("#server_list").off('click', '.btn-update').on('click', '.btn-update', function () {
        $("#server_title").html("编辑服务器");
        var rowIndex = $(this).data("rowIndex");
        var data = serverList.rows(rowIndex).data()[0];
        $("#server_name").val(data.name);
        $("#server_ip").val(data.serverIp);
        $("#server_port").val(data.serverPort);
        $("#remark").val(data.note);
        $("#add_server_form").data("type", "1").data("id", data.id);
        $("#modal_server_add").modal('show');
    });

    $("#add_server_form").validate({
        rules: {
            serverName: {required: true},
            serverIP: {required: true, ipValid: true},
            serverPort: {required: true, portValid: true}
        },
        messages: {
            serverName: {required: "服务器名称不能为空"},
            serverIP: {required: "IP地址不能为空", ipValid: "请输入正确的IP"},
            serverPort: {required: "端口不能为空", portValid: "请输入正确的端口号"}
        },
        submitHandler: function () {
            var btn = $('#submit-parent');
            btn.attr('disabled', "true");
            btn.html("保存中..请稍后");
            var flag = $("#add_server_form").data("type") == "0";
            var req = Request.post;
            var params = {
                name: $("#server_name").val(),
                serverIp: $("#server_ip").val(),
                serverPort: $("#server_port").val(),
                note: $("#remark").val()
            };

            if (!flag) {
                params.id = $("#add_server_form").data("id");
                req = Request.put;
            }

            req("server/" + (flag ? "add" : "update"), JSON.stringify(params), function (e) {
                if (e.success) {
                    toastr.info("保存完毕");
                    $("#modal_server_add").modal('hide');
                    serverList.ajax.reload();
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

            if (noCheckArray !== null) {
                params.cancelDeviceIdList = noCheckArray;
            }
            if (checkArray !== null && checkArray.length > 0) {
                params.deviceIdList = checkArray;
            }

            Request.post("server/addDevice", JSON.stringify(params), function (e) {
                if (e.success) {
                    toastr.info("保存完毕");
                    $("#modal_server_device_add").modal('hide');
                    serverList.ajax.reload();
                } else {
                    toastr.error(e.message);
                }
                btn.html("保存");
                btn.removeAttr('disabled');
                $("#modal_server_device_add").modal('hide');
            });
        }
    });

    //设置是否选中节点的区域
    $(".checkbox").off('change', '.checkDevice').on('change', '.checkDevice', function () {
        var _self = $(this);
        var str = "";
        if (_self.prop("checked")) {
            str = "<label class='checklabel'><input class='checkDevice' type='checkbox' name='vehicle' " +
                "value='" + _self.val() + "' data-status = '" +
                _self.data("status") + "' checked='checked' data-id='" + _self.data("id") + "'>" + _self.val() + "</label>";
            _self.parent().remove();
            $("#checkbox").append(str);
        }
        else {
            str = "<label class='checklabel'><input class='checkDevice' type='checkbox' name='vehicle' " +
                "value='" + _self.val() + "' data-status = '" +
                _self.data("status") + "' data-id='" + _self.data("id") + "'>" + _self.val() + "</label>";
            _self.parent().remove();
            $("#noCheckbox").append(str);
        }
    });

    //全选
    $("#checkAll").off('change').on('change', function () {
        if ($(this).prop("checked")) {
            $(".checkDevice").each(function (i) {
                if (!$(this).prop("checked")) {
                    var _self = $(this);
                    var str = "";
                    str = "<label class='checklabel'><input class='checkDevice' type='checkbox' name='vehicle' " +
                        "value='" + _self.val() + "' data-status = '" +
                        _self.data("status") + "' data-id='" + _self.data("id") + "' checked='checked'>" + _self.val() + "</label>";
                    _self.parent().remove();
                    $("#checkbox").append(str);
                }
            });
            $(".checkDevice").attr("checked", true);
        }
        else {
            $(".checkDevice").each(function (i) {
                if ($(this).prop("checked")) {
                    var _self = $(this);
                    var str = "";
                    str = "<label class='checklabel'><input class='checkDevice' type='checkbox' name='vehicle' " +
                        "value='" + _self.val() + "' data-status = '" +
                        _self.data("status") + "' data-id='" + _self.data("id") + "'>" + _self.val() + "</label>";
                    _self.parent().remove();
                    $("#noCheckbox").append(str);
                }
            });
            $(".checkDevice").attr("checked", false);
        }
    });


    //获取选中设备的设备id
    function getCheckAdIds() {
        var arrays = new Array();
        $("#checkbox input:checkbox[name=vehicle]").each(function (i) {
            if ($(this).data("status") === 0) {
                arrays.push($(this).data("id"));
            }
        });
        return arrays;
    }

    //获取取消选中设备的设备id
    function cancelCheckAdIds() {
        var arrays = new Array();
        $("#noCheckbox input:checkbox[name=vehicle]").each(function (i) {
            if ($(this).data("status") === 1) {
                arrays.push($(this).data("id"));
            }
        });
        return arrays;
    }

    //删除服务器
    $("#server_list").off('click', '.btn-delete').on('click', '.btn-delete', function () {
        var id = $(this).data("id");
        confirm('警告', '真的要删除该服务器吗?', function () {
            Request.delete("server/deleteServer/" + id, {}, function (e) {
                if (e.success) {
                    toastr.info("删除成功!");
                    serverList.ajax.reload();
                } else {
                    toastr.error(e.message);
                }
            });
        });
    });

    //查看服务器详情
    $("#server_list").off('click', '.btn-server-info').on('click', '.btn-server-info', function () {
        $("#info-deviceInfo").html("");
        $("#info-note").html("")
        var id = $(this).data("id");
        Request.get('server/selectInfo/' + id, {}, function (e) {
                if (e !== null) {
                    if (e.deviceList !== "") {
                        var deviceArray = e.deviceList.split(","),
                            len = deviceArray.length,
                            str = "";
                        for (var i = 0; i < len; i++) {
                            str += "<label class='checklabel'><input type='checkbox' checked='checked' disabled>" + deviceArray[i] + "</label>";
                        }
                    } else {
                        str = "没有关联设备";
                    }
                    $("#info-deviceInfo").append(str);
                    $("#info-note").append("备注：" + e.note);
                }
            }
        );
        $("#modal_server_info").modal('show');
    });

    //加载设备列表
    function loadDeviceList(id) {
        Request.get('server/queryDevice/' + id, {}, function (e) {
            var checkList = e.data,
                len = checkList.length,
                checkStr = "",
                noCheckStr = "";
            if (len > 0) {
                for (var i = 0; i < len; i++) {
                    if (checkList[i].status === 1) {
                        checkStr += "<label class='checklabel'><input class='checkDevice' type='checkbox' name='vehicle' " +
                            "data-id ='" + checkList[i].id + "' data-status = '" + checkList[i].status + "' " +
                            " checked='checked' value='" + checkList[i].code + "'>" + checkList[i].code + "</label>";
                    } else {
                        noCheckStr += "<label class='checklabel'><input class='checkDevice' type='checkbox' name='vehicle' " +
                            "data-id ='" + checkList[i].id + "' data-status = '" + checkList[i].status + "' " +
                            "value='" + checkList[i].code + "'>" + checkList[i].code + "</input></label>";
                    }
                }
                $("#checkbox").append(checkStr);
                $("#noCheckbox").append(noCheckStr);
            }
        })
    }

    //验证ip地址
    jQuery.validator.addMethod("ipValid", function (value, element) {
        var ip = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/;
        return ip.test(value) || this.optional(element);
    }, "请输入正确的IP");

    //验证端口
    jQuery.validator.addMethod("portValid", function (value, element) {
        var port = /^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{4}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/;
        return port.test(value) || this.optional(element);
    }, "请输入正确的端口");
});


