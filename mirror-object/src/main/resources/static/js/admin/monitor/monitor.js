$(function () {


    /**
     * 初始化组织树
     * @type {boolean}
     */
    var inited = false;
    var organization_list = [];
    var pluginInstall = false; //是否安装过插件
    var szIP = ""; //设备IP

    var initOrganizationTree = function () {
        Request.get("organization/queryTree", function (e) {
            organization_list = e;
            var tree = organizationTree.init();
            var rootNodes = tree.getRootNodes(e);

            $('#area_tree').treeview({
                data: rootNodes,
                levels: 3,
                onNodeSelected: function (event, data) {
                    var selected = $('#area_tree').treeview('getSelected')[0];
                    if (pluginInstall) {
                        if (selected.level === 3) {
                            Request.get("camera/" + selected.id, function (e) {
                                clickLogout();
                                clickLogin(e.data);
                                var url = "rtsp://" + e.data.account + ":" + e.data.password + "@" + e.data.ip + ":" + e.data.httpPort
                                    + "/MPEG-4/ch1/main/av_stream";
                                $('#monitor_video').val(url);
                                $('#vlc').show();
                            });
                        }
                    }
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

    /**
     * 实时预览
     */
    WebVideo();

    function WebVideo() {
        if (-1 == WebVideoCtrl.I_CheckPluginInstall()) {
            alert("您还未安装过插件，请安装WebComponents.exe！");
            Request.get("WebComponents/WebComponents.exe", {}, function () {});
            return;
        }
        pluginInstall = true;
        var width = $("#webVideo").width();
        var height = width * 3 / 5;
        $("#webVideo").css("height", height);
        WebVideoCtrl.I_InitPlugin(width, height, {
            iWndowType: 4
        });
        WebVideoCtrl.I_InsertOBJECTPlugin("webVideo");
    }

// 登录
    function clickLogin(data) {
        WebVideoCtrl.I_Login(data.ip, 1, data.httpPort, data.account, data.password, {
            success: function (xmlDoc) {
                szIP = data.ip;
                //预览
                WebVideoCtrl.I_StartRealPlay(szIP, {});
            },
            error: function (e) {
                console.log(e)
                toastr.warning("请确认IP/端口/用户名/密码是否正确");
            }
        });
    }

// 退出
    function clickLogout() {
        if (szIP == "") {
            return;
        }
        WebVideoCtrl.I_Logout(szIP);
        szIP = "";
    }
});
