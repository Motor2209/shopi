package com.iadvigun.study;

import java.time.LocalDateTime;

public class Product {
    private Long id;    // ??????
    private String name;
    private int amount;
    private int expiration;
    private String overdueDate;

    public Product(String name, int amount, int expiration) {
        this.name = name;
        this.amount = amount;
        this.expiration = expiration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }


    public String getOverdueDate() {
        return overdueDate.toString();
    }

    public void setOverdueDate(String overdueDate) {
        this.overdueDate = overdueDate;
    }
}
