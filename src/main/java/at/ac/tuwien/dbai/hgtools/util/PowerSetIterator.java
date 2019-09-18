package at.ac.tuwien.dbai.hgtools.util;

import java.util.*;

/**
 * @author st0le
 */
public class PowerSetIterator<E> implements ListIterator<Set<E>>, Iterable<Set<E>> {
    private E[] arr = null;
    private BitSet bset = null;

    @SuppressWarnings("unchecked")
    public PowerSetIterator(Set<E> set) {
        arr = (E[]) set.toArray();
        bset = new BitSet(arr.length + 1);
    }

    @SuppressWarnings("unchecked")
    public PowerSetIterator(Collection<E> set) {
        arr = (E[]) set.toArray();
        bset = new BitSet(arr.length + 1);
    }

    @Override
    public boolean hasNext() {
        return !bset.get(arr.length);
    }

    @Override
    public Set<E> next() {
        Set<E> returnSet = getCurrent();

        //increment bset
        for (int i = 0; i < bset.size(); i++) {
            if (!bset.get(i)) {
                bset.set(i);
                break;
            } else
                bset.clear(i);
        }

        return returnSet;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public Iterator<Set<E>> iterator() {
        return this;
    }

    @Override
    public void add(Set<E> e) {
        throw new UnsupportedOperationException("Not Supported!");

    }

    @Override
    public boolean hasPrevious() {
        for (int i = 0; i < bset.size(); i++)
            if (bset.get(i))
                return true;
        return false;
    }

    @Override
    public int nextIndex() {
        // TODO Auto-generated method stub
        return convert();
    }

    @Override
    public Set<E> previous() {

        int i;
        for (i = 0; i < bset.size(); i++)
            if (bset.get(i))
                break;
        bset.clear(i);
        for (i -= 1; i >= 0; i--)
            bset.set(i);


        return getCurrent();
    }

    @Override
    public int previousIndex() {
        return convert() - 1;
    }

    @Override
    public void set(Set<E> e) {
        throw new UnsupportedOperationException("Not Supported!");
    }

    private int convert() {
        int value = 0;
        for (int i = 0; i < bset.length(); ++i) {
            value += bset.get(i) ? (1 << i) : 0;
        }
        return value;
    }

    private Set<E> getCurrent() {
        Set<E> returnSet = new HashSet<E>();
        for (int i = 0; i < arr.length; i++) {
            if (!bset.get(i))
                returnSet.add(arr[i]);
        }
        return returnSet;
    }


}
