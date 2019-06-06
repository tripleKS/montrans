package com.task.rvt.mt.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.task.rvt.mt.util.ErrorCodes.BAD_REQUEST;

public final class Converters {
    private static final Logger LOG = LogManager.getLogger(Converters.class);

    public static LocalDate getDateFromString(String dateStr) throws MTransferException {
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException e) {
            LOG.warn("Failed to parse string to date. Source string: [{}]", dateStr);
            throw new MTransferException("Invalid date format. Expected date format is yyyyMMdd. Value failed to parse: [{" + dateStr + "}]", BAD_REQUEST);
        }

        return date;
    }

    private Converters() {
    }
}
