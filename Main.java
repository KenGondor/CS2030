import cs2030.simulator.EventSimulator;

import java.util.Scanner;
import java.io.FileReader;

/**
 * Main class for the program.
 *
 * @author LeeEnHao_A0204679W
 * */
public class Main {

    /**
     * Entry point of the program.
     * */
    public static void main(String[] args) {
        Scanner input = readInput(args);
        if (input == null) {
            return;
        } else {
            EventSimulator es = init(input);
            es.run();
            es.printStats();
        }
    }

    private static Scanner readInput(String[] args) {
        if (args.length == 0) {
            //No file read from stdin
            return new Scanner(System.in);
        } else {
            try {
                FileReader fr = new FileReader(args[0]);
                return new Scanner(fr);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static EventSimulator init(Scanner sc) {
        int seed = sc.nextInt();
        int serverNum = sc.nextInt();
        int nSelf = sc.nextInt();
        int qMax = sc.nextInt();
        int customerNum = sc.nextInt();
        double lambda = sc.nextDouble();
        double mu = sc.nextDouble();
        double rho = sc.nextDouble();
        double restProb = sc.nextDouble();
        double greedP = sc.nextDouble();

        EventSimulator es = EventSimulator.launch(seed, serverNum, nSelf, qMax, customerNum,
            lambda, mu, rho, restProb, greedP);

        return es;
    }

}
