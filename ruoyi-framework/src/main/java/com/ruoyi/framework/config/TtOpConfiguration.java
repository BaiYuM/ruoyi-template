package com.ruoyi.framework.config;
import com.ruoyi.framework.handler.VerifyWebhookHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
// import org.apache.ibatis.ognl.NullHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
// import sun.rmi.log.LogHandler;
import vip.gadfly.tiktok.config.impl.TtOpDefaultConfigImpl;
import vip.gadfly.tiktok.config.impl.TtOpRedisConfigImpl;
import vip.gadfly.tiktok.core.enums.TtOpConst;
import vip.gadfly.tiktok.core.http.impl.OkHttpTtOpHttpClient;
import vip.gadfly.tiktok.core.message.TtOpWebhookRedisDuplicateChecker;
import vip.gadfly.tiktok.core.redis.RedisTemplateTtOpRedisOps;
import vip.gadfly.tiktok.open.common.DefaultTtOpServiceImpl;
import vip.gadfly.tiktok.open.common.ITtOpBaseService;
// import vip.gadfly.tiktok.open.message.ITtOpWebhookMessageHandler;
import vip.gadfly.tiktok.open.message.TtOpWebhookMessageRouter;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(TtOpConfiguration.TtOpProperties.class)
public class TtOpConfiguration {
    private final TtOpProperties properties;
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public ITtOpBaseService ttBaseService() {
        final List<TtOpProperties.TtOpConfig> configs = this.properties.getConfigs();
        if (CollectionUtils.isEmpty(configs)) {
            throw new RuntimeException("抖音配置无效，请检查！");
        }

        RedisTemplateTtOpRedisOps redisOps = new RedisTemplateTtOpRedisOps(redisTemplate);
        DefaultTtOpServiceImpl service = new DefaultTtOpServiceImpl();
        service.setMultiConfigStorages(configs.stream().map(a -> {
            TtOpDefaultConfigImpl configStorage;
            if (this.properties.isUseRedis()) {
                configStorage = new TtOpRedisConfigImpl(redisOps, "tiktok_open");
            } else {
                configStorage = new TtOpDefaultConfigImpl();
            }
            configStorage.setClientKey(a.getClientKey());
            configStorage.setClientSecret(a.getClientSecret());
            return configStorage;
        }).collect(Collectors.toMap(TtOpDefaultConfigImpl::getClientKey, a -> a, (o, n) -> o)));

        // 设置http client，okhttp是默认值，可以不设置
        service.setTiktokOpenHttpClient(new OkHttpTtOpHttpClient());
        return service;
    }

    @Data
    @ConfigurationProperties(prefix = "tt.op")
    public static class TtOpProperties {
        /**
         * 是否使用redis存储access token
         */
        private boolean useRedis;

        /**
         * 多个抖音开放应用配置信息
         */
        private List<TtOpConfig> configs;

        @Data
        public static class TtOpConfig {
            /**
             * 设置抖音开放应用的clientKey
             */
            private String clientKey;

            /**
             * 设置抖音开放应用的app secret
             */
            private String clientSecret;
        }
    }


    // private final LogHandler logHandler;
    // private final NullHandler nullHandler;
    private final VerifyWebhookHandler verifyWebhookHandler;

    @Bean
    public TtOpWebhookMessageRouter messageRouter() {
        RedisTemplateTtOpRedisOps redisOps = new RedisTemplateTtOpRedisOps(redisTemplate);
        final TtOpWebhookMessageRouter messageRouter =
                new TtOpWebhookMessageRouter(new TtOpWebhookRedisDuplicateChecker(redisOps));

        // 默认async是true，也就是异步执行，可以在这些异步处理里加上专用的日志记录等
//        messageRouter.rule().addHandler(logHandler).next();
        // 可以指定event来让这个事件的情况都进入某个handler。对于私信类事件还可以指定msgType
        // msgType根据收到消息中的content.messageType字段来区分。event 和 msgType 的判断都忽略大小写。
        // 如果给非私信事件配置msgType，会因为取不到消息中的content.messageType而认为不符合路由
        // 因此对于非私信事件，必须将msgType置为null或不设置
        //
        // 这里使用verify webhook仅为示例，实际开发中这个事件直接在controller里处理更简单
        messageRouter.rule().async(false).event(TtOpConst.WebhookEventType.VERIFY_WEBHOOK).addHandler(this.verifyWebhookHandler).end();
        // 不指定event则这个handler处理所有的事件。路由规则的设置依赖ArrayList，因此需要注意顺序，这个兜底的路由需要放在最后。
//        messageRouter.rule().async(false).addHandler(logHandler).end();
        return messageRouter;
    }
}
