package org.puumcore.customermanagement.custom;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.time.Clock;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ecommerce
 * @version 1.x
 * @since 7/18/2024 2:28 PM
 */

public abstract class Assistant {

    public static final Clock clock = Clock.system(ZoneId.of("Africa/Nairobi"));
    private static final String DATE_TIME_FORMAT = "yyyy-MMM-dd HH:mm:ss";
    public final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    public static final String ERROR_SUFFIX = " Please try again later.";

    protected final boolean containsSpecialCharacter(final String param) {
        if (param == null) {
            return true;
        }
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(param);
        return matcher.find();
    }

    protected final boolean correctPhoneNumberFormat(@NonNull final String param) {
        return Pattern.matches("^\\d{10}$", param) ||
                Pattern.matches("^(\\d{3}[- .]?){2}\\d{4}$", param) ||
                Pattern.matches("^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", param) ||
                Pattern.matches("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", param);
    }

    protected boolean correctEmailFormat(@NotNull final String param) {
        return Pattern.matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", param);
    }

    protected final boolean isNumeric(@NonNull final String param) {
        return Pattern.matches("[+]?[0-9]+", param);
    }

}
