package org.puumcore.customermanagement.model;

import com.google.gson.Gson;
import lombok.NonNull;
import org.puumcore.customermanagement.model.constants.Gender;
import org.puumcore.customermanagement.model.constants.Role;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ms-customer-management
 * @version 1.x
 * @since 9/20/2024 1:03 PM
 */

public abstract class Forms {


    public record UpdateCustomer(@NonNull Long customer_id, String phone, String address) implements Serializable {

        @Serial
        private static final long serialVersionUID = 392421L;

        @Override
        public String toString() {
            return new Gson().toJson(this, this.getClass());
        }

    }


    public record SaveCustomerReq(@NonNull String name,
                                  @NonNull String national_id,
                                  @NonNull LocalDate dob,
                                  @NonNull Gender gender,
                                  String address,
                                  @NonNull String phone,
                                  @NonNull String email,
                                  @NonNull String password,
                                  @NonNull Role role) implements Serializable {

        @Serial
        private static final long serialVersionUID = 814686L;

        @Override
        public String toString() {
            return new Gson().toJson(this, this.getClass());
        }

    }

}
