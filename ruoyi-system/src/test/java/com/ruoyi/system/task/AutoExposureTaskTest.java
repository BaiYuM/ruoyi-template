package com.ruoyi.system.task;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ruoyi.system.domain.ExposureConfig;
import com.ruoyi.system.domain.ExposureEvent;
import com.ruoyi.system.mapper.ExposureEventMapper;
import com.ruoyi.system.service.IExposureConfigService;
import com.ruoyi.system.service.IExposureExecutorService;

/**
 * 简单单元测试（无第三方 mock）：验证当配置的 startTime 到达且当天未触发时会调用执行器并写入事件。
 */
public class AutoExposureTaskTest {

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    static class FakeExposureService implements IExposureConfigService {
        private List<ExposureConfig> list;
        FakeExposureService(List<ExposureConfig> list) { this.list = list; }
        @Override public List<ExposureConfig> selectExposureList(ExposureConfig exposure) { return list; }
        @Override public ExposureConfig selectExposureById(Long id) { return null; }
        @Override public int insertExposure(ExposureConfig exposure) { return 0; }
        @Override public int updateExposure(ExposureConfig exposure) { return 0; }
        @Override public int deleteExposureByIds(Long[] ids) { return 0; }
        @Override public List<com.ruoyi.system.domain.ExposureStatsDTO> selectExposureStats(ExposureConfig filter) { return null; }
    }

    static class FakeEventMapper implements ExposureEventMapper {
        boolean upsertCalled = false;
        @Override public int insertExposureEvent(ExposureEvent event) { return 0; }
        @Override public int updateExposureEvent(ExposureEvent event) { return 0; }
        @Override public int incrementExposureCount(Long configId, String account, String platform, Integer delta, java.util.Date exposureTime) { return 0; }
        @Override public int incrementSingleRowByConfigOrAccountPlatform(Long configId, String account, String platform, Integer delta, java.util.Date exposureTime) { return 0; }
        @Override public int upsertExposureEvent(ExposureEvent event) { upsertCalled = true; return 1; }
        @Override public ExposureEvent selectByConfigAccountPlatform(Long configId, String account, String platform) { return null; }
        @Override public java.util.Date selectLastExposureTime(String account, String platform) { return null; }
        @Override public int selectTodayExposureCountByConfig(Long configId) { return 0; }
    }

    static class FakeExecutor implements IExposureExecutorService {
        boolean executed = false;
        @Override public boolean executeExposure(ExposureConfig config) { executed = true; return true; }
    }

    @Test
    public void testStartTimeTriggersExposure() throws Exception {
        AutoExposureTask task = new AutoExposureTask();

        // Prepare a config whose startTime is 1 minute before now
        ExposureConfig cfg = new ExposureConfig();
        cfg.setId(123L);
        cfg.setAccount("acct1");
        cfg.setPlatform("douyin");
        cfg.setStatus("0"); // enabled
        String start = LocalTime.now().minusMinutes(1).format(DateTimeFormatter.ofPattern("HH:mm"));
        cfg.setStartTime(start);

        List<ExposureConfig> list = Arrays.asList(cfg);

        FakeExposureService exposureService = new FakeExposureService(list);
        FakeEventMapper eventMapper = new FakeEventMapper();
        FakeExecutor executor = new FakeExecutor();

        // Inject fakes
        setField(task, "exposureService", exposureService);
        setField(task, "eventMapper", eventMapper);
        setField(task, "exposureExecutorService", executor);

        // Run the task
        task.run();

        // verify executor and upsert called
        Assert.assertTrue("executor should have been called", executor.executed);
        Assert.assertTrue("upsert should have been called on event mapper", eventMapper.upsertCalled);
    }
}
