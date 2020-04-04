package com.zone.uvdownloader.controller;

import com.zone.uvdownloader.base.common.JsonResult;
import com.zone.uvdownloader.download.JobWorkerOverseer;
import com.zone.uvdownloader.download.m3u8.M3u8Job;
import com.zone.uvdownloader.download.m3u8.M3u8JobWorker;
import com.zone.uvdownloader.entity.M3u8Item;
import com.zone.uvdownloader.service.M3u8Service;
import com.zone.uvdownloader.util.HttpUrlConnectionUtil;
import com.zone.uvdownloader.util.PFUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 2020/1/11 13:56
 *
 * @author owen pan
 */
@RestController
@RequestMapping("m3u8")
public class M3U8Controller {
    public static AtomicLong CONNECT_SIZE = new AtomicLong(1);
    @Autowired
    private M3u8Service m3u8Service;
    public static ConcurrentHashMap<String, M3u8Job> jobs = new ConcurrentHashMap<>();

    @GetMapping("getConnectSize")
    public JsonResult getConnectSize() {
        return new JsonResult.Builder<Long>().data(CONNECT_SIZE.get()).build();
    }

    @PostMapping("setConnectSize")
    public JsonResult setConnectSize(String value) {
        Long re = m3u8Service.updateM3u8ConfigByName("connect-size", value);
        if (re > 0) {
            CONNECT_SIZE.set(Long.parseLong(value));
            return new JsonResult.Builder<Long>().msg(re.toString()).data(CONNECT_SIZE.get()).build();
        } else {
            return new JsonResult.Builder<Long>().code(500).msg(re.toString()).data(CONNECT_SIZE.get()).build();
        }
    }

    @PostMapping("download")
    public JsonResult download(M3u8Job m3u8Job) {
        String listStr = HttpUrlConnectionUtil.sendHttpByGet(m3u8Job.getFrom()).getResponseBodyStr();
        List<String> list = Arrays.asList(listStr.replaceAll("\r", "").split("\n"));
        List<String> msgs = new ArrayList<>();
        String urlNoEnd = m3u8Job.getFrom().substring(0, m3u8Job.getFrom().lastIndexOf("/"));
        List<M3u8Item> items = new ArrayList<>();
        AtomicReference<M3u8Item> tmp = new AtomicReference<>(null);
        Double duringSum = 0D;
        for (String str : list) {
            str = str.trim();
            if (str.length() <= 0) {
                continue;
            }
            if (str.startsWith("#")) {
                if (str.startsWith("#EXTINF:")) {
                    String[] arr = str.substring(8).split(",", -1);
                    M3u8Item it = tmp.get();
                    if (it == null) {
                        it = new M3u8Item();
                        it.setDuring(Double.parseDouble(arr[0]));
                        it.setName(arr[1]);
                        tmp.set(it);
                        duringSum += it.getDuring();
                    }
                } else {
                    msgs.add(str);
                }
            } else {
                if (str.contains("/")) {
                    tmp.get().setUrl(str);
                    tmp.get().setFileName(str.substring(str.lastIndexOf("/") + 1));
                } else {
                    tmp.get().setUrl(urlNoEnd + "/" + str);
                    tmp.get().setFileName(str);
                    items.add(tmp.get());
                    tmp.set(null);
                }
            }
        }
        m3u8Job.setDuringSum(duringSum);
        m3u8Job.setItems(items);
        m3u8Job.setMsg(msgs);
        m3u8Job.setTotal(items.size());
        m3u8Job.setId(m3u8Job.getFrom() + "_" + m3u8Job.getDir() + "/" + m3u8Job.getFile());
        jobs.put(m3u8Job.getId(), m3u8Job);
        JobWorkerOverseer.WORK_POOL.add(new M3u8JobWorker(m3u8Job));
        Map map = PFUtil.getFieldMap(m3u8Job);
        map.remove("items");
        return new JsonResult.Builder<Object>().data(map).build();
    }

    @GetMapping("getJobs")
    public JsonResult getJobs() {
        HashMap<String, Object> map = new HashMap<>();
        for (Map.Entry<String, M3u8Job> entry : jobs.entrySet()) {
            Map m = PFUtil.getFieldMap(entry.getValue());
            m.remove("items");
            map.put(entry.getKey(), m);
        }
        return new JsonResult.Builder<Object>().data(map).build();
    }

