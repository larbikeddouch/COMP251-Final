import java.util.*;
import java.lang.Math.*;

public class McMetro {
    protected Track[] tracks;
    protected HashMap<BuildingID, Building> buildingTable = new HashMap<>();

    // maxPassengers (max-flow)
    private HashMap<BuildingID, Integer> buildingIndex = new HashMap<>();
    private ArrayList<FlowEdge>[] flowGraph;
    private int numBuildings;

    // for passengers
    private Trie trie = new Trie();

    // You may initialize anything you need in the constructor
    @SuppressWarnings("unchecked")
    McMetro(Track[] tracks, Building[] buildings) {
        this.tracks = tracks;
        this.numBuildings = (buildings == null) ? 0 : buildings.length;

        // Populate buildings table + index
        if (buildings != null) {
            for (int i = 0; i < buildings.length; i++) {
                Building b = buildings[i];
                buildingTable.putIfAbsent(b.id(), b);
                buildingIndex.put(b.id(), i);
            }
        }

        // Build flow graph (directed)
        flowGraph = new ArrayList[numBuildings];
        for (int i = 0; i < numBuildings; i++) flowGraph[i] = new ArrayList<>();

        if (tracks != null) {
            for (Track t : tracks) {
                Building a = buildingTable.get(t.startBuildingId());
                Building b = buildingTable.get(t.endBuildingId());
                if (a == null || b == null) continue;

                int cap = Math.min(t.capacity(), Math.min(a.occupants(), b.occupants()));
                int u = buildingIndex.get(t.startBuildingId());
                int v = buildingIndex.get(t.endBuildingId());

                // add directed edge u -> v
                addFlowEdge(u, v, cap);
            }
        }
    }

    // Maximum number of passengers that can be transported from start to end
    int maxPassengers(BuildingID start, BuildingID end) {
        Building bs = buildingTable.get(start);
        Building be = buildingTable.get(end);
        if (bs == null || be == null) return 0;

        if (bs.occupants() == 0 || be.occupants() == 0) return 0;

        int s = buildingIndex.get(start);
        int t = buildingIndex.get(end);

        // Special case: same building -> sum self-loop capacities
        if (s == t) {
            int total = 0;
            for (FlowEdge e : flowGraph[s]) {
                if (e.to == s) total += e.cap;
            }
            return total;
        }

        // Edmonds-Karp (BFS augmenting paths)
        return edmondsKarp(s, t);
    }

    // Returns a list of trackIDs that connect to every building maximizing total network capacity taking cost into account
    TrackID[] bestMetroSystem() {
        Track[] sorted = Arrays.copyOf(tracks, tracks.length);

        Arrays.sort(sorted, (a, b) -> {
            long numA = numerator(a);
            long numB = numerator(b);

            // compare numB/b.cost vs numA/a.cost (descending)
            long left = numB * (long) a.cost();
            long right = numA * (long) b.cost();
            if (left != right) return Long.compare(left, right);

            // tie break 1: bigger raw track capacity first  (fixes your triangle test)
            if (a.capacity() != b.capacity()) return Integer.compare(b.capacity(), a.capacity());

            // Tie break 2: cheaper first (optional)
            if (a.cost() != b.cost()) return Integer.compare(a.cost(), b.cost());

            // tie break 3: trackID (deterministic)
            return a.id().compareTo(b.id());
        });

        NaiveDisjointSet<BuildingID> ds = new NaiveDisjointSet<>();
        for (BuildingID id : buildingTable.keySet()) ds.add(id);

        ArrayList<TrackID> mst = new ArrayList<>();
        int need = Math.max(0, buildingTable.size() - 1);

        for (Track t : sorted) {
            BuildingID u = t.startBuildingId();
            BuildingID v = t.endBuildingId();

            if (!ds.find(u).equals(ds.find(v))) {
                ds.union(u, v);
                mst.add(t.id());
                if (mst.size() == need) break;
            }
        }

        return mst.toArray(new TrackID[0]);
    }

    // Adds a passenger to the system
    void addPassenger(String name) {
        trie.addPassenger(name);
    }

    // Do not change this
    void addPassengers(String[] names) {
        for (String s : names) {
            addPassenger(s);
        }
    }

    // Returns all passengers in the system whose names start with firstLetters
    ArrayList<String> searchForPassengers(String firstLetters) {
        return trie.search(firstLetters);
    }

