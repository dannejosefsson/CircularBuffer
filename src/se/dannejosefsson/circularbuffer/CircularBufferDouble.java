package se.dannejosefsson.circularbuffer;



import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.RandomAccess;

/**
 * @author Daniel Josefsson
 * @url 
 *      http://www.museful.net/2012/software-development/circulararraylist-for-java
 */
public class CircularBufferDouble extends AbstractCircularBufferDouble implements RandomAccess {
    private final double[] buffer;

    protected final int bufferLength;

    protected int tail = 0;

    protected int head = 0;

    /**
     * @throws IllegalArgumentException if n is less than 0.
     * @param n
     */
    public CircularBufferDouble(int n) {
        super(n);
        bufferLength = n + 1;
        buffer = new double[bufferLength];
    }

    /**
     * @return The capacity given at creation.
     */
    public int capacity() {
        return this.bufferLength - 1;
    }

    /**
     * Calculates the index by folding it so it fits inside the buffer.
     * 
     * @param i
     * @return integer residing in the buffer.
     */
    protected int wrappedIndex(int i) {
        return i % bufferLength;
    }

    public double get() throws BufferUnderflowException {
        double result = peek();
        head = wrappedIndex(head + 1);
        return result;
    }

    public double peek(final int location) throws IndexOutOfBoundsException {
        int size = size();
        if (0 > location || location >= size) {
            throw new IndexOutOfBoundsException("0 <= location < " + size + ", Given : " + location);
        }
        return buffer[wrappedIndex(head + location)];
    }

    public double set(int location, double d) {
        int size = size();
        if (0 > location || location >= size) {
            throw new IndexOutOfBoundsException("0 <= location < " + size + ", Given : " + location);
        }
        double ret = buffer[wrappedIndex(head + location)];
        buffer[wrappedIndex(head + location)] = d;
        return ret;
    }

    /**
     * Uses set to add object.
     * 
     * @see {@link #set(int, Object)}
     * @throws BufferOverflowException() if buffer is full.
     */
    public boolean add(double d) {
        int size = size();
        if (capacity() == size) {
            throw new BufferOverflowException();
        }
        buffer[wrappedIndex(tail)] = d;
        tail = wrappedIndex(tail + 1);
        return true;
    }

    public int size() {
        return tail - head + (tail < head ? bufferLength : 0);
    }

    @Override
    public boolean add(double[] newData, int offsetArray, int n) throws BufferOverflowException,
            IndexOutOfBoundsException {
        if (offsetArray < 0 || n + offsetArray > newData.length) {
            throw new IndexOutOfBoundsException();
        }
        if (n > this.capacity() - this.size()) {
            throw new BufferOverflowException();
        }
        int topPart = buffer.length - tail;
        int topLength = (n >= topPart) ? topPart : n;
        int lowLength = (n >= topPart) ? n - topLength : 0;
        System.arraycopy(newData, 0, buffer, tail, topLength);
        System.arraycopy(newData, topLength, buffer, 0, lowLength);
        tail = (0 == lowLength) ? tail + topLength : lowLength;
        return true;
    }

    @Override
    public void clear() {
        head = 0;
        tail = 0;
    }

    @Override
    public void get(double[] dst, final int offset, final int length)
            throws IndexOutOfBoundsException, BufferUnderflowException {
        peek(dst, offset, length);
        head = wrappedIndex(head + length);
    }

    @Override
    public double peek() throws BufferUnderflowException {
        if (0 == size()) {
            throw new BufferUnderflowException();
        }
        return peek(0);
    }

    @Override
    public void peek(double[] dst, final int offset, final int length)
            throws BufferUnderflowException, IndexOutOfBoundsException {
        if (offset < 0 || length + offset > dst.length) {
            throw new IndexOutOfBoundsException();
        }
        if (size() < length) {
            throw new BufferUnderflowException();
        }
        int topPart = buffer.length - head;
        int topLength = (length >= topPart) ? topPart : length;
        int lowLength = (length >= topPart) ? length - topLength : 0;
        System.arraycopy(buffer, head, dst, offset, topLength);
        System.arraycopy(buffer, 0, dst, offset + topLength, lowLength);
    }
}
