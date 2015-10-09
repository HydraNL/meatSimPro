/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import main.CFG;



/**
 * Description of Evaluation.
 * 
 * @author rijk
 */
public class Evaluation {
	private double grade;
	private PContext currentContext;
	private double Iweight;
	private double indE;
	private double Sweight;
	private double socE;


	/**
	 * The constructor.
	 */
	public Evaluation(double Iweight, double indE, double Sweight, double socE, PContext myContext) {
		this.Iweight =Iweight; //ND 1 0.25
		this.indE = indE; //ND 1 0.25 maar met AVG omhoog kunnen er grote afwijkingen zijn van de mean
		this.Sweight =Sweight; //ND 1 0.25
		this.socE = socE; //ND nu ook 1 0.25
		double x = CFG.complexEvaluation() ? 
				(Iweight * indE + Sweight * socE)/2.0 : CFG.individualEvualuation() ?
						indE:socE;
		this.grade =//x;
				Math.max(0, 1 +Math.tanh((x-1)/0.2)); //range tussen 0 en 2, f(1) =1;
		this.currentContext = myContext;
	}

	public void setGrade(double grade) {
		this.grade = grade;	
	}

	public double getGrade() {
		//System.out.println(grade);
		return grade;
	}

	public double getIweight() {
		return Iweight;
	}

	public double getIndE() {
		return indE;
	}

	public double getSweight() {
		return Sweight;
	}

	public double getSocE() {
		return socE;
	}
	
	public PContext getContext(){
		return currentContext;
	}
}
