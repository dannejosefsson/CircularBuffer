package se.dannejosefsson.circularbuffer;



import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.DoubleBuffer;
import java.util.RandomAccess;

public class CircularBufferDoubleDB extends AbstractCircularBufferDouble implements RandomAccess {
    protected DoubleBuffer mDb;

    protected final int mBufferLength;

    protected int mHead = 0;

    public CircularBufferDoubleDB(int length) {
        super(length);
        this.mBufferLength = length;
        mDb = DoubleBuffer.allocate(length);
    }

    @Override
    public boolean add(double d) throws BufferOverflowException {
        mDb.put(d);
        return true;
    }

    @Override
    public boolean add(double[] newData, int offsetArray, int n) throws BufferOverflowException,
            IndexOutOfBoundsException {
        if (n > mDb.remaining() + mHead) {
            throw new BufferOverflowException();
        }
        int temp = mDb.remaining();
        if (n > mDb.remaining()) {
            downShift();
        }
        mDb.put(newData, offsetArray, n);
        return true;
    }

    public void clear() {
        mDb.clear();
        mHead = 0;
    }

    public int capacity() {
        return mDb.capacity();
    }

    @Override
    public double get() throws IndexOutOfBoundsException, BufferUnderflowException {
        if (0 == size()) {
            throw new BufferUnderflowException();
        }
        return mDb.get(mHead++);
    }

    @Override
    public void get(double[] dst, final int offset, final int length)
            throws IndexOutOfBoundsException, BufferUnderflowException {
        peek(dst, offset, length);
        mHead += length;
    }

    @Override
    public double peek() throws BufferUnderflowException {
        if (0 == size()) {
            throw new BufferUnderflowException();
        }
        return mDb.get(mHead);
    }

    @Override
    public double peek(int location) throws IndexOutOfBoundsException {
        int size = size();
        if (0 > location || location >= size) {
            throw new IndexOutOfBoundsException("0 <= location < " + size + ", Given : " + location);
        }
        return mDb.get(location + mHead);
    }

    @Override
    public void peek(double[] dst, final int offset, final int length)
            throws BufferUnderflowException, IndexOutOfBoundsException {
        int size = size();
        if (size < length) {
            throw new BufferUnderflowException();
        }
        if (0 > length) {
            throw new IndexOutOfBoundsException("0 <= length" + ", Given : " + length);
        }
        if (0 > offset || offset + length > dst.length) {
            throw new IndexOutOfBoundsException("0 <= offset + length <= " + dst.length
                    + ", Given : " + (offset + length));
        }
        int pos = mDb.position();
        mDb.position(mHead);
        mDb.get(dst, offset, length);
        mDb.position(pos);
    }

    @Override
    public double set(int location, double d) throws IndexOutOfBoundsException {
        double current = peek(location);
        int pos = mDb.position();
        mDb.position(location + mHead);
        mDb.put(d);
        mDb.position(pos);
        return current;
    }

    @Override
    public int size() {
        return mDb.limit() - mDb.remaining() - mHead;
    }

    protected void downShift() {
        int size = size();
        mDb.position(mHead);
        mHead = 0;
        if (0 < size) {
            double[] dst = new double[size];
            mDb.get(dst);
            mDb.position(mHead);
            mDb.put(dst);
        } else {
            mDb.position(mHead);
        }
    }
}
