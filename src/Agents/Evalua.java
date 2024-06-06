package Agents;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
public class Evalua extends Agent {
    private static final int MAX_ITERATIONS = 500;
    private int iterations = 0;
    private int swarmSize;
    private AID[] particleAgents;

    protected void setup() {
        // Obtiene los argumentos pasados al agente al iniciarlo
        Object[] args = getArguments();
        if (args != null && args.length >= 2) {
            // El primer argumento es el tamaño del enjambre
            swarmSize = (int) args[0];
            // El segundo argumento es la lista de identificadores de los agentes
            particleAgents = (AID[]) args[1];
        }

        addBehaviour(new TickerBehaviour(this, 1) {
            protected void onTick() {
                iterations++;
                System.out.println("Iteration: " + iterations + " Best fitness: " + Particle.gBestFitness);
                if (iterations >= MAX_ITERATIONS) {
                    System.out.println("Final best fitness: " + Particle.gBestFitness);
                    System.out.println("Best parameters: Beta0 = " + Particle.gBest[0] + ", Beta1 = " + Particle.gBest[1] + ", Beta2 = " + Particle.gBest[2]);

                    // Enviar mensaje de terminación
                    ACLMessage terminationMessage = new ACLMessage(ACLMessage.INFORM);
                    terminationMessage.setContent("terminate");
                    // Añade todos los agentes de partículas como receptores del mensaje
                    for (AID aid : particleAgents) {
                        terminationMessage.addReceiver(aid);
                    }
                    terminationMessage.addReceiver(new AID("StartAgent", AID.ISLOCALNAME));
                    send(terminationMessage);
                    doDelete();
                }
            }
        });
    }
    protected void takeDown() {
        System.out.println("Evalua terminating.");
    }
}
