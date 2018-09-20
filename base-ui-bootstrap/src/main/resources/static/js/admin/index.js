var sidebar_menu_li = $(".sidebar-menu li.active");
sidebar_menu_li.parent().show();
sidebar_menu_li.parent().parent().addClass("menu-open");

$('.sidebar-toggle').on('click', function () {
    var flag = $('body').hasClass('sidebar-collapse');
    if (flag) {
        $.cookie('sidebar', 'sidebar-collapse', {expires: 30, path: '/'});
    } else {
        $.cookie('sidebar', '', {expires: 30, path: '/'});
    }
});
(function () {
    var cookie = $.cookie('sidebar');
    if (cookie == '') {
        $('body').addClass('sidebar-collapse');
    } else {
        $('body').removeClass('sidebar-collapse');
    }
})(jQuery);


/**
 * base option
 * @type {string}
 */
var lang = {
    "sProcessing": "处理中...",
    "sLengthMenu": "每页 _MENU_ 项",
    "sZeroRecords": "没有匹配结果",
    "sInfo": "当前显示第 _START_ 至 _END_ 项，共 _TOTAL_ 项",
    "sInfoEmpty": "",
    "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
    "sInfoPostFix": "",
    "sSearch": "<span class='fa fa-search'></span>",
    "sUrl": "",
    "sEmptyTable": "表中数据为空",
    "sLoadingRecords": "载入中...",
    "sInfoThousands": ",",
    "oPaginate": {
        "sFirst": "首页",
        "sPrevious": "上页",
        "sNext": "下页",
        "sLast": "末页",
        "sJump": "跳转"
    },
    "oAria": {
        "sSortAscending": ": 以升序排列此列",
        "sSortDescending": ": 以降序排列此列"
    },
    "searchPlaceholder": "按列搜索"
};

/* toastr options */
var opts = {
    closeButton: true,
    debug: false,
    positionClass: "toast-top-full-width",
    onclick: null,
    showDuration: "300",
    hideDuration: "1000",
    timeOut: "5000",
    extendedTimeOut: "1000",
    showEasing: "swing",
    hideEasing: "linear",
    showMethod: "fadeIn",
    hideMethod: "fadeOut"
};

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}


;(function () {
    var inited = false;
    //var dialogEle = $("#dialog-custom");

    var dialog = {
        ele: $("#dialog-custom"),
        init: function () {
            if (inited) {
                return this;
            }
            if (jQuery === undefined) {
                console.error("Required jQuery support is not available");
            } else {
                inited = true;
                var that = this;
                return this;
            }
        },
        alert: function (title, message, callback) {
            this.ele.find('.btn-apply').text('确认');
            this.baseDialog(title, message, callback, 'btn-primary btn-danger btn-warning btn-success', 'btn-info');
        },
        confirm: function (title, message, callback) {
            this.ele.find('.btn-apply').text('确认');
            this.baseDialog(title, message, callback, 'btn-info btn-danger btn-warning btn-success', 'btn-primary');
        },
        delete: function (title, message, callback) {
            this.ele.find('.btn-apply').text('确认删除');
            this.baseDialog(title, message, callback, 'btn-info btn-primary btn-danger btn-success', 'btn-warning');
        },
        error: function (title, message, callback) {
            this.ele.find('.btn-apply').text('确认');
            this.baseDialog(title, message, callback, 'btn-info btn-primary btn-warning btn-success', 'btn-danger');
        },
        baseDialog: function (title, message, callback, removeBtnClass, addBtnClass) {
            var that = this;

            if (typeof message === "function") {
                this.ele.find('.modal-title').html('提示');
                this.ele.find('.modal-body').html(title);
                that.bindCallback(message);
            } else if (typeof callback === "function") {
                this.ele.find('.modal-title').html(title);
                this.ele.find('.modal-body').html(message);
                that.bindCallback(callback);
            }
            this.ele.find('.btn-apply').removeClass(removeBtnClass).addClass(addBtnClass);
            that.show();
        },
        bindCallback: function (callback) {
            var that = this;
            that.ele.find('.btn-apply').off('click').on('click', function () {
                (callback && typeof(callback) === "function") && callback();
                that.ele.modal('hide');
            });
        },
        show: function () {
            this.ele.modal('show');
        },
        hide: function () {
            this.ele.modal('hide');
        }
    };

    window.dialog = dialog.init();
})();


/**
 * 确认提示框
 * @param title
 * @param message
 * @param callback
 */
var confirm = function (title, message, callback) {
    window.dialog.confirm(title, message, callback);
};


Socket.URL = "ws://" + window.location.host + BASE_PATH + "socket";

Request.BASH_PATH = BASE_PATH;

Request.get("online/total", function (e) {
    if (e.success) {
        $(".online-count").text(e.data);
    }
});

$(function () {
    $(".btn-logout").off('click').on('click', function () {
        console.log("logout btn clicked");
        Request.post("exit", null, function (e) {
            toastr.info("您已成功登出账户，3秒后跳转到登录页", opts);
            setTimeout(function () {
                document.execCommand("ClearAuthenticationCache");
                window.location.href = BASE_PATH;
            }, 2000);
        });
    });
});


function treeMenu(a) {
    this.tree = a || [];
    this.groups = {};
    this.flag = false;
    this.count = 0;
};

treeMenu.prototype = {
    init: function (pid) {
        this.group();
        return this.getDom(this.groups[pid]);
    },
    group: function () {
        for (var i = 0; i < this.tree.length; i++) {
            if (this.groups[this.tree[i].parentId]) {
                this.groups[this.tree[i].parentId].push(this.tree[i]);
            } else {
                this.groups[this.tree[i].parentId] = [];
                this.groups[this.tree[i].parentId].push(this.tree[i]);
            }
        }
        console.log(this.groups);
    },
    getDom: function (a) {
        if (!a) {
            return '';
        }  //当前节点不存在的时候，退出
        var html = '';

        html = this.flag ? '\n<ul class="treeview-menu">\n' : '';
        if (!this.flag) {
            this.flag = true;
        }
        for (var i = 0; i < a.length; i++) {
            html += '' + (a[i].uri == "" ? '<li class="treeview">' : '<li>');
            html += '<a href="' + (a[i].uri == "" ? "javascript:;" : a[i].uri) + '"><i class="fa fa-circle-o"></i> <span>' + a[i].name + '</span> <span class="pull-right-container"><i class="fa fa-angle-left pull-right"></i></span></a>';
            html += this.getDom(this.groups[a[i].id]);
            html += '</li>\n';
            this.count++;
        }
        console.log("count:", this.count, " length:", this.tree.length);
        html += this.count < this.tree.length ? '</ul>\n' : '';
        return html;
    }
};

Socket.open(function (socket) {
    if (socket) {
        //订阅在线人数推送
        socket.sub("online", {type: "total"}, "onlineUserTotal");
        socket.on("onlineUserTotal", function (e) {
            $(".online-count").text(e);
        });
    } else {
        // toastr.info("你的浏览器不支持websocket,部分功能可能无法正常使用!");
    }
});