package zplot.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import zplot.plotter.IPlotter;
import zplot.utility.Interval2D;
import zplot.utility.ObjectUtils;

public class SeriesCollectionBuilder implements List<SeriesCollection.Entry> {

    public static SeriesCollection emptyCollection(double xBegin, double xEnd, double yBegin, double yEnd) {
	assert xBegin <= xEnd;
	assert yBegin <= yEnd;
	return new SeriesCollection(new Interval2D(xBegin, xEnd, yBegin, yEnd), new ArrayList<SeriesCollection.Entry>(0));
    }

    public SeriesCollection build() {
	ArrayList<SeriesCollection.Entry> copy = new ArrayList<SeriesCollection.Entry>(data);
	Interval2D envelope = Interval2D.invalid();
	for (SeriesCollection.Entry e : copy) {
	    envelope = Interval2D.envelope(envelope, e.series.range());
	}
	return new SeriesCollection(envelope, copy);
    }

    @Override
    public int size() {
	return data.size();
    }

    @Override
    public boolean isEmpty() {
	return data.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
	return data.contains(o);
    }

    @Override
    public Object[] toArray() {
	return data.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
	return data.toArray(ts);
    }

    @Override
    public boolean add(SeriesCollection.Entry e) {
	return data.add(e);
    }

    @Override
    public boolean remove(Object o) {
	return data.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> clctn) {
	return data.containsAll(clctn);
    }

    @Override
    public boolean addAll(Collection<? extends SeriesCollection.Entry> clctn) {
	return data.addAll(clctn);
    }

    @Override
    public boolean addAll(int i, Collection<? extends SeriesCollection.Entry> clctn) {
	return data.addAll(i, clctn);
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
	return data.removeAll(clctn);
    }

    @Override
    public boolean retainAll(Collection<?> clctn) {
	return data.retainAll(clctn);
    }

    @Override
    public void clear() {
	data.clear();
    }

    @Override
    public SeriesCollection.Entry get(int i) {
	return data.get(i);
    }

    @Override
    public SeriesCollection.Entry set(int i, SeriesCollection.Entry e) {
	return data.set(i, e);
    }

    @Override
    public void add(int i, SeriesCollection.Entry e) {
	data.add(i, e);
    }

    @Override
    public SeriesCollection.Entry remove(int i) {
	return data.remove(i);
    }

    @Override
    public int indexOf(Object o) {
	return data.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
	return data.lastIndexOf(o);
    }

    @Override
    public Iterator<SeriesCollection.Entry> iterator() {
	return data.iterator();
    }

    @Override
    public ListIterator<SeriesCollection.Entry> listIterator() {
	return data.listIterator();
    }

    @Override
    public ListIterator<SeriesCollection.Entry> listIterator(int i) {
	return data.listIterator(i);
    }

    @Override
    public List<SeriesCollection.Entry> subList(int i, int i1) {
	return data.subList(i, i1);
    }

    public void add(Object key, ISeries series, IPlotter plotter) {
	add(new SeriesCollection.Entry(key, series, plotter));
    }
    
    public void add(ISeries series, IPlotter plotter) {
	add(null, series, plotter);
    }

    public SeriesCollection.Entry getFirstByKey(Object key) {
	for (SeriesCollection.Entry e : data) {
	    if (ObjectUtils.nullSafeCompare(key, key.equals(e.key))) {
		return e;
	    }
	}
	return null;
    }

    public Collection<SeriesCollection.Entry> getByKey(Object key) {
	ArrayList<SeriesCollection.Entry> matches = new ArrayList<SeriesCollection.Entry>();
	for (SeriesCollection.Entry e : data) {
	    if (ObjectUtils.nullSafeCompare(key, key.equals(e.key))) {
		matches.add(e);
	    }
	}
	return matches;
    }

    public void removeByKey(Object key) {
	Iterator<SeriesCollection.Entry> i = data.iterator();
	while (i.hasNext()) {
	    if (ObjectUtils.nullSafeCompare(key, i.next().key)) {
		i.remove();
	    }
	}
    }
    private final ArrayList<SeriesCollection.Entry> data = new ArrayList(4);
}
