package com.creativemd.creativecore.common.utils.type;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class SingletonList<E> extends AbstractList<E> implements RandomAccess, Serializable {
	
	private static final long serialVersionUID = 3093736618740652951L;
	
	private E element;
	
	public SingletonList(E obj) {
		element = obj;
	}
	
	public SingletonList<E> setElement(E element) {
		this.element = element;
		return this;
	}
	
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private boolean hasNext = true;
			
			@Override
			public boolean hasNext() {
				return hasNext;
			}
			
			@Override
			public E next() {
				if (hasNext) {
					hasNext = false;
					return element;
				}
				throw new NoSuchElementException();
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void forEachRemaining(Consumer<? super E> action) {
				Objects.requireNonNull(action);
				if (hasNext) {
					action.accept(element);
					hasNext = false;
				}
			}
		};
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public boolean contains(Object obj) {
		return element == null ? obj == null : element.equals(obj);
	}
	
	@Override
	public E get(int index) {
		if (index != 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: 1");
		return element;
	}
	
	@Override
	public void forEach(Consumer<? super E> action) {
		action.accept(element);
	}
	
	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void sort(Comparator<? super E> c) {
	}
	
	@Override
	public Spliterator<E> spliterator() {
		return new Spliterator<E>() {
			long est = 1;
			
			@Override
			public Spliterator<E> trySplit() {
				return null;
			}
			
			@Override
			public boolean tryAdvance(Consumer<? super E> consumer) {
				Objects.requireNonNull(consumer);
				if (est > 0) {
					est--;
					consumer.accept(element);
					return true;
				}
				return false;
			}
			
			@Override
			public void forEachRemaining(Consumer<? super E> consumer) {
				tryAdvance(consumer);
			}
			
			@Override
			public long estimateSize() {
				return est;
			}
			
			@Override
			public int characteristics() {
				int value = (element != null) ? Spliterator.NONNULL : 0;
				
				return value | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.ORDERED;
			}
		};
	}
}