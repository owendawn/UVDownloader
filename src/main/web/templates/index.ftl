<#assign path=springMacroRequestContext.contextPath/>
<!DOCTYPE HTML>
<html>
<head>

    <title>UV-Downloader</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="PanCss.css">
    <script type="application/javascript" src="PanUtil.js"></script>
</head>

<body>
<h1 style="margin:6px;text-align: center;font-size: 7rem;line-height:7rem;padding: 1rem;text-shadow: 16px 22px 22px #504040;">
    ${projectName}
</h1>
<div style="display: flex">
    <article style="width: 40%;">
        <h2 style="margin-top: 36px;font-size: 6rem;text-shadow: 2px 2px 17px #1d0c0c;font-style: italic;text-align: center;line-height: 0;color: darkgray;">
            m3u8</h2>
        <hr>
        <p>
            线程数(<span id="connectSize2">-</span>)
            <input type="range" min="1" max="100" id="connectSize" onchange="changeConnectSize(this)">
            <button onclick="setConnectSize()">设置</button>
        </p>
        <hr>
        <p>
            下载路径
            <input type="text" id="dir" value="${desktopPath}">
            <button onclick="document.getElementById('dir').value='${desktopPath}'">桌面</button>
        </p>
        <p>
            文件名
            <input type="text" id="file">

        </p>
        <hr>
        <p>
            <textarea rows="4" id="from" onchange="getFileName()" style="line-height: 1.5rem;width: 100%"></textarea>
            <button onclick="download()">下载</button>
            <br>
            <button onclick="refreshJobs()">刷新</button>
            <br>
        </p>
        <hr>
        <p>

        </p>
    </article>
    <article style="width: 60%">
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
    </article>
</div>


<script>
    var jobMap={};
    var switchMap={};
    var ws;
    // document.getElementById("from").value = "https://zk2.cdt-md.com/2020/03/28/kPSiMsPLsGn07UwH/playlist.m3u8"
    // document.getElementById("from").value = "https://www.mmicloud.com/20190404/Eoxt9tiu/2000kb/hls/index.m3u8"
    // document.getElementById("from").value = "https://s2.135-cdn.com/2020/04/10/o762FVjpITReMJSB/index.m3u8"

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
            alert(re.code===200?"开始下载":re.msg);
        })
    }
    function details(dom) {
        var id=dom.getAttribute("data-id");
        document.getElementById("from").value=jobMap[id].from;
        document.getElementById("dir").value=jobMap[id].dir;
        document.getElementById("file").value=jobMap[id].file;
    }


    function setConnectSize() {
        PanUtil.ajax.post("/m3u8/setConnectSize", {
            value: document.getElementById("connectSize").value,
        }, function (re) {
            if (re.code === 500) {
                alert("操作失败");
                document.getElementById("connectSize").value = re.data;
                document.getElementById("connectSize2").innerText = re.data;
            } else {
                alert("操作成功");
            }
        })
    }

    function changeConnectSize(dom) {
        document.getElementById("connectSize2").innerText = dom.value;
    }

    function refreshJobs() {
        PanUtil.ajax.get("/m3u8/getJobs", {}, function (re) {
            parseJobs(re.data)
            if(ws){
                ws.close();
                ws=undefined;
            }
            doTask();
        });
    }

    refreshJobs();

    function parseJobs(map) {
        jobMap=map;
        var arr = [];
        for (var k in map) {
            var it = map[k];
            arr.push([
                "<tr><td colspan='2' style='background: #f7f7f7;color: brown;font-weight: bold;'>"
                + it.from
                + "<br><button data-id='"+k+"' onclick='details(this)'>详情</button>"
                + "<br><button data-id='"+k+"' onclick='toggle(this)'>收缩/展开</button>"
                +"</td></tr>",
                "<tr "+(switchMap[k]===1?"style='display:none;'":"style='display:table-row;;'")+"><td>下载路径</td><td>" + it.dir + "/" + it.file + "</td></tr>",
                "<tr><td>工作线程</td><td>" + it.active + "</td></tr>",
                "<tr><td>速度</td><td>" + PanUtil.formatShortNumber(it.speed, 2) + "/s</td></tr>",
                "<tr><td>总时长</td><td>" + PanUtil.dateFormat.toTimeFormatter(Math.round(it.duringSum) * 1000, 'HH:mm:ss') + "</td></tr>",
                "<tr><td>完成时长</td><td>" + PanUtil.dateFormat.toTimeFormatter(Math.round(it.duringAlready) * 1000, 'HH:mm:ss') + "</td></tr>",
                "<tr><td>总大小</td><td>" + PanUtil.formatShortNumber(it.length, 3) + "</td></tr>",
                "<tr><td>完成大小</td><td>" + PanUtil.formatShortNumber(it.complete, 3) + "</td></tr>",
                "<tr><td>切片数</td><td>" + it.total + "</td></tr>",
                "<tr><td>完成切片</td><td>" + it.count + "</td></tr>",
                "<tr><td>转换切片</td><td>" + it.transfered + "</td></tr>",
                "<tr><td>操作</td><td><button onclick='transfer(\"" + it.id + "\")'>合并转换</button> <button onclick='reloadPiece(\"" + it.id + "\")'>切片重新下载</button></td></tr>",
            ].join(""))
        }
        document.getElementById("tbody").innerHTML = arr.join("")
    }

    function toggle(dom) {
        var id=dom.getAttribute("data-id");
        var i=switchMap[id]||false;
        dom.parentNode.parentNode.nextElementSibling.style.display=(i?"none":"table-row");
        dom.parentNode.parentNode.nextElementSibling.nextElementSibling.style.display=(i?"none":"table-row");
        dom.parentNode.parentNode.nextElementSibling.nextElementSibling.nextElementSibling.style.display=(i?"none":"table-row");
        dom.parentNode.parentNode.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.style.display=(i?"none":"table-row");
        dom.parentNode.parentNode.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.style.display=(i?"none":"table-row");
        dom.parentNode.parentNode.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.style.display=(i?"none":"table-row");
        dom.parentNode.parentNode.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.style.display=(i?"none":"table-row");
        dom.parentNode.parentNode.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.style.display=(i?"none":"table-row");
        dom.parentNode.parentNode.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.style.display=(i?"none":"table-row");
        dom.parentNode.parentNode.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.style.display=(i?"none":"table-row");
        dom.parentNode.parentNode.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.nextElementSibling.style.display=(i?"none":"table-row");
        switchMap[id]=!Number(i)
    }

    function transfer(id) {
        PanUtil.ajax.post("/m3u8/transfer2", {
            id: id,
        }, function (re) {
            if (re.code === 500) {
                alert("操作失败");
                document.getElementById("connectSize").value = re.data;
                document.getElementById("connectSize2").innerText = re.data;
            } else {
                alert("转换完毕")
            }
        })
    }

    function reloadPiece(id) {
        PanUtil.ajax.post("/m3u8/reloadPiece", {
            id: id,
            file:prompt("请输入重新下载文件名（包含后缀）")
        }, function (re) {
            if (re.code === 500) {
                alert("操作失败");
            } else {
                alert("操作成功")
            }
        })
    }

    function doTask() {
        ws = new WebSocket("ws://localhost:8080/ws/m3u8");
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
                console.log(evt)
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
            }, 100000)
        };
    }

</script>
</body>
</html>
