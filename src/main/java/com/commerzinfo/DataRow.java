package com.commerzinfo;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

public class DataRow {
    private final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

    private Date bookingDate;
    private String bookingText;
    private Date valueDate;
    private BigDecimal value;

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingText() {
        return bookingText;
    }

    public void setBookingText(String bookingText) {
        this.bookingText = bookingText;
    }

    @SuppressWarnings("unused")
    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DataRow{" +
                "bookingDate=" + df.format(bookingDate) +
                ", bookingText='" + bookingText + '\'' +
                ", valueDate=" + df.format(valueDate) +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass().equals(DataRow.class) && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
