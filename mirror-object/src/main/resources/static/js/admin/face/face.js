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
});
