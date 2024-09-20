package org.puumcore.customermanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.puumcore.customermanagement.model.constants.Gender;
import org.puumcore.customermanagement.model.constants.Role;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ms-customer-management
 * @version 1.x
 * @since 9/20/2024 12:37 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "customers")
public class Customer implements Serializable {

    @Serial
    private static final long serialVersionUID = 753577L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long customer_id;
    private String name;
    private String national_id_no;
    @Temporal(TemporalType.DATE)
    private Date dob;
    private Gender gender;
    private String address;
    private String phone;
    private String email;
    private String password;
    private Role role;

}
