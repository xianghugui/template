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
                str += '&terms%5b1%5d.column=name&terms%5b1%5d.value=%25' + searchName;
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
            {"data": "associationCode", "orderable": false},
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
                        buttons += '<button type="button" data-id="' + a + '" class="btn btn-default btn-xs btn-edit">编辑</button>\n';
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

    loadDeviceList();

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

    //编辑用户弹出操作
    $("#user_list").off('click', '.btn-edit').on('click', '.btn-edit', function () {
        var that = $(this);
        var id = that.data('id');
        user_id = id;
        $(".modal-title").html("编辑用户");
        $("#modal-add").modal('show');
    });

    //删除服务器
    $("#modal-delete").off('click', '.btn-close-sure').on('click', '.btn-close-sure', function () {
        var id = user_id;
        Request.put("user/" + id + "/disable", {}, function (e) {
            if (e.success) {
                toastr.info("注销成功!");
                user_list.draw();
                user_list.ajax.reload();
            } else {
                toastr.error(e.message);
            }
        });
    });

    //添加设备
    $("#server_list").off('click', '.btn-add-device').on('click', '.btn-add-device', function () {
        $("#add_server_device_form").data("id",$(this).data("id"));
        $("#modal_server_device_add").modal('show');
    });

    $("#add_server_device_form").validate({
        submitHandler: function () {

            var checkArray = getCheckAdIds();
            if(checkArray === null || checkArray.length === 0){
                toastr.info("请选择设备");
                return;
            }
            var btn = $('#submit-parent');
            btn.attr('disabled', "true");
            btn.html("保存中..请稍后");

            var params = {
                serverId: $("#add_server_device_form").data("id"),
                deviceIdList: checkArray
            };

            Request.post("server/addDevice", JSON.stringify(params), function (e) {
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




    //全选
    $("#checkAll").off('change').on('change',function () {
        if($(this).prop("checked")){
            $(".checkbox").attr("checked", true);
        }
        else{
            $(".checkbox").attr("checked", false);
        }
    });


    //选中设备
    function getCheckAdIds() {
        var arrays = new Array();
        $("input:checkbox[name=vehicle]:checked").each(function(i){
            arrays[i] = $(this).val();
        });
        return arrays;
    }

    //加载设备列表
    function loadDeviceList() {
        Request.get('server/queryDevice', {}, function (e) {
            var checkList = e.data,
                len = checkList.length,
                str = "";
            if (len > 0) {
                for (var i = 0; i < len; i++) {
                        str += "<label class=''><input class='checkbox' type='checkbox' name='vehicle' " +
                            "value='" + checkList[i].id + "'>" + checkList[i].code + "</label>";
                }
                $("#checkbox").append(str);
            }
        })
    }


});


