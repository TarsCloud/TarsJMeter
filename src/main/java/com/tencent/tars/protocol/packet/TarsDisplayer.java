//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tencent.tars.protocol.packet;

import com.tencent.tars.protocol.tars.TarsStructBase;
import com.tencent.tars.protocol.exceptions.TarsEncodeException;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class TarsDisplayer {
    private StringBuilder sb;
    private int _level = 0;

    private void ps(String fieldName) {
        for(int i = 0; i < this._level; ++i) {
            this.sb.append('\t');
        }

        if (fieldName != null) {
            this.sb.append(fieldName).append(": ");
        }

    }

    public TarsDisplayer(StringBuilder sb, int level) {
        this.sb = sb;
        this._level = level;
    }

    public TarsDisplayer(StringBuilder sb) {
        this.sb = sb;
    }

    public TarsDisplayer display(boolean b, String fieldName) {
        this.ps(fieldName);
        this.sb.append((char)(b ? 'T' : 'F')).append('\n');
        return this;
    }

    public TarsDisplayer display(byte n, String fieldName) {
        this.ps(fieldName);
        this.sb.append(n).append('\n');
        return this;
    }

    public TarsDisplayer display(char n, String fieldName) {
        this.ps(fieldName);
        this.sb.append(n).append('\n');
        return this;
    }

    public TarsDisplayer display(short n, String fieldName) {
        this.ps(fieldName);
        this.sb.append(n).append('\n');
        return this;
    }

    public TarsDisplayer display(int n, String fieldName) {
        this.ps(fieldName);
        this.sb.append(n).append('\n');
        return this;
    }

    public TarsDisplayer display(long n, String fieldName) {
        this.ps(fieldName);
        this.sb.append(n).append('\n');
        return this;
    }

    public TarsDisplayer display(float n, String fieldName) {
        this.ps(fieldName);
        this.sb.append(n).append('\n');
        return this;
    }

    public TarsDisplayer display(double n, String fieldName) {
        this.ps(fieldName);
        this.sb.append(n).append('\n');
        return this;
    }

    public TarsDisplayer display(String s, String fieldName) {
        this.ps(fieldName);
        if (null == s) {
            this.sb.append("null").append('\n');
        } else {
            this.sb.append(s).append('\n');
        }

        return this;
    }

    public TarsDisplayer display(byte[] v, String fieldName) {
        this.ps(fieldName);
        if (null == v) {
            this.sb.append("null").append('\n');
            return this;
        } else if (v.length == 0) {
            this.sb.append(v.length).append(", []").append('\n');
            return this;
        } else {
            this.sb.append(v.length).append(", [").append('\n');
            TarsDisplayer jd = new TarsDisplayer(this.sb, this._level + 1);
            byte[] var4 = v;
            int var5 = v.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                byte o = var4[var6];
                jd.display((byte)o, (String)null);
            }

            this.display((char)']', (String)null);
            return this;
        }
    }

    public TarsDisplayer display(char[] v, String fieldName) {
        this.ps(fieldName);
        if (null == v) {
            this.sb.append("null").append('\n');
            return this;
        } else if (v.length == 0) {
            this.sb.append(v.length).append(", []").append('\n');
            return this;
        } else {
            this.sb.append(v.length).append(", [").append('\n');
            TarsDisplayer jd = new TarsDisplayer(this.sb, this._level + 1);
            char[] var4 = v;
            int var5 = v.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                char o = var4[var6];
                jd.display((char)o, (String)null);
            }

            this.display((char)']', (String)null);
            return this;
        }
    }

    public TarsDisplayer display(short[] v, String fieldName) {
        this.ps(fieldName);
        if (null == v) {
            this.sb.append("null").append('\n');
            return this;
        } else if (v.length == 0) {
            this.sb.append(v.length).append(", []").append('\n');
            return this;
        } else {
            this.sb.append(v.length).append(", [").append('\n');
            TarsDisplayer jd = new TarsDisplayer(this.sb, this._level + 1);
            short[] var4 = v;
            int var5 = v.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                short o = var4[var6];
                jd.display((short)o, (String)null);
            }

            this.display((char)']', (String)null);
            return this;
        }
    }

    public TarsDisplayer display(int[] v, String fieldName) {
        this.ps(fieldName);
        if (null == v) {
            this.sb.append("null").append('\n');
            return this;
        } else if (v.length == 0) {
            this.sb.append(v.length).append(", []").append('\n');
            return this;
        } else {
            this.sb.append(v.length).append(", [").append('\n');
            TarsDisplayer jd = new TarsDisplayer(this.sb, this._level + 1);
            int[] var4 = v;
            int var5 = v.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                int o = var4[var6];
                jd.display((int)o, (String)null);
            }

            this.display((char)']', (String)null);
            return this;
        }
    }

    public TarsDisplayer display(long[] v, String fieldName) {
        this.ps(fieldName);
        if (null == v) {
            this.sb.append("null").append('\n');
            return this;
        } else if (v.length == 0) {
            this.sb.append(v.length).append(", []").append('\n');
            return this;
        } else {
            this.sb.append(v.length).append(", [").append('\n');
            TarsDisplayer jd = new TarsDisplayer(this.sb, this._level + 1);
            long[] var4 = v;
            int var5 = v.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                long o = var4[var6];
                jd.display(o, (String)null);
            }

            this.display((char)']', (String)null);
            return this;
        }
    }

    public TarsDisplayer display(float[] v, String fieldName) {
        this.ps(fieldName);
        if (null == v) {
            this.sb.append("null").append('\n');
            return this;
        } else if (v.length == 0) {
            this.sb.append(v.length).append(", []").append('\n');
            return this;
        } else {
            this.sb.append(v.length).append(", [").append('\n');
            TarsDisplayer jd = new TarsDisplayer(this.sb, this._level + 1);
            float[] var4 = v;
            int var5 = v.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                float o = var4[var6];
                jd.display(o, (String)null);
            }

            this.display((char)']', (String)null);
            return this;
        }
    }

    public TarsDisplayer display(double[] v, String fieldName) {
        this.ps(fieldName);
        if (null == v) {
            this.sb.append("null").append('\n');
            return this;
        } else if (v.length == 0) {
            this.sb.append(v.length).append(", []").append('\n');
            return this;
        } else {
            this.sb.append(v.length).append(", [").append('\n');
            TarsDisplayer jd = new TarsDisplayer(this.sb, this._level + 1);
            double[] var4 = v;
            int var5 = v.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                double o = var4[var6];
                jd.display(o, (String)null);
            }

            this.display((char)']', (String)null);
            return this;
        }
    }

    public <K, V> TarsDisplayer display(Map<K, V> m, String fieldName) {
        this.ps(fieldName);
        if (null == m) {
            this.sb.append("null").append('\n');
            return this;
        } else if (m.isEmpty()) {
            this.sb.append(m.size()).append(", {}").append('\n');
            return this;
        } else {
            this.sb.append(m.size()).append(", {").append('\n');
            TarsDisplayer jd1 = new TarsDisplayer(this.sb, this._level + 1);
            TarsDisplayer jd = new TarsDisplayer(this.sb, this._level + 2);
            Iterator var5 = m.entrySet().iterator();

            while(var5.hasNext()) {
                Entry<K, V> en = (Entry)var5.next();
                jd1.display((char)'(', (String)null);
                jd.display((Object)en.getKey(), (String)null);
                jd.display((Object)en.getValue(), (String)null);
                jd1.display((char)')', (String)null);
            }

            this.display((char)'}', (String)null);
            return this;
        }
    }

    public <T> TarsDisplayer display(T[] v, String fieldName) {
        this.ps(fieldName);
        if (null == v) {
            this.sb.append("null").append('\n');
            return this;
        } else if (v.length == 0) {
            this.sb.append(v.length).append(", []").append('\n');
            return this;
        } else {
            this.sb.append(v.length).append(", [").append('\n');
            TarsDisplayer jd = new TarsDisplayer(this.sb, this._level + 1);
            Object[] var4 = v;
            int var5 = v.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                T o = (T) var4[var6];
                jd.display((Object)o, (String)null);
            }

            this.display((char)']', (String)null);
            return this;
        }
    }

    public <T> TarsDisplayer display(Collection<T> v, String fieldName) {
        if (null == v) {
            this.ps(fieldName);
            this.sb.append("null").append('\t');
            return this;
        } else {
            return this.display(v.toArray(), fieldName);
        }
    }

    public <T> TarsDisplayer display(T o, String fieldName) {
        if (null == o) {
            this.sb.append("null").append('\n');
        } else if (o instanceof Byte) {
            this.display((Byte)o, fieldName);
        } else if (o instanceof Boolean) {
            this.display((Boolean)o, fieldName);
        } else if (o instanceof Short) {
            this.display((Short)o, fieldName);
        } else if (o instanceof Integer) {
            this.display((Integer)o, fieldName);
        } else if (o instanceof Long) {
            this.display((Long)o, fieldName);
        } else if (o instanceof Float) {
            this.display((Float)o, fieldName);
        } else if (o instanceof Double) {
            this.display((Double)o, fieldName);
        } else if (o instanceof String) {
            this.display((String)o, fieldName);
        } else if (o instanceof Map) {
            this.display((Map)o, fieldName);
        } else if (o instanceof List) {
            this.display((Collection)((List)o), fieldName);
        } else if (o instanceof TarsStructBase) {
            this.display((TarsStructBase)o, fieldName);
        } else if (o instanceof byte[]) {
            this.display((byte[])((byte[])o), fieldName);
        } else if (o instanceof boolean[]) {
            this.display((Object)((boolean[])((boolean[])o)), fieldName);
        } else if (o instanceof short[]) {
            this.display((short[])((short[])o), fieldName);
        } else if (o instanceof int[]) {
            this.display((int[])((int[])o), fieldName);
        } else if (o instanceof long[]) {
            this.display((long[])((long[])o), fieldName);
        } else if (o instanceof float[]) {
            this.display((float[])((float[])o), fieldName);
        } else if (o instanceof double[]) {
            this.display((double[])((double[])o), fieldName);
        } else {
            if (!o.getClass().isArray()) {
                throw new TarsEncodeException("write object error: unsupport type.");
            }

            this.display((Object[])((Object[])o), fieldName);
        }

        return this;
    }

    public TarsDisplayer display(TarsStructBase v, String fieldName) {
        this.display('{', fieldName);
        if (null == v) {
            this.sb.append('\t').append("null");
        } else {
            v.display(this.sb, this._level + 1);
        }

        this.display((char)'}', (String)null);
        return this;
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(1.2D);
        System.out.println(sb.toString());
    }
}
