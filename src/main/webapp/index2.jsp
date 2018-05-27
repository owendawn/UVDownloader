<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>主页</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
    hello , welcome to the index page<br>
    ${returnMsg}<br>
  <button onclick="sayHi();">How are You?</button>
    <button onclick="testTransaction();">事物测试</button>
  <script type="application/javascript">
      var Ajax={
          get: function (url,fn){
              var obj=new XMLHttpRequest();  // XMLHttpRequest对象用于在后台与服务器交换数据
              obj.open('GET',url,true);
              obj.onreadystatechange=function(){
                  if (obj.readyState == 4 && obj.status == 200 || obj.status == 304) { // readyState==4说明请求已完成
                      fn.call(this, obj.responseText);  //从服务器获得数据
                  }
              };
              obj.send(null);
          },
          post: function (url, data, fn) {
              var obj = new XMLHttpRequest();
              obj.open("POST", url, true);
              obj.setRequestHeader("Content-type", "application/x-www-form-urlencoded"); // 发送信息至服务器时内容编码类型
              obj.onreadystatechange = function () {
                  if (obj.readyState == 4 && (obj.status == 200 || obj.status == 304)) {  // 304未修改
                      fn.call(this, obj.responseText);
                  }
              };
              obj.send(data);
          }
      };

      function sayHi(){
        Ajax.post("<%=path%>/user/sayHi",null,function (data) {
           try{
               data=eval('('+data+')');
               data.success&&alert(data.msg);
           }catch (e){
               alert("error");
           }
        });
      }

      function testTransaction() {
          Ajax.get("<%=path%>/user/testTransaction",function (data) {
              try{
                  data=eval('('+data+')');
                  data.success&&alert(data.msg);
              }catch (e){
                  alert("error");
              }
          });
      }
  </script>
  </body>
</html>
