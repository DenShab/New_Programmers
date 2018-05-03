package MyAgents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Proger {
	int n=3;
	public String name;
	public String[] schedule=new String[] {"","","","","","","",""};
    public int[] competences=new int[n];
    
    public Proger(String name, int[] competences)
    {
        this.name = name;
        this.competences = competences;
        
    }
    
    public Proger(String string) {
    	String[] param =string.split(";");
    	 this.name=(param[0].split("-"))[1];
    	String competencesTMP=(param[1].split("-"))[1];
    	int length=competencesTMP.split(",").length;
    	for(int i=0;i<n;i++)
    	{
    		if(length>i)
    		this.competences[i]=Integer.parseInt((competencesTMP.split(","))[i]);
    		else this.competences[i]=0;
    	}
       
	}

	public int getTime(int taskTime)
    {
    	int time = 0;
    	int competencesSr = 0;
    	for(int i = 0;i<n;i++) 
    	{
    		competencesSr=competencesSr+competences[i]/n;
    	}
    	time = taskTime+taskTime*(50 - competencesSr)/100;
    	if (time<=0)
    		time=1;
    	if (time>8)
    		time=8;
    	return time;
    }
    
  
    
    public void addTask(MyTask task)
    {
    	ArrayList<Integer> freetime=freeTime();
    	int k=0;
    	int time1=getTime(task.time);
    	for(int i : freetime)
    	{
    		k++;
    		this.schedule[i]=task.name;
    		if(k==time1)
    			break;
    	}
    	Arrays.sort(this.schedule);
    	
    }
    public void replacTask(MyTask task1,MyTask task2)
    {
    	delTask( task1);
    	Arrays.sort(this.schedule);
    	ArrayList<Integer> time=freeTime();
    	int k=0;
    	int time1=getTime(task2.time);
    	for(int i : time)
    	{
    		k++;
    		this.schedule[i]=task2.name;
    		if(k==time1)
    			break;
    	}
    	Arrays.sort(this.schedule);
    }
    
    public void delTask(MyTask task)
    {
    	for(int i=0;i<this.schedule.length;i++)
    	{
    		if(this.schedule[i]==task.name)
	    	{
    			this.schedule[i]="";
	    	}
    	}
    	Arrays.sort(this.schedule);
    }
    
    public ArrayList<Integer>  freeTime()
    {
    	ArrayList<Integer> freeT = new ArrayList<Integer>();
    	for(int i=0;i<this.schedule.length;i++) 
    	{
	    	if(Objects.equals(this.schedule[i],""))
	    	{
	    		freeT.add(i);
	    	}
    	}
    	return freeT;
    }
    public int  freeTimeH()
    {
    	int H=0;
    	for(int i=0;i<this.schedule.length;i++) 
    	{
	    	if(this.schedule[i]=="")
	    	H++;
    	}
    	return H;
    }
    
    @Override
    public String toString()
    {
    	String sch="";
    	for(int i=0;i<this.schedule.length;i++)
    		if(this.schedule[i]!="")
    		sch=sch+this.schedule[i]+"	";
    		else sch=sch+"-		";
        return name + ";	" + sch + ";" ;
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
