/**
 * Created by david on 2017/6/12.
 */
var camera_id = '';
$(function () {
    //用户列表
    var camera_list = $('#camera_list').DataTable({
        "language": lang,
        "lengthChange": false,
        "searching": false,
        "serverSide": true,
        "destroy": true,
        "info": true,
        "autoWidth": false,
        "mark": {
            "exclude": [".exclude"]
        },
        "ajax": function (data, callback, settings) {
            // var param = {};
            // param.pageSize = data.length;
            // param.pageIndex = data.start;
            // param.page = (data.start / data.length) + 1;
            var param = {
                pageSize: data.data.limit,
                pageIndex: data.data.offset,
                param: {}
            };
            $.ajax({
                url: BASE_PATH + "camera",
                type: "GET",
                cache: false,
                data: param,
                dataType: "json",
                success: function (result) {
                    data.success({
                        'rows': result.data.data,
                        'total': result.data.total
                    });

                    // var resultData = {};
                    // resultData.draw = result.data.draw;
                    // resultData.recordsTotal = result.total;
                    // resultData.recordsFiltered = result.total;
                    // resultData.data = result.data;
                    // if(resultData.data == null){
                    //     resultData.data =[];
                    // }
                    // callback(resultData);
                },
                error: function () {
                    toastr.warning("请求列表数据失败, 请重试");
                }
            });
        },
        columns: [
            {"data": "id", "searchable": false, "orderable": false, "className": "exclude"},
            {"data": "name", "orderable": false},
            {"data": "code", "searchable": false, "className": "exclude", "orderable": false},
            {"data": "ip", "orderable": false},
            {"data": "organization", "searchable": false, "className": "exclude", "orderable": false},
            {"data": "address", "searchable": false, "className": "exclude", "orderable": false},
            {"data": "status", "searchable": false, "className": "exclude"},
            {"data": "createTime", "searchable": false, "className": "exclude"},
            {"data": "null", "searchable": false, "orderable": false, "className": "exclude"}
        ],
        "aoColumnDefs": [
            {
                "sClass": "center",
                "aTargets": [8],
                "mData": "id",
                "mRender": function (a, b, c, d) {//a表示statCleanRevampId对应的值，c表示当前记录行对象
                    // 修改 删除 权限判断
                    var buttons = '';
                    if (accessUpdate) {
                        buttons += '<button type="button" data-id="' + a + '" class="btn btn-default btn-xs btn-edit">编辑</button>\n';
                    }
                    if (accessDelete) {
                        if (c.status == 1) {
                            buttons += '<button type="button" data-id="' + a + '" class="btn btn-warning btn-xs btn-close">关闭</button>';
                        }
                        else {
                            buttons += '<button type="button" data-id="' + a + '" class="btn btn-success btn-xs btn-open">开启</button>';
                        }
                        buttons += '<button type="button" data-id="' + a + '" class="btn btn-danger btn-xs btn-delete">删除</button>';
                    }
                    return buttons;

                }
            }
        ],
        fnRowCallback: function (nRow, aData, iDataIndex) {
            var status = aData.status;
            var html = '<text aria-hidden="true" style="color: red" data-state = "' + status + '">未开启</text>';
            if (status == 1) {
                html = '<text aria-hidden="true" style="color: #00e765"  data-state = "' + status + '">已开启</text>';
            }
            $('td:eq(6)', nRow).html(html);
            return nRow;
        }

    });

    /* 数组转json
     * @param array 数组
     * @param type 类型 json array
     */
    function formatArray(array, type) {
        var dataArray = {};
        $.each(array, function () {
            if (dataArray[this.name]) {
                if (!dataArray[this.name].push) {
                    dataArray[this.name] = [dataArray[this.name]];
                }
                dataArray[this.name].push(this.value || '');
            } else {
                dataArray[this.name] = this.value || '';
            }
        });
        return ((type == "json") ? JSON.stringify(dataArray) : dataArray);
    }

    jQuery.validator.addMethod("telphoneValid", function (value, element) {
        var tel = /^(13|14|15|17|18)\d{9}$/;
        return tel.test(value) || this.optional(element);
    }, "请输入正确的手机号码");

    //新增或修改用户验证
    $("form#user_form").validate({
        rules: {
            username: {required: true},
            password: {required: true},
            email: {required: true, email: true},
            phone: {required: true, telphoneValid: true}
        },
        messages: {
            username: {required: "请输入用户名."},
            password: {required: "请输入密码"},
            email: {required: "请输入 E-Mail 地址", email: "请输入正确的 E-Mail 地址"},
            phone: {required: "请输入手机号码", telphoneValid: "请输入正确的手机号码"}
        },
        submitHandler: function (form) {

            //提交数据
            var data = $("#user_form").serializeArray();
            var roles = new Array();
            for (var item in data) {
                if (data[item]["name"] == "userRoles") {
                    roles.push({roleId: data[item]["value"]});
                    delete data[item];
                }

            }
            data.push({name: "userRoles", value: roles});
            var dataJson = formatArray(data, "json");
            console.log(dataJson);
            if (camera_id == '') {
                var api = "user/add";
                // ajax
                $('button[type="submit"]').attr('disabled', true);
                Request.post(api, dataJson, function (e) {
                    console.log(e);
                    $('button[type="submit"]').attr('disabled', false);
                    if (e.success) {
                        toastr.info("新增用户成功");
                        $("#modal-add").modal('hide');
                        camera_list.draw(false);
                        camera_list.ajax.reload();
                    }
                    else {
                        toastr.error(e.message);
                    }

                });
            }
            else {
                var api = "camera/" + camera_id;
                // ajax
                $('button[type="submit"]').attr('disabled', true);
                Request.put(api, dataJson, function (e) {
                    console.log(e);
                    $('button[type="submit"]').attr('disabled', false);

                    if (e.success) {
                        toastr.info("修改用户成功");
                        $("#modal-add").modal('hide');
                        camera_list.draw(false);

                    }
                    else {
                        toastr.error(e.message);
                    }

                });
            }
        }
    });
    //新增用户弹出操作
    $(".box-tools").off('click', '.btn-add').on('click', '.btn-add', function () {
        camera_id = '';
        $(".modal-title").html("新增用户");
        $("#modal-add").modal('show');
        clearData();
    });

    //编辑用户弹出操作
    $("#camera_list").off('click', '.btn-edit').on('click', '.btn-edit', function () {
        var that = $(this);
        var id = that.data('id');
        camera_id = id;
        $(".modal-title").html("编辑用户");
        $("#modal-add").modal('show');
        //加载编辑数据
        Request.get("camera/" + id, {}, function (e) {
            if (e.success) {
                e.data.password = "$default";
                var data = e.data;
                $("input#username").val(data.username);
                $("input#password").val(data.password);
                $("input#name").val(data.name);
                $("input#phone").val(data.phone);
                $("input#email").val(data.email);
                var roles = [];
                for (var i = 0; i < data.userRoles.length; i++) {
                    roles.push(data.userRoles[i]["roleId"])
                }
                var checkchilds = $("input.checkchild");
                for (var i = 0; i < checkchilds.length; i++) {
                    if (contains(roles, checkchilds[i].value)) {
                        checkchilds[i].checked = true;
                    }

                }

            }
        });

    });
    //用户禁用
    $("#camera_list").off('click', '.btn-close').on('click', '.btn-close', function () {
        var that = $(this);
        var id = that.data('id');
        camera_id = id;
        $("#modal-delete").modal('show');

    });
    $("#modal-delete").off('click', '.btn-close-sure').on('click', '.btn-close-sure', function () {
        var id = camera_id;
        Request.put("camera/" + id + "/disable", {}, function (e) {
            if (e.success) {
                toastr.info("注销成功!");
                camera_list.draw();
                camera_list.ajax.reload();
            } else {
                toastr.error(e.message);
            }
        });
    });
    //用户启用
    $("#camera_list").off('click', '.btn-open').on('click', '.btn-open', function () {
        var that = $(this);
        var id = that.data('id');
        Request.put("user/" + id + "/enable", {}, function (e) {
            if (e.success) {
                toastr.info("启用成功!");
                camera_list.draw();
                camera_list.ajax.reload();
            } else {
                toastr.error(e.message);
            }
        });
    });
    //用户删除
    $("#camera_list").off('click', '.btn-delete').on('click', '.btn-delete', function () {
        var that = $(this);
        var id = that.data('id');
        Request.delete("camera/" + id + "/delete", {}, function (e) {
            if (e.success) {
                toastr.info("删除成功!");
                camera_list.draw();
                camera_list.ajax.reload();
            } else {
                toastr.error(e.message);
            }
        });


    });

    //表单数据清空
    function clearData() {
        $("input#code").val("");
        $("input#name").val("");
        $("input#ip").val("");
        $("input#address").val("");
        $("input#node").val("");
        $("input.checkchild").prop("checked", false);
    }

    //数组是否存在元素
    function contains(arr, obj) {
        var i = arr.length;
        while (i--) {
            if (arr[i] === obj) {
                return true;
            }
        }
        return false;
    }

});


