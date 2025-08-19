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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 简单图表数据演示控制器
 */
@RestController
@RequestMapping("/system/chart")
public class ChartController extends BaseController {

    @Resource
    private SysSleepRecordService sleepRecordService;

    /**
     * 温度一周变化（示例数据）
     */
    @Log(title = "图表-温度周变化", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('system:chart:temperature:list')")
    @GetMapping("/temperature")
    public AjaxResult temperature() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Temperature Change in the Coming Week");
        data.put("xAxis", new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"});
        data.put("highest", new int[]{10, 55, 13, 11, 12, 12, 66});
        data.put("lowest", new int[]{1, -2, 2, 5, 3, 2, 0});
        return AjaxResult.success(data);
    }

    

    /**
     * 睡眠时间监测（示例数据）
     * 返回结构：
     * {
     * dates: ["2025-08-13", ...],
     * intervals: [ { sleep: "23:30", wake: "07:15" }, ... ]
     * }
     */
    @Log(title = "图表-睡眠监测", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('system:chart:sleep:list')")
    @GetMapping("/sleep")
    public AjaxResult sleep(@RequestParam(value = "startDate", required = false) String startDate,
                            @RequestParam(value = "endDate", required = false) String endDate) {
        Map<String, Object> data = new HashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 构造日期范围：默认最近 7 天（含今天）
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate, fmt) : LocalDate.now();
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate, fmt) : end.minusDays(6);
        if (start.isAfter(end)) {
            // 纠正非法范围
            LocalDate tmp = start;
            start = end;
            end = tmp;
        }

        List<String> dateList = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            dateList.add(d.format(fmt));
        }
        data.put("dates", dateList);

        // 查询数据库真实数据，并按天对齐
        List<SysSleepRecord> list = sleepRecordService.listByDateRange(Date.valueOf(start), Date.valueOf(end));
        Map<String, SysSleepRecord> byDate = list.stream()
                .collect(Collectors.toMap(
                        r -> r.getRecordDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(fmt),
                        r -> r,
                        (a, b) -> a
                ));

        List<Map<String, String>> intervals = new ArrayList<>();
        for (String ds : dateList) {
            SysSleepRecord r = byDate.get(ds);
            if (r != null) {
                intervals.add(mapOf(r.getSleepTime(), r.getWakeTime()));
            } else {
                // 缺失数据用空段占位，前端可选择性处理
                intervals.add(mapOf(null, null));
            }
        }
        data.put("intervals", intervals);
        return AjaxResult.success(data);
    }

    private Map<String, String> mapOf(String sleep, String wake) {
        Map<String, String> m = new HashMap<>();
        m.put("sleep", sleep);
        m.put("wake", wake);
        return m;
    }
}
