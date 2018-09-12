/**
 * Created by david on 2017/6/12.
 */
//用户id
var user_id = '';
$(function () {
    //用户列表
    var user_list = $('#user_list').DataTable({
        "language": lang,
        "paging": true,
        "lengthChange": true,
        "searching": true,
        "ordering": true,
        "destroy":true,
        "info": true,
        "autoWidth": false,
        "bStateSave": true,
        "sPaginationType": "full_numbers",
        "mark":{
            "exclude":[".exclude"]
        },
        "ajax": function (data, callback, settings) {
            var param = {};
            param.pageSize = data.length;
            param.pageIndex = data.start;
            param.page = (data.start / data.length) + 1;
            $.ajax({
                url: BASE_PATH + "user",
                type: "GET",
                cache: false,
                data: param,
                dataType: "json",
                success: function (result) {
                    var resultData = {};
                    resultData.draw = result.data.draw;
                    resultData.recordsTotal = result.total;
                    resultData.recordsFiltered = result.total;
                    resultData.data = result.data;
                    if(resultData.data == null){
                        resultData.data =[];
                    }
                    callback(resultData);
                },
                error: function () {
                    toastr.warning("请求列表数据失败, 请重试");
                }
            });
        },
        columns: [
            {"data": "id","searchable":false,"orderable":false,"className":"exclude"},
            {"data": "username"},
            {"data": "name"},
            {"data": "phone","orderable":false},
            {"data": "createDate"},
            {"data": "status","searchable":false,"orderable":false,"className":"exclude"}
        ],
        "aoColumnDefs": [
            {
                "sClass":"center",
                "aTargets":[6],
                "mData":"id",
                "mRender":function(a,b,c,d) {//a表示statCleanRevampId对应的值，c表示当前记录行对象
                    // 修改 删除 权限判断
                    var buttons = '';
                    if (accessUpdate) {
                    buttons += '<button type="button" data-id="'+a+'" class="btn btn-default btn-xs btn-edit">修改</button>\n';
                    }
                    if (accessDelete) {
                        if (c.status==1)
                        {
                            buttons += '<button type="button" data-id="'+a+'" class="btn btn-warning btn-xs btn-close">禁用</button>';
                        }
                        else {
                            buttons += '<button type="button" data-id="'+a+'" class="btn btn-success btn-xs btn-open">启用</button>';
                        }
                        buttons += '\n<button type="button" data-id="'+a+'" class="btn btn-danger btn-xs btn-delete">删除</button>';
                    }
                    return buttons;

                }
            }
        ],
        fnRowCallback : function(nRow,aData,iDataIndex){
            var status=aData.status;
            var html = '<span class="fa fa-circle text-error" aria-hidden="true" style="color: red" data-state = "'+status+'"></span>';
            if (status==1)
            {
                html = '<span class="fa fa-circle text-success" aria-hidden="true" style="color: #00e765"  data-state = "'+status+'"></span>';
            }
            $('td:eq(5)', nRow).html(html);
            return nRow;
        }

    });
    //全选复选框
    $(".checkall").click(function () {
        var check = $(this).prop("checked");
        $(".checkchild").prop("checked", check);
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

    jQuery.validator.addMethod("telphoneValid", function(value, element) {
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
            var  roles = new Array();
            for(var item in data){
                if (data[item]["name"]=="userRoles")
                {
                    roles.push({roleId:data[item]["value"]});
                    delete data[item];
                }

            }
            data.push({name:"userRoles",value:roles});
            var  dataJson = formatArray(data,"json");
            console.log(dataJson);
            if (user_id=='')
            {
                var api = "user/add";
                // ajax
                $('button[type="submit"]').attr('disabled', true);
                Request.post(api,dataJson,function (e) {
                    console.log(e);
                    $('button[type="submit"]').attr('disabled', false);
                    if (e.success) {
                        toastr.info("新增用户成功");
                        $("#modal-add").modal('hide');
                        user_list.draw( false );
                        user_list.ajax.reload();
                    }
                    else
                    {
                        toastr.error(e.message);
                    }

                });
            }
            else
            {
                var api = "user/" + user_id;
                // ajax
                $('button[type="submit"]').attr('disabled', true);
                Request.put(api,dataJson,function (e) {
                    console.log(e);
                    $('button[type="submit"]').attr('disabled', false);

                    if (e.success) {
                        toastr.info("修改用户成功");
                        $("#modal-add").modal('hide');
                        user_list.draw( false );

                    }
                    else
                    {
                        toastr.error(e.message);
                    }

                });
            }
        }
    });
    //新增用户弹出操作
    $(".box-tools").off('click', '.btn-add').on('click', '.btn-add', function () {
        user_id = '';
        $(".modal-title").html("新增用户");
        $("#modal-add").modal('show');
        clearData();
    });

    //编辑用户弹出操作
    $("#user_list").off('click', '.btn-edit').on('click', '.btn-edit', function () {
    var that = $(this);
    var id = that.data('id');user_id = id;
    $(".modal-title").html("编辑用户");
    $("#modal-add").modal('show');
    clearData();
    //加载编辑数据
        Request.get("user/" + id, {}, function (e) {
            if (e.success) {
                e.data.password = "$default";
                var  data = e.data;
                $("input#username").val(data.username);
                $("input#password").val(data.password);
                $("input#name").val(data.name);
                $("input#phone").val(data.phone);
                $("input#email").val(data.email);
                var  roles = [];
                for (var  i=0;i<data.userRoles.length;i++){
                    roles.push(data.userRoles[i]["roleId"])
                }
                var checkchilds =  $("input.checkchild");
                for (var  i=0;i<checkchilds.length;i++){
                    if (contains(roles,checkchilds[i].value))
                    {
                        checkchilds[i].checked = true;
                    }

                }

             }
        });

    });
    //用户禁用
    $("#user_list").off('click', '.btn-close').on('click', '.btn-close', function () {
        var that = $(this);
        var id = that.data('id');
        user_id = id;
       $("#modal-delete").modal('show');

    });
    $("#modal-delete").off('click', '.btn-close-sure').on('click', '.btn-close-sure', function () {
        var id = user_id;
        Request.put("user/" + id + "/disable", {}, function (e) {
            if (e.success) {
                toastr.info("注销成功!");
                user_list.draw(  );
                user_list.ajax.reload();
            } else {
                toastr.error(e.message);
            }
        });
    });
    //用户启用
    $("#user_list").off('click', '.btn-open').on('click', '.btn-open', function () {
        var that = $(this);
        var id = that.data('id');
        Request.put("user/" + id + "/enable", {}, function (e) {
            if (e.success) {
                toastr.info("启用成功!");
                user_list.draw(  );
                user_list.ajax.reload();
            } else {
                toastr.error(e.message);
            }
        });
    });
    //用户删除
    $("#user_list").off('click', '.btn-delete').on('click', '.btn-delete', function () {
        var that = $(this);
        var id = that.data('id');
        if(id==1008611){
            toastr.error("管理员账号不允许删除！！");
        }else {
            Request.delete("user/" + id + "/delete", {}, function (e) {
                if (e.success) {
                    toastr.info("删除成功!");
                    user_list.draw(  );
                    user_list.ajax.reload();
                } else {
                    toastr.error(e.message);
                }
            });
        }

    });
    //表单数据清空
    function  clearData() {
        $("input#username").val("");
        $("input#password").val("");
        $("input#name").val("");
        $("input#phone").val("");
        $("input#email").val("");
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


