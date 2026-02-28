# COMP 251 Final Assignment — Study Notes

> **Group 12** | Java 19 | Maven | JUnit 5

---

## Quick Reference

| Method | Algorithm | Keyword to remember | Complexity |
|---|---|---|---|
| `maxPassengers` | Edmonds-Karp | **BFS + Residual Graph** | O(V·E²) |
| `bestMetroSystem` | Modified Kruskal | **Sort by ratio → MST** | O(E log E) |
| `addPassenger` / `searchForPassengers` | Trie | **Prefix Tree + DFS** | O(L) / O(P+R) |
| `hireTicketCheckers` | Greedy Activity Selection | **Sort by end time** | O(n log n) |
| `NaiveDisjointSet` | Union-Find | **Path compress + Size** | O(α(n)) ≈ O(1) |

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Data Model](#2-data-model)
3. [Algorithm 1 — maxPassengers (Edmonds-Karp)](#3-algorithm-1--maxpassengers)
4. [Algorithm 2 — bestMetroSystem (Kruskal)](#4-algorithm-2--bestmetrosystem)
5. [Algorithm 3 — Passenger Trie](#5-algorithm-3--passenger-trie)
6. [Algorithm 4 — hireTicketCheckers](#6-algorithm-4--hireticketcheckers)
7. [NaiveDisjointSet — Union-Find](#7-naivedisjointset--union-find)
8. [File Structure](#8-file-structure)
9. [Complexity Summary](#9-complexity-summary)
10. [Test Utilities Cheat Sheet](#10-test-utilities-cheat-sheet)

---

## 1. Project Overview

A **metro network** where:
- `Building` = a station node with a number of `occupants`
- `Track` = a directed edge with `capacity` (max passengers) and `cost`
- **Effective capacity** of any track = `min(track.cap, start.occupants, end.occupants)`

```
    [A: 50]  ──track(cap=30,cost=2)──>  [B: 40]  ──track(cap=20,cost=1)──>  [C: 60]
      50 occ                               40 occ                               60 occ
                effective cap = min(30,50,40) = 30        effective cap = min(20,40,60) = 20
```

The `McMetro` constructor **builds a residual flow graph** from all tracks at startup.

---

## 2. Data Model

```
BuildingID(int)  ──wraps──>  just an int ID, Comparable + Serializable
Building(id, occupants)      node in the graph; occupants = capacity constraint
TrackID(int)                 wraps an int ID
Track(id, startId, endId, cost, capacity)   directed edge
```

**Key formula used everywhere:**
```
effectiveCap = min(track.capacity,  start.occupants,  end.occupants)
                        ^                  ^                  ^
                  track limit       source limit        sink limit
```

---

## 3. Algorithm 1 — `maxPassengers`

> **One-liner:** Find how much water can flow from S to T through pipes with limited widths.

### What it solves
Maximum flow from building `start` to building `end` through the directed track network.

### Algorithm: Edmonds-Karp (Ford-Fulkerson with BFS)

```
SHORTCUT TO REMEMBER:
  1. BFS → find ANY path S→T with leftover capacity
  2. Push as much flow as the bottleneck allows
  3. Update residual edges (reverse edges absorb the flow)
  4. Repeat until no path exists
  5. Total pushed = max flow
```

---

### Visual Walkthrough

**Initial graph** (S=A, T=D):

```
        10          10
  A ────────> B ────────> D
  |                       ^
  |    5                  |
  └──────────> C ─────────┘
                    10
```

**Iteration 1 — BFS finds path A→B→D (bottleneck = 10):**

```
  Before:  A→B cap=10,  B→D cap=10
  Push 10: A→B cap= 0,  B→D cap= 0
  Reverse: B→A cap=10,  D→B cap=10   ← residual edges gain capacity

  Flow so far: 10
```

**Iteration 2 — BFS finds path A→C→D (bottleneck = 5):**

```
  Before:  A→C cap=5,  C→D cap=10
  Push 5:  A→C cap=0,  C→D cap= 5
  Reverse: C→A cap=5,  D→C cap= 5

  Flow so far: 15
```

**Iteration 3 — BFS finds no path (A→B is saturated, A→C is saturated)**

```
  Total max flow = 15  ✓
```

---

### Residual Graph Trick

```
When you add edge u→v (cap=C):
  ┌─────────────────────────────────────────────┐
  │  u ──[cap=C, rev=idx]──> v                  │
  │  v ──[cap=0, rev=idx]──> u  (reverse edge)  │
  └─────────────────────────────────────────────┘

When you push flow f along u→v:
  forward edge:  cap -= f   (less room left)
  reverse edge:  cap += f   (allows "undoing" this flow later)
```

The `rev` field is the index of the partner edge — O(1) lookup to find the reverse.

---

### Graph is Copied per Call

```
flowGraph (original, never mutated)
     │
     └──copyFlowGraph()──> working copy
                                │
                            edmondsKarp() runs on this copy
                            ← original stays clean for next call
```

---

### Edge Case

| Situation | Behaviour |
|---|---|
| `start` or `end` missing | return 0 |
| building has 0 occupants | return 0 |
| `start == end` | return sum of self-loop capacities |

---

## 4. Algorithm 2 — `bestMetroSystem`

> **One-liner:** Pick the cheapest set of tracks that connects all buildings, but "cheap" means best capacity-per-dollar.

### What it solves
Build a spanning tree of all buildings that **maximizes total capacity-to-cost ratio** (modified MST).

### Algorithm: Modified Kruskal's

```
SHORTCUT TO REMEMBER:
  1. Compute "goodness" = effectiveCap / cost  for each track
  2. Sort tracks by goodness  DESCENDING  (best first)
  3. Greedily add a track if it connects two different components
  4. Stop when you have (numBuildings - 1) tracks
```

---

### Visual Walkthrough

**3 buildings (A, B, C), 3 tracks:**

```
        Track T1           Track T2            Track T3
   A ──(cap=10,cost=2)──> B ──(cap=6,cost=1)──> C
   └──────────────────────────(cap=4,cost=1)─────┘
```

**Step 1 — compute goodness:**

```
  T1: effectiveCap=10, cost=2  → goodness = 10/2 = 5.0
  T2: effectiveCap= 6, cost=1  → goodness =  6/1 = 6.0  ← best
  T3: effectiveCap= 4, cost=1  → goodness =  4/1 = 4.0
```

**Step 2 — sort descending: T2, T1, T3**

**Step 3 — Kruskal loop:**

```
  Try T2 (B→C):  find(B)=B, find(C)=C → different → UNION  ✓  MST={T2}
  Try T1 (A→B):  find(A)=A, find(B)=B → different → UNION  ✓  MST={T2,T1}
  numBuildings-1 = 2 edges reached → STOP
```

**Result:** MST = {T1, T2}

---

### Cross-Multiply Comparator (No Float Division!)

```
Want to compare:  capA/costA  vs  capB/costB

Multiply both sides by costA * costB  (always positive):
  capA * costB  vs  capB * costA

Cast to long to avoid int overflow.

Code:
  long left  = numB * (long) a.cost();   // capB * costA
  long right = numA * (long) b.cost();   // capA * costB
  return Long.compare(left, right);      // descending: bigger ratio first
```

---

### Tie-Break Order

```
  1. Higher raw track capacity  (more throughput)
  2. Lower cost                 (cheaper)
  3. TrackID order              (deterministic, for tests)
```

---

### Union-Find Integration

```
  NaiveDisjointSet<BuildingID> ds

  For each building → ds.add(building)
  For each chosen track → ds.union(start, end)
  Cycle check:  if ds.find(start) == ds.find(end) → skip (would create a cycle)
```

---

## 5. Algorithm 3 — Passenger Trie

> **One-liner:** Store names in a tree of letters; walk down to a prefix, then collect all names below.

### What it solves
- `addPassenger(name)` — insert a name (case-insensitive, deduped)
- `searchForPassengers(prefix)` — return all stored names starting with prefix, **sorted alphabetically**

---

### Trie Structure

```
Names added: "Alice", "Alex", "Bob", "Alan"

            [root]
           /      \
          a        b
         /          \
        l            o
       / \            \
      i   e            b*   ← * = end of name "Bob"
     /     \
    c*      x*         ← * = end of "Alice", "Alex"
   (Alice) (Alex)
  /
(Alan would branch off 'l' → 'a' → 'n'*)

Full trie for a, al, ala, ale, ali:
  root → a → l → [a → n*]   "Alan"
                 [e → x*]   "Alex"
                 [i → c → e*] "Alice"
         b → o → b*          "Bob"
```

**`TreeMap` children** = alphabetical order guaranteed (a < b < c...)

---

### Add a Passenger

```
addPassenger("Alice"):
  lowercase → "alice"
  root → 'a' → 'l' → 'i' → 'c' → 'e'
                                     └── end = true  (marks valid name)

  Same name added twice? Same path → end = true again → no duplicate
```

---

### Search for Passengers

```
searchForPassengers("al"):
  1. Walk: root → 'a' → 'l'   (prefix found)
  2. DFS from 'l' node, collect all end=true nodes:
       'l' → 'a' → 'n'*  → "Alan"
           → 'e' → 'x'*  → "Alex"
           → 'i' → 'c' → 'e'* → "Alice"
  3. Capitalize first letter → ["Alan", "Alex", "Alice"]
  4. Already sorted because TreeMap visits 'a' < 'e' < 'i'
```

---

### Shortcut

```
ADD:   lowercase → walk/create nodes → mark last as end
FIND:  lowercase → walk to prefix node → DFS → capitalize → return list
KEY:   TreeMap children = free alphabetical sort
```

---

## 6. Algorithm 4 — `hireTicketCheckers`

> **One-liner:** Given shifts on a timeline, find the max number that fit without overlapping.

### What it solves
`static int hireTicketCheckers(int[][] schedule)` — each `[start, end]` is a shift. Returns how many non-overlapping shifts can be selected.

---

### Visual Walkthrough

**Shifts (before sort):**

```
  Timeline:  0──1──2──3──4──5──6──7──8──9──10
  A          [1──────4]
  B                [3──────6]
  C                      [5──────8]
  D             [2──3]
  E                            [7─────10]
```

**Step 1 — sort by end time:**

```
  D: [2,3]   end=3  ← first
  A: [1,4]   end=4
  B: [3,6]   end=6
  C: [5,8]   end=8
  E: [7,10]  end=10
```

**Step 2 — greedy selection (start >= lastEnd):**

```
  lastEnd = -∞

  D [2,3]:  2 >= -∞ → SELECT ✓   lastEnd = 3   count=1
  A [1,4]:  1 >= 3?  NO → skip
  B [3,6]:  3 >= 3 → SELECT ✓   lastEnd = 6   count=2   (touching ok!)
  C [5,8]:  5 >= 6?  NO → skip
  E [7,10]: 7 >= 6 → SELECT ✓   lastEnd = 10  count=3

  Answer: 3
```

**On the timeline:**

```
  0──1──2──3──4──5──6──7──8──9──10
        [D=2,3]
              [B=3,6]
                    [E=7,10]
```

---

### Shortcut

```
SORT by end time (ascending)
PICK if start >= lastEnd   ← "touching is OK"
COUNT picks = answer
```

> **Why sort by end time?** Finishing early leaves maximum room for future shifts. This is the classic greedy proof.

---

## 7. `NaiveDisjointSet` — Union-Find

> **One-liner:** Track which buildings are already connected; merge groups efficiently.

### Two Optimizations

```
1. PATH COMPRESSION (in find):
   Instead of following a long chain to the root,
   make every node point DIRECTLY to the root after find.

   Before:  A → B → C → D (root)
   After:   A → D,  B → D,  C → D   ← all point to root directly

2. UNION BY SIZE:
   Always attach the SMALLER group under the LARGER group.
   This keeps trees shallow.

   Group {A,B,C} size=3  +  Group {D,E} size=2
   → attach D,E under A,B,C (not the other way)
```

---

### Visual

```
BEFORE union({A,B,C}, {D,E}):
    A          D
   / \          \
  B   C          E

AFTER union by size (C is root of left, D is root of right):
    A
   /|\
  B  C  D
          \
           E
```

---

### Path Compression in Action

```
find(E):
  E → D → A (root)

  After compression:
  E → A   (direct)
  D → A   (direct, already was)
```

---

### State

```java
HashMap<T, T>       parentMap   // node → parent  (root points to itself)
HashMap<T, Integer> sizeMap     // only roots have entries here
```

---

### Shortcut

```
add(x)     → parentMap[x]=x, sizeMap[x]=1
find(x)    → follow parents to root, compress path on the way back
union(a,b) → find roots, attach smaller under bigger, remove smaller from sizeMap
```

---

## 8. File Structure

```
Project_comp251/
├── pom.xml                        ← Java 19, JUnit 5.10.2
└── src/
    ├── main/java/
    │   ├── McMetro.java           ← All 4 algorithms + FlowEdge + Trie (inner classes)
    │   ├── NaiveDisjointSet.java  ← Generic Union-Find
    │   ├── Building.java          ← record(id, occupants)
    │   ├── BuildingID.java        ← record(int)  Serializable + Comparable
    │   ├── Track.java             ← record(id, start, end, cost, capacity)
    │   └── TrackID.java           ← record(int)  Comparable
    └── test/java/
        ├── McMetroTest.java       ← Full JUnit 5 test suite
        └── TestUtils.java         ← String-based graph builders for tests
```

---

## 9. Complexity Summary

| Method | Time | Space | Bottleneck |
|---|---|---|---|
| Constructor | O(V + E) | O(V + E) | Building all adjacency lists |
| `maxPassengers` | O(V · E²) | O(V + E) | Edmonds-Karp worst case |
| `bestMetroSystem` | O(E log E) | O(V + E) | Sorting edges |
| `addPassenger` | O(L) | O(L) | L = name length |
| `searchForPassengers` | O(P + R) | O(R) | P = prefix, R = result count |
| `hireTicketCheckers` | O(n log n) | O(1) | Sorting intervals |
| `find` (Union-Find) | O(α(n)) ≈ O(1) | — | Path compression |
| `union` (Union-Find) | O(α(n)) ≈ O(1) | — | Union by size |

> **α(n)** = inverse Ackermann — grows so slowly it is effectively constant for any realistic n.

---

## 10. Test Utilities Cheat Sheet

### String format for graphs

```
"[startId, endId, cap]  [startId, endId, cap] ..."

Example:  "[1,2,10][2,3,5][1,3,3]"
```

### `maxPassengersBuilder(tracksString)`
Builds a graph where all buildings have `Integer.MAX_VALUE` occupants.
→ Only track capacity matters (used to test pure max-flow).

### `bestMetroBuilder(tracksString, seed)`
3rd field is `goodness` (capacity/cost ratio target), not raw capacity.
Uses seeded `Random` for reproducible test data.

### `checkPassengerSearch(passengers[], expected[], prefix)`
Adds names → calls `searchForPassengers(prefix)` → asserts result matches `expected[]` in order.

### `testHiring(intervalsString, expected)`
Parses `"[1,3][2,5]"` → calls `hireTicketCheckers` → asserts count.

### `trackIdsEqual(int[] actual, TrackID[] expected)`
Order-insensitive MST check — converts both to `HashSet<TrackID>` and asserts equality.

---

## Algorithm Decision Tree

```
Need to find max flow between two nodes?
  └─> maxPassengers()  →  Edmonds-Karp (BFS augmenting paths)

Need to connect all nodes, best capacity per cost?
  └─> bestMetroSystem()  →  Modified Kruskal (sort by ratio, union-find cycle check)

Need to store/search names by prefix?
  └─> Trie  →  addPassenger / searchForPassengers (DFS, TreeMap = sorted)

Need max non-overlapping intervals?
  └─> hireTicketCheckers()  →  Greedy (sort by end time, pick if start >= lastEnd)

Need to check if two nodes are in the same component?
  └─> NaiveDisjointSet.find()  →  Union-Find (path compression + union by size)
```

---

*COMP 251 Final Assignment — Group 12*