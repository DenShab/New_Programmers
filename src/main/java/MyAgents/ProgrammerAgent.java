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
import java.util.*;

public class ProgrammerAgent extends Agent {
    // Список участвующих абитуриентов
    private ArrayList abiturs;
    private ArrayList abiturs_mark;
    //мнимальное число баллов, для поступления на специальность
    private int minmark;
	//рейтинг специальности
    private int price;
    //число вакантных мест на специальности
    private int abitursNumber;

	protected String GetAgentName()
	{
		return getAID().getName().split("@")[0];
	}
	
    protected void setup() {
        abiturs = new ArrayList();
        abiturs_mark = new ArrayList();
        Object[] args = getArguments();
        Proger pro=(Proger) getArguments()[0];
        if (args != null && args.length >= 3) {
            minmark = Integer.parseInt((String) args[0]);
            price = Integer.parseInt((String) args[1]);
            abitursNumber = Integer.parseInt((String) args[2]);
        }
        System.out.println("Агент-Программист " + GetAgentName() + " готов к выполению задач" + "\n" +
        "Свойство 1 " + minmark + "\n" +
        "Свойство 2 " + price + "\n" +
        "Свойство 3 " + abitursNumber);
        // Register the project-participating service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Programmer");
        sd.setName("JADE-proj-prog");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Add the behaviour serving queries from programmer agents
        addBehaviour(new RequestsServer());

        // Add the behaviour serving purchase orders from programmer agents
        addBehaviour(new ApplyOffersServer());
    }
    // Put agent clean-up operations here
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            //fe.printStackTrace();
        }
        // вывод сообщения об завершении
        System.out.println("Агент-Программист  " + getAID().getName() + " завершил работу");
        doDelete();
    }
    private class RequestsServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
				if (msg.getContent() == "DIE")
				{
					takeDown();
					block();
					return;
				}
                // PROPOSE Message received. Process it
                int mark = Integer.parseInt(msg.getContent());
                ACLMessage reply = msg.createReply();
				System.out.println("if (mark >= minmark) {");
                //Check if mark is OK for the project
                if (mark >= minmark) {
                    boolean accept_flag = true;
					System.out.println("if (abiturs.size() == abitursNumber) {");
                    if (abiturs.size() == abitursNumber) {
                        int worst_prog = 0;
                        int worst_mark = (Integer) abiturs_mark.get(0);
                        for (int i = 1; i < abiturs_mark.size(); i++){
							System.out.println("if (((Integer) abiturs_mark.get(i)) < worst_mark) {");
                            if (((Integer) abiturs_mark.get(i)) < worst_mark) {
								System.out.println(">> " + i + " >> " + (Integer) abiturs_mark.get(i));
                                worst_mark = (Integer) abiturs_mark.get(i);
                                worst_prog = i;
                                break;
                            }
						}
                        if (worst_mark < mark) {
							System.out.println("Была найдена задача получше" + abiturs.size());
                            ACLMessage refuse = new ACLMessage(ACLMessage.REFUSE);
                            refuse.addReceiver((AID) abiturs.get(worst_prog));
                            refuse.setContent("Была найдена задача получше");
                            myAgent.send(refuse);
                            abiturs.remove(worst_prog);
                            abiturs_mark.remove(worst_prog);
                        } else
                            accept_flag = false;
                    }
                    if (accept_flag) {
						System.out.println("True");
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        reply.setContent(String.valueOf(price));
                    } else {
						System.out.println("false");
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        reply.setContent("Были найдены все необходимые задачи");
                    }
                } else {
					System.out.println("The mark is too low.");
                    // The mark is too low.
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    reply.setContent("Очень низкие (свойства)");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    } // End of inner class RequestsServer
    private class ApplyOffersServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // ACCEPT_PROPOSAL Message received. Process it
                ACLMessage reply = msg.createReply();
                if (!abiturs.contains(msg.getSender())) {
                    abiturs.add(msg.getSender());
                    abiturs_mark.add(Integer.parseInt(msg.getContent()));
                }
                reply.setContent("");
                reply.setPerformative(ACLMessage.INFORM);
                myAgent.send(reply);
                if (abiturs.size() == abitursNumber) {
                    String Participatedabiturs = "";
                    for (int i = 0; i < abiturs.size(); i++)
                        Participatedabiturs += "---" + ((AID) abiturs.get(i)).getName() + "\n";
                    System.out.println("Задачи : \n--" + Participatedabiturs.substring(2) 
						+ " приняты на выполнение " + getAID().getName() + "\n");
                }
            } else {
                block();
            }
        }
    } // End of inner class RequestsServer
}