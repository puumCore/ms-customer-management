package org.puumcore.customermanagement.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ms-customer-management
 * @version 1.x
 * @since 9/20/2024 2:29 PM
 */

@Configuration
@ConfigurationProperties(prefix = "rabbit.communications")
@Getter
@Setter
public class EmailConfig {

    private String exchange;
    private String key;
}
