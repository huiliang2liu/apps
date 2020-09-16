package com.live;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
//    @JsonIgnoreProperties(ignoreUnknown = true)
public class TestClass {
    public String status;
    public String country;
    public String countryCode;
    public String region;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof TestClass))
            return false;
        TestClass t = (TestClass) obj;
        return status.equals(t.status) &&
                country.equals(t.country) &&
                countryCode.equals(t.countryCode) &&
                region.equals(t.region);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("{status:%s,country:%s,countryCode:%s,region:%s}", status, country, countryCode, region);
    }
}
