package team.creative.creativecore.common.util.type.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class CopyArrayCollection<E> implements Collection<E>, Cloneable {
    
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    
    public static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;
    
    public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        // preconditions not checked because of inlining
        // assert oldLength >= 0
        // assert minGrowth > 0
        
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth); // might overflow
        if (0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH) {
            return prefLength;
        } else {
            // put code cold in a separate method
            return hugeLength(oldLength, minGrowth);
        }
    }
    
    private static int hugeLength(int oldLength, int minGrowth) {
        int minLength = oldLength + minGrowth;
        if (minLength < 0) { // overflow
            throw new OutOfMemoryError("Required array length " + oldLength + " + " + minGrowth + " is too large");
        } else if (minLength <= SOFT_MAX_ARRAY_LENGTH) {
            return SOFT_MAX_ARRAY_LENGTH;
        } else {
            return minLength;
        }
    }
    
    private Object[] content;
    
    private int size;
    
    private boolean hadIterator = false;
    
    public CopyArrayCollection() {
        content = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
        size = 0;
    }
    
    public CopyArrayCollection(int initialCapacity) {
        if (initialCapacity > 0) {
            this.content = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.content = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        size = 0;
    }
    
    public CopyArrayCollection(E element) {
        this(1);
        this.content[0] = element;
        this.size = 1;
    }
    
    public CopyArrayCollection(Collection<? extends E> c) {
        if (c instanceof CopyArrayCollection)
            content = Arrays.copyOf(((CopyArrayCollection) c).content, ((CopyArrayCollection) c).content.length, Object[].class);
        else
            content = c.toArray(new Object[0]);
        this.size = c.size();
    }
    
    public CopyArrayCollection(Iterable<? extends E> c) {
        this();
        c.forEach(this::add);
    }
    
    public CopyArrayCollection(E... toCopyIn) {
        this.content = Arrays.copyOf(toCopyIn, toCopyIn.length, Object[].class);
        this.size = content.length;
    }
    
    public synchronized void trimToSize() {
        if (size < content.length) {
            hadIterator = false;
            content = (size == 0) ? EMPTY_ELEMENTDATA : Arrays.copyOf(content, size);
        }
    }
    
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > content.length && !(content == DEFAULTCAPACITY_EMPTY_ELEMENTDATA && minCapacity <= DEFAULT_CAPACITY))
            grow(minCapacity);
    }
    
    private Object[] grow(int minCapacity) {
        int oldCapacity = content.length;
        if (oldCapacity > 0 || content != DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            int newCapacity = newLength(oldCapacity, minCapacity - oldCapacity, /* minimum growth */
                    oldCapacity >> 1 /* preferred growth */);
            hadIterator = false;
            return content = Arrays.copyOf(content, newCapacity);
        }
        hadIterator = false;
        return content = new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];
    }
    
    private Object[] grow() {
        return grow(size + 1);
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    public E first() {
        Object[] es;
        int size;
        synchronized (this) {
            es = content;
            size = this.size;
        }
        if (size > 0)
            return (E) es[0];
        return null;
    }
    
    @Override
    public boolean contains(Object o) {
        Object[] es;
        int size;
        synchronized (this) {
            es = content;
            size = this.size;
        }
        return indexOfRange(o, es, 0, size) >= 0;
    }
    
    int indexOfRange(Object o, Object[] es, int start, int end) {
        if (o == null) {
            for (int i = start; i < end; i++)
                if (es[i] == null)
                    return i;
        } else
            for (int i = start; i < end; i++)
                if (o.equals(es[i]))
                    return i;
        return -1;
    }
    
    @Override
    public Object clone() {
        return new CopyArrayCollection<E>(this);
    }
    
    @Override
    public Object[] toArray() {
        Object[] es;
        int size;
        synchronized (this) {
            es = content;
            size = this.size;
        }
        return Arrays.copyOf(es, size);
    }
    
    @Override
    public <T> T[] toArray(T[] a) {
        Object[] es;
        int len;
        synchronized (this) {
            es = content;
            len = es.length;
        }
        if (a.length < len)
            return (T[]) Arrays.copyOf(es, len, a.getClass());
        else {
            System.arraycopy(es, 0, a, 0, len);
            if (a.length > len)
                a[len] = null;
            return a;
        }
    }
    
    @Override
    public boolean add(E e) {
        synchronized (this) {
            Object[] es = content;
            int size = this.size;
            if (size == es.length)
                es = grow();
            es[size] = e;
            this.size = size + 1;
        }
        return true;
    }
    
    @Override
    public void clear() {
        synchronized (this) {
            if (hadIterator) {
                hadIterator = false;
                this.content = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
                this.size = 0;
            } else {
                final Object[] es = content;
                for (int to = size, i = size = 0; i < to; i++)
                    es[i] = null;
            }
        }
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty())
            return false;
        synchronized (this) {
            if (hadIterator) {
                Object[] cs = c.toArray();
                Object[] es = content;
                int len = size;
                int newSize = len + cs.length;
                Object[] newElements = Arrays.copyOf(es, newSize);
                System.arraycopy(cs, 0, newElements, len, cs.length);
                hadIterator = false;
                this.content = newElements;
                this.size = newSize;
            } else {
                Object[] a = c.toArray();
                int numNew = a.length;
                Object[] elementData;
                final int s;
                if (numNew > (elementData = this.content).length - (s = size))
                    elementData = grow(s + numNew);
                System.arraycopy(a, 0, elementData, s, numNew);
                this.content = elementData;
                size = s + numNew;
            }
        }
        return true;
    }
    
    @Override
    public boolean containsAll(Collection<?> c) {
        Object[] es;
        int size;
        synchronized (this) {
            es = content;
            size = this.size;
        }
        for (Object o : c)
            if (indexOfRange(o, es, 0, size) < 0)
                return false;
        return true;
    }
    
    @Override
    public Iterator<E> iterator() {
        final Object[] es;
        final int size;
        synchronized (this) {
            es = content;
            size = this.size;
            hadIterator = true;
        }
        return new Iterator<E>() {
            
            int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < size;
            }
            
            @Override
            public E next() {
                E result = (E) es[index];
                index++;
                return result;
            }
            
            @Override
            public void remove() {
                CopyArrayCollection.this.remove(es[index]);
            }
        };
    }
    
    @Override
    public boolean remove(Object o) {
        synchronized (this) {
            Object[] snapshot = content;
            int index = indexOfRange(o, snapshot, 0, snapshot.length);
            return index >= 0 && remove(o, snapshot, index);
        }
    }
    
    private boolean remove(Object o, Object[] snapshot, int index) {
        if (hadIterator) {
            Object[] current = content;
            int len = size;
            Object[] newElements = new Object[len - 1];
            System.arraycopy(current, 0, newElements, 0, index);
            System.arraycopy(current, index + 1, newElements, index, len - index - 1);
            this.size -= 1;
            this.hadIterator = false;
            this.content = newElements;
        } else {
            final Object[] es = this.content;
            fastRemove(es, index);
        }
        return true;
    }
    
    private synchronized void fastRemove(Object[] es, int i) {
        final int newSize;
        if ((newSize = size - 1) > i)
            System.arraycopy(es, i + 1, es, i, newSize - i);
        es[this.size = size - 1] = null;
    }
    
    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return bulkRemove(c::contains);
    }
    
    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return bulkRemove(e -> !c.contains(e));
    }
    
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        return bulkRemove(filter);
    }
    
    private boolean bulkRemove(Predicate<? super E> filter) {
        synchronized (this) {
            return bulkRemove(filter, 0, size);
        }
    }
    
    private static long[] nBits(int n) {
        return new long[((n - 1) >> 6) + 1];
    }
    
    private static void setBit(long[] bits, int i) {
        bits[i >> 6] |= 1L << i;
    }
    
    private static boolean isClear(long[] bits, int i) {
        return (bits[i >> 6] & (1L << i)) == 0;
    }
    
    boolean bulkRemove(Predicate<? super E> filter, int i, int end) {
        // assert Thread.holdsLock(lock);
        final Object[] es = content;
        // Optimize for initial run of survivors
        while (i < end && !filter.test((E) es[i])) {
            i++;
        }
        if (i < end) {
            final int beg = i;
            final long[] deathRow = nBits(end - beg);
            int deleted = 1;
            deathRow[0] = 1L; // set bit 0
            for (i = beg + 1; i < end; i++)
                if (filter.test((E) es[i])) {
                    setBit(deathRow, i - beg);
                    deleted++;
                }
            // Did filter reentrantly modify the list?
            if (es != content)
                throw new ConcurrentModificationException();
            final Object[] newElts = Arrays.copyOf(es, es.length - deleted);
            int w = beg;
            for (i = beg; i < end; i++)
                if (isClear(deathRow, i - beg))
                    newElts[w++] = es[i];
            System.arraycopy(es, i, newElts, w, es.length - i);
            hadIterator = false;
            this.content = newElts;
            this.size = newElts.length;
            return true;
        } else {
            if (es != content)
                throw new ConcurrentModificationException();
            return false;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof List<?> list))
            return false;

        Iterator<?> it = list.iterator();
        for (Object element : content)
            if (!it.hasNext() || !Objects.equals(element, it.next()))
                return false;
        return !it.hasNext();
    }
    
    private static int hashCodeOfRange(Object[] es, int from, int to) {
        int hashCode = 1;
        for (int i = from; i < to; i++) {
            Object x = es[i];
            hashCode = 31 * hashCode + (x == null ? 0 : x.hashCode());
        }
        return hashCode;
    }
    
    @Override
    public int hashCode() {
        final Object[] es;
        final int size;
        synchronized (this) {
            es = content;
            size = this.size;
        }
        return hashCodeOfRange(es, 0, size);
    }
    
    @Override
    public String toString() {
        Iterator<E> it = iterator();
        if (!it.hasNext())
            return "[]";
        
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            E e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (!it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }
}
