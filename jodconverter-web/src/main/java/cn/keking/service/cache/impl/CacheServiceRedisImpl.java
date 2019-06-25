package cn.keking.service.cache.impl;

import cn.keking.service.FileConverQueueTask;
import cn.keking.service.cache.CacheService;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @auther: chenjh
 * @time: 2019/4/2 18:02
 * @description
 */
@ConditionalOnExpression("'${cache.type:default}'.equals('redis')")
@Service
public class CacheServiceRedisImpl implements CacheService {

    private Config config;

    @Autowired
    public CacheServiceRedisImpl(Config config) {
        this.config = config;
        this.redissonClient = Redisson.create(config);
    }

    private RedissonClient redissonClient;

    @Override
    public void initPDFCachePool(Integer capacity) {

    }

    @Override
    public void initIMGCachePool(Integer capacity) {

    }

    @Override
    public void initPdfImagesCachePool(Integer capacity) {

    }

    @Override
    public void putPDFCache(String key, String value) {
        RMapCache<String, String> convertedList = redissonClient.getMapCache(REDIS_FILE_PREVIEW_PDF_KEY);
        convertedList.fastPut(key, value);
    }

    @Override
    public void putImgCache(String key, List<String> value) {
        RMapCache<String, List<String>> convertedList = redissonClient.getMapCache(REDIS_FILE_PREVIEW_IMGS_KEY);
        convertedList.fastPut(key, value);
    }

    @Override
    public Map<String, String> getPDFCache() {
        return redissonClient.getMapCache(REDIS_FILE_PREVIEW_PDF_KEY);
    }

    @Override
    public String getPDFCache(String key) {
        RMapCache<String, String> convertedList = redissonClient.getMapCache(REDIS_FILE_PREVIEW_PDF_KEY);
        return convertedList.get(key);
    }

    @Override
    public Map<String, List<String>> getImgCache() {
        return redissonClient.getMapCache(REDIS_FILE_PREVIEW_IMGS_KEY);
    }

    @Override
    public List<String> getImgCache(String key) {
        RMapCache<String, List<String>> convertedList = redissonClient.getMapCache(REDIS_FILE_PREVIEW_IMGS_KEY);
        return convertedList.get(key);
    }

    @Override
    public Integer getPdfImageCache(String key) {
        RMapCache<String, Integer> convertedList = redissonClient.getMapCache(REDIS_FILE_PREVIEW_PDF_IMGS_KEY);
        return convertedList.get(key);
    }

    @Override
    public void putPdfImageCache(String pdfFilePath, int num) {
        RMapCache<String, Integer> convertedList = redissonClient.getMapCache(REDIS_FILE_PREVIEW_PDF_IMGS_KEY);
        convertedList.fastPut(pdfFilePath, num);
    }

    @Override
    public void addQueueTask(String url) {
        RBlockingQueue<String> queue = redissonClient.getBlockingQueue(FileConverQueueTask.queueTaskName);
        queue.addAsync(url);
    }

    @Override
    public String takeQueueTask() throws InterruptedException {
        RBlockingQueue<String> queue = redissonClient.getBlockingQueue(FileConverQueueTask.queueTaskName);
        return queue.take();
    }
}
