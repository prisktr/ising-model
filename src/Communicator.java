// FILE: Communicator.java
// DATE: May 11, 2025
// AUTHOR: Timothy Prisk; tprisk@gmail.com

import java.io.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

public class Communicator
{
    private final UUID id = UUID.randomUUID(); // Unique identifer for the simulation.
    private int N = 100; // The magnet consists of N x N spins.
    private double T = 2.26918; // System temperature in units of J. Default is critical temperature.
    private double h = 0; // Energy per spin from external magnetic field. May be positive or negative.
    private int eqSteps = 1000000; // Initial number of moves used to equilibrate the system.  No data collected.
    private int measureSteps = 100; // Number of moves used to measure observables.
    private boolean inputValid = true;
    private String initialStateName;
    private int[][] initalState;
    private boolean loadState = false;
    private final double Tc = 2.26918;

    //
    // Constructor.
    //
    public Communicator(String[] args)
    {
        if (args.length != 0)
        {
            String[] argParts;
            String parameterName;
            String parameterValue;
            for (String arg : args)
            {
                argParts = arg.split("=",2);
                if (argParts.length == 2)
                {
                    parameterName = argParts[0];
                    parameterValue = argParts[1];
                    assign(parameterName, parameterValue);
                }
            }
        }
    }

    //
    // Public methods.
    //

    // Greet the user and print the simulation parameters.
    public void greet()
    {
        System.out.println("*********************************");
        System.out.println("*** 2D ISING MODEL SIMULATION ***");
        System.out.println("*********************************");
        System.out.println("ID: " + id);
        System.out.println("Size: " + N + "x" + N);
        System.out.println("Temperature: " + T);
        System.out.println("External field: " + h);
        System.out.println("Equilibration steps: " + eqSteps);
        System.out.println("Measurement steps: " + measureSteps);
        System.out.println();
    }

    // Help the user if command line arguments are invalid.
    public void help()
    {
        System.out.println();
        System.out.println("Oh no!  Something was wrong the with command line arguments.");
        System.out.println();
        System.out.println("Here is an example of valid input:");
        System.out.println("java Ising -n=100 -t=2.26918 -h=0 -e=10000 -m=1000 -s=<FILENAME>");
        System.out.println();
        System.out.println("These are the simulation parameters that can be specified:");
        System.out.println("Size NxN: -n");
        System.out.println("Temperature (T/J): -t");
        System.out.println("Magnetic field (h/J): -h");
        System.out.println("Equilibration steps: -e");
        System.out.println("Measurement steps: -m");
        System.out.println("Initial state: -s");
    }

    // Create a log file with the simulation parameters.
    public void writeLog(Stats stats, int acceptance)
    {
        // Determine acceptance rate.
        double acceptRate = (double) acceptance/(double) measureSteps;

        // Determine starting condition.
        String initString = determineInitialCondition();

        // Write the log.
        String filename = "Ising-"+id+".log";
        try(BufferedWriter writer = new BufferedWriter(new FileWriter((filename))))
        {
            writer.write("*** SIMULATION PARAMETERS ***");
            writer.newLine();
            writer.write("ID: " + id);
            writer.newLine();
            writer.write("Size: " + N + "x" + N);
            writer.newLine();
            writer.write("Temperature: " + T);
            writer.newLine();
            writer.write("External field: " + h);
            writer.newLine();
            writer.write("Equilibration steps: " + eqSteps);
            writer.newLine();
            writer.write("Measurement steps: " + measureSteps);
            writer.newLine();
            writer.write("Initial state: " + initString);
            writer.newLine();
            writer.newLine();
            writer.write("*** SIMULATION OUTCOME ***");
            writer.newLine();
            writer.write("Mean magnetization: " + stats.getAvg());
            writer.newLine();
            writer.write("Variance: " + stats.getVar());
            writer.newLine();
            writer.write("Acceptance ratio: " + acceptRate);

            System.out.println("Log file written.");
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
        }
    }

