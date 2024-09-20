package org.puumcore.customermanagement.repository;

import org.puumcore.customermanagement.model.entity.Customer;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ms-customer-management
 * @version 1.x
 * @since 9/20/2024 2:53 PM
 */
public interface AuthOps extends UserDetailsService {

    Customer getUserFromToken(final String token);


}
