package coupang;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ryan Lee
 * @version $ MapAlgo, v 0.1 2026/7/23 16:58 Ryan Lee Exp $
 * @Description
 */
public class MapAlgo {


    interface Map<K, V> {

        void put(K k, V v);

        V get(K k);

        void remove(K k);

    }

    static class HashMap<K, V> implements Map<K, V> {

        private Node<K, V>[] nodes;

        private int size;

        public int getSize() {
            return size;
        }

        public HashMap() {
            size = 0;
            this.nodes = new Node[16];
        }

        public Node<K,V>[] getNodes() {
            return nodes;
        }

        @Override
        public void put(K k, V v) {
            if (null == k) {
                throw new RuntimeException("key不能为空");
            }

            Node<K, V> kvNode = new Node<>();
            kvNode.setKey(k);
            kvNode.setVal(v);
            int index = getIndex(k);
            Node<K, V> node = nodes[index];
            if (null == node) {
                nodes[index] = kvNode;
                size++;
                return;
            }

            Node<K, V> tmp = node;
            while (true) {

                //覆盖逻辑
                if (tmp.getKey().equals(k)) {
                    tmp.setVal(v);
                    return;
                }
                Node<K, V> next = tmp.getNext();
                //当前最后一个元素
                if (null == next) {
                    tmp.setNext(kvNode);
                    size++;
                    return;
                }
                tmp = next;
            }

        }

        @Override
        public V get(K k) {
            int index = getIndex(k);
            Node<K, V> node = nodes[index];
            if (null == node) {
                return null;
            }

            Node<K, V> tmp = node;
            while (null != tmp) {
                K key = tmp.getKey();
                if (key.equals(k)) {
                    return tmp.getVal();
                }
                tmp = node.getNext();
            }
            return null;
        }

        @Override
        public void remove(K k) {
            int index = getIndex(k);
            Node<K, V> node = nodes[index];
            if (null == node) {
                return;
            }

            if (node.getKey().equals(k)) {
                nodes[index] = node.getNext();
                size--;
                return;
            }

            Node<K, V> tmp = node;
            while (true) {
                Node<K, V> next = tmp.getNext();
                if (null == next) {
                    return;
                }

                if (next.getKey().equals(k)) {
                    tmp.setNext(next.getNext());
                    size--;
                    return;
                }
                tmp = next;
            }
        }


        private int getIndex(K key) {
            int hash = key.hashCode();
            return hash & (this.nodes.length - 1);
        }
    }

    static class Node<K, V> {
        private K key;

        private V val;

        private Node<K, V> next;

        public Node<K, V> getNext() {
            return next;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getVal() {
            return this.val;
        }

        public void setVal(V val) {
            this.val = val;
        }
    }

    public static void main(String[] args) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        map.put("4", 4);
        Node<String, Integer>[] nodes = map.getNodes();
        List<Integer> collect = Arrays.stream(nodes).filter(java.util.Objects::nonNull).map(Node::getVal).collect(Collectors.toList());
        System.out.println(collect);
        System.out.println("size:"+map.getSize());
        map.remove("4");
        System.out.println("size2:"+map.getSize());
        System.out.println(Arrays.stream(nodes).filter(java.util.Objects::nonNull).map(Node::getVal).collect(Collectors.toList()));
        System.out.println("3:" + map.get("3"));
        System.out.println("2:" + map.get("2"));
    }
}
