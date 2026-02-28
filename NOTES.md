# COMP 251 Final Assignment — Project Notes

> **Group 12** | Java 19 | Maven | JUnit 5

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [File Structure](#2-file-structure)
3. [Data Model](#3-data-model)
4. [McMetro — Main Class](#4-mcmetro--main-class)
   - [Constructor](#41-constructor)
   - [maxPassengers — Edmonds-Karp Max Flow](#42-maxpassengers--edmonds-karp-max-flow)
   - [bestMetroSystem — Modified Kruskal MST](#43-bestmetrosystem--modified-kruskal-mst)
   - [Passenger Trie — add & search](#44-passenger-trie--add--search)
   - [hireTicketCheckers — Greedy Interval Scheduling](#45-hireticketcheckers--greedy-interval-scheduling)
5. [NaiveDisjointSet — Union-Find](#5-naivedisjointset--union-find)
6. [Internal Flow Graph](#6-internal-flow-graph)
7. [Algorithm Complexity Summary](#7-algorithm-complexity-summary)
8. [Key Design Decisions](#8-key-design-decisions)
9. [Test Utilities Cheat Sheet](#9-test-utilities-cheat-sheet)

---

## 1. Project Overview

The project simulates a **metro (subway) network management system** called *McMetro*. It models:
- **Buildings** as nodes with a fixed number of occupants.
- **Tracks** as directed edges between buildings with a capacity and a cost.

Four core problems are solved:

| Method | Problem Type | Algorithm |
|---|---|---|
| `maxPassengers` | Maximum flow | Edmonds-Karp (BFS-based Ford-Fulkerson) |
| `bestMetroSystem` | Optimal spanning tree | Modified Kruskal (greedy by capacity/cost ratio) |
| `addPassenger` / `searchForPassengers` | Prefix search | Trie (prefix tree) |
| `hireTicketCheckers` | Interval scheduling | Greedy (sort by end time) |

---

## 2. File Structure

```
Project_comp251/
├── pom.xml                        ← Maven build config (Java 19, JUnit 5.10.2)
└── src/
    ├── main/java/
    │   ├── McMetro.java           ← Main class with all 4 methods
    │   ├── NaiveDisjointSet.java  ← Generic Union-Find (path compression + union by size)
    │   ├── Building.java          ← Record: (BuildingID id, int occupants)
    │   ├── BuildingID.java        ← Record: (int buildingID) — Serializable, Comparable
    │   ├── Track.java             ← Record: (TrackID id, start, end, cost, capacity)
    │   └── TrackID.java           ← Record: (int trackId) — Comparable
    └── test/java/
        ├── McMetroTest.java       ← Full JUnit 5 test suite
        └── TestUtils.java         ← Helper builders for tests
```

---

## 3. Data Model

### `BuildingID`
```java
record BuildingID(int buildingID) implements Comparable<BuildingID>, Serializable
```
- Wraps a plain `int`. Sorted numerically via `compareTo`.

### `Building`
```java
record Building(BuildingID id, int occupants) implements Comparable<Building>
```
- A node in the metro graph.
- `occupants` acts as a **capacity constraint** on any track touching this building.

### `TrackID`
```java
record TrackID(int trackId) implements Comparable<TrackID>
```
- Wraps a plain `int`.

### `Track`
```java
record Track(TrackID id, BuildingID startBuildingId, BuildingID endBuildingId, int cost, int capacity)
```
- A directed edge in the metro graph.
- `capacity` = max passengers the track can carry.
- `cost` = monetary cost of the track.
- **Effective capacity** is `min(track.capacity, start.occupants, end.occupants)`.

---

## 4. McMetro — Main Class

### Fields

```java
protected Track[] tracks;
protected HashMap<BuildingID, Building> buildingTable;  // id → Building

private HashMap<BuildingID, Integer> buildingIndex;     // id → array index (for flow graph)
private ArrayList<FlowEdge>[] flowGraph;                // adjacency list (residual graph)
private int numBuildings;

private Trie trie;                                      // for passenger name search
```

---

### 4.1 Constructor

```
McMetro(Track[] tracks, Building[] buildings)
```

**Steps:**
1. Populate `buildingTable` (id → Building) and `buildingIndex` (id → int index).
2. Allocate `flowGraph` as an array of `ArrayList<FlowEdge>` of size `numBuildings`.
3. For each `Track`:
   - Compute **effective capacity** = `min(track.capacity, start.occupants, end.occupants)`.
   - Add a **directed** forward edge + a zero-capacity reverse edge (residual graph setup).

> **Note:** The graph is directed. Each `Track` becomes one forward edge, not bidirectional.

---

### 4.2 `maxPassengers` — Edmonds-Karp Max Flow

```java
int maxPassengers(BuildingID start, BuildingID end)
```

**Returns** the maximum number of passengers that can travel from `start` to `end`.

#### Algorithm: Edmonds-Karp

Edmonds-Karp is **Ford-Fulkerson using BFS** to find augmenting paths (guarantees polynomial time).

**Steps:**
1. Validate that both buildings exist and have `occupants > 0`.
2. Handle `start == end` edge case: return sum of self-loop capacities.
3. **Copy the flow graph** (so the original is not mutated between calls).
4. Repeat until no augmenting path exists:
   a. **BFS** from `s` to `t` → find a path with remaining capacity.
   b. **Find bottleneck**: minimum residual capacity along the path.
   c. **Augment**: subtract bottleneck from forward edges, add to reverse edges.
5. Return total flow accumulated.

#### BFS (`bfs` method)

```
parentV[node] = which node we came from
parentE[node] = which edge index in that parent's adjacency list
```
- Uses a `Queue<Integer>`.
- Visits only edges with `cap > 0`.
- Terminates early when `t` is reached.

#### Residual Graph Trick

Every edge `u → v` with capacity `cap` is paired with a reverse edge `v → u` with capacity `0`.
When flow is pushed, the forward edge decreases and the reverse edge increases.
This allows the algorithm to "undo" flow choices.

```
addFlowEdge(u, v, cap):
    fwd = FlowEdge(to=v, rev=flowGraph[v].size(), cap=cap)
    rev = FlowEdge(to=u, rev=flowGraph[u].size(), cap=0)
    flowGraph[u].add(fwd)
    flowGraph[v].add(rev)
```

---

### 4.3 `bestMetroSystem` — Modified Kruskal MST

```java
TrackID[] bestMetroSystem()
```

**Returns** a set of `TrackID`s forming a spanning tree that connects all buildings while **maximizing total network capacity per cost** (a modified MST).

#### Algorithm: Modified Kruskal's

Standard Kruskal builds a minimum spanning tree by edge weight. Here, edges are sorted by a **capacity-to-cost ratio** (descending) instead.

**Effective capacity** of a track = `min(track.capacity, start.occupants, end.occupants)`.

**Sorting criterion (descending goodness):**
```
goodness = effectiveCapacity / cost
```
Because we want to **maximize** this ratio, we sort tracks so the best ratio comes first.

**Comparator logic (cross-multiply to avoid floats):**
```
Compare B vs A: numB * costA  vs  numA * costB
```
Tie-breaks (in order):
1. Higher raw track capacity first.
2. Lower cost first.
3. TrackID lexicographic order (deterministic).

**Kruskal loop:**
```
for each track t (sorted best → worst):
    if find(t.start) ≠ find(t.end):
        union(t.start, t.end)
        add t.id to MST
        if MST has (numBuildings - 1) edges: break
```

Uses `NaiveDisjointSet<BuildingID>` for union-find.

---

### 4.4 Passenger Trie — add & search

```java
void addPassenger(String name)
ArrayList<String> searchForPassengers(String firstLetters)
```

**Uses a Trie (prefix tree)** stored as a nested private class.

#### `Trie` inner class

```
Node:
    TreeMap<Character, Node> child   ← sorted map → guarantees lexicographic DFS
    boolean end                       ← marks end of a valid name
```

#### `addPassenger(name)`
1. Convert name to **lowercase**.
2. Walk/create nodes character by character.
3. Set `end = true` on the last node (deduplication is free — same name hits the same path).

#### `searchForPassengers(prefix)`
1. Convert prefix to **lowercase**.
2. Walk down the trie along the prefix. Return empty list if prefix not found.
3. **DFS** from the node at the end of the prefix.
4. Because `child` is a `TreeMap`, DFS visits children in alphabetical order → results are **lexicographically sorted**.
5. Capitalize the first letter before returning (`capFirst`).

---

### 4.5 `hireTicketCheckers` — Greedy Interval Scheduling

```java
static int hireTicketCheckers(int[][] schedule)
```

**Returns** the minimum number of non-overlapping time intervals needed to cover all shifts (i.e., maximum number of non-overlapping intervals selectable = minimum checkers needed to check all without overlap conflicts).

> **Problem type:** Activity Selection — classic greedy.

#### Algorithm

1. Sort intervals by **end time** (ascending). Tie-break by start time.
2. Greedily select an interval if its **start ≥ lastEnd** (touching endpoints are allowed).
3. Count selected intervals.

```java
Arrays.sort(schedule, (a, b) -> a[1] != b[1] ? a[1] - b[1] : a[0] - b[0]);

int count = 0, lastEnd = Integer.MIN_VALUE;
for (int[] iv : schedule) {
    if (iv[0] >= lastEnd) {
        count++;
        lastEnd = iv[1];
    }
}
return count;
```

---

## 5. NaiveDisjointSet — Union-Find

```java
public class NaiveDisjointSet<T>
```

Generic union-find with two optimizations:
- **Path compression** in `find` (every node points directly to root after lookup).
- **Union by size** (smaller set attaches under larger set).

### Methods

| Method | Description |
|---|---|
| `add(T element)` | Initialize element as its own root, size = 1 |
| `find(T a)` | Return root of `a`'s component; auto-adds `a` if missing; compresses path |
| `union(T a, T b)` | Merge sets of `a` and `b`; smaller under larger |

### Internal State

```java
HashMap<T, T> parentMap;   // element → parent (root points to itself)
HashMap<T, Integer> sizeMap; // root → size of its component
```

> `sizeMap` only stores entries for **roots**. When a root is merged under another, its entry is removed.

---

## 6. Internal Flow Graph

### `FlowEdge` (private static class)

```java
class FlowEdge {
    int to;   // destination node index
    int rev;  // index of the reverse edge in flowGraph[to]
    int cap;  // remaining capacity
}
```

### `addFlowEdge(u, v, cap)`

Adds a matched pair:
- `flowGraph[u]` ← forward edge to `v` with capacity `cap`
- `flowGraph[v]` ← reverse edge to `u` with capacity `0`

The `rev` field stores the index of the partner edge, enabling O(1) reverse edge access.

### `copyFlowGraph()`

Returns a deep copy of the flow graph so that `edmondsKarp` does not mutate the stored graph. This allows calling `maxPassengers` multiple times with different source/sink pairs.

---

## 7. Algorithm Complexity Summary

| Method | Time Complexity | Space Complexity | Notes |
|---|---|---|---|
| Constructor | O(V + E) | O(V + E) | V = buildings, E = tracks |
| `maxPassengers` | O(V · E²) | O(V + E) | Edmonds-Karp worst case |
| `bestMetroSystem` | O(E log E + E · α(V)) | O(V + E) | Sort + Kruskal with path compression |
| `addPassenger` | O(L) | O(L) | L = name length |
| `searchForPassengers` | O(P + R) | O(R) | P = prefix walk, R = results |
| `hireTicketCheckers` | O(n log n) | O(1) | Sorting dominates |
| `NaiveDisjointSet.find` | O(α(n)) amortized | — | Path compression |
| `NaiveDisjointSet.union` | O(α(n)) amortized | — | Union by size |

> α = inverse Ackermann function (effectively constant).

---

## 8. Key Design Decisions

### Effective Capacity = `min(track, start, end)`
A track can only carry as many passengers as its own capacity **and** as many as either endpoint building can supply/receive. This constraint is baked into the flow graph at construction time.

### Directed Flow Graph
Tracks are one-directional (`startBuilding → endBuilding`). The graph is not automatically made bidirectional.

### Graph Copy per `maxPassengers` Call
`edmondsKarp` works on a **copy** of the graph so:
- The original `flowGraph` stays intact.
- Multiple calls with different source/sink pairs all start from the original capacities.

### Trie Uses `TreeMap` for Lexicographic Order
By using `TreeMap<Character, Node>` instead of `HashMap`, the DFS over children naturally visits nodes in alphabetical order, giving sorted results without a post-sort step.

### MST Cross-Multiply Comparator (No Floating Point)
To compare ratios `capA/costA` vs `capB/costB` without floating-point errors:
```
capB * costA  vs  capA * costB
```
Values are cast to `long` to avoid integer overflow.

### `hireTicketCheckers` — Touching Endpoints Allowed
The condition `start >= lastEnd` (not `> lastEnd`) means two intervals that share an endpoint (e.g., `[1,3]` and `[3,5]`) are considered non-overlapping and both get selected.

---

## 9. Test Utilities Cheat Sheet

### `TestUtils.maxPassengersBuilder(String tracksString)`
Parses a string like `"[1,2,5][2,3,3]"` into `Track[]` + `Building[]` where all buildings have `Integer.MAX_VALUE` occupants (so building capacity is not the bottleneck; only track capacity matters).

### `TestUtils.bestMetroBuilder(String tracksString, int seed)`
More complex builder. Uses a `goodness` parameter (3rd field per track) and a seeded `Random` to generate consistent building occupants and track capacities that respect the goodness ratio.

### `TestUtils.checkPassengerSearch(passengers[], expected[], prefix)`
Builds a minimal metro, adds passengers, calls `searchForPassengers`, and asserts the result matches `expected` with `assertArrayEquals`.

### `TestUtils.testHiring(String intervalsString, int expected)`
Parses `"[1,3][2,5]"` into a `int[][]` schedule and asserts `hireTicketCheckers` returns `expected`.

### `TestUtils.trackIdsEqual(int[] actual, TrackID[] expected)`
Converts both to `HashSet<TrackID>` and asserts equality (order-independent comparison for MST tests).

---

*End of notes — COMP 251 Final Assignment, Group 12*
