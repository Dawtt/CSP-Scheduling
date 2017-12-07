# CSP-Scheduling

Demonstration of AI focused Local Search algorithms. Based on Poole & Mackworth's presentation in Artificial Intelligence: Foundations of Computational Agents . - http://artint.info/html/ArtInt_83.html

Uses 3 files for data input, expecting them to be in the directory it is run from.
Use Driver class to run.

Basic Construction of Algorithm:

Random Initialization: Create a random schedule.
	Two-Stage Choice: for highest conflict variable, choose value with lowest conflicts. Repeat.
		Random Selection: if highest conflict variable is in tabu list, choose a random variable instead for most improving.
			Random Restart: If get the same number of conflicts X times, start the search over at a new random schedule.


This program uses different methods built on random selection of a given domain of values for variables, in order to find a set of values which satisfy constraints on them.

The specific problem addressed here is putting together a 4 year schedule of courses which have varying requirements:
- some must be taken before others
- one course per day of a week of a semester
- different courses have different availability at different semesters (spring vs. fall).


The problem set is large enough that there is approximately  1.712e+36  possible different sets of variable values, a very small perecentage of which fulfill the constraint requirements of the default constraint.txt input file.


Running the algorithm with two-stage choice, and without random selection or just without random restart, will  show observable local minimums of value sets the algorithm becomes stuck in and unable to produce any further optimization. With default input files these local minimums commonly occur at around 8-20 constraint violations.

There is an excellent visual explanation of local minimums here: http://artint.info/html/ArtInt_85.html 