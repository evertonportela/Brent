package br.com.apresentacao;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import aluno.Aluno;

public class GerarArquivo {
	public static final String NM_ARQUIVO_APRESENTACAO = "brent_apresentacao.db";
	
	public static void main(String[] args) {
		try{
			File fOrigem = new File(GerarArquivo.NM_ARQUIVO_APRESENTACAO);
			RandomAccessFile fileOrigem;
			fileOrigem = new RandomAccessFile(fOrigem, "rw");
			FileChannel channelOrigem = fileOrigem.getChannel();
			
			// Gera arquivo com 11 alunos.
			for(int i=0; i<11; i++){
				Aluno aluno = new Aluno(0, "", "", (short) 0, "", "");
				channelOrigem.write(aluno.getBuffer(), channelOrigem.size()==0?0:channelOrigem.size());
				System.out.println(channelOrigem.size() + "- " + (channelOrigem.size()/Aluno.LENGTH));
			}
			
			channelOrigem.close();
			fileOrigem.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
