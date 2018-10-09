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
        Request.get("organization/queryTree", function (e) {
            organization_list = e;
            var tree = organizationTree.init();
            var rootNodes = tree.getRootNodes(e);

            $('#area_tree').treeview({
                data: rootNodes,
                levels: 3,
                onNodeSelected: function (event, data) {
                    $("#preview").hide();
                    uploadId = null;
                    target_list.ajax.reload();
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
            return result.concat().sort(function (a, b) {
                return a.id - b.id;
            }).filter(function (item, index, array) {
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


                    var minSimilarity = $("#minSimilarity").val();
                    if (!checkNumber(minSimilarity)) {
                        toastr.warning("请输入0~100的相识度");
                        return false;
                    }
                    param.minSimilarity = minSimilarity;


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
                        var html = "<div class='img-show-box'><image class='img' src='" + data.imageUrl + "'></image>" +
                            "<div class='img-content'><div>" + data.name + "</div>" +
                            "<div>" + data.createTime;
                        if (data.similarity != null) {
                            html += "<span class='similarity-box'>" + parseInt(data.similarity * 100) + "%</span>";
                        }
                        html += "</div></div></div>";
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
     * 设置默认筛选条件
     */
    function getNowFormatDate() {
        var date = new Date();
        var seperator1 = "-";
        var seperator2 = ":";
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var searchStart = date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + "00" + seperator2 + "00" + seperator2 + "00";

        var searchEnd = date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + date.getHours() + seperator2 + date.getMinutes()
            + seperator2 + date.getSeconds();

        $('#searchStart').val(searchStart);
        $('#searchEnd').val(searchEnd);
        $('#minSimilarity').val(40);
    }

    getNowFormatDate();


    /**
     * 搜索事件
     */
    $(".form-inline").off('click', '.btn-search').on('click', '.btn-search', function () {
        target_list.ajax.reload();
    });


    /**
     * 上传图片
     */
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

            //在IE9下,获取图片绝对路径
            var imgObj = document.getElementById("preview");
            var file = document.getElementById("file_upload");
            file.select();
            file.blur();
            var dataURL = document.selection.createRange().text;
            document.selection.empty();
            imgObj.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + dataURL + "',sizingMethod=image)";
            //图片必须显示出来,才能获取原图片的高和宽
            $("#preview").show();

            //获取原图片的高和宽
            var nWidth = imgObj.offsetWidth;
            var nHight = imgObj.offsetHeight;
            //按比例设置图片的宽
            var imgWidth = parseInt(nWidth * (200 / nHight));
            $('.preview_img').css("width",imgWidth);
            
            imgObj.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + dataURL + "',sizingMethod=scale)";
        }

        $('#upload_button').attr('disabled', "true");
        //用ajaxSubmit提交图片
        var options = {
            url: "/aims/upload",
            success: function (res) {
                uploadId = null;
                $('#upload_button').removeAttr('disabled');
                if (res == "没有获取到特征值,请重新上传图片") {
                    toastr.warning("没有获取到特征值,请重新上传图片");
                    return false;
                }
                else if (res == "检测到多张人脸,请重新上传图片") {
                    toastr.warning("检测到多张人脸,请重新上传图片");
                    return false;
                }
                uploadId = res;
            },
            resetForm: true
        };
        $("#uploadForm").ajaxSubmit(options);

    });


    //验证字符串是否是数字
    function checkNumber(theObj) {
        var reg = /^[0-9]+.?[0-9]*$/;
        if (theObj == "") {
            return false;
        }
        if (!reg.test(theObj)) {
            return false;
        }
        if (0 > theObj || theObj > 100) {
            return false;
        }
        return true;
    }
});


