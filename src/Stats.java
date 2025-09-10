// FILE: Stats.java
// DATE: May 11, 2025
// AUTHOR: Timothy Prisk; tprisk@gmail.com

public class Stats
{
    private double[] results;

    //
    // Constructor.
    //
    Stats(double[] results)
    {
        this.results = results;
    }

    //
    // Public methods.
    //

    // Print simulation results to the terminal.
    public void printOutcome()
    {
        // Calculate estimators for the magnetization and its statistical error.
        double magEst = calcAvg(results);
        double magErr = calcVar(results);

        // Display statistics for the user.
        System.out.println("Average magnetization: " + magEst);
        System.out.println("Variance: " + magErr);
    }

    //
    // Private methods.
    //

    // Determine mean.
    private double calcAvg(double[] values)
    {
        double sum = 0.0;
        for (double val : values)
        {
            sum += val;
        }
        return sum/values.length;
    }

    // Determine variance.
    private double calcVar(double[] values)
    {
        double mean = calcAvg(values);

        double sqDiffSum = 0.0;
        for (double val: values)
        {
            sqDiffSum += (val - mean)*(val-mean);
        }

        return sqDiffSum/(values.length-1);
    }

    //
    // Getters, setters, and misc.
    //

    // Return average.
    public double getAvg()
    {
        return calcAvg(results);
    }

    // Return standard error in the mean.
    public double getVar()
    {
        return calcVar(results);
    }
}