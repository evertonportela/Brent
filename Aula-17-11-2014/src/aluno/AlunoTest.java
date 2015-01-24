package aluno;
import java.io.IOException;
import java.util.ArrayList;

import organizador.IFileOrganizer;
import organizador.OrganizadorSequencial;


public class AlunoTest {
	/**
	 * - Gera um número aleatório de 0 a 100 
	 * que não esteja nas matriculas que já foram geradas
	 * 
	 * @param matriculasExistentes
	 * @return
	 */
	public int getMatricula(ArrayList<Integer> matriculasExistentes){
		int matricula = (int) (1 + (Math.random() * 100));
		if(matriculasExistentes.contains(matricula)){
			return getMatricula(matriculasExistentes);
		}else{
			return matricula;
		}
	}

	public static void main(String[] args) throws IOException {
		AlunoTest alunoTest = new AlunoTest();
		
		// Array utilizado para guardar todas as matriculas
		ArrayList<Integer> arrayMatriculas = new ArrayList<Integer>();
		
		IFileOrganizer organizador = new OrganizadorSequencial("alunos.db");		
		for(int i = 0; i<20; i++){
			int matricula = alunoTest.getMatricula(arrayMatriculas);

			// Adiciona a matricula para não haver repetições
			arrayMatriculas.add(matricula);
			
			Aluno aluno = new Aluno(matricula, "Joao-"+matricula, "Rua A 4151", (short)10,	"M", "joao@mail.com");
			organizador.addReg(aluno);
		}
		
		// Busca por esta matricula, depois a exclui e confirma se foi excluida
		int excluirmatricula = arrayMatriculas.get(0);
		
		System.out.println("Busca inicial...");
		Aluno alunoManipulado = organizador.getReg(excluirmatricula);
		System.out.println(alunoManipulado);
		
		System.out.println(" \nDeletando... ");
		System.out.println(organizador.delReg(excluirmatricula));
		
		System.out.println(" \nBuscando...");
		alunoManipulado = organizador.getReg(excluirmatricula);
		System.out.println(alunoManipulado);
	}
	
}
