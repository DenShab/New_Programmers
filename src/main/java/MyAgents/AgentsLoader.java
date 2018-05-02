package MyAgents;

import jade.core.Agent;
import jade.core.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import MyAgents.TaskAgent;

public class AgentsLoader extends Agent {

    @Override
    protected void setup() {
    	System.err.println("START !");
        Object[] args = getArguments();
        if(args.length != 1) {
            System.err.println("Please specify input file!");
            return;
        }
		//System.out.println("Загрузка данных: \n");
        BufferedReader reader = null;
        int lineCount = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0].toString()), "utf-8"));
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                lineCount++;
				//System.out.println(currentLine + "\n");
                AgentController ac = parseAgent(currentLine);
                if (ac != null) {
                    ac.start();
                }
            }
        } catch (IOException ex) {
            System.out.println("Проблемы с прочтением строки " + lineCount);
        } catch (StaleProxyException ex) {
            ex.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                System.out.println("Не удалось закрыть файл");
            }
        }
    }
    
    private AgentController parseAgent(String s) throws StaleProxyException {
        String[] splitted = s.split(":");
        switch (splitted[0]) {
            case "Task":      
            	MyTask task=new MyTask(splitted[1]);
            	MyTask[] tasks =new MyTask[] {task};
                //System.out.println("Создание задачи"+","+agentName+","+Mark);
                jade.wrapper.AgentContainer c;
                c=getContainerController();
                return c.createNewAgent(task.name, "MyAgents.TaskAgent", tasks);

            case "Programmer":
            	Proger pro=new Proger(splitted[1]);
            	Proger[] pros=new Proger[] {pro};
                //System.out.println("Создание программиста");
                return getContainerController()
					.createNewAgent(pro.name, "MyAgents.ProgrammerAgent", pros);

            default:
                return null;
        }        
    }
}
