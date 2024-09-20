package org.puumcore.customermanagement.model;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ms-customer-management
 * @version 1.x
 * @since 9/20/2024 12:37 PM
 */

@Getter
@Setter
@NoArgsConstructor
public class Email implements Serializable {

    @Serial
    private static final long serialVersionUID = 184504L;

    private String to;
    private List<String> cc;
    private String subject;
    private String message;

    @Override
    public String toString() {
        return new Gson().toJson(this, Email.class);
    }
}
