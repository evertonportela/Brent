package aluno;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import organizador.IFileOrganizer;
import organizador.OrganizadorSimples;


public class AlunoTest {

	public static void main(String[] args) throws IOException {
		IFileOrganizer organizador = new OrganizadorSimples("altest.db");		
		int mat = 1;
		while(mat<=20){
			Aluno aluno = new Aluno(mat, "Joao"+mat, "Rua A 4151", (short)10,	"M", "joao@mail.com");
			organizador.addReg(aluno);
			mat++;
		}
		
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
