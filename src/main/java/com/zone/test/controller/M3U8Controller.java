package com.zone.test.controller;

import com.zone.test.base.common.JsonResult;
import com.zone.test.download.JobWorkerOverseer;
import com.zone.test.download.m3u8.M3u8JobWorker;
import com.zone.test.entity.M3u8Item;
import com.zone.test.download.m3u8.M3u8Job;
import com.zone.test.service.M3u8Service;
import com.zone.test.util.HttpUrlConnectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private static ConcurrentHashMap<String,M3u8Job> jobs=new ConcurrentHashMap<>();

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
        Double duringSum=0D;
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
                        duringSum+=it.getDuring();
                    }
                } else {
                    msgs.add(str);
                }
            } else {
                if (str.indexOf("/") >= 0) {
                    tmp.get().setUrl(str);
                } else {
                    tmp.get().setUrl(urlNoEnd + "/" + str);
                    items.add(tmp.get());
                    tmp.set(null);
                }
            }
        }
        m3u8Job.setDuringSum(duringSum);
        m3u8Job.setItems(items);
        m3u8Job.setMsg(msgs);
        jobs.put(m3u8Job.getFrom(),m3u8Job);
        JobWorkerOverseer.WORK_POOL.add(new M3u8JobWorker(m3u8Job));
        return new JsonResult.Builder<Object>().data(duringSum).msg(String.join("\n",msgs)).build();
    }
}
