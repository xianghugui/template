$(function () {


    /**
     * 初始化组织树
     * @type {boolean}
     */
    var inited = false;
    var organization_list = [];
    var target_list = null;

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
                        initFileInput();
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
    function initTable(uploadFaceFeature) {
        target_list = $('#target_list').DataTable({
            "language": lang,
            "lengthChange": false,
            "searching": false,
            "pageLength": 8,
            "serverSide": true,
            "destroy": true,
            "info": true,
            "ordering": false,
            "autoWidth": false,
            "order": [],
            "stripeClasses": ['col-md-3'],
            "ajax": function (data, callback, settings) {
                var organization = $('#area_tree').treeview('getSelected')[0];
                if (typeof organization !== "undefined") {
                    var str = "pageSize=" + data.length + "&pageIndex=" + data.start;
                    //区域树条件
                    if (organization.level == 0) {
                        str += '&terms%5b3%5d.column=organizationId&terms%5b3%5d.value=' + (organization.id / 1000000) + '%25&terms%5b3%5d.termType=like&terms%5b3%5d.type=and';
                    } else if (organization.level == 1) {
                        str += '&terms%5b3%5d.column=organizationId&terms%5b3%5d.value=' + (organization.id / 1000) + '%25&terms%5b3%5d.termType=like&terms%5b3%5d.type=and';
                    } else if (organization.level == 2) {
                        str += '&terms%5b3%5d.column=organizationId&terms%5b3%5d.value=' + organization.id + '&terms%5b3%5d.termType=eq&terms%5b3%5d.type=and';
                    }
                    //按时间排序
                    str += '&sorts%5b0%5d.name=createTime&sorts%5b0%5d.order=desc';
                    $.ajax({
                        url: BASE_PATH + "aims/select",
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
                        var html = "<div><image class='img' src='" + data.imageUrl + "'></image>" +
                            "<div class='time'>"+data.createTime+"</div></div>"
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
        console.log($("#minSimilarity").val())
        console.log($("#maxSimilarity").val())
        console.log($("#searchStart").val())
        console.log($("#searchEnd").val())

    });

    function initFileInput(){
        //初始化图片上传控件
        $("input#img_input").fileinput('destroy');
        delete fileinputoption.initialPreview;
        delete fileinputoption.initialPreviewConfig;
        $("input#img_input").fileinput(fileinputoption);
        $("#img_input").fileinput('refresh').fileinput('enable');
    }

    /**
     * 上传图片
     */
        // $("#img_input").on('change', function (e) {
        //     var file = e.target.files[0];
        //     if (!file.type.match('image.*')) {
        //         return false;
        //     }
        //     // document.getElementById("img_input").select();
        //     // console.log(document.selection.createRange().text)
        //     var reader = new FileReader();
        //     reader.readAsDataURL(file); // 读取文件
        //     // 渲染文件
        //     reader.onload = function (arg) {
        //         // console.log(arg.target.result)
        //         $("#preview_img").attr("src", arg.target.result);
        //         $("#preview_img").show();
        //         var data = $('#img_input').files;
        //         $.ajax({
        //             url: '/upload',
        //             type: 'POST',
        //             cache: false,
        //             data: new FormData($('#uploadForm')[0]),
        //             processData: false,
        //             contentType: false
        //         }).done(function(res) {
        //         }).fail(function(res) {});
        //     }
        // });

    var fileinputoption = {
            required: true,
            uploadUrl: Request.BASH_PATH + 'aims/uploadFaceImage',
            dropZoneTitle: "上传图片",
            language: 'zh', //设置语言
            showUpload: false, //是否显示上传按钮
            showRemove: false,
            showCaption: false,//是否显示标题
            showClose: false,
            allowedPreviewTypes: ['image'],
            allowedFileTypes: ['image'],
            allowedFileExtensions: ['jpg', 'gif', 'png'],
            maxFileCount: 1,
            maxFileSize: 2000,
            autoReplace: true,
            validateInitialCount: false,
            overwriteInitial: false,
            initialPreviewAsData: false,
            uploadAsync: true //同步上传
        };

    $('#img_input').fileinput(fileinputoption);

    $("#img_input").on("filebatchselected", function (event, files) {
        console.log(files);
    });
});


