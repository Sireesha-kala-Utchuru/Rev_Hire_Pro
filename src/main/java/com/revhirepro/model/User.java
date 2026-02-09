package com.revhirepro.model;

public class User {
    private final long id;
    private final Role role;
    private final String name;
    private final String email;
    private final String phone;
    private final boolean locked;

    public User(long id, Role role, String name, String email, String phone, boolean locked) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.locked = locked;
    }

    public long getId() { return id; }
    public Role getRole() { return role; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isLocked() { return locked; }
}
