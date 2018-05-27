<%--
  Created by IntelliJ IDEA.
  User: owen
  Date: 2017-06-29
  Time: 14:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String uriPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    System.out.println(uriPath);
    System.out.println(1);
    System.out.println(path);
%>
<html>
<head>
    <title>a</title>
</head>
<body>
${a}|<%=request%>|<%=path%>|123
</body>
</html>
