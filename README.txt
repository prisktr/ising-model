About:
This program simulates the two-dimensional Ising ferromagnet using the Metropolis Monte Carlo method. For background,
see M.E.J. Newman & G.T. Barkema, Monte Carlo Methods in Statistical Physics (Clarendon Press, Oxford, 1997).

The program is designed as follows.  The Ising class contains the main driver for the simulation.  It accepts optional
command line arguments.  First, it instantiates a Communicator object, which is responsible for parsing command line
arguments, setting simulation parameters, prompting the user, and outputting all results.  Second, the driver creates
a Spins object which represents the state of the magnet and contains methods for making Monte Carlo moves.  Third, the
driver equilibrates the magnet.  (No values are saved during equilibration; if the user wants to see the approach to
equilibrium from the initial state, then the -e flag should be set to zero.) Fourth, the simulation proceeds according
to the Metropolis Monte Carlo method.  Here results for the average magnetization are accumulated and the acceptance
ratio is counted.  Lastly, the outcome of the simulation is shown and results are saved.

Contents:
-- src/Ising.java: This is the main driver for the simulation.
-- src/Communicator.java: This class handles I/O for the program.
-- src/Spins.java: This class represents the state of the magnet and implements the Monte Carlo moves.
-- src/Visualizer.java: This class generates a .png snapshot from a magnet state.
-- src/Stats.java: This class computes simple statistics from the magnetization results.
-- example/Ising-5ae92966-5d73-490e-bc36-a2a8c4d18077.log: Example log file.
-- example/Ising-magnetization-5ae92966-5d73-490e-bc36-a2a8c4d18077.mag: Example measurement file.
-- example/Ising-snapshot-5ae92966-5d73-490e-bc36-a2a8c4d18077.png: Example snapshot.
-- example/Ising-state-5ae92966-5d73-490e-bc36-a2a8c4d18077.state: Example state file.

Notes:
-- Example command line: java Ising -n=100 -t=2.26918 -h=0 -e=1000000 -m=1000 -s=<FILENAME>
-- This program was compiled with Java SDK 11.0.26 on Ubuntu 24.04.2 LTS.

Disclaimer:
This program was written as an exercise in Java programming.  It has not been carefully benchmarked to ensure that it
produces mathematically correct results.  If there are logical or conceptual errors in the program design, please
reach out and let me know about them.
