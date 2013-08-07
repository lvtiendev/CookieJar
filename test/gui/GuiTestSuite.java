package gui;
//@author A0059827N
import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;
import cookiejar.gui.Utilities;

public class GuiTestSuite {

	@Test
	public void testArraySum() {
		int arraySize = 100;
		int expectedSum = arraySize*(arraySize+1)/2;
		int[] intArray = new int[arraySize];
		
		for (int i = 1; i <= arraySize; ++i) {
			intArray[i-1] = i;
		}
		
		assertEquals(expectedSum, Utilities.arraySum(intArray));
	}
	
	@Test
	public void testArrayToVectorConverter() {
		String[] array = new String[] {"Hello", "World", "haha", "bigbang!"};
		Vector<String> vector = Utilities.convertArrayToVector(array);
		
		for (int i = 0; i < vector.size(); ++i) {
			assertEquals(array[i], vector.get(i));
		}
	}
}
