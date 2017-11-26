/**
 * Pair class.
 * @author Henrik
 * 11/5/2017
 */
public class Pair<K,V> {
    protected K key;
    protected V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return Key in pair.
     */
    public K getKey() {
        return key;
    }

    /**
     * @return Value in pair.
     */
    public V getValue() {
        return value;
    }
}
