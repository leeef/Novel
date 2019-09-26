package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.ui.adapter.view.LocalFileHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalFileAdapter extends BaseListAdapter<File> {
    //记录item是否被选中的Map
    private HashMap<File, Boolean> mCheckMap = new HashMap<>();

    @Override
    protected IViewHolder<File> createViewHolder(int viewType) {
        return new LocalFileHolder(mCheckMap);
    }


    @Override
    public void addItems(List<File> values) {
        for (File file : values) {
            mCheckMap.put(file, false);
        }
        super.addItems(values);
    }

    //设置点击切换
    public void setCheckedItem(int pos) {
        File file = getItem(pos);

        boolean isSelected = mCheckMap.get(file);
        if (isSelected) {
            mCheckMap.put(file, false);
        } else {
            mCheckMap.put(file, true);
        }
        notifyDataSetChanged();
    }

    public List<File> getCheckedFiles() {
        List<File> files = new ArrayList<>();
        Set<Map.Entry<File, Boolean>> entrys = mCheckMap.entrySet();
        for (Map.Entry<File, Boolean> entry : entrys) {
            if (entry.getValue()) {
                files.add(entry.getKey());
            }
        }
        return files;
    }
}
