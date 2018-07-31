package com.girfa.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.girfa.file.Document;

/**
 * Created by Afrig Aminuddin on 07/04/2017.
 */

public class DataAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    protected Context context;
    private LayoutInflater inflater;
    private DataAdapter header;
    protected Document document;
    private DataAdapter footer;
    private boolean useHeader;
    private boolean useFooter;
    private ViewGroup parent;

    public DataAdapter(Context context, Document document) {
        this.context = context;
        this.document = document;
        this.inflater = LayoutInflater.from(context);
    }

    public void setHeader(DataAdapter header) {
        this.header = header;
        this.useHeader = header != null;
    }

    public void setFooter(DataAdapter footer) {
        this.footer = footer;
        this.useFooter = footer != null;
    }

    @Override
    public final int getCount() {
        return (useHeader ? header.getCount() : 0) + getSize() + (useFooter ? footer.getCount() : 0);
    }

    protected int getSize() {
        return document.size();
    }

    @Override
    public final Object getItem(int position) {
        return position;
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        this.parent = parent;
        int headerCount = useHeader ? header.getCount() : 0;
        int bodyCount = getSize();
        if (position < headerCount) return header.getView(position, convertView);
        else if (position < headerCount + bodyCount) return getView(position - headerCount, convertView);
        else if (useFooter) return footer.getView(position - headerCount - bodyCount, convertView);
        else return new View(context);
    }

    protected View getView(int position, View view) {
        Object object = document.get(String.valueOf(position));
        view = createView(view);
        if (object instanceof Document) view = getView((Document) object, view);
        else view = getView(object, view);
        return view;
    }

    protected View createView(View view) {
        return view;
    }

    protected View getView(Document document, View view) {
        return view;
    }

    protected View getView(Object object, View view) {
        return view;
    }

    protected final <T extends View> T findView(View view, int id) {
        Object child = view.getTag(id);
        if (child instanceof View) return (T) child;
        else return view.findViewById(id);
    }

    protected final View inflate(int resource) {
        return inflater.inflate(resource, parent, false);
    }

    protected final View inflate(int resource, int... usedIds) {
        View view = inflate(resource);
        for (int usedId : usedIds) {
            view.setTag(usedId, view.findViewById(usedId));
        }
        return view;
    }

    @Override
    public final boolean isEnabled(int position) {
        int headerCount = useHeader ? header.getCount() : 0;
        int bodyCount = getSize();
        if (position < headerCount) return header.isItemEnabled(position);
        else if (position < headerCount + bodyCount) return isItemEnabled(position - headerCount);
        else return !useFooter || footer.isItemEnabled(position - headerCount - bodyCount);
    }

    protected boolean isItemEnabled(int position) {
        return true;
    }

    @Override
    public final View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public final int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= ((ListView)parent).getHeaderViewsCount();
        int headerCount = useHeader ? header.getCount() : 0;
        int bodyCount = getSize();
        if (position < 0);
        else if (useHeader && position < headerCount) header.onItemClick(position, view);
        else if (position < headerCount + bodyCount) onItemClick(position - headerCount, view);
        else if (useFooter) footer.onItemClick(position - headerCount - bodyCount, view);
    }

    protected void onItemClick(int position, View view) {
        Object object = document.get(document.isArray()
                ? String.valueOf(position) : document.keys().get(position));
        if (object instanceof Document) onItemClick((Document) object, view);
        else onItemClick(object, view);
    }

    protected void onItemClick(Document document, View view) {}

    protected void onItemClick(Object object, View view) {}
}
