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
import java.util.*;


public class TaskAgent extends Agent {
    // Mark of the Abitur
    private Integer mark;
    // The list of known Abitur agents
    private AID[] specialtyAgents;
    private boolean isParticipated = false;
    // Put agent initializations here
    protected void setup() {
        // Получает начальные аргументы
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            mark = Integer.parseInt((String) args[0]);
			// Напечатать приветственное сообщение
			String StartMes = "Агент-задача" + getAID().getName() + "с " + " готова к выполнению.\n";
			StartMes += "\tимеет " + mark + " баллов";
            System.out.println( StartMes );
            // Add a TickerBehaviour that schedules a request to specialty agents every 5 seconds
            addBehaviour(new OneShotBehaviour(this) {
//new TickerBehaviour(this, 5000) {
            @Override
			public void action(){
                    if (!isParticipated) {
                        System.out.println(getAID().getName() + 
							" ищет программиста с проходным баллом ниже " + mark);
                        // Update the list of specialty agents
                        DFAgentDescription template = new DFAgentDescription();
                        ServiceDescription sd = new ServiceDescription();
                        sd.setType("Programmer");
                        template.addServices(sd);
                        try {
                            DFAgentDescription[] result = DFService.search(myAgent, template);
                            String Specialties = "Задача " + getAID().getName() + " нашла следующих программистов:\n";
                            Specialties += "(\n";
							specialtyAgents = new AID[result.length];
                            for (int i = 0; i < result.length; ++i) {
                                specialtyAgents[i] = result[i].getName();
                                Specialties += "\t" + specialtyAgents[i].getName() + "\n";
                            }
							Specialties += ")";
							if	(specialtyAgents.length > 0)
								System.out.println(Specialties);
                        } catch (FIPAException fe) {
                            fe.printStackTrace();
                        }
                        myAgent.addBehaviour(new RequestPerformer());
                    }
                }
            });
            addBehaviour(new GetSpecialtyResponse());

        } else {
            // Make the agent terminate
            System.out.println("Агент-задача не может быть выполненна (?) " + getAID().getName() + "");
            doDelete();
        }
    }
	
	//Абитуриент вновь начинает поиск специальности
    private class GetSpecialtyResponse extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REFUSE);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println("Агент-задача " + getAID().getName() + " вновь готова к выполению");
                isParticipated = false;
            }
            block();
        }
    }
	
    // Здесь выполняется удаление агента из списков
    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Агент-задача " + getAID().getName() + " удален.");
    }
	
    private class RequestPerformer extends Behaviour {
        private AID bestSpecialty; // The agent who provides the best offer
        private int bestOffer; // The best offered price
        private int repliesCnt = 0; // The counter of replies from specialty agents
        private MessageTemplate mt; // The template to receive replies
        private int step = 0;
		private int index = 0;
		private ArrayList specialty; //список специальностей готовых нас принять 
		private ArrayList specialtyOffer;  // список рейтингов
		
        public void action() {
		while(step!=4)
            switch (step) {
                case 0:
                    // Send the cfp to all specialtys
                    ACLMessage cfp = new ACLMessage(ACLMessage.PROPOSE);
                    for (int i = 0; i < specialtyAgents.length; ++i) {
                        cfp.addReceiver(specialtyAgents[i]);
                    }
                    cfp.setContent(mark.toString());
                    cfp.setConversationId("Prog-Proj");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Prog-Proj"),
                        MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
					specialty = new ArrayList();
					specialtyOffer = new ArrayList();
                    step = 1;
                    break;
                case 1:
                    // Receive all proposals/refusals from specialty agents
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        //Специальность удовлетворяет этого абитуриента
                        if (reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                            System.out.println("Агент-задача " + getAID().getName() + " в рассмотрении " + reply.getSender().getName());
                            // Это рейтинг
                            int price = Integer.parseInt(reply.getContent());
							specialty.add(reply.getSender());
							specialtyOffer.add(price);
                            //if (bestSpecialty == null || price >= bestOffer) {
                            //    // Лучшая специальность на данный момент
                            //    bestOffer = price;
                            //    bestSpecialty = reply.getSender();
                            //}
                        }
                        repliesCnt++;
                        if (repliesCnt >= specialtyAgents.length) {
                            // Мы получили все ответы
							// Внешний цикл алгоритма совершает
							// ровно size итераций
							for (int i = 0; i < specialtyOffer.size(); i++) {
								// Массив просматривается с конца до
								// позиции i и "легкие элементы всплывают"
								for (int j = specialtyOffer.size() - 1; j > i; j--) {
									// Если соседние элементы расположены
									// в неправильном порядке, то меняем
									// их местами
									if (((int)specialtyOffer.get(j)) > ((int)specialtyOffer.get(j - 1))) {
										AID tempSpecialty = (AID)specialty.get(j);
										int tempOffer = (int)specialtyOffer.get(j);
										//int temp = a[j];
										
										specialty.set(j, specialty.get(j - 1));
										specialtyOffer.set(j, specialtyOffer.get(j - 1));
										//a[j] = a[j - 1];
										
										specialty.set(j - 1, tempSpecialty);
										specialtyOffer.set(j - 1, tempOffer);
										//a[j - 1] = temp;
									}
								}
							}
                            step = 2;
                        }
                    } else {
                        block();
                    }
                    break;
                case 2:
                    // Отправка заказа на поставку в специальность
                    ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    order.addReceiver((AID)specialty.get(index));
                    order.setContent(mark.toString());
                    order.setConversationId("Prog-Proj");
                    order.setReplyWith("order" + System.currentTimeMillis());
                    myAgent.send(order);
                    // Подготовка шаблона, чтобы получить ответ.
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Prog-Proj"),
                        MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                    step = 3;
                    break;
                case 3:
                    // Получение ответа
                    reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Получен ответ
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            // поиск выполнен успешно. Мы можем прекратить
                            System.out.println(getAID().getName() + " взята на выполение " + reply.getSender().getName()+"\n" + 
							"\tБаллы = " + mark.toString() + "; Рейтинг специальности = " + bestOffer);
                            isParticipated = true;
							step = 4;
                        } else {
                            System.out.println(getAID().getName() + " не взята на выполение " + reply.getSender().getName());
							step = 2;
							index++;
                        }
                        
                    } else {
                        block();
                    }
                    break;
            }
        }
        public boolean done() {
            if (step == 2 && bestSpecialty == null) {
                System.out.println("Попытка не удалась: Количество баллов, равное " + mark + " недостаточно для поступления");
            }
            return ((step == 2 && bestSpecialty == null) || step == 4);
        }
    } // End of inner class RequestPerformer
}