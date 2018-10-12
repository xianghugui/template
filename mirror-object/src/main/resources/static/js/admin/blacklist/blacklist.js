$(function () {

    var black_list = null;
    var black_id = "";
    initTable();

    /**
     * dataTable
     */
    function initTable() {
        black_list = $('#black_list').DataTable({
            "language": lang,
            "lengthChange": false,
            "searching": true,
            "pageLength": 16,
            "dom": 'lrtip',
            "processing": true,
            "serverSide": false,
            "destroy": true,
            "info": true,
            "ordering": false,
            "autoWidth": false,
            "order": [],
            "stripeClasses": ['col-md-3'],
            "ajax": function (data, callback, settings) {
                $.ajax({
                    url: BASE_PATH + "blacklist",
                    type: "GET",
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

            },
            "columns": [
                {
                    className: "col-md-3",
                    "data": null,
                    render: function (data, type, row, meta) {
                        var html = "<div class='img-show-box' class='btn btn-default' data-toggle='tooltip' data-placement='bottom' title='"
                            + data.createTime +"'><div class='img-box'><image class='img' src='" + data.imageUrl + "'></image></div>" +
                            "<div class='img-content'>" + data.name + "<button data-row='" + meta.row + "'  class='btn btn-default btn-xs btn-edit pull-right'>编辑</button></div>" +
                            "<div class='img-content'>" + data.code + "<button data-id='" + data.id + "'  class='btn btn-danger btn-xs btn-delete pull-right'>删除</button></div>" +
                            "</div>"
                        return html;
                    }
                },
            ]
        });
    }

    //新增设备弹出操作
    $(".box-header").off('click', '.btn-add').on('click', '.btn-add', function () {
        $("#uploadForm").validate().resetForm();
        $(".modal-title").html("添加");
        black_id = "";
        clearData();
        $("#modal-add").modal('show');
    });

    //表单数据清空
    function clearData() {
        $("#preview").hide();
        $("input#name").val("");
        $("input#code").val("");
        $("input#file_upload").val("");
        $("#preview").attr("src", "");
        $("#preview").hide();
    }

    //编辑设备弹出操作
    $("#black_list").off('click', '.btn-edit').on('click', '.btn-edit', function () {
        $("#uploadForm").validate().resetForm();
        $(".modal-title").html("编辑");
        var data = black_list.row($(this).data("row")).data();
        black_id = data.id;
        $("input#name").val(data.name);
        $("input#code").val(data.code);
        $("#preview").attr("src", data.imageUrl);
        $("#preview").show();
        var imgObj = document.getElementById("preview");
        var nWidth = imgObj.offsetWidth;
        var nHight = imgObj.offsetHeight;
        //按比例设置图片的宽
        var imgHeight = parseInt(nHight * (370 / nWidth));
        $("#modal-add").modal('show');
    });

    jQuery.validator.addMethod("fileValidator", function (value, element) {
        console.log(black_id)
        console.log(value)
        return black_id != "" || value != "";
    });

    //新增或修改
    $("form#uploadForm").validate({
        rules: {
            name: {required: true},
            code: {required: true},
            file: {fileValidator: true},
        },
        messages: {
            name: {required: "请输入名称"},
            code: {required: "请输入身份证号码"},
            file: {fileValidator: "请上传图片"},
        },
        submitHandler: function (form) {
            var url = "/blacklist/upload";
            if (black_id != '') {
                url = "/blacklist/" + black_id;
            }
            var options = {
                url: url,
                success: function (res) {
                    if (res < 0) {
                        toastr.warning("没有获取到特征值,请重新上传图片");
                        return false;
                    }
                    toastr.info("添加成功");
                    $("#modal-add").modal('hide');
                    black_list.ajax.reload();
                },
            };
            $("#uploadForm").ajaxSubmit(options);
        }
    });

    //用户删除
    $("#black_list").off('click', '.btn-delete').on('click', '.btn-delete', function () {
        black_id = $(this).data('id');
        $("#modal-delete").modal('show');

    });
    $("#modal-delete").off('click', '.btn-close-sure').on('click', '.btn-close-sure', function () {
        Request.delete("blacklist/" + black_id, {}, function (e) {
            if (e.success) {
                toastr.info("删除成功!");
                black_list.ajax.reload();
            } else {
                toastr.error(e.message);
            }
        });
    });

    /**
     * 预览图片
     */
    $("#file_upload").change(function () {
        $("#preview").attr("src", "");
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
            var imgHeight = parseInt(nHight * (370 / nWidth));
            $('.preview_img').css("height", imgHeight);
            imgObj.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + dataURL + "',sizingMethod=scale)";
        }
    });

    /**
     * 搜索事件
     */
    $(".form-inline").off('keyup', '#search').on('keyup', '#search', function () {
        black_list.search($("#search").val().trim()).draw();
    });

    /**
     * 图片双击预览
     */
    $('#black_list').on("dblclick", ".img", function () {
        var _self = $(this);
        $('#img_show').attr('src', _self[0].src);
        $('#modal_img_show').modal("show");
    });
});
