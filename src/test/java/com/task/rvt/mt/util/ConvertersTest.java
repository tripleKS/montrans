package com.task.rvt.mt.util;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static com.task.rvt.mt.util.Converters.getDateFromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ConvertersTest {
    @Test
    public void testWrongFormat() {
        Throwable mte = catchThrowable(() -> getDateFromString("2019099") );

        assertThat(mte).isInstanceOf(MTransferException.class);
    }

    @Test
    public void testWrongDate() {
        Throwable mte = catchThrowable(() -> getDateFromString("20190949") );

        assertThat(mte).isInstanceOf(MTransferException.class);
    }

    @Test
    public void testCorrectDate()throws Exception {
        LocalDate calculatedDate = getDateFromString("20190606");
        assertThat(calculatedDate.getYear()).isEqualTo(2019);
        assertThat(calculatedDate.getMonth()).isEqualTo(Month.JUNE);
        assertThat(calculatedDate.getDayOfMonth()).isEqualTo(6);
    }
}