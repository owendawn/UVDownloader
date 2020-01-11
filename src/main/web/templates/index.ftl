<#assign path=springMacroRequestContext.contextPath/>
<!DOCTYPE HTML>
<html>
<head>

    <title>引导页</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <script type="application/javascript" src="PanUtil.js"></script>
</head>

<body>
<h1>${projectName}</h1>
<div style="display: flex">
    <div style="width: 40%;">
        <h2>m3u8</h2>
        <hr>
        线程数(<span id="connectSize2">-</span>)
        <input type="range" min="1" max="100"  id="connectSize" onchange="changeConnectSize(this)">
        <button onclick="setConnectSize()">设置</button>
        <hr>
        <textarea rows="4" id="from" onchange="getFileName()" style="width: 100%"></textarea>
        <br>
        下载路径
        <input type="text" id="dir" value="${desktopPath}">
        <button onclick="document.getElementById('dir').value='${desktopPath}'">桌面</button>
        <br>
        文件名
        <input type="text" id="file">
        <br>
        <button onclick="download()">下载</button>

    </div>
    <div style="width: 60%">
        dkjdlf
        ${request.contextPath}|${path}|123
    </div>
</div>

<hr>

<script>
    document.getElementById("from").value="https://eth.ppzuida.com/20190423/5533_3984065b/1000k/hls/index.m3u8"
    function getFileName() {
        var url=document.getElementById("from").value.trim();
        if(url.indexOf("/")>=0){
            var str=url.substring(url.lastIndexOf("/")+1);
            document.getElementById("file").value= str.substring(0,str.indexOf("."))
        }else {
            document.getElementById("file").value = '';
        }
    }
    getFileName();

    PanUtil.ajax.get("/m3u8/getConnectSize",{},function (re) {
        document.getElementById("connectSize").value=re.data;
        document.getElementById("connectSize2").innerText=re.data;
    });

    function download() {
        PanUtil.ajax.post("/m3u8/download",{
            from:document.getElementById("from").value.trim(),
            dir:document.getElementById("dir").value.trim(),
            file:document.getElementById("file").value.trim(),
        },function (re) {

        })
    }
    function setConnectSize() {
        PanUtil.ajax.post("/m3u8/setConnectSize",{
            value:document.getElementById("connectSize").value,
        },function (re) {
            if(re.code===500){
                alert("操作失败");
                document.getElementById("connectSize").value=re.data;
                document.getElementById("connectSize2").innerText=re.data;
            }
        })
    }
    function changeConnectSize(dom) {
        document.getElementById("connectSize2").innerText=dom.value;
    }
</script>
</body>
</html>
