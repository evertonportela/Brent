package br.com.apresentacao;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import organizador.IFileOrganizer;
import organizador.OrganizadorBrent;

import aluno.Aluno;

public class MigracaoBrent{

	public static void main(String[] args){
		try {
			File fOrigem = new File("enem_aleat.db");
			RandomAccessFile fileOrigem;
			fileOrigem = new RandomAccessFile(fOrigem, "r");
			FileChannel channelOrigem = fileOrigem.getChannel();

			IFileOrganizer org = new OrganizadorBrent("enem_brent.db");

			// Ler cada aluno do arquivo de origem e inserir no de destino
			for (long pos=0; pos < channelOrigem.size(); pos +=Aluno.LENGTH)  {
				// Ler da origem
				ByteBuffer buff = ByteBuffer.allocate(Aluno.LENGTH);
				channelOrigem.read(buff, pos);

				buff.flip();
				Aluno aluno = new Aluno(buff);

				// Inserir no destino
				org.addReg(aluno);

				// posição de parada
				if (pos>(10*Aluno.LENGTH)) {
					break;
				}
			}
			channelOrigem.close();
			fileOrigem.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
