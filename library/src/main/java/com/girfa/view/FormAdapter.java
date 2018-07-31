package com.girfa.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.girfa.file.Document;
import com.girfa.BuildConfig;
import com.girfa.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Afrig Aminuddin on 28/03/2017.
 */

public class FormAdapter extends DataAdapter implements AdapterView.OnItemClickListener {
    private static final String NAME = FormAdapter.class.getName();
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "hh:mm";
    private final Factory factory;
    private final List<Element> elements = new ArrayList<>();
    private Listener listener;
    private final float scale;

    public FormAdapter(Context context, Document document) {
        super(context, document);
        this.factory = new Factory();
        this.scale = context.getResources().getDisplayMetrics().density;
    }

    public FormAdapter(Context context, Document document, Listener listener) {
        this(context, document);
        this.listener = listener;
    }

    public Factory getFactory() {
        return factory;
    }

    public class Factory {
        public Document getDocument() {
            return document;
        }

        public Factory addBoolean(String key, int titleRes, int hintRes) {
            return addBoolean(key, context.getString(titleRes), context.getString(hintRes));
        }

        public Factory addBoolean(String key, String title, String hint) {
            Boolean def = document == null ? null : document.getBoolean(key);
            elements.add(new Element(Type.BOOLEAN, key, def, title, hint));
            return this;
        }

        public Factory addDate(String key, int titleRes, int hintRes, int formatRes) {
            return addDate(key, context.getString(titleRes), context.getString(hintRes), context.getString(formatRes));
        }

