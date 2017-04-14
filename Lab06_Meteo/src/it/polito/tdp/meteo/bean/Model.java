package it.polito.tdp.meteo.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	MeteoDAO dao=new MeteoDAO();
	List <Rilevamento> rilevamenti;
	List <Rilevamento> rilevamentii;
	Map <String,Citta> citta=new TreeMap<String,Citta>();
	List<Citta> cittaa;
	private final static int COST = 50;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private double costoMinimo=999999;
	List <SimpleCity> soluzione;

	public Model() {
// rilevamenti=dao.getAllRilevamenti();
		rilevamentii=dao.getAllRilevamenti();
		this.setCitta();
		

	}
 
	public void setCitta(){
		for (Rilevamento r:rilevamentii){
			if (!citta.containsKey(r.getLocalita()))
				citta.put(r.getLocalita(), new Citta(r.getLocalita(),dao.getAllRilevamentiLocalita(r.getLocalita())));
			
		}
		
	}
	public String getUmiditaMedia(int mese) {
String s="";
		for (Citta c:citta.values())
			s+=c.getNome()+" "+this.getAvgRilevamentiLocalitaMese(mese, c.getNome())+"\n";
		return s.trim();
	}

	public String trovaSequenza(int mese) {
		String f="";
		int level=0;
		cittaa=new ArrayList<Citta>( citta.values());
		 soluzione=new ArrayList<SimpleCity>();

		this.recursive(soluzione, level,mese);
		 
		for(SimpleCity s: soluzione)
			f+=s.getNome()+"; ";
		return f;
	}

	public void recursive (List<SimpleCity> parziale, int level,int mese) {
	
		if (level>=this.NUMERO_GIORNI_TOTALI) {
			 if (this.controllaParziale(parziale,mese)==true){
		//		System.out.println(this.punteggioSoluzione(parziale));
			 if(this.punteggioSoluzione(parziale)<=this.costoMinimo){
				
		//		 for(Citta c:cittaa){
		//			 c.setCounter(0);
	//			 }
				 costoMinimo=this.punteggioSoluzione(parziale);
			 System.out.println(parziale+"   kkkkkkkkk");
				soluzione=new ArrayList<SimpleCity>(parziale);
			 }
			 }
			 this.cancellaCostoParziale(parziale);
		 return;
		 }
	
		 for (int i =0;i<citta.size();i++) {
			 if( cittaa.get(i).getCounter()<6){
				
			 parziale.add(new SimpleCity(cittaa.get(i).getNome()));
			
			 cittaa.get(i).increaseCounter();
			 
			 
			 recursive (parziale, level + 1,mese);
	//		 System.out.println(level);
	//		 System.out.println(cittaa.get(i)+" "+cittaa.get(i).getCounter()+" @@@@");
			 parziale.remove(parziale.size()-1);
			 cittaa.get(i).decreaseCounter();
			 }
		 }
	}
		
		
	private void cancellaCostoParziale(List<SimpleCity> parziale) {
		for(SimpleCity s:parziale)
			s.setCosto(0);
			
		
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		
		double score = 0.0;
		for (SimpleCity s: soluzioneCandidata)
			score+=s.getCosto();
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale,int mese) {
for (Citta c:cittaa)
	if (c.getCounter()==0)
		return false;

String a="";
int []d=new int[15];
int f=0;
for(int i =0;i<parziale.size();i++){
		parziale.get(i).increaseCosto(dao.getRilevamentoLocalitaMeseGiorno(mese, i, parziale.get(i).getNome()));
	if (a=="" || a.equals(parziale.get(i).getNome())){
		d[f]++;
		
	}
	else
	{
		f++;
	
	parziale.get(i).increaseCosto(100);
	}
	a=parziale.get(i).getNome();
}
for (int i=0;i<=f;i++)
	if (d[i]<2){
//		System.out.println(parziale+" "+i+" "+f+" "+d[i]);
		return false;
	}

		return true;
	}
	
	
	public int getAvgRilevamentiLocalitaMese(int mese, String localita){
		int sum=0;
		 rilevamenti= dao.getAllRilevamentiLocalitaMese(mese, localita);
		for (Rilevamento r:rilevamenti)
			sum+=r.getUmidita();
		 
		 return (sum/rilevamenti.size());
	 }

}
