package org.fusesource.restygwt.client.basic;

public abstract class Optional<T> {

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> absent() {
        return (Optional<T>) Absent.INSTANCE;
    }

    public static <T> Optional<T> of(T reference) {
        if (reference == null) {
            throw new RuntimeException();
        }
        return new Present<T>(reference);
    }

    public abstract boolean isPresent();

    public abstract T get();

    private static class Absent extends Optional<Object> {
        static final Absent INSTANCE = new Absent();

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public Object get() {
            throw new IllegalStateException("Absent.get");
        }

        @Override
        public boolean equals(Object object) {
            return object == this;
        }
    }

    private static class Present<T> extends Optional<T> {
        private final T reference;

        Present(T reference) {
            this.reference = reference;
        }

        @Override public boolean isPresent() {
            return true;
        }

        @Override public T get() {
            return reference;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Present) {
                Present<?> other = (Present<?>) object;
                return reference.equals(other.reference);
            }
            return false;
        }
    }

}
