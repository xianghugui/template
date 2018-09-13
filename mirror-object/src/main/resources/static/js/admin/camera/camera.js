/**
 * Created by david on 2017/6/12.
 */
var camera_id = '';
$(function () {
    var camera_list;
    /**
     * 初始化下拉选择框
     */
    $('select').selectpicker({ noneSelectedText : '请选择' });

    /**
     * 初始化组织树
     * @type {boolean}
     */
    var inited = false;
    var organization_list = [];

    var initOrganizationTree = function () {
        Request.get("organization/organizationTree", function (e) {
            organization_list = e;
            var tree = organizationTree.init();
            var rootNodes = tree.getRootNodes(e);

            $('#area_tree').treeview({
                data: rootNodes,
                levels: 3,
                onNodeSelected: function (event, data) {
                    camera_list.draw(false);
                }
            });
            $('#area_tree').treeview('selectNode', [0]);
        });
    };

    initOrganizationTree();

    var organizationTree = {
        init: function () {
            if (inited) return this;
            if (jQuery === undefined) {
                console.error("Required jQuery support is not available");
            } else {
                inited = true;
                var that = this;
                $(function () {

                });
            }
            return this;
        },
        reload: function () {

        },
        getRootNodes: function (data) {
            var that = this;
            var result = [];
            var level = 0;
            $.each(data, function (index, item) {
                if (item['parentId'] == '-1') {
                    var obj = {
                        id: item.id,
                        level: level,
                        parentId: item.parentId,
                        text: item.name,
                        nodes: []
                    };
                    obj.nodes = that.getChildNodes(data, item,level);
                    result.push(obj);
                }
            });
            return result;
        },
        getChildNodes: function (data, parentNode,level) {
            var that = this;
            var result = [];
            level++;
            $.each(data, function (i, item) {
                if (item['parentId'] == parentNode['id']) {
                    var obj = {
                        id: item.id,
                        level: level,
                        parentId: item.parentId,
                        text: item.name,
                        nodes: null
                    };
                    result.push(obj);
                    var childNodes = that.getChildNodes(data, item,level);
                    if (childNodes != null && childNodes.length > 0) {
                        obj.nodes = childNodes;
                    }
                }
            });
            return result;
        }
    };

    /**
     * 初始化Table
     * @type {*|jQuery}
     */
    camera_list = $('#camera_list').DataTable({
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
            var organization = $('#area_tree').treeview('getSelected')[0];
            if (!isNaN(organization.id)) {
                var str = "pageSize=" + data.length + "&pageIndex=" + data.start;
                var searchIp = $("#searchIp").val().trim();
                if (searchIp != "") {
                    str += '&terms%5b0%5d.column=ip&terms%5b0%5d.value=' + searchIp + '&terms%5b0%5d.termType=eq&terms%5b0%5d.type=and';
                }
                var searchName = $("#searchName").val().trim();
                if (searchName != "") {
                    str += '&terms%5b1%5d.column=name&terms%5b1%5d.value=%25' + searchName + '%25&terms%5b1%5d.termType=like&terms%5b1%5d.type=and';
                }
                if ($("#searchStatus").val() == 1) {
                    str += '&terms%5b2%5d.column=status&terms%5b2%5d.value=1&terms%5b2%5d.termType=eq&terms%5b2%5d.type=and';
                }
                if (organization.level == 0){
                    str += '&terms%5b3%5d.column=organizationId&terms%5b3%5d.value=' + (organization.id / 1000000) + '%25&terms%5b3%5d.termType=like&terms%5b3%5d.type=and';
                } else if(organization.level == 1) {
                    str += '&terms%5b3%5d.column=organizationId&terms%5b3%5d.value=' + (organization.id / 1000) + '%25&terms%5b3%5d.termType=like&terms%5b3%5d.type=and';
                }else if(organization.level == 2) {
                    str += '&terms%5b3%5d.column=organizationId&terms%5b3%5d.value=' + organization.id + '&terms%5b3%5d.termType=eq&terms%5b3%5d.type=and';
                }
                if (data.order.length > 0) {
                    str += '&sorts%5b0%5d.name=createTime&sorts%5b0%5d.order=' + data.order[0].dir;
                }
                $.ajax({
                    url: BASE_PATH + "camera",
                    type: "GET",
                    data: str,
                    cache: false,
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
            }
        },
        columns: [
            {"data": null, "searchable": false, "orderable": false, "className": "exclude",
                render:function(data,type,row,meta) {
                    return meta.row + 1;
                }
            },
            {"data": "name", "orderable": false},
            {"data": "code", "searchable": false, "className": "exclude", "orderable": false},
            {"data": "ip", "orderable": false},
            {"data": "port", "searchable": false, "className": "exclude", "orderable": false},
            {"data": "note", "searchable": false, "className": "exclude", "orderable": false},
            {"data": "status", "searchable": false, "className": "exclude", "orderable": false},
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
                        buttons += '<button type="button" data-id="' + c.id + '" class="btn btn-default btn-xs btn-edit">编辑</button>\n';
                    }
                    if (accessDelete) {
                        buttons += '<button type="button" data-id="' + c.id + '" class="btn btn-danger btn-xs btn-delete">删除</button>';
                    }
                    return buttons;

                }
            }
        ],
        fnRowCallback: function (nRow, aData, iDataIndex) {
            var status = aData.status;
            var html = '<text aria-hidden="true" style="color: red" data-state = "' + status + '">未布防</text>';
            if (status == 1) {
                html = '<text aria-hidden="true" style="color: #00e765"  data-state = "' + status + '">已布防</text>';
            }
            $('td:eq(6)', nRow).html(html);
            return nRow;
        }

    });

    /**
     * 搜索条件
     */
    $(".box-header").off('click', '.btn-search').on('click', '.btn-search', function () {
        if ($("#searchIp").val().trim() == "") {
            $("#searchIp").val("");
        }
        if ($("#searchName").val().trim() == "") {
            $("#searchName").val("");
        }
        camera_list.draw(false);
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

    jQuery.validator.addMethod("ipValid", function (value, element) {
        var tel = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/;
        return tel.test(value) || this.optional(element);
    }, "请输入正确的IP");

    //新增或修改用户验证
    $("form#camera_form").validate({
        rules: {
            code: {required: true},
            name: {required: true},
            ip: {required: true, ipValid: true},
            port: {required: true, digits: true},
            note: {required: true, }
        },
        messages: {
            code: {required: "请输入设备编号"},
            name: {required: "请输入设备名称"},
            ip: {required: "请输入设备IP", ipValid: "请输入正确的IP"},
            port: {required: "请输入端口", digits: "请输入正确的端口号"},
            note: {required: "请输入备注"}
        },
        submitHandler: function (form) {
            var tree = $('#area_tree').treeview('getSelected')[0];
            if (isNaN(tree.id) || tree.level != 2) {
                toastr.warn("请选择子节点");
                return;
            }
            //提交数据
            var data = $("#camera_form").serializeArray();
            data.push({
                name: 'organizationId',
                value: tree.id
            })
            var dataJson = formatArray(data, "json");
            if (camera_id == '') {
                $('button[type="submit"]').attr('disabled', true);
                Request.post("camera", dataJson, function (e) {
                    $('button[type="submit"]').attr('disabled', false);
                    if (e.success) {
                        toastr.info("添加设备成功");
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
                    $('button[type="submit"]').attr('disabled', false);
                    if (e.success) {
                        toastr.info("修改设备成功");
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
    //新增设备弹出操作
    $(".box-header").off('click', '.btn-add').on('click', '.btn-add', function () {
        var tree = $('#area_tree').treeview('getSelected')[0];
        if (isNaN(tree.id) || tree.level != 2) {
            toastr.warn("请选择子节点");
            return;
        }
        camera_id = '';
        $(".modal-title").html("添加设备");
        clearData();
        $("#modal-add").modal('show');
    });

    //编辑设备弹出操作
    $("#camera_list").off('click', '.btn-edit').on('click', '.btn-edit', function () {
        var that = $(this);
        var id = that.data('id');
        camera_id = id;
        $(".modal-title").html("编辑设备");
        //加载编辑数据
        Request.get("camera/" + id, {}, function (e) {
            if (e.success) {
                var data = e.data;
                $("input#code").val(data.code);
                $("input#name").val(data.name);
                $("input#ip").val(data.ip);
                $("input#port").val(data.port);
                $("input#note").val(data.note);
            }
        });
        $("#modal-add").modal('show');
    });
    //用户删除
    $("#camera_list").off('click', '.btn-delete').on('click', '.btn-delete', function () {
        var that = $(this);
        var id = that.data('id');
        camera_id = id;
        $("#modal-delete").modal('show');

    });
    $("#modal-delete").off('click', '.btn-close-sure').on('click', '.btn-close-sure', function () {
        var id = camera_id;
        Request.delete("camera/" + id, {}, function (e) {
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
        $("input#port").val("");
        $("input#note").val("");
    }

});


