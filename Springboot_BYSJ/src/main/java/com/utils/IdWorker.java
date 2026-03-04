package com.utils;

/**
 * 雪花算法 ID 生成器
 * 生成分布式唯一 ID（64 位 long 型）
 */
public class IdWorker {
    
    // 起始时间戳（2020-01-01 00:00:00 UTC）
    private final static long START_STMP = 1577808000000L;
    
    // 各部分占用的位数
    private final static long SEQUENCE_BIT = 12;   // 序列号占 12 位
    private final static long MACHINE_BIT = 5;     // 机器标识占 5 位
    private final static long DATA_CENTER_BIT = 5; // 数据中心占 5 位
    
    // 各部分的最大值
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_DATA_CENTER_NUM = ~(-1L << DATA_CENTER_BIT);
    
    // 各部分向左移动的位数
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;
    
    private long dataCenterId;  // 数据中心
    private long machineId;     // 机器标识
    private long sequence = 0L; // 序列号
    private long lastStmp = -1L;// 上一次时间戳
    
    public IdWorker(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("Data center ID can't be greater than " + MAX_DATA_CENTER_NUM + " or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("Machine ID can't be greater than " + MAX_MACHINE_NUM + " or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }
    
    /**
     * 生成下一个 ID
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }
        
        if (currStmp == lastStmp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大限制
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            // 不同毫秒内，序列号置为 0
            sequence = 0L;
        }
        
        lastStmp = currStmp;
        
        return (currStmp - START_STMP) << TIMESTAMP_LEFT
                | dataCenterId << DATA_CENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }
    
    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }
    
    private long getNewstmp() {
        return System.currentTimeMillis();
    }
    
    // 默认实例（数据中心 ID=1, 机器 ID=1）
    private static final IdWorker instance = new IdWorker(1, 1);
    
    /**
     * 获取默认实例生成的 ID
     */
    public static long getId() {
        return instance.nextId();
    }
}
