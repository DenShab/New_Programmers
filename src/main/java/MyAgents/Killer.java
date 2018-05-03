package MyAgents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;


public class Killer extends Agent {
    // The list of known agents
    private AID[] specialtyAgents;
	
    // Put agent initializations here
    protected void setup() {
            System.out.println("Готов убивать!!!");
            // Add a TickerBehaviour that schedules a request to specialty agents every 1 seconds
            addBehaviour(new TickerBehaviour(this, 1000) {
                protected void onTick() {
					System.out.println("Ищет специальности!!!");
                    // Update the list of specialty agents
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("Specialty");
                    template.addServices(sd);
					try {
						DFAgentDescription[] result = DFService.search(myAgent, template);
						String Specialties = "Убийца нашел следующие специальности:\n";
						specialtyAgents = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
							specialtyAgents[i] = result[i].getName();
                            Specialties += "---" + specialtyAgents[i].getName() + "\n";
                        }
						if	(specialtyAgents.length > 0)
							System.out.println(Specialties);
						else{
							System.out.println("Специальностей нет, дело сделано");
							doDelete();
						}
                        } catch (FIPAException fe) {
                            fe.printStackTrace();
                        }
                        myAgent.addBehaviour(new RequestPerformer());
				}
			});
	}
	
	private class RequestPerformer extends Behaviour {
        private MessageTemplate mt; // The template to receive replies
        private int repliesCnt = 0; // The counter of replies from specialty agents
        private int step = 0;
		public void action() {
			// Send the cfp to all specialtys
			ACLMessage cfp = new ACLMessage(ACLMessage.PROPOSE);
			for (int i = 0; i < specialtyAgents.length; ++i) {
				cfp.addReceiver(specialtyAgents[i]);
			}
			cfp.setContent("DIE");
			cfp.setConversationId("Prog-Proj");
			cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
			myAgent.send(cfp);
			
		}
		
        public boolean done() {
			return true;
		}
	}
}




