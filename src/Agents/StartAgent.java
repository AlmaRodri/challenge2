package Agents;
import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.util.Random;
public class StartAgent extends Agent {
    private static final int MIN_SWARM_SIZE = 10;
    private static final int MAX_SWARM_SIZE = 30;
    private AID[] particleAgents; // Array para almacenar los identificadores de los agentes de partículas

    protected void setup(){
        Random random = new Random();
        // Genera un tamaño de enjambre aleatorio
        int SWARM_SIZE = random.nextInt(MAX_SWARM_SIZE - MIN_SWARM_SIZE + 1) + MIN_SWARM_SIZE;
        particleAgents = new AID[SWARM_SIZE];

        Particle.gBest = new double[3];
        Particle.gBestFitness = Double.MAX_VALUE;
        Particle.dataSet = new DataSet();
        // Obtiene el contenedor de agentes donde se ejecutará este agente
        AgentContainer container = getContainerController();

        try { // Crea y lanza los agentes de partículas
            for (int i = 0; i < SWARM_SIZE; i++) {
                AgentController particle = container.createNewAgent("ParticleAgent" + i, Particle.class.getName(), null);
                particleAgents[i] = new AID("ParticleAgent" + i, AID.ISLOCALNAME);
                particle.start();
            }

            // Crear y lanzar el agente supervisor (Evalua)
            AgentController supervisor = container.createNewAgent("Supervisor", Evalua.class.getName(), new Object[]{SWARM_SIZE, particleAgents});
            supervisor.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
    protected void takeDown() {
        System.out.println("StartAgent terminating.");
    }
}
