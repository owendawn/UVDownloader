<#assign path=springMacroRequestContext.contextPath/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

    <title>主页</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <script type="application/javascript" src="/PanUtil.js"></script>
</head>

<body>
<h2>hello , welcome to the index page</h2>
<h3>${returnMsg!''}</h3>
<button onclick="sayHi();">How are You?</button>
<button onclick="testTransaction();">事物测试</button>
<script type="application/javascript">
    function sayHi() {
        PanUtil.ajax.post("${path}/user/sayHi", null, function (data) {
            try {
                data = eval('(' + data + ')');
                data.success && alert(data.msg);
            } catch (e) {
                alert("error");
            }
        });
    }

    function testTransaction() {
        PanUtil.ajax.get("${path}/user/testTransaction", {}, function (data) {
            try {
                data = eval('(' + data + ')');
                data.code === 200 && alert(data.msg);
            } catch (e) {
                alert("error");
            }
        });
    }
</script>
</body>
</html>
