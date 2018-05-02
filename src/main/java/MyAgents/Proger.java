package MyAgents;

import java.util.ArrayList;

public class Proger {
	int n=3;
	String name;
    String[] schedule=new String[] {"","","","","","","",""};
    int[] competences=new int[n];
    
    public Proger(String name, int[] competences)
    {
        this.name = name;
        this.competences = competences;
        
    }
    
    public int getTime(int taskTime)
    {
    	int time = 0;
    	int competencesSr = 0;
    	for(int i = 0;i<n;i++) 
    	{
    		competencesSr=competences[i]/n;
    	}
    	time = taskTime+taskTime*(50 - competencesSr)/100;
    	
    	return time;
    }
    
  
    
    public void addTask(MyTask task)
    {
    	int time=getTime(task.time);
    	
    }
    public void replacTask(MyTask task1,MyTask task2)
    {
    	delTask( task1);
    	ArrayList<Integer> time=freeTime();
    	int k=0;
    	int time1=getTime(task2.time);
    	for(int i : time)
    	{
    		k++;
    		schedule[i]=task2.name;
    		if(k==time1)
    			break;
    	}
    }
    
    public void delTask(MyTask task)
    {
    	for(int i=0;i<schedule.length;i++)
    	{
    		if(schedule[i]==task.name)
	    	{
    			schedule[i]="";
	    	}
    	}
    }
    
    public ArrayList<Integer>  freeTime()
    {
    	ArrayList<Integer> freeT = new ArrayList<Integer>();
    	for(int i=0;i<schedule.length;i++) 
    	{
	    	if(schedule[i]=="")
	    	{
	    		freeT.add(i);
	    	}
    	}
    	return freeT;
    }
  /*  public Proger(String str)
    {
        String[] elems = str.split(";");
        this.theme = elems[0].trim();
        this.text = elems[1].trim();
        this.complexity = Integer.parseInt(elems[2].trim());
    }
    */
    /*
    @Override
    public String toString()
    {
        return theme + ";" + text + ";" + complexity;
    }
    */
}
