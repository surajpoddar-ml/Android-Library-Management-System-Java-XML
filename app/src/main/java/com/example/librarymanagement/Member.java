package com.example.librarymanagement;

import java.io.Serializable;

public class Member implements Serializable {
    private long id;
    private String fullName;
    private String membershipId;
    private String email;
    private String phone;
    private String address;
    private String registrationDate;
    private String status;

    public Member() {
    }

    public Member(long id, String fullName, String membershipId, String email,
                  String phone, String address, String registrationDate, String status) {
        this.id = id;
        this.fullName = fullName;
        this.membershipId = membershipId;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    public Member(String fullName, String membershipId, String email,
                  String phone, String address, String registrationDate, String status) {
        this(-1, fullName, membershipId, email, phone, address, registrationDate, status);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return fullName + " (" + membershipId + ")";
    }
}
