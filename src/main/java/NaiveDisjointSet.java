import java.util.*;

public class NaiveDisjointSet<T> {
    HashMap<T, T> parentMap = new HashMap<>();
    HashMap<T, Integer> sizeMap = new HashMap<>();

    void add(T element) {
        parentMap.put(element, element);
        sizeMap.put(element, 1);
    }

    // find the root of a, and compress the path so future finds are faster
    T find(T a) {
        // if a was never added add it (avoids null issues)
        if (parentMap.containsKey(a) == false) {
            add(a);
        }

        T parent = parentMap.get(a);

        // if a is the root, return it
        if (parent.equals(a)) {
            return a;
        }

        // otherwise, find the root and make a point directly to it
        T root = find(parent);
        parentMap.put(a, root);
        return root;
    }

    // union by size: attach the smaller set under the bigger set
    void union(T a, T b) {
        T rootA = find(a);
        T rootB = find(b);

        // already in the same set
        if (rootA.equals(rootB)) {
            return;
        }

        int sizeA = sizeMap.get(rootA);
        int sizeB = sizeMap.get(rootB);

        // attach smaller to bigger
        if (sizeA < sizeB) {
            parentMap.put(rootA, rootB);
            sizeMap.put(rootB, sizeA + sizeB);
            sizeMap.remove(rootA);
        } else {
            parentMap.put(rootB, rootA);
            sizeMap.put(rootA, sizeA + sizeB);
            sizeMap.remove(rootB);
        }
    }
}
