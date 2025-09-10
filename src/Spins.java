// FILE: Spins.java
// DATE: May 11, 2025
// AUTHOR: Timothy Prisk; tprisk@gmail.com

import java.util.Random;

public class Spins
{
    private final double Tc = 2.26918;
    private final Random spinSetter = new Random();
    private final Random randomIndex = new Random();
    private int[][] state;
    private int N;
    private double T;
    private double h;

    //
    // Constructor.
    //
    public Spins(Communicator comms)
    {
        this.N = comms.getN();
        this.T = comms.getT();
        this.h = comms.getH();

        state = new int[N][N];

        if (comms.isLoadState())
        {
            state = comms.getState();
        }
        else
        {
            makeState(N, T, h);
        }
    }

    //
    // Public methods.
    //

    // Attempt a Monte Carlo move.
    public boolean tryMove()
    {
        // Choose a spin at random.
        int i = randomIndex.nextInt(N);
        int j = randomIndex.nextInt(N);

        // Calculate the change in system energy.
        double deltaE = getEnergyChange(i, j);

        boolean moveAccepted;
        // If the delta-E is less than or equal to 0, then accept the move.
        if (deltaE <= 0)
        {
            state[i][j] *= -1;
            moveAccepted = true;
        }

        // If delta-E is greater than 0, then accept the move with a probability exp(-deltaE/T).
        else
        {
            double dblDeltaE = (double) deltaE;
            double acceptRatio = Math.exp(-1.0*dblDeltaE/T);
            double choice = Math.random();

            if (choice < acceptRatio)
            {
                state[i][j] *= -1;
                moveAccepted = true;
            }
            else
            {
                moveAccepted = false;
            }
        }

        return moveAccepted;
    }

    // Magnetization per spin in the current state.
    public double getMag()
    {
        int mag = 0;
        int N = this.state.length;
        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
            {
                mag += state[i][j];
            }
        }
        double dblMag = (double) mag;
        return dblMag /((double) N * (double) N);
    }


    //
    // Private methods.
    //
    private void makeState(int N, double T, double h)
    {
        if (T >= Tc)
        {
            for (int i = 0; i < N; i++)
            {
                for (int j = 0; j < N; j++)
                {
                    state[i][j] = generateRandomSpin();
                }
            }
            System.out.println("The magnet is initialized at T = inf.");
        }

        if (T < Tc && h >= 0)
        {
            for (int i = 0; i < N; i++)
            {
                for (int j = 0; j < N; j++)
                {
                    state[i][j] = +1;
                }
            }
            System.out.println("The magnet is initialized at T = 0.");
        }

        if (T < Tc && h < 0)
        {
            for (int i = 0; i < N; i++)
            {
                for (int j = 0; j < N; j++)
                {
                    state[i][j] = -1;
                }
            }
            System.out.println("The magnet is initialized at T = 0.");
        }
    }

    // Randomly choose +1 or -1.
    private int generateRandomSpin()
    {
        int rand = spinSetter.nextInt(2);
        if (rand == 0)
        {
            return -1;
        }
        return +1;
    }

    // Determine the change in system energy when spin (i, j) is flipped.
    private double getEnergyChange(int i, int j)
    {
        int deltaEint = 0;
        double deltaEext = 0;

        // Change in internal energy (i.e. from nearest neighbor interactions).
        int neighbor1 = state[(i+1)%N][j];
        int neighbor2 = state[(i+N-1)%N][j];
        int neighbor3 = state[i][(j+1)%N];
        int neighbor4 = state[i][(j+N-1)%N];
        deltaEint = 2*state[i][j]*(neighbor1 + neighbor2 + neighbor3 + neighbor4); // internal interactions

        // Change in external energy (i.e. from external magnetic field).
        if (state[i][j] == +1)
        {
            deltaEext = +2.0*h;
        }
        if (state[i][j] == -1)
        {
            deltaEext = -2.0*h;
        }

        return (double) deltaEint + deltaEext;
    }

    //
    // Getters, setters, and misc.
    //

    public int[][] getState()
    {
        return state;
    }

}
