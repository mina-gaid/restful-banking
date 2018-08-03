/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.groupo.bank.service;

public class Transaction {

    private int transaction_id;
    private String description;
    private double post_balance;

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPost_balance() {
        return post_balance;
    }

    public void setPost_balance(double post_balance) {
        this.post_balance = post_balance;
    }



}
