<#assign path=springMacroRequestContext.contextPath/>
<!DOCTYPE HTML>
<html>
<head>

    <title>引导页</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>

</head>

<body>
<h1>${projectName}</h1>
<h2>引导页</h2>

<a href="${path}/view/index2">首页</a>
<br>
<a href="${path}/view/login">登录</a>

<hr>
${request.contextPath}|${path}|123
</body>
</html>
