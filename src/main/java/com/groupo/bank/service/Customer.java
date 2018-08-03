package com.groupo.bank.service;

public class Customer {

    private int customer_id;
    private String email;
    private String name;


    public int getId() {
        return customer_id;
    }

    public void setCustomerID(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
