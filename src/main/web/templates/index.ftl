<#assign path=springMacroRequestContext.contextPath/>
<!DOCTYPE HTML>
<html>
<head>

    <title>引导页</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="PanCss.css">
    <script type="application/javascript" src="PanUtil.js"></script>
</head>

<body>
<h1 style="text-align: center;font-size: 5rem;padding: 1rem;text-shadow: 3px 5px 8px #504040;">
    ${projectName}
</h1>
<div style="display: flex">
    <div style="width: 40%;">
        <hr>
        <h2 style="font-size: 4rem;font-style: italic;text-align: center;line-height: 0;color: darkgray;">m3u8</h2>
        <hr>
        <p>
            线程数(<span id="connectSize2">-</span>)
            <input type="range" min="1" max="100" id="connectSize" onchange="changeConnectSize(this)">
            <button onclick="setConnectSize()">设置</button>
        </p>
        <hr>
        <p>
            <textarea rows="4" id="from" onchange="getFileName()" style="width: 100%"></textarea>
        </p>
        <p>
            下载路径
            <input type="text" id="dir" value="${desktopPath}">
            <button onclick="document.getElementById('dir').value='${desktopPath}'">桌面</button>
        </p>
        <p>
            文件名
            <input type="text" id="file">
        </p>
        <p>
            <button onclick="download()">下载</button>
            <button onclick="refreshJobs()">刷新</button>
        </p>
    </div>
    <div style="width: 60%">
        <p>
        <table>
            <thead>
            <tr>
                <th>参数</th>
                <th>值</th>
            </tr>
            </thead>
            <tbody id="tbody">
            </tbody>
        </table>
        </p>
    </div>
</div>

<hr>

<script>
    document.getElementById("from").value = "https://eth.ppzuida.com/20190423/5533_3984065b/1000k/hls/index.m3u8"

    function getFileName() {
        var url = document.getElementById("from").value.trim();
        if (url.indexOf("/") >= 0) {
            var str = url.substring(url.lastIndexOf("/") + 1);
            document.getElementById("file").value = str.substring(0, str.indexOf("."))
        } else {
            document.getElementById("file").value = '';
        }
    }

    getFileName();

    PanUtil.ajax.get("/m3u8/getConnectSize", {}, function (re) {
        document.getElementById("connectSize").value = re.data;
        document.getElementById("connectSize2").innerText = re.data;
    });

    function download() {
        PanUtil.ajax.post("/m3u8/download", {
            from: document.getElementById("from").value.trim(),
            dir: document.getElementById("dir").value.trim(),
            file: document.getElementById("file").value.trim(),
        }, function (re) {

        })
    }

    function setConnectSize() {
        PanUtil.ajax.post("/m3u8/setConnectSize", {
            value: document.getElementById("connectSize").value,
        }, function (re) {
            if (re.code === 500) {
                alert("操作失败");
                document.getElementById("connectSize").value = re.data;
                document.getElementById("connectSize2").innerText = re.data;
            }
        })
    }

    function changeConnectSize(dom) {
        document.getElementById("connectSize2").innerText = dom.value;
    }

    function refreshJobs() {
        PanUtil.ajax.get("/m3u8/getJobs", {}, function (re) {
            parseJobs(re.data)
        });
    }

    refreshJobs();

    function parseJobs(map) {
        var arr = [];
        for (var k in map) {
            var it = map[k];
            arr.push([
                "<tr><td colspan='2' style='background: #f7f7f7;color: brown;font-weight: bold;'>" + it.from + "</td></tr>",
                "<tr><td>下载路径</td><td>" + it.dir + "/" + it.file + "</td></tr>",
                "<tr><td>工作线程</td><td>" + it.active + "</td></tr>",
                "<tr><td>速度</td><td>" + PanUtil.formatShortNumber(it.speed, 2) + "/s</td></tr>",
                "<tr><td>总时长</td><td>" + PanUtil.dateFormat.toTimeFormatter(Math.round(it.duringSum) * 1000, 'HH:mm:ss') + "</td></tr>",
                "<tr><td>完成时长</td><td>" + PanUtil.dateFormat.toTimeFormatter(Math.round(it.duringAlready) * 1000, 'HH:mm:ss') + "</td></tr>",
                "<tr><td>总大小</td><td>" + PanUtil.formatShortNumber(it.length, 3) + "</td></tr>",
                "<tr><td>完成大小</td><td>" + PanUtil.formatShortNumber(it.complete, 3) + "</td></tr>",
                "<tr><td>切片数</td><td>" + it.total + "</td></tr>",
                "<tr><td>完成切片</td><td>" + it.count + "</td></tr>",
                "<tr><td>转换切片</td><td>" + it.transfered + "</td></tr>",
                "<tr><td>操作</td><td><button onclick='transfer(\"" + it.id + "\")'>合并转换</button></td></tr>",
            ].join(""))
        }
        document.getElementById("tbody").innerHTML = arr.join("")
    }

    function transfer(id) {
        PanUtil.ajax.post("/m3u8/transfer", {
            id: id,
        }, function (re) {
            if (re.code === 500) {
                alert("操作失败");
                document.getElementById("connectSize").value = re.data;
                document.getElementById("connectSize2").innerText = re.data;
            }
        })
    }

    (function doTask() {

        var ws = new WebSocket("ws://localhost:8080/ws/m3u8");
        ws.onopen = function (evt) {
            console.log("Connection open ...");
            ws.onmessage = function (e) {
                // console.log(e)
                var re = JSON.parse(e.data);
                switch (re.command) {
                    case "getJobs.res": {
                        parseJobs(re.data)
                        break;
                    }
                    default: {
                    }
                }
            };
            ws.onclose = function (evt) {
                console.log("Connection closed.");
                doTask()
            };
            ws.onerror = function (event) {
                console.log("Connection error.");
                doTask()
            };

            function sendWs(command, map) {
                ws.send(JSON.stringify({
                    command: command,
                    data: map
                }));
            }

            setInterval(function () {
                if (ws.readyState === 1) {
                    sendWs("getJobs")
                }
            }, 1000)
        };
    })();

</script>
</body>
</html>