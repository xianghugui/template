$(function () {


    /**
     * 初始化组织树
     * @type {boolean}
     */
    var inited = false;
    var organization_list = [];
    var target_list = null;
    var uploadId = null;

    var initOrganizationTree = function () {
        Request.get("organization/organizationTree", function (e) {
            if (e.success) {
                organization_list = e;
                var tree = organizationTree.init();
                var rootNodes = tree.getRootNodes(e.data);

                $('#area_tree').treeview({
                    data: rootNodes,
                    levels: 3,
                    onNodeSelected: function (event, data) {
                        $("#preview").hide();
                        uploadId = null;
                        initTable();
                    }
                });
                $('#area_tree').treeview('selectNode', [0]);
                initTable();
            }
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
                if (item['parentId'] == parentNode['id']) {
                    var obj = {
                        id: item.id,
                        level: level,
                        parentId: item.parentId,
                        text: item.name,
                        nodes: null
                    };
                    result.push(obj);
                    var childNodes = that.getChildNodes(data, item, level);
                    if (childNodes != null && childNodes.length > 0) {
                        obj.nodes = childNodes;
                    }
                }
            });
            return result;
        }
    };

    /**
     * dataTable
     */
    function initTable() {
        target_list = $('#target_list').DataTable({
            "language": lang,
            "lengthChange": false,
            "searching": false,
            "pageLength": 8,
            "paging": true,
            "serverSide": false,
            "destroy": true,
            "info": true,
            "ordering": false,
            "autoWidth": false,
            "order": [],
            "stripeClasses": ['col-md-3'],
            "ajax": function (data, callback, settings) {
                var organization = $('#area_tree').treeview('getSelected')[0];
                if (typeof organization !== "undefined") {
                    var param = {}
                    var str = "";
                    //区域树条件
                    if (organization.level == 0) {

                        param.organizationId = (organization.id / 1000000);
                    } else if (organization.level == 1) {
                        param.organizationId = (organization.id / 1000);
                    } else if (organization.level == 2) {
                        param.organizationId = (organization.id);
                    } else if (organization.level == 3) {
                        param.deviceId = (organization.id);
                    }

                    if ($('#searchStart').val() !== "") {
                        param.searchStart = $('#searchStart').val();
                    }
                    if ($('#searchEnd').val() !== "") {
                        param.searchEnd = $('#searchEnd').val();
                    }

                    if ($("#minSimilarity").val() !== "") {
                        var minSimilarity = $("#minSimilarity").val();
                        if (!checkNumber(minSimilarity)) {
                            toastr.warning("请输入1~100的数字");
                            return false;
                        }
                        param.minSimilarity = minSimilarity;
                    }

                    if ($("#maxSimilarity").val() !== "") {
                        var minSimilarity = $("#minSimilarity").val();
                        if (!checkNumber(minSimilarity)) {
                            toastr.warning("请输入1~100的数字");
                            return false;
                        }
                        param.maxSimilarity = $("#maxSimilarity").val();
                        param.maxSimilarity = maxSimilarity;
                    }
                    if (uploadId !== null) {
                        param.uploadId = uploadId;
                    }
                    $.ajax({
                        url: BASE_PATH + "aims/faceRecognize",
                        type: "GET",
                        data: param,
                        cache: false,
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
            },
            "columns": [
                {
                    "data": null,
                    render: function (data, type, row, meta) {
                        var html = "<div class='img-show-box'><div><image class='img' src='" + data.imageUrl + "'></image>" +
                            "<div class='time'>" + data.createTime + "</div></div></div>"
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
        todayBtn: "linked"
    });

    /**
     * 搜索
     */
    $(".form-inline").off('click', '.btn-search').on('click', '.btn-search', function () {
        initTable();
    });

    // /**
    //  * 上传图片
    //  */
    // $("#img_input").on('change', function (e) {
    //     // var file = $('#preview_size_fake');
    //     // var reader = new FileReader();
    //     // reader.readAsDataURL(file); // 读取文件
    //     // // 渲染文件
    //     // reader.onload = function (arg) {
    //         // $("#preview_img").attr("src", arg.target.result);
    //         // $("#preview_img").show();
    //         $.ajax({
    //             url: 'aims/upload',
    //             type: 'POST',
    //             cache: false,
    //             data: new FormData($('#uploadForm')[0]),
    //             processData: false,
    //             contentType: false,
    //             success:function (res) {
    //                 uploadId = res.data;
    //             },
    //             error:function (res) {
    //                 toastr.warning("请求列表数据失败, 请重试");
    //             }
    //         });
    //     };
    // });


    $("#file_upload").change(function () {
        var $file = $(this);
        var fileObj = $file[0];
        var windowURL = window.URL || window.webkitURL;
        var dataURL;
        var $img = $("#preview");
        if (fileObj && fileObj.files && fileObj.files[0]) {
            dataURL = windowURL.createObjectURL(fileObj.files[0]);
            $img.attr('src', dataURL);
        } else {
            //在IE下
            dataURL = $file.val();
            var imgObj = document.getElementById("preview");
            imgObj.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
            imgObj.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = dataURL;
        }

        $("#preview").show();

        var options = {
            url : "/aims/upload",
            success : function(res) {
                uploadId = res;
            },
            resetForm : true
        };
        $("#uploadForm").ajaxSubmit(options);

    });


    //验证字符串是否是数字
    function checkNumber(theObj) {
        var reg = /^[0-9]+.?[0-9]*$/;
        if (!reg.test(theObj)) {
            return false;
        }
        if (0 > theObj || theObj > 100) {
            return false;
        }
        return true;
    }
});


