package MyAgents;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
        String[] InputArgs;
		Object[] args;
        switch (splitted[0]) {
            case "Task":        
				InputArgs = splitted[1].split(",");
                String agentName = InputArgs[0];
                String Mark = InputArgs[1];

                args = new Object[] { Mark };
                //System.out.println("Создание адачи"+","+agentName+","+Mark);
                return getContainerController()
					.createNewAgent(agentName, "TaskAgent", args);

            case "Programmer":
				InputArgs = splitted[1].split(",");
                String projectName = InputArgs[0];
				String minMark = InputArgs[1];
				String Reyting = InputArgs[2];
				String NeedAbiture = InputArgs[3];
				
                args = new Object[] { minMark, Reyting, NeedAbiture };
                //System.out.println("Создание gпрограммиста");
                return getContainerController()
					.createNewAgent(projectName, "ProgrammerAgent", args);

            default:
                return null;
        }        
    }
}