        public Factory addDate(String key, String title, String hint, String format) {
            Date def = null;
            try {
                def = document == null ? null : new SimpleDateFormat(DATE_FORMAT, Locale.US)
                        .parse(document.getString(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Element el = new Element(Type.DATE, key, def, title, hint);
            el.format = format;
            elements.add(el);
            return this;
        }

        public Factory addGroup(int titleRes) {
            return addGroup(context.getString(titleRes));
        }

        public Factory addGroup(String title) {
            elements.add(new Element(Type.GROUP, null, null, title, null));
            return this;
        }

        public Factory addMulti(String key, Map<String, Object> list, int titleRes, int hintRes) {
            return addMulti(key, list, context.getString(titleRes), context.getString(hintRes));
        }

        public Factory addMulti(String key, Map<String, Object> list, String title, String hint) {
            List<Pair<String, Object>> listNew = null;
            if (list != null) {
                listNew = new ArrayList<>();
                for (String k : list.keySet()) {
                    listNew.add(new Pair<String, Object>(k, list.get(k)));
                }
            }
            return addMulti(key, listNew, title, hint);
        }

        public Factory addMulti(String key, Document list, int titleRes, int hintRes) {
            return addMulti(key, list, context.getString(titleRes), context.getString(hintRes));
        }

        public Factory addMulti(String key, Document list, String title, String hint) {
            List<Pair<String, Object>> listNew = null;
            if (list != null) {
                listNew = new ArrayList<>();
                for (String k : list.keys()) {
                    listNew.add(new Pair<String, Object>(k, list.get(k)));
                }
            }
            return addMulti(key,  listNew, title, hint);
        }

        public Factory addMulti(String key, List<Pair<String, Object>> list, int titleRes, int hintRes) {
            return addMulti(key, list, context.getString(titleRes), context.getString(hintRes));
        }

        public Factory addMulti(String key, List<Pair<String, Object>> list, String title, String hint) {
            Document def = document == null ? null : document.getDocument(key);
            List<String> defNew = null;
            if (def != null) {
                defNew = new ArrayList<>();
                for (String k : def.keys()) {
                    defNew.add(def.getString(k));
                }
            }
            Element el = new Element(Type.MULTI, key, null, title, hint);
            el.values = defNew;
            el.list = list;
            elements.add(el);
            return this;
        }

        public Factory addSingle(String key, Map<String, Object> list, int titleRes, int hintRes) {
            return addSingle(key, list, context.getString(titleRes), context.getString(hintRes));
        }

        public Factory addSingle(String key, Map<String, Object> list, String title, String hint) {
            List<Pair<String, Object>> listNew = null;
            if (list != null) {
                listNew = new ArrayList<>();
                for (String k : list.keySet()) {
                    listNew.add(new Pair<String, Object>(k, list.get(k)));
                }
            }
            return addSingle(key, listNew, title, hint);
        }

        public Factory addSingle(String key, Document list, int titleRes, int hintRes) {
            return addSingle(key, list, context.getString(titleRes), context.getString(hintRes));
        }

        public Factory addSingle(String key, Document list, String title, String hint) {
            List<Pair<String, Object>> listNew = null;
            if (list != null) {
                listNew = new ArrayList<>();
                for (String k : list.keys()) {
                    listNew.add(new Pair<String, Object>(k, list.get(k)));
                }
            }
            return addSingle(key, listNew, title, hint);
        }

        public Factory addSingle(String key, List<Pair<String, Object>> list, int titleRes, int hintRes) {
            return addSingle(key, list, context.getString(titleRes), context.getString(hintRes));
        }

        public Factory addSingle(String key, List<Pair<String, Object>> list, String title, String hint) {
            String def = document == null ? null : document.getString(key);
            Element el = new Element(Type.SINGLE, key, def, title, hint);
            el.list = list;
            elements.add(el);
            return this;
        }

        public Factory addSlider(String key, int titleRes, Number min, Number max, int step, int iconRes) {
            Drawable icon = null;
            if (Build.VERSION.SDK_INT < 21) icon = context.getResources().getDrawable(iconRes);
            else icon = context.getDrawable(iconRes);
            return addSlider(key, context.getString(titleRes), min, max, step, icon);
        }

        public Factory addSlider(String key, String title, Number min, Number max, int step, Drawable icon) {
            if (step < 1) step = 1000;
            if (min == null) min = 0;
            if (max == null) max = 100;
            Number def = document == null ? null : document.getDouble(key);
            Element el = new Element(Type.SLIDER, key, def, title, null);
            el.sliderMin = min;
            el.sliderMax = max;
            el.sliderStep = step;
            el.sliderIcon = icon;
            elements.add(el);
            return this;
        }

        public Factory addText(String key, int titleRes, int hintRes, int inputType) {
            return addText(key, context.getString(titleRes), context.getString(hintRes), inputType);
        }

        public Factory addText(String key, String title, String hint, int inputType) {
            String def = document == null ? null : document.getString(key);
            Element el = new Element(Type.TEXT, key, def, title, hint);
            el.inputType = inputType;
            elements.add(el);
            return this;
        }

        public Factory addTime(String key, int titleRes, int hintRes, String format) {
            return addTime(key, context.getString(titleRes), context.getString(hintRes), format);
        }

        public Factory addTime(String key, String title, String hint, String format) {
            Date def = null;
            try {
                def = document == null ? null : new SimpleDateFormat(TIME_FORMAT, Locale.US)
                        .parse(document.getString(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Element el = new Element(Type.TIME, key, def, title, hint);
            el.format = format;
            elements.add(el);
            return this;
        }

        public Factory addView(String key, int titleRes, int hintRes) {
            return addView(key, context.getString(titleRes), context.getString(hintRes));
        }

        public Factory addView(String key, String title, String hint) {
            String def = document == null ? null : document.getString(key);
            elements.add(new Element(Type.VIEW, key, def, title, hint));
            return this;
        }

        private Element get(String key) {
            for (Element el : elements) {
                if (key.equals(el.key)) return el;
            }
            return null;
        }

        private Factory set(String key, Object value) {
            Element el = get(key);
            if (el == null && BuildConfig.DEBUG) Log.d(NAME, key + " is not exist");
            else {
                el.setValue(document, value);
            }
            return this;
        }

        public Factory setBoolean(String key, Boolean value) {
            return set(key, value);
        }

        public Factory setDate(String key, Date value) {
            return set(key, value);
        }

        public Factory setMultiList(String key, List<Pair<String, Object>> list) {
            Element el = get(key);
            if (el == null && BuildConfig.DEBUG) Log.d(NAME, key + " is not exist");
            else el.list = list;
            return this;
        }

        public Factory setMulti(String key, List<String> values) {
            Element el = get(key);
            if (el == null && BuildConfig.DEBUG) Log.d(NAME, key + " is not exist");
            else {
                el.setValues(document, values);
            }
            return this;
        }

        public Factory setSingleList(String key, List<Pair<String, Object>> list) {
            Element el = get(key);
            if (el == null && BuildConfig.DEBUG) Log.d(NAME, key + " is not exist");
            else el.list = list;
            return this;
        }

        public Factory setSingle(String key, String value) {
            return set(key, value);
        }

        public Factory setSlider(String key, int value) {
            return set(key, value);
        }

        public Factory setText(String key, String value) {
            return set(key, value);
        }

        public Factory setTime(String key, Date value) {
            return set(key, value);
        }

        public Factory setView(String key, String value) {
            return set(key, value);
        }

        public Factory remove(String key) {
            Element el = get(key);
            if (el != null) elements.remove(el);
            return this;
        }

        public void commit() {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getSize() {
        return elements.size();
    }

    @Override
    public boolean isItemEnabled(int position) {
        return elements.get(position).type != Type.GROUP;
    }

    @Override
    public View getView(int i, View view) {
        final Element el = elements.get(i);
        TextView title = new TextView(context);
        title.setText(el.title == null ? el.key : el.title);
        title.setTextSize(16);
        TextView hint = null;
        title.setTextColor(color(R.attr.editTextColor));
        if (el.hint != null) {
            hint = new TextView(context);
            hint.setText(el.hint);
            hint.setTextSize(14);
        }
        switch (el.type) {
            case BOOLEAN:
                view = viewBoolean(el, title, hint);
                break;
            case GROUP:
                view = viewGroup(el);
                break;
            case SLIDER:
                view = viewSlider(el, title);
                break;
            default:
                view = viewDefault(el, title, hint);
        }
        return view;
    }

    private int color(int attrId) {
        TypedArray themeArray = context.getTheme().obtainStyledAttributes(new int[] {attrId});
        int color = themeArray.getColor(0, 0);
        themeArray.recycle();
        return color;
    }

    private RelativeLayout.LayoutParams relativeLayout(int width, int height) {
        if (width > 0) width = (int) (width * scale + 0.5f);
        if (height > 0) height = (int) (height * scale + 0.5f);
        return new RelativeLayout.LayoutParams(width, height);
    }

    private LinearLayout linearLayout(int left, int top, int right, int bottom) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding((int) (left * scale + 0.5f), (int) (top * scale + 0.5f),
                (int) (right * scale + 0.5f), (int) (bottom * scale + 0.5f));
        return layout;
    }

    private View viewBoolean(final Element el, TextView title, TextView hint) {
        RelativeLayout layout = new RelativeLayout(context);
        layout.setPadding((int) (16 * scale + 0.5f), (int) (16 * scale + 0.5f),
                (int) (16 * scale + 0.5f), (int) (16 * scale + 0.5f));
        layout.setLayoutParams(relativeLayout(-1, -2));
        SwitchCompat swtc = new SwitchCompat(context);
        swtc.setId(android.R.id.button1);
        swtc.setChecked(el.value == null? false : (Boolean) el.value);
        swtc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                el.setValue(document, b);
                if (listener != null) listener.onFormUpdate(factory, el);
            }
        });
        RelativeLayout.LayoutParams rlpSwtc = relativeLayout(-2, -1);
        rlpSwtc .addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlpSwtc.addRule(RelativeLayout.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= 17) rlpSwtc.addRule(RelativeLayout.ALIGN_PARENT_END);
        layout.addView(swtc, rlpSwtc);
        LinearLayout linear = linearLayout(0, 0, 0, 0);
        RelativeLayout.LayoutParams rlpLinear = relativeLayout(-1, -1);
        rlpLinear.addRule(RelativeLayout.LEFT_OF, swtc.getId());
        rlpLinear.addRule(RelativeLayout.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= 17) rlpLinear.addRule(RelativeLayout.ALIGN_PARENT_END);
        linear.addView(title);
        if (hint != null) linear.addView(hint);
        layout.addView(linear, rlpLinear);
        return layout;
    }

    private View viewGroup(Element el) {
        LinearLayout layout = linearLayout(16, 16, 16, 0);
        TextView group = new TextView(context);
        group.setTextColor(color(R.attr.colorPrimary));
        group.setTextSize(14);
        group.setTypeface(null, Typeface.BOLD);
        group.setText(el.title);
        layout.addView(group);
        return layout;
    }

    private View viewSlider(final Element el, TextView title) {
        final LinearLayout layout = linearLayout(16, 16, 16, 16);
        layout.addView(title);
        RelativeLayout content = new RelativeLayout(context);
        layout.addView(content);
        final ImageView icon = new ImageView(context);
        icon.setId(android.R.id.button1);
        if (el.sliderIcon != null) {
            icon.setImageDrawable(el.sliderIcon);
        }
        RelativeLayout.LayoutParams rlpIcon = relativeLayout(-2, 32);
        rlpIcon.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlpIcon.addRule(RelativeLayout.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= 17) rlpIcon.addRule(RelativeLayout.ALIGN_PARENT_START);
        content.addView(icon, rlpIcon);
        final TextView value = new TextView(context);
        value.setLines(1);
        value.setTextSize(14);
        final LinearLayout wrap = new LinearLayout(context);
        wrap.setId(android.R.id.button2);
        wrap.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams rlpValue = relativeLayout(-2, -1);
        rlpValue.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlpValue.addRule(RelativeLayout.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= 17) rlpValue.addRule(RelativeLayout.ALIGN_PARENT_END);
        wrap.addView(value);
        content.addView(wrap, rlpValue);
        final SeekBar slider = new SeekBar(context);
        slider.setMax(el.sliderStep);
        final double min = el.sliderMin.doubleValue();
        final double max = el.sliderMax.doubleValue();
        final boolean fraction = el.sliderMin instanceof Float || el.sliderMin instanceof Double
                || el.sliderMax instanceof Float || el.sliderMax instanceof Double;
        if (el.value != null) {
            slider.setProgress((int) ((((Number)el.value).doubleValue() - min)
                    / (max - min) * slider.getMax()));
        }
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int length;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                double val = (double) i / el.sliderStep * (max - min) + min;
                el.setValue(document, fraction ? val : (int) val);
                value.setText(fraction ? new DecimalFormat("###.###").format(val)
                        : String.valueOf((int) val));
                if (length < value.getWidth()) length = value.getWidth();
                wrap.setMinimumWidth(length);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (listener != null) listener.onFormUpdate(factory, el);
            }
        });
        RelativeLayout.LayoutParams rlpSlider = new RelativeLayout.LayoutParams(-2, -1);
        rlpSlider.addRule(RelativeLayout.CENTER_VERTICAL);
        rlpSlider.addRule(RelativeLayout.RIGHT_OF, icon.getId());
        rlpSlider.addRule(RelativeLayout.LEFT_OF, wrap.getId());
        if (el.value != null) {
            value.setText(fraction ? new DecimalFormat("###.###").format(el.value)
                    : String.valueOf((int)(double)el.value));
        }
        content.addView(slider, rlpSlider);
        return layout;
    }

