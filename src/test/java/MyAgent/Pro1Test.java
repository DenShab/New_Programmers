package MyAgent;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import MyAgents.MyTask;
import MyAgents.Proger;

public class Pro1Test {

	@Test
	public void test() {
		String s="Programmer:Name-specialty1;Competences-72,200,2";
		Proger pro=new Proger(s);
		assertEquals(pro.name, "specialty1");
		assertEquals(pro.competences[0], 72);
		assertEquals(pro.competences[1], 200);
		assertEquals(pro.competences[2], 2);
		//assertEquals
		//assertTrue
		//assertFalse
	}
	@Test
	public void test2() {
		String s="Programmer:Name-specialty1;Competences-50,50,50";
		Proger pro=new Proger(s);
		int taskTime=5;
		int t=pro.getTime(taskTime);
		System.out.println(t);
		assertEquals(t, 5);
	}
	@Test
	public void test3() {
		System.out.println("TEST3");
		String s="Name-specialty1;Competences-50,50,50";
		Proger pro=new Proger(s);
		String s2="Name-Abitur1;Competences-true,false,true;Time-5";
		MyTask task=new MyTask(s2);
		pro.addTask(task);
		int t=pro.getTime(task.time);
		System.out.println(pro.toString());
		assertEquals(t, 5);
	}

	@Test
	public void test4() {
		System.out.println("TEST4");
		String s="Name-specialty1;Competences-50,50,50";
		Proger pro=new Proger(s);
		String s2="Name-Abitur1;Competences-true,false,true;Time-5";
		MyTask task1=new MyTask(s2);
		String s3="Name-Abitur2;Competences-true,false,true;Time-6";
		MyTask task2=new MyTask(s3);
		ArrayList<Integer> free=pro.freeTime();
		String freeS="";
		
		System.out.println(pro.toString());
		for(int i=0;i<free.size();i++)
			freeS=freeS+free.get(i)+" ";
		System.out.println(freeS);
		System.out.println(pro.freeTimeH());
		freeS="";
		
		pro.addTask(task1);
		
		System.out.println(pro.toString());
		free=pro.freeTime();
		for(int i=0;i<free.size();i++)
			freeS=freeS+free.get(i)+" ";
		System.out.println(freeS);
		freeS="";
		
		int t=pro.getTime(task1.time);
		
		System.out.println(pro.freeTimeH());
		
		
		
		
		
		pro.replacTask(task1, task2);
		
		System.out.println(pro.toString());
		free=pro.freeTime();
		for(int i=0;i<free.size();i++)
			freeS=freeS+free.get(i)+" ";
		System.out.println(freeS);
		freeS="";
		
		System.out.println(pro.freeTimeH());
		assertEquals(t, 5);
		
		pro.delTask(task2);
	
		System.out.println(pro.toString());
		free=pro.freeTime();
		for(int i=0;i<free.size();i++)
			freeS=freeS+free.get(i)+" ";
		System.out.println(freeS);
		freeS="";
		
		System.out.println(pro.freeTimeH());
		assertEquals(t, 5);
	}
}
