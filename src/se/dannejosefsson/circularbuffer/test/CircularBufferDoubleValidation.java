
package se.dannejosefsson.circularbuffer.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import se.dannejosefsson.circularbuffer.AbstractCircularBufferDouble;
import se.dannejosefsson.circularbuffer.CircularBufferDouble;
import se.dannejosefsson.circularbuffer.CircularBufferDoubleDB;

@RunWith(JUnit4.class)
public class CircularBufferDoubleValidation {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void naturalLengthInit() {
        int bufferLength = 0;
        final int nBuffers = 2;
        AbstractCircularBufferDouble[] buffers = new AbstractCircularBufferDouble[nBuffers];
        buffers[0] = new CircularBufferDouble(bufferLength);
        buffers[1] = new CircularBufferDoubleDB(bufferLength);
        for (AbstractCircularBufferDouble buffer : buffers) {
            assertEquals("The length of the buffer differs from given: " + bufferLength,
                    buffer.capacity(), bufferLength);
        }
        bufferLength = 100000;
        buffers[0] = new CircularBufferDouble(bufferLength);
        buffers[1] = new CircularBufferDoubleDB(bufferLength);
        for (AbstractCircularBufferDouble buffer : buffers) {
            assertEquals("The length of the buffer differs from given: " + bufferLength,
                    buffer.capacity(), bufferLength);
        }
    }

    @Test
    public void nonNaturalLengthInit() {
        final int bufferLength = -1;
        @SuppressWarnings("unused")
        CircularBufferDouble cdb0;
        @SuppressWarnings("unused")
        CircularBufferDoubleDB cdb1;
        boolean throwed = false;
        try {
            cdb0 = new CircularBufferDouble(bufferLength);
        } catch (IllegalArgumentException e) {
            throwed = true;
        } finally {
            assertTrue(throwed);
            throwed = false;
        }
        try {
            cdb1 = new CircularBufferDoubleDB(bufferLength);
        } catch (IllegalArgumentException e) {
            throwed = true;
        } finally {
            assertTrue(throwed);
            throwed = false;
        }
    }

    static public double[] fillArray(final int length, final double d) {
        double[] array = new double[length];
        for (int i = 0; i < length; i++) {
            array[i] = d;
        }
        return array;
    }

    static public double[] fillArrayChirp(int length) {
        double[] array = new double[length];
        for (int i = 0; i < length; i++) {
            array[i] = (double)i;
        }
        return array;
    }

    @Test
    public void add() {
        final int bufferLength = 3;
        double testVal = 2d;
        final int nBuffers = 2;
        AbstractCircularBufferDouble[] buffers = new AbstractCircularBufferDouble[nBuffers];
        buffers[0] = new CircularBufferDouble(bufferLength);
        buffers[1] = new CircularBufferDoubleDB(bufferLength);
        boolean throwed = false;
        for (AbstractCircularBufferDouble buffer : buffers) {
            for (int i = 0; i < bufferLength; i++) {
                buffer.add(testVal);
            }
            assertEquals(bufferLength, buffer.size());
            try {
                buffer.add(testVal);
            } catch (BufferOverflowException e) {
                throwed = true;
            } finally {
                assertTrue("BufferOverflowException not throwed.", throwed);
                throwed = false;
            }
        }
    }

    @Test
    public void addArray() {
        final int bufferLength = 5;
        double testVal = 2d;
        final int nBuffers = 2;
        AbstractCircularBufferDouble[] buffers = new AbstractCircularBufferDouble[nBuffers];
        buffers[0] = new CircularBufferDouble(bufferLength);
        buffers[1] = new CircularBufferDoubleDB(bufferLength);
        boolean throwed = false;
        final double[] toAdd = fillArray(bufferLength, testVal);
        for (AbstractCircularBufferDouble buffer : buffers) {
            // Throw IndexOutOfBoundsExceptions.
            // Negative offset
            try {
                buffer.add(toAdd, -1, 1);
            } catch (IndexOutOfBoundsException e) {
                throwed = true;
            } finally {
                assertTrue("IndexOutOfBoundsException not throwed.", throwed);
                throwed = false;
            }
            // After array end.
            try {
                buffer.add(toAdd, 1, bufferLength);
            } catch (IndexOutOfBoundsException e) {
                throwed = true;
            } finally {
                assertTrue("IndexOutOfBoundsException not throwed.", throwed);
                throwed = false;
            }
            // Add empty array
            buffer.add(toAdd, 0, 0);
            assertEquals(0, buffer.size());
            // Fill buffer
            buffer.add(toAdd, 1, bufferLength - 1);
            buffer.add(toAdd, 0, 1);
            assertEquals(bufferLength, buffer.size());
            // Throw buffer overflow exceptions.
            try {
                buffer.add(toAdd, 0, bufferLength);
            } catch (BufferOverflowException e) {
                throwed = true;
            } finally {
                assertTrue("BufferOverflowException not throwed.", throwed);
                throwed = false;
            }
        }
    }

