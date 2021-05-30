package com.indilist.photoreceipt.ui.camera;

import org.json.JSONObject;

public class PresetFilter {
    private String filterName;
    private JSONObject filter;
    private int index;
    public PresetFilter(String name, JSONObject obj, int index){
        this.filterName = name;
        this.filter = obj;
        this.index = index;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterName() {
        return filterName;
    }

    public JSONObject getFilter(){
        return filter;
    }

    public int getIndex(){
        return index;
    }


}
