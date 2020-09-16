package com.live.entities;

import java.util.ArrayList;
import java.util.List;

public class ListEntity {
    public int type;
    public int topPading;
    public boolean select = false;
    public String title;
    public Object object;

    public static ListEntity entity0() {
        ListEntity entity = new ListEntity();
        List<String> urls = new ArrayList<>();
        urls.add("http://img.08087.cc/uploads/20190113/18/1547376821-ZoKisEvDNa.jpg");
        urls.add("http://img0.imgtn.bdimg.com/it/u=1546416376,1306385786&fm=26&gp=0.jpg");
        urls.add("http://img2.imgtn.bdimg.com/it/u=2690387131,3828772643&fm=26&gp=0.jpg");
        urls.add("http://image.biaobaiju.com/uploads/20180917/22/1537193641-xITEYMrOyD.jpg");
        urls.add("http://pic1.win4000.com/wallpaper/7/58b0f6bf5e86e.jpg");
        entity.object = urls;
        entity.type = 0;
        return entity;
    }

    public static ListEntity entity1() {
        ListEntity entity1 = new ListEntity();
        entity1.object = "测试";
        entity1.type = 1;
        return entity1;
    }

    public static ListEntity entity2() {
        ListEntity entity2 = new ListEntity();
        entity2.type = 2;
        entity2.object = "热播";
        return entity2;
    }

    public static ListEntity entity3() {
        ListEntity entity3 = new ListEntity();
        entity3.type = 3;
        ShowEntity showEntity = new ShowEntity();
        showEntity.name = "刘德华";
        showEntity.url = "http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
        entity3.object = showEntity;
        return entity3;
    }

    public static ListEntity entity4() {
        ListEntity entity4 = new ListEntity();
        List<ShowEntity> showEntities = new ArrayList<>();
        ShowEntity showEntity = new ShowEntity();
        showEntity.name = "刘德华";
        showEntity.url = "http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
        showEntities.add(showEntity);
        ShowEntity showEntity1 = new ShowEntity();
        showEntity1.name = "刘德华";
        showEntity1.url = "http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
        showEntities.add(showEntity1);
        entity4.object = showEntities;
        entity4.type = 4;
        return entity4;
    }

    public static ListEntity entity5() {
        ListEntity entity5 = new ListEntity();
        List<ShowEntity> showEntities = new ArrayList<>();
        ShowEntity showEntity = new ShowEntity();
        showEntity.name = "刘德华";
        showEntity.type = "历史";
        showEntity.url = "http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
        showEntities.add(showEntity);
        ShowEntity showEntity1 = new ShowEntity();
        showEntity1.name = "刘德华";
        showEntity1.type = "谍战";
        showEntity1.url = "http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
        showEntities.add(showEntity1);
        entity5.object = showEntities;
        entity5.type = 5;
        return entity5;
    }

    public static ListEntity entity8() {
        ListEntity entity = new ListEntity();
        entity.type = 8;
        entity.object = "http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
        return entity;
    }

    public static ListEntity entity9() {
        ListEntity entity = new ListEntity();
        entity.title = "随便看看";
        entity.type = 9;
        entity.topPading = 0;
        List<ShowEntity> entities = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            ShowEntity entity1 = new ShowEntity();
            entity1.url = "http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
            entity1.name = "CCTV-3综艺";
            entity1.type = "综艺喜乐汇";
            entities.add(entity1);
        }
        entity.object = entities;
        return entity;
    }

    public static ListEntity entity10() {
        ListEntity entity = new ListEntity();
        entity.type = 10;
        List<ShowEntity> entities = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ShowEntity entity1 = new ShowEntity();
            entity1.url = "http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
            entity1.name = "CCTV-3综艺";
            entity1.type = "综艺喜乐汇";
            entities.add(entity1);
        }
        entity.object = entities;
        return entity;
    }


    public static ListEntity entity11() {
        ListEntity entity = new ListEntity();
        entity.type = 11;
        entity.select = false;
        entity.title = "湖南";
        List<ListEntity> entities = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entities.add(entity12());
        }
        entity.object = entities;
        return entity;
    }

    public static ListEntity entity12() {
        ListEntity listEntity = new ListEntity();
        listEntity.type = 12;
        ShowEntity showEntity = new ShowEntity();
        showEntity.url = "http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
        showEntity.name = "湖南娱乐";
        listEntity.object = showEntity;
        return listEntity;
    }

    public static ListEntity entity13() {
        ListEntity entity = new ListEntity();
        entity.type = 13;
        ShowEntity showEntity = new ShowEntity();
        showEntity.name = "刘德华";
        showEntity.type = "历史";
        showEntity.url = "http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
        entity.object = showEntity;
        return entity;
    }

    public static ListEntity entity14() {
        ListEntity entity = new ListEntity();
        entity.type = 14;
        ShowEntity showEntity = new ShowEntity();
        showEntity.name = "刘德华";
        showEntity.type = "历史";
        showEntity.time = "02:46";
        showEntity.url = "http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
        entity.object = showEntity;
        return entity;
    }

    public static ListEntity entity15() {
        ListEntity entity = new ListEntity();
        entity.type = 15;
        List<ListEntity> entities = new ArrayList<>();
        for (int i = 0; i < 12; i++)
            entities.add(entity16());
        entities.get(5).select = true;
        entity.object = entities;
        entity.title = "08月29日";
        return entity;
    }

    public static ListEntity entity16() {
        ListEntity entity = new ListEntity();
        ShowEntity showEntity = new ShowEntity();
        entity.type = 16;
        showEntity.name = "九州缥缈录-大结局";
        showEntity.type = "回看";
        showEntity.time = "19:40";
        entity.object = showEntity;
        return entity;
    }
    public static ListEntity entity18(){
        ListEntity entity = new ListEntity();
        ShowEntity showEntity = new ShowEntity();
        entity.type = 18;
        showEntity.url="http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg";
        showEntity.name = "上传者用户名";
        showEntity.type = "第四届中国蒙古舞大赛优 秀节目展演在呼和浩特...";
        entity.object = showEntity;
        return entity;
    }
}
