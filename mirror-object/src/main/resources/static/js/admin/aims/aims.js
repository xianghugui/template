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
                    getNowFormatDate();
                }
            });
            $('#area_tree').treeview('selectNode', [0]);
            initTable();
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
        var language = lang;
        lang.sProcessing = "请稍等，数据检索中......";
        target_list = $('#target_list').DataTable({
            "language": language,
            "lengthChange": false,
            "searching": false,
            "pageLength": 8,
            "processing": true,
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
                var param = {};
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
                param.minSimilarity = minSimilarity;
                param.uploadId = uploadId;
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
            },
            "columns": [
                {
                    "data": null
                },
            ],
            "rowCallback": function (row, data) {
                var html = "<div class='img-show-box'><image class='img' src='" + BASE_PATH + "file/image/" + data.resourceId + "'></image>";
                if (data.blackListName != null && data.blackListName != 0) {
                    html += "<div class='img-content'><div>" + data.deviceName + "</div>";
                    if (data.similarity != null) {
                        html += "<div>" + data.createTime + "<span class='similarity-box'>" + parseInt(data.similarity * 100) + "%</span></div>";
                    }
                    html += "</div><div class='blackListName-box'>" + data.blackListName + "<span class='black-code'>" + data.code + "</span></div>";
                }
                else {
                    html += "<div class='img-content'><div class='aims-name'>" + data.deviceName + "</div>";
                    if (data.similarity != null) {
                        html += "<div>" + data.createTime + "<span class='similarity-box'>" + parseInt(data.similarity * 100) + "%</span></div>";
                    }
                }
                html += "</div></div>";
                $(row).html(html);
            }
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
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var searchStart = date.getFullYear() + "-" + month + "-" + strDate
            + " " + "00" + ":" + "00" + ":" + "00";

        var searchEnd = date.getFullYear() + "-" + month + "-" + strDate
            + " " + date.getHours() + ":" + date.getMinutes()
            + ":" + date.getSeconds();
        $('#searchStart').val(searchStart);
        $('#searchEnd').val(searchEnd);
        $('#minSimilarity').val(40);
    }


    /**
     * 搜索事件
     */

    $(".form-inline").off('click', '.btn-search').on('click', '.btn-search', function () {
        var organization = $('#area_tree').treeview('getSelected')[0];
        if (GetDateDiff($('#searchStart').val(), $('#searchEnd').val()) > 7) {
            toastr.warning("数据量过大,查询时间间隔不能超过7天");
            return false;
        }
        if (typeof organization === "undefined") {
            toastr.warning("请选中节点后再搜索");
            return false;
        }
        else if (uploadId == null) {
            toastr.warning("请先上传搜索图片后再搜索");
            return false;
        }
        else if ($('#searchStart').val() >= $('#searchEnd').val()) {
            toastr.warning("开始时间必须小于结束时间");
            return false;
        }
        else if (!checkNumber($("#minSimilarity").val())) {
            toastr.warning("请输入0~100的相识度");
            return false;
        }
        target_list.ajax.reload();
        $('#target_list').show();
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
            $("#preview").show();
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
            $('.preview_img').css("width", imgWidth);

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

    /**
     * 图片双击预览
     */

    $('#target_list').on("dblclick", ".img", function () {
        var _self = $(this);
        $('#img_show').attr('src', _self[0].src);
        $('#modal_img_show').modal("show");
    });

    /**
     * 计算时间间隔
     */
    function GetDateDiff(startDate, endDate) {
        var startTime = new Date(Date.parse(startDate.replace(/-/g, "/"))).getTime();
        var endTime = new Date(Date.parse(endDate.replace(/-/g, "/"))).getTime();
        var dates = Math.abs((startTime - endTime)) / (1000 * 60 * 60 * 24);
        return dates;
    }
});


