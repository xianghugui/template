$(function () {


    /**
     * 初始化组织树
     * @type {boolean}
     */
    var inited = false;
    var organization_list = [];
    var face_list = null;

    var initOrganizationTree = function () {
        Request.get("organization/queryTree", function (e) {
            organization_list = e;
            var tree = organizationTree.init();
            var rootNodes = tree.getRootNodes(e);

            $('#area_tree').treeview({
                data: rootNodes,
                levels: 3,
                onNodeSelected: function (event, data) {
                    initTable();
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
                        id: item.organizationId,
                        level: level,
                        parentId: item.parentId,
                        text: item.organizationName,
                        nodes: []
                    };
                    obj.nodes = that.getChildNodes(data, item, level);
                    result.push(obj);
                }
            });
            return result;
        },
        getChildNodes: function (data, parentNode, level) {
            var that = this;
            var result = [];
            level++;
            $.each(data, function (i, item) {
                if (item['parentId'] == parentNode['organizationId']) {
                    var obj = {
                        id: item.organizationId,
                        level: level,
                        parentId: item.parentId,
                        text: item.organizationName,
                        nodes: null
                    };
                    result.push(obj);
                    var childNodes = that.getChildNodes(data, item, level);
                    if (childNodes != null && childNodes.length > 0) {
                        obj.nodes = childNodes;
                    } else {
                        obj.nodes = that.getMonitor(data, item, level);
                    }
                }
            });
            return result.concat().sort(function(a, b) {
                return a.id - b.id;
            }).filter(function(item, index, array){
                return !index || item.id !== array[index - 1].id
            });
        },
        getMonitor: function (data, parentNode, level) {
            var that = this;
            var result = [];
            level++;
            $.each(data, function (i, item) {
                if (item['deviceId'] !== null && item['organizationId'] === parentNode['organizationId']) {
                    var obj = {
                        id: item.deviceId,
                        level: level,
                        parentId: item.organizationId,
                        text: item.deviceName,
                        nodes: null
                    };
                    result.push(obj);
                }
            });
            return result;
        }
    };

    /**
     * dataTable
     */
    function initTable() {
        face_list = $('#face_list').DataTable({
            "language": lang,
            "lengthChange": false,
            "searching": false,
            "pageLength": 16,
            "serverSide": true,
            "destroy": true,
            "info": true,
            "ordering": false,
            "autoWidth": false,
            "order": [],
            "stripeClasses": [ 'col-md-3' ],
            "ajax": function (data, callback, settings) {
                var organization = $('#area_tree').treeview('getSelected')[0];
                if (typeof organization !== "undefined") {
                    var param = {
                        pageIndex: settings._iDisplayStart,
                        pageSize: settings._iDisplayLength
                    }
                    if (organization.level == 0) {

                        param.organizationId = (organization.id / 1000000);
                    } else if (organization.level == 1) {
                        param.organizationId = (organization.id / 1000);
                    } else if (organization.level == 2) {
                        paramorganizationId = (organization.id);
                    } else if (organization.level == 3) {
                        param.deviceId = (organization.id);
                    }

                    if ($('#searchStart').val() !== "") {
                        param.searchStart = $('#searchStart').val();
                    }
                    if ($('#searchEnd').val() !== "") {
                        param.searchEnd = $('#searchEnd').val();
                    }
                    $.ajax({
                        url: BASE_PATH + "aims/faceimage",
                        type: "GET",
                        data: param,
                        cache: false,
                        dataType: "json",
                        success: function (result) {
                            var resultData = {};
                            resultData.draw = result.data.data.draw;
                            resultData.recordsTotal = result.data.total;
                            resultData.recordsFiltered = result.data.total;
                            resultData.data = result.data.data;
                            if (resultData.data == null) {
                                resultData.data = [];
                            }
                            callback(resultData);
                        },
                        error: function () {
                            toastr.warning("请求列表数据失败, 请重试");
                        }
                    });
                }
            },
            "columns": [
                {
                    "data": null,
                    render: function (data, type, row, meta) {
                        var html = "<div class='img-show-box'><image class='img' src='" + data.imageUrl + "'></image>" +
                            "<div class='img-content'><div>"+data.name+"</div><div>"+data.createTime+"</div></div></div>"
                        return html;
                    }
                },
            ]
        });
    }

    /**
     * 时间选择控件
     */
    $('.form_datetime').datetimepicker({
        format: 'yyyy-mm-dd hh:00',
        language: 'zh-CN',
        autoclose: true,
        minView: 1,
        todayBtn: "linked",

    });

    /**
     * 搜索
     */
    $(".form-inline").off('click', '.btn-search').on('click', '.btn-search', function () {
        face_list.ajax.reload();
    });
});
