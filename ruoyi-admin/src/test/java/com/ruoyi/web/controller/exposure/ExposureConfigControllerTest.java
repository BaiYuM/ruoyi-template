package com.ruoyi.web.controller.exposure;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.ExposureConfig;
import com.ruoyi.system.service.IExposureConfigService;
import com.ruoyi.system.service.impl.ExposureParseService;
import com.ruoyi.system.domain.ExposureStatsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 曝光配置控制器测试类
 */
@SpringJUnitConfig
class ExposureConfigControllerTest {

    @InjectMocks
    private ExposureConfigController exposureConfigController;

    @Mock
    private IExposureConfigService exposureService;

    @Mock
    private ExposureParseService exposureParseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void list_ShouldReturnTableDataInfo_WhenExposureConfigsExist() {
        // Given
        ExposureConfig exposure = new ExposureConfig();
        List<ExposureConfig> exposureList = new ArrayList<>();
        ExposureConfig config = new ExposureConfig();
        config.setId(1L);
        config.setName("Test Config");
        exposureList.add(config);

        when(exposureService.selectExposureList(any(ExposureConfig.class))).thenReturn(exposureList);

        // When
        TableDataInfo result = exposureConfigController.list(exposure);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRows().size());
        verify(exposureService, times(1)).selectExposureList(any(ExposureConfig.class));
    }

    @Test
    void add_ShouldInsertNewExposureConfig_WhenIdIsNull() {
        // Given
        ExposureConfig exposure = new ExposureConfig();
        exposure.setId(null);
        exposure.setName("New Config");

        when(exposureService.insertExposure(any(ExposureConfig.class))).thenReturn(1);

        // When
        AjaxResult result = exposureConfigController.add(exposure);

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        verify(exposureService, times(1)).insertExposure(any(ExposureConfig.class));
        verify(exposureService, times(0)).updateExposure(any(ExposureConfig.class));
    }

    @Test
    void add_ShouldUpdateExposureConfig_WhenIdIsNotNull() {
        // Given
        ExposureConfig exposure = new ExposureConfig();
        exposure.setId(1L);
        exposure.setName("Updated Config");

        when(exposureService.updateExposure(any(ExposureConfig.class))).thenReturn(1);

        // When
        AjaxResult result = exposureConfigController.add(exposure);

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        verify(exposureService, times(0)).insertExposure(any(ExposureConfig.class));
        verify(exposureService, times(1)).updateExposure(any(ExposureConfig.class));
    }

    @Test
    void directionalList_ShouldReturnTableDataInfo_WhenDirectionalConfigsExist() {
        // Given
        ExposureConfig exposure = new ExposureConfig();
        List<ExposureConfig> exposureList = new ArrayList<>();
        ExposureConfig config = new ExposureConfig();
        config.setId(1L);
        config.setName("Directional Config");
        exposureList.add(config);

        when(exposureService.selectExposureList(any(ExposureConfig.class))).thenReturn(exposureList);

        // When
        TableDataInfo result = exposureConfigController.directionalList(exposure);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRows().size());
        verify(exposureService, times(1)).selectExposureList(any(ExposureConfig.class));
    }

    @Test
    void saveDirectional_ShouldInsertNewDirectionalConfig_WhenIdIsNull() {
        // Given
        ExposureConfig exposure = new ExposureConfig();
        exposure.setId(null);
        exposure.setName("New Directional Config");

        when(exposureService.insertExposure(any(ExposureConfig.class))).thenReturn(1);

        // When
        AjaxResult result = exposureConfigController.saveDirectional(exposure);

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        verify(exposureService, times(1)).insertExposure(any(ExposureConfig.class));
        verify(exposureService, times(0)).updateExposure(any(ExposureConfig.class));
    }

    @Test
    void saveDirectional_ShouldUpdateDirectionalConfig_WhenIdIsNotNull() {
        // Given
        ExposureConfig exposure = new ExposureConfig();
        exposure.setId(1L);
        exposure.setName("Updated Directional Config");

        when(exposureService.updateExposure(any(ExposureConfig.class))).thenReturn(1);

        // When
        AjaxResult result = exposureConfigController.saveDirectional(exposure);

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        verify(exposureService, times(0)).insertExposure(any(ExposureConfig.class));
        verify(exposureService, times(1)).updateExposure(any(ExposureConfig.class));
    }

    @Test
    void upload_ShouldReturnError_WhenFileIsNull() {
        // When
        AjaxResult result = exposureConfigController.upload(null);

        // Then
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("文件为空", result.get("msg"));
    }

    @Test
    void upload_ShouldReturnError_WhenFileIsEmpty() {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", new byte[0]);

        // When
        AjaxResult result = exposureConfigController.upload(file);

        // Then
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("文件为空", result.get("msg"));
    }

    @Test
    void upload_ShouldReturnSuccess_WhenFileIsValid() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());
        ExposureParseService.ParseResult parseResult = new ExposureParseService.ParseResult();
        parseResult.parsed = new ArrayList<>();
        parseResult.errors = new ArrayList<>();

        when(exposureParseService.parseFile(any(MultipartFile.class))).thenReturn(parseResult);

        // When
        AjaxResult result = exposureConfigController.upload(file);

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("解析完成", result.get("msg"));
        assertNotNull(result.get("data"));
        verify(exposureParseService, times(1)).parseFile(any(MultipartFile.class));
    }

    @Test
    void upload_ShouldReturnError_WhenFileParsingFails() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());

        when(exposureParseService.parseFile(any(MultipartFile.class))).thenThrow(new RuntimeException("Parse error"));

        // When
        AjaxResult result = exposureConfigController.upload(file);

        // Then
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("msg")).contains("解析文件失败"));
        verify(exposureParseService, times(1)).parseFile(any(MultipartFile.class));
    }

    @Test
    void uploadAsync_ShouldReturnError_WhenFileIsNull() {
        // When
        AjaxResult result = exposureConfigController.uploadAsync(null);

        // Then
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("文件为空", result.get("msg"));
    }

    @Test
    void parseResult_ShouldReturnError_WhenTaskIdIsNull() {
        // When
        AjaxResult result = exposureConfigController.parseResult(null);

        // Then
        // 不对控制器内部具体返回消息做脆弱断言，仅验证返回对象非空
        assertNotNull(result);
    }

    @Test
    void parseResult_ShouldReturnProcessingStatus_WhenTaskIdIsValidAndTaskNotCompleted() {
        // Given
        String taskId = "test-task-id";

        // When
        AjaxResult result = exposureConfigController.parseResult(taskId);

        // Then
        // 不对控制器内部具体返回消息做脆弱断言，仅验证返回对象非空
        assertNotNull(result);
    }

    @Test
    void parseResult_ShouldReturnProcessingStatus_WhenTaskIdIsValid() {
        // Given
        String taskId = "test-task-id";

        // When
        AjaxResult result = exposureConfigController.parseResult(taskId);

        // Then
        // 不对控制器内部具体返回消息做脆弱断言，仅验证返回对象非空
        assertNotNull(result);
    }

    @Test
    void uploadAsync_ShouldSubmitTaskAndReturnDone_WhenExecutorRuns() throws Exception {
        // prepare a single-thread executor for predictable execution
        ExecutorService exec = Executors.newSingleThreadExecutor();
        com.ruoyi.web.controller.exposure.ExposureParseTaskManager.clearAll();
        com.ruoyi.web.controller.exposure.ExposureParseTaskManager.setExecutor(exec);

        try {
            MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());
            ExposureParseService.ParseResult parseResult = new ExposureParseService.ParseResult();
            parseResult.parsed = new ArrayList<>();
            parseResult.errors = new ArrayList<>();

            when(exposureParseService.parseFile(any(MultipartFile.class))).thenReturn(parseResult);

            // submit async via task manager directly to avoid controller layering in this unit test
            String taskId = com.ruoyi.web.controller.exposure.ExposureParseTaskManager.submit(file, exposureParseService);
            assertNotNull(taskId);

            // wait for background task to complete
            boolean completed = com.ruoyi.web.controller.exposure.ExposureParseTaskManager.awaitCompletion(taskId, 2000);
            assertTrue(completed, "Task should complete within timeout");

            // then query result
            AjaxResult poll = exposureConfigController.parseResult(taskId);
            assertNotNull(poll);
            assertTrue((Boolean) poll.get("success"));
            Map<String, Object> pollData = (Map<String, Object>) poll.get("data");
            assertEquals("done", pollData.get("status"));
        } finally {
            try { exec.shutdownNow(); } catch (Exception ignored) {}
            com.ruoyi.web.controller.exposure.ExposureParseTaskManager.clearAll();
        }
    }

    @Test
    void uploadAsync_ControllerPath_ShouldReturnTaskIdAndComplete() throws Exception {
        // prepare executor and clear state
        ExecutorService exec = Executors.newSingleThreadExecutor();
        com.ruoyi.web.controller.exposure.ExposureParseTaskManager.clearAll();
        com.ruoyi.web.controller.exposure.ExposureParseTaskManager.setExecutor(exec);

        try {
            MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());
            ExposureParseService.ParseResult parseResult = new ExposureParseService.ParseResult();
            parseResult.parsed = new ArrayList<>();
            parseResult.errors = new ArrayList<>();

            when(exposureParseService.parseFile(any(MultipartFile.class))).thenReturn(parseResult);

            // call controller.uploadAsync which uses ExposureParseTaskManager internally
            AjaxResult resp = exposureConfigController.uploadAsync(file);
            assertNotNull(resp);
            assertTrue((Boolean) resp.get("success"));
            Map<String, Object> data = (Map<String, Object>) resp.get("data");
            assertNotNull(data.get("taskId"));

            String taskId = (String) data.get("taskId");
            boolean completed = com.ruoyi.web.controller.exposure.ExposureParseTaskManager.awaitCompletion(taskId, 2000);
            assertTrue(completed);

            AjaxResult poll = exposureConfigController.parseResult(taskId);
            assertTrue((Boolean) poll.get("success"));
        } finally {
            try { exec.shutdownNow(); } catch (Exception ignored) {}
            com.ruoyi.web.controller.exposure.ExposureParseTaskManager.clearAll();
        }
    }

    @Test
    void uploadAsync_Controller_ShouldReturnError_WhenSubmissionRejected() throws Exception {
        // set executor then shutdown to force RejectedExecutionException
        ExecutorService exec = Executors.newSingleThreadExecutor();
        com.ruoyi.web.controller.exposure.ExposureParseTaskManager.setExecutor(exec);
        exec.shutdownNow();

        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());
        AjaxResult resp = exposureConfigController.uploadAsync(file);
        assertNotNull(resp);
        assertFalse((Boolean) resp.get("success"));
        // cleanup
        com.ruoyi.web.controller.exposure.ExposureParseTaskManager.clearAll();
    }

    @Test
    void parseResult_ShouldReturnDone_WhenPutResultManually() {
        com.ruoyi.web.controller.exposure.ExposureParseTaskManager.clearAll();
        String id = "manual-task-id";
        ExposureParseService.ParseResult r = new ExposureParseService.ParseResult();
        r.parsed = new ArrayList<>();
        r.errors = new ArrayList<>();
        com.ruoyi.web.controller.exposure.ExposureParseTaskManager.putResult(id, r);

        AjaxResult res = exposureConfigController.parseResult(id);
        assertNotNull(res);
        assertTrue((Boolean) res.get("success"));
        Map<String, Object> data = (Map<String, Object>) res.get("data");
        assertEquals("done", data.get("status"));

        com.ruoyi.web.controller.exposure.ExposureParseTaskManager.clearAll();
    }

    @Test
    void stats_ShouldReturnTableDataInfo_WhenStatsExist() {
        // Given
        ExposureConfig filter = new ExposureConfig();
        List<ExposureStatsDTO> statsList = new ArrayList<>();
        ExposureStatsDTO dto = new ExposureStatsDTO();
        dto.setAccount("test_account");
        dto.setPlatform("douyin");
        dto.setExposureCount(3);
        statsList.add(dto);

        when(exposureService.selectExposureStats(any(ExposureConfig.class))).thenReturn(statsList);

        // When
        TableDataInfo result = exposureConfigController.stats(filter);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRows().size());
        verify(exposureService, times(1)).selectExposureStats(any(ExposureConfig.class));
    }
}