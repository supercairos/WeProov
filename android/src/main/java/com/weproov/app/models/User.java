package com.weproov.app.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "User")
public class User extends Model{

    @Column(name = "Name")
    public String name;

    @Column(name = "Email")
    public String email;
}
