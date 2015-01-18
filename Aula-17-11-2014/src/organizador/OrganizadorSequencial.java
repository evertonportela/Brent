package organizador;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import aluno.Aluno;


public class OrganizadorSequencial implements IFileOrganizer{

	private FileChannel canal;
	private static final long INEXISTENTE = -1;
	
	public OrganizadorSequencial(String arqName) throws FileNotFoundException{
		File file = new File(arqName);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		this.canal = raf.getChannel();
		//raf.close();
	}
	
	@Override
	public boolean addReg(Aluno pAluno) {
		try{
			long position = this.getPositionMaior(pAluno.getMatricula());
			
			// Se não encontrar maior ecreve no fim
			if(position == this.INEXISTENTE){
				canal.write(pAluno.getBuffer(), canal.size());				
			}else{
				// Move todos os maiores para frente
				for(long i=canal.size(); i>=position; i-=Aluno.LENGTH){
					ByteBuffer buff = alocarAluno(i - Aluno.LENGTH, Aluno.LENGTH);
					canal.write(buff, i);
				}
				// Escreve na posição correta
				canal.write(pAluno.getBuffer(), position);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Aluno getReg(int pMatricula){
		try{
			long position = this.getPosition(pMatricula);
			if(position==this.INEXISTENTE){
				return null;
			}else{
				return new Aluno(this.alocarAluno(position, Aluno.LENGTH));
			}
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public Aluno delReg(int pMatricula){
		try{
			Aluno alunoDeletado = null;
			ByteBuffer buff = null;
			
			long position = this.getPosition(pMatricula);
			
			if(position==this.INEXISTENTE){
				return alunoDeletado;
			}else{
				buff = alocarAluno(position, Aluno.LENGTH);
				alunoDeletado = new Aluno(buff);

				// Move todos os maiores para frente caso não seja o ultimo
				if(position + Aluno.LENGTH != (this.canal.size()-1)){				
					for(long i=position; i<canal.size(); i+=Aluno.LENGTH){
						buff = alocarAluno(i, Aluno.LENGTH);
						canal.write(buff, i);
					}
					// Recebe a posição do truncate
					position = this.canal.size()-Aluno.LENGTH;
				}
				
				this.canal.truncate(position);
			}

			return alunoDeletado;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 
	 * @param matricula
	 * @return
	 * @throws IOException 
	 */
	private long getPosition(int pMatricula) throws IOException{
		long size = this.canal.size();
		
		// Coloca o canal na posição 0
		for(long pos=0; pos<size; pos+=Aluno.LENGTH){
			ByteBuffer buff = alocarAluno(pos, 4);
			
			if(pMatricula==buff.getInt()){
				return pos;
			}else if(buff.getInt() > pMatricula){
				break;
			}
		}
		
		return this.INEXISTENTE;
	}

	private ByteBuffer alocarAluno(long pos, int pBuffTamanho) throws IOException {
		ByteBuffer buff = ByteBuffer.allocate(pBuffTamanho);
		this.canal.read(buff, pos);
		buff.flip();
		return buff;
	}
	
	private long getPositionMaior(int pMatricula) throws IOException{
		long size = this.canal.size();

		// Coloca o canal na posição 0
		for(long pos=0; pos<size; pos+=Aluno.LENGTH){
			ByteBuffer buff = alocarAluno(pos, 4);
			
			if(buff.getInt() > pMatricula){
				return pos;
			}
		}

		return this.INEXISTENTE;
	}
	
	@Override
	public List<Aluno> listar() {
		
		
		return null;
	}
}
