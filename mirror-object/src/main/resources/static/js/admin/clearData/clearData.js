$(function () {
    $("#clear_from").validate({
        submitHandler: function () {
            var searchStart = $("#searchStart").val();
            var searchEnd = $("#searchEnd").val();
            if (searchStart === "") {
                toastr.warning("请选择开始时间");
                return false;
            }
            if (searchEnd === "") {
                toastr.warning("请选择结束时间");
                return false;
            }
            if (searchStart > searchEnd) {
                toastr.warning("开始时间不能大于结束时间");
                return false;
            }
            var uploadValue = {
                searchStart: searchStart,
                searchEnd: searchEnd
            };

            $('.btn').button('loading');

            Request.post("clearData/delete", uploadValue, function (e) {
                if (e.success) {
                    $('.btn').button('reset');
                    toastr.success("清空完毕");
                } else {
                    toastr.error(e.message);
                }
            });
        }
    });

    /**
     * 时间选择控件
     */

    $('.form_datetime').datetimepicker({
        format: 'yyyy-mm-dd',
        language: 'zh-CN',
        autoclose: true,
        minView: 2,
        todayBtn: "linked"
    });

    toastr.options = {
        "timeOut": "800"
    }
});


