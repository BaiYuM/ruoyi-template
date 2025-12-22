package com.ruoyi.common.utils.uuid;

import java.security.SecureRandom;

/**
 * ID生成器工具类
 * 
 * @author ruoyi
 */
public class IdUtils
{
    // 雪花算法相关参数
    private static final long START_TIMESTAMP = 1609459200000L; // 2021-01-01 00:00:00
    private static final long DATA_CENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    private static final long workerId;
    private static final long dataCenterId;
    private static long lastTimestamp = -1L;
    private static long sequence = 0L;
    
    static {
        // 初始化工作节点ID和数据中心ID（这里简单使用随机数，实际生产环境中建议从配置文件或外部服务获取）
        SecureRandom random = new SecureRandom();
        workerId = random.nextLong() & MAX_WORKER_ID;
        dataCenterId = random.nextLong() & MAX_DATA_CENTER_ID;
    }

    /**
     * 获取随机UUID
     * 
     * @return 随机UUID
     */
    public static String randomUUID()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     * 
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID()
    {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     * 
     * @return 随机UUID
     */
    public static String fastUUID()
    {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     * 
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID()
    {
        return UUID.fastUUID().toString(true);
    }
    
    /**
     * 雪花算法生成ID
     * 
     * @return 生成的ID
     */
    public synchronized static Long getSnowflakeId() {
        long timestamp = System.currentTimeMillis();
        
        // 获取当前时间戳
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨异常");
        }
        
        // 判断是否同一毫秒内
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号溢出，等待下一毫秒
            if (sequence == 0) {
                timestamp = getNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        
        lastTimestamp = timestamp;
        
        // 组装ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }
    
    /**
     * 获取下一个毫秒数
     * 
     * @param lastTimestamp 最后的时间戳
     * @return 下一个毫秒数
     */
    private static long getNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}