    /**
     * 该方案在实际使用中出现，第二次合并时间戳不一致的问题，导致合并文件后面全部无法播放，遂废弃
     * @param id
     * @return
     */
   /* @PostMapping("transfer")
    public JsonResult transfer(String id) {
        M3u8Job m3u8Job = jobs.get(id);
        String prefix = "";
        try {
            prefix = (new File("").getCanonicalPath().replaceAll("\\\\", "/") + "/ffmpeg.exe -i ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer stringBuffer = new StringBuffer();
        String tmp = null;
        for (int i = 0; i < m3u8Job.getItems().size(); i++) {
            M3u8Item item = m3u8Job.getItems().get(i);
            if (i > 0) {
                stringBuffer.append("|");
            }
            stringBuffer.append(item.getTarget());
            if (stringBuffer.length() > 1500 || i == m3u8Job.getItems().size() - 1) {
                String tempFile = m3u8Job.getDir() + "/" + m3u8Job.getFile() + "/transfer/" + System.currentTimeMillis() + ".ts";
                System.out.println(i + "@" + m3u8Job.getItems().size());
                File ff = new File(tempFile);
                if (!ff.getParentFile().exists()) {
                    ff.getParentFile().mkdirs();
                }
                String re = executeCommand(prefix + "\"concat:" + stringBuffer.toString() + "\" -c copy -safe 0 -movflags +faststart " + tempFile, tempFile);
                System.out.println("command >>" + re);
                if (tmp != null) {
                    new File(tmp).delete();
                }
                m3u8Job.getTransfered().set(i);
                stringBuffer = new StringBuffer();
                stringBuffer.append(tempFile);
                tmp = tempFile;
            }
        }
        String target = m3u8Job.getDir() + "/" + m3u8Job.getFile() + "/" + m3u8Job.getFile() + ".mp4";
        if (new File(target).exists()) {
            target = m3u8Job.getDir() + "/" + m3u8Job.getFile() + "/" + m3u8Job.getFile() + "_" + System.currentTimeMillis() + ".mp4";
        }
        String msg = executeCommand(prefix + "\"concat:" + stringBuffer.toString() + "\" -c copy -bsf:a aac_adtstoasc -movflags +faststart "
                        + target,
                target
        );
        if (tmp != null) {
            new File(tmp).delete();
        }
        m3u8Job.getTransfered().set(m3u8Job.getTotal());
        return new JsonResult.Builder<Object>().msg(msg).build();
    }*/

    @PostMapping("transfer2")
    public JsonResult transfer2(String id) {
        M3u8Job m3u8Job = jobs.get(id);
        String target = m3u8Job.getDir() + "/" + m3u8Job.getFile() + "/" + m3u8Job.getFile() + ".mp4";
        if (new File(target).exists()) {
            target = m3u8Job.getDir() + "/" + m3u8Job.getFile() + "/" + m3u8Job.getFile() + "_" + System.currentTimeMillis() + ".mp4";
        }
        try {
            String listPath = m3u8Job.getDir() + "/" + m3u8Job.getFile() + "/filelist.txt";
            String command = (new File("").getCanonicalPath().replaceAll("\\\\", "/") + "/ffmpeg.exe -f concat -i \"" + listPath + "\" -c copy " + target);
            File file = new File(listPath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, true);
            for (int i = 0; i < m3u8Job.getItems().size(); i++) {
                String realFileName=m3u8Job.getItems().get(i).getTarget().substring(m3u8Job.getItems().get(i).getTarget().lastIndexOf("/")+1);
                fileWriter.write("file 'tmp/" + realFileName + "'\n");
                fileWriter.flush();
            }
            fileWriter.close();
            String msg = executeCommand(command, target);
            m3u8Job.getTransfered().set(m3u8Job.getTotal());
            return new JsonResult.Builder<Object>().msg(msg).build();
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResult.Builder<Object>().code(500).msg(e.getMessage()).build();
        }
    }

    private String executeCommand(String command, String name) {
        System.out.println(">>>" + command);
        System.out.println(">>>" + name);
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuffer msg = new StringBuffer();
        try {
            ProcessBuilder builder = new ProcessBuilder(command.replaceAll("\\s+", " ").split(" "));
            builder.redirectErrorStream(true);
            Process process = builder.start();

            isr = new InputStreamReader(process.getInputStream());
            br = new BufferedReader(isr);
            String szline;
            while ((szline = br.readLine()) != null) {
                System.out.println(szline);
                msg.append(szline);
            }
            int result = process.waitFor();
            System.out.println(result);
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(isr);
            IOUtils.closeQuietly(br);
        }
        return msg.toString();
    }
}
