<#assign path=springMacroRequestContext.contextPath/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>登录</title>
	  <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
  </head>
  
  <body>
  <h2>hello please login first </h2>
    <form method="post" action="${path}/user/login?logined=true">
    	用户名:<input type="text" name="name"><br>
    	<input type="submit" value="提交">
    	<br>
    	<span style="color:red;display:<#if returnMsg?exists>'none'<#else> 'block'</#if>;">${returnMsg!''}</span>
    	
    	
    </form>
  </body>
</html>
