package se.dannejosefsson.circularbuffer;



import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

public interface CircularDoubleBufferInterface {

    /**
     * @throws BufferOverflowException if buffer is full.
     */
    public boolean add(final double d) throws BufferOverflowException;

    /**
     * @throws BufferOverflowException if given collection is greater than
     *             buffer capacity.
     */
    public boolean add(final double[] newData, final int offsetArray, final int n)
            throws BufferOverflowException, IndexOutOfBoundsException;

    /**
     * @return The capacity given at creation.
     */
    public int capacity();

    public void clear();

    public double get() throws IndexOutOfBoundsException;

    public void get(double[] dst, final int offset, final int length)
            throws BufferUnderflowException, IndexOutOfBoundsException;

    public double peek() throws BufferUnderflowException;

    public double peek(final int location) throws BufferUnderflowException;

    public void peek(double[] dst, final int offset, final int length)
            throws BufferUnderflowException, IndexOutOfBoundsException;

    public double set(final int location, final double d) throws IndexOutOfBoundsException;

    public int size();

}
