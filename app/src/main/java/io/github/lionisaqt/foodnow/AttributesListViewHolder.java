package io.github.lionisaqt.foodnow;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public class AttributesListViewHolder extends GroupViewHolder {
    private TextView attributesList;

    public AttributesListViewHolder(View itemView) {
        super(itemView);
        attributesList = itemView.findViewById(R.id.attributes_list);
    }

    public void bind(AttributesList attributesList) {
        this.attributesList.setText(attributesList.getTitle());
    }
}