    // Return how many ticket checkers will be hired
    static int hireTicketCheckers(int[][] schedule) {
        if (schedule == null || schedule.length == 0) return 0;

        Arrays.sort(schedule, (a, b) -> {
            if (a[1] != b[1]) return Integer.compare(a[1], b[1]); // end time
            return Integer.compare(a[0], b[0]);                   // start time tie-break
        });

        int count = 0;
        int lastEnd = Integer.MIN_VALUE;

        for (int[] iv : schedule) {
            int start = iv[0];
            int end = iv[1];
            if (start >= lastEnd) { // endpoints can touch
                count++;
                lastEnd = end;
            }
        }
        return count;
    }

    private long numerator(Track t) {
        Building a = buildingTable.get(t.startBuildingId());
        Building b = buildingTable.get(t.endBuildingId());
        if (a == null || b == null) return 0;
        return (long) Math.min(t.capacity(), Math.min(a.occupants(), b.occupants()));
    }

    // flow edge with reverse pointer (standard residual graph trick)
    private static class FlowEdge {
        int to, rev, cap;
        FlowEdge(int to, int rev, int cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
        }
    }

    private void addFlowEdge(int u, int v, int cap) {
        FlowEdge fwd = new FlowEdge(v, flowGraph[v].size(), cap);
        FlowEdge rev = new FlowEdge(u, flowGraph[u].size(), 0);
        flowGraph[u].add(fwd);
        flowGraph[v].add(rev);
    }

    private int edmondsKarp(int s, int t) {
        // destroy the original graph capacities permanently,
        // so we run on a copied residual graph each call.
        ArrayList<FlowEdge>[] g = copyFlowGraph();

        int flow = 0;
        int[] parentV = new int[numBuildings];
        int[] parentE = new int[numBuildings];

        while (bfs(g, s, t, parentV, parentE)) {
            int add = Integer.MAX_VALUE;

            // find bottleneck
            for (int v = t; v != s; v = parentV[v]) {
                int u = parentV[v];
                FlowEdge e = g[u].get(parentE[v]);
                add = Math.min(add, e.cap);
            }

            // push flow
            for (int v = t; v != s; v = parentV[v]) {
                int u = parentV[v];
                FlowEdge e = g[u].get(parentE[v]);
                e.cap -= add;
                g[v].get(e.rev).cap += add;
            }

            flow += add;
        }

        return flow;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<FlowEdge>[] copyFlowGraph() {
        ArrayList<FlowEdge>[] g = new ArrayList[numBuildings];
        for (int i = 0; i < numBuildings; i++) g[i] = new ArrayList<>();

        for (int u = 0; u < numBuildings; u++) {
            for (FlowEdge e : flowGraph[u]) {
                // copy edge
                g[u].add(new FlowEdge(e.to, e.rev, e.cap));
            }
        }
        return g;
    }

    private boolean bfs(ArrayList<FlowEdge>[] g, int s, int t, int[] parentV, int[] parentE) {
        Arrays.fill(parentV, -1);
        Queue<Integer> q = new LinkedList<>();
        q.add(s);
        parentV[s] = s;

        while (!q.isEmpty()) {
            int u = q.poll();
            for (int i = 0; i < g[u].size(); i++) {
                FlowEdge e = g[u].get(i);
                if (parentV[e.to] == -1 && e.cap > 0) {
                    parentV[e.to] = u;
                    parentE[e.to] = i;
                    if (e.to == t) return true;
                    q.add(e.to);
                }
            }
        }
        return false;
    }

    // --------------------- trie ---------------------

    private static class Trie {
        private static class Node {
            TreeMap<Character, Node> child = new TreeMap<>();
            boolean end;
        }

        private final Node root = new Node();

        void addPassenger(String name) {
            if (name == null || name.isEmpty()) return;

            String s = name.toLowerCase(Locale.ROOT);
            Node cur = root;

            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                cur.child.putIfAbsent(c, new Node());
                cur = cur.child.get(c);
            }
            cur.end = true; // dedupe naturally
        }

        ArrayList<String> search(String prefix) {
            ArrayList<String> res = new ArrayList<>();

            if (prefix == null) prefix = "";
            String p = prefix.toLowerCase(Locale.ROOT);

            Node cur = root;
            for (int i = 0; i < p.length(); i++) {
                char c = p.charAt(i);
                if (!cur.child.containsKey(c)) return res;
                cur = cur.child.get(c);
            }

            // DFS gives lexicographic order with TreeMap
            StringBuilder sb = new StringBuilder(p);
            dfs(cur, sb, res);
            return res;
        }

        private void dfs(Node node, StringBuilder sb, ArrayList<String> res) {
            if (node.end) res.add(capFirst(sb.toString()));
            for (Map.Entry<Character, Node> ent : node.child.entrySet()) {
                sb.append(ent.getKey());
                dfs(ent.getValue(), sb, res);
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        private String capFirst(String s) {
            if (s.isEmpty()) return s;
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
    }
}
