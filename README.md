# pwdIndexer
A super-fast token searcher using m-trees and hash-maps to index characters

# TL;DR

This program ingests a file containing tokens (e.g. password) and indexes each token in order to enable a super-fast, case-sensitive, match-all search within the tokens. A sample file is provided in the project. It contains 3 million passwords (with duplicates). Despite the large number of indexed tokens, each search lasts about few milliseconds on an i5 Intel processor.

The sample file is in the following form.

```
...
hello
foo123###
my$strongestPwd!
HELLO
...
```

# How the application works

This is a console application written in Java8. It reads the token file (provided along with the project) and indexes the tokens one by one. Then it searches some patterns, tests the search speed, and gives the time spent by each search, together with the tokens matching the given pattern. A sample output produced by running the application is the following.

```
Indexing...
0 lines so far...
1000000 lines so far...
2000000 lines so far...
2992150 lines indexed in 3453ms

Now searching...
bastimen (5 found in 23ms): bastimen, bastimentar, bastimentero, bastimenti, bastimento
totor (13 found in 59ms): atotori, epytotor, itotoro, llitotor, totor, totora, totora32, ...
BIK (4 found in 1ms): BIKE'S, BIKKESBAKKER, LEIBIKER, LUSCHBIK
sall (144 found in 45ms): Casalla, Casalle, Gasalla, Moussalli, Vassallo, abdel-sallam, ...
6731 (74 found in 110ms): 00006731, 01196731, 016731, 02196731, 026731, 03196731, 036731, ...
stipula (18 found in 41ms): adstipulate, adstipulation, adstipulator, astipula, astipulate, ...
t3050 (1 found in 27ms): adt30500
ahtkwelotn (0 found in 22ms): 
#$%() (0 found in 0ms):
```

# How indexing works

Token are indexed building a m-tree. Each node tree corresponds to a single character. A node has as many children as the possible distinct following characters. So each existing token has a specific descending path in the tree, with the first character at the first level of the tree. For example, let's suppose to index the following four words:

* top
* toppy
* run
* towel

The index tree after the first word appears as follows.

![Index three after indexing the first token](/docs/first.png?raw=true "Index three after indexing the first token")

After the second word it appears as follows.

![Index three after indexing the second token](/docs/second.png?raw=true "Index three after indexing the second token")

After the third word it appears as follows.

![Index three after indexing the third token](/docs/third.png?raw=true "Index three after indexing the third token")

After the fourth word it appears as follows.

![Index three after indexing the fourth token](/docs/fourth.png?raw=true "Index three after indexing the fourth token")

As one can see, the last character of each token is colored (in the codebase, there is a `last` flag set to true in this case). A colored node corresponds to an existing token. More precisely, climbing up the tree from a colored node until reaching the root node rebuilds the token. For example, climbing up the three from the colored node `y`, gives the sequence `yppot` which, reversed, is the token `toppy`.

Let's continue by indexing one more token: the token `topping`. The index tree after the indexing appears as follows.

![Index three after indexing the token topping](/docs/fifth.png?raw=true "Index three after indexing the token topping")

As in the previous case, the first 4 characters of the indexed token (i.e. `topp`) are already in the tree, and do not instantiate any new node. The remaining part of the token (i.e. `ing`) generates a new tree path under the node `p`.

Starting from the current state of the tree, let's now try to search the pattern `to`. The first letter of the pattern is `t`, and the search starts from the only node in the tree containing this character. The next character is `o`, and it happens to exist within the children of the current node `t`. The pattern is completed and the search found a match in the node `o`, highlighted in yellow in the following picture.

![The mantching node for the pattern 'to'](/docs/sixth.png?raw=true "The mantching node for the pattern 'to'")

Starting from the matching node, a recursive visit of the subtree (enclosed in the dashed line) is started in order to collect all the colored nodes. As we have seen before, for each of them, a match is returned by the search. In this case: top, toppy, and topping.

# Implementation details

During indexing, two additional support data structures are filled. The `rootNodes` map and the `allNodes` map.

## The `rootNodes` map

The `rootNodes` map holds references to all the first level nodes, keyed by the related character.

![The rootNodes map](/docs/rootNodes.png?raw=true "The rootNodes map")

These references allow to find the entry point in the tree, according to the first character of the token to be indexed. For example, the token `toppy` starts with the `top` prefix, which is already indexed. In this case, the nodes are not duplicated in the tree and `toppy` is indexed sharing the first three nodes of the `top` token, and appending the remaining nodes to it (i.e. the `p` and the `y`). This data structure is used only during indexing phase. It is not used during the search phase.

## The `allNodes` map

The `allNodes` map holds references to all the nodes in the tree. Each reference is keyed by a character and contains the list of nodes related to that character in the tree.

![The allNodes map](/docs/allNodes.png?raw=true "The allNodes map")

When a search is requested, the `allNodes` map is read, keyed by the first character of the search pattern. The list of all the tree nodes holding that character is retrieved and the search is started for each such node. For example, in case the pattern is `py`, the `allNodes` map is checked keyed by the `p` character. Two results are found. The first `p` does not have an `y` as a child and then the search stops here. The second `p` happens to have a `y` as child. The search patterns is now complete and then a match is found. Now, the matching node is visited searching for all the colored nodes contained in the subtree rooted in this node. For each colored node found, a search result is returned.

# Notes

The sample password file provided is obtained by concatenating a list of about 1,5 million tokens to itself. This allows to show how indexing the second time the same tokens already indexed gives only a small overhead when compared to indexing new tokens never indexed before.

# License
Source code is released under the terms of MIT license.

# Disclaimer
Use this project at your own risk. The author is not responsible for any damage which might result from this project usage.
