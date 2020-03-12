$(document).ready(function () {
    const userName = localStorage.getItem("userName");
    const userPass = localStorage.getItem("userPass");
    if (userName != null) {
        $("#userName").val(userName);
    }
    if (userPass != null) {
        $("#userPass").val(userPass);
    }
});

$('#btn-login').click(function () {
    let prevLink = document.referrer;
    $('#btn-login').button('loading');
    const name = $("#userName").val();
    const pwd = $("#userPass").val();
    const rememberMe = $("#rememberMe").prop("checked");
    if (name == "" || pwd == "") {
        showMsg("请输入完整信息！", "error", 1000);
        $('#btn-login').button('reset');
    } else {
        $.ajax({
            type: 'POST',
            url: '/login',
            async: false,
            data: {
                'userName': name,
                'userPass': pwd
            },
            success: function (data) {
                if (rememberMe) {
                    localStorage.setItem('userName', $("#userName").val());
                    localStorage.setItem('userPass', $("#userPass").val());
                } else {
                    localStorage.setItem('userName', '');
                    localStorage.setItem('userPass', '');
                }
                if (data.code == 1) {
                    if ($.trim(prevLink) == '' || $.trim(prevLink).indexOf('/login') != -1 || $.trim(prevLink).indexOf('/register') != -1 || $.trim(prevLink).indexOf('/forget') != -1) {
                        prevLink = '/';
                    }
                    showMsgAndRedirect(data.msg, "success", 1000, prevLink);
                } else {
                    showMsg(data.msg, "error", 1000);
                    $('#btn-login').button('reset');
                }
            }
        });
    }
})


$('#btn-register').click(function () {
    $('#btn-register').button('loading');
    const userName = $("#userName").val();
    const userPass = $("#userPass").val();
    const userEmail = $("#userEmail").val();
    if (userName == "" || userPass == "" || userEmail == "") {
        showMsg("请输入完整信息！", "error", 1000);
        $('#btn-register').button('reset');
    } else {
        $.ajax({
            type: 'POST',
            url: '/register',
            async: false,
            data: {
                'userName': userName,
                'userPass': userPass,
                'userEmail': userEmail
            },
            success: function (data) {
                if (data.code == 1) {
                    showMsgAndRedirect(data.msg, "success", 1000, "/login");
                } else {
                    showMsg(data.msg, "error", 1000);
                    $('#btn-register').button('reset');
                    localStorage.setItem('userName', userName);
                }
            }
        });
    }
});


$('#btn-forget').click(function () {
    const userName = $("#userName").val();
    const userEmail = $("#userEmail").val();
    if (userName == "" || userEmail == "") {
        showMsg("请输入完整信息！", "error", 1000);
        $('#btn-register').button('reset');
    } else {
        $.ajax({
            type: 'POST',
            url: '/forget',
            async: false,
            data: {
                'userName': userName,
                'userEmail': userEmail
            },
            success: function (data) {
                if (data.code == 1) {
                    showMsgAndRedirect(data.msg, "success", 2000, "/login");
                } else {
                    showMsg(data.msg, "error", 1000);
                    $('#btn-forget').button('reset');
                    localStorage.setItem('userName', userName);
                }
            }
        });
    }
});
