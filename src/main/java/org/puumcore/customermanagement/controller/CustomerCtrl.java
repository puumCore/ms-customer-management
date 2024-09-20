package org.puumcore.customermanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.puumcore.customermanagement.model.BatchSummary;
import org.puumcore.customermanagement.model.Forms;
import org.puumcore.customermanagement.model.Paged;
import org.puumcore.customermanagement.model.entity.Customer;
import org.puumcore.customermanagement.service.CustomerService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ms-customer-management
 * @version 1.x
 * @since 9/20/2024 1:47 PM
 */

@RestController
@RequestMapping("customer")
@Slf4j
@RequiredArgsConstructor
public class CustomerCtrl {

    private final CustomerService service;

    @Operation(
            summary = "Save customer",
            description = "Creates one or more customers provided in the request body",
            tags = "customer-mgnt"
    )
    @PostMapping(path = "save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    BatchSummary<Object> save(@RequestBody @NonNull final List<Forms.SaveCustomerReq> request) {
        return service.save(request);
    }

    @Operation(
            summary = "Update customer info",
            description = "Can update target customer's address or phone number",
            tags = "customer-mgnt"
    )
    @PatchMapping(path = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Customer update(@RequestBody @NonNull final Forms.UpdateCustomer request) {
        return service.update(request);
    }

    @Operation(
            summary = "Delete customer",
            description = "Deletes target customer and returns a copy of the deleted customer",
            tags = "customer-mgnt"
    )
    @DeleteMapping(path = "remove", params = "customer", produces = MediaType.APPLICATION_JSON_VALUE)
    Customer remove(@RequestParam(name = "customer") @NonNull final Long id) {
        return service.remove(id);
    }

    @Operation(
            summary = "Get customers",
            description = "Fetch existing customers",
            tags = "customer-mgnt"
    )
    @PostMapping("/filter")
    Paged<Customer> getCustomers(
            @ParameterObject
            @PageableDefault(size = 20, page = 1)
            @SortDefault(sort = "customer_id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return service.getCustomers(pageable);
    }

    @Operation(
            summary = "Get customer",
            description = "Fetch customer by Id",
            tags = "customer-mgnt"
    )
    @PostMapping(path = "byId", params = "customer", produces = MediaType.APPLICATION_JSON_VALUE)
    Customer byId(@RequestParam(name = "customer") @NonNull final Long id) {
        return service.getCustomer(id);
    }

    @Operation(
            summary = "Get customer",
            description = "Fetch customer by username",
            tags = "customer-mgnt"
    )
    @PostMapping(path = "byUsername", params = "customer", produces = MediaType.APPLICATION_JSON_VALUE)
    Customer byUsername(@RequestParam(name = "customer") @NonNull final String username) {
        return service.getCustomer(username);
    }

}
