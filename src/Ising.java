// FILE: Ising.java
// DATE: May 11, 2025
// AUTHOR: Timothy Prisk; tprisk@gmail.com

public class Ising
{
    private static int eqSteps;
    private static int measureSteps;
    private static int acceptance = 0;
    private static double[] results;
    private static boolean moveAccepted;

    // Simulation driver.
    public static void main(String[] args)
    {
        // Parse the command line arguments.
        Communicator comms = new Communicator(args);

        // If the command line arguments are invalid, prompt the user and exit the program.
        if (!comms.getValid())
        {
            comms.help();
            return;
        }

        // Initialization stage.
        comms.greet();
        Spins magnet = new Spins(comms);
        comms.informInitDone();

        // Equilibration stage.
        eqSteps = comms.getEqSteps();
        if (eqSteps > 0)
        {
            for (int s = 0; s < eqSteps; s++)
            {
                moveAccepted = magnet.tryMove();
            }
        }
        comms.informEqDone();

        // Measurement stage.
        measureSteps = comms.getMeasureSteps();
        results = new double[measureSteps];
        for (int s = 0; s < measureSteps; s++)
        {
            moveAccepted = magnet.tryMove();
            if (moveAccepted)
            {
                acceptance++;
            }

            // Accumulate the results.
            results[s] = magnet.getMag();
        }
        comms.informMeasureDone();

        // Display results.
        Stats stats = new Stats(results);
        stats.printOutcome();

        // Save results.
        Visualizer vis = new Visualizer();
        vis.makeSnapshot(comms.getId(), magnet.getState());
        comms.writeLog(stats, acceptance);
        comms.writeMag(results);
        comms.writeState(magnet);
        comms.bye();
    }
}