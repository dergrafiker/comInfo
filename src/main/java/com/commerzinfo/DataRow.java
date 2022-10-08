package com.commerzinfo;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Objects;

public class DataRow {
    private final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

    private LocalDate bookingDate;
    private String bookingText;
    private LocalDate valueDate;
    private BigDecimal value;

    public DataRow() {
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingText() {
        return bookingText;
    }

    public void setBookingText(String bookingText) {
        this.bookingText = bookingText;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
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
                "df=" + df +
                ", bookingDate=" + bookingDate +
                ", bookingText='" + bookingText + '\'' +
                ", valueDate=" + valueDate +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataRow dataRow = (DataRow) o;
        return Objects.equals(df, dataRow.df) && bookingDate.equals(dataRow.bookingDate) && bookingText.equals(dataRow.bookingText) && valueDate.equals(dataRow.valueDate) && value.equals(dataRow.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(df, bookingDate, bookingText, valueDate, value);
    }

    public boolean allFieldsAreFilled() {
        return Objects.nonNull(bookingDate)
                && Objects.nonNull(bookingText)
                && Objects.nonNull(valueDate)
                && Objects.nonNull(value);
    }
}
