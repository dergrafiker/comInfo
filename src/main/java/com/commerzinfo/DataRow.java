package com.commerzinfo;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

public class DataRow {
    private DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataRow dataRow = (DataRow) o;

        if (bookingDate != null ? !bookingDate.equals(dataRow.bookingDate) : dataRow.bookingDate != null) return false;
        if (bookingText != null ? !bookingText.equals(dataRow.bookingText) : dataRow.bookingText != null) return false;
        if (value != null ? !value.equals(dataRow.value) : dataRow.value != null) return false;
        if (valueDate != null ? !valueDate.equals(dataRow.valueDate) : dataRow.valueDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bookingDate != null ? bookingDate.hashCode() : 0;
        result = 31 * result + (bookingText != null ? bookingText.hashCode() : 0);
        result = 31 * result + (valueDate != null ? valueDate.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
