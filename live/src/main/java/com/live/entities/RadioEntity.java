package com.live.entities;

import java.util.List;

import androidx.annotation.Nullable;

public class RadioEntity {
    public String name;
    public List<String> strings;
    public int playIndex;
    public int playTimes;
    public boolean play;

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==null||!(obj instanceof RadioEntity))
            return false;
        return name.equals(((RadioEntity) obj).name);
    }
}
