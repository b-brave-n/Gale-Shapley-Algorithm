# Gale-Shapley-Algorithm

The Gale-Shapley algorithm can be modified to accommodate many variations on the Stable Matching Problem. Your task is to implement the following in Java

Forbidden matches: In this variant of the Stable Matching Problem, in addition to the set of men M and women W, there is a set F ⊆ M × W of forbidden pairs that are not allowed to be matched together. The adapted GS algorithm for the forbidden matches is:

<img width="774" height="542" alt="image" src="https://github.com/user-attachments/assets/022b6f83-a017-46a5-81cd-ce518d040b4c" />



Coping with indifference: In this variant of the Stable Matching Problem, ties are allowed in the preference ordering. They are implemented by turning the preference list into a list of sets, where ties are placed into the same set. In your starter code, also implement the function gs_tie(men, women, preftie), where preftie is a dictionary mapping people to a list of sets, for example, of the form:


<img width="665" height="182" alt="Screenshot 2025-10-18 at 7 06 11 PM" src="https://github.com/user-attachments/assets/a17fc351-5b15-440c-9eb5-f2585e319590" />



Here, Xavier strictly prefers Bertha to Amy to Clare, Yancey is indifferent between Amy
and Bertha, but prefers both to Clare, and so on. Suppose that a woman w is already
matched with a man m1, and a new man m2 proposes to w. If m1 and m2 have a tie on
w’s list, then w will reject m2. Rematching is only possible when m2’s rank is strictly higher
than m1’s.

