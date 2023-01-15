package by.alis.functionalservercontrol.spigot.libraries.org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CharSet implements Serializable {
    private static final long serialVersionUID = 5947847346149275958L;
    public static final CharSet EMPTY = new CharSet((String)null);
    public static final CharSet ASCII_ALPHA = new CharSet("a-zA-Z");
    public static final CharSet ASCII_ALPHA_LOWER = new CharSet("a-z");
    public static final CharSet ASCII_ALPHA_UPPER = new CharSet("A-Z");
    public static final CharSet ASCII_NUMERIC = new CharSet("0-9");
    protected static final Map COMMON = Collections.synchronizedMap(new HashMap());
    private final Set set = Collections.synchronizedSet(new HashSet());

    public static CharSet getInstance(String setStr) {
        Object set = COMMON.get(setStr);
        return set != null ? (CharSet)set : new CharSet(setStr);
    }

    public static CharSet getInstance(String[] setStrs) {
        return setStrs == null ? null : new CharSet(setStrs);
    }

    protected CharSet(String setStr) {
        this.add(setStr);
    }

    protected CharSet(String[] set) {
        int sz = set.length;

        for(int i = 0; i < sz; ++i) {
            this.add(set[i]);
        }

    }

    protected void add(String str) {
        if (str != null) {
            int len = str.length();
            int pos = 0;

            while(true) {
                while(pos < len) {
                    int remainder = len - pos;
                    if (remainder >= 4 && str.charAt(pos) == '^' && str.charAt(pos + 2) == '-') {
                        this.set.add(CharRange.isNotIn(str.charAt(pos + 1), str.charAt(pos + 3)));
                        pos += 4;
                    } else if (remainder >= 3 && str.charAt(pos + 1) == '-') {
                        this.set.add(CharRange.isIn(str.charAt(pos), str.charAt(pos + 2)));
                        pos += 3;
                    } else if (remainder >= 2 && str.charAt(pos) == '^') {
                        this.set.add(CharRange.isNot(str.charAt(pos + 1)));
                        pos += 2;
                    } else {
                        this.set.add(CharRange.is(str.charAt(pos)));
                        ++pos;
                    }
                }

                return;
            }
        }
    }

    public CharRange[] getCharRanges() {
        return (CharRange[])(this.set.toArray(new CharRange[this.set.size()]));
    }

    public boolean contains(char ch) {
        Iterator it = this.set.iterator();

        CharRange range;
        do {
            if (!it.hasNext()) {
                return false;
            }

            range = (CharRange)it.next();
        } while(!range.contains(ch));

        return true;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof CharSet)) {
            return false;
        } else {
            CharSet other = (CharSet) obj;
            return this.set.equals(other.set);
        }
    }

    public int hashCode() {
        return 89 + this.set.hashCode();
    }

    public String toString() {
        return this.set.toString();
    }

    static {
        COMMON.put((Object)null, EMPTY);
        COMMON.put("", EMPTY);
        COMMON.put("a-zA-Z", ASCII_ALPHA);
        COMMON.put("A-Za-z", ASCII_ALPHA);
        COMMON.put("a-z", ASCII_ALPHA_LOWER);
        COMMON.put("A-Z", ASCII_ALPHA_UPPER);
        COMMON.put("0-9", ASCII_NUMERIC);
    }
}
