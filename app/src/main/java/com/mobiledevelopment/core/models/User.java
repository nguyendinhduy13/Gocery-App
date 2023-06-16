package com.mobiledevelopment.core.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

public final class User {
    public static final String ROLE_ID_CUSTOMER = "1";
    public static final String ROLE_ID_ADMIN = "2";

    public static User undefinedUser = new User(
            "Undefined",
            "Undefined",
            "Undefined",
            "Undefined",
            "Undefined",
            "Undefined",
            "Undefined",
            "Undefined",
            "Undefined",
            -1L,
            "Undefined");

    @DocumentId
    private final String id;
    private final String email;

    private final String fullName;
    private final String password;
    private final String username;
    private final String phone;
    private final String gender;
    private final String age;
    private final String roleId;
    private final long membershipPoint;
    private final String avatar;

    // Need to have a constructor without params for Firebase's toObject() method to work
    @SuppressWarnings("unused")
    public User() {
        this.id = undefinedUser.getId();
        this.email = undefinedUser.email;
        this.password = undefinedUser.password;
        this.username = undefinedUser.username;
        this.phone = undefinedUser.phone;
        this.gender = undefinedUser.gender;
        this.age = undefinedUser.age;
        this.roleId = undefinedUser.roleId;
        this.membershipPoint = undefinedUser.membershipPoint;
        this.avatar = undefinedUser.avatar;
        fullName = null;
    }

    public User(
            String id,
            String email,
            String fullName,
            String password,
            String username,
            String phone,
            String gender,
            String age,
            String roleId,
            long membershipPoint,
            String avatar) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
        this.roleId = roleId;
        this.membershipPoint = membershipPoint;
        this.avatar = avatar;
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getRoleId() {
        return roleId;
    }

    public long getMembershipPoint() {
        return membershipPoint;
    }

    public String getAvatar() {
        return avatar;
    }


    @NonNull
    @Override
    public String toString() {
        return "id= " + getId() + "; username= " + getUsername() + "; email= " + getEmail();
    }

}
