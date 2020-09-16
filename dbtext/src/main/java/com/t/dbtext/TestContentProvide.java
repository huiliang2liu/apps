package com.t.dbtext;

import android.content.Context;

import com.io.db.TableEntity;
import com.io.provider.BaseContentProvider;
import com.io.provider.DbProvider;
import com.io.provider.IProvider;

import java.util.ArrayList;
import java.util.List;

public class TestContentProvide extends BaseContentProvider {
    protected String autohority() {
        return "com.t.dbtext";
    }

    @Override
    protected List<String> matchers() {
        List<String> matchers = new ArrayList<>();
        matchers.add("student");
        return matchers;
    }

    @Override
    protected IProvider provider() {
        return new DbProvider(getContext(), "student") {
            {
                List<TableEntity> entities = new ArrayList<>();
                entities.add(new TableEntity().setName("id").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.PRIMARY_KEY));
                entities.add(new TableEntity().setName("name").setType(TableEntity.KeyType.STRING).add(TableEntity.Type.NOT_NULL));
                createTable("student", entities);
            }
        };
    }
}
