package com.momo.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class PeopleEntity implements Serializable {
    public String head;
    public String name;
    public String city;
    public int age;
    public ArrayList<String> pointPic;
    public ArrayList<String> pic;
    public int state;
    public int sex=0;
    public ArrayList<DynamicEntity> dynamics;
    public String constellation;
    public String signature;
    public String registTime;
    public ArrayList<String> label;

}
