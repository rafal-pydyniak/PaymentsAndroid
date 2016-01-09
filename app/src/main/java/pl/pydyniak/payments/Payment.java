package pl.pydyniak.payments;

import java.util.Date;

/**
 * Created by rafal on 29.11.15.
 */
public class Payment {
    private String name;
    private Date date;
    private Double price;
    private String description;
    private boolean isOpen;

    public Payment() {
    }

    public Payment(String name, Date date, String description, double price) {
        super();
        this.name = name;
        this.date = date;
        this.price = price;
        this.description = description;
        this.isOpen = false;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
}
