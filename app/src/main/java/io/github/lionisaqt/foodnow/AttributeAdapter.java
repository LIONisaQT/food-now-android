package io.github.lionisaqt.foodnow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablecheckrecyclerview.CheckableChildRecyclerViewAdapter;
import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class AttributeAdapter extends CheckableChildRecyclerViewAdapter<AttributesListViewHolder, AttributeViewHolder> {
    public AttributeAdapter(List<? extends CheckedExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public AttributeViewHolder onCreateCheckChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_attribute, parent, false);
        return new AttributeViewHolder(v);
    }

    @Override
    public void onBindCheckChildViewHolder(AttributeViewHolder holder, int flatPosition, CheckedExpandableGroup group, int childIndex) {
        final Attribute attribute = (Attribute)group.getItems().get(childIndex);
        holder.bind(attribute);
    }

    @Override
    public AttributesListViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_attributes_list, parent, false);
        return new AttributesListViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(AttributesListViewHolder holder, int flatPosition, ExpandableGroup group) {
        final AttributesList attributesList = (AttributesList)group;
        holder.bind(attributesList);
    }
}
