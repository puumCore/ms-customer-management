package org.puumcore.customermanagement.service;



import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.puumcore.customermanagement.custom.Assistant;
import org.puumcore.customermanagement.utils.DateUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ms-customer-management
 * @version 1.x
 * @since 9/20/2024 12:37 PM
 */

@RequiredArgsConstructor
@Slf4j
@Service
public class MQPublisher extends Assistant {

    private final RabbitTemplate rabbitTemplate;

    public boolean publishedToTopic(@NonNull MessageProperties properties, @NonNull final String exchange, @NonNull final String routingKey, byte @NonNull [] msg) {
        try {
            rabbitTemplate.send(exchange, routingKey, new Message(msg, properties));
            return true;
        } catch (Exception e) {
            log.error("Unable to publish to exchange with routing key", e);
        }
        return false;
    }

    public final MessageProperties setProperties(@NonNull String id) {
        final MessageProperties properties = new MessageProperties();
        properties.setMessageId(id);
        properties.setTimestamp(DateUtils.asDate(LocalDateTime.now(clock)));
        properties.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return properties;
    }


}
