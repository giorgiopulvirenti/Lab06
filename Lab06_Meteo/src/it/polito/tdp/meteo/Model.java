package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	MeteoDAO dao=new MeteoDAO();
	List <Rilevamento> rilevamenti;
	Map <String,Citta> citta=new TreeMap<String,Citta>();
	List<Citta> cittaa;
	private final static int COST = 50;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private double costoMinimo=99999999;
	List <SimpleCity> soluzione;

	public Model() {
// rilevamenti=dao.getAllRilevamenti();
		

	}
 
	public void setCitta(){
		for (Rilevamento r:rilevamenti){
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
		cittaa=(List<Citta>) citta.values();
		 soluzione=new ArrayList<SimpleCity>();

		this.recursive(soluzione, level,mese);
		 
		for(SimpleCity s: soluzione)
			f+=s.getNome()+"; ";
		return f;
	}

	public void recursive (List<SimpleCity> parziale, int level,int mese) {
	
		 if (parziale.size()>=this.NUMERO_GIORNI_TOTALI) {
			 if (this.controllaParziale(parziale,mese)==true){
			 if(this.punteggioSoluzione(parziale)<=this.costoMinimo){
				 for(Citta c:cittaa)
					 c.setCounter(0);
				 costoMinimo=this.punteggioSoluzione(parziale);
				soluzione=parziale;
			 }
			 }
		 return;
		 }
	
		 for (int i =0;i<citta.size();i++) {
			 if( cittaa.get(i).getCounter()<6){
				
			 parziale.add(new SimpleCity(cittaa.get(i).getNome()));
			
			 cittaa.get(i).increaseCounter();
			 }
			 }
			 recursive (parziale, level + 1,mese);
			 parziale.remove(parziale.size()-1);
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
int d=0;
for(int i =0;i<parziale.size();i++){
	parziale.get(i).increaseCosto(dao.getRilevamentoLocalitaMeseGiorno(mese, i, parziale.get(i).getNome()));
	if (a=="" || a.equals(parziale.get(i).getNome())){
		d++;
		a=parziale.get(i).getNome();
	}
	else
	{
		if (d<3){
			d=0;
			return false;
			}
	d=0;
	parziale.get(i).increaseCosto(100);
	}
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
