package com.logtracking.lib.api.config;

import java.util.*;

public class NonModifiableCollections {

    public static <T> List<T> newNonModifiableList(List<T> content){
        return new NonModifiableArrayList<T>(content);
    }

    public static <K,V> Map<K,V> newNonModifiableMap(Map<K,V> content){
        return new NonModifiableHashMap<K,V>(content);
    }

    private static void throwUnsupportedOperationException(){
        throw new UnsupportedOperationException();
    }

    private static final class NonModifiableArrayList<T> extends ArrayList<T> {

        NonModifiableArrayList(List<T> initialList){
            super(initialList);
        }

        @Override
        public void add(int location, T object) {
            throwUnsupportedOperationException();
        }

        @Override
        public boolean add(T object) {
            throwUnsupportedOperationException();
            return false;
        }

        @Override
        public boolean addAll(int location, Collection<? extends T> collection) {
            throwUnsupportedOperationException();
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends T> collection) {
            throwUnsupportedOperationException();
            return false;
        }

        @Override
        public void clear() {
            throwUnsupportedOperationException();
        }

        @Override
        public T remove(int location) {
            throwUnsupportedOperationException();
            return null;
        }

        @Override
        public boolean remove(Object object) {
            throwUnsupportedOperationException();
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            throwUnsupportedOperationException();
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            throwUnsupportedOperationException();
            return false;
        }

        @Override
        public T set(int location, T object) {
            throwUnsupportedOperationException();
            return null;
        }
    }


    private static class NonModifiableHashMap<K,V> extends HashMap<K,V> {

        NonModifiableHashMap(Map<K,V> initialMap){
            super(initialMap);
        }

        @Override
        public void clear() {
            throwUnsupportedOperationException();
        }

        @Override
        public V put(K key, V value) {
            throwUnsupportedOperationException();
            return null;
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            throwUnsupportedOperationException();
        }

        @Override
        public V remove(Object key) {
            throwUnsupportedOperationException();
            return null;
        }
    }
}
