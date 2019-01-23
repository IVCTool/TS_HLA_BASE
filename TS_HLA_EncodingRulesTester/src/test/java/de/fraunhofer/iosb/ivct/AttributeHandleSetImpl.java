package de.fraunhofer.iosb.ivct;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;

public class AttributeHandleSetImpl implements AttributeHandleSet {
	/**
	 *
	 */
	private static final long serialVersionUID = -8753751797072403032L;

	public AttributeHandleSetImpl() {

	}
	Set<AttributeHandle> s = new HashSet<AttributeHandle>();

	public boolean isEmpty() {
		return s.isEmpty();
	}

	public boolean containsAll(Collection<?> c) {
		return s.containsAll(c);
	}

	public boolean contains(Object o) {
		return s.contains(o);
	}

	public void clear() {
		s.clear();
	}

	public Object[] toArray() {
		return s.toArray();
	}

	@Override
	public <AttributeHandle> AttributeHandle[] toArray(AttributeHandle[] ah) {
		return s.toArray(ah);
	}

	public boolean removeAll(Collection<?> c) {
		return s.removeAll(c);
	}

	public Iterator<AttributeHandle> iterator() {
		return s.iterator();
	}

	public boolean retainAll(Collection<?> c) {
		return s.retainAll(c);
	}

	public int size() {
		return s.size();
	}

	public boolean add(AttributeHandle ah) {
		return s.add(ah);
	}

	public boolean remove(Object o) {
		return s.remove(o);
	}

	public boolean addAll(Collection<? extends  AttributeHandle> a) {
		return s.addAll(a);
	}

	public AttributeHandleSet clone() {
		AttributeHandleSet ahs = null;
		try {
			ahs = (AttributeHandleSet) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ahs;
//		return (AttributeHandleSet) super.clone();
	}
}
