package br.com.apresentacao;

import aluno.Aluno;
import organizador.OrganizadorBrent;

public class ApresentacaoBrent {
	public static void main(String[] args) {
		try {
			// Organizador
			OrganizadorBrent organizadorBrent = 
					new OrganizadorBrent(GerarArquivo.NM_ARQUIVO_APRESENTACAO);
			
			// Inseri na sequencia do slide passado em sala
			/*organizadorBrent.addReg(new Aluno(27, "Aluno", "Rua 10",
					(short) 18, "M", "aluno@ufs.br"));

			organizadorBrent.addReg(new Aluno(18, "Aluno", "Rua 10",
					(short) 18, "M", "aluno@ufs.br"));*/
			
			organizadorBrent.addReg(new Aluno(29, "Aluno", "Rua 10",
					(short) 18, "M", "aluno@ufs.br"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
