package organizador;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import aluno.Aluno;


public class OrganizadorSimples implements IFileOrganizer{

	
	private FileChannel canal;
	
	public OrganizadorSimples(String arqName) throws IOException{
		File file = new File(arqName);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		this.canal = raf.getChannel();
		//raf.close();
	}

	@Override
	public boolean addReg(Aluno aluno) {
		try{
			canal.write(aluno.getBuffer(), canal.size());
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Aluno getReg(int matricula) {				
		try{
			long position = this.getPosition(matricula);
			if(position < 0){
				return null;
			}
			
			ByteBuffer buf = ByteBuffer.allocate(Aluno.LENGTH);
			this.canal.read(buf, position);
			buf.flip();
			
			return new Aluno(buf);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param matricula
	 * @return
	 * @throws IOException 
	 */
	private long getPosition(int matricula) throws IOException{
		long size = this.canal.size();
		
		// Coloca o canal na posição 0
		for(long pos=0; pos<size; pos+=Aluno.LENGTH){
			ByteBuffer buff = ByteBuffer.allocate(4);
			this.canal.read(buff, pos);
			buff.flip();
			
			if(matricula==buff.getInt()){
				return pos;
			}
		}
		
		return -1;
	}

	@Override
	public Aluno delReg(int matricula) {
		try{
			long position = this.getPosition(matricula);
			if(position<0){
				return null;
			}
			
			// Lê o ultimo registro
			ByteBuffer bufUltimo = ByteBuffer.allocate(Aluno.LENGTH);
			this.canal.read(bufUltimo, this.canal.size()-Aluno.LENGTH);			
			
			// Lê quem vai ser apagado
			ByteBuffer bufDel = ByteBuffer.allocate(Aluno.LENGTH);
			this.canal.read(bufDel, position);
			
			bufDel.flip();
			Aluno aluno = new Aluno(bufDel);
			
			// Sobrescreve o registro
			bufUltimo.flip();
			this.canal.write(bufUltimo, position);
			
			// diminui o tamanho do canal
			this.canal.truncate(this.canal.size()-Aluno.LENGTH);
			
			return aluno;
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public List<Aluno> listar() {
		// TODO Auto-generated method stub
		return null;
	}
}
