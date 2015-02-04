package br.com.apresentacao;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import organizador.IFileOrganizer;
import organizador.OrganizadorBrent;
import organizador.OrganizadorSequencial;
import aluno.Aluno;

public class Desempenho{
	
	public static void main(String[] args){
		try {
			File fOrigem = new File("selected.db");
			RandomAccessFile fileOrigem;
			fileOrigem = new RandomAccessFile(fOrigem, "r");
			FileChannel channelOrigem = fileOrigem.getChannel();

			//IFileOrganizer org = new OrganizadorBrent("enem_brent.db");
			IFileOrganizer org = new OrganizadorSequencial("enem_seq.db");
			
			System.out.println("Começou!!");
			long timeInicial = System.currentTimeMillis();
			
			// Ler cada aluno do arquivo de origem e consultar no destino
			for (int pos=0; pos < channelOrigem.size(); pos +=Aluno.MATRICULA_LENGTH)  {
				// Ler da origem
				ByteBuffer buff = ByteBuffer.allocate(Aluno.MATRICULA_LENGTH);
				channelOrigem.read(buff, pos);

				buff.flip();
				int matricula = buff.getInt();
				
				// Consulta no destino
				Aluno aluno = org.getReg(matricula);
			}

			long timeFinal = System.currentTimeMillis();
			System.out.println((timeFinal-timeInicial) / 1000 );
			
			channelOrigem.close();
			fileOrigem.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
