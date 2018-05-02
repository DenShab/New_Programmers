package MyAgent;

import static org.junit.Assert.*;

import org.junit.Test;

import MyAgents.MyTask;
import MyAgents.Proger;

public class TaskTest {

	@Test
	public void test() {
		String s="Name-Abitur1;Competences-true,false,true;Time-5";
		MyTask task=new MyTask(s);
		assertEquals(task.name, "Abitur1");
		assertEquals(task.competences[0], true);
		assertEquals(task.competences[1], false);
		assertEquals(task.competences[2], true);
		assertEquals(task.time, 5);
	}

}
