package organizador;
import java.util.List;

import aluno.Aluno;


public interface IFileOrganizer {
	
	/**
	 * 
	 * @param aluno
	 * @return
	 */
	public boolean addReg(Aluno aluno);
	
	/**
	 * 
	 * @param matricula
	 * @return
	 */
	public Aluno getReg(int matricula);
	
	/**
	 * 
	 * @param matricula
	 * @return
	 */
	public Aluno delReg(int matricula);
	
	/**
	 * 
	 * @return
	 */
	public List<Aluno> listar();
}
