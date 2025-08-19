package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysSleepRecord;
import com.ruoyi.system.service.SysSleepRecordService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 监控数据录入 Controller（例如：睡眠数据录入）
 */
@RestController
@RequestMapping("/system/monitor/input")
public class MonitorDataInputController extends BaseController {

    @Resource
    private SysSleepRecordService sleepRecordService;

    /**
     * 保存睡眠记录（只保存当前登录用户）
     */
    @Log(title = "监控-睡眠录入", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('system:chart:sleep:add')")
    @PostMapping("/sleep")
    public AjaxResult saveSleep(@RequestBody SleepSaveReq req) {
        if (req == null || isBlank(req.recordDate) || isBlank(req.sleepTime) || isBlank(req.wakeTime)) {
            return AjaxResult.error("参数不完整");
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate d;
        try {
            d = LocalDate.parse(req.recordDate, fmt);
        } catch (Exception e) {
            return AjaxResult.error("日期格式错误，应为 yyyy-MM-dd");
        }

        int total = calcDurationMin(req.sleepTime, req.wakeTime);
        if (total < 0) {
            return AjaxResult.error("时间范围不合法");
        }

        SysSleepRecord entity = new SysSleepRecord();
        entity.setRecordDate(Date.valueOf(d));
        entity.setSleepTime(req.sleepTime);
        entity.setWakeTime(req.wakeTime);
        entity.setTotalMinutes(total);
        entity.setRemark(req.remark);
        entity.setDelFlag("0");
        String username = getUsername();
        entity.setCreateBy(username);
        entity.setUpdateBy(username);

        // 存在则覆盖：按日期+当前用户查询
        SysSleepRecord existed = sleepRecordService.getByDate(Date.valueOf(d));
        int rows;
        if (existed != null && existed.getId() != null) {
            entity.setId(existed.getId());
            // 保留原 createBy
            entity.setCreateBy(existed.getCreateBy());
            rows = sleepRecordService.update(entity);
        } else {
            rows = sleepRecordService.insert(entity);
        }
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("保存失败");
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private int toMinutes(String hm) {
        if (hm == null || !hm.contains(":")) return -1;
        try {
            String[] arr = hm.split(":");
            int h = Integer.parseInt(arr[0]);
            int m = Integer.parseInt(arr[1]);
            return h * 60 + m;
        } catch (Exception e) { return -1; }
    }

    private int calcDurationMin(String sleepHM, String wakeHM) {
        int s = toMinutes(sleepHM);
        int w = toMinutes(wakeHM);
        if (s < 0 || w < 0) return -1;
        if (w < s) w += 24 * 60;
        return w - s;
    }

    public static class SleepSaveReq {
        public String recordDate;
        public String sleepTime;
        public String wakeTime;
        public String remark;
    }
}
