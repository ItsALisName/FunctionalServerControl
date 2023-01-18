package net.alis.functionalservercontrol.libraries.org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.alis.functionalservercontrol.libraries.org.apache.commons.lang3.text.StrBuilder;

public class StringUtils {
    public static final String EMPTY = "";

    public static final int INDEX_NOT_FOUND = -1;

    private static final int PAD_LIMIT = 8192;

    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
            return true;
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String clean(String str) {
        return (str == null) ? "" : str.trim();
    }

    public static String trim(String str) {
        return (str == null) ? null : str.trim();
    }

    public static String trimToNull(String str) {
        String ts = trim(str);
        return isEmpty(ts) ? null : ts;
    }

    public static String trimToEmpty(String str) {
        return (str == null) ? "" : str.trim();
    }

    public static String strip(String str) {
        return strip(str, null);
    }

    public static String stripToNull(String str) {
        if (str == null)
            return null;
        str = strip(str, null);
        return (str.length() == 0) ? null : str;
    }

    public static String stripToEmpty(String str) {
        return (str == null) ? "" : strip(str, null);
    }

    public static String strip(String str, String stripChars) {
        if (isEmpty(str))
            return str;
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    public static String stripStart(String str, String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
            return str;
        int start = 0;
        if (stripChars == null) {
            while (start != strLen && Character.isWhitespace(str.charAt(start)))
                start++;
        } else {
            if (stripChars.length() == 0)
                return str;
            while (start != strLen && stripChars.indexOf(str.charAt(start)) != -1)
                start++;
        }
        return str.substring(start);
    }

    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0)
            return str;
        if (stripChars == null) {
            while (end != 0 && Character.isWhitespace(str.charAt(end - 1)))
                end--;
        } else {
            if (stripChars.length() == 0)
                return str;
            while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1)
                end--;
        }
        return str.substring(0, end);
    }

    public static String[] stripAll(String[] strs) {
        return stripAll(strs, null);
    }

    public static String[] stripAll(String[] strs, String stripChars) {
        int strsLen;
        if (strs == null || (strsLen = strs.length) == 0)
            return strs;
        String[] newArr = new String[strsLen];
        for (int i = 0; i < strsLen; i++)
            newArr[i] = strip(strs[i], stripChars);
        return newArr;
    }

    public static boolean equals(String str1, String str2) {
        return (str1 == null) ? ((str2 == null)) : str1.equals(str2);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return (str1 == null) ? ((str2 == null)) : str1.equalsIgnoreCase(str2);
    }

    public static int indexOf(String str, char searchChar) {
        if (isEmpty(str))
            return -1;
        return str.indexOf(searchChar);
    }

    public static int indexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str))
            return -1;
        return str.indexOf(searchChar, startPos);
    }

    public static int indexOf(String str, String searchStr) {
        if (str == null || searchStr == null)
            return -1;
        return str.indexOf(searchStr);
    }

    public static int ordinalIndexOf(String str, String searchStr, int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, false);
    }

    private static int ordinalIndexOf(String str, String searchStr, int ordinal, boolean lastIndex) {
        if (str == null || searchStr == null || ordinal <= 0)
            return -1;
        if (searchStr.length() == 0)
            return lastIndex ? str.length() : 0;
        int found = 0;
        int index = lastIndex ? str.length() : -1;
        while (true) {
            if (lastIndex) {
                index = str.lastIndexOf(searchStr, index - 1);
            } else {
                index = str.indexOf(searchStr, index + 1);
            }
            if (index < 0)
                return index;
            found++;
            if (found >= ordinal)
                return index;
        }
    }

    public static int indexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null)
            return -1;
        if (searchStr.length() == 0 && startPos >= str.length())
            return str.length();
        return str.indexOf(searchStr, startPos);
    }

    public static int indexOfIgnoreCase(String str, String searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }

    public static int indexOfIgnoreCase(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null)
            return -1;
        if (startPos < 0)
            startPos = 0;
        int endLimit = str.length() - searchStr.length() + 1;
        if (startPos > endLimit)
            return -1;
        if (searchStr.length() == 0)
            return startPos;
        for (int i = startPos; i < endLimit; i++) {
            if (str.regionMatches(true, i, searchStr, 0, searchStr.length()))
                return i;
        }
        return -1;
    }

    public static int lastIndexOf(String str, char searchChar) {
        if (isEmpty(str))
            return -1;
        return str.lastIndexOf(searchChar);
    }

    public static int lastIndexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str))
            return -1;
        return str.lastIndexOf(searchChar, startPos);
    }

    public static int lastIndexOf(String str, String searchStr) {
        if (str == null || searchStr == null)
            return -1;
        return str.lastIndexOf(searchStr);
    }

    public static int lastOrdinalIndexOf(String str, String searchStr, int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, true);
    }

    public static int lastIndexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null)
            return -1;
        return str.lastIndexOf(searchStr, startPos);
    }

    public static int lastIndexOfIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null)
            return -1;
        return lastIndexOfIgnoreCase(str, searchStr, str.length());
    }

    public static int lastIndexOfIgnoreCase(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null)
            return -1;
        if (startPos > str.length() - searchStr.length())
            startPos = str.length() - searchStr.length();
        if (startPos < 0)
            return -1;
        if (searchStr.length() == 0)
            return startPos;
        for (int i = startPos; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, searchStr.length()))
                return i;
        }
        return -1;
    }

    public static boolean contains(String str, char searchChar) {
        if (isEmpty(str))
            return false;
        return (str.indexOf(searchChar) >= 0);
    }

    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null)
            return false;
        return (str.indexOf(searchStr) >= 0);
    }

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null)
            return false;
        int len = searchStr.length();
        int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, searchStr, 0, len))
                return true;
        }
        return false;
    }

    public static int indexOfAny(String str, char[] searchChars) {
        if (isEmpty(str) || ArrayUtils.isEmpty(searchChars))
            return -1;
        int csLen = str.length();
        int csLast = csLen - 1;
        int searchLen = searchChars.length;
        int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch)
                    if (i < csLast && j < searchLast && CharUtils.isHighSurrogate(ch)) {
                        if (searchChars[j + 1] == str.charAt(i + 1))
                            return i;
                    } else {
                        return i;
                    }
            }
        }
        return -1;
    }

    public static int indexOfAny(String str, String searchChars) {
        if (isEmpty(str) || isEmpty(searchChars))
            return -1;
        return indexOfAny(str, searchChars.toCharArray());
    }

    public static boolean containsAny(String str, char[] searchChars) {
        if (isEmpty(str) || ArrayUtils.isEmpty(searchChars))
            return false;
        int csLength = str.length();
        int searchLength = searchChars.length;
        int csLast = csLength - 1;
        int searchLast = searchLength - 1;
        for (int i = 0; i < csLength; i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < searchLength; j++) {
                if (searchChars[j] == ch)
                    if (CharUtils.isHighSurrogate(ch)) {
                        if (j == searchLast)
                            return true;
                        if (i < csLast && searchChars[j + 1] == str.charAt(i + 1))
                            return true;
                    } else {
                        return true;
                    }
            }
        }
        return false;
    }

    public static boolean containsAny(String str, String searchChars) {
        if (searchChars == null)
            return false;
        return containsAny(str, searchChars.toCharArray());
    }

    public static int indexOfAnyBut(String str, char[] searchChars) {
        if (isEmpty(str) || ArrayUtils.isEmpty(searchChars))
            return -1;
        int csLen = str.length();
        int csLast = csLen - 1;
        int searchLen = searchChars.length;
        int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            char ch = str.charAt(i);
            int j = 0;
            while (true) {
                if (j < searchLen) {
                    if (searchChars[j] == ch && (
                            i >= csLast || j >= searchLast || !CharUtils.isHighSurrogate(ch) ||
                                    searchChars[j + 1] == str.charAt(i + 1)))
                        break;
                    j++;
                    continue;
                }
                return i;
            }
        }
        return -1;
    }

    public static int indexOfAnyBut(String str, String searchChars) {
        if (isEmpty(str) || isEmpty(searchChars))
            return -1;
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);
            boolean chFound = (searchChars.indexOf(ch) >= 0);
            if (i + 1 < strLen && CharUtils.isHighSurrogate(ch)) {
                char ch2 = str.charAt(i + 1);
                if (chFound && searchChars.indexOf(ch2) < 0)
                    return i;
            } else if (!chFound) {
                return i;
            }
        }
        return -1;
    }

    public static boolean containsOnly(String str, char[] valid) {
        if (valid == null || str == null)
            return false;
        if (str.length() == 0)
            return true;
        if (valid.length == 0)
            return false;
        return (indexOfAnyBut(str, valid) == -1);
    }

    public static boolean containsOnly(String str, String validChars) {
        if (str == null || validChars == null)
            return false;
        return containsOnly(str, validChars.toCharArray());
    }

    public static boolean containsNone(String str, char[] searchChars) {
        if (str == null || searchChars == null)
            return true;
        int csLen = str.length();
        int csLast = csLen - 1;
        int searchLen = searchChars.length;
        int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch)
                    if (CharUtils.isHighSurrogate(ch)) {
                        if (j == searchLast)
                            return false;
                        if (i < csLast && searchChars[j + 1] == str.charAt(i + 1))
                            return false;
                    } else {
                        return false;
                    }
            }
        }
        return true;
    }

    public static boolean containsNone(String str, String invalidChars) {
        if (str == null || invalidChars == null)
            return true;
        return containsNone(str, invalidChars.toCharArray());
    }

    public static int indexOfAny(String str, String[] searchStrs) {
        if (str == null || searchStrs == null)
            return -1;
        int sz = searchStrs.length;
        int ret = Integer.MAX_VALUE;
        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];
            if (search != null) {
                tmp = str.indexOf(search);
                if (tmp != -1)
                    if (tmp < ret)
                        ret = tmp;
            }
        }
        return (ret == Integer.MAX_VALUE) ? -1 : ret;
    }

    public static int lastIndexOfAny(String str, String[] searchStrs) {
        if (str == null || searchStrs == null)
            return -1;
        int sz = searchStrs.length;
        int ret = -1;
        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];
            if (search != null) {
                tmp = str.lastIndexOf(search);
                if (tmp > ret)
                    ret = tmp;
            }
        }
        return ret;
    }

    public static String substring(String str, int start) {
        if (str == null)
            return null;
        if (start < 0)
            start = str.length() + start;
        if (start < 0)
            start = 0;
        if (start > str.length())
            return "";
        return str.substring(start);
    }

    public static String substring(String str, int start, int end) {
        if (str == null)
            return null;
        if (end < 0)
            end = str.length() + end;
        if (start < 0)
            start = str.length() + start;
        if (end > str.length())
            end = str.length();
        if (start > end)
            return "";
        if (start < 0)
            start = 0;
        if (end < 0)
            end = 0;
        return str.substring(start, end);
    }

    public static String left(String str, int len) {
        if (str == null)
            return null;
        if (len < 0)
            return "";
        if (str.length() <= len)
            return str;
        return str.substring(0, len);
    }

    public static String right(String str, int len) {
        if (str == null)
            return null;
        if (len < 0)
            return "";
        if (str.length() <= len)
            return str;
        return str.substring(str.length() - len);
    }

    public static String mid(String str, int pos, int len) {
        if (str == null)
            return null;
        if (len < 0 || pos > str.length())
            return "";
        if (pos < 0)
            pos = 0;
        if (str.length() <= pos + len)
            return str.substring(pos);
        return str.substring(pos, pos + len);
    }

    public static String substringBefore(String str, String separator) {
        if (isEmpty(str) || separator == null)
            return str;
        if (separator.length() == 0)
            return "";
        int pos = str.indexOf(separator);
        if (pos == -1)
            return str;
        return str.substring(0, pos);
    }

    public static String substringAfter(String str, String separator) {
        if (isEmpty(str))
            return str;
        if (separator == null)
            return "";
        int pos = str.indexOf(separator);
        if (pos == -1)
            return "";
        return str.substring(pos + separator.length());
    }

    public static String substringBeforeLast(String str, String separator) {
        if (isEmpty(str) || isEmpty(separator))
            return str;
        int pos = str.lastIndexOf(separator);
        if (pos == -1)
            return str;
        return str.substring(0, pos);
    }

    public static String substringAfterLast(String str, String separator) {
        if (isEmpty(str))
            return str;
        if (isEmpty(separator))
            return "";
        int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == str.length() - separator.length())
            return "";
        return str.substring(pos + separator.length());
    }

    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null)
            return null;
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1)
                return str.substring(start + open.length(), end);
        }
        return null;
    }

    public static String[] substringsBetween(String str, String open, String close) {
        if (str == null || isEmpty(open) || isEmpty(close))
            return null;
        int strLen = str.length();
        if (strLen == 0)
            return ArrayUtils.EMPTY_STRING_ARRAY;
        int closeLen = close.length();
        int openLen = open.length();
        List list = new ArrayList();
        int pos = 0;
        while (pos < strLen - closeLen) {
            int start = str.indexOf(open, pos);
            if (start < 0)
                break;
            start += openLen;
            int end = str.indexOf(close, start);
            if (end < 0)
                break;
            list.add(str.substring(start, end));
            pos = end + closeLen;
        }
        if (list.isEmpty())
            return null;
        return (String[]) list.<String>toArray(new String[0]);
    }

    public static String getNestedString(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    public static String getNestedString(String str, String open, String close) {
        return substringBetween(str, open, close);
    }

    public static String[] split(String str) {
        return split(str, null, -1);
    }

    public static String[] split(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    public static String[] split(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    public static String[] splitByWholeSeparator(String str, String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, false);
    }

    public static String[] splitByWholeSeparator(String str, String separator, int max) {
        return splitByWholeSeparatorWorker(str, separator, max, false);
    }

    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, true);
    }

    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator, int max) {
        return splitByWholeSeparatorWorker(str, separator, max, true);
    }

    private static String[] splitByWholeSeparatorWorker(String str, String separator, int max, boolean preserveAllTokens) {
        if (str == null)
            return null;
        int len = str.length();
        if (len == 0)
            return ArrayUtils.EMPTY_STRING_ARRAY;
        if (separator == null || "".equals(separator))
            return splitWorker(str, null, max, preserveAllTokens);
        int separatorLength = separator.length();
        ArrayList substrings = new ArrayList();
        int numberOfSubstrings = 0;
        int beg = 0;
        int end = 0;
        while (end < len) {
            end = str.indexOf(separator, beg);
            if (end > -1) {
                if (end > beg) {
                    numberOfSubstrings++;
                    if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                        continue;
                    }
                    substrings.add(str.substring(beg, end));
                    beg = end + separatorLength;
                    continue;
                }
                if (preserveAllTokens) {
                    numberOfSubstrings++;
                    if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                    } else {
                        substrings.add("");
                    }
                }
                beg = end + separatorLength;
                continue;
            }
            substrings.add(str.substring(beg));
            end = len;
        }
        return (String[]) substrings.<String>toArray(new String[0]);
    }

    public static String[] splitPreserveAllTokens(String str) {
        return splitWorker(str, null, -1, true);
    }

    public static String[] splitPreserveAllTokens(String str, char separatorChar) {
        return splitWorker(str, separatorChar, true);
    }

    private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        if (str == null)
            return null;
        int len = str.length();
        if (len == 0)
            return ArrayUtils.EMPTY_STRING_ARRAY;
        List<String> list = new ArrayList<>();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || (preserveAllTokens && lastMatch))
            list.add(str.substring(start, i));
        return (String[]) list.<String>toArray(new String[0]);
    }

    public static String[] splitPreserveAllTokens(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, true);
    }

    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        if (str == null)
            return null;
        int len = str.length();
        if (len == 0)
            return ArrayUtils.EMPTY_STRING_ARRAY;
        List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch))
            list.add(str.substring(start, i));
        return list.<String>toArray(new String[list.size()]);
    }

    public static String[] splitByCharacterType(String str) {
        return splitByCharacterType(str, false);
    }

    public static String[] splitByCharacterTypeCamelCase(String str) {
        return splitByCharacterType(str, true);
    }

    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null)
            return null;
        if (str.length() == 0)
            return ArrayUtils.EMPTY_STRING_ARRAY;
        char[] c = str.toCharArray();
        List list = new ArrayList();
        int tokenStart = 0;
        int currentType = Character.getType(c[tokenStart]);
        for (int pos = tokenStart + 1; pos < c.length; pos++) {
            int type = Character.getType(c[pos]);
            if (type != currentType) {
                if (camelCase && type == 2 && currentType == 1) {
                    int newTokenStart = pos - 1;
                    if (newTokenStart != tokenStart) {
                        list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                        tokenStart = newTokenStart;
                    }
                } else {
                    list.add(new String(c, tokenStart, pos - tokenStart));
                    tokenStart = pos;
                }
                currentType = type;
            }
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return (String[])list.<String>toArray(new String[0]);
    }

    public static String concatenate(Object[] array) {
        return join(array, (String)null);
    }

    public static String join(Object[] array) {
        return join(array, (String)null);
    }

    public static String join(Object[] array, char separator) {
        if (array == null)
            return null;
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, char separator, int startIndex, int endIndex) {
        if (array == null)
            return null;
        int bufSize = endIndex - startIndex;
        if (bufSize <= 0)
            return "";
        bufSize *= ((array[startIndex] == null) ? 16 : array[startIndex].toString().length()) + 1;
        StrBuilder buf = new StrBuilder(bufSize);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex)
                buf.append(separator);
            if (array[i] != null)
                buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(Object[] array, String separator) {
        if (array == null)
            return null;
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null)
            return null;
        if (separator == null)
            separator = "";
        int bufSize = endIndex - startIndex;
        if (bufSize <= 0)
            return "";
        bufSize *= ((array[startIndex] == null) ? 16 : array[startIndex].toString().length()) + separator.length();
        StrBuilder buf = new StrBuilder(bufSize);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex)
                buf.append(separator);
            if (array[i] != null)
                buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(Iterator iterator, char separator) {
        if (iterator == null)
            return null;
        if (!iterator.hasNext())
            return "";
        Object first = iterator.next();
        if (!iterator.hasNext())
            return ObjectUtils.toString(first);
        StrBuilder buf = new StrBuilder(256);
        if (first != null)
            buf.append(first);
        while (iterator.hasNext()) {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null)
                buf.append(obj);
        }
        return buf.toString();
    }

    public static String join(Iterator iterator, String separator) {
        if (iterator == null)
            return null;
        if (!iterator.hasNext())
            return "";
        Object first = iterator.next();
        if (!iterator.hasNext())
            return ObjectUtils.toString(first);
        StrBuilder buf = new StrBuilder(256);
        if (first != null)
            buf.append(first);
        while (iterator.hasNext()) {
            if (separator != null)
                buf.append(separator);
            Object obj = iterator.next();
            if (obj != null)
                buf.append(obj);
        }
        return buf.toString();
    }

    public static String join(Collection collection, char separator) {
        if (collection == null)
            return null;
        return join(collection.iterator(), separator);
    }

    public static String join(Collection collection, String separator) {
        if (collection == null)
            return null;
        return join(collection.iterator(), separator);
    }

    public static String deleteSpaces(String str) {
        if (str == null)
            return null;
        return CharSetUtils.delete(str, " \t\r\n\b");
    }

    public static String deleteWhitespace(String str) {
        if (isEmpty(str))
            return str;
        int sz = str.length();
        char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i)))
                chs[count++] = str.charAt(i);
        }
        if (count == sz)
            return str;
        return new String(chs, 0, count);
    }

    public static String removeStart(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove))
            return str;
        if (str.startsWith(remove))
            return str.substring(remove.length());
        return str;
    }

    public static String removeStartIgnoreCase(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove))
            return str;
        if (startsWithIgnoreCase(str, remove))
            return str.substring(remove.length());
        return str;
    }

    public static String removeEnd(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove))
            return str;
        if (str.endsWith(remove))
            return str.substring(0, str.length() - remove.length());
        return str;
    }

    public static String removeEndIgnoreCase(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove))
            return str;
        if (endsWithIgnoreCase(str, remove))
            return str.substring(0, str.length() - remove.length());
        return str;
    }

    public static String remove(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove))
            return str;
        return replace(str, remove, "", -1);
    }

    public static String remove(String str, char remove) {
        if (isEmpty(str) || str.indexOf(remove) == -1)
            return str;
        char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove)
                chars[pos++] = chars[i];
        }
        return new String(chars, 0, pos);
    }

    public static String replaceOnce(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, 1);
    }

    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    public static String replace(String text, String searchString, String replacement, int max) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0)
            return text;
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == -1)
            return text;
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = (increase < 0) ? 0 : increase;
        increase *= (max < 0) ? 16 : ((max > 64) ? 64 : max);
        StrBuilder buf = new StrBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            if (--max == 0)
                break;
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    public static String replaceEachRepeatedly(String text, String[] searchList, String[] replacementList) {
        int timeToLive = (searchList == null) ? 0 : searchList.length;
        return replaceEach(text, searchList, replacementList, true, timeToLive);
    }

    private static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {
        if (text == null || text.length() == 0 || searchList == null || searchList.length == 0 || replacementList == null || replacementList.length == 0)
            return text;
        if (timeToLive < 0)
            throw new IllegalStateException("TimeToLive of " + timeToLive + " is less than 0: " + text);
        int searchLength = searchList.length;
        int replacementLength = replacementList.length;
        if (searchLength != replacementLength)
            throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
        boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex = -1;
        for (int i = 0; i < searchLength; i++) {
            if (!noMoreMatchesForReplIndex[i] && searchList[i] != null && searchList[i].length() != 0 && replacementList[i] != null) {
                tempIndex = text.indexOf(searchList[i]);
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        if (textIndex == -1)
            return text;
        int start = 0;
        int increase = 0;
        for (int j = 0; j < searchList.length; j++) {
            if (searchList[j] != null && replacementList[j] != null) {
                int greater = replacementList[j].length() - searchList[j].length();
                if (greater > 0)
                    increase += 3 * greater;
            }
        }
        increase = Math.min(increase, text.length() / 5);
        StrBuilder buf = new StrBuilder(text.length() + increase);
        while (textIndex != -1) {
            int m;
            for (m = start; m < textIndex; m++)
                buf.append(text.charAt(m));
            buf.append(replacementList[replaceIndex]);
            start = textIndex + searchList[replaceIndex].length();
            textIndex = -1;
            replaceIndex = -1;
            tempIndex = -1;
            for (m = 0; m < searchLength; m++) {
                if (!noMoreMatchesForReplIndex[m] && searchList[m] != null && searchList[m].length() != 0 && replacementList[m] != null) {
                    tempIndex = text.indexOf(searchList[m], start);
                    if (tempIndex == -1) {
                        noMoreMatchesForReplIndex[m] = true;
                    } else if (textIndex == -1 || tempIndex < textIndex) {
                        textIndex = tempIndex;
                        replaceIndex = m;
                    }
                }
            }
        }
        int textLength = text.length();
        for (int k = start; k < textLength; k++)
            buf.append(text.charAt(k));
        String result = buf.toString();
        if (!repeat)
            return result;
        return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
    }

    public static String replaceChars(String str, char searchChar, char replaceChar) {
        if (str == null)
            return null;
        return str.replace(searchChar, replaceChar);
    }

    public static String replaceChars(String str, String searchChars, String replaceChars) {
        if (isEmpty(str) || isEmpty(searchChars))
            return str;
        if (replaceChars == null)
            replaceChars = "";
        boolean modified = false;
        int replaceCharsLength = replaceChars.length();
        int strLength = str.length();
        StrBuilder buf = new StrBuilder(strLength);
        for (int i = 0; i < strLength; i++) {
            char ch = str.charAt(i);
            int index = searchChars.indexOf(ch);
            if (index >= 0) {
                modified = true;
                if (index < replaceCharsLength)
                    buf.append(replaceChars.charAt(index));
            } else {
                buf.append(ch);
            }
        }
        if (modified)
            return buf.toString();
        return str;
    }

    public static String overlayString(String text, String overlay, int start, int end) {
        return (new StrBuilder(start + overlay.length() + text.length() - end + 1)).append(text.substring(0, start)).append(overlay).append(text.substring(end)).toString();
    }

    public static String overlay(String str, String overlay, int start, int end) {
        if (str == null)
            return null;
        if (overlay == null)
            overlay = "";
        int len = str.length();
        if (start < 0)
            start = 0;
        if (start > len)
            start = len;
        if (end < 0)
            end = 0;
        if (end > len)
            end = len;
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        return (new StrBuilder(len + start - end + overlay.length() + 1)).append(str.substring(0, start)).append(overlay).append(str.substring(end)).toString();
    }

    public static String chomp(String str) {
        if (isEmpty(str))
            return str;
        if (str.length() == 1) {
            char ch = str.charAt(0);
            if (ch == '\r' || ch == '\n')
                return "";
            return str;
        }
        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r')
                lastIdx--;
        } else if (last != '\r') {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    public static String chomp(String str, String separator) {
        if (isEmpty(str) || separator == null)
            return str;
        if (str.endsWith(separator))
            return str.substring(0, str.length() - separator.length());
        return str;
    }

    public static String chompLast(String str) {
        return chompLast(str, "\n");
    }

    public static String chompLast(String str, String sep) {
        if (str.length() == 0)
            return str;
        String sub = str.substring(str.length() - sep.length());
        if (sep.equals(sub))
            return str.substring(0, str.length() - sep.length());
        return str;
    }

    public static String getChomp(String str, String sep) {
        int idx = str.lastIndexOf(sep);
        if (idx == str.length() - sep.length())
            return sep;
        if (idx != -1)
            return str.substring(idx);
        return "";
    }

    public static String prechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx == -1)
            return str;
        return str.substring(idx + sep.length());
    }

    public static String getPrechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx == -1)
            return "";
        return str.substring(0, idx + sep.length());
    }

    public static String chop(String str) {
        if (str == null)
            return null;
        int strLen = str.length();
        if (strLen < 2)
            return "";
        int lastIdx = strLen - 1;
        String ret = str.substring(0, lastIdx);
        char last = str.charAt(lastIdx);
        if (last == '\n' &&
                ret.charAt(lastIdx - 1) == '\r')
            return ret.substring(0, lastIdx - 1);
        return ret;
    }

    public static String chopNewline(String str) {
        int lastIdx = str.length() - 1;
        if (lastIdx <= 0)
            return "";
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r')
                lastIdx--;
        } else {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    public static String escape(String str) {
        return StringEscapeUtils.escapeJava(str);
    }

    public static String repeat(String str, int repeat) {
        char ch, output1[];
        int i;
        char ch0, ch1, output2[];
        int j;
        if (str == null)
            return null;
        if (repeat <= 0)
            return "";
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0)
            return str;
        if (inputLength == 1 && repeat <= 8192)
            return padding(repeat, str.charAt(0));
        int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1:
                ch = str.charAt(0);
                output1 = new char[outputLength];
                for (i = repeat - 1; i >= 0; i--)
                    output1[i] = ch;
                return new String(output1);
            case 2:
                ch0 = str.charAt(0);
                ch1 = str.charAt(1);
                output2 = new char[outputLength];
                for (j = repeat * 2 - 2; j >= 0; j--, j--) {
                    output2[j] = ch0;
                    output2[j + 1] = ch1;
                }
                return new String(output2);
        }
        StrBuilder buf = new StrBuilder(outputLength);
        for (int k = 0; k < repeat; k++)
            buf.append(str);
        return buf.toString();
    }

    public static String repeat(String str, String separator, int repeat) {
        if (str == null || separator == null)
            return repeat(str, repeat);
        String result = repeat(str + separator, repeat);
        return removeEnd(result, separator);
    }

    private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
        if (repeat < 0)
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++)
            buf[i] = padChar;
        return new String(buf);
    }

    public static String rightPad(String str, int size) {
        return rightPad(str, size, ' ');
    }

    public static String rightPad(String str, int size, char padChar) {
        if (str == null)
            return null;
        int pads = size - str.length();
        if (pads <= 0)
            return str;
        if (pads > 8192)
            return rightPad(str, size, String.valueOf(padChar));
        return str.concat(padding(pads, padChar));
    }

    public static String rightPad(String str, int size, String padStr) {
        if (str == null)
            return null;
        if (isEmpty(padStr))
            padStr = " ";
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0)
            return str;
        if (padLen == 1 && pads <= 8192)
            return rightPad(str, size, padStr.charAt(0));
        if (pads == padLen)
            return str.concat(padStr);
        if (pads < padLen)
            return str.concat(padStr.substring(0, pads));
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; i++)
            padding[i] = padChars[i % padLen];
        return str.concat(new String(padding));
    }

    public static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    public static String leftPad(String str, int size, char padChar) {
        if (str == null)
            return null;
        int pads = size - str.length();
        if (pads <= 0)
            return str;
        if (pads > 8192)
            return leftPad(str, size, String.valueOf(padChar));
        return padding(pads, padChar).concat(str);
    }

    public static String leftPad(String str, int size, String padStr) {
        if (str == null)
            return null;
        if (isEmpty(padStr))
            padStr = " ";
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0)
            return str;
        if (padLen == 1 && pads <= 8192)
            return leftPad(str, size, padStr.charAt(0));
        if (pads == padLen)
            return padStr.concat(str);
        if (pads < padLen)
            return padStr.substring(0, pads).concat(str);
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; i++)
            padding[i] = padChars[i % padLen];
        return (new String(padding)).concat(str);
    }

    public static int length(String str) {
        return (str == null) ? 0 : str.length();
    }

    public static String center(String str, int size) {
        return center(str, size, ' ');
    }

    public static String center(String str, int size, char padChar) {
        if (str == null || size <= 0)
            return str;
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0)
            return str;
        str = leftPad(str, strLen + pads / 2, padChar);
        str = rightPad(str, size, padChar);
        return str;
    }

    public static String center(String str, int size, String padStr) {
        if (str == null || size <= 0)
            return str;
        if (isEmpty(padStr))
            padStr = " ";
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0)
            return str;
        str = leftPad(str, strLen + pads / 2, padStr);
        str = rightPad(str, size, padStr);
        return str;
    }

    public static String upperCase(String str) {
        if (str == null)
            return null;
        return str.toUpperCase();
    }

    public static String upperCase(String str, Locale locale) {
        if (str == null)
            return null;
        return str.toUpperCase(locale);
    }

    public static String lowerCase(String str) {
        if (str == null)
            return null;
        return str.toLowerCase();
    }

    public static String lowerCase(String str, Locale locale) {
        if (str == null)
            return null;
        return str.toLowerCase(locale);
    }

    public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
            return str;
        return (new StrBuilder(strLen)).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
    }

    public static String capitalise(String str) {
        return capitalize(str);
    }

    public static String uncapitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
            return str;
        return (new StrBuilder(strLen)).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
    }

    public static String uncapitalise(String str) {
        return uncapitalize(str);
    }

    public static String swapCase(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
            return str;
        StrBuilder buffer = new StrBuilder(strLen);
        char ch = Character.MIN_VALUE;
        for (int i = 0; i < strLen; i++) {
            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                ch = Character.toUpperCase(ch);
            }
            buffer.append(ch);
        }
        return buffer.toString();
    }

    public static String capitaliseAllWords(String str) {
        return WordUtils.capitalize(str);
    }

    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub))
            return 0;
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    public static boolean isAlpha(String str) {
        if (str == null)
            return false;
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(str.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isAlphaSpace(String str) {
        if (str == null)
            return false;
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(str.charAt(i)) && str.charAt(i) != ' ')
                return false;
        }
        return true;
    }

    public static boolean isAlphanumeric(String str) {
        if (str == null)
            return false;
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(str.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isAlphanumericSpace(String str) {
        if (str == null)
            return false;
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(str.charAt(i)) && str.charAt(i) != ' ')
                return false;
        }
        return true;
    }

    public static boolean isAsciiPrintable(String str) {
        if (str == null)
            return false;
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!CharUtils.isAsciiPrintable(str.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        if (str == null)
            return false;
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isNumericSpace(String str) {
        if (str == null)
            return false;
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != ' ')
                return false;
        }
        return true;
    }

    public static boolean isWhitespace(String str) {
        if (str == null)
            return false;
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isAllLowerCase(String str) {
        if (str == null || isEmpty(str))
            return false;
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLowerCase(str.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isAllUpperCase(String str) {
        if (str == null || isEmpty(str))
            return false;
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isUpperCase(str.charAt(i)))
                return false;
        }
        return true;
    }

    public static String defaultString(String str) {
        return (str == null) ? "" : str;
    }

    public static String defaultString(String str, String defaultStr) {
        return (str == null) ? defaultStr : str;
    }

    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    public static String reverse(String str) {
        if (str == null)
            return null;
        return (new StrBuilder(str)).reverse().toString();
    }

    public static String reverseDelimited(String str, char separatorChar) {
        if (str == null)
            return null;
        String[] strs = split(str, separatorChar);
        ArrayUtils.reverse((Object[])strs);
        return join((Object[])strs, separatorChar);
    }

    public static String reverseDelimitedString(String str, String separatorChars) {
        if (str == null)
            return null;
        String[] strs = split(str, separatorChars);
        ArrayUtils.reverse((Object[])strs);
        if (separatorChars == null)
            return join((Object[])strs, ' ');
        return join((Object[])strs, separatorChars);
    }

    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    public static String abbreviate(String str, int offset, int maxWidth) {
        if (str == null)
            return null;
        if (maxWidth < 4)
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        if (str.length() <= maxWidth)
            return str;
        if (offset > str.length())
            offset = str.length();
        if (str.length() - offset < maxWidth - 3)
            offset = str.length() - maxWidth - 3;
        if (offset <= 4)
            return str.substring(0, maxWidth - 3) + "...";
        if (maxWidth < 7)
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        if (offset + maxWidth - 3 < str.length())
            return "..." + abbreviate(str.substring(offset), maxWidth - 3);
        return "..." + str.substring(str.length() - maxWidth - 3);
    }

    public static String abbreviateMiddle(String str, String middle, int length) {
        if (isEmpty(str) || isEmpty(middle))
            return str;
        if (length >= str.length() || length < middle.length() + 2)
            return str;
        int targetSting = length - middle.length();
        int startOffset = targetSting / 2 + targetSting % 2;
        int endOffset = str.length() - targetSting / 2;
        StrBuilder builder = new StrBuilder(length);
        builder.append(str.substring(0, startOffset));
        builder.append(middle);
        builder.append(str.substring(endOffset));
        return builder.toString();
    }

    public static String difference(String str1, String str2) {
        if (str1 == null)
            return str2;
        if (str2 == null)
            return str1;
        int at = indexOfDifference(str1, str2);
        if (at == -1)
            return "";
        return str2.substring(at);
    }

    public static int indexOfDifference(String str1, String str2) {
        if (str1 == str2)
            return -1;
        if (str1 == null || str2 == null)
            return 0;
        int i;
        for (i = 0; i < str1.length() && i < str2.length() &&
                str1.charAt(i) == str2.charAt(i); i++);
        if (i < str2.length() || i < str1.length())
            return i;
        return -1;
    }

    public static int indexOfDifference(String[] strs) {
        if (strs == null || strs.length <= 1)
            return -1;
        boolean anyStringNull = false;
        boolean allStringsNull = true;
        int arrayLen = strs.length;
        int shortestStrLen = Integer.MAX_VALUE;
        int longestStrLen = 0;
        for (int i = 0; i < arrayLen; i++) {
            if (strs[i] == null) {
                anyStringNull = true;
                shortestStrLen = 0;
            } else {
                allStringsNull = false;
                shortestStrLen = Math.min(strs[i].length(), shortestStrLen);
                longestStrLen = Math.max(strs[i].length(), longestStrLen);
            }
        }
        if (allStringsNull || (longestStrLen == 0 && !anyStringNull))
            return -1;
        if (shortestStrLen == 0)
            return 0;
        int firstDiff = -1;
        for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
            char comparisonChar = strs[0].charAt(stringPos);
            for (int arrayPos = 1; arrayPos < arrayLen; arrayPos++) {
                if (strs[arrayPos].charAt(stringPos) != comparisonChar) {
                    firstDiff = stringPos;
                    break;
                }
            }
            if (firstDiff != -1)
                break;
        }
        if (firstDiff == -1 && shortestStrLen != longestStrLen)
            return shortestStrLen;
        return firstDiff;
    }

    public static String getCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0)
            return "";
        int smallestIndexOfDiff = indexOfDifference(strs);
        if (smallestIndexOfDiff == -1) {
            if (strs[0] == null)
                return "";
            return strs[0];
        }
        if (smallestIndexOfDiff == 0)
            return "";
        return strs[0].substring(0, smallestIndexOfDiff);
    }

    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null)
            throw new IllegalArgumentException("Strings must not be null");
        int n = s.length();
        int m = t.length();
        if (n == 0)
            return m;
        if (m == 0)
            return n;
        if (n > m) {
            String tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }
        int[] p = new int[n + 1];
        int[] d = new int[n + 1];
        int i;
        for (i = 0; i <= n; i++)
            p[i] = i;
        for (int j = 1; j <= m; j++) {
            char t_j = t.charAt(j - 1);
            d[0] = j;
            for (i = 1; i <= n; i++) {
                int cost = (s.charAt(i - 1) == t_j) ? 0 : 1;
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }
            int[] _d = p;
            p = d;
            d = _d;
        }
        return p[n];
    }

    public static boolean startsWith(String str, String prefix) {
        return startsWith(str, prefix, false);
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return startsWith(str, prefix, true);
    }

    private static boolean startsWith(String str, String prefix, boolean ignoreCase) {
        if (str == null || prefix == null)
            return (str == null && prefix == null);
        if (prefix.length() > str.length())
            return false;
        return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
    }

    public static boolean startsWithAny(String string, String[] searchStrings) {
        if (isEmpty(string) || ArrayUtils.isEmpty((Object[])searchStrings))
            return false;
        for (int i = 0; i < searchStrings.length; i++) {
            String searchString = searchStrings[i];
            if (startsWith(string, searchString))
                return true;
        }
        return false;
    }

    public static boolean endsWith(String str, String suffix) {
        return endsWith(str, suffix, false);
    }

    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return endsWith(str, suffix, true);
    }

    private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str == null || suffix == null)
            return (str == null && suffix == null);
        if (suffix.length() > str.length())
            return false;
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    public static String normalizeSpace(String str) {
        str = strip(str);
        if (str == null || str.length() <= 2)
            return str;
        StrBuilder b = new StrBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c)) {
                if (i > 0 && !Character.isWhitespace(str.charAt(i - 1)))
                    b.append(' ');
            } else {
                b.append(c);
            }
        }
        return b.toString();
    }

    public static boolean endsWithAny(String string, String[] searchStrings) {
        if (isEmpty(string) || ArrayUtils.isEmpty((Object[])searchStrings))
            return false;
        for (int i = 0; i < searchStrings.length; i++) {
            String searchString = searchStrings[i];
            if (endsWith(string, searchString))
                return true;
        }
        return false;
    }
}
