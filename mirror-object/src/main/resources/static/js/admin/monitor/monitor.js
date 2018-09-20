$(function () {


    /**
     * 初始化组织树
     * @type {boolean}
     */
    var inited = false;
    var organization_list = [];

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
                    if (selected.level === 3) {
                        Request.get("camera/" + selected.id, function (e) {
                            var url = "rtsp://" + e.data.account + ":" + e.data.password + "@" + e.data.ip + ":" + e.data.port
                                +"/MPEG-4/ch1/main/av_stream";
                            $('#monitor_video').val(url);
                            $('#vlc').show();
                        });
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


});

/**
 * 视频发生故障并且文件突然不可用时
 */
function emptied() {
    toastr.warning("请确认设备账号/密码/IP/端口是否正确");
}
