package com.lendingapp.neighborly;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kishan on 4/18/17.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, Item> _listDataChild;

    public Boolean getViewOnly() {
        return viewOnly;
    }

    public void setViewOnly(Boolean viewOnly) {
        this.viewOnly = viewOnly;
    }

    private Boolean viewOnly;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, Item> listDataChild) {
        this._context = context;
        this._listDataChild = listDataChild;
        this._listDataHeader = listDataHeader;
        viewOnly = false;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Item child = (Item) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChildDesc = (TextView) convertView.findViewById(R.id.description);
        txtListChildDesc.setText(child.getDescription());

        TextView txtListChildActive = (TextView) convertView.findViewById(R.id.active);
        txtListChildActive.setText(child.isActive() ? "YES" : "NO");
        txtListChildActive.setTextColor(child.isActive() ? Color.GREEN : Color.RED);

        TextView activeTitle = (TextView) convertView.findViewById(R.id.textView8);

        Button editItem = (Button) convertView.findViewById(R.id.editItem);
        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, EditItemActivity.class);
                intent.putExtra("description", child.getDescription());
                intent.putExtra("name", child.getName());
                intent.putExtra("active", child.isActive());
                intent.putExtra("id", child.getId());
                _context.startActivity(intent);
            }
        });

        if (viewOnly){
            editItem.setVisibility(View.GONE);
            txtListChildActive.setVisibility(View.GONE);
            activeTitle.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String header = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(header);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
}
