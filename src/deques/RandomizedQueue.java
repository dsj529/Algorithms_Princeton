import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] itemArr;
    private int size;

    // construct an empty randomized queue
    public RandomizedQueue() {
        itemArr = (Item[]) new Object[2];
        size = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    private void resize(int capacity) {
        if (capacity < size) {
            throw new UnsupportedOperationException();
        }
        Item[] newArr = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            newArr[i] = itemArr[i];
        }
        itemArr = newArr;

    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Null Item cannot be added");
        }
        if (size == itemArr.length) {
            resize(2 * itemArr.length);
        }
        itemArr[size++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Data Underflow");
        }
        int i = StdRandom.uniform(size);
        Item out = itemArr[i];
        itemArr[i] = itemArr[--size];
        itemArr[size] = null;

        if (size > 0 && size <= itemArr.length / 4) {
            resize(itemArr.length / 2);
        }

        return out;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException("Data Underflow");
        }
        int i = StdRandom.uniform(size);
        Item out = itemArr[i];
        return out;

    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomQueueIterator();
    }

    private class RandomQueueIterator implements Iterator<Item> {
        private int s = size;
        private Item[] randomized;

        public RandomQueueIterator() {
            randomized = (Item[]) new Object[size];
            for (int i = 0; i < size; i++) {
                randomized[i] = itemArr[i];
            }
            StdRandom.shuffle(randomized);
        }

        public boolean hasNext() {
            return s > 0;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return randomized[--s];
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        return;
    }

}
