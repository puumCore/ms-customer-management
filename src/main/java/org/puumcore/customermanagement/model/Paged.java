package org.puumcore.customermanagement.model;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ms-customer-management
 * @version 1.x
 * @since 9/20/2024 1:47 PM
 */

@Getter
@Setter
@RequiredArgsConstructor
public class Paged<E> implements Serializable {

    @Serial
    private static final long serialVersionUID = 564354L;

    private final Integer totalPages;
    private List<E> data = new ArrayList<>();

    @Override
    public String toString() {
        return new Gson().toJson(this, this.getClass());
    }

}
