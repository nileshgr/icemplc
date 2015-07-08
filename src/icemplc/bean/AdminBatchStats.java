package icemplc.bean;

import java.io.Serializable;

/*
 * Bean for the batch wise statistics about batches and students on admin home
 */

public class AdminBatchStats implements Serializable {
	private static final long serialVersionUID = 1L;

	private int batch = 0;
	private int numOfStudents = 0;
	private int numOfPlacedStudents = 0;

	/*
	 * Bean no argument constructor
	 */

	public AdminBatchStats() {
	}

	public AdminBatchStats(int batch, int numOfStudents, int numOfPlacedStudents) {
		this.batch = batch;
		this.numOfStudents = numOfStudents;
		this.numOfPlacedStudents = numOfPlacedStudents;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}

	public int getNumOfStudents() {
		return numOfStudents;
	}

	public void setNumOfStudents(int numOfStudents) {
		this.numOfStudents = numOfStudents;
	}

	public int getNumOfPlacedStudents() {
		return numOfPlacedStudents;
	}

	public void setNumOfPlacedStudents(int numOfPlacedStudents) {
		this.numOfPlacedStudents = numOfPlacedStudents;
	}
}