package org.puumcore.customermanagement.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.puumcore.customermanagement.custom.Assistant;

/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ms-customer-management
 * @version 1.x
 * @since 9/20/2024 12:37 PM
 */


@Slf4j
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message.contains(Assistant.ERROR_SUFFIX) ? message : message.concat(Assistant.ERROR_SUFFIX));
        log.error(super.getMessage());
    }

    public AccessDeniedException(String message, Object... args) {
        super(String.format(message.contains(Assistant.ERROR_SUFFIX) ? message : message.concat(Assistant.ERROR_SUFFIX), args));
        log.error(super.getMessage());
    }

}