    @Test
    public void clean() {
        final int bufferLength = 1;
        double testVal = 2d;
        final int nBuffers = 2;
        AbstractCircularBufferDouble[] buffers = new AbstractCircularBufferDouble[nBuffers];
        buffers[0] = new CircularBufferDouble(bufferLength);
        buffers[1] = new CircularBufferDoubleDB(bufferLength);
        for (AbstractCircularBufferDouble buffer : buffers) {
            buffer.add(testVal);
            buffer.clear();
            assertEquals(0, buffer.size());
        }
    }

    @Test
    public void peek() {
        final int bufferLength = 5;
        final int nBuffers = 2;
        AbstractCircularBufferDouble[] buffers = new AbstractCircularBufferDouble[nBuffers];
        buffers[0] = new CircularBufferDouble(bufferLength);
        buffers[1] = new CircularBufferDoubleDB(bufferLength);
        boolean throwed = false;
        final double[] toAdd = fillArrayChirp(bufferLength);
        for (AbstractCircularBufferDouble buffer : buffers) {
            buffer.add(toAdd, 0, bufferLength);
            assertEquals(toAdd[0], buffer.peek(), 0d);
            for (int i = 0; i < bufferLength; i++) {
                assertEquals((double)i, buffer.peek(i), 0d);
                assertEquals(bufferLength, buffer.size());
            }
            // Negative offset
            try {
                buffer.peek(-1);
            } catch (IndexOutOfBoundsException e) {
                throwed = true;
            } finally {
                assertTrue("IndexOutOfBoundsException not throwed.", throwed);
                throwed = false;
            }
            // After array end.
            try {
                buffer.peek(bufferLength);
            } catch (IndexOutOfBoundsException e) {
                throwed = true;
            } finally {
                assertTrue("IndexOutOfBoundsException not throwed.", throwed);
                throwed = false;
            }
            // Throw a buffer underflow exception.
            buffer.clear();
            try {
                buffer.peek();
            } catch (BufferUnderflowException e) {
                throwed = true;
            } finally {
                assertTrue("BufferUnderflowException not throwed.", throwed);
                throwed = false;
            }
            // Batch tests
            double[] dst = new double[bufferLength];
            int offset = 2;
            buffer.add(toAdd, 0, bufferLength);
            // Negative offset
            try {
                buffer.peek(dst, -1, bufferLength);
            } catch (IndexOutOfBoundsException e) {
                throwed = true;
            } finally {
                assertTrue("IndexOutOfBoundsException not throwed.", throwed);
                throwed = false;
            }
            // After array end.
            try {
                buffer.peek(dst, offset, bufferLength);
            } catch (IndexOutOfBoundsException e) {
                throwed = true;
            } finally {
                assertTrue("IndexOutOfBoundsException not throwed.", throwed);
                throwed = false;
            }
            // Get the first part
            buffer.peek(dst, offset, bufferLength - offset);
            assertArrayEquals(Arrays.copyOfRange(toAdd, 0, bufferLength - offset),
                    Arrays.copyOfRange(dst, offset, bufferLength), 0d);
            // Get the second part
            buffer.peek(dst, 0, offset);
            assertArrayEquals(Arrays.copyOfRange(toAdd, 0, offset),
                    Arrays.copyOfRange(dst, 0, offset), 0d);
            // Throw a buffer underflow exception.
            buffer.clear();
            try {
                buffer.peek(dst, 0, offset);
            } catch (BufferUnderflowException e) {
                throwed = true;
            } finally {
                assertTrue("BufferUnderflowException not throwed.", throwed);
                throwed = false;
            }
        }
    }

