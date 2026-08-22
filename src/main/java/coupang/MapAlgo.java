package coupang;

import java.util.Objects;

/**
 * A simple hash map implementation based on an array and linked lists.
 */
public class MapAlgo {

    interface Map<K, V> {

        V put(K key, V value);

        V get(K key);

        V remove(K key);

        boolean containsKey(K key);

        int size();
    }

    static class HashMap<K, V> implements Map<K, V> {

        private static final int DEFAULT_CAPACITY = 16;
        private static final float LOAD_FACTOR = 0.75F;

        private Node<K, V>[] nodes;
        private int size;
        private int threshold;

        public HashMap() {
            this.nodes = createNodeArray(DEFAULT_CAPACITY);
            this.threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
        }

        /** Kept for compatibility with the original exercise code. */
        public int getSize() {
            return size;
        }

        @Override
        public V put(K key, V value) {
            requireKey(key);
            int index = getIndex(key, nodes.length);
            Node<K, V> current = nodes[index];
            Node<K, V> tail = null;

            while (current != null) {
                if (current.key.equals(key)) {
                    V oldValue = current.value;
                    current.value = value;
                    return oldValue;
                }
                tail = current;
                current = current.next;
            }

            Node<K, V> newNode = new Node<>(key, value);
            if (tail == null) {
                nodes[index] = newNode;
            } else {
                tail.next = newNode;
            }
            size++;
            if (size > threshold) {
                resize();
            }
            return null;
        }

        @Override
        public V get(K key) {
            requireKey(key);
            Node<K, V> node = findNode(key);
            return node == null ? null : node.value;
        }

        @Override
        public V remove(K key) {
            requireKey(key);
            int index = getIndex(key, nodes.length);
            Node<K, V> current = nodes[index];
            Node<K, V> previous = null;

            while (current != null) {
                if (current.key.equals(key)) {
                    if (previous == null) {
                        nodes[index] = current.next;
                    } else {
                        previous.next = current.next;
                    }
                    size--;
                    return current.value;
                }
                previous = current;
                current = current.next;
            }
            return null;
        }

        @Override
        public boolean containsKey(K key) {
            requireKey(key);
            return findNode(key) != null;
        }

        @Override
        public int size() {
            return size;
        }

        private Node<K, V> findNode(K key) {
            int index = getIndex(key, nodes.length);
            Node<K, V> current = nodes[index];
            while (current != null) {
                if (current.key.equals(key)) {
                    return current;
                }
                // Advance from the current node. Using the bucket head here
                // would revisit the same node and can loop forever.
                current = current.next;
            }
            return null;
        }

        private void resize() {
            Node<K, V>[] oldNodes = nodes;
            Node<K, V>[] newNodes = createNodeArray(oldNodes.length << 1);

            for (Node<K, V> node : oldNodes) {
                Node<K, V> current = node;
                while (current != null) {
                    Node<K, V> next = current.next;
                    int index = getIndex(current.key, newNodes.length);
                    current.next = newNodes[index];
                    newNodes[index] = current;
                    current = next;
                }
            }

            nodes = newNodes;
            threshold = (int) (newNodes.length * LOAD_FACTOR);
        }

        private int getIndex(K key, int capacity) {
            int hash = key.hashCode();
            hash ^= hash >>> 16;
            return hash & (capacity - 1);
        }

        private void requireKey(K key) {
            Objects.requireNonNull(key, "key不能为空");
        }

        @SuppressWarnings("unchecked")
        private Node<K, V>[] createNodeArray(int capacity) {
            return (Node<K, V>[]) new Node<?, ?>[capacity];
        }
    }

    private static class Node<K, V> {
        private final K key;
        private V value;
        private Node<K, V> next;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public static void main(String[] args) {
        HashMap<String, Integer> map = new HashMap<>();

        // These keys collide with the Java-8 hash spreading function.
        map.put("1", 1);
        map.put("12", 12);
        map.put("23", 23);
        check(map.size() == 3, "size should be 3");
        check(map.get("1") == 1, "should find the first collision node");
        check(map.get("12") == 12, "should find the middle collision node");
        check(map.get("23") == 23, "should find the last collision node");
        check(map.get("34") == null, "missing key should return null");

        Integer oldValue = map.put("12", 120);
        check(oldValue == 12, "put should return the old value");
        check(map.get("12") == 120, "put should overwrite an existing value");
        check(map.size() == 3, "overwriting should not change size");

        check(map.remove("12") == 120, "remove should return the removed value");
        check(!map.containsKey("12"), "removed key should not exist");
        check(map.size() == 2, "remove should decrease size");
        check(map.remove("1") == 1, "should remove the collision-chain head");
        check(map.remove("23") == 23, "should remove the collision-chain tail");
        check(map.remove("missing") == null, "removing a missing key should return null");
        check(map.size() == 0, "all collision keys should be removed");

        expectNullKeyFailure(() -> map.put(null, 1));
        expectNullKeyFailure(() -> map.get(null));
        expectNullKeyFailure(() -> map.remove(null));

        map.put("nullable", null);
        check(map.containsKey("nullable"), "containsKey should distinguish a null value");
        check(map.get("nullable") == null, "null value should be supported");

        for (int i = 0; i < 1000; i++) {
            map.put("key-" + i, i);
        }
        for (int i = 0; i < 1000; i++) {
            check(map.get("key-" + i) == i, "value missing after resize: " + i);
        }

        System.out.println("All checks passed, size=" + map.getSize());
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectNullKeyFailure(Runnable action) {
        try {
            action.run();
        } catch (NullPointerException expected) {
            return;
        }
        throw new AssertionError("null key should be rejected");
    }
}
