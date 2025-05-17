package assign11;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class represents a better dynamic array that stores elements of a
 * generic type, doubling the length of the backing array when more space is
 * needed and never shrinking.
 * 
 * @param <T> the type of elements stored in this dynamic array
 * 
 * @author Prof. Parker, Prof. Heisler, and Olivia Styler
 * @version November 22, 2024
 */
public class BetterDynamicArray<T> implements Iterable<T> {

	// DO NOT ADD OR REMOVE ANY INSTANCE VARIABLES
	private Object[] elements; // the backing array
	private int elementCount; // the number of elements

	/**
	 * Creates a dynamic array with space for ten elements, but zero spaces
	 * occupied.
	 * 
	 * DO NOT MODIFY THIS METHOD
	 */

	@SuppressWarnings("unchecked")
	public BetterDynamicArray() {
		elements = (T[]) new Object[10]; // Use Object array and cast to T[]
		elementCount = 0;
	}

	/**
	 * Appends the given AudioEvent to end of this dynamic array.
	 * 
	 * @param value - the AudioEvent to append
	 */
	public void add(T value) {
		insert(elementCount, value);
	}

	/**
	 * Inserts a given AudioEvent into this dynamic array at a given index.
	 * 
	 * @param index - the index at which to insert
	 * @param value - the AudioEvent to insert
	 * @throws IndexOutOfBoundsException if the given index is out of bounds
	 */
	public void insert(int index, T value) {
		if (index < 0 || index > elementCount) { // index must be between 0 and elementCount inclusive
			throw new IndexOutOfBoundsException("Index out of bounds: " + index);
		}

		if (elementCount == elements.length) {
			doubleBackingArray();
		}

		for (int i = elementCount; i > index; i--) {
			elements[i] = elements[i - 1];
		}

		elements[index] = value;

		elementCount++;
	}

	/**
	 * Creates a new array with twice the length as the backing array. Copies all
	 * elements from the backing array to the new array. Sets the backing array
	 * reference to the new array.
	 */
	@SuppressWarnings("unchecked")
	private void doubleBackingArray() {
		T[] largerArray = (T[]) new Object[elements.length * 2]; // Create a larger generic array
		System.arraycopy(elements, 0, largerArray, 0, elements.length);
		elements = largerArray;
	}

	/**
	 * Gets the AudioEvent stored in this dynamic array at the given index.
	 * 
	 * @param index - the index of the element to get
	 * @return the element at the given index
	 * @throws IndexOutOfBoundsException if the given index is out of bounds
	 */
	@SuppressWarnings("unchecked")
	public T get(int index) {
		if (index < 0 || index >= elementCount) {
			throw new IndexOutOfBoundsException();
		}

		return (T) elements[index];
	}

	/**
	 * Returns the number of elements in this dynamic array.
	 * 
	 * @return the number of elements
	 */
	public int size() {
		return elementCount;
	}

	/**
	 * Sets (i.e., changes) the AudioEvent stored in this dynamic array at the given
	 * index to the given integer.
	 * 
	 * @param index - the index of the element to set
	 * @param value - the new AudioEvent value for setting the element
	 * @throws IndexOutOfBoundsException if the given index is out of bounds
	 */
	public void set(int index, T value) {
		if (index < 0 || index >= elementCount) {
			throw new IndexOutOfBoundsException();
		}
		elements[index] = value;
	}

	/**
	 * Removes the AudioEvent at the given index from this dynamic array.
	 * 
	 * @param index - the index of the element to delete
	 * @throws IndexOutOfBoundsException if the given index is out of bounds
	 */
	public void remove(int index) {
		if (index < 0 || index >= elementCount) {
			throw new IndexOutOfBoundsException();
		}

		for (int i = index; i < elementCount - 1; i++) {
			elements[i] = elements[i + 1];
		}

		elements[elementCount - 1] = null;

		elementCount--;

	}

	/**
	 * Removes the first element in the array that is equal to the given value. If
	 * no equal element is found, the array is not changed.
	 * 
	 * @param value - the AudioEvent to be removed
	 */
	public void remove(T value) {
		for (int i = 0; i < elementCount; i++) {
			if (elements[i].equals(value)) {
				remove(i);
				return;
			}
		}

	}

	/**
	 * Removes all elements from the dynamic array.
	 */
	public void clear() {
		elementCount = 0;
	}

	/**
	 * Sorts the elements of this dynamic array from smallest to largest. This
	 * depends on your AudioEvent class implementing the Comparable interface.
	 */
	@SuppressWarnings("unchecked")
	public void sort() {
		if (elementCount <= 0) {
			return; // Nothing to sort
		}

		T[] sortedArray = (T[]) new Comparable[elementCount];
		System.arraycopy(elements, 0, sortedArray, 0, elementCount);
		java.util.Arrays.sort((Comparable[]) sortedArray);
		System.arraycopy(sortedArray, 0, elements, 0, elementCount);
	}

	/**
	 * Generates a textual representation of this dynamic array.
	 * 
	 * @return the textual representation
	 * 
	 *         DO NOT MODIFY THIS METHOD
	 */
	public String toString() {
		String result = "[";
		if (size() > 0)
			result += get(0);

		for (int i = 1; i < size(); i++)
			result += ", " + get(i);

		return result + "] backing array length: " + elements.length;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < size(); // Assuming size() is implemented
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return get(index++); // Assuming get() retrieves the element at index
			}
		};
	}
}