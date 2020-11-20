<#assign path=springMacroRequestContext.contextPath/>
<!DOCTYPE HTML>
<html>
<head>

    <title>UV-Downloader</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="PanCss.css">
    <script type="application/javascript" src="PanUtil.js"></script>
    <style>
        #tbody tr td{word-break: break-all;}
        .hideFails{display: none!important;}
    </style>
</head>

<body>
<h1 style="margin:6px;text-align: center;font-size: 7rem;line-height:7rem;padding: 1rem;text-shadow: 16px 22px 22px #504040;">
    ${projectName!""}
</h1>
<div style="display: flex">
    <article style="width: 40%;">
        <h2 style="margin-top: 36px;font-size: 6rem;text-shadow: 2px 2px 17px #1d0c0c;font-style: italic;text-align: center;line-height: 0;color: darkgray;">
            m3u8</h2>
        <hr>
        <p>
            线程数(<span id="connectSize2">100</span>)
            <input type="range" min="1" max="200" value="100" id="connectSize" onchange="changeConnectSize(this)">
            <button onclick="setConnectSize()">设置</button>
        </p>
        <hr>
        <p>
            下载路径
            <input type="text" id="dir" value="${desktopPath!''}">
            <button onclick="document.getElementById('dir').value='${desktopPath!''}'">桌面</button>
        </p>
        <p>
            &emsp;文件名
            <input type="text" id="file">

        </p>
        <hr>
        <p>
            <textarea rows="4" id="from" onchange="getFileName()" style="line-height: 1.5rem;width: 100%"></textarea>
            <button onclick="download()">下载</button>
            <br>

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
                <th style="width:5rem;"><button style="padding: 0px 3px;float: left;border-radius: 50%;" onclick="refreshJobs()">刷新</button></th>
                <th>下载列表</th>
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
    var failSwitchMap={};
    var ws;
    var interval1;
    var tansferMap={};
    // document.getElementById("from").value = "https://zk2.cdt-md.com/2020/03/28/kPSiMsPLsGn07UwH/playlist.m3u8"
    // document.getElementById("from").value = "https://www.mmicloud.com/20190404/Eoxt9tiu/2000kb/hls/index.m3u8"
    // document.getElementById("from").value = "https://s2.135-cdn.com/2020/04/10/o762FVjpITReMJSB/index.m3u8"
    // document.getElementById("from").value = "https://videos8.jsyunbf.com/20190717/eAjPtGHN/950kb/hls/index.m3u8?sign=a729c317b39341f710c5ed34f65c0dde197e4c2495819e3877c7f6fa6e287db58a74ea8394349fed43e75d7e9152f26eda827d9e21d9ff448246efd887fb517b"

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

    function getConnectSize() {
        PanUtil.ajax.get("/m3u8/getConnectSize", {}, function (re) {
            document.getElementById("connectSize").value = re.data;
            document.getElementById("connectSize2").innerText = re.data;
        });
    }
    getConnectSize();
    function download() {
        PanUtil.ajax.post("/m3u8/download", {
            from: document.getElementById("from").value.trim(),
            dir: document.getElementById("dir").value.trim(),
            file: document.getElementById("file").value.trim(),
        }, function (re) {
            alert(re.code===200?"开始下载":re.msg);
            refreshJobs();
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
        getConnectSize();
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
        var jobs=[];
        for (var k in map) {
            jobs.push(map[k])
        }
        jobs= jobs.sort(function (a,b){
            return b.downloadTime.replace(/[\s-:]/g,"")-a.downloadTime.replace(/[\s-:]/g,"")
        });
        for(var i=0;i<jobs.length;i++){
            var it = jobs[i];
            if(it.total===it.count){
                if(it.transfered===it.total){
                    tansferMap[it.id]=true
                }else{
                    if(!tansferMap[it.id]) {
                        transfer(it.id)
                    }
                }
            }
            arr.push([
                "<tr><td colspan='2' style='background: #f7f7f7;color: brown;font-weight: bold;'>",
                "<div style='line-height: 2rem;'>"+it.dir + "/" + it.file+"</div>",
                "   <div>",
                "       <button style=\"padding: 0px 12px;float: left;border-radius: 50%;width:160px;background:purple;\" "+(it.total>it.count&&it.total>it.count+it.fail.length?"":"hidden")+">正在下载 - "+Math.round(it.count/it.total*10000)/100+"%</button>",
                "       <button style=\"padding: 0px 12px;float: left;border-radius: 50%;width:160px;background:orange;\" "+(it.total>it.count&&it.total<=it.count+it.fail.length?"":"hidden")+">已经暂停 - "+Math.round(it.count/it.total*10000)/100+"%</button>",
                "       <button style=\"padding: 0px 12px;float: left;border-radius: 50%;width:160px;background:green;\" "+(it.total<=it.count&&it.total<=it.count+it.fail.length?"":"hidden")+">下载完成 - "+Math.round(it.count/it.total*10000)/100+"%</button>",
                "       <button style=\"padding: 0px 12px;float: left;border-radius: 50%;width: 90px;margin-left: 6px;background: darkred;\">"+PanUtil.formatShortNumber(it.speed, 2)+"/s</button>",
                "       <span style='float: left;line-height: 2rem;color: #8c8989;font-weight: normal;font-size: 14px;margin-left: 6px;'>"+it.downloadTime+"</span>",
                "       <button style='float: right;padding: 0px 8px;' data-id='"+it.id+"' onclick='details(this)'>详情</button>",
                "       <button style='float: right;padding: 0px 8px;margin-right:6px;' data-id='"+it.id+"' onclick='toggle(this)'>收缩/展开</button>",
                "   </div>",
                "   </td>",
                "</tr>",
                "<tr "+(!switchMap[it.id]?"style='display:none;'":"style='display:table-row;;'")+">",
                "<td colspan='2'>",
                "<table>",
                "   <tr><td style='min-width: 70px'>下载时间</td><td>" + it.downloadTime + "</td></tr>",
                "   <tr><td>下载地址</td><td>" + it.from + "</td></tr>",
                "   <tr><td>工作组</td><td>" + it.active+"（ "+ PanUtil.formatShortNumber(it.speed, 2)+"/s ）" + "</td></tr>",
                "   <tr><td>总时长</td><td>",
                PanUtil.dateFormat.toTimeFormatter(Math.round(it.duringAlready) * 1000, 'HH:mm:ss'),
                " / "+ PanUtil.dateFormat.toTimeFormatter(Math.round(it.duringSum) * 1000, 'HH:mm:ss') ,
                "       </td>",
                "   </tr>",
                "   <tr><td>容量缓存</td><td>" + PanUtil.formatShortNumber(it.complete, 3)+" / "+PanUtil.formatShortNumber(it.length, 3) + "</td></tr>",
                "   <tr><td>切片数</td><td>" + it.count+" / " +it.total + "</td></tr>",
                "   <tr><td>失败切片</td><td>" + it.fail.length +(it.fail.length<=0?"":" <button onclick='toggleFails(\"fails_"+it.id+"\")'>收缩/展开</button>")+"</td></tr>",
                "   <tr id='fails_"+it.id+"' "+(failSwitchMap["fails_"+it.id]?"":"class='hideFails'")+">",
                "       <td colspan='2' style='padding:0 24px;'>" ,
                "           <div >",
                it.fail.map(function(o,idx){
                    return "<div style='word-break: break-all;'><button onclick='reloadPiece(\"" + it.id + "\",\"" + o.fileName + "\")'>重试</button> "+o.url+"</div>";
                }).join(""),
                "           </div>",
                "       </td>",
                "   </tr>",
                "   <tr><td>转换切片</td><td>" + it.transfered + "</td></tr>",
                "   <tr><td>操作</td><td>",
                "       <button id='transfer-"+it.id+"' onclick='transfer(\"" + it.id + "\")' "+(it.transfered===it.total?"hidden":"")+">"+(tansferMap[it.id]?"正在转换":"合并转换")+"</button>",
                "       <button onclick='reloadPiece(\"" + it.id + "\")' "+(it.transfered===it.total?"hidden":"")+">切片重新下载</button>",
                "       <button onclick='deleteJob(\"" + it.id + "\")' style='background: crimson;'>删除</button></td>",
                "   </tr>",
                "   </table>",
                "   </td>",
                "</tr>"
            ].join(""))
        }
        document.getElementById("tbody").innerHTML = arr.join("")
    }

    function toggleFails(id) {
        var dom=document.getElementById(id);
        if(dom.className){
            dom.className="";
            failSwitchMap[id]=true
        }else{
            dom.className="hideFails";
            failSwitchMap[id]=false
        }
    }

    function toggle(dom) {
        var id=dom.getAttribute("data-id");
        var i=switchMap[id]||false;
        // dom.parentNode.parentNode.parentNode.nextElementSibling.style.display=(i?"none":"table-row");
        switchMap[id]=!Number(i)
        for (var k in switchMap){
            if(k!==id){
                switchMap[k]=false
            }
        }
        refreshJobs();
    }

    function transfer(id) {
        tansferMap[id]=true
        var dom=document.getElementById("transfer-"+id)
        PanUtil.ajax.post("/m3u8/transfer2", {
            id: id,
        }, function (re) {
            if (re.code === 500) {111
                tansferMap[id]=false
                var name=re.msg.substring(re.msg.lastIndexOf("/")+1,re.msg.lastIndexOf("'"));
                if(confirm("转换失败！\n\n是否重新下载切片："+name)){
                    reloadPiece(id,name);
                }
                document.getElementById("connectSize").value = re.data;
                document.getElementById("connectSize2").innerText = re.data;
            } else {
                alert("转换完毕")
            }
        })
    }

    function reloadPiece(id,fileName) {
        if(!fileName) {
            var ff = prompt("请输入重新下载文件名（包含后缀）")
            if(!ff){
                return
            }
            fileName=ff.trim()
        }
        PanUtil.ajax.post("/m3u8/reloadPiece", {
            id: id,
            file:fileName
        }, function (re) {
            if (re.code === 500) {
                alert("下载切片失败");
            } else {
                notify("下载切片成功")
            }
        });
        refreshJobs();
    }

    function deleteJob(id) {
            if(!confirm("确认删除吗？")){
                return
            }
        PanUtil.ajax.post("/m3u8/deleteJob", {
            id: id
        }, function (re) {
            if (re.code === 500) {
                alert("删除任务失败");
            } else {
                notify("删除任务成功")
            }
        });
        refreshJobs();
    }

    function doTask() {
        if(interval1){
            clearInterval(interval1)
        }
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
                try {
                    ws.close()
                }catch (e){}
                doTask()
            };

            function sendWs(command, map) {
                ws.send(JSON.stringify({
                    command: command,
                    data: map
                }));
            }

            interval1= setInterval(function () {
                if (ws.readyState === 1) {
                    sendWs("getJobs")
                }
            }, 1000)
        };
    }

    function notify(msg){
        if(window.Notification && Notification.permission !== "denied") {
            //Notification.requestPermission这是一个静态方法，作用就是让浏览器出现是否允许通知的提示
            Notification.requestPermission(function(status) {
                console.log('2: '+status);
                //如果状态是同意
                if (status === "granted") {
                    var m = new Notification('提醒', {
                        body: msg,　　//消息体内容
                        icon:"http://officeweb365.com/Content/imgs/ow.jpg"　　//消息图片
                    });
                    m.onclick = function () {

                    }
                } else{
                    alert('当前浏览器不支持弹出消息')
                }
            });
        }
    }

</script>
</body>
</html>
