package com.example.duyda.onlinesaleshop.Models;

public class Account {
    private String Email;
    private String Name;
//    private String Pass;
    private String Phone;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

//    public String getPass() {
//        return Pass;
//    }

//    public void setPass(String pass) {
//        Pass = pass;
//    }


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }


    public Account() {

    }


    public Account(String email, String name, String phone) {
        Email = email;
        Name = name;
        Phone = phone;
    }
}