$(document).ready(function () {
    var inited = false;
    var organization_list = [];

    var initOrganizationTree = function () {
        Request.get("organization/organizationTree", function (e) {
            organization_list = e;
            var tree = organizationTree.init();
            var rootNodes = tree.getRootNodes(e);

            $('#organization_tree').treeview({
                data: rootNodes,
                selectedBackColor: "#07100e",
                onNodeSelected: function (event, data) {
                    // setModuleInfo(organization_list[data.code]);
                }
            });

            // $('#organization_tree').treeview('selectNode', [0]);

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
        load: function () {

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

    // 添加节点
    $('.btn-tree-add').off('click').on('click', function () {
        var selected = $('#organization_tree').treeview('getSelected');
        if(selected !== null && selected.length !== 0 && selected[0].level === 2){
            toastr.warning("当前类别树只支持三级");
            return false;
        }
        $("#organization_name").val("");
        $("#add_organization_form").data("type", "0");
        $("#modal_organization_add").modal('show');
    });

    $("#add_organization_form").validate({
        rules: {
            organizationName: {required: true}
        },
        messages: {
            organizationName: {required: "组织名称不能为空"}
        },
        submitHandler: function () {
            var btn = $('#submit-parent'),
                selected = $('#organization_tree').treeview('getSelected');
            btn.attr('disabled', "true");
            btn.html("保存中..请稍后");
            var params = {
                name: $("#organization_name").val()
            };

            var type = true,
                req = Request.post;
            if ($('#add_organization_form').data("type") === '1') {
                type = false;
                params.id = $('#add_organization_form').data("id");
                req = Request.put;
            }
            else{
                if (selected !== null && selected.length !== 0) {
                    params.level = selected[0].level;
                    params.parentId = selected[0].id
                }
            }

            req("organization/" + (type ? "add" : "update"), JSON.stringify(params), function (e) {
                if (e.success) {
                    toastr.info("保存完毕");
                    $("#modal_organization_add").modal('hide');
                    initOrganizationTree();
                } else {
                    toastr.error(e.message);
                }
                btn.html("保存");
                btn.removeAttr('disabled');
            });
        }
    });

    // 删除节点
    $('.btn-tree-del').off('click').on('click', function () {
        var selected = $('#organization_tree').treeview('getSelected'),
            id = selected[0].id,
            organizationName = selected[0].text;
        confirm('警告', '真的要删除： [' + organizationName + '] 吗，如果为主节点，将会导致子节点都被删除?', function () {
            // 请求 module_id 删除
            Request.delete("organization/delete/" + id, {}, function (e) {
                if (e.success) {
                    toastr.success("删除成功");
                    initOrganizationTree();
                } else {
                    toastr.error(e.message);
                }
            });
        });
    });

    // 添加节点
    $('.btn-tree-edit').off('click').on('click', function () {
        var selected = $('#organization_tree').treeview('getSelected');
        if (selected === null || selected.length === 0) {
            toastr.warning("请选择要编辑的节点");
            return false;
        }
        $(".modal-title").html("编辑组织信息");
        $("#organization_name").val(selected[0].text);
        $("#add_organization_form").data("id", selected[0].id).data("type", "1");
        $("#modal_organization_add").modal('show');
    });

});