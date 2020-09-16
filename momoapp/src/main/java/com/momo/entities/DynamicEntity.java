package com.momo.entities;

import java.io.Serializable;
import java.util.List;

public class DynamicEntity implements Serializable {
    public DynamicEntity(String head, String name, int sex, int age, long time, String distance, int read, List<String> pic,
                         String text, int giveLike, List<CommentEntity> commentEntities, boolean give) {
        this.head = head;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.time = time;
        this.distance = distance;
        this.read = read;
        this.pic = pic;
        this.text = text;
        this.giveLike = giveLike;
        this.commentEntities = commentEntities;
        this.give = give;
    }

    public String head;
    public String name;
    public int sex;
    public int age;
    public long time;
    public String distance;
    public int read;
    public List<String> pic;
    public String text;
    public int giveLike;
    public List<CommentEntity> commentEntities;
    public boolean give;
}
