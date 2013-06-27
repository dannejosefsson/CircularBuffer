package se.dannejosefsson.circularbuffer;



import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

abstract public class AbstractCircularBufferDouble implements CircularDoubleBufferInterface {
    public AbstractCircularBufferDouble(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Buffer length n has to be positive.");
        }
    }

    abstract public boolean add(double d) throws BufferOverflowException;

    abstract public boolean add(double[] newData, int offsetArray, int n)
            throws BufferOverflowException, IndexOutOfBoundsException;

    abstract public int capacity();

    abstract public void clear();

    abstract public double get() throws IndexOutOfBoundsException, BufferUnderflowException;

    abstract public void get(double[] dst, final int offset, final int length)
            throws IndexOutOfBoundsException, BufferUnderflowException;

    abstract public double peek() throws BufferUnderflowException;

    abstract public double peek(final int location) throws IndexOutOfBoundsException,
            BufferUnderflowException;

    abstract public void peek(double[] dst, final int offset, final int length)
            throws BufferUnderflowException, IndexOutOfBoundsException;

    abstract public double set(int location, double d) throws IndexOutOfBoundsException;

    abstract public int size();
}
