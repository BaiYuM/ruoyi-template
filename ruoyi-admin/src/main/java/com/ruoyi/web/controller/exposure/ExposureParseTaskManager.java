package com.ruoyi.web.controller.exposure;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.system.service.impl.ExposureParseService;

/**
 * 简易的后台解析任务管理（内存存储）。
 * 说明：为快速实现异步流程，使用内存 Map 存储结果；生产可替换为持久化表。
 *
 * 改进点：
 * - 使用 ExecutorService 取代裸线程，便于测试与资源管理。
 * - 在异常路径中保证 ParseResult 内部集合已初始化，避免 NPE。
 * - 提供清理/关闭方法以便在测试中复位状态。
 */
public class ExposureParseTaskManager {
    private static final ConcurrentMap<String, ExposureParseService.ParseResult> TASKS = new ConcurrentHashMap<>();
    // marker instance to indicate a task is processing (ConcurrentHashMap disallows null values)
    private static final ExposureParseService.ParseResult PROCESSING_MARKER = new ExposureParseService.ParseResult();
    private static ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        @Override
        public Thread newThread(Runnable r) {
            Thread t = defaultFactory.newThread(r);
            t.setDaemon(true);
            t.setName("ExposureParseTask-" + t.getId());
            return t;
        }
    });

    public static String submit(final MultipartFile file, final ExposureParseService parseService) {
        if (file == null) throw new IllegalArgumentException("file is null");
        if (parseService == null) throw new IllegalArgumentException("parseService is null");

        final String id = UUID.randomUUID().toString();
        TASKS.put(id, PROCESSING_MARKER); // marker 表示处理中

        executor.submit(() -> {
            try {
                ExposureParseService.ParseResult r = parseService.parseFile(file);
                if (r == null) {
                    r = new ExposureParseService.ParseResult();
                }
                // ensure lists are initialized to avoid NPEs when consumers access them
                if (r.parsed == null) r.parsed = new ArrayList<>();
                if (r.errors == null) r.errors = new ArrayList<>();
                TASKS.put(id, r);
            } catch (Exception ex) {
                ExposureParseService.ParseResult r = new ExposureParseService.ParseResult();
                r.parsed = new ArrayList<>();
                r.errors = new ArrayList<>();
                r.errors.add("后台解析异常：" + ex.getMessage());
                TASKS.put(id, r);
            }
        });

        return id;
    }

    public static ExposureParseService.ParseResult getResult(String id) {
        ExposureParseService.ParseResult r = TASKS.get(id);
        if (r == PROCESSING_MARKER) return null;
        return r;
    }

    // 清理所有任务（测试专用）
    static void clearAll() {
        TASKS.clear();
    }

    // 关闭执行器（测试/应用关闭时调用）
    static void shutdownExecutor() {
        executor.shutdownNow();
    }

    // 包级可见：替换执行器，测试时可注入同步/可控执行器
    static void setExecutor(ExecutorService exec) {
        if (exec == null) throw new IllegalArgumentException("exec is null");
        ExecutorService prev = executor;
        executor = exec;
        try {
            prev.shutdownNow();
        } catch (Exception ignored) {
        }
    }

    // 包级可见：等待指定任务完成（有结果）或超时，返回是否完成
    static boolean awaitCompletion(String id, long timeoutMs) {
        if (id == null) return false;
        long deadline = System.currentTimeMillis() + Math.max(0, timeoutMs);
        while (System.currentTimeMillis() < deadline) {
            ExposureParseService.ParseResult val = TASKS.get(id);
            if (val != null && val != PROCESSING_MARKER) return true;
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        ExposureParseService.ParseResult val = TASKS.get(id);
        return val != null && val != PROCESSING_MARKER;
    }

    // 包级可见：测试时手动设置任务结果
    static void putResult(String id, ExposureParseService.ParseResult r) {
        if (id == null) throw new IllegalArgumentException("id is null");
        if (r == null) r = new ExposureParseService.ParseResult();
        if (r.parsed == null) r.parsed = new ArrayList<>();
        if (r.errors == null) r.errors = new ArrayList<>();
        TASKS.put(id, r);
    }
}
