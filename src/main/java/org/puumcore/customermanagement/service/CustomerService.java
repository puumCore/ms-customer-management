package org.puumcore.customermanagement.service;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.puumcore.customermanagement.config.EmailConfig;
import org.puumcore.customermanagement.custom.Assistant;
import org.puumcore.customermanagement.exceptions.AccessDeniedException;
import org.puumcore.customermanagement.exceptions.BadRequestException;
import org.puumcore.customermanagement.exceptions.FailureException;
import org.puumcore.customermanagement.exceptions.NotFoundException;
import org.puumcore.customermanagement.model.BatchSummary;
import org.puumcore.customermanagement.model.Email;
import org.puumcore.customermanagement.model.Forms;
import org.puumcore.customermanagement.model.Paged;
import org.puumcore.customermanagement.model.entity.Customer;
import org.puumcore.customermanagement.repository.AuthOps;
import org.puumcore.customermanagement.repository.CustomerRepo;
import org.puumcore.customermanagement.utils.DateUtils;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ms-customer-management
 * @version 1.x
 * @since 9/20/2024 12:44 PM
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerService extends Assistant implements AuthOps {

    private final PasswordEncoder passwordEncoder;
    private final CustomerRepo customerRepo;
    private final MQPublisher mqPublisher;
    private final EmailConfig emailConfig;

    public final Paged<Customer> getCustomers(final Pageable pageable) {
        Page<Customer> all = customerRepo.findAll(pageable);

        Paged<Customer> paged = new Paged<>(all.getTotalPages());
        paged.getData().addAll(all.get().toList());

        return paged;
    }

    public final Customer remove(final Long id) {
        Customer customer = getCustomer(id);

        try {
            customerRepo.deleteById(customer.getCustomer_id());
        } catch (Exception e) {
            log.error("delete", e);
            throw new FailureException("Failed to update customer data");
        }

        return customer;
    }

    public final Customer update(final Forms.UpdateCustomer updateCustomer) {
        Optional.ofNullable(updateCustomer.phone())
                .ifPresent(s -> {
                    if (s.isBlank() || !correctPhoneNumberFormat(s)) {
                        throw new BadRequestException("Invalid customer phone");
                    }
                });
        Optional.ofNullable(updateCustomer.address())
                .ifPresent(s -> {
                    if (s.isBlank() || containsSpecialCharacter(s)) {
                        throw new BadRequestException("Invalid customer address");
                    }
                });

        Customer customer = getCustomer(updateCustomer.customer_id());
        Optional.ofNullable(updateCustomer.phone())
                .ifPresent(customer::setPhone);
        Optional.ofNullable(updateCustomer.address())
                .ifPresent(customer::setAddress);

        try {
            customerRepo.deleteById(customer.getCustomer_id());
            customer = customerRepo.save(customer);
        } catch (Exception e) {
            log.error("update", e);
            throw new FailureException("Failed to update customer data");
        }

        return customer;
    }

    public final BatchSummary<Object> save(final List<Forms.SaveCustomerReq> customerReqList) {
        if (customerReqList.isEmpty()) {
            throw new NotFoundException("Please add one or more customers to continue");
        }

        final BatchSummary<Object> batchSummary = new BatchSummary<>();
        customerReqList.forEach(saveCustomerReq -> {
            if (saveCustomerReq.name().isBlank() || containsSpecialCharacter(saveCustomerReq.name().trim().replace(" ", ""))) {
                batchSummary.getFailed().add(
                        new BatchSummary.BatchItem<>(saveCustomerReq.name(), "Invalid customer name")
                );
                return;
            }
            if (saveCustomerReq.national_id().isBlank() || !isNumeric(saveCustomerReq.national_id().trim())  || containsSpecialCharacter(saveCustomerReq.national_id().trim().replace(" ", ""))) {
                batchSummary.getFailed().add(
                        new BatchSummary.BatchItem<>(saveCustomerReq.national_id(), "Invalid customer national id")
                );
                return;
            }
            if (saveCustomerReq.dob().isAfter(LocalDate.now(clock))) {
                batchSummary.getFailed().add(
                        new BatchSummary.BatchItem<>(saveCustomerReq.dob().format(dateTimeFormatter), "Invalid customer date of birth")
                );
                return;
            }
            Optional.ofNullable(saveCustomerReq.address())
                    .ifPresent(s -> {
                        if (s.isBlank() || containsSpecialCharacter(s.trim().replace(" ", ""))) {
                            batchSummary.getFailed().add(
                                    new BatchSummary.BatchItem<>(s, "Invalid customer address")
                            );
                        }
                    });
            if (saveCustomerReq.phone().isBlank() || !correctPhoneNumberFormat(saveCustomerReq.phone())) {
                batchSummary.getFailed().add(
                        new BatchSummary.BatchItem<>(saveCustomerReq.phone(), "Invalid customer phone number")
                );
                return;
            }
            if (saveCustomerReq.email().isBlank() || !correctEmailFormat(saveCustomerReq.email())) {
                batchSummary.getFailed().add(
                        new BatchSummary.BatchItem<>(saveCustomerReq.email(), "Invalid customer email")
                );
                return;
            }
            if (saveCustomerReq.password().isBlank() || (saveCustomerReq.password().length() > 8 && saveCustomerReq.password().length() <= 12)) {
                batchSummary.getFailed().add(
                        new BatchSummary.BatchItem<>(saveCustomerReq.name(), "Invalid customer password. Remember it should be between 8-12 characters")
                );
                return;
            }

            Optional<Customer> customerOptional = customerRepo.findByEmail(saveCustomerReq.email());
            if (customerOptional.isPresent()) {
                batchSummary.getFailed().add(
                        new BatchSummary.BatchItem<>(saveCustomerReq.email(), "A customer with this email already exists")
                );
            } else {
                final String encodedPwd = passwordEncoder.encode(saveCustomerReq.password());
                final Customer customer = new Customer();
                customer.setName(saveCustomerReq.name());
                customer.setNational_id_no(saveCustomerReq.national_id());
                customer.setDob(DateUtils.asDate(saveCustomerReq.dob()));
                customer.setGender(saveCustomerReq.gender());
                customer.setAddress(saveCustomerReq.address());
                customer.setPhone(saveCustomerReq.phone());
                customer.setEmail(saveCustomerReq.email());
                customer.setPassword(encodedPwd);
                customer.setRole(saveCustomerReq.role());

                try {
                    Customer saved = customerRepo.save(customer);
                    batchSummary.getSuccessful().add(
                            new BatchSummary.BatchItem<>(saved.getName(), "Successfully saved")
                    );
                    boolean emailSent = emailSent(saved.getEmail(), "Welcome %s".formatted(saved.getName()), "Hello, you have been successfully added as a customer into the system");
                    log.info("Attempt to send welcome message to new customer = {}", emailSent);
                } catch (Exception e) {
                    log.error("save", e);
                    batchSummary.getFailed().add(
                            new BatchSummary.BatchItem<>(saveCustomerReq.name(), "Failed to save customer")
                    );
                }
            }
        });

        final float successPercentage = ((float) batchSummary.getSuccessful().size() / customerReqList.size()) * 100;
        batchSummary.setSuccessRate(successPercentage);

        return batchSummary;
    }

    protected final boolean emailSent(final String to, final String subject, final String content) {
        final Email email = new Email();
        email.setTo(to);
        email.setSubject(subject);
        email.setMessage(content);

        MessageProperties properties = mqPublisher.setProperties(UUID.randomUUID().toString());
        return mqPublisher.publishedToTopic(properties, emailConfig.getExchange(), emailConfig.getKey(), email.toString().getBytes(StandardCharsets.UTF_8));
    }


    public final Customer getCustomer(final Long id) {
        log.info("Searching for customer with the id = {}", id);
        Optional<Customer> customerOptional = customerRepo.findById(id);
        if (customerOptional.isEmpty()) {
            throw new NotFoundException("No such customer found");
        }

        return customerOptional.get();
    }

    public final Customer getCustomer(@NonNull final String username) {
        log.info("Searching for customer with the username = {}", username);
        Optional<Customer> customerOptional = customerRepo.findByEmail(username);
        if (customerOptional.isEmpty()) {
            throw new NotFoundException("No such customer found");
        }

        return customerOptional.get();
    }


    @Override
    public Customer getUserFromToken(final String token) {
        Customer customer = null;
        if (!token.isBlank()) {
            byte[] decoded = Base64.getDecoder().decode(token);
            String decodedToken = new String(decoded);
            if (decodedToken.contains(":")) {
                String paramA = decodedToken.split(":")[0];
                String paramB = decodedToken.split(":")[1];
                if (paramA.contains("=") && paramB.contains("=")) {
                    if (paramA.contains("username")) {
                        String s = paramA.split("=")[1];
                        customer = getCustomer(s);
                    } else if (paramB.contains("username")) {
                        String s = paramA.split("=")[1];
                        customer = getCustomer(s);
                    }
                    String pwd = null;
                    if (paramA.contains("password")) {
                        pwd = paramA.split("=")[1];
                    } else if (paramB.contains("username")) {
                        pwd = paramA.split("=")[1];
                    }

                    if (customer != null && pwd != null) {
                        /*boolean matches = passwordEncoder.matches(pwd, customer.getPassword());
                        if (!matches) {
                            throw new AccessDeniedException("Failed to verify token");
                        }*/
                    }
                }
            }
        }
        if (customer == null) {
            throw new AccessDeniedException("Failed to verify token");
        }
        return customer;
    }



    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customerOptional = customerRepo.findByEmail(username);
        if (customerOptional.isEmpty()) {
            throw new UsernameNotFoundException("Invalid credentials");
        }
        Customer customer = customerOptional.get();
        if (customer.getRole() == null) {
            throw new UsernameNotFoundException("Invalid credentials");
        }
        final List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>(
                Collections.singletonList(new SimpleGrantedAuthority(customer.getRole().name()))
        ) {
        };
        return new User(customer.getEmail(), customer.getPassword(), grantedAuthorities);
    }
}
