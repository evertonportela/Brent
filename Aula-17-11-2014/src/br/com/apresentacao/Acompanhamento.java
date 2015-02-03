package br.com.apresentacao;

import aluno.Aluno;

public class Acompanhamento extends Thread{
	public void run() {
		try{
			while(true){
				System.out.println(MigracaoBrent.contador/Aluno.LENGTH + " de 5.791.290");
				Thread.sleep(5000*60);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	};
}
