package MyAgents;

public class MyTask {

	int n=3;
	public int time;
	public String name;
	public boolean[] competences=new boolean[] {false,false,false};

	public MyTask(String name, boolean[] competences)
    {
        this.name = name;
        this.competences = competences;
        
    }
    
    public MyTask(String string) {
    	String[] param =string.split(";");
    	 this.name=(param[0].split("-"))[1];
    	String competencesTMP=(param[1].split("-"))[1];
    	int length=competencesTMP.split(",").length;
    	for(int i=0;i<competences.length;i++)
    	{
    		if(length>i)
    		this.competences[i]=Boolean.parseBoolean((competencesTMP.split(","))[i]);
    		else this.competences[i]=false ;
    	}
    	this.time=Integer.parseInt((param[2].split("-"))[1]);
       
	}
}
