package app.common;

/**
 * Creates a pair of two objects
 *
 * @param <T1> the type of object 1
 * @param <T2> the type of object 2
 */
public class Pair<T1, T2> {

    /**
     * The number of bits per bytes
     */
    private static final int BITS_PER_BYTES = 8;
    /**
     * The number of bytes in the hash
     */
    private static final int NUMBER_BITS = Integer.BYTES * BITS_PER_BYTES;
    /**
     * The number of bytes in the hash
     */
    private static final int HALF_NUMBER_BITS = NUMBER_BITS / 2;
    /**
     * 0's for the first half of the int, 1's for the second half of the int
     */
    private static final int EMPTY_FULL = (1 << (HALF_NUMBER_BITS + 1)) - 1;
    /**
     * 1's for the first half of the int, 0's for the second half of the int
     */
    private static final int FULL_EMPTY = EMPTY_FULL << HALF_NUMBER_BITS;
    /**
     * The first object
     */
    private final T1 obj1;
    /**
     * The second object
     */
    private final T2 obj2;

    /**
     * Creates a new pair of objects
     *
     * @param first  the first object
     * @param second the second object
     */
    public Pair(T1 first, T2 second) {
        this.obj1 = first;
        this.obj2 = second;
    }

    /**
     * @return the first object
     */
    public T1 getFirst() {
        return this.obj1;
    }

    /**
     * @return the second object
     */
    public T2 getSecond() {
        return this.obj2;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Pair<?, ?>)) {
            return false;
        }
        Pair<?, ?> otherObj = (Pair<?, ?>) other;
        return otherObj.obj1.equals(obj1) && otherObj.obj2.equals(obj2);
    }

    @Override
    public int hashCode() {
        int firstHash = this.obj1.hashCode();
        int first16Bits = (EMPTY_FULL & firstHash) ^ ((FULL_EMPTY & firstHash) >> HALF_NUMBER_BITS);
        int secondHash = this.obj2.hashCode();
        int second16Bits = (EMPTY_FULL & secondHash) ^ ((FULL_EMPTY & secondHash) >> HALF_NUMBER_BITS);
        return (first16Bits << HALF_NUMBER_BITS) | second16Bits;
    }

    @Override
    public String toString() {
        return String.format("Pair<%s, %s>", this.obj1.toString(),
                this.obj2.toString());
    }
}

