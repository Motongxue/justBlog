package com.nbclass.framework.aspect;

import com.nbclass.framework.annotation.RedisCache;
import com.nbclass.framework.util.AspectUtil;
import com.nbclass.service.RedisService;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * RedisCacheAspect
 *
 * @version V1.0
 * @date 2019/10/10
 * @author nbclass
 */
@Aspect
@Component
public class RedisCacheAspect {

    private static Logger logger = LoggerFactory.getLogger(RedisCacheAspect.class);

    private static final String CACHE_PREFIX = "JUST_";

    @Autowired
    private RedisService redisService;

    /**
     * RedisCache handle
     *
     * @param point
     *
     */
    @Around("pointcut()")
    public Object handle(ProceedingJoinPoint point) throws Throwable {
        Method currentMethod = AspectUtil.getMethod(point);

        // 获取操作名称
        RedisCache cache = currentMethod.getAnnotation(RedisCache.class);
        boolean flush = cache.flush();
        if (flush) {
            String classPrefix = StringUtils.isNotBlank(cache.key()) ? (CACHE_PREFIX+cache.key()) : AspectUtil.getKeyOfClassPrefix(point, CACHE_PREFIX);
            redisService.delBatch(classPrefix);
            return point.proceed();
        }
        String  key = StringUtils.isNotBlank(cache.key()) ? (CACHE_PREFIX+cache.key()) : AspectUtil.getKey(point, CACHE_PREFIX);
        boolean hasKey = redisService.hasKey(key);
        if (hasKey) {
            try {
                return redisService.get(key);
            } catch (Exception e) {
                logger.error("从缓存中获取数据失败！", e);
            }
        }
        // 先执行业务
        Object result = point.proceed();
        redisService.set(key, result, cache.expire(), cache.unit());
        return result;
    }

    /**
     * pointcut
     *
     */
    @Pointcut(value = "@annotation(com.nbclass.framework.annotation.RedisCache)")
    public void pointcut() {}
}
