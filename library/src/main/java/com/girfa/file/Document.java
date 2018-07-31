/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girfa.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Afrig Aminuddin
 */
public class Document {

    private final static String SPARATOR = ".";
    private final File file;
    private final String prefix;
    private Data data;


    public Document() {
        this.file = null;
        this.prefix = "";
    }

    public Document(String prefix) {
        this.file = null;
        this.prefix = check(prefix);
        this.data = setLoop(this.data, prefix, new ObjectData());
    }

    public Document(File file) {
        this.file = file;
        this.prefix = "";
        if (file != null) read(file);
    }

    public Document(File file, String prefix) {
        this.file = file;
        this.prefix = check(prefix);
        this.data = setLoop(this.data, prefix, new ObjectData());
        if (file != null) read(file);
    }

    private Document(Document document, String prefix) {
        this.file = document.file;
        this.prefix = prefix;
        this.data = document.data;
    }

    public Document write(File file) {
        try {
            file.getParentFile().mkdirs();
            return write(new FileOutputStream(file));
        } catch (Exception ex) {
            return this;
        }
    }

    public Document write(OutputStream stream) {
        if (data != null) {
            data.encode(new Encoder(stream, 0));
        }
        return this;
    }

    public Document read(File file) {
        try {
            return read(new FileInputStream(file));
        } catch (Exception ex) {
            return this;
        }
    }

    public Document read(InputStream stream) {
        return readIntl(new Decoder(stream));
    }

    public Document read(String source) {
        if (source == null) source = "";
        return readIntl(new Decoder(source));
    }

    public Document copyTo(Document document) {
        if (document != null) {
            for (String key : keys()) {
                document.set(key, get(key));
            }
        }
        return this;
    }

    public Document copyFrom(Document document) {
        if (document != null) {
            for (String key : document.keys()) {
                set(key, document.get(key));
            }
        }
        return this;
    }

    public Document set(String key, Object value) {
        if (value instanceof Document) {
            Document doc = (Document) value;
            value = doc.getLoop(doc.data, doc.prefix);
        }
        data = setLoop(data, pre(key), value);
        save();
        return this;
    }

    public Document set(String key, Document value) {
        return set(key, (Object) value);
    }

