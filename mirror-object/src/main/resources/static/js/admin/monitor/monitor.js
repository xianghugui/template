$(function () {
    /**
     * 初始化组织树
     * @type {boolean}
     */
    var inited = false;
    var pluginInstall = false; //是否安装过插件
    var szIP = []; //设备IP
    var monitor_list = null;

    var initOrganizationTree = function () {
        Request.get("organization/queryTree", function (e) {
            var tree = organizationTree.init();
            var rootNodes = tree.getRootNodes(e);
            $('#area_tree').treeview({
                data: rootNodes,
                levels: 3,
                onNodeSelected: function (event, data) {
                    if (pluginInstall) {
                        monitor_list.ajax.reload();
                    }
                }
            });
            initTable();
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
            if (level === 2) {
                result = result.concat().sort(function(a, b) {
                    return a.id - b.id;
                }).filter(function(item, index, array){
                    return !index || item.id !== array[index - 1].id
                });
            }
            return result;
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
    function initTable() {
        var langStr = lang;
        langStr.sLengthMenu = "_MENU_画面"
        langStr.sInfo = "当前显示第 _START_ 至 _END_ 画面，共 _TOTAL_ 画面"
        monitor_list = $('#monitor_table').DataTable({
            "language": langStr,
            "lengthMenu": [ 1, 4, 9, 16],
            "pageLength": 16,
            "lengthChange": true,
            "paging": true,
            "ordering": false,
            "searching": false,
            "destroy": true,
            "deferRender": true,
            "info": true,
            "autoWidth": false,
            "ajax": function (data, callback, settings) {
                var organization = $('#area_tree').treeview('getSelected')[0];
                if (typeof organization !== "undefined") {
                    var areaId = organization.id;
                    if (organization.level == 0) {
                        areaId /= 1000000;
                    } else if (organization.level == 1) {
                        areaId /= 1000;
                    }
                    if (organization.level === 3) {
                        Request.get("camera/" + areaId, function (result) {
                            var resultData = {};
                            resultData.draw = result.data.draw;
                            resultData.recordsTotal = 1;
                            resultData.recordsFiltered = 1;
                            resultData.data = [result.data];
                            if (resultData.data == null) {
                                resultData.data = [];
                            }
                            callback(resultData);
                        });
                    } else {
                        $.ajax({
                            url: BASE_PATH + "organization/" + areaId + "/camera",
                            type: "GET",
                            cache: false,
                            dataType: "json",
                            success: function (result) {
                                var resultData = {};
                                resultData.draw = result.data.draw;
                                resultData.recordsTotal = result.data.length;
                                resultData.recordsFiltered = result.data.length;
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
                    }
                }
            },
            "columns": [
                {
                    "data": "ip",
                    render: function (data, type, row, meta) {
                        return "";
                    }
                },
            ],
            "preDrawCallback": function( settings ) {
                for(var i = 0; i < szIP.length; i++){
                    WebVideoCtrl.I_Stop(i);
                    WebVideoCtrl.I_Logout(szIP[i]);
                }
                szIP = [];
                var organization = $('#area_tree').treeview('getSelected')[0];
                if (typeof organization !== "undefined" && organization.level == 3) {
                        WebVideoCtrl.I_ChangeWndNum(1);
                        return;
                }
                WebVideoCtrl.I_ChangeWndNum(Math.sqrt(settings._iDisplayLength));
            },
            "rowCallback": function(ro, data, displayNum, displayIndex, dataIndex ) {
                szIP.push(data.ip)
                clickLogin(data, displayNum)
            }
        });
    }

    /**
     * 实时预览
     */
    WebVideo();

    function WebVideo() {
        if (-1 == WebVideoCtrl.I_CheckPluginInstall()) {
            alert("您还未安装过插件，请安装WebComponentsKit.exe！");
            window.open(Request.BASH_PATH + "WebComponents/WebComponentsKit.exe")
            return;
        }
        pluginInstall = true;
        var width = $("#webVideo").width();
        var height = width * 3 / 5;
        $("#webVideo").css("height", height);
        WebVideoCtrl.I_InitPlugin(width, height, {
            bWndFull: true,
            iWndowType: 4
        });
        WebVideoCtrl.I_InsertOBJECTPlugin("webVideo");
    }

// 登录以及预览
    function clickLogin(data, index) {
        WebVideoCtrl.I_Login(data.ip, 1, data.httpPort, data.account, data.password, {
            success: function (xmlDoc) {
                WebVideoCtrl.I_StartRealPlay(data.ip, {
                    iWndIndex: index
                });
            },
            error: function (e) {
                toastr.warning("请确认IP/端口/用户名/密码是否正确");
            }
        });
    }

});