    @Test
    public void get() {
        final int bufferLength = 5;
        final int nBuffers = 2;
        AbstractCircularBufferDouble[] buffers = new AbstractCircularBufferDouble[nBuffers];
        buffers[0] = new CircularBufferDouble(bufferLength);
        buffers[1] = new CircularBufferDoubleDB(bufferLength);
        boolean throwed = false;
        final double[] toAdd = fillArrayChirp(bufferLength);
        for (AbstractCircularBufferDouble buffer : buffers) {
            buffer.add(toAdd, 0, bufferLength);
            // Using get() removes item from buffer
            for (int i = 0; i < bufferLength; i++) {
                assertEquals((double)i, buffer.get(), 0d);
                assertEquals(bufferLength - i - 1, buffer.size());
            }
            // Throw a buffer underflow exception.
            try {
                buffer.get();
            } catch (BufferUnderflowException e) {
                throwed = true;
            } finally {
                assertTrue("BufferUnderflowException not throwed.", throwed);
                throwed = false;
            }
            // Batch tests
            double[] dst = new double[bufferLength];
            int offset = 2;
            buffer.clear();
            buffer.add(toAdd, 0, bufferLength);
            // Negative offset
            try {
                buffer.get(dst, -1, bufferLength);
            } catch (IndexOutOfBoundsException e) {
                throwed = true;
            } finally {
                assertTrue("IndexOutOfBoundsException not throwed.", throwed);
                throwed = false;
            }
            // Negative length
            try {
                buffer.get(dst, 0, -1);
            } catch (IndexOutOfBoundsException e) {
                throwed = true;
            } finally {
                assertTrue("IndexOutOfBoundsException not throwed.", throwed);
                throwed = false;
            }
            // After array end.
            try {
                buffer.get(dst, offset, bufferLength);
            } catch (IndexOutOfBoundsException e) {
                throwed = true;
            } finally {
                assertTrue("IndexOutOfBoundsException not throwed.", throwed);
                throwed = false;
            }
            // Get the first part
            buffer.get(dst, offset, bufferLength - offset);
            assertArrayEquals(Arrays.copyOfRange(toAdd, 0, bufferLength - offset),
                    Arrays.copyOfRange(dst, offset, bufferLength), 0d);
            // Get the second part
            buffer.get(dst, 0, offset);
            assertArrayEquals(Arrays.copyOfRange(toAdd, bufferLength - offset, bufferLength),
                    Arrays.copyOfRange(dst, 0, offset), 0d);
            // Throw a buffer underflow exception.
            try {
                buffer.get(dst, 0, offset);
            } catch (BufferUnderflowException e) {
                throwed = true;
            } finally {
                assertTrue("BufferUnderflowException not throwed.", throwed);
                throwed = false;
            }
        }
    }

    @Test
    public void circular() {
        final int bufferLength = 5;
        final int nBuffers = 2;
        final int offset = 2;
        final int topPart = bufferLength - offset;
        AbstractCircularBufferDouble[] buffers = new AbstractCircularBufferDouble[nBuffers];
        buffers[0] = new CircularBufferDouble(bufferLength);
        buffers[1] = new CircularBufferDoubleDB(bufferLength);
        final double[] toAdd = fillArrayChirp(bufferLength);
        double[] temp = new double[bufferLength];
        final double[] result = new double[bufferLength];
        System.arraycopy(toAdd, offset, result, 0, topPart);
        System.arraycopy(toAdd, 0, result, topPart, offset);
        double[] peaked = new double[bufferLength - offset];
        final double[] peakedExp = Arrays.copyOfRange(toAdd, offset, bufferLength);
        for (AbstractCircularBufferDouble buffer : buffers) {
            buffer.add(toAdd, 0, bufferLength);
            // Get the first values and refill the buffer so this values are
            // placed last in the buffer.
            buffer.get(temp, 0, offset);
            assertEquals(toAdd[offset], buffer.peek(), 0d);
            buffer.peek(peaked, 0, bufferLength - offset);
            assertArrayEquals(peakedExp, peaked, 0d);
            buffer.add(toAdd, 0, offset);
            buffer.get(temp, 0, bufferLength);
            assertArrayEquals(result, temp, 0d);
        }
    }