    public Document set(String key, boolean value) {
        return set(key, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public Document set(String key, int value) {
        return set(key, Integer.valueOf(value));
    }

    public Document set(String key, long value) {
        return set(key, Long.valueOf(value));
    }

    public Document set(String key, float value) {
        return set(key, Float.valueOf(value));
    }

    public Document set(String key, double value) {
        return set(key, Double.valueOf(value));
    }

    public Document set(String key, String value) {
        return set(key, (Object) value);
    }

    public Document set(int index, Object value) {
        return set(String.valueOf(index), value);
    }

    public Document set(int index, Document value) {
        return set(String.valueOf(index), value);
    }

    public Document set(int index, boolean value) {
        return set(String.valueOf(index), value);
    }

    public Document set(int index, int value) {
        return set(String.valueOf(index), value);
    }

    public Document set(int index, long value) {
        return set(String.valueOf(index), value);
    }

    public Document set(int index, float value) {
        return set(String.valueOf(index), value);
    }

    public Document set(int index, double value) {
        return set(String.valueOf(index), value);
    }

    public Document set(int index, String value) {
        return set(String.valueOf(index), value);
    }

    public Document add(Object value) {
        if (value instanceof Document) {
            Document doc = (Document) value;
            value = doc.getLoop(doc.data, doc.prefix);
        }
        Data child = (Data) getLoop(data, prefix);
        if (child == null) {
            child = new ArrayData();
            if (prefix.length() == 0) data = child;
            else data = setLoop(data, prefix, child);
        }
        child.add(value);
        save();
        return this;
    }

    public Document add(Document value) {
        return add((Object) value);
    }

    public Document add(boolean value) {
        return add(value ? Boolean.TRUE : Boolean.FALSE);
    }

    public Document add(int value) {
        return add(Integer.valueOf(value));
    }

    public Document add(long value) {
        return add(Long.valueOf(value));
    }

    public Document add(float value) {
        return add(Float.valueOf(value));
    }

    public Document add(double value) {
        return add(Double.valueOf(value));
    }

    public Document add(String value) {
        return add((Object) value);
    }

    public Object get(String key) {
        Object object = getLoop(data, pre(key));
        if (object instanceof Data) return getDocument(key);
        else return object;
    }

    public Document getDocument(String key) {
        return new Document(this, pre(key));
    }

    public boolean getBoolean(String key) {
        Object object = get(key);
        return (object != null && object.equals(Boolean.TRUE))
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("true"));
    }

    public int getInt(String key) {
        Object object = get(key);
        try {
            return object instanceof Number ? ((Number) object).intValue()
                    : Integer.parseInt((String) object);
        } catch (NullPointerException | NumberFormatException ignore) {
            return 0;
        }
    }

    public long getLong(String key) {
        Object object = get(key);
        try {
            return object instanceof Number ? ((Number) object).longValue()
                    : Long.parseLong((String) object);
        } catch (NullPointerException | NumberFormatException ignore) {
            return 0L;
        }
    }

    public float getFloat(String key) {
        Object object = get(key);
        try {
            return object instanceof Number ? ((Number) object).floatValue()
                    : Float.parseFloat((String) object);
        } catch (NullPointerException | NumberFormatException ignore) {
            return 0f;
        }
    }

    public double getDouble(String key) {
        Object object = get(key);
        try {
            return object instanceof Number ? ((Number) object).doubleValue()
                    : Double.parseDouble((String) object);
        } catch (NullPointerException | NumberFormatException ignore) {
            return 0d;
        }
    }

    public String getString(String key) {
        Object object = get(key);
        try {
            return object.toString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Object get(int index) {
        return get(String.valueOf(index));
    }

    public Document getDocument(int index) {
        return getDocument(String.valueOf(index));
    }

    public boolean getBoolean(int index) {
        return getBoolean(String.valueOf(index));
    }

    public int getInt(int index) {
        return getInt(String.valueOf(index));
    }

    public long getLong(int index) {
        return getLong(String.valueOf(index));
    }

    public float getFloat(int index) {
        return getFloat(String.valueOf(index));
    }

    public double getDouble(int index) {
        return getDouble(String.valueOf(index));
    }

    public String getString(int index) {
        return getString(String.valueOf(index));
    }

    public Document remove(String key) {
        removeLoop(data, pre(key));
        save();
        return this;
    }

    public Document remove(int index) {
        return remove(String.valueOf(index));
    }

    public Document remove(String[] keys) {
        for (String key : keys) {
            removeLoop(data, pre(key));
        }
        save();
        return this;
    }

    public void clear() {
        Data child = (Data) getLoop(data, prefix);
        if (child != null) {
            child.clear();
        }
        save();
    }

    public boolean delete() {
        if (data != null) {
            data.clear();
        }
        save();
        return file != null && file.delete();
    }

    public boolean has(String key) {
        return hasLoop(data, pre(key));
    }

    public boolean has(String[] keys) {
        for (String key : keys) {
            if (!has(key)) return false;
        }
        return true;
    }

    public boolean has(int index) {
        return has(String.valueOf(index));
    }

    public boolean contains(Object value) {
        Data child = (Data) getLoop(data, prefix);
        return child != null && child.contains(value);
    }

    public int size() {
        Data child = (Data) getLoop(data, prefix);
        if (child != null) {
            return child.size();
        }
        return 0;
    }

    public List<String> keys() {
        Data child = (Data) getLoop(data, prefix);
        if (child != null) {
            return child.keys();
        }
        return new ArrayList<>();
    }

    public boolean isArray() {
        return data instanceof ArrayData;
    }

    public boolean isObject() {
        return data instanceof ObjectData;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        if (data != null) {
            Object child = getLoop(data, prefix);
            if (child instanceof Data) {
                Encoder enc = new Encoder(indent);
                if (((Data)child).encode(enc)) {
                    return enc.toString();
                }
            } else if (child != null) {
                return child.toString();
            }
        }
        return null;
    }

    private Document save() {
        if (file != null) write(file);
        return this;
    }

    private Document readIntl(Decoder decoder) {
        ObjectData object = new ObjectData();
        ArrayData array = new ArrayData();
        if (object.decode(decoder)) {
            if (data == null) {
                data = object;
            } else {
                data.clear();
                for (String key : object.keys()) {
                    data.set(key, object.get(key));
                }
            }
        } else if (array.decode(decoder)) {
            if (data == null) {
                data = array;
            } else {
                data.clear();
                for (int i = 0; i < array.size(); i++) {
                    try {
                        data.add(array.get(i));
                    } catch (IndexOutOfBoundsException ignore) {}
                }
            }
        }
        save();
        return this;
    }

    private String check(String key) {
        if (key == null || key.length() == 0 || key.equals(SPARATOR)
                || key.startsWith(SPARATOR) || key.endsWith(SPARATOR)) {
            throw new DocumentException("Invalid key \"" + key + "\"");
        }
        return key;
    }

    private String pre(String key) {
        return (prefix.length() == 0 ? "" : prefix + SPARATOR) + check(key);
    }

    private Data setLoop(Data data, String key, Object value) {
        key = check(key);
        int index = key.indexOf(SPARATOR);
        if (index > -1) {
            String left = key.substring(0, index);
            if (data == null) {
                data = initData(left);
            }
            String right = key.substring(index + SPARATOR.length());
            Data child = (Data) data.get(left);
            if (child == null) {
                int i = right.indexOf(SPARATOR);
                child = initData(i > -1 ? right.substring(0, i) : right);
                data.set(left, child);
            }
            setLoop(child, right, value);
        } else {
            if (data == null) {
                data = initData(key);
            }
            if (value instanceof Collection<?>) {
                if (((Collection<?>) value).isEmpty()) {
                    data.set(key, value);
                } else {
                    int n = 0;
                    for (Object o : ((Collection) value)) {
                        setLoop(data, key + SPARATOR + (n++), o);
                    }
                }
            } else if (value instanceof Map<?, ?>) {
                if (((Map<?, ?>) value).isEmpty()) {
                    data.set(key, value);
                } else {
                    for (Object o : ((Map) value).entrySet()) {
                        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                        setLoop(data, key + SPARATOR + entry.getKey(), entry.getValue());
                    }
                }
            } else {
                data.set(key, value);
            }
        }
        return data;
    }

    private Object getLoop(Data data, String key) {
        if (data == null) {
            return null;
        }
        if (key == null || key.length() == 0) {
            return data;
        }
        int index = key.indexOf(SPARATOR);
        if (index > -1) {
            Object obj = data.get(key.substring(0, index));
            if (obj instanceof Data) {
                return getLoop((Data) obj, key.substring(index + SPARATOR.length()));
            }
        } else {
            return data.get(key);
        }
        return null;
    }

    private Document removeLoop(Data data, String key) {
        if (data == null) {
            return this;
        }
        int index = key.indexOf(SPARATOR);
        if (index > -1) {
            Object obj = data.get(key.substring(0, index));
            if (obj instanceof Data) {
                return removeLoop((Data) obj, key.substring(index + SPARATOR.length()));
            }
        } else {
            data.remove(key);
        }
        return this;
    }

    private boolean hasLoop(Data data, String key) {
        if (data == null) {
            return false;
        }
        int index = key.indexOf(SPARATOR);
        if (index > -1) {
            Object obj = data.get(key.substring(0, index));
            if (obj instanceof Data) {
                return hasLoop((Data) obj, key.substring(index + SPARATOR.length()));
            }
        } else {
            return data.has(key);
        }
        return false;
    }

    private static boolean isInt(String str) {
        try {
            return Integer.parseInt(str) >= 0;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private static Data initData(String key) {
        return isInt(key) ? new ArrayData() : new ObjectData();
    }

    private interface Data {

        boolean encode(Encoder encoder);

        boolean decode(Decoder decoder);

        void set(String key, Object value) throws DocumentException;

        boolean add(Object value) throws DocumentException;

        Object get(String key) throws DocumentException;

        void remove(String key) throws DocumentException;

        void clear() throws DocumentException;

        boolean has(String key) throws DocumentException;

        boolean contains(Object value);

        int size();

        List<String> keys();
    }

    private static class ObjectData extends LinkedHashMap<String, Object> implements Data {

        @Override
        public boolean encode(Encoder encoder) {
            return encoder.writeObject(this);
        }

        @Override
        public boolean decode(Decoder decoder) {
            return decoder.readObject(this);
        }

        @Override
        public void set(String key, Object value) throws DocumentException {
            if (isInt(key)) {
                throw new DocumentException("Cannot set \""
                        + key + "\" to " + ObjectData.class.getSimpleName());
            }
            if (value == null) {
                super.remove(key);
            } else {
                super.put(key, value);
            }
        }

        @Override
        public boolean add(Object value) throws DocumentException {
            throw new DocumentException("Cannot add getDocument to "
                    + ObjectData.class.getSimpleName());
        }

        @Override
        public Object get(String key) throws DocumentException {
            if (isInt(key)) {
                throw new DocumentException("Cannot get \""
                        + key + "\" from " + ObjectData.class.getSimpleName());
            }
            return super.get(key);
        }

        @Override
        public void remove(String key) throws DocumentException {
            if (isInt(key)) {
                throw new DocumentException("Cannot remove \""
                        + key + "\" from " + ObjectData.class.getSimpleName());
            }
            super.remove(key);
        }

        @Override
        public boolean has(String key) throws DocumentException {
            if (isInt(key)) {
                throw new DocumentException("Cannot has \""
                        + key + "\" from " + ObjectData.class.getSimpleName());
            }
            return super.containsKey(key);
        }

        @Override
        public boolean contains(Object value) {
            return super.containsValue(value);
        }

        @Override
        public List<String> keys() {
            List<String> keys = new ArrayList<>();
            for (String key : super.keySet()) {
                keys.add(key);
            }
            return keys;
        }

        @Override
        public String toString() {
            return ObjectData.class.getSimpleName() + "(" + size() + ")";
        }

    }

    private static class ArrayData extends ArrayList<Object> implements Data {

        @Override
        public boolean encode(Encoder encoder) {
            return encoder.writeArray(this);
        }

        @Override
        public boolean decode(Decoder decoder) {
            return decoder.readArray(this);
        }

        @Override
        public void set(String key, Object value) throws DocumentException {
            if (!isInt(key)) {
                throw new DocumentException("Cannot set \""
                        + key + "\" to " + ArrayData.class.getSimpleName());
            }
            int index = Integer.valueOf(key);
            try {
                super.set(Integer.valueOf(key), value);
            } catch (IndexOutOfBoundsException ignore) {
                for (int i = size(); i < index; i++) {
                    super.add(null);
                }
                super.add(value);
            }
        }

        @Override
        public Object get(String key) throws DocumentException {
            if (!isInt(key)) {
                throw new DocumentException("Cannot get \""
                        + key + "\" from " + ArrayData.class.getSimpleName());
            }
            try {
                return super.get(Integer.valueOf(key));
            } catch (IndexOutOfBoundsException ignore) {
                return null;
            }
        }

        @Override
        public void remove(String key) throws DocumentException {
            if (!isInt(key)) {
                throw new DocumentException("Cannot remove \""
                        + key + "\" from " + ArrayData.class.getSimpleName());
            }
            super.remove((int) Integer.valueOf(key));
        }

        @Override
        public boolean has(String key) throws DocumentException {
            if (!isInt(key)) {
                throw new DocumentException("Cannot has \""
                        + key + "\" from " + ArrayData.class.getSimpleName());
            }
            int index = Integer.valueOf(key);
            return index >= 0 && index < super.size();
        }

        @Override
        public List<String> keys() {
            List<String> keys = new ArrayList<>();
            int size = super.size();
            for (int i = 0; i < size; i++) {
                keys.add(String.valueOf(i));
            }
            return keys;
        }

        @Override
        public String toString() {
            return ArrayData.class.getSimpleName() + "(" + size() + ")";
        }

    }

    public static class DocumentException extends RuntimeException {

        DocumentException(String message) {
            super(message);
        }

        DocumentException(String message, Throwable thr) {
            super(message, thr);
        }
    }

    private static class Encoder {

        private final Writer writer;
        private int indent;

        Encoder(int indent) {
            this.writer = new StringWriter();
            this.indent = indent;
        }

        Encoder(OutputStream out, int indent) {
            this.writer = new PrintWriter(out);
            this.indent = indent;
        }



        @Override
        public String toString() {
            if (writer instanceof StringWriter) {
                return writer.toString();
            }
            return null;
        }

        boolean writeObject(ObjectData data) {
            try {
                objectLoop(data, 0);
                if (writer instanceof PrintWriter) {
                    writer.flush();
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        boolean writeArray(ArrayData data) {
            try {
                arrayLoop(data, 0);
                if (writer instanceof PrintWriter) {
                    writer.flush();
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        private void objectLoop(ObjectData data, int loopIndent) throws IOException {
            boolean comma = false;
            final int size = data.size();
            Iterator<String> keys = data.keys().iterator();
            writer.write('{');
            if (size > 0) {
                final int newindent = loopIndent + indent;
                while (keys.hasNext()) {
                    Object key = keys.next();
                    if (comma) {
                        writer.write(',');
                    }
                    if (indent > 0) {
                        writer.write('\n');
                    }
                    indent(newindent);
                    quote(key.toString());
                    writer.write(':');
                    if (indent > 0) {
                        writer.write(' ');
                    }
                    valueLoop(data.get(key.toString()), newindent);
                    comma = true;
                }
                if (indent > 0) {
                    writer.write('\n');
                }
                indent(loopIndent);
            }
            writer.write('}');
        }

        private void arrayLoop(ArrayData data, int loopIndent) throws IOException {
            boolean comma = false;
            int size = data.size();
            writer.write('[');
            if (size > 0) {
                final int newindent = loopIndent + indent;
                for (int i = 0; i < size; i++) {
                    if (comma) {
                        writer.write(',');
                    }
                    if (indent > 0) {
                        writer.write('\n');
                    }
                    indent(newindent);
                    valueLoop(data.get(i), newindent);
                    comma = true;
                }
                if (indent > 0) {
                    writer.write('\n');
                }
                indent(loopIndent);
            }
            writer.write(']');
        }

        private void valueLoop(Object value, int loopIndent) throws IOException {
            if (value == null) {
                writer.write("null");
            } else if (value instanceof ObjectData) {
                objectLoop((ObjectData) value, loopIndent);
            } else if (value instanceof ArrayData) {
                arrayLoop((ArrayData) value, loopIndent);
            } else if (value instanceof Number) {
                writer.write(number((Number) value));
            } else if (value instanceof Boolean) {
                writer.write(value.toString());
            } else {
                quote(value.toString());
            }
        }

        private void indent(int count) throws IOException {
            for (int i = 0; i < count; i++) {
                writer.write(' ');
            }
        }

        private void quote(String string) throws IOException {
            if (string == null || string.length() == 0) {
                writer.write("\"\"");
                return;
            }
            char b;
            char c = 0;
            String hhhh;
            int len = string.length();
            writer.write('"');
            for (int i = 0; i < len; i++) {
                b = c;
                c = string.charAt(i);
                switch (c) {
                    case '\\':
                    case '"':
                        writer.write('\\');
                        writer.write(c);
                        break;
                    case '/':
                        if (b == '<') {
                            writer.write('\\');
                        }
                        writer.write(c);
                        break;
                    case '\b':
                        writer.write("\\b");
                        break;
                    case '\t':
                        writer.write("\\t");
                        break;
                    case '\n':
                        writer.write("\\n");
                        break;
                    case '\f':
                        writer.write("\\f");
                        break;
                    case '\r':
                        writer.write("\\r");
                        break;
                    default:
                        if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                                || (c >= '\u2000' && c < '\u2100')) {
                            writer.write("\\u");
                            hhhh = Integer.toHexString(c);
                            writer.write("0000", 0, 4 - hhhh.length());
                            writer.write(hhhh);
                        } else {
                            writer.write(c);
                        }
                }
            }
            writer.write('"');
        }

        private String number(Number number) {
            if (number == null) {
                throw new DocumentException("Null pointer");
            } else if (number instanceof Double && (((Double) number).isInfinite()
                    || ((Double) number).isNaN())) {
                throw new DocumentException("Document does not allow non-finite numbers.");
            } else if (number instanceof Float && (((Float) number).isInfinite()
                    || ((Float) number).isNaN())) {
                throw new DocumentException("Document does not allow non-finite numbers.");
            }
            String string = number.toString();
            if (string.indexOf('.') > 0 && string.indexOf('e') < 0
                    && string.indexOf('E') < 0) {
                while (string.endsWith("0")) {
                    string = string.substring(0, string.length() - 1);
                }
                if (string.endsWith(".")) {
                    string = string.substring(0, string.length() - 1);
                }
            }
            return string;
        }
    }

    private static class Decoder {

        private long character;
        private boolean eof;
        private long index;
        private long line;
        private char previous;
        private Reader reader;
        private boolean usePrevious;

        Decoder(Reader reader) {
            this.reader = reader;
            this.eof = false;
            this.usePrevious = false;
            this.previous = 0;
            this.index = 0;
            this.character = 1;
            this.line = 1;
        }

        Decoder(InputStream inputStream) throws DocumentException {
            this(new InputStreamReader(inputStream));
        }

        Decoder(String s) {
            this(new StringReader(s));
        }

        boolean readObject(ObjectData data) {
            char next = nextClean();
            if (next == '[' || end()) {
                back();
                return false;
            } else if (next != '{') {
                throw syntaxError("A ObjectData text must begin with '{'");
            }
            String key;
            for (;;) {
                switch (nextClean()) {
                    case 0:
                        throw syntaxError("A ObjectData text must end with '}'");
                    case '}':
                        return true;
                    default:
                        back();
                        key = nextValue().toString();
                }
                if (nextClean() != ':') {
                    throw syntaxError("Expected a ':' after a key");
                }
                data.set(key, nextValue());
                switch (nextClean()) {
                    case ';':
                    case ',':
                        if (nextClean() == '}') {
                            return true;
                        }
                        back();
                        break;
                    case '}':
                        return true;
                    default:
                        throw syntaxError("Expected a ',' or '}'");
                }
            }
        }

        boolean readArray(ArrayData data) {
            char next = nextClean();
            if (next == '{' || end()) {
                back();
                return false;
            } else if (next != '[') {
                throw syntaxError("A ArrayData text must begin with '['");
            }
            if (nextClean() != ']') {
                back();
                for (;;) {
                    if (nextClean() == ',') {
                        back();
                        data.add(null);
                    } else {
                        back();
                        data.add(nextValue());
                    }
                    switch (nextClean()) {
                        case ',':
                            if (nextClean() == ']') {
                                return true;
                            }
                            back();
                            break;
                        case ']':
                            return true;
                        default:
                            throw syntaxError("Expected a ',' or ']'");
                    }
                }
            }
            return true;
        }

        private void back() throws DocumentException {
            if (usePrevious || index <= 0) {
                throw new DocumentException("Stepping back two steps is not supported");
            }
            index -= 1;
            character -= 1;
            usePrevious = true;
            eof = false;
        }

        private boolean end() {
            return eof && !usePrevious;
        }

        private char next() throws DocumentException {
            int c;
            if (usePrevious) {
                usePrevious = false;
                c = previous;
            } else {
                try {
                    c = reader.read();
                } catch (IOException exception) {
                    throw new DocumentException("next error", exception);
                }
                if (c <= 0) {
                    eof = true;
                    c = 0;
                }
            }
            index += 1;
            if (previous == '\r') {
                line += 1;
                character = c == '\n' ? 0 : 1;
            } else if (c == '\n') {
                line += 1;
                character = 0;
            } else {
                character += 1;
            }
            previous = (char) c;
            return previous;
        }

        private String next(int n) throws DocumentException {
            if (n == 0) {
                return "";
            }
            char[] chars = new char[n];
            int pos = 0;
            while (pos < n) {
                chars[pos] = next();
                if (end()) {
                    throw syntaxError("Substring bounds error");
                }
                pos += 1;
            }
            return new String(chars);
        }

        private char nextClean() throws DocumentException {
            for (;;) {
                char c = next();
                if (c == 0 || c > ' ') {
                    return c;
                }
            }
        }

        private String nextString(char quote) throws DocumentException {
            char c;
            StringBuilder sb = new StringBuilder();
            for (;;) {
                c = next();
                switch (c) {
                    case 0:
                    case '\n':
                    case '\r':
                        throw syntaxError("Unterminated string");
                    case '\\':
                        c = next();
                        switch (c) {
                            case 'b':
                                sb.append('\b');
                                break;
                            case 't':
                                sb.append('\t');
                                break;
                            case 'n':
                                sb.append('\n');
                                break;
                            case 'f':
                                sb.append('\f');
                                break;
                            case 'r':
                                sb.append('\r');
                                break;
                            case 'u':
                                sb.append((char) Integer.parseInt(next(4), 16));
                                break;
                            case '"':
                            case '\'':
                            case '\\':
                            case '/':
                                sb.append(c);
                                break;
                            default:
                                throw syntaxError("Illegal escape.");
                        }
                        break;
                    default:
                        if (c == quote) {
                            return sb.toString();
                        }
                        sb.append(c);
                }
            }
        }

        private Object nextValue() throws DocumentException {
            char c = nextClean();
            switch (c) {
                case '"':
                case '\'':
                    return nextString(c);
                case '{':
                    back();
                    ObjectData obj = new ObjectData();
                    obj.decode(this);
                    return obj;
                case '[':
                    back();
                    ArrayData arr = new ArrayData();
                    arr.decode(this);
                    return arr;
                default:
                    StringBuilder sb = new StringBuilder();
                    while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
                        sb.append(c);
                        c = next();
                    }
                    back();
                    String string = sb.toString().trim();
                    if ("".equals(string)) {
                        throw syntaxError("Missing valueLoop");
                    }
                    return stringToValue(string);
            }
        }

        private Object stringToValue(String string) {
            Double d;
            if (string.equals("")) {
                return string;
            }
            if (string.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }
            if (string.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }
            if (string.equalsIgnoreCase("null")) {
                return null;
            }
            char b = string.charAt(0);
            if ((b >= '0' && b <= '9') || b == '-') {
                try {
                    if (string.indexOf('.') > -1 || string.indexOf('e') > -1
                            || string.indexOf('E') > -1) {
                        d = Double.valueOf(string);
                        if (!d.isInfinite() && !d.isNaN()) {
                            return d;
                        }
                    } else {
                        Long myLong = Long.valueOf(string);
                        if (string.equals(myLong.toString())) {
                            if (myLong == myLong.intValue()) {
                                return myLong.intValue();
                            } else {
                                return myLong;
                            }
                        }
                    }
                } catch (NumberFormatException ignore) {
                }
            }
            return string;
        }

        private DocumentException syntaxError(String message) {
            return new DocumentException(message + toString());
        }

        @Override
        public String toString() {
            return " at " + index + " [character " + character + " line "
                    + line + "]";
        }
    }
}
