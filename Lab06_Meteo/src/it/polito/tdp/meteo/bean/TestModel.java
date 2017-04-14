package it.polito.tdp.meteo.bean;

public class TestModel {

	public static void main(String[] args) {

		Model m = new Model();
		
		System.out.println(m.getUmiditaMedia(12));
		
		System.out.println("ciao  "+m.trovaSequenza(5));
		
//		System.out.println(m.trovaSequenza(4));
	}

}
