package com.nmt.education.config;

import com.nmt.education.commmons.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@ConditionalOnClass(TokenConfigProperties.class)
public class TokenConfig implements InitializingBean {


    @Autowired
    private TokenConfigProperties tokenConfigProperties;


    /**
     * Invoked by the containing {@code BeanFactory} after it has set all bean properties
     * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
     * <p>This method allows the bean instance to perform validation of its overall
     * configuration and final initialization when all bean properties have been set.
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an
     *                   essential property) or if initialization fails for any other reason
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        TokenUtil.setJWT_KEY(tokenConfigProperties.getKey());
        TokenUtil.setEXPIRE_MINUTE(tokenConfigProperties.getExpireMinute());
        if (log.isDebugEnabled()) {
            log.info("加载token：" + tokenConfigProperties.getKey().substring(0, 4));
            log.info("超时时间：" + tokenConfigProperties.getExpireMinute());
        }
    }
}
