package io.github.lionisaqt.foodnow;

import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup;

import java.util.List;

public class AttributesList extends MultiCheckExpandableGroup {
    public AttributesList(String title, List<Attribute> items) {
        super(title, items);
    }
}
