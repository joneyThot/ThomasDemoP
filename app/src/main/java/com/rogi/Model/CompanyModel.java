package com.rogi.Model;

import java.io.Serializable;

/**
 * Created by "Mayuri" on 21/7/17.
 */
public class CompanyModel implements Serializable {

    String id, name, title, pos, titleAppend;
    boolean selected = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getTitleAppend() {
        return titleAppend;
    }

    public void setTitleAppend(String titleAppend) {
        this.titleAppend = titleAppend;
    }
}
