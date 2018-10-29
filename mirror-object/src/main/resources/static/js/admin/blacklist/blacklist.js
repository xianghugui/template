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
                        $('[name="my-checkbox"]').bootstrapSwitch({
                            onText: "启用",
                            offText: "禁用",
                            onColor: "success",
                            offColor: "warning",
                            size: "small",
                            onSwitchChange: function (event, state) {
                                var url = "/enable",
                                    id = event.target.value;
                                if (state) {
                                    url = "/disable";
                                    console.log(id);
                                }
                                Request.put("blacklist/" + id + url, null, function (e) {
                                    console.log(e);
                                })
                            }
                        });
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
                        var status = "";
                        var html = "<div class='img-show-box' class='btn btn-default' data-toggle='tooltip' data-placement='bottom' title='"
                            + data.createTime + "'><div class='img-box'><image class='img' src='" + data.imageUrl + "'></image></div>" +
                            "<div class='img-content'>" + data.name + "<button data-row='" + meta.row + "'  class='btn btn-default btn-xs btn-edit pull-right'>编辑</button></div>" +
                            "<div class='img-content'>" + data.code + "<button data-id='" + data.id + "'  class='btn btn-danger btn-xs btn-delete pull-right'>删除</button></div>";
                        if (data.status == 0) {
                            html += "<div class='img-content'>检索相识度:  " + data.similarity * 100 + "%<button type='button' data-id='" + data.id + "' class='btn btn-warning btn-xs btn-close pull-right'>禁用</button></div>";
                        }
                        else {
                            html += "<div class='img-content'>检索相识度:  " + data.similarity * 100 + "%<button type='button' data-id='" + data.id + "' class='btn btn-success btn-xs btn-open pull-right'>启用</button></div>";
                        }
                        html += "</div>";
                        return html;
                    }
                }
            ]
        });
    }

    //用户禁用
    $("#black_list").off('click', '.btn-close').on('click', '.btn-close', function () {
        var that = $(this);
        var id = that.data('id');
        Request.put("blacklist/" + id + "/disable", {}, function (e) {
            if (e.success) {
                black_list.draw();
                black_list.ajax.reload();
            } else {
                toastr.error(e.message);
            }
        });
    });

    //用户启用
    $("#black_list").off('click', '.btn-open').on('click', '.btn-open', function () {
        var that = $(this);
        var id = that.data('id');
        Request.put("blacklist/" + id + "/enable", {}, function (e) {
            if (e.success) {
                black_list.draw();
                black_list.ajax.reload();
            } else {
                toastr.error(e.message);
            }
        });
    });

    //新增设备弹出操作
    $(".box-header").off('click', '.btn-add').on('click', '.btn-add', function () {
        $("#uploadForm").validate().resetForm();
        $(".modal-title").html("添加");
        $('#upload_button').attr('disabled', false);
        $('#upload_button').show();
        black_id = "";
        clearData();
        $("#modal-add").modal('show');
    });

    //表单数据清空
    function clearData() {
        $("#preview").hide();
        $("input#name").val("");
        $("input#code").val("");
        $("input#similarity").val(50);
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
        $("input#similarity").val(data.similarity * 100);
        $("#preview").attr("src", data.imageUrl);
        $('#upload_button').attr('disabled', true);
        $('#upload_button').hide();
        $("#preview").show();
        //按比例设置图片的宽
        $("#modal-add").modal('show');
    });

    jQuery.validator.addMethod("fileValidator", function (value, element) {
        return black_id != "" || value != "";
    });

    // 身份证号码验证
    jQuery.validator.addMethod("isIdCardNo", function (value, element) {
        if (value.trim() == "") {
            return true;
        }
        return this.optional(element) || idCardNoUtil.checkIdCardNo(value);
    });

    //新增或修改
    $("form#uploadForm").validate({
        rules: {
            name: {required: true},
            code: {isIdCardNo: true},
            file: {fileValidator: true},
            similarity: {required: true}
        },
        messages: {
            name: {required: "请输入名称"},
            code: {isIdCardNo: "请输入正确的身份证号码"},
            file: {fileValidator: "请上传图片"},
            similarity: {required: "请输入相识度"}
        },
        submitHandler: function (form) {
            $('#insert').attr('disabled', true);
            if (black_id != '') {
                var data = formatArray($("#uploadForm").serializeArray());
                data.similarity = data.similarity / 100;
                Request.put("blacklist/" + black_id, JSON.stringify(data), function (e) {
                    $('#insert').attr('disabled', false);
                    if (e.success) {
                        toastr.info("更新成功");
                        $("#modal-add").modal('hide');
                        black_list.ajax.reload();
                    }
                    else {
                        toastr.error(e.message);
                    }

                });
            } else {
                var options = {
                    url: "/blacklist/upload",
                    beforeSubmit: validate,
                    success: function (res) {
                        $('#insert').attr('disabled', false);
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

    /* 数组转json
    * @param array 数组
    * @param type 类型 json array
    */
    function formatArray(array) {
        var dataArray = {};
        $.each(array, function () {
            if (dataArray[this.name]) {
                if (!dataArray[this.name].push) {
                    dataArray[this.name] = [dataArray[this.name]];
                }
                dataArray[this.name].push(this.value || '');
            } else {
                dataArray[this.name] = this.value || '';
            }
        });
        return (dataArray);
    }

    var idCardNoUtil = {
        provinceAndCitys: {
            11: "北京",
            12: "天津",
            13: "河北",
            14: "山西",
            15: "内蒙古",
            21: "辽宁",
            22: "吉林",
            23: "黑龙江",
            31: "上海",
            32: "江苏",
            33: "浙江",
            34: "安徽",
            35: "福建",
            36: "江西",
            37: "山东",
            41: "河南",
            42: "湖北",
            43: "湖南",
            44: "广东",
            45: "广西",
            46: "海南",
            50: "重庆",
            51: "四川",
            52: "贵州",
            53: "云南",
            54: "西藏",
            61: "陕西",
            62: "甘肃",
            63: "青海",
            64: "宁夏",
            65: "新疆",
            71: "台湾",
            81: "香港",
            82: "澳门",
            91: "国外"
        },

        powers: ["7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"],

        parityBit: ["1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"],

        genders: {male: "男", female: "女"},

        checkAddressCode: function (addressCode) {
            var check = /^[1-9]\d{5}$/.test(addressCode);
            if (!check) return false;
            if (idCardNoUtil.provinceAndCitys[parseInt(addressCode.substring(0, 2))]) {
                return true;
            } else {
                return false;
            }
        },

        checkBirthDayCode: function (birDayCode) {
            var check = /^[1-9]\d{3}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))$/.test(birDayCode);
            if (!check) return false;
            var yyyy = parseInt(birDayCode.substring(0, 4), 10);
            var mm = parseInt(birDayCode.substring(4, 6), 10);
            var dd = parseInt(birDayCode.substring(6), 10);
            var xdata = new Date(yyyy, mm - 1, dd);
            if (xdata > new Date()) {
                return false;//生日不能大于当前日期
            } else if ((xdata.getFullYear() == yyyy) && (xdata.getMonth() == mm - 1) && (xdata.getDate() == dd)) {
                return true;
            } else {
                return false;
            }
        },

        getParityBit: function (idCardNo) {
            var id17 = idCardNo.substring(0, 17);
            var power = 0;
            for (var i = 0; i < 17; i++) {
                power += parseInt(id17.charAt(i), 10) * parseInt(idCardNoUtil.powers[i]);
            }
            var mod = power % 11;
            return idCardNoUtil.parityBit[mod];
        },

        checkParityBit: function (idCardNo) {
            var parityBit = idCardNo.charAt(17).toUpperCase();
            if (idCardNoUtil.getParityBit(idCardNo) == parityBit) {
                return true;
            } else {
                return false;
            }
        },

        checkIdCardNo: function (idCardNo) {
            //15位和18位身份证号码的基本校验
            var check = /^\d{15}|(\d{17}(\d|x|X))$/.test(idCardNo);
            if (!check) return false;

            //判断长度为15位或18位
            if (idCardNo.length == 15) {
                return idCardNoUtil.check15IdCardNo(idCardNo);
            } else if (idCardNo.length == 18) {
                return idCardNoUtil.check18IdCardNo(idCardNo);
            } else {
                return false;
            }
        },

        //校验15位的身份证号码
        check15IdCardNo: function (idCardNo) {
            //15位身份证号码的基本校验
            var check = /^[1-9]\d{7}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))\d{3}$/.test(idCardNo);
            if (!check) return false;
            //校验地址码
            var addressCode = idCardNo.substring(0, 6);
            check = idCardNoUtil.checkAddressCode(addressCode);
            if (!check) return false;
            var birDayCode = '19' + idCardNo.substring(6, 12);
            //校验日期码
            return idCardNoUtil.checkBirthDayCode(birDayCode);
        },

        //校验18位的身份证号码
        check18IdCardNo: function (idCardNo) {
            //18位身份证号码的基本格式校验
            var check = /^[1-9]\d{5}[1-9]\d{3}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))\d{3}(\d|x|X)$/.test(idCardNo);
            if (!check) return false;

            //校验地址码
            var addressCode = idCardNo.substring(0, 6);
            check = idCardNoUtil.checkAddressCode(addressCode);
            if (!check) return false;

            //校验日期码
            var birDayCode = idCardNo.substring(6, 14);
            check = idCardNoUtil.checkBirthDayCode(birDayCode);
            if (!check) return false;

            //验证校检码
            return idCardNoUtil.checkParityBit(idCardNo);
        },
    };

    //添加提交相识度参数转换成小数
    function validate(formData, jqForm, options) {
        formData[2].value = formData[2].value / 100;
        return true;
    }
});