    @Test
    public void set() {
        final int bufferLength = 5;
        final int nBuffers = 2;
        AbstractCircularBufferDouble[] buffers = new AbstractCircularBufferDouble[nBuffers];
        buffers[0] = new CircularBufferDouble(bufferLength);
        buffers[1] = new CircularBufferDoubleDB(bufferLength);
        final double[] toAdd = fillArrayChirp(bufferLength);
        double[] temp = fillArrayChirp(bufferLength + 1);
        final double[] result = new double[bufferLength];
        System.arraycopy(temp, 1, result, 0, bufferLength);
        boolean throwed = false;
        for (AbstractCircularBufferDouble buffer : buffers) {
            buffer.add(toAdd, 0, bufferLength);
            // Get the first values and refill the buffer so this values are
            // placed last in the buffer.
            double peaked = buffer.peek();
            buffer.set(0, peaked + 1d);
            assertEquals(result[0], buffer.peek(), 0d);

            // Negative offset
            try {
                buffer.set(-1, 2d);
            } catch (IndexOutOfBoundsException e) {
                throwed = true;
            } finally {
                assertTrue("IndexOutOfBoundsException not throwed.", throwed);
                throwed = false;
            }
            // After array end.
            try {
                buffer.set(bufferLength, 2d);
            } catch (IndexOutOfBoundsException e) {
                throwed = true;
            } finally {
                assertTrue("IndexOutOfBoundsException not throwed.", throwed);
                throwed = false;
            }
        }
    }

    @Test
    public void compareBuffers() {
        // Input and output
        int length = (int)Math.pow(2, 14);
        final double[] data = fillArrayChirp(length);
        int overlaps = (int)Math.pow(2, 5);
        double[] overlap = fillArrayChirp(overlaps);
        for (int i = 0; i < overlaps; i++) {
            overlap[i] /= (double)overlaps;
        }
        final int nBuffers = 2;
        // TODO: Change this to about 10k if you want to do a real test.
        final int nRounds = 10;
        // Buffers of AbstractCircularBufferDoubleDB
        AbstractCircularBufferDouble[] buffers = new AbstractCircularBufferDouble[nBuffers];
        buffers[0] = new CircularBufferDouble(length);
        buffers[1] = new CircularBufferDoubleDB(length);
        // "Hard coded" reference buffers
        final int hcBuffers = 1;
        DoubleBuffer db = DoubleBuffer.allocate(length);

        double[][] temp = new double[nBuffers + hcBuffers][length];

        long[][] times = new long[nBuffers + hcBuffers][overlap.length];
        long currentTime;

        for (int j = 0; j < overlap.length; j++) {
            for (AbstractCircularBufferDouble buffer : buffers) {
                buffer.clear();
                buffer.add(data, 0, length);
            }
            db.clear();
            db.put(data, 0, length);
            int samplesToRemove = (int)((1 - overlap[j]) * length);
            for (int k = 0; k < nRounds; k++) {
                for (int i = 0; i < nBuffers; i++) {
                    currentTime = System.currentTimeMillis();
                    buffers[i].get(temp[i], 0, samplesToRemove);
                    buffers[i].peek(temp[i], samplesToRemove, length - samplesToRemove);
                    buffers[i].add(data, 0, samplesToRemove);
                    times[i][j] += System.currentTimeMillis() - currentTime;
                }
                // Hard coded buffers
                // Start double buffer, this should be linear in time over
                // different overlaps.
                currentTime = System.currentTimeMillis();
                // Get data from buffer that will be discarded next time.
                db.position(0);
                db.get(temp[nBuffers], 0, samplesToRemove);
                // "Peek" and shift buffer down to reuse data.
                db.get(temp[nBuffers], samplesToRemove, length - samplesToRemove);
                db.position(0);
                db.put(temp[nBuffers], samplesToRemove, length - samplesToRemove);
                // Add new data
                db.put(data, 0, samplesToRemove);
                times[nBuffers][j] += System.currentTimeMillis() - currentTime;
                // End double buffer
                // Make sure that the different buffers looks alike.
                // This take quite some time and can be skipped.
                for (int i = 1; i < nBuffers + hcBuffers; i++) {
                    assertArrayEquals(temp[i - 1], temp[i], 0d);
                }
            }
        }
        // Print results to log.
        List<String> names = new ArrayList<String>(nBuffers + hcBuffers);
        for (AbstractCircularBufferDouble buffer : buffers) {
            names.add(buffer.getClass().toString() + ": ");
        }
        names.add(db.getClass().toString() + ": ");
        Logger compResult = Logger.getLogger("compareResults");
        StringBuilder strB = new StringBuilder();
        for (int i = 0; i < nBuffers + hcBuffers; i++) {
            strB.append(names.get(i));
            for (int j = 0; j < overlap.length; j++) {
                strB.append(times[i][j]).append(",");
            }
            compResult.info(strB.toString());
            strB.delete(0, strB.length());
        }
    }
}
