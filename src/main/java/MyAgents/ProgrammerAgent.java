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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ProgrammerAgent extends Agent {
    // Список участвующих абитуриентов
    private ArrayList abiturs;
    private ArrayList abiturs_mark;
    private ArrayList<MyTask> tasks;
    private Proger pro = null;
    private int Timer=8;
    //мнимальное число баллов, для поступления на специальность
   // private int minmark;
	//рейтинг специальности
  //  private int price;
    //число вакантных мест на специальности
  //  private int abitursNumber;

	protected String GetAgentName()
	{
		return getAID().getName().split("@")[0];
	}
	
    protected void setup() {
        abiturs = new ArrayList();
        abiturs_mark = new ArrayList();
        tasks=new ArrayList<MyTask>();
        Object[] args = getArguments();
        if (args != null && args.length >= 1) {
            pro=(Proger) getArguments()[0];
          //  minmark = Integer.parseInt((String) args[0]);
           // price = Integer.parseInt((String) args[1]);
           // abitursNumber = Integer.parseInt((String) args[2]);
        }
        System.out.println("Агент-Программист " + GetAgentName() + " готов к выполению задач" + "\n" +
        "Компетенции: "+ pro.competences[0]+ ", " + pro.competences[1]+ ", "+pro.competences[2]+ "\n" +
        "Свободное время " + pro.freeTimeH());
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
				//в меседже получаем задачу
				String[] string=(msg.getContent()).split(",");
				String name =string[0];
				int time=Integer.parseInt(string[1]);
				boolean[] comp=new boolean[] {Boolean.parseBoolean(string[2]),Boolean.parseBoolean(string[3]),Boolean.parseBoolean(string[4])};
               // int mark = Integer.parseInt(msg.getContent());
				MyTask mytask=new MyTask(name, comp, time);
                ACLMessage reply = msg.createReply();
                System.out.println(pro.name+" и задача " +msg.getContent() );
				System.out.println( " если компетенции подходят {");
                //проверяем подходят ли компетенции
                if (pro.checkCompetence(comp)) {
                    boolean accept_flag = true;
					System.out.println("if (abiturs.size() == abitursNumber) {");
                  //  if (abiturs.size() == abitursNumber) {
                    //	if (pro.freeTimeH()<pro.getTime(mytask.time)) {
					if (Timer<pro.getTime(mytask.time)||pro.freeTimeH()<pro.getTime(mytask.time) ) {	
					//Timer
                        int worst_prog = 0;
                       // int worst_mark = (Integer) abiturs_mark.get(0);
                        MyTask worst_task=tasks.get(0);
                     //   for (int i = 1; i < abiturs_mark.size(); i++){
                        	for (int i = 1; i < tasks.size(); i++){
							System.out.println("if  tasks.get(i).time < worst_task.time {");
                           // if (((Integer) abiturs_mark.get(i)) < worst_mark) {
							if ((tasks.get(i)).time > worst_task.time) {
								System.out.println(">> " + i + " >> " + (tasks.get(i)).toString());
								worst_task=tasks.get(i);
								//worst_mark = (Integer) abiturs_mark.get(i);
                                worst_prog = i;
                                break;
                            }
						}
                        //if (worst_mark < mark) {
                        	//worst_task>mytask
                        	if (worst_task.time > mytask.time) {
                        	
							//System.out.println("Была найдена задача получше" + abiturs.size());
                        		System.out.println("Была найдена задача получше (" + worst_task.name+" хуже "+mytask.name);
                            ACLMessage refuse = new ACLMessage(ACLMessage.REFUSE);
                           refuse.addReceiver((AID) abiturs.get(worst_prog));
                           // refuse.addReceiver((AID) tasks.get(worst_prog));
                            refuse.setContent("Была найдена задача получше");
                            myAgent.send(refuse);
                            tasks.remove(worst_prog);
                            
                           // pro.delTask(worst_task);
                            //ro.addTask(mytask);
                            Timer=Timer+pro.getTime(worst_task.time);
                            abiturs.remove(worst_prog);
                          //  msgs.remove(worst_prog);
                           // abiturs_mark.remove(worst_prog);
                        } else
                            accept_flag = false;
                    }
                    if (accept_flag) {
						System.out.println("True");
						
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                       // reply.setContent(String.valueOf(price));
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
 //   ArrayList<ACLMessage> msgs;
    private class ApplyOffersServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);
           
            if (msg != null) {
                // ACCEPT_PROPOSAL Message received. Process it
                ACLMessage reply = msg.createReply();
                if (!abiturs.contains(msg.getSender())) {
                	//MyTask mytask=new MyTask(msg.getSender());
                	//(msg.getSender()).split(";");
                	String[] string=(msg.getContent()).split(",");
    				String name =string[0];
    				int time=Integer.parseInt(string[1]);
    				boolean[] comp=new boolean[] {Boolean.parseBoolean(string[2]),Boolean.parseBoolean(string[3]),Boolean.parseBoolean(string[4])};
    				MyTask mytask=new MyTask(name, comp, time);
    				tasks.add(mytask);
                    abiturs.add(msg.getSender());
 //                   msgs.add(msg);
                    Timer=Timer-pro.getTime(mytask.time);
                    //abiturs.get(0);
                    System.out.println(pro.toString());
                   // abiturs_mark.add(Integer.parseInt((msg.getContent().split(","))[0]));
                    
                }
                reply.setContent("");
                reply.setPerformative(ACLMessage.INFORM);
                
              //  myAgent.send(reply);
                int T=8;
               // if (abiturs.size() == abitursNumber) {
                for (int i = 0; i < abiturs.size(); i++) {
                	T-=pro.getTime(tasks.get(i).time);
                	}
                	 //if (pro.freeTimeH() <=4) {
                if (T <=1&&T >-1) {
                	myAgent.send(reply);
                	
                	pro.delAllTask();
                    String Participatedabiturs = "";
                    for (int i = 0; i < abiturs.size(); i++) {
                    	//((Agent) msgs.get(i)).send(reply);
                    	pro.addTask(tasks.get(i));
                        Participatedabiturs += "---" + ((AID) abiturs.get(i)).getName() + "\n";}
                    System.out.println("Задачи : \n--" + Participatedabiturs.substring(2) 
						+ " приняты на выполнение " + getAID().getName() + "\n");
                    Participatedabiturs ="Задачи : \n--" + Participatedabiturs.substring(2) 
                    + "\n"+ " приняты на выполнение " + getAID().getName() + "\n"+
					pro.toString()+"\n"+T;
                    BufferedWriter bw = null;
					try {
						bw = new BufferedWriter(new FileWriter("out\\"+pro.name+".txt",false));
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                   
                    try {
						bw.write(Participatedabiturs);
						 bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}}
                	
            } else {
                block();
            }
        }
    } // End of inner class RequestsServer
}