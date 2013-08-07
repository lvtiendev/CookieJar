//@author A0059827N
package cookiejar.gui;

import java.util.Vector;

/**
 * This class provides utility methods used in the GUI package.
 */
public class Utilities {

	/**
	 * Returns the sum of all elements of an integer array.
	 * 
	 * @param intArray
	 *            an integer array
	 * @return the sum of all elements
	 */
	public static int arraySum(int[] intArray) {
		int sum = 0;
		for (int i = 0; i < intArray.length; ++i) {
			sum += intArray[i];
		}

		return sum;
	}

	/**
	 * Returns a converted vector from an array.
	 * 
	 * @param array
	 *            the array to be converted
	 * @return the converted vector
	 */
	public static <T> Vector<T> convertArrayToVector(T[] array) {
		Vector<T> vector = new Vector<T>();

		for (int i = 0; i < array.length; ++i) {
			vector.add(array[i]);
		}

		return vector;
	}
}