package Agents;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Random;
public class Particle extends Agent {
    double[] position;
    double[] velocity;
    double[] pBest;
    double fitness;
    static final double C1 = 2.0;// aceleración para el componente cognitivo pBest
    static final double C2 = 2.0; // aceleración para el componente social gBest
    static final double W = 0.7; //inercia
    static double[] gBest = new double[3];
    static double gBestFitness = Double.MAX_VALUE;
    static DataSet dataSet;

    protected void setup() {
        dataSet = new DataSet();
        position = new double[3];  // 3 dimensions: Beta0, Beta1, Beta2
        velocity = new double[3];
        pBest = new double[3];
        Random random = new Random();
        // Inicializa la posición y la velocidad de la partícula de manera aleatoria
        for (int i = 0; i < 3; i++) {
            position[i] = random.nextDouble() * 10;
            velocity[i] = random.nextDouble() * 2 - 1;
            pBest[i] = position[i];
        }
        fitness = evaluateFitness(position);

        addBehaviour(new TickerBehaviour(this, 1) {
            protected void onTick() {
                // Evaluar la aptitud de la posición actual
                double fitness = evaluateFitness(position);
                // Actualizar pBest
                if (fitness < Particle.this.fitness) {
                    Particle.this.fitness = fitness;
                    System.arraycopy(position, 0, pBest, 0, position.length);
                }
                // Actualizar gBest
                synchronized (gBest) {
                    if (fitness < gBestFitness) {
                        gBestFitness = fitness;
                        System.arraycopy(position, 0, gBest, 0, position.length);
                    }
                }
                // Actualizar la velocidad y la posición de la partícula
                for (int i = 0; i < 3; i++) {
                    double r1 = random.nextDouble();
                    double r2 = random.nextDouble();
                    velocity[i] = W * velocity[i] +
                            C1 * r1 * (pBest[i] - position[i]) +
                            C2 * r2 * (gBest[i] - position[i]);
                    position[i] += velocity[i];
                }
            }
        });
    // comportamiento para escuchar mensajes de terminación
    addBehaviour(new CyclicBehaviour(this) {
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchContent("terminate"));
            if (msg != null) {
                doDelete();
            } else {
                block();
            }
        }
    });
}
    private double evaluateFitness(double[] position) {
        double beta0 = position[0];
        double beta1 = position[1];
        double beta2 = position[2];
        double sum = 0.0;

        // Calcular el error cuadrático medio entre los valores predichos y los valores reales
        for (int i = 0; i < dataSet.getX().length; i++) {
            double predictedY = beta0 + beta1 * dataSet.getX()[i] + beta2 * dataSet.getZ()[i];
            double error = predictedY - dataSet.getY()[i];
            sum += error * error;
        }
        return sum / dataSet.getX().length;
    }
}

