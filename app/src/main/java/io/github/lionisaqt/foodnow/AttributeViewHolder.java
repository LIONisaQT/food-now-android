package io.github.lionisaqt.foodnow;

import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder;

public class AttributeViewHolder extends CheckableChildViewHolder {
    private CheckedTextView attribute;

    public AttributeViewHolder(View itemView) {
        super(itemView);
        attribute = itemView.findViewById(R.id.attribute);
    }

    @Override
    public Checkable getCheckable() {
        return attribute;
    }

    public void bind(Attribute attribute) {
        this.attribute.setText(attribute.name);
    }
}