    private View viewDefault(Element el, TextView title, TextView hint) {
        LinearLayout layout = linearLayout(16, 16, 16, 16);
        layout.addView(title);
        String text = el.toString();
        if (text != null && text.length() > 0) {
            TextView value = new TextView(context);
            value.setText(text);
            value.setTextSize(14);
            value.setTypeface(null, Typeface.BOLD);
            layout.addView(value);
        } else if (hint != null) {
            layout.addView(hint);
        }
        return layout;
    }

    @Override
    public void onItemClick(int position, View view) {
        final Element el = elements.get(position);
        if (el.type == Type.VIEW || el.type == Type.SLIDER || el.type == Type.BOOLEAN) {
            if (listener != null && el.type == Type.VIEW) listener.onFormUpdate(factory, el);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(el.title == null ? el.key : el.title);
            switch (el.type) {
                case DATE:
                    dialogDate(builder, el);
                    break;
                case MULTI:
                    dialogMulti(builder, el);
                    break;
                case SINGLE:
                    dialogSingle(builder, el);
                    break;
                case TEXT:
                    dialogText(builder, el);
                    break;
                case TIME:
                    dialogTime(builder, el);
                    break;
            }
            if (el.type != Type.SINGLE) {
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handlePositiveButton(el);
                    }
                });
            }
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.show();
        }
    }

    private void dialogDate(AlertDialog.Builder builder, Element el) {
        LinearLayout layout = linearLayout(0, 8, 0, 0);
        el.tmpDate = new DatePicker(context);
        if (el.value != null) {
            Calendar temp = Calendar.getInstance();
            temp.setTime((Date) el.value);
            el.tmpDate.init(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DATE), null);
        }
        layout.addView(el.tmpDate);
        builder.setView(layout);
    }

    private void dialogMulti(AlertDialog.Builder builder, final Element el) {
        if (el.list == null || el.list.size() == 0) return;
        if (el.values == null) el.values = new ArrayList<String>();
        String[] data = new String[el.list.size()];
        boolean[] check = new boolean[el.list.size()];
        for (int i = 0; i < data.length; i++) {
            Pair<String, Object> pair = el.list.get(i);
            data[i] = String.valueOf(pair.second);
            check[i] = el.values.contains(pair.first);
        }
        builder.setMultiChoiceItems(data, check, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                String first = el.list.get(i).first;
                if (b) el.values.add(first);
                else el.values.remove(first);
            }
        });
    }

    private void dialogSingle(AlertDialog.Builder builder, final Element el) {
        if (el.list == null || el.list.size() == 0) return;
        String[] data = new String[el.list.size()];
        int check = -1;
        for (int i = 0; i < data.length; i++) {
            Pair<String, Object> pair = el.list.get(i);
            data[i] = String.valueOf(pair.second);
            if (pair.first.equals(el.value)) check = i;
        }
        builder.setSingleChoiceItems(data, check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                el.setValue(document, el.list.get(i).first);
                if (listener != null) listener.onFormUpdate(factory, el);
                dialogInterface.cancel();
                notifyDataSetChanged();
            }
        });
    }

    private void dialogText(AlertDialog.Builder builder, final Element el) {
        LinearLayout layout = linearLayout(16, 10, 16, 10);
        el.tmpText = new EditText(context);
        el.tmpText.setInputType(el.inputType);
        el.tmpText.setSingleLine(false);
        el.tmpText.post(new Runnable() {
            @Override
            public void run() {
                if (el.value != null) {
                    el.tmpText.setText("");
                    el.tmpText.append(el.value.toString());
                }
                InputMethodManager imm = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(el.tmpText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        layout.addView(el.tmpText);
        if (el.hint != null){
            TextView hint = new TextView(context);
            hint.setTextSize(14);
            hint.setPadding((int) (4 * scale + 0.5f), 0, (int) (4 * scale + 0.5f), 0);
            hint.setText(el.hint);
            layout.addView(hint);
        }
        builder.setView(layout);
    }

    private void dialogTime(AlertDialog.Builder builder, Element el) {
        LinearLayout layout = linearLayout(0, 8, 0, 0);
        el.tmpTime = new TimePicker(context);
        if (el.value != null) {
            Calendar temp = Calendar.getInstance();
            temp.setTime((Date) el.value);
            if (Build.VERSION.SDK_INT < 25) {
                el.tmpTime.setCurrentHour(temp.get(Calendar.HOUR));
                el.tmpTime.setCurrentMinute(temp.get(Calendar.MINUTE));
            } else {
                el.tmpTime.setHour(temp.get(Calendar.HOUR));
                el.tmpTime.setMinute(temp.get(Calendar.MINUTE));
            }
        }
        el.tmpTime.setIs24HourView(true);
        layout.addView(el.tmpTime);
        builder.setView(layout);
    }

    private void handlePositiveButton(Element el) {
        switch (el.type) {
            case DATE:
                Calendar cdate = Calendar.getInstance();
                cdate.set(el.tmpDate.getYear(), el.tmpDate.getMonth(),
                        el.tmpDate.getDayOfMonth());
                el.setValue(document, cdate.getTime());
                break;
            case MULTI:
                el.setValues(document, el.values);
                break;
            case TEXT:
                el.setValue(document, el.tmpText.getText().toString());
                break;
            case TIME:
                Calendar ctime = Calendar.getInstance();
                int hour = 0;
                int minute = 0;
                if (Build.VERSION.SDK_INT < 25) {
                    hour = el.tmpTime.getCurrentHour();
                    minute = el.tmpTime.getCurrentMinute();
                } else {
                    hour = el.tmpTime.getHour();
                    minute = el.tmpTime.getMinute();
                }
                ctime.set(Calendar.HOUR_OF_DAY, hour);
                ctime.set(Calendar.MINUTE, minute);
                el.setValue(document, ctime.getTime());
                break;
        }
        if (listener != null) listener.onFormUpdate(factory, el);
        notifyDataSetChanged();
    }

    public class Element{
        private final Type type;
        private final String key;
        private Object value;
        private List<String> values = new ArrayList<>();
        private List<Pair<String, Object>> list = new ArrayList<>();
        private final String title;
        private final String hint;
        private int inputType;
        private String format;
        private Number sliderMin;
        private Number sliderMax;
        private int sliderStep;
        private Drawable sliderIcon;
        private DatePicker tmpDate;
        private EditText tmpText;
        private TimePicker tmpTime;

        private Element(Type type, String key, Object value, String title, String hint) {
            this.type = type;
            this.key = key;
            this.value = value;
            this.title = title;
            this.hint = hint;
        }

        private void setValue(Document document, Object value) {
            if (type == Type.MULTI) return;
            if (value != null && value.toString().length() == 0) value = null;
            this.value = value;
            if (document != null) {
                Object val = value;
                if (value instanceof Date) {
                    if (type == Type.DATE) val = new SimpleDateFormat(DATE_FORMAT, Locale.US).format(val);
                    else if (type == Type.TIME) val = new SimpleDateFormat(TIME_FORMAT, Locale.US).format(val);
                }
                document.set(key, val);
            }
        }

        private void setValues(Document document, List<String> values) {
            if (type != Type.MULTI || values == null
                    || list == null || list.size() == 0) return;
            List<String> temp = new ArrayList<>();
            for (Pair<String, Object> val : list) {
                if (values.contains(val.first)) {
                    temp.add(val.first);
                }
            }
            if (temp.size() == 0) temp = null;
            this.values = temp;
            document.set(key, temp);
        }

        public Type getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        public List<String> getValues() {
            return values;
        }

        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            String text = null;
            switch (type) {
                case DATE:
                case TIME:
                    if (value == null) break;
                    if (format != null && Build.VERSION.SDK_INT <= 8) {
                        format = format.replace('L', 'M');
                    }
                    DateFormat df = new SimpleDateFormat(format);
                    text = df.format(value);
                    break;
                case MULTI:
                    if (values == null) break;
                    text = "";
                    for (Pair<String, Object> val : list) {
                        if (values.contains(val.first)) text += val.second + ", ";
                    }
                    if (text.length() > 0) text = text.substring(0, text.length() - 2);
                    break;
                case SINGLE:
                    if (value == null || list == null || list.size() == 0) break;
                    for (Pair<String, Object> val : list) {
                        if (value.equals(val.first)) {
                            text = String.valueOf(val.second);
                            break;
                        }
                    }
                    break;
                case TEXT:
                case VIEW:
                    text = value == null ? null : value.toString();
                    break;
            }
            return  text;
        }
    }

    public interface Listener {
        void onFormUpdate(Factory factory, Element element);
    }
    public enum Type {
        BOOLEAN, DATE, GROUP, MULTI, SINGLE, SLIDER, TEXT, TIME, VIEW
    }
}
