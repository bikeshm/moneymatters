package com.tricon.labs.crumbs.models;

public class Contact {
    public int id;
    public String name;
    public String phone;

    public Contact(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    @Override
    public boolean equals(Object contact) {
        return contact instanceof Contact && this.phone.equals(((Contact) contact).phone);
    }

    @Override
    public int hashCode() {
        return this.phone.hashCode();
    }
}
