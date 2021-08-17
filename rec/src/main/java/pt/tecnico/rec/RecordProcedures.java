package pt.tecnico.rec;

import java.util.*;

public class RecordProcedures extends Thread {
	
	private Map<String, Double> records;
	//private Map<String, Double> tags;
	private int instance;
	
	public RecordProcedures(int instance) {
		records = new HashMap<String, Double>();
		this.instance = instance;
	}
	
	public synchronized String ping() {
	//public String ping() {
		return "Rec " + instance + " is running.";
	}
	
	public synchronized double read(String id) {
	//public double read(String id) {
		//synchronized(records) {
			double value;
			//double tag;
			if (records.containsKey(id)) value = records.get(id); //tag = tags.get(id);
			else {
				value = 0;
				records.put(id, value);
				/*tag = 0;
				tags.put(id, tag);*/
			}
			return value;
			/*double[] res = {value, tag};
			return res;*/
	}
	
	public synchronized void write(String id, double value /*double[] req*/) {
		/*double value = req[0];
		double tag = req[1];*/
		if (records.containsKey(id)) {
			records.replace(id, value);
			//tags.replace(id, tag);
		}
		else {
			records.put(id, value);
			//tags.put(id, tag);
		}
	}
	
}
