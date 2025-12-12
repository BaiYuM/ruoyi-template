package com.ruoyi.web.controller.exposure;

import com.ruoyi.system.service.impl.ExposureParseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExposureParseTaskManager to increase coverage.
 */
class ExposureParseTaskManagerTest {

    @AfterEach
    void tearDown() {
        ExposureParseTaskManager.clearAll();
        try {
            ExposureParseTaskManager.shutdownExecutor();
        } catch (Exception ignored) {
        }
    }

    @Test
    void submit_ShouldThrow_WhenFileIsNull() {
        ExposureParseService svc = mock(ExposureParseService.class);
        assertThrows(IllegalArgumentException.class, () -> ExposureParseTaskManager.submit(null, svc));
    }

    @Test
    void submit_ShouldThrow_WhenServiceIsNull() {
        MockMultipartFile file = new MockMultipartFile("file", "a.csv", "text/csv", "x".getBytes());
        assertThrows(IllegalArgumentException.class, () -> ExposureParseTaskManager.submit(file, null));
    }

    @Test
    void submitAndGetResult_ShouldReturnParseResult_WhenServiceSucceeds() throws Exception {
        ExposureParseService svc = mock(ExposureParseService.class);
        ExposureParseService.ParseResult r = new ExposureParseService.ParseResult();
        r.parsed = new ArrayList<>();
        r.errors = new ArrayList<>();

        when(svc.parseFile(any())).thenReturn(r);

        ExecutorService exec = Executors.newSingleThreadExecutor();
        ExposureParseTaskManager.setExecutor(exec);

        MockMultipartFile file = new MockMultipartFile("file", "a.csv", "text/csv", "x".getBytes());
        String id = ExposureParseTaskManager.submit(file, svc);
        assertNotNull(id);

        boolean ok = ExposureParseTaskManager.awaitCompletion(id, 2000);
        assertTrue(ok);

        ExposureParseService.ParseResult got = ExposureParseTaskManager.getResult(id);
        assertNotNull(got);
        assertNotNull(got.parsed);
        assertNotNull(got.errors);

        exec.shutdownNow();
    }

    @Test
    void submit_ShouldProduceErrorResult_WhenServiceThrows() throws Exception {
        ExposureParseService svc = mock(ExposureParseService.class);
        when(svc.parseFile(any())).thenThrow(new RuntimeException("boom"));

        ExecutorService exec = Executors.newSingleThreadExecutor();
        ExposureParseTaskManager.setExecutor(exec);

        MockMultipartFile file = new MockMultipartFile("file", "a.csv", "text/csv", "x".getBytes());
        String id = ExposureParseTaskManager.submit(file, svc);
        assertNotNull(id);

        boolean ok = ExposureParseTaskManager.awaitCompletion(id, 2000);
        assertTrue(ok);

        ExposureParseService.ParseResult got = ExposureParseTaskManager.getResult(id);
        assertNotNull(got);
        assertNotNull(got.errors);
        assertTrue(got.errors.stream().anyMatch(s -> s.contains("后台解析异常") || s.contains("boom")));

        exec.shutdownNow();
    }

    @Test
    void getResult_ShouldReturnNull_WhenTaskProcessing() throws Exception {
        ExposureParseService svc = mock(ExposureParseService.class);
        // make parseFile sleep so we can observe processing marker
        when(svc.parseFile(any())).thenAnswer(invocation -> {
            Thread.sleep(200);
            ExposureParseService.ParseResult r = new ExposureParseService.ParseResult();
            r.parsed = new ArrayList<>();
            r.errors = new ArrayList<>();
            return r;
        });

        ExecutorService exec = Executors.newSingleThreadExecutor();
        ExposureParseTaskManager.setExecutor(exec);

        MockMultipartFile file = new MockMultipartFile("file", "a.csv", "text/csv", "x".getBytes());
        String id = ExposureParseTaskManager.submit(file, svc);
        assertNotNull(id);

        // immediately check result; should be processing (null)
        ExposureParseService.ParseResult interim = ExposureParseTaskManager.getResult(id);
        assertNull(interim);

        boolean ok = ExposureParseTaskManager.awaitCompletion(id, 2000);
        assertTrue(ok);

        ExposureParseService.ParseResult finalr = ExposureParseTaskManager.getResult(id);
        assertNotNull(finalr);

        exec.shutdownNow();
    }

    @Test
    void putResultAndClearAll_ShouldWork() {
        String id = "manual-id";
        ExposureParseService.ParseResult r = new ExposureParseService.ParseResult();
        r.parsed = new ArrayList<>();
        r.errors = new ArrayList<>();
        ExposureParseTaskManager.putResult(id, r);

        ExposureParseService.ParseResult got = ExposureParseTaskManager.getResult(id);
        assertNotNull(got);

        ExposureParseTaskManager.clearAll();
        assertNull(ExposureParseTaskManager.getResult(id));
    }

    @Test
    void setExecutorNull_ShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> ExposureParseTaskManager.setExecutor(null));
    }

    @Test
    void shutdownExecutor_ShouldRejectNewTasks() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        ExposureParseTaskManager.setExecutor(exec);
        ExposureParseTaskManager.shutdownExecutor();

        ExposureParseService svc = mock(ExposureParseService.class);
        MockMultipartFile file = new MockMultipartFile("file", "a.csv", "text/csv", "x".getBytes());
        assertThrows(RejectedExecutionException.class, () -> ExposureParseTaskManager.submit(file, svc));
    }

    @Test
    void submit_ServiceReturnsNull_InitializesLists() throws Exception {
        ExposureParseService svc = new ExposureParseService() {
            @Override
            public ParseResult parseFile(org.springframework.web.multipart.MultipartFile file) {
                return null; // force branch where parse returns null
            }
        };

        ExecutorService exec = Executors.newSingleThreadExecutor();
        ExposureParseTaskManager.setExecutor(exec);

        MockMultipartFile file = new MockMultipartFile("file", "n.csv", "text/csv", "x".getBytes());
        String id = ExposureParseTaskManager.submit(file, svc);
        assertNotNull(id);

        boolean ok = ExposureParseTaskManager.awaitCompletion(id, 2000);
        assertTrue(ok);

        ExposureParseService.ParseResult got = ExposureParseTaskManager.getResult(id);
        assertNotNull(got);
        assertNotNull(got.parsed);
        assertNotNull(got.errors);
        assertTrue(got.parsed.isEmpty());
        assertTrue(got.errors.isEmpty());

        exec.shutdownNow();
    }

    @Test
    void awaitCompletion_Interrupted_ByCaller_returnsFalseAndSetsInterruptFlag() throws Exception {
        final java.util.concurrent.atomic.AtomicBoolean result = new java.util.concurrent.atomic.AtomicBoolean(true);
        final java.util.concurrent.atomic.AtomicBoolean interrupted = new java.util.concurrent.atomic.AtomicBoolean(false);

        Thread waiter = new Thread(() -> {
            boolean r = ExposureParseTaskManager.awaitCompletion("no-exist-id", 5000);
            result.set(r);
            interrupted.set(Thread.currentThread().isInterrupted());
        }, "await-test-thread");

        waiter.start();
        Thread.sleep(20);
        waiter.interrupt();
        waiter.join(1000);

        assertFalse(result.get());
        assertTrue(interrupted.get());
    }
}
