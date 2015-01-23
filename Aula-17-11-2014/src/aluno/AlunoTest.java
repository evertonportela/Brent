package aluno;
import java.io.IOException;

import organizador.IFileOrganizer;
import organizador.OrganizadorSequencial;


public class AlunoTest {

	public static void main(String[] args) throws IOException {
		IFileOrganizer organizador = new OrganizadorSequencial("alunos.db");		
		int mat = 1;
		while(mat<=20){
			Aluno aluno = new Aluno(mat, "Joao"+mat, "Rua A 4151", (short)10,	"M", "joao@mail.com");
			organizador.addReg(aluno);
			mat++;
		}
		
		Aluno aluno = new Aluno(10, "Joao 10B", "Rua A 4151", (short)20,	"M", "joao@mail.com");
		organizador.addReg(aluno);
		Aluno aluno2 = new Aluno(12, "Joao 12B", "Rua A 4151", (short)20,	"M", "joao@mail.com");
		organizador.addReg(aluno2);
		
		
		System.out.println("Busca inicial...");
		Aluno alunoManipulado = organizador.getReg(15);
		System.out.println(alunoManipulado);
		
		System.out.println(" \nDeletando... ");
		System.out.println(organizador.delReg(15));
		
		System.out.println(" \nBuscando...");
		alunoManipulado = organizador.getReg(15);
		System.out.println(alunoManipulado);
	}
}