    // Write a file containing all magnetization measurements.
    public void writeMag(double[] results)
    {
        String filename = "Ising-magnetization-"+id+".mag";
        String formattedValue;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter((filename))))
        {
            for (double val: results)
            {
                formattedValue = String.format(Locale.US, "%.20f", val);
                writer.write(formattedValue);
                writer.newLine();
            }
            System.out.println("Magnetization results file written.");
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
        }
    }

    // Write a file containing the state of the magnet in space delimited format.
    public void writeState(Spins magnet)
    {
        String filename = "Ising-state-"+id+".state";

        try(BufferedWriter writer = new BufferedWriter((new FileWriter(filename))))
        {
            int[][] state = magnet.getState();

            for (int i = 0; i < N; i++)
            {
                for (int j = 0; j < N; j++) {
                    writer.write(String.valueOf(state[i][j]));
                    if (j < N - 1)
                    {
                        writer.write(" ");
                    }
                }
                writer.newLine();
            }
            System.out.println("State file written.");
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
        }
    }


    //
    // Private methods.
    //

    // Assign a value to a simulation parameter from a command line argument.
    private void assign(String parameterName, String parameterValue)
    {
        try
        {
            if (parameterName.equals("-n"))
            {
                N = Integer.parseInt(parameterValue);
                if (N <= 0)
                {
                    throw new IllegalArgumentException("N must be a positive-definite integer.");
                }
            }
            if(parameterName.equals("-t"))
            {
                T = Double.parseDouble(parameterValue);
                if (T==0.0)
                {
                    throw new IllegalArgumentException("Temperature must be nonzero!");
                }
            }
            if(parameterName.equals("-h"))
            {
                h = Double.parseDouble(parameterValue);
            }
            if (parameterName.equals("-e"))
            {
                eqSteps = Integer.parseInt(parameterValue);
                if (eqSteps < 0)
                {
                    throw new IllegalArgumentException("Number of equilibration steps cannot be less than zero!");
                }
            }
            if (parameterName.equals("-m"))
            {
                measureSteps = Integer.parseInt(parameterValue);
                if (measureSteps <= 0)
                {
                    throw new IllegalArgumentException("Number of measurement steps must be greater than zero!");
                }
            }
            if (parameterName.equals("-s"))
            {
                loadState = true;
                this.initialStateName = parameterValue;
                readState(parameterValue);
            }
        }
        catch(NumberFormatException e)
        {
            inputValid = false;
            System.err.println("Number format exception: " + e.getMessage());
        }
        catch(IllegalArgumentException e)
        {
            inputValid = false;
            System.err.println("Illegal argument exception: " + e.getMessage());
        }
    }

    // Start a simulation from a saved state.
    private void readState(String stateName)
    {
        try(BufferedReader reader = new BufferedReader(new FileReader(stateName)))
        {
            String line = reader.readLine();
            if (line == null)
            {
                throw new RuntimeException("File is empty!");
            }

            N = line.split(" ").length; // This will overwrite the -n command line argument.
            initalState = new int[N][N];

            // Parse the file.
            int row = 0;
            initalState[row++] = Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray();
            while ((line = reader.readLine()) != null)
            {
                if (row >= N)
                {
                    throw new IOException("Numbers of rows and columns are not equal.");
                }
                initalState[row++] = Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray();
            }
            if (row != N)
            {
                throw new IOException("Numbers of rows and columns are not equal.");
            }
        }
        catch(IOException e)
        {
            inputValid = false;
            System.err.println("IOException: " + e.getMessage());
        }
        catch (RuntimeException e)
        {
            inputValid = false;
            System.err.println("RuntimeException: " + e.getMessage());
        }

    }

    //
    // Getters, setters, and misc.
    //

    // Prompt the user that the program is finished.
    public void bye()
    {
        System.out.println("*** SIMULATION COMPLETE ***");
    }

    // Prompt user about the status of simulation.
    public void informInitDone()
    {
        System.out.println("Initialization complete.");
    }

    public void informEqDone()
    {
        System.out.println("Equilibration complete.");
    }

    public void informMeasureDone()
    {
        System.out.println("Measurements complete.");
    }

    public UUID getId()
    {
        return id;
    }

    public int getN()
    {
        return N;
    }

    public double getT()
    {
        return T;
    }

    public double getH()
    {
        return h;
    }

    public int getEqSteps()
    {
        return eqSteps;
    }

    public int getMeasureSteps()
    {
        return measureSteps;
    }

    public boolean getValid()
    {
        return inputValid;
    }

    public boolean isLoadState()
    {
        return loadState;
    }

    public int[][] getState()
    {
        return initalState;
    }

    private String determineInitialCondition()
    {
        if (loadState)
        {
            return initialStateName;
        }
        else
        {
            if (T >= Tc)
            {
                return "T = inf";
            }
            else
            {
                return "T = 0";
            }

        }

    }
}
