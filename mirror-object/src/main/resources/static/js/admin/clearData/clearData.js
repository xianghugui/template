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
            if(GetDateDiff(searchStart,searchEnd) > 30){
                toastr.warning("数据量过大,时间间隔不能超过30天");
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

    /**
     * 计算时间间隔
     */

    function GetDateDiff(startDate,endDate)
    {
        var startTime = new Date(Date.parse(startDate.replace(/-/g,   "/"))).getTime();
        var endTime = new Date(Date.parse(endDate.replace(/-/g,   "/"))).getTime();
        var dates = Math.abs((startTime - endTime))/(1000*60*60*24);
        return  dates;
    }
});


